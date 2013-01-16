package de.upbracing.code_generation.generators;

import java.util.List;

import de.upbracing.code_generation.config.DBCMessageConfig;
import de.upbracing.dbc.DBCMessage;

/**
 * Helper functions for the CAN code generator
 * 
 * @author sven
 */
public class CanGeneratorHelper {
	
	public static final String NL = System.getProperties().getProperty("line.separator");
	
	/**
	 * Returns the id of a message with an optional suffix
	 * 
	 * @param message The message object
	 * @param suffix If true, an "x" is added to the id of an extended message
	 * @return The message id as a hex string with "0x" prefix
	 */
	public static String messageId(DBCMessage message, boolean suffix) {
		return "0x" + Integer.toHexString(message.getId()) + ((suffix && message.isExtended()) ? "x" : "");
	}
	
	/**
	 * Returns the ids of some messages with an optional suffix
	 * 
	 * @param messages The message objects
	 * @param suffix If true, an "x" is added to the id of an extended message
	 * @return The message ids as a hex string with "0x" prefix, separated by commas
	 */
	public static String messageIds(Iterable<? extends DBCMessage> messages, boolean suffix) {
		StringBuffer sb = new StringBuffer();
		for (DBCMessage msg : messages) {
			if (sb.length() != 0)
				sb.append(", ");
			
			sb.append(messageId(msg, suffix));
		}
		
		if (sb.length() == 0)
			return "---";
		else
			return sb.toString();
	}

	/**
	 * Prints a string to a StringBuffer and prefixes each line with an indent string
	 * 
	 * @param stringBuffer The StringBuffer to which the code is printed
	 * @param code A multiline string that is printed to the StringBuffer
	 * @param indent String that is added as a prefix to each line of code (usually tabs)
	 * @return true if code was printed, false if code.length == 0 or code or indent is null
	 */
	public static boolean printCode(StringBuffer stringBuffer, String code, String indent) {

		if (code == null || indent == null) return false;
		
		for(String line : code.split(NL)) {
			stringBuffer.append(NL + indent + line);
		}
		
		return code.length() > 0;
	}

	/**
	 * Concatenates a list of strings to one string, separated by commas
	 * 
	 * @param list A list of strings
	 * @return String with concatenated list
	 */
	public static String implode(List<String> list) {
		String result = "";
		boolean firstEntry = true;
		
		for(String string : list) {
			result += (firstEntry?"":", ") + string; 
			firstEntry = false;
		}
		
		return result;
	}
	
	/**
	 * Concatenates the names of a list of DBCMessageConfig objects to one string, separated by commas 
	 * 
	 * @param messages A list with DBCMessageConfig objects
	 * @return String with concatenated message names
	 */
	public static String implodeMessages(List<DBCMessageConfig> messages) {
		String result = "";
		boolean firstEntry = true;
		
		for(DBCMessageConfig msg : messages) {
			result += (firstEntry?"":", ") + msg.getName(); 
			firstEntry = false;
		}
		
		return result;
	}
}
