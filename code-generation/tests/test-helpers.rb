
def erase_processor
	system "#{AVRDUDE} -e"
end

def flash_processor_action(build_dir, name, parts = [:flash])
	parts = [parts] if parts.is_a? String or parts.is_a? Symbol
	return { :dependencies => [], :action => lambda {}, :cmdline => "" } if parts.empty?

	dependencies = []
	commands = ""
	parts.each do |part|
		part = part.intern if part.is_a? String
		type = part.to_s
		case part
		when :flash, :application
			ext = "hex"
		when :eeprom
			ext = "eep"
		when :efuse, :lfuse, :hfuse, /^fuse[0-9]+$/, :calibration
			ext = part.to_s
		else
			raise "Unsupported type of memory: #{part}"
		end

		file = File.expand_path(File.join(build_dir, "#{name}.#{ext}"))
		dependencies << file
		commands += " '-U#{type}:w:#{flash_file}:a'"
	end

	cmdline = AVRDUDE+commands

	{ :dependencies => dependencies, :action => lambda { system cmdline }, :cmdline => cmdline }
end

def flash_processor(*args)
	system flash_processor_action(*args)[:cmdline]
end
