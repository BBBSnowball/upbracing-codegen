# domain specific language for statemachines
# Use it instead of (or in addition to) the GUI, if your
# statemachine contains repetitive parts that you would
# like to generate.

# The syntax is similar to pluginaweek/state_machine [1]
# with a few additions and modifications. The most
# important ones are:
# - not embedded in a class -> statemachine is returned
#   by the state_machine function
# - actions are strings with C code, not Ruby blocks
# - optional parameters are different for most functions
# [1] https://github.com/pluginaweek/state_machine

# first argument is either a name for the new statemachine
# or an existing statemachine object
# named arguments:
#  :initial     - name of initial state
#  :base_period - base period for the statemachine
#  :factory     - factory to use for constructing statemachine parts
def state_machine(name_or_existing_statemachine, named_args = {}, &block)
  #StatemachineBuilder.new name_or_existing_statemachine, named_args, &block

  unknown_keys = named_args.keys - [:initial, :base_period, :factory, :name]
  raise "unknown keys in arguments: " + unknown_keys.inspect unless unknown_keys.empty?

  if name_or_existing_statemachine.is_a? String
    raise "cannot use two names" if named_args[:name]
    named_args[:name] = name_or_existing_statemachine
    
    # create a new statemachine
    factory = named_args[:factory] || statemachine_factory
    statemachine = factory.createStateMachine
    statemachine.factory = factory
  elsif name_or_existing_statemachine.is_a? Java::statemachine::StateMachine
    # use the existing statemachine
    statemachine = name_or_existing_statemachine
    statemachine.factory = named_args[:factory] if named_args[:factory]
  end
  
  if named_args[:name]
    statemachine = Java::de::upbracing::code_generation::fsm::model::StateMachineForGeneration.new named_args[:name], statemachine
  end
  
  statemachine.base_rate = named_args[:base_period] if named_args[:base_period]
  
  statemachine.initial = named_args[:initial] if named_args[:initial]
  
  statemachine.instance_eval(&block) if block_given?
  
  statemachine.update if statemachine.respond_to? :update
  
  return statemachine
end

alias :statemachine :state_machine

# we need persistent proxies to use instance variables
# https://github.com/jruby/jruby/wiki/Persistence
#NOTE It seems that we cannot set this on a module :-(
#Java::statemachine::StateMachine::__persistent__ = true
#Java::statemachine::StateParent.__persistent__  = true
Java::statemachine::impl::StateMachineImpl.__persistent__ = true
Java::statemachine::impl::RegionImpl.__persistent__ = true
Java::de::upbracing::code_generation::fsm::model::StateMachineForGeneration.__persistent__ = true

module Java::statemachine::StateMachine
  def statemachine
    self
  end
  
  def factory
    unless @factory
      @factory = statemachine_factory
    end
    return @factory
  end
  
  def factory=(factory)
    @factory = factory
  end
  
  # global_code(name = nil, :header/:cfile, ccode)
  # global_code(name, :header/:cfile => true, ccode)
  # global_code(..., ccode = &block)
  def global_code(*args, &block)
    if args[0].is_a? Hash
      named_args = args[0]
      args.delete_at 0
    elsif args[1].is_a? Hash
      named_args = args[1]
      args.delete_at 1
    else
      named_args = {}
    end
    
    unless block_given?
      ccode = args.last
      args.delete_at(-1)
    end

    if named_args[:header]
      type = :header
    elsif named_args[:cfile]
      type = :cfile
    elsif args.delete :header
      type = :header
    elsif args.delete :cfile
      type = :cfile
    else
      type = :cfile
    end
    
    if block_given? or args.length >= 2
      name = args.delete_at(0).to_s
    else
      name = nil
    end
    
    if not args.empty? and block_given?
      # we have a block, but we have an argument left for 'ccode'
      ccode = args.first
    end
    
    def process_code(code)
      # determine indent of first line and remove the same amount from each line
      if /\A[ \t]+/ =~ code
        code.gsub /^#{$~[0]}/, ""
      else
        code
      end
    end
    
    codebox = statemachine.factory.createGlobalCode
    codebox.name = name if name
    codebox.code = process_code(ccode) if ccode
    codebox.inHeaderFile = (type == :header)
    
    self.global_code_boxes << codebox
    
    if block_given?
      text = codebox.instance_eval(&block)
      codebox.code = process_code(text) if text and not ccode
    end 
    
    return codebox
  end
end

