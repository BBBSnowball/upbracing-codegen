
rundesc = $generator.make_run_description "tests-java-helpers",
	"upbracing-AVR-CodeGenerator"           => "CODE_GENERATOR_DIR",
	"java-parser-tools"                     => "JAVA_PARSER_TOOLS_DIR",
	"upbracing-AVR-TimerConfigurationModel" => "TIMER_CONFIGURATION_MODEL_DIR",
	"XMegaOS"                               => "CARTOS_DIR",
	"Statemachine"                          => "STATEMACHINE_DIR"

vmopts = ["-client"]
args = vmopts + ["org.jruby.Main", "-S", "rake", "$@"]

# only one kind of Unix because we use symlinks for the other ones
# other Unixes: "Mac OS X"
["Linux"].each do |os|
	rundesc.write_script(File.join($path, "rake-#{os}"), rundesc.unix_script($path, *args))
end

["Windows", "Windows 7"].each do |os|
	rundesc.write_script(File.join($path, "rake-#{os}.bat"), rundesc.windows_script($path, *args))
end
