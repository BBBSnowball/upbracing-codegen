require 'ffi-ncurses'
require 'logger'

#require 'live_console'

# all Proc objects should be Runnable
class Proc
	include java.lang.Runnable

	# Ruby calls it call, in Java it is run
	alias_method :run, :call
end

def newThread(&block)
	Java::java::lang::Thread.new block
end

Toolkit = Java::de::upbracing::code_generation::tests::Toolkit
Runtime = Java::java::lang::Runtime
ExternalProgramContext = Java::de::upbracing::code_generation::tests::context::ExternalProgramContext

class NCursesWindow
	def initialize(x, y, width, height)
		@win = FFI::NCurses::newwin height, width, y, x
	end

	def dispose
		border(' ', ' ', ' ',' ',' ',' ',' ',' ')
	end

	def method_missing(m, *args, &block)
		m = "w#{m}".intern unless [:delwin, :box, :scrollok].include? m
		FFI::NCurses::send(m, @win, *args, &block)
	end
end

class NCursesFramedWindow
	def initialize(x, y, width, height)
		@outer = NCursesWindow.new(x, y, width, height)
		@outer.box(0, 0)
		@outer.refresh
		@inner = NCursesWindow.new(x+1, y+1, width-2, height-2)

		#TODO make sure that they move together
	end

	def method_missing(m, *args, &block)
		@inner::send(m, *args, &block)
	end
end

#Messages = Java::de::upbracing::code_generation::Messages
OptionValidator = Java::de::upbracing::code_generation::tests::OptionValidator
Validator = Java::de::upbracing::code_generation::tests::Validator
TestContext = Java::de::upbracing::code_generation::tests::context::TestContext
PropertyChangeListener = Java::java::beans::PropertyChangeListener
ProgramIO = Java::de::upbracing::code_generation::tests::context::ProgramIO
ProgramIOListener = ProgramIO::ProgramIOListener
#PIOType = ProgramIO::Type
#StringBuffer = Java::java::lang::StringBuffer
#Severity = Messages::Severity
ErrorOrFailure = Java::de::upbracing::code_generation::tests::context::Result::ErrorOrFailure

