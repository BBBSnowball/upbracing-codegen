package de.upbracing.code_generation;

import java.io.IOException;
import java.util.Collection;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import de.upbracing.code_generation.CodeGenerationMain.Arguments;
import de.upbracing.code_generation.config.CodeGeneratorConfigurations;

import static de.upbracing.code_generation.CodeGenerationMain.*;

public class Main {
	/** Main entry point of the application
	 * @param program arguments
	 * @throws ScriptException if the configuration script contains errors or raises an exception
	 * @throws IOException if there is an error reading the config file
	 * @throws ParseException if there are any invalid arguments
	 */
	public static void main(String[] args) throws ScriptException, IOException {
		// define options which will be parsed by Apache commons-cli
		Options opts = new Options();
		opts.addOption("w", "which-files", false, "Print the names of all files that would be generated. Don't create any files.");
		opts.addOption("d", "dependencies", false, "Print the names of all files that could influence the code generation. Don't create any files.");
		opts.addOption("D", "all-dependencies", false, "like -d, but includes the class path of the generator");
		opts.addOption("h", "help", false, "Print this help");
		opts.addOption("C", "directory", true, "Generate files in directory");
		opts.addOption("T", "temp-directory", true, "Put intermediate files in this directory");
		opts.addOption("c", "check-config", false, "Only load the configuration file");
		
		// parse the program arguments
		Arguments config = new Arguments();
		CommandLine cmd;
		try {
			cmd = new PosixParser().parse(opts, args);
		} catch (ParseException e) {
			System.err.println("error parsing options: " + e.getMessage());
			System.exit(-1);
			return;
		}

		// print help message, if appropiate
		if (cmd.hasOption("h")) {
			new HelpFormatter().printHelp("code_generator [options] config_file", opts, false);
			return;
		}
		
		// all files will go into the target directory, which is the
		// current directory, unless it is set with a cli argument
		config.setTargetDirectory(cmd.getOptionValue('C', "."));
		
		config.setTempDirectory(cmd.getOptionValue('T', config.getTargetDirectory()));
		
		boolean only_check_config = cmd.hasOption('c');
		
		// configuration file should be the only left-over argument
		args = cmd.getArgs();
		if (args.length == 1)
			config.setConfigFile(args[0]);
		else if (args.length > 1) {
			System.err.println("Too many left-over arguments: " + args[0] + " " + args[1]
					+ (args.length > 2 ? " ..." : ""));
			System.exit(-1);
			return;
		}
		
		// only print a list of file to generate, if the -w option is given
		// This can be used to specify dependencies in a Makefile.
		if (cmd.hasOption("w")) {
			printFileListForMakefile(getGeneratedFiles(config));
			return;
		}
		
		// make sure that we have a config file
		if (!config.hasConfigFile()) {
			System.err.println("Please give me a configuration file!");
			System.exit(-1);
			return;
		}
		
		// only print dependencies, if the -d option is given
		// This can be used to specify dependencies in a Makefile.
		if (cmd.hasOption("d") || cmd.hasOption("D")) {
			boolean complete = cmd.hasOption("D");
			
			printFileListForMakefile(getDependencies(config, complete));
			
			return;
		}
		
		// load config file
		CodeGeneratorConfigurations mcu_config = loadConfig(config);
		
		if (only_check_config) {
			return;
		}
		
		// generate the files
		if (!runGenerators(mcu_config, config.getTargetDirectory()))
			System.exit(1);
	}

	public static void printFileListForMakefile(Collection<String> fileList) {
		for (String file : fileList)
			// we cannot escape dollar signs (at least not for Mac), so we just ignore those files
			// In most cases, those are anonymous classes and the parent class will trigger the
			// regeneration anyway.
			if (!file.contains("$"))
				System.out.println(escapeForMakefile(file));
	}

	/** escape a string (e.g. a file name) for use in a Makefile
	 * 
	 * @param str String to escape
	 * @return the string with appropiate escaping
	 */
	private static String escapeForMakefile(String str) {
		//TODO Escaping spaces is very buggy in make and for colons it doesn't
		//      work at all. Should we detect those cases and print a warning
		//      or even terminate the program with an error?
		//      http://www.mail-archive.com/bug-make@gnu.org/msg03318.html
		//      http://www.cmcrossroads.com/ask-mr-make/7859-gnu-make-meets-file-names-with-spaces-in-them
		//      http://www.cmcrossroads.com/ask-mr-make/8442-gnu-make-escaping-a-walk-on-the-wild-side
		//NOTE Using '$$' for '$' doesn't seem to work.
		return str.replaceAll("([:#\\\\?*%~\\[\\]$])", "\\\\$1");
	}
}
