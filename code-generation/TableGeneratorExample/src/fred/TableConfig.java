package fred;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class TableConfig {
	private String name;
	private List<String> names = new ArrayList<String>();
	private List<String> types = new ArrayList<String>();
	private List<Object[]> data = new ArrayList<Object[]>();
	
	// JRuby: tableconfig.name
	public String getName() {
		return name;
	}
	
	// JRuby: tableconfig.name = "my_name"
	public void setName(String name) {
		if (!Pattern.matches("^[a-zA-Z_][a-zA-Z0-9_]*$", name))
			throw new IllegalArgumentException("invalid name (must be C identifier): " + name);
		
		this.name = name;
	}
	
	// JRuby: first_name = tableconfig.names.first
	// JRuby: tableconfig.names.add "position"
	// JRuby: column_pos = tableconfig.names.index? "position"
	public List<String> getNames() {
		return names;
	}
	
	// JRuby: tableconfig.types.add "uint8_t"
	public List<String> getTypes() {
		return types;
	}
	
	// JRuby: tableconfig.data.first[0]
	public List<Object[]> getData() {
		return data;
	}
	
	// JRuby: add_column("voltage", "int8_t")
	public void addColumn(String name, String type) {
		if (!Pattern.matches("^[a-zA-Z_][a-zA-Z0-9_]*$", name))
			throw new IllegalArgumentException("invalid name (must be C identifier): " + name);
		
		this.names.add(name);
		this.types.add(type);
	}
	
	// JRuby: add_data(3, 40)
	public void addData(Object... row) {
		if (row.length != names.size())
			throw new IllegalArgumentException("please provide exactly " + names.size() + " values");
		
		this.data.add(row);
	}
}
