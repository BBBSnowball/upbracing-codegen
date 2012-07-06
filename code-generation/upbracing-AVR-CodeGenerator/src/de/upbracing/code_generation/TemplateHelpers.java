package de.upbracing.code_generation;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Contains static functions which are called from the templates
 * 
 * @author benny
 */
public class TemplateHelpers {
	/**
	 * Calculate MD5 hash of the string and return it as a String of hex values
	 * @param x String to compute the hash of
	 * @return MD5 hash as a String of hex values
	 */
	public static String md5(String x) {
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "MD5_NOT_POSSIBLE";
		}
		md5.update(x.getBytes());
		byte data[] = md5.digest();
		StringBuffer sb = new StringBuffer();
		for (int i=0;i<data.length;i++) {
			sb.append(String.format("%02X", data[i]));
		}
		return sb.toString();
	}
	
	/**
	 * Convert the first letter of the String to upper case
	 * 
	 * @param x input
	 * @return modified String
	 */
	public static String capitalize(String x) {
		if (x.length() < 1)
			return x;
		
		return Character.toUpperCase(x.charAt(0)) + x.substring(1);
	}
}
