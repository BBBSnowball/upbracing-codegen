
JRubyHelpers = Java::de::upbracing::code_generation::JRubyHelpers

def port(*args) ; $config.pins.addPort(*args) ; end
def pin(*args)  ; $config.pins.add(*args)     ; end
def pinAlias(*args) ; $config.pins.addAlias(*args) ; end

def pins(pins)
  pins.each do |name,pin|
    if pin.start_with? "="
      pinAlias(name, pin[1..-1])
    elsif pin.length == 3
      pin(name, pin)
    elsif pin.length == 2
      port(name, pin)
    else
      raise "Don't know what I should do with pin name: " + pin
    end
  end
end

def getResourceAsStream(name)
end

def generate_pins_from_eagle(eagle_sch_file, output)
  return if File.exists?(output) and File.mtime(output) >= File.mtime(eagle_sch_file)
  
  if File.exists?(output) and not system("which eagle")
    puts "WARNING: Cannot find Eagle, so '#{output}' will not be refreshed!"
    return
  end
  
  ulp = JRubyHelpers.readResource("processor_pins.ulp")
  
  require 'tempfile'
  file = Tempfile.new(["processor_pins", ".ulp"])
  begin
    file.write ulp
    file.close
    
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
  end
end

def load_pins_from_eagle(eagle_sch_file)
  #TODO This generates a file. This fact should be available to the Makefile.
  generate_pins_from_eagle(
    eagle_sch_file,
    "pins_from_eagle.rb")
  
  require 'pins_from_eagle'
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

READY = Java::de::upbracing::code_generation::config::RTOSTask::TaskState::READY
SUSPENDED = Java::de::upbracing::code_generation::config::RTOSTask::TaskState::SUSPENDED


# tools for statemachines

# get the factory that you can use to create Statecharts.* instances
#NOTE I think you cannot do this in JRuby because it looks for "statecharts.*",
#     if you ask for "Statecharts.*". JRuby seems to expect lower-case package
#     names only - not confirmed, but just my guess.
def statemachine_factory
  JRubyHelpers::getStatemachineFactory
end

# add a global code box with some code
# addGlobalCode(StateMachineForGeneration smg, boolean in_header, String code)
def addGlobalCode(*args)
  JRubyHelpers::addGlobalCode(*args)
end

# enable tracing and include appropiate code
def enableTracing(smg, level, printer, declarations)
  smg.enable_tracing(level, printer)
  addGlobalCode(smg, false, "#include <avr/pgmspace.h>\n" + declarations)
end
