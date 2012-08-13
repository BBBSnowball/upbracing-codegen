
begin
  require 'java'
  $jruby = true
rescue LoadError
  $jruby = false
end

if $jruby
  DBCSignal = Java::de::upbracing::dbc::DBCSignal
  DBCMessage = Java::de::upbracing::dbc::DBCMessage
  DBCEcu = Java::de::upbracing::dbc::DBCEcu
  DBC = Java::de::upbracing::dbc::DBC
  DBCValueTable = Java::de::upbracing::dbc::DBCValueTable
end

class DBCSignal
  if !$jruby
    attr_reader :name, :sign, :endianness, :start, :length, :message, :factor, :offset, :min_limit, :max_limit, :unit, :rx_ecus
    attr_accessor :value_table, :values
    def initialize(name, sign, endianness, start, length, msg, factor, offset, min, max, unit, rx_ecus)
      @name = name
      @sign = sign
      @endianness = endianness
      @start = start
      @length = length
      @message = msg
      @factor = factor
      @offset = offset
      @min_limit = min
      @max_limit = max
      @unit = unit
      @rx_ecus = rx_ecus
      @value_table = nil
      @values = nil
    end
  end
  
  def init
    return self
  end
  
  def to_s
    "signal(#{name}, #{sign == "+" ? "u" : "s"}#{length})"
  end
  
  if defined? Ruby2Java
    def name2
      self.name
    end
    
    str = Java::string  #java.lang.String
    fl = Java::float  #java.lang.Float
    int = Java::int
    #{ :name => str, :sign => str, :endianness => str, :start => int,
    #  :length => int, :factor => fl, :offset => fl, :min_limit => fl,
    #  :max_limit => fl, :unit => str }.each do |x,t|
    #    signature x, [] => t
    #end
    puts "blub: #{instance_method(:name2).args}"
    signature :name2, [] => java.lang.String
    #TODO :message, :rx_ecus
  end
end

class DBCMessage
  if !$jruby
    attr_reader :id, :raw_id, :extended, :name, :length, :tx_ecus, :signals, :signal_order
    attr_accessor :comment
    def initialize(id, raw_id, extended, name, len, tx_ecus)
      @id = id
      @raw_id = raw_id
      @extended = extended
      @name = name
      @length = len
      @tx_ecus = tx_ecus
      @signals = {}
      @signal_order = []
      @comment = nil
    end
    def init
      return self
    end
  else
    def init
      self.signals = {}
      self.signal_order = []
      return self
    end
  end
  
  def add_signal(signal)
    self.signals[signal.name] = signal
    self.signal_order << signal
    self.signal_order.sort! { |a,b| 1000*a.start + a.length <=> 1000*b.start + b.length }
  end
  
  def to_s
    sigs = signal_order.map { |s| s.to_s }.join(", ")
    "msg(#{name}, \##{id.to_s(16)}#{extended ? "x" : ""}, [ #{sigs} ])"
  end
end

class DBCEcu
  if !$jruby
    attr_reader :name, :tx_msgs, :rx_msgs, :rx_signals
    attr_accessor :comment
    def initialize(name)
      @name = name
      @tx_msgs = []
      @rx_msgs = []
      @rx_signals = []
      @comment = nil
    end
    def init
      return self
    end
  else
    def init
      self.tx_msgs = []
      self.rx_msgs = []
      self.rx_signals = []
      return self
    end
  end
  
  def to_s
    rx_names = rx_msgs.map { |m| m.name }.join(",")
    tx_names = tx_msgs.map { |m| m.name }.join(",")
    "ecu(#{name}, rx:#{rx_names}, tx:#{tx_names})"
  end
end

class DBC
  if !$jruby
    attr_reader :version, :value_tables, :ecus, :messages, :signals
    attr_accessor :ecu_names
    def initialize(version)
      @version = version
      @value_tables = {}
      @ecus = {}
      @messages = {}
      @signals = {}
      @ecu_names = nil
    end
    def init
      return self
    end
  else
    def init
      self.value_tables = {}
      self.ecus = {}
      self.messages = {}
      self.signals = {}
      return self
    end
  end
  
  def to_s
    "dbc{ ecus = #{ecus}, msgs = #{messages}, vtables = #{value_tables.keys} }"
  end
