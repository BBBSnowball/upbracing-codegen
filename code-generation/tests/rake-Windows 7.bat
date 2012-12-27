@echo off

IF "%CODE_GENERATOR_DIR%"=="" SET CODE_GENERATOR_DIR="%~dp0\..\upbracing-AVR-CodeGenerator"
IF "%TIMER_CONFIGURATION_MODEL_DIR%"=="" SET TIMER_CONFIGURATION_MODEL_DIR="%~dp0\..\upbracing-AVR-TimerConfigurationModel"
IF "%JAVA_PARSER_TOOLS_DIR%"=="" SET JAVA_PARSER_TOOLS_DIR="%~dp0\..\java-parser-tools"
IF "%STATEMACHINE_DIR%"=="" SET STATEMACHINE_DIR="%~dp0\..\StatemachineEditor\Statemachine"

java -cp "%~dp0\..\tests-java-helpers\bin;%CODE_GENERATOR_DIR%\libs\jruby-complete-1.7.0.jar;%CODE_GENERATOR_DIR%\libs\org.eclipse.emf.common_2.7.0.v20120127-1122.jar;%CODE_GENERATOR_DIR%\libs\org.eclipse.emf.ecore_2.7.0.v20120127-1122.jar;%CODE_GENERATOR_DIR%\libs\org.eclipse.emf.ecore.xmi_2.7.0.v20120127-1122.jar;%CODE_GENERATOR_DIR%\libs\ruby-gems.jar;%CODE_GENERATOR_DIR%\libs\simple-xml-2.6.4.jar;%TIMER_CONFIGURATION_MODEL_DIR%\dist\de.upbracing.timer.configurationmodel.jar;%~dp0\..\tests-java-helpers\libs\nrjavaserial-3.8.4.jar;%JAVA_PARSER_TOOLS_DIR%\bin;%CODE_GENERATOR_DIR%\bin;%CODE_GENERATOR_DIR%\libs\commons-cli-1.2.jar;%CODE_GENERATOR_DIR%\libs\guava-13.0.1.jar;%STATEMACHINE_DIR%\bin;%EXTRA_CLASSPATH%" -client org.jruby.Main -S rake %*
