
$toolkit.showInstructions <<EOF
Test 0
Each test has a number. Please make sure
that no number is left out, as this
indicates a function that is ignored
without notice.
EOF

$toolkit.showInstructions <<EOF
Test 1
This text should be presented as
test instructions. They must be
visible until you dismiss them.
If possible, you should be able
to look them up later.

- Do you see the instructions?
  (obviously you do *g*)
- Is there a way to dismiss?
- Does the program stop until
  you have dismissed them?
- Does it continue after that?
- Can you reread them after
  dismissing them?
  (In simple UI only by
   scrolling up, sorry)
EOF

$toolkit.wait_for_user <<EOF
Test 2
You should see this text and
the program must wait for you.

- Is the program stopped?
- Do you know that it is
  waiting for you?
- Do you know how to continue?
- Does it continue, if you
  do that?
EOF

answer = $toolkit.ask <<EOF, nil
Test 3
- Do you understand that the
  program expects some input?
- Please enter the number 42
- Does the program continue?
EOF
$toolkit.showInstructions "I got #{answer.inspect} -> #{answer == "42" ? "ok" : "wrong"}"

#NOTE Don't do it like that! The
#     RichToolkit provides a wrapper
#     that we don't use here.
validator = Java::de::upbracing::code_generation::tests::RegexValidator.new "^a+z$"
answer = $toolkit.ask <<EOF, validator
Test 4
Unless you are using the simple UI:
(input without quotation marks,
 do not click 'ok' or press enter)
- "b" should marked wrong
- "a" should be marked incomplete
- "az" should be marked valid
- "azz" should be marked wrong

For the simple UI:
(without quotation marks)
- Write "aa" and press enter. The
  input should not be accepted and
  the prompt should NOT change.

For all UI:
(without quotation marks)
- Enter a wrong value: "azz"
  It should not be accepted. The
  program should ask again.
- Enter a correct value: "aaaz"
EOF
$toolkit.showInstructions "I got #{answer.inspect} -> #{answer == "aaaz" ? "ok" : "wrong"}"

answer = $toolkit.askOptions <<EOF, nil, "red", "green", "yellow"
Test 5
- Do you understand that the program
  expects some input?
- Do you understand that you should
  choose one of three colors?
- Does the program ask again, if you
  enter: "blue"
- Does it accept the input and
  continue, if you enter: "green"
EOF
$toolkit.showInstructions "I got #{answer.inspect} -> #{answer == "green" ? "ok" : "wrong"}"

OptionShaper = Java::de::upbracing::code_generation::tests::OptionShaper
class DummyShaper
	include OptionShaper
	def process(input)
		if input.strip == "r" and not input.end_with? "\n"
			return "red"
		else
			return input
		end
	end
end
answer = $toolkit.askOptions <<EOF, DummyShaper.new, "red", "green", "yellow"
Test 6
- If you can enter an arbitrary
  text, please enter: "r"
  (should be accepted)
- If you cannot enter text, please
  choose "red".
EOF
$toolkit.showInstructions "I got #{answer.inspect} -> #{answer == "red" ? "ok" : "wrong"}"

$toolkit.showInstructions <<EOF
Test 7
I will run some programs now. You should
see this output:
(The exit status may be reported in a
 different way. The output may include
 a report for each started process.)

abc  def g
-> exit code 0 (success)
#{Dir.pwd}
-> exit code 0 (success)
#{File.dirname(Dir.pwd)}
-> exit code 0 (success)
abc'"$USER%PATH%&:?*	<def>g abc'"$USER%PATH%&:?*	<def>g
-> exit code 0 (success)
42
-> exit code 0 (success)
<some error message by cat or type>
-> exit code not 0 (success)
EOF
# a dummy string to make my editor happy
# passed to a dummy function to make lint happy
def ignore(x)
end
ignore <<EOF
"
EOF

def read_and_close(stream)
	while stream.available > 0
		break if stream.read < 0
	end
	stream.close
end
def cleanup_process(process_context)
	process_context.process.output_stream.close
	exit_code = process_context.process.wait_for
	read_and_close process_context.process.input_stream
	read_and_close process_context.process.error_stream
	return exit_code
end

BufferedReader = Java::java::io::BufferedReader
InputStreamReader = Java::java::io::InputStreamReader

process_context = $toolkit.exec_program "echo1", ["echo", "abc  def", "g"], nil, nil
stream = BufferedReader.new(InputStreamReader.new(process_context.process.input_stream))
line = stream.readLine()
expected = "abc  def g"
exit_code = cleanup_process(process_context)
puts "expected clean exit (code 0), but got #{exit_code}" if exit_code != 0
puts "wrong line for test 7a: expected = #{expected.inspect}, actual = #{line.inspect}" if line != expected

