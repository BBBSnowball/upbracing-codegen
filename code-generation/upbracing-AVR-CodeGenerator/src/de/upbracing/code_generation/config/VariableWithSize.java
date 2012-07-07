package de.upbracing.code_generation.config;

/**
 * A variable which has a size in addition to a type. In most cases the size
 * can be provided by {@link CType#getSizeOf(String)}.
 * 
 * @author benny
 */
public class VariableWithSize extends Variable {
	private int size;

	/**
	 * constructor
	 * 
	 * @param name variable name
	 * @param type variable type in C code
	 */
	public VariableWithSize(String name, String type) {
		super(name, type);
		this.size = -1;
	}

	/**
	 * constructor
	 * 
	 * @param name variable name
	 * @param type variable type in C code
	 * @param size size of the type in bytes or -1
	 */
	public VariableWithSize(String name, String type, int size) {
		super(name, type);
		this.size = size;
	}

	/** get size in bytes
	 * 
	 * @return the size or a negative value, if the size is not available
	 */
	public int getSize() {
		return size;
	}
	
	/** set type and size
	 * 
	 * @param type the C type
	 * @param size the size in bytes
	 */
	public void setType(String type, int size) {
		setType(type);
		this.size = size;
	}
}
