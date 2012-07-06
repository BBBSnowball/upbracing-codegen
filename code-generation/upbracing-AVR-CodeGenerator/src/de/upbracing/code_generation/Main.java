package de.upbracing.code_generation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map.Entry;
import java.util.ServiceLoader;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import de.upbracing.code_generation.config.MCUConfiguration;

public class Main {
	/**
	 * Load IGenerator instances via the Service Provider Interface
	 * 
	 * This will find all class that are mentioned in an SPI file somewhere on the
	 * class path. The file is called:
	 * META-INF/services/de.upbracing.code_generation.IGenerator
	 * @return a list of generator instances
	 */
	private static Iterable<IGenerator> findGenerators() {
		ServiceLoader<IGenerator> loader = ServiceLoader.load(IGenerator.class);
		return loader;
	}
	
	/**
	 * load a JRuby configuration file
	 * 
	 * @param stream InputStream for the configuration file. It should be encoded with utf-8.
	 * @return the configuration object
	 * @throws ScriptException if the config script contains errors or raises an exception
	 */
	private static MCUConfiguration loadConfig(InputStream stream) throws ScriptException {
		// use JSR 223 API to invoke JRuby
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("jruby");
		if (engine == null)
			throw new RuntimeException("Couldn't find the JRuby engine!");
		
		//TODO set class path for JRuby, so the config.rb script can 'require' other files
		// System.setProperty("org.jruby.embed.class.path", ...);

		// create a configuration object and put it into the script engine
		MCUConfiguration config = new MCUConfiguration();
		engine.put("config", config);
		
		// execute the script
		Reader script_reader = new InputStreamReader(stream, Charset.forName("utf-8"));
		engine.eval(script_reader);
		try {
			script_reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// return the configuration object
		config = (MCUConfiguration) engine.get("config");
		return config;
	}

	/**
	 * load a JRuby configuration file
	 * 
	 * @param file File name of the configuration file. It should be encoded with utf-8.
	 * @return the configuration object
	 * @throws ScriptException if the config script contains errors or raises an exception
	 * @throws FileNotFoundException if the config file cannot be opened
	 */
	private static MCUConfiguration loadConfig(String file) throws FileNotFoundException, ScriptException {
		return loadConfig(new FileInputStream(file));
	}

	/** Main entry point of the application
	 * @param program arguments
	 * @throws ScriptException if the configuration script contains errors or raises an exception
	 * @throws FileNotFoundException if the configuration script cannot be opened
	 * @throws ParseException if there are any invalid arguments
	 */
	public static void main(String[] args) throws FileNotFoundException, ScriptException {
		// define options which will be parsed by Apache commons-cli
		Options opts = new Options();
		opts.addOption("w", "which-files", false, "Print the names of all files that would be generated. Don't create any files.");
		opts.addOption("h", "help", false, "Print this help");
		opts.addOption("C", "directory", true, "Generate files in directory");
		
		// parse the program arguments
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
		String target_directory = cmd.getOptionValue('C', ".");
		if (!target_directory.endsWith("/"))
			target_directory += "/";
		
		// configuration file should be the only left-over argument
		String config_file = null;
		args = cmd.getArgs();
		if (args.length == 1)
			config_file = args[0];
		else if (args.length > 1) {
			System.err.println("Too many left-over arguments: " + args[0] + " " + args[1]
					+ (args.length > 2 ? " ..." : ""));
			System.exit(-1);
			return;
		}
		
		// only print a list of file to generate, if the -w option is given
		// This can be used to specify dependencies in a Makefile.
		if (cmd.hasOption("w")) {
			for (IGenerator gen : findGenerators()) {
				for (String file : gen.getFiles().keySet()) {
					//TODO escape filenames
					System.out.print(target_directory + file);
					System.out.print(" ");
				}
			}
			return;
		}
		
		// load config file
		if (config_file == null) {
			System.err.println("Please give me a configuration file!");
			System.exit(-1);
			return;
		}
		MCUConfiguration config = loadConfig(config_file);
		
		// generate the files
		runGenerators(config, target_directory);
	}

	/**
	 * Run all generators
	 * @param config the configuration object
	 * @param target_directory directory for the generated files
	 */
	private static void runGenerators(MCUConfiguration config,
			String target_directory) {
		for (IGenerator gen : findGenerators()) {
			for (Entry<String, ITemplate> e : gen.getFiles().entrySet()) {
				File file = new File(target_directory + e.getKey());
				ITemplate generator = e.getValue();
				
				// run the generator
				System.out.println("Generating " + file);
				String contents = generator.generate(config);
				//TODO track errors and warnings and display a summary;
				//      return a non-zero value with System.exit, if there are errors
				
				// write the result into a file
				if (contents != null) {
					file.getParentFile().mkdirs();
					
					try {
						Writer w = new OutputStreamWriter(
								new FileOutputStream(file),
								Charset.forName("utf-8"));
						w.write(contents);
						w.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	}
}
