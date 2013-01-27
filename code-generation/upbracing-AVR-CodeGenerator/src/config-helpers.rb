
require 'parse-dbc.rb'
require 'parse-ecu-list.rb'

JRubyHelpers     = Java::de::upbracing::code_generation::JRubyHelpers
CodeGeneratorConfigurations = Java::de::upbracing::code_generation::config::CodeGeneratorConfigurations
ConfigurationExtender       = Java::de::upbracing::code_generation::config::ConfigurationExtender

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
  # return, if the output file is present and new enough
  # We don't run this check for classpath items because we cannot (usually)
  # get an mtime for them.
  return if !(eagle_sch_file =~ /^classpath:/) and File.exists?(output) and File.mtime(output) >= File.mtime(eagle_sch_file)
  
  if File.exists?(output) and not system("which eagle")
    puts "WARNING: Cannot find Eagle, so '#{output}' will not be refreshed!"
    return
  end
  
  ulp = JRubyHelpers.readResource("processor_pins.ulp")
  
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

READY = Java::de::upbracing::code_generation::config::rtos::RTOSTask::TaskState::READY
SUSPENDED = Java::de::upbracing::code_generation::config::rtos::RTOSTask::TaskState::SUSPENDED


# tools for statemachines

# get the factory that you can use to create statemachine.* instances
#NOTE I think you cannot do this in JRuby because it looks for "statemachine.*",
#     if you ask for "statemachine.*". JRuby seems to expect lower-case package
#     names only - not confirmed, but just my guess.
#NOTE Now the name is lower-case, so we could do it in JRuby.
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


NO_LOCK = Java::de::upbracing::code_generation::fsm::model::StatemachineLockMethod::NO_LOCK
OS_LOCK = Java::de::upbracing::code_generation::fsm::model::StatemachineLockMethod::OS
SEMAPHORE_LOCK = Java::de::upbracing::code_generation::fsm::model::StatemachineLockMethod::SEMAPHORE
INTERRUPT_LOCK = Java::de::upbracing::code_generation::fsm::model::StatemachineLockMethod::INTERRUPT
CUSTOM_LOCK = Java::de::upbracing::code_generation::fsm::model::StatemachineLockMethod::CUSTOM


#$can_aliases = array(
#  'messages' => array(), 'signals' => array(),
#  'messages_back' => array(), 'signals_back' => array()
#);

def current_can_ecu
  return $config.current_ecu && $config.current_ecu.node_name \
    && $config.can.getEcu($config.current_ecu.node_name)
end

