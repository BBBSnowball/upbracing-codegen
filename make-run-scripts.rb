require 'rubygems'
require 'xmlsimple'

RUN_SPEC_PATTERN = "*.runspec.rb"
EXCLUDE_DIRS = [".git", "bin", "Debug", "Release", "GEM_HOME", "jruby-1.7.0.preview1"]

class Project
  attr_reader :name, :dir, :natures, :is_java, :classpath, :source_path, :linked_resources
  attr_accessor :location_variable

  def initialize(dir)
    @dir = dir
    data = XmlSimple.xml_in(File.join(dir, ".project"))
    @name = data["name"].first
    @natures = {}
    data["natures"].first["nature"].each do |nature|
      @natures[nature.strip] = true
    end
    #TODO linked resources are read, but not used
    @linked_resources = {}
    if data["linkedResources"]
      data["linkedResources"].first["link"].each do |link|
        @linked_resources[link["name"].first] = link["location"].first
      end
    end
    puts linked_resources.inspect unless linked_resources.empty?

    @is_java = natures["org.eclipse.jdt.core.javanature"]

    read_classpath(File.join(dir, ".classpath")) if @is_java
  end

  private

  def read_classpath(filename)
    data = XmlSimple.xml_in(filename)
    @classpath = []
    @source_path = []
    data["classpathentry"].each do |entry|
      case entry["kind"]
      when "con"
        #ignore (JRE container)
      when "src"
        if entry["path"] && entry["path"].start_with?("/")
          # this is a project

          @classpath << "~projectref~" + entry["path"]
        else
          # it's a source folder
          @source_path << entry["path"]
          classpath << entry["output"] if entry["output"]
        end
      when "output"
        # output folder
        classpath.insert(0, entry["path"])
      when "lib"
        path = entry["path"]
        classpath << path
      end
    end
    return classpath
  end

  def expand_variables(path, variables)
    path.gsub(/\$([A-Za-z_]+)|\$\{([^}]+)\}/) do
      name = $1 || $2
      var = variables[name]
      if var
        var.last  # we assume that all the others are redirections
      else
        puts "WARN: unknown variable '#{name}'"
        return ""
      end
    end
  end

  def lookup_path(path, variables, projects)
    if path && path.start_with?("/")
      # the path is absolute -> we must look it up in the workspace
      parts = path.match(/^\/+([^\/]+)(\/(.*))?$/)
      project_name = parts[1]
      path_in_project = parts[3]
      
      project = projects[project_name]
      if project
        path2 = project.path_to_file(path_in_project, variables)
        
        real_path = expand_variables(path2, variables)
        unless File.exists? real_path
          puts "WARN: Path in workspace doesn't exist: #{path} -> #{path2} -> #{real_path}"
        end

        return path2
      else
        # either an absolute path or a project
        puts "WARN: Absolute path or unknown project: #{path}"
        return path
      end
    elsif path
      # it's a relative path -> add directory
      return path_to_file(path, variables)
    end
  end

  def add_variables(variables)
    if @location_variable
      variables[@location_variable.first.to_s] = @location_variable[1..-1] + [@dir]
    end
  end

  public

  def path_to_root(variables)
    if @location_variable
      @location_variable = [*@location_variable]
      add_variables(variables)
      return "$" + @location_variable.first.to_s
    else
      return @dir
    end
  end

  def path_to_file(file, variables)
    root = path_to_root(variables)
    if file and file != ""
      File.join(root, file)
    else
      root
    end
  end

  def contribute_classpath_to(classpath, variables, projects, added_projects)
    # avoid loops
    return if added_projects[@name]
    added_projects[@name] = true

    @classpath.each do |entry|
      if entry.start_with? "~projectref~"
        # it's a referenced project -> tell it to add itself
        project_name = entry.sub(/~projectref~\/?/, "")
        project = projects[project_name]
        unless project
          puts "ERROR: Couldn't find project '#{project_name}'"
          exit 1
        else
          project.contribute_classpath_to(classpath, variables, projects, added_projects)
        end
      else
        path = lookup_path(entry, variables, projects)
        classpath << path unless classpath.include? path
      end
    end
  end
end

class RunGenerator
  attr_reader :projects

  def initialize
    @project_dirs = []
    @run_specs = []
  end

  def search_files(dir)
    @run_specs += Dir[File.join(dir, RUN_SPEC_PATTERN)]
    Dir.foreach dir do |child|
      child2 = File.join(dir, child)
      if child != "." and child != ".." \
          and !EXCLUDE_DIRS.include? child and !EXCLUDE_DIRS.include? child2 \
          and Dir.exists? child2
        @project_dirs << child2 if File.exists? File.join(child2, ".project")

        search_files(child2)
      end
    end
  end

  def read_projects
    @projects = {}

    @project_dirs.each do |dir|
      project = Project.new dir
      puts "project #{project.name} in #{dir}"
      puts "WARN: more than one project with name '#{project.name}'" if @projects[project.name]
      @projects[project.name] = project
    end
  end

  def process_runspec(runspec)
    $generator = self
    $runspec = runspec
    $path = File.dirname(runspec)

    require runspec
  end

  def run
    search_files(".")
    puts @run_specs.inspect
    read_projects
    @run_specs.each do |runspec|
      process_runspec runspec
    end
  end

  def make_run_description(projects_on_classpath, variable_names)
    variable_names.each do |project, varname|
      @projects[project].location_variable = varname
    end

    rundesc = RunDescription.new
    [*projects_on_classpath].each do |project|
      project = @projects[project] if project.is_a? String
      rundesc.add_project_to_classpath(project, projects)
    end

    rundesc
  end
