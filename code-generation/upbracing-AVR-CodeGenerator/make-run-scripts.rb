require 'rubygems'
require 'xmlsimple'

def relative_path(parent, child)
  if File.absolute_path(child) != child
    File.join(parent, child)
  else
    child
  end
end

def read_classpath(filename, referenced_projects)
  data = XmlSimple.xml_in(filename)
  classpath = []
  data["classpathentry"].each do |entry|
    case entry["kind"]
    when "con"
      #ignore
    when "src"
      if entry["path"] && entry["path"].start_with?("/")
        # this is a project
        project_name = entry["path"][1..-1]
        
        projects = referenced_projects[project_name]
        if projects
          # load classpath of referenced project and add it
          projects = [projects] unless projects.is_a? Array 
          projects = projects.map {|x| File.join(x, ".classpath")}
          referenced_project_classpath_file = projects.first {|x| File.exists? x} 
          
          if referenced_project_classpath_file
            referenced_project_classpath = read_classpath(referenced_project_classpath_file,
                                                          referenced_projects)
            referenced_project_classpath = referenced_project_classpath.map {|x|
              relative_path(File.dirname(referenced_project_classpath_file), x) }
            #TODO remove duplicates
            classpath.concat(referenced_project_classpath)
          else
            puts "WARN: Found some paths for '{project_name}', but none of them exists"
          end
        else
          puts "WARN: ignoring referenced project '#{project_name}' because I don't know where it is"
        end
      else
        # it's a source folder
        classpath << entry["output"] if entry["output"]
      end
    when "output"
      classpath.insert(0, entry["path"])
    when "lib"
      classpath << entry["path"]
    end
  end
  return classpath
end

dir = File.dirname(__FILE__)
filename = File.join(dir, ".classpath")
classpath = read_classpath(filename,
  "statemachine" => File.join(dir, "../StatechartEditor"),
  "Parser" => File.join(dir, "../java-parser-tools"))

unix_classpath = classpath.map {|x| '$DIR/' + x}.join ":"
win_classpath = classpath.map {|x| '%~dp0\\' + x}.join ";"

File.open("run", "w") do |f|
  f.chmod(0755)
  f.write(%{
#!/bin/sh

DIR="$(dirname "$0")"
java -cp "} + unix_classpath + %{" de.upbracing.code_generation.Main "$@"
})
end 

File.open("run.bat", "w") do |f|
  f.write(%{rem Meaning of %~dp0 -> see http://weblogs.asp.net/whaggard/archive/2005/01/28/get-directory-path-of-an-executing-batch-file.aspx
java -cp "} + win_classpath + %{" de.upbracing.code_generation.Main %*})
end
