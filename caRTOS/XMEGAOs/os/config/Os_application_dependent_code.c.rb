#!/usr/bin/ruby

# generated file is in same directory as this script
dest = File.expand_path(File.join(File.dirname(__FILE__), "Os_application_dependent_code.c"))

# we start the file with some explanatory comments
text = <<EOF
/*
 * Os_application_dependent_code.c
 *
 * generated by script
 */

// This file contains code that should be part of the OS, but depends on the
// application configuration (e.g. count of TCBs). It must be compiled for
// each application.

// This file is generated by the shell script Os_application_dependent_code.c.sh
// or the Ruby script Os_application_dependent_code.c.rb
// DO NOT MODIFY IT !!!

// You can mark application specific code like this:


// will be compiled in Os_application_dependent_code.c:
#ifdef APPLICATION_DEPENDENT_CODE
// your includes (including the directory name!)
// your code
#endif	// end of APPLICATION_DEPENDENT_CODE


#include "config/Os_cfg_application.h"

EOF

# get name of parent of parent directory of this script
project_dir_name = File.basename(File.expand_path(File.dirname(File.dirname(__FILE__))))

# regular expressions that we will use
#NOTE: In contrast to 'sed' the pattern '\s' might match a newline, but
#      this doesn't matter as we always test one line. The lines normally
#      end with a '\n' (that may be matched by our pattern), but the last
#      line won't.
starts_app_section = /^#\s*ifdef\s+APPLICATION_DEPENDENT_CODE($|\s)/
ends_app_section   = /^#\s*(else|endif)\s*\/\/\s*end of APPLICATION_DEPENDENT_CODE($|\s)/

# find all C files in the parent directory (recursively)
# The shell script keeps the first path component (for technical reasons)
# and we want to output the same text, so we chdir and search in the same way.
error = false
Dir.chdir(File.join(File.dirname(__FILE__), "..", ".."))
Dir.glob(File.join(project_dir_name, "**", "*.c")).each do |file|
    in_app_section = false
    nesting_level = 0
    line_no = 0
    line_count = nil

    File.new(file).lines.each do |line|
        line_no += 1

        if !in_app_section
            # does this line start an application section?
            if line =~ starts_app_section
                in_app_section = true

                if nesting_level > 0
                    STDERR.puts "ERROR: start of application section is nested in an #if\n\tThe code will " +
                        "be compiled regardless of any of those conditions. Please put any #if or #ifdef " +
                        "into the code block.\n\tin file #{file}, line #{line_no}"
                    error = true
                end

                line_count = 0

                puts "application code section in #{file} at line #{line_no}"

                text += "\n\n\/\/ from file #{file}:\n\n"

            elsif line =~ ends_app_section
                STDERR.puts "ERROR: End of application code section without matching begin, in file #{file}, line #{line_no}"
                error = true
            end
        else
            if line =~ starts_app_section
                STDERR.puts "ERROR: End of application code section without matching begin, in file #{file}, line #{line_no}"
                error = true
            
            elsif line =~ ends_app_section
                in_app_section = false

                puts "  with #{line_count} lines"

                if nesting_level != 1
                    STDERR.puts "ERROR: Application code has an unmatched #if or #endif, in file #{file}, line #{line_no}"
                    error = true
                end
            
            else
                # normal line in application code section -> copy it
                text += line

                line_count += 1
            end
        end

        # keep track of nesting
        #NOTE #else and #elif don't change the nesting level
        if line =~ /^#\s*if/
            nesting_level += 1
        elsif line =~ /^#\s*endif/
            nesting_level -= 1
        end
    end

    if in_app_section
        STDERR.puts "ERROR: Couldn't find end of application code section, in file #{file}"
        error = true
    elsif nesting_level != 0
        STDERR.puts "WARN:  Unmatched #if or #endif in file #{file} (not in application code, so I ignore that)"
    end
end

if error
    STDERR.puts "Exit with non-zero status due to previous errors (see above)"
    STDERR.puts "Os_application_dependent_code.c hasn't been updated!"
    exit 1
end

# We update the file, unless it already has the right
# contents. In that case, we won't touch it, so we
# don't trigger unnecessary updates for other files.
unless File.exists?(dest) and File.read(dest) == text
    File.open(dest, "w") do |f|
        f.write text
    end
end