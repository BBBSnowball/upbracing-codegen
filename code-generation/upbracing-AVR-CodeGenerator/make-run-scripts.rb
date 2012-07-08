require 'rubygems'
require 'xmlsimple'

filename = File.dirname(__FILE__) + "/.classpath"
data = XmlSimple.xml_in(filename)
classpath = []
ecus = data["classpathentry"].each do |entry|
  case entry["kind"]
  when "con"
    #ignore
  when "src"
    classpath << entry["output"] if entry["output"]
  when "output"
    classpath.insert(0, entry["path"])
  when "lib"
    classpath << entry["path"]
  end
end

unix_classpath = classpath.map {|x| '$DIR/' + x}.join ":"
win_classpath = classpath.map {|x| '%~dp0\\' + x}.join ";"

File.open("run", "w") do |f|
  f.chmod(0755)
  f.write(%{
#!/bin/sh

DIR="$(dirname "$0")"
java -cp "} + unix_classpath + %{" de.upbracing.code_generation.Main "$@"
})
end 

File.open("run.bat", "w") do |f|
  f.write(%{rem Meaning of %~dp0 -> see http://weblogs.asp.net/whaggard/archive/2005/01/28/get-directory-path-of-an-executing-batch-file.aspx
java -cp "} + win_classpath + %{" de.upbracing.code_generation.Main %*})
end
