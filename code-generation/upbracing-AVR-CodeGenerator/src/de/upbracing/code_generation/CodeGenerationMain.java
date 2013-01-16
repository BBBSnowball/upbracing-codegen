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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import de.upbracing.code_generation.config.MCUConfiguration;
import de.upbracing.code_generation.utils.Util;

public final class CodeGenerationMain {
	private CodeGenerationMain() { }

	/** Represents code generation arguments */
	public static class Arguments {
		private String config_file;
		private String target_directory, temp_directory;
		
		public String getConfigFile() {
			return config_file;
		}
		
		public void setConfigFile(String config_file) {
			this.config_file = config_file;
		}
		
		public String getTargetDirectory() {
			return target_directory;
		}
		
		public void setTargetDirectory(String target_directory) {
			if (!target_directory.endsWith("/"))
				target_directory += "/";
			this.target_directory = target_directory;
		}
		
		public boolean hasConfigFile() {
			return config_file != null;
		}
		
		public String getTempDirectory() {
			return temp_directory;
		}
		
		public void setTempDirectory(String temp_directory) {
			if (!temp_directory.endsWith("/"))
				temp_directory += "/";
			this.temp_directory = temp_directory;
		}
	}

	/**
	 * Load IGenerator instances via the Service Provider Interface
	 * 
	 * This will find all classes that are mentioned in an SPI file somewhere on the
	 * class path. The file is called:
	 * META-INF/services/de.upbracing.code_generation.IGenerator
	 * @return a list of generator instances
	 */
	private static Iterable<IGenerator> findGenerators() {
		ServiceLoader<IGenerator> loader = ServiceLoader.load(IGenerator.class);
		return loader;
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
		Pattern p1 = Pattern.compile("^\\s*#[ \t]*DEPENDS ON", Pattern.MULTILINE);
		Pattern p2 = Pattern.compile("^\\s*#[ \t]*DEPENDS ON:?[ \t]*(\"([^\"]+)\"|(\\S+)|\r?\n[^\"\r\n]*\"([^\"\r\n]+)\"[^\"\r\n]*$)", Pattern.MULTILINE);
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

	/** Get all dependencies (not only from config file)
	 * 
	 * @param config code generator configuration
	 * @param complete include classpath dependencies (very many files!)
	 * @return	a list of dependencies
	 * @throws IOException	if the config file cannot be read
	 */
	public static Collection<String> getDependencies(Arguments config, boolean complete,
				String... ignored_classpath_entries)
			throws IOException {
		List<String> files = new ArrayList<String>();
		
		// the config file itself is always a dependency
		files.add(config.config_file);
		
		String config_directory = new File(config.config_file).getParent();
		for (String dependency : getDependencies(config.config_file)) {
			if (!new File(dependency).isAbsolute())
				dependency = new File(config_directory, dependency).getPath();
			files.add(dependency);
		}
		
		if (complete) {
			Set<String> ignored_classpath_entries_set
				= new HashSet<String>(Arrays.asList(ignored_classpath_entries));
			String classpath[] = System.getProperty("java.class.path")
					.split(""+File.pathSeparatorChar);
			for (String entry : classpath) {
				if (ignored_classpath_entries_set.contains(entry))
					continue;
				
				File f = new File(entry);
				if (f.isFile())
					// a JAR file
					files.add(f.getPath());
				else if (f.isDirectory()) {
					// a directory -> find all files
					//NOTE In theory, it would be enough to list the *.class files, but
					//     we have *.rb files as well and probably some others.
					for (String class_file : getFiles(f))
						files.add(class_file);
				} else
					System.err.println("WARNING: The class path element '" + entry + "' couldn't be found. Therefore, it cannot be tracked as a dependency of the generated code.");
			}
		}
		
		return files;
	}

	/** Get all files that could be generated
	 * 
	 * @param config code generator configuration
	 * @return list of files
	 */
	public static Collection<String> getGeneratedFiles(Arguments config) {
		List<String> files = new ArrayList<String>();
		for (IGenerator gen : findGenerators()) {
			for (String file : gen.getFiles().keySet()) {
				files.add(config.target_directory + file);
			}
		}
		return files;
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

	/** Make a set of used generators
	 * 
	 * @param gen generator to query used generators for
	 * @return the set
	 */
	private static Set<Class<IGenerator>> getUsedSet(IGenerator gen) {
		HashSet<Class<IGenerator>> used = new HashSet<Class<IGenerator>>();
		for (Class<IGenerator> used_gen : gen.getUsedGenerators())
			used.add(used_gen);
		return used;
	}

	/** Test whether the class or one of its superclasses or interfaces is in the set
	 * 
	 * @param gen_class the class to look for
	 * @param used the set
	 * @return whether the class or one of its superclasses or interfaces is in the set
	 */
	private static boolean inUsedSet(Class<?> gen_class, Set<Class<IGenerator>> used) {
		do {
			if (used.contains(gen_class))
				return true;
			
			for (Class<?> gen_interface : gen_class.getInterfaces())
				if (inUsedSet(gen_interface, used))
					return true;
			
			gen_class = gen_class.getSuperclass();
		} while (gen_class != null);
		
		return false;
	}

	/** Test whether the class or one of its superclasses or interfaces is in the set
	 * 
	 * @param gen the generator to look for
	 * @param used the set
	 * @return whether the class or one of its superclasses or interfaces is in the set
	 */
	private static boolean inUsedSet(IGenerator gen, Set<Class<IGenerator>> used) {
		return inUsedSet(gen.getClass(), used);
	}

	/**
	 * Sort generators according to the information {@link IGenerator#getUsedGenerators()}
	 * @param generators
	 * @return
	 */
	private static Iterable<IGenerator> sortGenerators(Iterable<IGenerator> generators) {
		//TODO This algorithm will only work for simple cases. We have to build a graph.
		ArrayList<IGenerator> list = new ArrayList<IGenerator>();
		for (IGenerator gen : generators) {
			Set<Class<IGenerator>> used = getUsedSet(gen);
			
			// find the first generator that is used by the generator
			int index;
			for (index=0;index<list.size();index++) {
				if (inUsedSet(list.get(index), used))
					break;
			}
			
			// place the generator before the used class
			// (This works in all cases because add(...) allows index==list.size().)
			int add_index = index;
			list.add(index, gen);
			
			// make sure that the generator is not used by any of the generators following it
			for (;index<list.size();index++) {
				if (inUsedSet(gen, getUsedSet(list.get(index)))) {
					System.err.println("ERROR: Dependency loop in generators (or fail of the "
							+ "too simple dependency resolution algorithm) with "
							+ list.get(index).getName() + " -> " + gen.getName() + " -> "
							+ list.get(add_index+1).getName());
				}
			}
		}
		return list;
	}

	/**
	 * Validate configuration
	 * 
	 * @param generators list of generators
	 * @param config the configuration
	 * @param after_update_config true, if validate should assume that updateConfig has been run
	 * @param failed_generators 
	 * @return
	 */
	private static boolean validate(Iterable<IGenerator> generators,
			MCUConfiguration config, boolean after_update_config, HashSet<IGenerator> failed_generators,
			Map<IGenerator, Object> generator_data) {
		boolean validation_passed = true;
		for (IGenerator gen : generators) {
			if (failed_generators.contains(gen))
				continue;
			
			if (!gen.validate(config, after_update_config,
					(generator_data != null ? generator_data.get(gen) : null))) {
				System.err.println("ERROR: Validation failed for " + gen.getName());
				validation_passed = false;
				failed_generators.add(gen);
			}
		}
		return validation_passed;
	}

	/**
	 * Run all generators
	 * @param config the configuration object
	 * @param target_directory directory for the generated files
	 */
	public static boolean runGenerators(MCUConfiguration config,
			String target_directory) {
		Iterable<IGenerator> generators = sortGenerators(findGenerators());
		
		HashSet<IGenerator> failed_generators = new HashSet<IGenerator>();
		
		if (!validate(generators, config, false, failed_generators, null))
			return false;
		
		Map<IGenerator, Object> generator_data_values = new HashMap<IGenerator, Object>();
		for (IGenerator gen : generators) {
			if (!failed_generators.contains(gen))
				generator_data_values.put(gen, gen.updateConfig(config));
		}
	
		if (!validate(generators, config, true, failed_generators, generator_data_values))
			return false;
		
		for (IGenerator gen : generators) {
			if (failed_generators.contains(gen))
				continue;
	
			Object generator_data = generator_data_values.get(gen);
			
			for (Entry<String, ITemplate> e : gen.getFiles().entrySet()) {
				File file = new File(target_directory + e.getKey());
				ITemplate template = e.getValue();
				
				if (!gen.isTemplateActive(e.getKey(), template, config))
					//TODO should we delete the file?
					continue;
				
				// run the generator
				System.out.println("Generating " + file);
				String contents = template.generate(config, generator_data);
				//TODO track errors and warnings and display a summary;
				//      return a non-zero value with System.exit, if there are errors
				
				// write the result into a file
				if (contents != null) {
					file.getParentFile().mkdirs();

					Charset charset = Charset.forName("utf-8");
					
					// does it already contain the right text?
					boolean right_content = false;
					if (file.exists()) {
						try {
							Reader r = new InputStreamReader(new FileInputStream(file), charset);
							String existing_content = JRubyHelpers.readContent(r);
							right_content = existing_content.equals(contents);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					
					if (!right_content) {
						try {
							Writer w = new OutputStreamWriter(
									new FileOutputStream(file),
									charset);
							w.write(contents);
							w.close();
						} catch (IOException e1) {
							e1.printStackTrace();
							failed_generators.add(gen);
						}
					} else
						System.out.println(" -> not changed");
				}
			}
		}
		
		if (!failed_generators.isEmpty()) {
			System.err.println("\n\n!!! Parts of the code have not been generated (see above) !!!\n");
			return false;
		} else
			return true;
	}
	
	
	/**
	 * load a JRuby configuration file
	 * 
	 * @param stream InputStream for the configuration file. It should be encoded with utf-8.
	 * @return the configuration object
	 * @throws ScriptException if the config script contains errors or raises an exception
	 */
	public static MCUConfiguration loadConfig(InputStream stream, String script_filename, String directory, Map<String, Object> global_vars) throws ScriptException {
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
		
		for (Entry<String, Object> pair : global_vars.entrySet())
			engine.put(pair.getKey(), pair.getValue());
		
		engine.eval("require 'config-helpers.rb'");
		
		// go to directory of the script
		String old_pwd = null;
		if (directory != null) {
			old_pwd = engine.eval("Dir.pwd").toString();
			engine.put("directory", directory);
			engine.eval("Dir.chdir($directory)");
			MCUConfiguration.setCurrentDirectory(directory);
		}
		
		// execute the script
		engine.put(ScriptEngine.FILENAME, script_filename);
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
	public static MCUConfiguration loadConfig(String file) throws FileNotFoundException, ScriptException {
		return loadConfig(
				new FileInputStream(file),
				file,
				new File(file).getAbsoluteFile().getParent(),
				Collections.<String,Object>emptyMap());
	}
	
	/**
	 * load a JRuby configuration file
	 * 
	 * @param config code generator configuration
	 * @return the configuration object
	 * @throws ScriptException if the config script contains errors or raises an exception
	 * @throws FileNotFoundException if the config file cannot be opened
	 */
	public static MCUConfiguration loadConfig(Arguments config) throws FileNotFoundException, ScriptException {
		String file = config.getConfigFile();
		String script_cwd = new File(file).getAbsoluteFile().getParent();
		return loadConfig(
				new FileInputStream(file),
				file,
				script_cwd,
				Collections.<String,Object>singletonMap("tempdir",
						Util.adjustToBeRelativeTo(config.getTempDirectory(), script_cwd)));
	}
}