class NCursesToolkit
	include Toolkit
	include Messages::ContextListener
	include Messages::MessageListener
	include ProgramIOListener

	def initialize
		@messages = Messages.new
		@messages.addContextListener self

		@failed_contexts = []
	end

	def start
		return if @started
		@started = true

		# we cannot print to the screen, so we might need a log
		$log = $logger = @log = Logger.new "ncurses-ui.log"

		# start console on network port because standard input will be used by ncurses
		#lc = LiveConsole.new :socket, :port => 3333, :bind => binding
		#lc.start

		#$toolkit = self
		#Thread.new do
		#	#Java::org::jruby::demo::IRBConsole.main([])
		#	Java::de::upbracing::code_generation::tests::ncurses::IRBConsole.start [], "$toolkit" => self
		#end

		@screen = FFI::NCurses::initscr

		@finished = false

		begin
			# we have to make sure that we tear down ncurses
			Runtime::runtime.add_shutdown_hook(newThread do
				self.tear_down
			end)

			# some settings

			# we want function keys as single char (wgetch !)
			FFI::NCurses::keypad(@screen, true)

			FFI::NCurses::nonl
			#FFI::NCurses::raw
			FFI::NCurses::cbreak
			FFI::NCurses::noecho

			_setup_screen
		rescue
			@finished = true

			FFI::NCurses::echo
			FFI::NCurses::nocbreak
			FFI::NCurses::nl
			FFI::NCurses::endwin

			raise
		end
	end

	def _setup_screen
		if false
			FFI::NCurses::printw("Hello World !!!")
			FFI::NCurses::refresh
			#FFI::NCurses::getch

			win = NCursesFramedWindow.new(4, 4, 10, 10)
			win.printw "blub\n"
			win.printw "We have " + FFI::NCurses.LINES.to_s + " lines and " + FFI::NCurses.COLS.to_s + " columns.\n"
			win.refresh
			FFI::NCurses::getch
		end

		lines = FFI::NCurses.LINES
		cols  = FFI::NCurses.COLS

		tree_width = cols / 3

		FFI::NCurses::refresh

		@tree = NCursesFramedWindow.new 1, 1, tree_width - 2, lines - 2
		@output = NCursesFramedWindow.new 1+tree_width, 1, cols - tree_width - 2, lines - 2

		@tree.scrollok true
		@output.scrollok true

		#TODO set up screen

		@thread = Thread.new do
			FFI::NCurses::halfdelay 5

			while !@finished
				key = FFI::NCurses::getch
				if key
					#TODO
					@output.printw "Got key: #{key}\n"
					@output.refresh unless @finished
				end
			end
		end
	end

	def tear_down
		#STDIN.readline
		#FFI::NCurses::getch
		#VER::stop_ncurses

		#@log.warn "tear_down called"

		# only do it once
		return if @finished

		@finished = true

		@thread.join 2 if @thread

		# This time we want to wait -> no halfdelay
		FFI::NCurses::nocbreak
		FFI::NCurses::cbreak
		FFI::NCurses::getch
		
		FFI::NCurses::echo
		FFI::NCurses::nocbreak
		FFI::NCurses::nl
		FFI::NCurses::endwin
	end

	def log
		@log
	end

	#DEBUG
	#def method_missing(m, *args, &block)
	#	@log.warn("Call to missing method #{m}")
	#end

	def getMessages
		@messages
	end

	def ask(prompt, validator)
		if prompt
			if prompt.end_with? ":"
				prompt = prompt + " "
			else
				prompt = prompt + "\n>  "
			end
		else
			prompt = ">  "
		end
		
		prev = nil
		while true
			unless prev
				printPrompt(prompt);
			else
				printPrompt(">> ");
			end
			answer = stdin.readLine();	#TODO
			if (answer.end_with?("\n"))
				answer = answer.substring(0, answer.length()-1)
			end
			
			return answer unless validator
			
			if prev
				both = prev + answer;
				result = validator.validate(both)
				if (result == Validator::Result::VALID || result == Validator::Result::UNKNOWN)
					return both;
				elsif (answer.isEmpty())
					# abort accumulating
					prev = null;
					continue;
				elsif (result == Validator::Result::INCOMPLETE)
					# do we need another line?
					both += "\n";
					result = validator.validate(both);
					if (result != Validator::Result::INVALID)
						prev = both;
						continue;
					else
						# another line wouldn't help
						prev = null;
					end
				end
			end

			result = validator.validate(answer);
			if (result == Validator::Result::VALID || result == Validator::Result::UNKNOWN)
				return answer;
			elsif (result == Validator::Result::INCOMPLETE)
				# do we need another line?
				result = validator.validate(answer + "\n");
				if (result != Validator::Result::INVALID)
					prev = answer + "\n";
				end
			end
		end
	end

	def askOptions(prompt, shaper, options)
		printPrompt(prompt + "\n") if prompt

		options_str = "options: " + options.to_a.join(", ")
		
		answer = ask(options_str, OptionValidator.new(shaper, options));
		
		
		answer = shaper.process(answer) if shaper

		answer
	end

	def waitForUser(prompt)
		#TODO
		prompt ||= ""
		prompt += "\n" unless prompt.end_with?("\n")
		
		#TODO better implementation, if we can put the terminal into raw mode
		
		prompt += "Press enter to continue";
		
		printPrompt(prompt);
		
		#TODO
		stdin.readLine();
	end

	def showInstructions(instructions)
		printInstructions(instructions);
		waitForUser(nil);
	end

	def execProgram(name, commandline, environment, dir)
		context = ExternalProgramContext.new(name, @messages, commandline, environment, dir)
		
		context.run
		
		printToConsole(context.getProgramIO())
		
		monitorStatus(context);

		context
	end

	def allTestsFinished
		@failed_contexts.each do |ctx|
			sb = Java::java::lang::StringBuffer.new

			test = ctx.topmost_item
			result = test.result
			sb.append(result.status)
			sb.append(": ")
			sb.append(result.message)

			ctx.toLongString("  ", sb)

			sb.append("\n")

			@output.printw(sb.toString())
			@output.refresh
		end
	end



	def contextPushed(context_item)
		#TODO indent
		@tree.printw context_item.to_s + "\n"
		@tree.refresh
	end
	
	def contextPopped(context_item)
		if (context_item.is_a? TestContext)
			# remember failed tests
			unless context_item.getResult().isSuccessful()
				@failed_contexts << @messages.getContext()
			end
		end
	end


	def printToConsole(programIO)
		programIO.addProgramIOListener self
	end

	def programIO(data, type)
		foreground = case type
		when PIOType::IN
			[:green]
		when PIOType::OUT
			[:white, :bright]
		when PIOType::ERROR
			[:red, :bright, :bold]
		else
			raise "unknown type of program IO: #{type}"
		end

		#data = data.gsub(/[^ -z]/) { |x| sprintf("\\x%02x", x.ord) }

		@output.printw alter_lines2(data) { |line| Paint[line, *foreground, [50,50,50]] }
		@output.refresh
	end

	class MonitorProcessStatus
		include PropertyChangeListener

		def initialize(toolkit, context)
			@toolkit = toolkit
			@context = context
		end

		def propertyChange(ev)
			result = @context.result
			@toolkit.reportProgramResult(@context, result) \
				if result.is_a?(ErrorOrFailure) or result.isSuccessful
				
		end
	end

	def monitorStatus(context)
		context.addPropertyChangeListener("result", MonitorProcessStatus.new(self, context))
	end
	


	def message(msg)
		sb = StringBuffer.new
		msg.format(sb, "  ")
		color = case msg.severity
		when Severity::FATAL, Severity::ERROR
			[:red, :bright, :bold]
		when Severity::WARNING
			Paint.mode == 256 and ["#ff4a12", :bold] or [:yellow, :bold]
		else
			[:bold]	#nil
		end
		if color and Paint.mode > 0
			print sb.toString.sub(/^(.*):/, Paint["\\1", *color] + ":")
		else
			print sb.toString
		end
	end


	def alter_lines(text)
		text.lines.map { |line| yield line }.join
	end

	def alter_lines2(text)
		alter_lines(text) do |line|
			if line.end_with? "\n"
				line = line.sub(/\n$/, "")
				(yield line, true) + "\n"
			else
				yield line, false
			end
		end
	end

	def left_bar(bar, text)
		alter_lines(text) { |line| bar + line }
	end

	def printPrompt(prompt)
		if prompt and not prompt.empty?
			prompt = left_bar(Paint[" ", nil, :blue] + " ", prompt)
			@output.printw prompt
			@output.refresh
		end
	end

	def printInstructions(instructions)
		instructions = instructions.sub(/\n*$/, '')
		@output.printw left_bar(Paint["I", :black, :white] + " ", instructions) + "\n"
		@output.refresh
	end

	def printProgramIO(data, type)
		foreground = case type
		when PIOType::IN
			[:green]
		when PIOType::OUT
			[:white, :bright]
		when PIOType::ERROR
			[:red, :bright, :bold]
		else
			raise "unknown type of program IO: #{type}"
		end

		#data = data.gsub(/[^ -z]/) { |x| sprintf("\\x%02x", x.ord) }

		@output.printw alter_lines2(data) { |line| Paint[line, *foreground, [50,50,50]] }
		@output.refresh
	end

	def reportProgramResult(program, result)
		name = program.name
		#name = Paint[program.name, nil, [50,50,50]]

		if result.isSuccessful
			@output.printw "#{name} finished successfully\n"
		else
			@output.printw name + ": " + Paint[result.message, :red] + "\n"
		end
		@output.refresh
	end

	#DEBUG
	def stdin
		x = Object.new
		class << x
			def readLine
				case rand(3)
				when 0
					return "red"
				when 1
					return "az"
				when 2
					return "42"
				end
			end
		end
		x
	end
end
