
rundesc = $generator.make_run_description "upbracing-AVR-CodeGenerator",
	"upbracing-AVR-CodeGenerator"           => "CODE_GENERATOR_DIR",
	"java-parser-tools"                     => "JAVA_PARSER_TOOLS_DIR",
	"upbracing-AVR-TimerConfigurationModel" => "TIMER_CONFIGURATION_MODEL_DIR",
	"XMegaOS"                               => "CARTOS_DIR",
	"Statemachine"                          => "STATEMACHINE_DIR"

rundesc.write_scripts "run", $path,
	"de.upbracing.code_generation.Main", "$@"

rundesc.write_scripts "jruby", $path,
	"org.jruby.Main", "$@"

rundesc.write_scripts "irb", $path,
	"org.jruby.Main", "-S", "irb", "-r", "config-helpers.rb", "$@"
