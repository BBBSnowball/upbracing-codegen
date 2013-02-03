package de.upbracing.code_generation.generators;

import java.util.HashMap;

import de.upbracing.code_generation.GlobalVariableCFileTemplate;
import de.upbracing.code_generation.GlobalVariableHeaderTemplate;
import de.upbracing.code_generation.TemplateHelpers;
import de.upbracing.code_generation.config.GlobalVariable;
import de.upbracing.code_generation.config.CodeGeneratorConfigurations;
import de.upbracing.code_generation.config.GlobalVariableConfigProvider;

/**
 * Generator for global variable accessors
 * 
 * @author benny
 */
public class GlobalVariableGenerator extends AbstractGenerator {
	public GlobalVariableGenerator() {
		super("global_variables.h", new GlobalVariableHeaderTemplate(),
				"global_variables.c", new GlobalVariableCFileTemplate());
	}
	
	@Override
	public Object updateConfig(CodeGeneratorConfigurations config) {
		HashMap<String, String> varnames = new HashMap<String, String>();
		for (GlobalVariable var : GlobalVariableConfigProvider.get(config)) {
			String varname = var.getName();
			varname += "_" + TemplateHelpers.md5(varname);
			varnames.put(var.getName(), varname);
		}
		return varnames;
	}
}
