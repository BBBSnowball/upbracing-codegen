
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
