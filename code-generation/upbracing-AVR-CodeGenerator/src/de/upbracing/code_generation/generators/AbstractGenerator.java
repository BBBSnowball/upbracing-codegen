package de.upbracing.code_generation.generators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import de.upbracing.code_generation.IGenerator;
import de.upbracing.code_generation.ITemplate;
import de.upbracing.code_generation.config.MCUConfiguration;

/**
 * Generic implementation of the IGenerator interface
 * 
 * @author benny
 */
public abstract class AbstractGenerator implements IGenerator {
	private SortedMap<String, ITemplate> files = new TreeMap<String, ITemplate>();
	private ArrayList<Class<IGenerator>> used_generators = new ArrayList<Class<IGenerator>>();
	
	/**
	 * constructor
	 * 
	 * @param filespecs used generators (Class<IGenerator>) and pairs of file name (String) and template (ITemplate)
	 */
	@SuppressWarnings("unchecked")
	public AbstractGenerator(Object... filespecs) {
		//if ((filespecs.length%2) != 0)
		//	throw new IllegalArgumentException("expecting pairs of file name and template, but got an odd number of arguments");
		
		for (int i=0;i+1<filespecs.length;) {
			if (filespecs[i] instanceof String) {
				if (filespecs.length <= i+1)
					throw new IllegalArgumentException("expecting pairs of file name and template, but there is no template for argument " + (i+1));
				if (!(filespecs[i+1] instanceof ITemplate))
					throw new IllegalArgumentException("Argument " + (i+2) + " should be an ITemplate");
				
				addFile((String)filespecs[i], (ITemplate)filespecs[i+1]);
				
				i += 2;
			} else if (filespecs[i] instanceof Class
					&& IGenerator.class.isAssignableFrom((Class<?>)filespecs[i])) {
				addUsedGenerator((Class<IGenerator>)filespecs[i]);
				
				i += 1;
			} else
				throw new IllegalArgumentException("Argument " + (i+1) + " should be a String (the file name) or a Class object which is a subclass of IGenerator (a used generator)");
		}
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public Map<String, ITemplate> getFiles() {
		return Collections.unmodifiableSortedMap(files);
	}

	@Override
	public boolean isTemplateActive(String filename, ITemplate template,
			de.upbracing.code_generation.config.MCUConfiguration config) {
		return true;
	}
	
	@Override
	public Object updateConfig(MCUConfiguration config) {
		return null;
	}
	
	@Override
	public boolean validate(MCUConfiguration config,
			boolean after_update_config, Object generator_data) {
		return true;
	}
	
	@Override
	public Iterable<Class<IGenerator>> getUsedGenerators() {
		return used_generators;
	}
	
	/**
	 * Add a file to be generated; should be used in the constructor only
	 * 
	 * @param filename name of the generated file
	 * @param template template that generates the code for this file
	 */
	protected void addFile(String filename, ITemplate template) {
		this.files.put(filename, template);
	}


	/**
	 * Add a used generator
	 * 
	 * @param used_gen the used generator
	 */
	protected void addUsedGenerator(Class<IGenerator> used_gen) {
		used_generators.add(used_gen);
	}
}
