
def generate_pins_from_eagle(eagle_sch_file, output)
  # return, if the output file is present and new enough
  # We don't run this check for classpath items because we cannot (usually)
  # get an mtime for them.
  return if !(eagle_sch_file =~ /^classpath:/) and File.exists?(output) and File.mtime(output) >= File.mtime(eagle_sch_file)
  
  if File.exists?(output) and not system("which eagle")
    puts "WARNING: Cannot find Eagle, so '#{output}' will not be refreshed!"
    return
  end
  
  ulp = JRubyHelpers.readResource("#$ruby_helpers_package/processor_pins.ulp")
  
  eagle_sch_file_tmp = nil
  
  require 'tempfile'
  file = Tempfile.new(["processor_pins", ".ulp"])
  begin
    file.write ulp
    file.close
     
    if eagle_sch_file =~ /^classpath:/
      # we have to put it into a temporary file
      eagle_sch_file_tmp = Tempfile.new(["schematic", ".sch"])
      eagle_sch_file_tmp.write(File.read(eagle_sch_file))
      eagle_sch_file_tmp.close
      
      eagle_sch_file = eagle_sch_file_tmp.path
    end
    
    prefix = ""
    prefix = "xvfb-run" if system "which xvfb-run"
    
    ## Eagle cannot handle relative file names well -> make it absolute
    #output = File.expand_path(output)
    
    cmd = "eagle -C \"run '#{file.path}' '#{output}' ; quit\" '#{eagle_sch_file}'"
    puts "Running: " + cmd
    if ! system cmd || !File.exists?(output)
      raise "ERROR: Couldn't execute Eagle to generate pin file."
    end
  ensure
    file.close
    file.unlink
    
    if eagle_sch_file_tmp
      eagle_sch_file_tmp.close
      eagle_sch_file_tmp.unlink
    end
  end
end

def load_pins_from_eagle(eagle_sch_file)
  pins_file = File.join($tempdir || ".", "pins_from_eagle.rb")
  
  #TODO This generates a file. This fact should be available to the Makefile.
  generate_pins_from_eagle(
    eagle_sch_file,
    pins_file)
  
  require pins_file
end

def string_or_regex_matches(pattern, str)
  case pattern
  when String
    return pattern == str
  when Regexp
    return pattern =~ str
  end
end

def eagle_pins(ic_regex, name_regex, pin_regex = //)
  PINNAMES.each do |part_name,pins|
    if string_or_regex_matches(ic_regex, part_name)
      pins.each do |pin,name|
        if string_or_regex_matches(pin_regex, pin) and string_or_regex_matches(name_regex, name)
          pin(name, pin.split("_")[0])
        end
      end
    end
  end
end

def eagle_port(port_name, ic_regex, name_regex, pin_regex = //)
  ports = {}
  PINNAMES.each do |part_name,pins|
    if string_or_regex_matches(ic_regex, part_name)
      pins.each do |pin,name|
        if string_or_regex_matches(pin_regex, pin) and string_or_regex_matches(name_regex, name)
          port = pin[1..1]
          bit = pin[2..2].to_i
          
          ports[port] ||= 0
          ports[port] |= (1<<bit)
        end
      end
    end
  end
  
  full_ports = ports.select {|port,bits| bits == 0xff}.map {|x| x[0]}
  case full_ports.length
  when 1
    port(port_name, "P" + full_ports[0])
  when 0
    raise "ERROR: No complete port found. Candidates: #{ports.inspect}"
  else
    raise "ERROR: More than one full port: #{full_ports.join(", ")}"
  end
end
