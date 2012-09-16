#DEPENDS ON:
counter_statechart = "counter.statecharts"

counter = $config.statemachines.load("counter", counter_statechart)
counter.for_test = true