module Java::statemachine::StateParent
  def lookup(state)
    if state.is_a? String or state.is_a? Symbol
      states.select { |s| s.name == state.to_s }.first
    elsif state.is_a? Regexp
      states.select { |s| s.name =~ state }.first
    elsif state.is_a? Java::statemachine::State
      state
    else
      raise "invalid argument"
    end
  end
  
  def lookup_or_create(state)
    lookup state or ( s = statemachine.factory.createNormalState(state) ; s.name = state ; s )
  end
  
  # from,   to, t_info = nil, :priority => num
  # from => to, t_info = nil, :priority => num
  # ..., :priority => num, t_info = &block
  def transition(*args, &block)
    special_args = [:t_info, :transition_info, :condition, :priority]

    if args.first.is_a? Hash
      c = args.first
      if args.length > 1
        # remove the hash, rest is arguments
        args.delete_at 0
      else
        # it contains named arguments and transitions
        named_args = {}
        special_args.each do |argname|
          value = c.delete argname
          named_args[argname] = value if value
        end
        args = [named_args]
      end
      
      c.each_pair do |from,to|
        transition from, to, *args, &block
      end
      
      return nil
    end
    
    if args.last.is_a? Hash
      named_args = args.last
      args.delete named_args if args.length > 1
    else
      named_args = {}
    end
    
    from,to,t_info = args
    
    t_info ||= named_args[:t_info] || named_args[:transition_info]
    if named_args[:condition]
      t_info ||= "[#{named_args[:condition]}]"
    end
    
    t = self.statemachine.factory.createTransition
    
    t.source          = lookup_or_create from
    t.destination     = lookup_or_create to
    t.transition_info = t_info if t_info
    t.priority        = named_args[:priority] if named_args[:priority]
    
    statemachine.transitions.add t
    
    if block_given?
      text = t.instance_eval(&block)
      t.transition_info = text if text and not t_info
    end
    
    return t
  end

  def state(name, type = :normal, args = {}, &block)
    name = name.to_s
    
    if type.is_a? Hash
      args = type
      type = :normal
    end
    
    if self.is_a? Java::statemachine::SuperState
      if args[:region]
        region = self[args[:region]]
        if not region
          # create it
          region = self.region region
        end
        region.state name, type, args, &block
      else
        raise "not allowed for a superstate (only with a region)"
      end
    end
    
    should_be_initial = (name == @initial_state_name)
    if type == :initial
      should_be_initial = true
      @initial_state_name = name.to_s
      type = :normal
    elsif args[:initial]
      should_be_initial = true
      @initial_state_name = name.to_s
      args.delete :initial
    end
  
    factory = statemachine.factory
    case type
    when :normal
      s = factory.createNormalState
    when :final
      s = factory.createFinalState
    when :super, :superstate
      s = factory.createSuperState
    else
      raise "invalid value for argument 'type'"
    end
    
    s.name = name
    states.add s
    s.parent = self
    
    make_initial_state s if should_be_initial
    
    s.action args if args and not args.empty?
    
    s.instance_eval(&block) if block_given?
    
    return s
  end

  def [](name)
    states.select { |s| s.name == name.to_s }.first
  end
  
  def initial_states
    self.states.select { |x| x.is_a? Java::statemachine::InitialState }
  end
  
  def initial(*args)
    if not args.empty?
      value,action = args
      @initial_action = "/ " + action if action
      self.initial = value
      return
    end
    
    return @initial_state_name if @initial_state_name
    
    i = initial_states
    if i.length == 1
      return i.first.outgoing_transitions && i.first.outgoing_transitions.destination
    else
      return nil
    end
  end
  
  def make_initial_state(state)
    is = initial_states
    # remove all transitions from initial states
    is_already_initial = false
    is.each do |i|
      [*i.transitions].each do |t|
        # we don't remove existing connections to this state,
        # as they may have some data
        if t.destination == state
          is_already_initial = true
          t.transition_info = @initial_action if @initial_action
        else
          i.transitions.remove i
        end
      end
    end
    
    return if is_already_initial
    
    # create an initial state, if we don't have one
    if is.empty?
      i = statemachine.factory.createInitialState
      self.states << i
    else
      i = is.first
    end
    
    # connect initial state
    transition i, state, @initial_action
  end
  
  def initial=(state)
    # remember initial state
    @initial_state_name = state.to_s
    
    # If the state exists, make sure that it is the initial one.
    s = lookup(state)
    make_initial_state s if s
  end
end

module Java::statemachine::SuperState
  def region(name = nil, &block)
    r = statemachine.factory
    r.name = name if name
    regions.add r
    
    r.instance_eval(&block) if block_given?
    
    return r
  end
  
  def [](name)
    regions.select { |r| r.name == name.to_s }.first
  end
  
  def initial
    raise "not allowed for superstates"
  end
  def initial=(x)
    raise "not allowed for superstates"
  end
end

module Java::statemachine::Transition
  #alias :from  :getSource
  #alias :from= :source=
  #alias :to    :destination
  #alias :to=   :destination=
  
  def from
    self.source
  end
  def from=(x)
    self.source = x
  end
  def to
    self.destination
  end
  def to=(x)
    self.destination = x
  end
end

module Java::statemachine::State
  # like StateParent.transition, but without the first argument
  def transition_to(*args, &block)
    parent.transition self, *args, &block
  end
end

module Java::statemachine::StateWithActions
  def append_action(a)
    if not self.actions or self.actions.empty?
      self.actions = a
    else
      self.actions += "\n" + a
    end
  end
  
  def action(*args)
    # special case: one argument and a block
    if block_given?
      raise "need exactly one argument" unless args.length == 1
      append_action "#{args.first.to_s.upcase} / #{yield.to_s}"
      return
    end
    
    # append everything that we can find
    args.each do |arg|
      if arg.is_a? Hash
        arg.each_pair do |k,v|
          append_action "#{k.to_s.upcase} / #{v.to_s}"
        end
      else
        append_action arg.to_s
      end
    end
    
    return self
  end
  
  def valid_actions
    Java::de::upbracing::code_generation::fsm::model::ActionType.values.map {|v| v.name}
  end
  
  def method_missing(name, *args, &block)
    action_name = name.to_s.upcase
    if valid_actions.include? action_name
      if block_given?
        action(action_name, *args, &block)
      else
        args.each do |arg|
          action(action_name => arg)
        end
      end
    else
      super(name, *args, &block)
    end
  end
end

class Java::de::upbracing::code_generation::fsm::model::StateMachineForGeneration
  # should behave like the inner class (StateMachine)
  def method_missing(method, *args, &block)
    inner = self.getStateMachine
    inner.send(method, *args, &block) if inner.respond_to? method
  end
end
