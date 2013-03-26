# We need some Java classes, so we define an alias
# for the package.
# -> nope, we don't because parse-dbc.rb already
#    defines aliases for all the classes

# we add additional instance variables to these
# objects, so we must make the wrappers persistent
DBCMessage.__persistent__ = true
DBCSignal.__persistent__  = true

class Hash
  def with_string_keys
    x = {}
    self.each_pair do |k,v|
      x[k.to_s] = v
    end
    return x
  end
end

def empty_dbc(&block)
  dbc = DBC.new ""
  dbc.ecus = {} #Java::java::utils::HashMap.new
  dbc.ecu_names = []
  dbc.value_tables = {}
  dbc.messages = {}

  dbc.instance_eval &block if block_given?    
  
  return dbc
end

class DBC
  # create an ECU
  def create_ecu(name, &block)
    ecu = DBCEcu.new name
    ecu.rx_msgs = []
    ecu.rx_signals = []
    ecu.tx_msgs = []
    
    self.ecus[name] = ecu
    self.ecu_names << name
  
    ecu.instance_eval &block if block_given?
      
    return ecu
  end
  
  # create a message
  def create_message(name, id, extended, length=1, &block)
    case extended.to_s
    when "standard"
      extended = false
    when "extended"
      extended = true
    end
      
    raw_id = (extended ? (1<<31)+id : id).to_s
    msg = DBCMessage.new(id, raw_id, extended, name, length, [])
    msg.signals = {}
    msg.signal_order = []
    msg.dbc = self
    
    self.messages[raw_id] = msg
    self.messages[name]   = msg
    
    msg.instance_eval &block if block_given?
    
    return msg
  end
  alias :create_msg :create_message
end

class DBCMessage
  attr_accessor :dbc
  
  def sent_by(*ecus)
    ecus = ecus.flatten
    if ecus.length == 1
      ecu = ecus.first
    else
      ecus.each do |ecu|
        self.sent_by ecu
      end
      return
    end
    
    ecu = self.dbc.ecus[ecu.to_s] if ecu.is_a? String or ecu.is_a? Symbol

    # puts "message #{self.name} is sent by #{ecu.name}"
    
    self.tx_ecus << ecu
    ecu.tx_msgs << self
  end
  
  def all_signals_received_by(*ecus)
    ecus = ecus.flatten
    if ecus.length == 1
      ecu = ecus.first
    else
      ecus.each do |ecu|
        self.all_signals_received_by ecu
      end
      return
    end

    ecu = self.dbc.ecus[ecu.to_s] if ecu.is_a? String or ecu.is_a? Symbol
    
    ecu.rx_msgs << self
    self.signal_order.each do |signal|
      ecu.rx_signals << signal
    end
  end
  
  def create_signal(name, opt = {}, &block)
    opt = opt.with_string_keys
    endianness = opt["little_endian"] && "1" || opt["big_endian"] && "0" || "1"
    start = opt["start"] || 0
    start += 7 if endianness != "1"
    signal = DBCSignal.new(name,
      opt["signed"] && "-" || "+",
      endianness, start, opt["length"] || 8, self,
      opt["factor"] || 1, opt["offset"] || 0,
      opt["min"] || 0, opt["max"] || 0, opt["unit"] || "",
      [])
    signal.dbc = self.dbc
    self.signals[name] = signal
    #TODO do we have to sort it?
    self.signal_order << signal
    
    signal.instance_eval &block if block_given?
    
    return signal
  end
end

class DBCSignal
  attr_accessor :dbc
  
  def received_by(*ecus)
    ecus = ecus.flatten
    if ecus.length == 1
      ecu = ecus.first
    else
      ecus.each do |ecu|
        self.received_by ecu
      end
      return
    end

    ecu = self.dbc.ecus[ecu.to_s] if ecu.is_a? String or ecu.is_a? Symbol
    
    puts "signal #{self.message.name}:#{self.name} is received by #{ecu.name}"
    
    ecu.rx_signals << self
    ecu.rx_msgs << self.message unless ecu.rx_msgs.member? self.message
    self.rx_ecus << ecu
  end
end