end

class RunDescription
  attr_accessor :classpath, :variables, :added_projects

  def initialize
    @classpath = []
    @variables = {}
    @added_projects = {}
  end

  def add_project_to_classpath(project, projects)
    project.contribute_classpath_to(@classpath, @variables, projects, @added_projects)
  end

  def get_parents_including_self(file)
    parents = []
    next_dir = file
    current = nil
    while next_dir and not next_dir == current
      current = next_dir
      parents << File.basename(current)
      
      next_dir = File.dirname(current)
    end
    
    return parents
  end

  def make_relative(path, cwd)
    # make it absolute and removes all "." and ".."
    path = File.expand_path path
    cwd  = File.expand_path cwd

    path_parents = get_parents_including_self path
    cwd_parents  = get_parents_including_self cwd

    # absolute path, if the roots are different
    # (e.g. different drive on Windows)
    return path if path_parents.last != cwd_parents.last
    
    # remove common leading path elements
    while not path_parents.empty? and not cwd_parents.empty? and path_parents.last == cwd_parents.last
      path_parents.delete_at(path_parents.length - 1)
      cwd_parents.delete_at(cwd_parents.length - 1)
    end
    
    # construct relative path: enough ".." to get out of cwd_parents and
    # then the remaining elements of cwd_parents
    result = [".."] * cwd_parents.length + path_parents.reverse
    result = ["."] if result.empty?
    
    return result.join("/")
  end

  def unix_header(cwd, *args)
    #TODO escape stuff (except the variables we want to have *g*)
    unix_classpath = @classpath.map {|x| if x =~ /^[$\/]|^[a-zA-Z]:[\/\\]/ then x else '$DIR/' + make_relative(x, cwd) end}.join ":"

    unix_classpath += ":$EXTRA_CLASSPATH"

    contents = "#!/bin/sh\n\nDIR=\"\$(dirname \"\$0\")\"\n"

    @variables.each do |varname,values|
      values = [*values]

      values.each do |value|
        if value.start_with? "$"
          value_str = '"' + value + '"'
        elsif value.start_with? "/"
          value_str = "'" + value + "'"
        else
          #value_str = '"$DIR"/' + "'" + value.sub(/^\.\//, "") + "'"
          value_str = '"$DIR/' + make_relative(value, cwd) + '"'
        end
        contents += "\n[ -n \"$#{varname}\" ] || #{varname}=#{value_str}"
      end
    end

    [contents + "\n\n", '"' + unix_classpath + '"']
  end

  def unix_script(cwd, *args)
    contents, classpath = unix_header(cwd, *args)
    contents + 'java -cp ' + classpath + args.map { |x| if x == "$@" then ' "$@"' else " " + x end }.join + "\n"
  end

  def path_for_windows(unix_path)
    unix_path.gsub("/", "\\").gsub(/\$([A-Za-z_]+)|\$\{([^}]+)\}/) do
      name = $1 || $2
      if name == "DIR"
        # Meaning of %~dp0 -> see http://weblogs.asp.net/whaggard/archive/2005/01/28/get-directory-path-of-an-executing-batch-file.aspx
        "%~dp0"
      else
        "%" + name + "%"
      end
    end
  end

  def windows_header(cwd, *args)
    #TODO escape stuff (except the variables we want to have *g*)
    classpath = @classpath.map {|x| path_for_windows(if x =~ /^[$\/]|^[a-zA-Z]:[\/\\]/ then x else '$DIR/' + make_relative(x, cwd) end) }.join ";"

    classpath += ";%EXTRA_CLASSPATH%"

    contents = "@echo off\n"

    @variables.each do |varname,values|
      values = [*values]

      values.each do |value|
        if value.start_with? "$"
          value_str = '"' + path_for_windows(value) + '"'
        elsif value.start_with? "/"
          value_str = '"' + path_for_windows(value) + '"'
        else
          value_str = '"' + path_for_windows('$DIR/' + make_relative(value, cwd)) + '"'
        end
        contents += "\nIF \"%#{varname}%\"==\"\" SET #{varname}=#{value_str}"
      end
    end

    [contents + "\n\n", '"' + classpath + '"']
  end

  def windows_script(cwd, *args)
    contents, classpath = windows_header(cwd, *args)
    contents + 'java -cp ' + classpath + args.map { |x| if x == "$@" then ' %*' else " " + x end }.join + "\n"
  end

  def write_script(filename, contents)
    File.open(filename, "w") do |f|
      f.chmod(0755) unless filename =~ /\.(bat|cmd|com|exe)$/i
      f.write(contents)
    end
  end

  def write_scripts(name, cwd, *args)
    write_script(File.join(cwd, name),          unix_script(cwd, *args))
    write_script(File.join(cwd, name + ".bat"), windows_script(cwd, *args))
  end
end

gen = RunGenerator.new
gen.run
