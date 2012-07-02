require 'rubygems'
require 'xmlsimple'

begin
  require 'java'
  $jruby = true
rescue LoadError
  $jruby = false
end

if $jruby
  EEPROMValue = Java::de::upbracing::eculist::EEPROMValue
  ECUDefinition = Java::de::upbracing::eculist::ECUDefinition
end

class EEPROMValue
  attr_accessor :name, :type, :default unless $jruby
  def init(attrs)
    attrs.each do |k,v|
      self.send("#{k}=".intern, v)    if [:name, :type, :default].member? k.intern
    end
    self
  end
  
  def to_s
    "eeprom-value(#{@name}:#{@type}=#{@default})"
  end
end

class ECUDefinition
  attr_accessor :name, :path, :build_dir, :type, :node_id, :node_name, :eeprom_values unless $jruby
  def init(attrs)
    attrs.each do |k,v|
      self.send("#{k}=".intern, v)    if [:name, :path, :build_dir, :type, :node_id, :node_name].member? k.intern
    end
    
    eeprom = attrs["eeprom"]
    if eeprom
      self.eeprom_values = eeprom[0]["value"].map do |x|
        EEPROMValue.new.init x
      end
    end
    self
  end
end

def read_ecu_list(filename = nil)
  filename = File.dirname(__FILE__) + "/../ecu-list.xml" unless filename
  data = XmlSimple.xml_in(filename)
  ecus = data["ecu"].map do |x|
    ECUDefinition.new.init(x)
  end
  return ecus
end

def get_ecu_definition(kwargs = {})
  ecu_name = kwargs[:ecu_name]
  ecu_program_dir = nil
  if kwargs[:ecu_program_dir]
    ecu_program_dir = kwargs[:ecu_program_dir]
  elsif kwargs[:calling_file]
    ecu_program_dir = File.dirname(kwargs[:calling_file])
  end
  if not ecu_name and not ecu_program_dir
    raise "invalid usage"
  end
  
  if kwargs[:ecu_list]
    ecu_list = kwargs[:ecu_list]
  else
    ecu_list_filename = kwargs[:ecu_list_filename] || nil
    ecu_list = read_ecu_list(ecu_list_filename)
  end
  
  if ecu_name
    ecudef = ecu_list.find { |x| x.name == ecu_name }
    puts "WARN: Invalid ecu name: #{ecu_name}"  unless ecudef
  else
    def normalize_path(path)
      path = File.realpath(path)
      path.gsub("\\", "/")
      path += "/" unless path[-1] == "/"
    end
    # if ecu_program_dir is a sub-path of ecu_list_dir, we strip that prefix
    ecu_program_dir = normalize_path(ecu_program_dir)
    ecu_list_dir = normalize_path(File.dirname(__FILE__) + "/..")
    if ecu_program_dir.start_with? ecu_list_dir
      ecu_program_dir_short = ecu_program_dir[ecu_list_dir.length...ecu_program_dir.length]
    else
      ecu_program_dir_short = ecu_program_dir
    end
    # find a matching ECUDefinition
    ecudef = ecu_list.find { |x| x.path and (ecu_program_dir_short == x.path || ecu_program_dir == normalize_path(x.path)) }
    if not ecudef
      puts "WARN: Cannot find an ecu with path '#{ecu_program_dir}' in ecu-list.xml"
    end
  end
  
  return ecudef
end
