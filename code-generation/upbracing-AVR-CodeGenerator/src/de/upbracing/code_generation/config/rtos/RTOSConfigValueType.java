package de.upbracing.code_generation.config.rtos;

import de.upbracing.code_generation.Messages;

public abstract class RTOSConfigValueType {
	private RTOSConfigValueType() { }
	
	public abstract boolean validate(String value, Messages messages);
	public abstract void addCode(String indent, StringBuffer sb, RTOSConfigValue value);

	/** define with a value
	 * 
	 * #define BLUB 42
	 */
	public static final RTOSConfigValueType DefineValue = new RTOSConfigValueType() {
		@Override
		public boolean validate(String value, Messages messages) {
			//TODO not valid, if it has unmatched parentheses
			return true;
		}
		
		@Override
		public void addCode(String indent, StringBuffer sb, RTOSConfigValue value) {
			sb.append("#" + indent + "define " + value.getName() + " ");
			sb.append(value.getValue().replaceAll("\n", "\t\\\n" + indent + "\t"));
			sb.append("\n");
		}
	};
		
	/** either defined (true) or not (false)
	 * 
	 * #define BLUB
	 *  or
	 * #undef  BLUB
	 */
	public static final RTOSConfigValueType DefineFlag = new RTOSConfigValueType() {
		private boolean parseBool(String x) throws NumberFormatException {
			x = x.toLowerCase();
			
			if (x == null || x.isEmpty())
				return false;
			else if (x.equals("0") || x.equals("false") || x.equals("f"))
				return false;
			else if (x.equals("1") || x.equals("true") || x.equals("t"))
				return true;
			else
				throw new NumberFormatException();
		}
		
		@Override
		public boolean validate(String value, Messages messages) {
			try {
				parseBool(value);
				return true;
			} catch (NumberFormatException e) {
				messages.error("expecting a boolean (1, 0, true or false), but got %s", value);
				return false;
			}
		}
		
		@Override
		public void addCode(String indent, StringBuffer sb, RTOSConfigValue value) {
			boolean x = parseBool(value.getValue());
			
			sb.append("#" + indent + (x ? "define " : "undef  ") + value.getName() + "\n");
		}
	};
	
	/** define with a value, but also generates flags
	 * 
	 * #define BLUB a
	 * #define BLUB_a
	 * #undef  BLUB_b
	 * #undef  BLUB_c
	 */
	public static RTOSConfigValueType DefineEnum(final String... flags) {
		return new RTOSConfigValueType() {
			private boolean flagMatches(String flag, String value) {
				return flag.equals(value);
			}
			
			private String defineName(String value_name, String option_name) {
				return value_name + "_" + option_name;
			}
			
			@Override
			public boolean validate(String value, Messages messages) {
				for (int i=0;i<flags.length;i++)
					if (flagMatches(flags[i], value))
						return true;
				
				messages.error("invalid enum value: %s", value);
				return false;
			}
			
			@Override
			public void addCode(String indent, StringBuffer sb, RTOSConfigValue value) {
				String name = value.getName();

				// add normal define
				sb.append("#" + indent + "define " + value.getName() + " ");
				sb.append(value.getValue().replaceAll("\n", "\t\\\n" + indent + "\t"));
				sb.append("\n");
				
				// add flags
				for (int i=0;i<flags.length;i++) {
					String option = flags[i];
					boolean x = flagMatches(flags[i], value.getValue());
					sb.append("#" + indent + (x ? "define " : "undef  ")
							+ defineName(name, option) + "\n");
				}
			}
		};
	}
	
	/** define a static integer constant
	 * 
	 * const static uint8_t blub = 42;
	 */
	public static RTOSConfigValueType DefineStaticConst(final String type) {
		return new RTOSConfigValueType() {
			@Override
			public boolean validate(String value, Messages messages) {
				return true;
			}
			
			@Override
			public void addCode(String indent, StringBuffer sb, RTOSConfigValue value) {
				sb.append(indent + "const static " + type + " " + value.getName() + " = ");
				sb.append(value.getValue().replaceAll("\n", "\n" + indent + "\t"));
				sb.append(";\n");
			}
		};
	}
}