#NOTE actually RichToolkit has to do some magic - otherwise we end up in the "tests"
#     directory instead of "tests/special/testUI"
process_context = $toolkit.exec_program "pwd1", ["pwd"], nil, nil
stream = BufferedReader.new(InputStreamReader.new(process_context.process.input_stream))
line = stream.readLine()
expected = Dir.pwd
exit_code = cleanup_process(process_context)
puts "expected clean exit (code 0), but got #{exit_code}" if exit_code != 0
puts "wrong line for test 7b: expected = #{expected.inspect}, actual = #{line.inspect}" if line != expected

process_context = $toolkit.exec_program "pwd2", ["pwd"], nil, Java::java::io::File.new("..")
stream = BufferedReader.new(InputStreamReader.new(process_context.process.input_stream))
line = stream.readLine()
expected = File.dirname(Dir.pwd)
exit_code = cleanup_process(process_context)
puts "expected clean exit (code 0), but got #{exit_code}" if exit_code != 0
puts "wrong line for test 7c: expected = #{expected.inspect}, actual = #{line.inspect}" if line != expected

process_context = $toolkit.exec_program "echo2", ["echo", "abc'\"$USER%PATH%&:?*\t<def>g", "abc'\"$USER%PATH%&:?*\t<def>g"], nil, nil
stream = BufferedReader.new(InputStreamReader.new(process_context.process.input_stream))
line = stream.readLine()
expected = "abc'\"$USER%PATH%&:?*\t<def>g abc'\"$USER%PATH%&:?*\t<def>g"
exit_code = cleanup_process(process_context)
puts "expected clean exit (code 0), but got #{exit_code}" if exit_code != 0
puts "wrong line for test 7d: expected = #{expected.inspect}, actual = #{line.inspect}" if line != expected

def is_windows
	RUBY_PLATFORM =~ /win32/i or (RUBY_PLATFORM == "java" and Java::java::lang::System.getProperty("os.name") =~ /Windows/)
end
process_env = ["BLUB=42", "PATH=#{ENV['PATH']}"]
def env_ref(name)
	if is_windows
		"%#{name}%"
	else
		"$#{name}"
	end
end
def print_envvar(name)
	if is_windows
		["cmd", "/c", "%#{name}%"]
	else
		["sh", "-c", "echo $#{name}"]
	end
end
process_context = $toolkit.exec_program "echo-env", print_envvar("BLUB"), process_env, nil
stream = BufferedReader.new(InputStreamReader.new(process_context.process.input_stream))
line = stream.readLine()
expected = "42"
exit_code = cleanup_process(process_context)
puts "expected clean exit (code 0), but got #{exit_code}" if exit_code != 0
puts "wrong line for test 7e: expected = #{expected.inspect}, actual = #{line.inspect}" if line != expected

not_existing_file = "file_that_does_not_exist"
while File.exists? not_existing_file
	$toolkit.showInstructions "Very funny! Please remove #{not_existing_file}!"
end
process_context = $toolkit.exec_program "file-not-found", [is_windows ? "type" : "cat", not_existing_file], nil, nil
stream = BufferedReader.new(InputStreamReader.new(process_context.process.error_stream))
line = stream.readLine()
exit_code = cleanup_process(process_context)
if exit_code == 0
	puts "expected UNCLEAN exit (file not found), but got #{exit_code}"
else
	puts "exit code is #{exit_code}, which is fine"
	puts "Don't worry about the error message by 'cat' (or 'type')."
end
puts "WARN: Error message doesn't contain the file name: #{line}" unless line.include? not_existing_file

msgs = $toolkit.messages

msgs.trace "A"
msgs.debug "B"
msgs.info  "C"
msgs.warn  "D"
msgs.error "E"
msgs.fatal "F"

$toolkit.showInstructions <<EOF
Test 8
You should see six messages with text
A, B, ..., F and severity trace, debug,
info, warn, error and fatal.
EOF

$toolkit.showInstructions <<EOF
Test 9
The tests end here. The UI should print
a summary of failed tests. This summary
should include exactly those tests
mentioned here:
- file-not-found program
EOF

$toolkit.all_tests_finished

#NOTE Enter new tests before this test and update the number accordingly.

#TODO
# - use messages -> a context for each test case
#   (ask user for result and report it to the UI)
