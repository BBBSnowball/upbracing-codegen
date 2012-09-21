package de.upbracing.code_generation;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for printing warning in code generator scripts
 * 
 * @author sven
 */
public class Warnings {

	private final String NL = System.getProperties().getProperty("line.separator");
	private List<String> warnings = new ArrayList<String>();
	
	/**
	 * Prints a warning to the command line and stores the warning
	 * in a list. The list should be printed at the end of the generated file.
	 * It can be retrieved by calling summary()
	 *  
	 * @param warning The text of the warning to be printed
	 * @return the warning text preceded by newlines and "#warning" 
	 */
	public String print(String warning) {
		System.out.println("Warning: " + warning);
		
		warnings.add(warning);
		
		return(NL + NL + "#warning " + warning + NL);
	}
	
	/**
	 * Returns a list of all printed warnings
	 * 
	 * @return a string with all warnings
	 */
	public String summary() {
		StringBuilder summary = new StringBuilder();
		
		if (warnings.size() > 0) {
			if (warnings.size() == 1)
				summary.append(NL + "#warning There was 1 warning" + NL + "/*");
			else
				summary.append(NL + "#warning There were " + warnings.size() + " warnings" + NL + "/*");
			
			for (String warning : warnings) {
				summary.append(NL + "  warning: " + warning);
			}
			
			summary.append(NL + "*/");
		}
		
		return summary.toString();
	}
	
	/**
	 * Clears the list of warnings
	 * 
	 */
	public void clear() {
		warnings.clear();
	}
	
}