end

def parse_dbc(filename = nil)
  filename = File.dirname(__FILE__) + "/../can_final.dbc"    unless filename
  
  dbc_result = nil
  mode = :init
  context = nil
	open(filename, "rt") do |f|
		DBCTokenizer.new.tokenize_dbc f do |tokens|
		  #DEBUG: puts tokens.inspect
		  if tokens.length > 0 && !(mode == :skip_indented && tokens.first == " ")
		    case tokens[0]
		    when "VERSION"
          dbc_result = DBC.new(tokens[1]).init
          mode = :normal
          context = nil
		    when "NS_"
          mode = :skip_indented
          context = nil
		    when "BS_"
		      # do nothing
		    when "BU_"
          start = (tokens[1] == ':' ? 2 : 1)
          ecu_names = tokens[start...tokens.length]
          dbc_result.ecu_names = ecu_names
          ecu_names.each do |name|
            dbc_result.ecus[name] = DBCEcu.new(name).init
          end
          mode = :normal
          context = nil
		    when "VAL_TABLE_"
          name = tokens[1]
          if $jruby
            values = DBCValueTable.new
          else
            values = {}
          end
          i = 2
          while i+1 < tokens.length
            values[tokens[i]] = tokens[i+1]
            i += 2
          end
          dbc_result.value_tables[name] = values
          mode = :normal
          context = nil
		    when "VAL_"
          raw_id = tokens[1]
          signal_name = tokens[2]
          msg = dbc_result.messages[raw_id]
          if !msg || !msg.signals[signal_name]
            puts "Ignoring value table, as there is no message with (raw) id #{raw_id}"
          else
            signal = msg.signals[signal_name]
            values = {}
            i = 3
            while i+1 < tokens.count
              values[tokens[i]] = tokens[i+1]
              i += 2
            end
            signal.values = values
            
            dbc_result.value_tables.each do |name,vt|
              if values == vt    #TODO this won't work...
                signal.value_table = name
                break
              end
            end
            #echo "value table for " . $signal['name'] . ": " . $signal['value_table'] . "\n"; 
          end
          mode = :normal
          context = nil
		    when "BO_"
          raw_id = id = tokens[1]
          name = tokens[2]
          len = tokens[4].to_i
          if tokens[5] == "Vector__XXX"
            ecus = []
          else
            ecus = tokens[5].split(",").map { |ecuname| dbc_result.ecus[ecuname] }
          end
          id = id.to_i
          extended = (id & (1<<31)) != 0
          #$extended = bccomp($id, "2147483648") >= 0;
          id &= ~(1<<31)
          if !dbc_result.messages[name] and !dbc_result.messages[raw_id]
            msg = DBCMessage.new(id, raw_id, extended, name, len, ecus).init
            dbc_result.messages[name] = msg
            dbc_result.messages[raw_id] = msg
            ecus.each do |ecu|
              ecu.tx_msgs << msg
            end
            mode = :message
            context = msg
          else
            puts "WARN: Ignoring second message with same name or id: id=#{raw_id}, name=#{name}"
            puts "      #{tokens}"
            mode = :message
            context = nil
          end
		    when " "
		      if tokens[1] == "SG_" and mode == :message
		        if context
              name = tokens[2];
              tokens[4] =~ /^([0-9]+)\|([0-9]+)@([01])([+-])$/
              start = $1.to_i
              length = $2.to_i
              endianness = $3
              sign = $4
              tokens[5] =~ /^\(([0-9.]+),([0-9]+)\)$/
              factor = $1.to_f
              offset = $2.to_f
              tokens[5] =~ /^\[([0-9.]+)\|([0-9]+)\]$/
              min = $1.to_f
              max = $2.to_f
              unit = tokens[7]
              if (tokens[8] != "Vector__XXX")
                #TODO resolve names? -> dbc_result[:ecus][ecuname]
                ecus = tokens[8].split(",").map { |ecuname| dbc_result.ecus[ecuname] }
              else
                ecus = []
              end
              
              msg = context
              signal = DBCSignal.new(name, sign, endianness, start, length, msg, factor, offset, min, max, unit, ecus)
              if !msg.signals[name]
                dbc_result.signals[name] = signal
                msg.add_signal(signal)
                ecus.each do |ecu|
                  if !ecu
                    puts "WARN: invalid ecu name: #{ecuname}"
                  else
                    ecu.rx_msgs << msg unless ecu.rx_msgs.member? msg
                    ecu.rx_signals << signal
                  end
                end
              else
                puts "WARN: Ignoring second signal with same name: #{name}"
              end
              
              # mode and context stay the same
		        end
		      else
            puts "Ignoring line starting with tokens: <space> #{tokens[1]}"
            mode = :normal
		      end
		    when "BO_TX_BU_"
          raw_id = tokens[1]
          ecu_names = tokens[3].split(",")
          msg = dbc_result.messages[raw_id]
          ecu_names.each do |ecuname|
            ecu = dbc_result.ecus[ecuname]
            if !ecu.tx_msgs.member? msg
              ecu.tx_msgs << msg
              msg.tx_ecus << dbc_result.ecus[ecuname]
            end
          end
		    when "CM_"
		      case tokens[1]
		      when "BU_"
		        dbc_result.ecus[tokens[2]].comment = tokens[3]
		      when "BO_"
		        dbc_result.messages[tokens[2]].comment = tokens[3]
		      else
		        puts "Ignoring comment for object of type '#{tokens[1]}'"
		      end
		    when "BA_DEF_", "BA_DEF_DEF_"
          # ignore attribute defitions and attribute values
		    else
          puts "Ignoring line starting with token: '#{tokens[0]}'"
          mode = :normal
		    end
		  end
		end
	end
	return dbc_result
