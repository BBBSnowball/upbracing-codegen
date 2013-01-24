package de.upbracing.code_generation.test;

import static de.upbracing.code_generation.test.TestHelpers.loadResource;
import static org.junit.Assert.assertEquals;

import java.util.Map.Entry;

import de.upbracing.code_generation.IGenerator;
import de.upbracing.code_generation.ITemplate;
import de.upbracing.code_generation.config.CodeGeneratorConfigurations;

public class GeneratorTester {
	private IGenerator gen;
	private CodeGeneratorConfigurations config;
	private Object generator_data;

	public GeneratorTester(IGenerator gen, CodeGeneratorConfigurations config) {
		this.gen = gen;
		this.config = config;
		
		this.prepare();
	}
	
	private void prepare() {
		assertEquals(true, gen.validate(config, false, null));
		generator_data = gen.updateConfig(config);
		assertEquals(true, gen.validate(config, true, generator_data));
	}
	
	private ITemplate getTemplate(String filename) {
		ITemplate template = gen.getFiles().get(filename);
		if (template == null)
			throw new IllegalArgumentException("The generator cannot generate the file '" + filename + "'");
		
		return template;
	}
	
	public String runTemplate(String filename) {
		return runTemplate(getTemplate(filename));
	}
	
	public String runTemplate(ITemplate template) {
		return template.generate(config, generator_data);
	}
	
	public void testTemplate(String generatedFilename, String expectedResultFilename) {
		testTemplate(getTemplate(generatedFilename), expectedResultFilename);
	}
	
	public void testTemplate(ITemplate template, String expectedResultFilename) {
		String expected, result;
		expected = loadResource(expectedResultFilename);
		result = runTemplate(template);
		String msg = String.format("Template '%s' generates wrong text", template.getClass().getSimpleName());
		assertEquals(msg, expected, result);
	}
	
	public void testTemplates(String expectedResultFolder) {
		for (Entry<String, ITemplate> entry : gen.getFiles().entrySet()) {
			testTemplate(entry.getValue(), expectedResultFolder + "/" + entry.getKey());
		}
	}
}
