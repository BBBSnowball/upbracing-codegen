rem Meaning of %~dp0 -> see http://weblogs.asp.net/whaggard/archive/2005/01/28/get-directory-path-of-an-executing-batch-file.aspx
java -cp "%~dp0\bin;%~dp0\libs/jruby-complete-1.6.7.2.jar;%~dp0\libs/simple-xml-2.6.4.jar;%~dp0\libs/ruby-gems.jar;%~dp0\libs/commons-cli-1.2.jar" de.upbracing.code_generation.Main %*