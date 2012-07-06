package de.upbracing.code_generation.generators;

import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import de.upbracing.code_generation.IGenerator;
import de.upbracing.code_generation.ITemplate;

public abstract class AbstractGenerator implements IGenerator {
	private SortedMap<String, ITemplate> files = new TreeMap<String, ITemplate>();
	
	public AbstractGenerator(Object... filespecs) {
		if ((filespecs.length%2) != 0)
			throw new IllegalArgumentException("expecting pairs of file name and template, but got an odd number of arguments");
		
		for (int i=0;i+1<filespecs.length;i+=2) {
			if (!(filespecs[i] instanceof String))
				throw new IllegalArgumentException("Argument " + (i+1) + " should be a String (the file name)");
			if (!(filespecs[i+1] instanceof ITemplate))
				throw new IllegalArgumentException("Argument " + (i+2) + " should be an ITemplate");
			
			addFile((String)filespecs[i], (ITemplate)filespecs[i+1]);
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
	
	protected void addFile(String filename, ITemplate template) {
		this.files.put(filename, template);
	}
}
