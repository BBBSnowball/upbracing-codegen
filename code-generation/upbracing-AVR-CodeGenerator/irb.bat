@echo off

IF "%CODE_GENERATOR_DIR%"=="" SET CODE_GENERATOR_DIR="%~dp0\."
IF "%STATEMACHINE_DIR%"=="" SET STATEMACHINE_DIR="%~dp0\..\StatemachineEditor\Statemachine"
IF "%JAVA_PARSER_TOOLS_DIR%"=="" SET JAVA_PARSER_TOOLS_DIR="%~dp0\..\java-parser-tools"
IF "%TIMER_CONFIGURATION_MODEL_DIR%"=="" SET TIMER_CONFIGURATION_MODEL_DIR="%~dp0\..\upbracing-AVR-TimerConfigurationModel"

java -cp "%CODE_GENERATOR_DIR%\bin;%CODE_GENERATOR_DIR%\libs\jruby-complete-1.7.0.jar;%CODE_GENERATOR_DIR%\libs\simple-xml-2.6.4.jar;%CODE_GENERATOR_DIR%\libs\ruby-gems.jar;%CODE_GENERATOR_DIR%\libs\commons-cli-1.2.jar;%CODE_GENERATOR_DIR%\libs\org.eclipse.emf.common_2.7.0.v20120127-1122.jar;%CODE_GENERATOR_DIR%\libs\org.eclipse.emf.ecore_2.7.0.v20120127-1122.jar;%CODE_GENERATOR_DIR%\libs\org.eclipse.emf.ecore.xmi_2.7.0.v20120127-1122.jar;%CODE_GENERATOR_DIR%\libs\guava-13.0.1.jar;%STATEMACHINE_DIR%\bin;%JAVA_PARSER_TOOLS_DIR%\bin;%TIMER_CONFIGURATION_MODEL_DIR%\dist\de.upbracing.timer.configurationmodel.jar;%EXTRA_CLASSPATH%" org.jruby.Main -S irb -r config-helpers.rb %*
