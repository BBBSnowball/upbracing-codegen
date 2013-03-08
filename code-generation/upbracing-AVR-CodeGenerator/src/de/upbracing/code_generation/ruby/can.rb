
#$can_aliases = array(
#  'messages' => array(), 'signals' => array(),
#  'messages_back' => array(), 'signals_back' => array()
#);

def current_can_ecu
  if $config.use_can_node
    $config.can.getEcu($config.use_can_node)
  elsif $config.current_ecu && $config.current_ecu.node_name
    $config.can.getEcu($config.current_ecu.node_name)
  else
    nil
  end
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
    msg_name = $3
    
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
