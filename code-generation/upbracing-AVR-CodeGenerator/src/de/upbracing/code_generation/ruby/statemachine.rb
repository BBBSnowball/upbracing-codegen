
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