end

class DBCTokenizer
  def tokenize_dbc(f)
    @f = f
    while (@line = @f.gets)
      @line = @line.sub(/\r?\n$/, "")
  		tokens = []
  		@i = 0
  		while true
  			token = getToken
  			if token
  				tokens << token
  			else
  				break
  			end
  		end
  		yield tokens
  	end
  end
  
  private

  def getToken
  	was_start = (@i == 0)
  	while @i < @line.length && " \t".index(@line[@i..@i])
  		@i += 1
  	end
  	if @i >= @line.length
  		return nil
  	end
  	if was_start && @i > 0
  		return " "
  	end
  	
  	c = @line[@i..@i]
  	case c
  		when ':', ';'
  		  @i += 1
  			return c
  		when '"'
  			@i += 1
  			return parseQuoted
  		else
  			return parseNormal
  	end
  end

  def parseNormal
  	start = @i
  	while @i < @line.length && !" \t:;".index(@line[@i..@i])
  		@i += 1
  	end
  	#puts "normal: #{start} .. #{@i}"
  	return @line[start...@i]
  end

  def parseQuoted
  	#echo "quoted starts at $i\n";
  	
  	start = @i
  	x = ""
  	j = 0
  	while true
  		j += 1
  		break if j > 10
  	  end_pos = @line.index('"', start)
  		if end_pos
  		  @i = end_pos + 1
        #TODO add '\n' ?
  		  return x + @line[start...end_pos]
      else
  			#TODO add '\n' ?
        x = x + @line[start..@line.length]
  			@line = @f.readline.sub(/\n$/, "")
  			start = 0
  		end
  	end
  end
end

def read_ecu_can(ecu_node_name, dbc_filename = nil)
  ecu_node_name = ecu_node_name.node_name unless ecu_node_name.is_a? String 
    #if ["de.upbracing.eculist.ECUDefinition", "ECUDefinition"].member? ecu_node_name.class.name
  dbc_data = parse_dbc(dbc_filename)
  ecu_can = dbc_data.ecus[ecu_node_name]
  if not ecu_can
    puts "\n#pragma error Node with name '#{ecu_node_name}' not found in #{dbc_filename && File.basename(dbc_filename)}\n"
  end
  return ecu_can
end
