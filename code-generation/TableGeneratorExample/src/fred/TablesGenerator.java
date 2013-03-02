package fred;

import de.upbracing.code_generation.generators.AbstractGenerator;

public class TablesGenerator extends AbstractGenerator {
	public TablesGenerator() {
		super("tables.h", new TablesTemplate());
	}
}