def find_can_object(objspec, search_global = false)
  return objspec unless objspec.is_a? String

  v_MSG_NAME = /([1-9][0-9]*x?|0x[0-9a-f]+x?|[a-z_][a-z_0-9]*)/i
  v_SIGNAL_NAME = /([a-z0-9_]+)/i
  v_MOB_NAME = /([a-z0-9_]+)/i
  v_MOB_REGEX = /^\s*mob\(\s*#{v_MOB_NAME}\s*\)\s*$/i
  v_MSG_REGEX = /^\s*msg\(\s*#{v_MSG_NAME}\s*(,\s*global\s*)?\)\s*$/i
  v_SIGNAL_REGEX = /^\s*signal\(\s*#{v_SIGNAL_NAME}\s*(,\s*#{v_MSG_NAME}\s*)?\)\s*$/i
  v_MOB_REGEX = /^\s*mob\(\s*#{v_MOB_NAME}\s*\)\s*$/i
  
  ecu = current_can_ecu()
  
  if objspec =~ v_MSG_REGEX
    is_message = true
    name = $1
    search_global ||= $2
    
    if search_global
      return $config.can.getMessage(name)
    else
      return ecu.getMessage(name)
    end

  elsif v_SIGNAL_REGEX =~ objspec
    name = $1
    msg_name = $2
    
    if msg_name
      return $config.can.getMessage(msg_name).getSignal(name)
    else
      #messages = $config.can.messages
      messages = ecu.rx_msgs.to_a + ecu.tx_msgs.to_a
      signals = messages.collect { |msg|
        msg.signals[name]
      }.select {|x| x}.uniq
      
      if signals.empty?
        raise "Couldn't find signal: " + name
      elsif signals.length > 1
        raise "Ambiguous signal: " + name
      else
        return signals.first
      end
    end
  elsif v_MOB_REGEX =~ objspec
    name = $1

    return ecu.getMobByName(name)
    #TODO create if it doesn't exist?
  else
    raise "Cannot understand key: " + objspec
  end
  
  raise "Couldn't find object: " + objspec
end

# You can call this as can_config(objspec, value_map) or can_config(objspec, key, value)
def can_config(objspec, values, x = nil)
  if x != nil
    # if we have a value, the second argument is the key
    values = { values => x } 
  elsif not values.is_a? Hash
    # it is not a hash, so we treat it as a flag
    values = { values => true }
  end
  
  # convert all keys to string
  values_old = values
  values = {}
  values_old.each do |k,v|
    values[k.to_s] = v
  end

  search_global = values["add_rx"] || values["add_tx"]
  obj = find_can_object(objspec, search_global)
  
  ecu = current_can_ecu()

  if obj && obj.is_a?(Java::de::upbracing::dbc::DBCMessage)
    if values["add_rx"]
      ecu.rx_msgs << obj unless ecu.rx_msgs.contains? obj
      
      if values["add_rx_signals"]
        signals = values["add_rx_signals"].map { |name| obj.getSignal(name) }
      else
        signals = obj.signal_order
      end
      
      signals.each do |sig|
        ecu.rx_signals << sig unless ecu.rx_signals.contains? sig
      end
      
      values.delete "add_rx"
      values.delete "add_rx_signals"
    end
    
    if values["add_tx"]
      ecu.tx_msgs << obj unless ecu.tx_msgs.contains? obj
      
      values.delete "add_tx"
    end
    
    if values["ignore_rx"] || values["ignore"]
      ecu.rx_msgs.remove obj
      
      obj.signal_order.each do |sig|
        ecu.rx_signals.remove sig
      end
    end
    ecu.tx_msgs.remove obj if values["ignore_tx"] || values["ignore"]
    
    ["ignore", "ignore_rx", "ignore_tx"].each do |x| values.delete x end
  end
  
  values.each do |k,v|
    obj.send("#{k}=".intern, v)
  end
end

def cleanup_code(code)
  # remove leading whitespace (e.g. from a here doc)
  code =~ /^(\s*)/
  whitespace = $1
  code = code.gsub /^#{whitespace}/, ""
  
  # replace indent by tab
  code = code.gsub(/^[ ]+/) { |x| "\t"*(x.length/2) }
    
  return code
end

def add_code(objspec, key, code, before = false)
  code = cleanup_code(code)
  
  obj = find_can_object(objspec)
  
  existing_code = obj.send(key)
  
  if existing_code
    if before
      code = code + "\n" + existing_code
    else
      code = existing_code + "\n" + code
    end
  end
  
  obj.send((key+"=").intern, code)
end

#function msg_alias($new, $old) {
#  global $can_aliases;
#  
#  $can_aliases['messages'][$new] = $old;
#  if (!isset($can_aliases['messages_back'][$old]))
#    $can_aliases['messages_back'][$old] = array();
#  $can_aliases['messages_back'][$old][] = $new;
#}
#
#function signal_alias($new, $old) {
#  global $can_aliases;
#  
#  $can_aliases['signals'][$new] = $old;
#  if (!isset($can_aliases['signals_back'][$old]))
#    $can_aliases['signals_back'][$old] = array();
#  $can_aliases['signals_back'][$old][] = $new;
#}

def snake_case(name)
  name.gsub(/[a-z0-9][A-Z]/) { |a| a[0] + "_" + a[1].downcase }
end

def upcase_first(name)
  name[0].upcase + name[1..-1]
end

def downcase_first(name)
  name[0].downcase + name[1..-1]
end

# add configuration extensions (methods and properties) to the configuration object (for use by JRuby)
class RubyConfigurationExtender
  include ConfigurationExtender
  # add a property that you can get and set by name (you must first add it as state)
  def addProperty(name, state)
    snake_name = snake_case name
    getters = [ name, snake_name, "get" + upcase_first(name) ].uniq
    setters = [ name+"=", snake_name+"=", "set" + upcase_first(name) ].uniq
    CodeGeneratorConfigurations.class_eval do
      getters.each do |mname|
        define_method(mname.intern) { self.getProperty name }
      end
      setters.each do |mname|
        define_method(mname.intern) { |*args| self.setProperty name, *args }
      end
    end
  end
  
  # add a read-only property that you can get by name (you must first add it as state)
  def addReadonlyProperty(name, state)
    snake_name = snake_case name
    getters = [ name, snake_name, "get" + upcase_first(name) ].uniq
    CodeGeneratorConfigurations.class_eval do
      getters.each do |mname|
        define_method(mname.intern) { self.getProperty name }
      end
    end
  end
  
  # add invisible state
  def addState(state, cls)
    # we don't care about this
  end
  
  # add a method
  def addMethod(name, method)
    CodeGeneratorConfigurations.class_eval do
      names = [name, snake_case(name)]
      if name =~ /^set/
        # It is a setter, so we add the usual aliases.
        name2 = downcase_first(name.sub(/^set/, ""))
        names << name2+"="
        names << snake_case(name2)+"="
      end
      names.uniq.each do |mname|
        define_method(mname.intern) do |*args|
          # get parameter types (without the first one which is the config)
          param_types = method.parameter_types[1..-1].to_a
          if method.isVarArgs
            # Find out whether the user tries to call it variadic (not with an array)
            #var_arg_type = param_types[-1]
            var_arg = args[-1]
            # If the uses passed too many arguments, we obviously use the variadic variant.
            uses_var_arg = args.length > param_types.length
            if !uses_var_arg
              # It might be a single variadic argument or an array.
              #  May not work for all cases, but at least for the ones in my test - that should be enough.
              is_array = var_arg.is_a?(Array) || (var_arg.respond_to?(:java_class) && var_arg.java_class.array?)
              uses_var_arg = !is_array
            end

            if uses_var_arg
              # Get the type of the variadic items
              var_arg_type = param_types[-1].component_type
              
              # Replace the variadic param type by
              # some copies of the array item type.
              # (and make sure it is a Ruby list)
              param_types = [*param_types[0..-2]]
              
              (args.length - param_types.length).times do
                param_types << var_arg_type
              end
            end
          end
          if param_types.length != args.length
            raise "I need #{param_types.length} arguments#{method.isVarArgs ? " (or more)" : ""}, but you gave me #{args.length}!"
          end
          
          # tell JRuby to cast the values appropiately
          args = param_types.zip(args).map do |type,value|
            #puts "#{value.inspect} -> #{type}"
            if type.interface?
              # JRuby doesn't seem to be able to convert this
              value
            elsif !type.isArray
              value.to_java(type)
            else
              # to_java of (JRuby) Array needs the item type
              value.to_a.to_java(type.component_type)
            end
          end
          
          if uses_var_arg
            # pack them into an array
            
            # non-variadic arguments (not counting config and the variadic array argument)
            non_var_args = method.parameter_types.length - 2
            
            non_variadic = args[0...non_var_args]
            vardiac_arg = args[non_var_args..-1].to_java(var_arg_type)
            
            args = non_variadic + [vardiac_arg]
          end
          
          method.invoke(nil, [self.to_java(CodeGeneratorConfigurations), *args].to_java)
          #self.call name, *args
        end
      end
    end
  end
end

CodeGeneratorConfigurations.add_extension_listener RubyConfigurationExtender.new
