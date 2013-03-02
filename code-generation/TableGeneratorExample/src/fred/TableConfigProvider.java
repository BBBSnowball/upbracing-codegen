package fred;

import java.util.ArrayList;
import java.util.List;

import de.upbracing.code_generation.Messages;
import de.upbracing.code_generation.config.CodeGeneratorConfigurations;
import de.upbracing.code_generation.config.ConfigState;
import de.upbracing.code_generation.config.ConfigurationMethod;
import de.upbracing.code_generation.config.IConfigProvider;
import de.upbracing.code_generation.config.RichConfigurationExtender;

public class TableConfigProvider implements IConfigProvider {
	// STATE is public, so our classes and other generators can use it
	public static final ConfigState<List<TableConfig>> STATE
		= new ConfigState<List<TableConfig>>("tables");

	@Override
	public void extendConfiguration(RichConfigurationExtender ext) {
		// add a list of TableConfig objects to each config object
		ext.addListState(STATE);
		
		// let the user access the list like a property
		ext.addProperty("tables", STATE);
		
		// add all methods marked with @ConfigurationMethod
		ext.addMethods(TableConfigProvider.class);
	}

	@Override
	public void initConfiguration(CodeGeneratorConfigurations config) {
		config.setState(STATE, new ArrayList<TableConfig>());
	}

	@Override
	public void addFormatters(Messages messages) {
		// no custom formatters
	}

	@ConfigurationMethod
	/// create and add a new TableConfig
	public static TableConfig addTable(CodeGeneratorConfigurations config, String name) {
		TableConfig t = new TableConfig();
		t.setName(name);
		config.getState(STATE).add(t);
		return t;
	}
}
