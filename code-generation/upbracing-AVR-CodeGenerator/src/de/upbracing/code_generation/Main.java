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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.ServiceLoader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.jruby.compiler.ir.operands.Array;

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
	private static MCUConfiguration loadConfig(InputStream stream, String directory) throws ScriptException {
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
		
		// go to directory of the script
		String old_pwd = null;
		if (directory != null) {
			old_pwd = engine.eval("Dir.pwd").toString();
			engine.put("directory", directory);
			engine.eval("Dir.chdir($directory)");
		}
		
		// execute the script
		Reader script_reader = new InputStreamReader(stream, Charset.forName("utf-8"));
		engine.eval(script_reader);
		try {
			script_reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// restore working directory
		if (old_pwd != null) {
			engine.put("directory", directory);
			engine.eval("Dir.chdir($directory)");
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
		return loadConfig(
				new FileInputStream(file),
				new File(file).getParent());
	}
	
	/** Read contents of a file
	 * 
	 * @param file the file
	 * @return file contents as String
	 * @throws IOException if an error occurs while opening or reading the file
	 */
	private static String readFile(String file) throws IOException {
		InputStream stream = new FileInputStream(file);
		Reader reader = new InputStreamReader(stream, Charset.forName("utf-8"));
		
		StringBuffer sb = new StringBuffer();
		char buf[] = new char[256];
		int len;
		while ((len = reader.read(buf)) > 0)
			sb.append(buf, 0, len);
		
		return sb.toString();
	}
	
	/** Read the config file and extract dependencies
	 * 
	 * To avoid the overhead of executing the config script, dependencies
	 * must be marked with "DEPENDS ON:". The dependency must be on the same
	 * line or the only double-quoted String on the following line.
	 * @param config_file the config file
	 * @return a list of dependencies
	 * @throws IOException if the config file cannot be read
	 */
	private static List<String> getDependencies(String config_file) throws IOException {
		// read the config file, but don't execute it
		String config_contents = readFile(config_file);
		
		// find lines which contain 'DEPENDS ON'
		// The pattern p1 finds all of those lines; p2 only
		// find the valid ones.
		Pattern p1 = Pattern.compile("^#[ \t]*DEPENDS ON", Pattern.MULTILINE);
		Pattern p2 = Pattern.compile("^#[ \t]*DEPENDS ON:?[ \t]*(\"([^\"]+)\"|(\\S+)|\r?\n[^\"\r\n]*\"([^\"\r\n]+)\"[^\"\r\n]*$)", Pattern.MULTILINE);
		Matcher m1 = p1.matcher(config_contents);
		Matcher m2 = p2.matcher(config_contents);

		/*while (m1.find()) {
			System.out.println("m1: " + m1.start());
		}
		while (m2.find()) {
			System.out.println("m2: " + m2.start() + ", " + m2.group(2) + ", " + m2.group(3) + ", " + m2.group(4));
			System.out.println(m2.group());
		}*/
		
		List<String> dependencies = new LinkedList<String>();
		boolean m1_found = m1.find();
		while (m2.find()) {
			while (m1_found && m1.start() < m2.start()) {
				int character = m1.start();
				int line = config_contents.substring(0, character+1).split("\r?\n").length;
				System.err.println("WARNING: Found 'DEPENDS ON' which is not valid (at character " + m1.start() + ", on line " + line + ").");
				
				m1_found = m1.find();
			}
			m1_found = m1.find();
			
			String dependency = m2.group(2);
			if (dependency == null)
				dependency = m2.group(3);
			if (dependency == null)
				dependency = m2.group(4);
			if (dependency == null)
				throw new RuntimeException("One of the groups must match -> error in the program");
			
			dependencies.add(dependency);
		}
		while (m1_found) {
			int character = m1.start();
			int line = config_contents.substring(0, character+1).split("\r?\n").length;
			System.err.println("WARNING: Found 'DEPENDS ON' which is not valid (at character " + m1.start() + ", on line " + line + ").");
			
			m1_found = m1.find();
		}
		
		return dependencies;
	}

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
					System.out.println(escapeForMakefile(target_directory + file));
				}
			}
			return;
		}
		
		// make sure that we have a config file
		if (config_file == null) {
			System.err.println("Please give me a configuration file!");
			System.exit(-1);
			return;
		}
		
		// only print dependencies, if the -d option is given
		// This can be used to specify dependencies in a Makefile.
		if (cmd.hasOption("d") || cmd.hasOption("D")) {
			String config_directory = new File(config_file).getParent();
			for (String dependency : getDependencies(config_file)) {
				if (!new File(dependency).isAbsolute())
					dependency = new File(config_directory, dependency).getPath();
				System.out.println(escapeForMakefile(dependency));
			}
			
			if (cmd.hasOption("D")) {
				String classpath[] = System.getProperty("java.class.path")
						.split(""+File.pathSeparatorChar);
				for (String entry : classpath) {
					File f = new File(entry);
					if (f.isFile())
						// a JAR file
						System.out.println(escapeForMakefile(f.getPath()));
					else if (f.isDirectory()) {
						// a directory -> find all files
						//NOTE In theory, it would be enough to list the *.class files, but
						//     we have *.rb files as well and probably some others.
						for (String class_file : getFiles(f))
							System.out.println(escapeForMakefile(class_file));
					} else
						System.err.println("WARNING: The class path element '" + entry + "' couldn't be found. Therefore, it cannot be tracked as a dependency of the generated code.");
				}
			}
			return;
		}
		
		// load config file
		MCUConfiguration config = loadConfig(config_file);
		
		// generate the files
		runGenerators(config, target_directory);
	}

	/** Find all files in a directory
	 * 
	 * @param directory the directory
	 * @return the list of file names
	 */
	private static List<String> getFiles(File directory) {
		List<String> class_files = new LinkedList<String>();
		getClassFiles(directory, class_files);
		return class_files;
	}
	
	/** Find all files in a directory
	 * 
	 * @param directory the directory
	 * @param result the list the found files will be put into
	 */
	private static void getClassFiles(File directory, List<String> result) {
		for (File f : directory.listFiles()) {
			if (f.isDirectory())
				getClassFiles(f, result);
			else
				result.add(f.getPath());
		}
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
