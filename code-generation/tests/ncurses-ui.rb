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

COLOR_DARK_GRAY = 8
COLOR_DARK_YELLOW = 9
COLOR_ORANGE = 10
COLOR_DEFAULT = -1

class NCursesToolkit
	include Toolkit
	include Messages::ContextListener
	include Messages::MessageListener
	include ProgramIOListener

	class IOType
		attr_accessor :source, :stream, :mode

		def initialize(source, stream, mode = :default)
			@source = source
			@stream = stream
			@mode = mode
		end

		def ==(b)
			return false unless self.class == b.class
			[:source, :stream, :mode].each do |key|
				av = self.send(key)
				bv = b.send(key)
				return false unless av == bv
			end
			return true
		end

		def hash
			[self, @source, @stream, @mode].hash
		end
	end

	def initialize		
		# we cannot print to the screen, so we might need a log
		$log = $logger = @log = Logger.new "ncurses-ui.log"
		$log.info "NCursesToolkit::initialize"

		#DEBUG
		class <<$log
			def close
				raise "Please don't close the log"
			end
		end

		@messages = Messages.new
		@messages.addContextListener self
		@messages.addMessageListener self

		@failed_contexts = []

		@io_types = {
			:default              => IOType.new(:default, :out),
			:default_error        => IOType.new(:default, :out, :error),

			:program_stdin        => IOType.new(:program, :out),
			:program_stdout       => IOType.new(:program, :in),
			:program_stderr       => IOType.new(:program, :in_err),
			:program_stdout_skip  => IOType.new(:program, :in,     :skip),
			:program_stderr_skip  => IOType.new(:program, :in_err, :skip),

			:serial_in            => IOType.new(:serial, :in),
			:serial_out           => IOType.new(:serial, :out),
			:serial_in_skip       => IOType.new(:serial, :in, :skip),

			:prompt_marker        => IOType.new(:user,   :out, :prompt_marker),
			:instruction_marker   => IOType.new(:user,   :out, :instruction_marker),
		}
		Severity.values.each do |severity|
			name = severity.name.downcase.intern
			header = "#{name}_header".intern
			@io_types[name] = IOType.new(:messages, :in, name)
			@io_types[header] = IOType.new(:messages, :in, header)
		end
	end

	def color_for(name, io_type)
		fg = nil
		bg = nil
		case io_type.source
		when :default
			bg = COLOR_DEFAULT	# FFI::NCurses::COLOR_BLACK
			if io_type.mode == :error
				fg = FFI::NCurses::COLOR_RED
			else
				fg = FFI::NCurses::COLOR_WHITE
			end
		when :program
			# in ncurses BLACK is gray - weird!
			# And the custom color break foreground color, so
			# we cannot use it :-(
			# TODO fix ^^ I don't think we can rely on such behaviour.
			bg = FFI::NCurses::COLOR_BLACK	#COLOR_DARK_GRAY
			if io_type.stream == :in_err
				fg = FFI::NCurses::COLOR_RED
			else
				fg = FFI::NCurses::COLOR_WHITE
			end
		when :serial
			bg = COLOR_DARK_YELLOW
			if io_type.stream == :in
				fg = COLOR_DEFAULT	#FFI::NCurses::COLOR_BLACK
			else
				fg = FFI::NCurses::COLOR_BLUE
			end
		when :user
			case io_type.mode
			when :prompt_marker
				bg = FFI::NCurses::COLOR_BLUE
				fg = FFI::NCurses::COLOR_BLACK
			when :instruction_marker
				bg = FFI::NCurses::COLOR_WHITE
				fg = FFI::NCurses::COLOR_BLACK
			else
				raise "unsupported type of user I/O: #{io_type.mode}"
			end
		when :messages
			bg = COLOR_DEFAULT	#FFI::NCurses::COLOR_BLACK
			case io_type.mode
			when :fatal_header, :error_header
				fg = FFI::NCurses::COLOR_RED
			when :warning_header
				fg = FFI::NCurses::COLOR_YELLOW 	#COLOR_ORANGE
			else
				fg = FFI::NCurses::COLOR_WHITE
			end
		else
			raise "unsupported source: #{io_type.source}"
		end
		[fg, bg]
	end

	def start
		return if @started
		@started = true

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
		#DEBUG
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


		FFI::NCurses::start_color

		# transparent background
		FFI::NCurses::use_default_colors

		#DEBUG
		if false
			FFI::NCurses::init_pair(1, FFI::NCurses::COLOR_RED, FFI::NCurses::COLOR_BLUE)
			FFI::NCurses::attron(FFI::NCurses::COLOR_PAIR(1))
			FFI::NCurses::printw("blub in red and blue")
			FFI::NCurses::getch
		end

		FFI::NCurses::init_color(COLOR_DARK_GRAY, 200, 200, 200)
		FFI::NCurses::init_color(COLOR_DARK_YELLOW, 300, 300, 0)
		FFI::NCurses::init_color(COLOR_ORANGE, 1000, 0x4a*1000/256, 0x12*1000/256)

		# I want a black that is black - not gray!
		#FFI::NCurses::init_color(FFI::NCurses::COLOR_BLACK, 0, 0, 0)
		#FFI::NCurses::init_color(COLOR_BLACK, 0, 0, 0)

		@colors = {}
		@io_types.each do |key,value|
			@colors[key] = color_for(key, value)
		end

		@unique_colors = @colors.values.uniq
		@color_pairs = {}
		i = 0
		@unique_colors.each do |color|
			i += 1
			@log.info("color pair #{i}: #{color.inspect}")
			FFI::NCurses::init_pair(i, *color)
			@colors.each do |key,value|
				@color_pairs[key] = i if value == color
			end
		end

		# set default to white text on black background
		# (I don't like the gray background that ncurses
		#  sets as default.)
		#FFI::NCurses::bkgd(FFI::NCurses::COLOR_PAIR(@color_pairs[:default]))


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
				if key and key != FFI::NCurses::ERR
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

		@output.printw("\nRake is shutting down\nWaiting for you to press a key\n")
		@output.refresh

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
		prompt += "\n" unless prompt.end_with?("\n") or prompt.empty?
		
		#TODO better implementation, if we can put the terminal into raw mode
		
		prompt += "Press enter to continue\n";
		
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
		iotype = case type
		when PIOType::IN
			:program_stdin
		when PIOType::OUT
			:program_stdout
		when PIOType::ERROR
			:program_stderr
		else
			raise "unknown type of program IO: #{type}"
		end

		#data = data.gsub(/[^ -z]/) { |x| sprintf("\\x%02x", x.ord) }

		print_attr(data, FFI::NCurses::COLOR_PAIR(@color_pairs[iotype]))
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
		color = FFI::NCurses::COLOR_PAIR(@color_pairs["#{msg.severity.name.downcase}_header".intern])
		x = /^([^:]*)(:.*$)/m.match(sb.to_s)
		print_attr x[1], (color | FFI::NCurses::A_BOLD)
		@output.printw(x[2])
		@output.refresh
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

	def print_attr(text, attr)
		@output.attron attr
		@output.printw text
		@output.attroff attr
	end

	def printLeftBar(bar, bar_attr, text)
		nl_at_end = text.end_with? "\n"
		text = text.sub(/\n$/, "")

		text.lines.each do |line|
			#@output.attron(bar_attr)
			#@output.printw(bar)
			#@output.attroff(bar_attr)
			print_attr(bar, bar_attr)
			@output.printw(" ")
			@output.printw(line)
		end

		@output.printw "\n" if nl_at_end

		@output.refresh
	end

	def printPrompt(prompt)
		if prompt and not prompt.empty?
			#prompt.sub(/\n$/, "").lines.each do |line|
			#	c = FFI::NCurses::COLOR_PAIR(@color_pairs[:prompt_marker])
			#	@output.attron(c | FFI::NCurses::A_BOLD)
			#	@output.printw(" ")
			#	@output.attroff(c | FFI::NCurses::A_BOLD)
			#	@output.printw(" ")
			#	@output.printw(line)
			#end
			#@output.printw("\n")
			#@output.refresh

			c = FFI::NCurses::COLOR_PAIR(@color_pairs[:prompt_marker])
			printLeftBar " ", (c | FFI::NCurses::A_BOLD), prompt
		end
	end

	def printInstructions(instructions)
		instructions = instructions.sub(/\n*$/, '\n')
		c = FFI::NCurses::COLOR_PAIR(@color_pairs[:instruction_marker])
		printLeftBar "I", (c | FFI::NCurses::A_BOLD), instructions
	end

	def reportProgramResult(program, result)
		name = program.name

		if result.isSuccessful
			@output.printw "#{name} finished successfully\n"
		else
			@output.printw name + ": "
			c = FFI::NCurses::COLOR_PAIR(@color_pairs[:default_error])
			print_attr result.message + "\n", c
		end
		@output.refresh
	end

	#DEBUG
	def stdin
		x = Object.new
		class << x
			def readLine
				sleep 0.5
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
