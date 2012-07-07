package de.upbracing.code_generation.config;

import org.simpleframework.xml.Default;

/**
 * Abstract base class of classes that represent variables
 * 
 * @author benny
 */
@Default
public abstract class Variable {
	private String name;
	private String type;
	
	/** constructor
	 * 
	 * @param name the name
	 * @param type C type
	 */
	public Variable(String name, String type) {
		super();
		this.name = name;
		this.type = type;
	}

	/** get the name
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/** get the type
	 * 
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/** set the type
	 * 
	 * @param type the new type
	 */
	public void setType(String type) {
		this.type = type;
	}
}
