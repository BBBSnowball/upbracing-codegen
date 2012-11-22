IF "%CODE_GENERATOR_DIR%" == "" set CODE_GENERATOR_DIR=..\upbracing-AVR-CodeGenerator
IF "%JAVA_HELPERS%" == "" set JAVA_HELPERS=..\tests-java-helpers\bin
IF "%NRSERIAL_JAR%" == "" set NRSERIAL_JAR=..\tests-java-helpers\libs\nrjavaserial-3.8.4.jar
IF "%JAVA_PARSER_TOOLS_BIN%" == "" set JAVA_PARSER_TOOLS_BIN=..\\java-parser-tools\\bin
IF "%TIMER_CONFIG_JAR%" == "" set TIMER_CONFIG_JAR=..\\upbracing-AVR-TimerConfigurationModel\\dist\\de.upbracing.timer.configurationmodel.jar

java -cp "%CODE_GENERATOR_DIR%\libs\jruby-complete-1.7.0.jar;%CODE_GENERATOR_DIR%\libs\ruby-gems.jar;%CODE_GENERATOR_DIR%\libs\org.eclipse.emf.common_2.7.0.v20120127-1122.jar;%CODE_GENERATOR_DIR%\libs\org.eclipse.emf.ecore_2.7.0.v20120127-1122.jar;%CODE_GENERATOR_DIR%\libs\org.eclipse.emf.ecore.xmi_2.7.0.v20120127-1122.jar;%CODE_GENERATOR_DIR%\libs\simple-xml-2.6.4.jar;%CODE_GENERATOR_DIR%\libs\guava-13.0.1.jar;%JAVA_HELPERS%;%JAVA_PARSER_TOOLS_BIN%;%TIMER_CONFIG_JAR%;%CODE_GENERATOR_DIR%/bin;%NRSERIAL_JAR%" org.jruby.Main -S rake %*
