package de.upbracing.code_generation.config;

import java.io.File;

/** In Java the current directory cannot be changed, so JRuby
 * keeps its own 'current working directory' which may be
 * different of Java's CWD. This means that we have to treat
 * paths in a special way, if they come from JRuby. We use
 * this class to keep track of the CWD in JRuby, so we can
 * fix paths for Java.
 * 
 * @author benny
 */
public class CWDProvider {
	/** current working directory for loading config files */
	public static String currentDirectory = ".";
	
	/** get current working directory for loading config files */
	public static String getCurrentDirectory() { return currentDirectory; }

	/** set current working directory for loading config files */
	public static void setCurrentDirectory(String cwd) { currentDirectory = cwd; }

	/** make absolute path
	 * 
	 * If the path is not absolute, use {@link #getCurrentDirectory()} as the parent. 
	 * 
	 * @param path the path
	 * @return absolute path
	 */
	public static File makeAbsolute(File path) {
		if (path.isAbsolute())
			return path;
		else
			return new File(getCurrentDirectory(), path.toString());
	}
	
	/** make absolute path
	 * 
	 * If the path is not absolute, use {@link #getCurrentDirectory()} as the parent. 
	 * 
	 * @param path the path
	 * @return absolute path
	 */
	public static String makeAbsolute(String path) {
		return makeAbsolute(new File(path)).getAbsolutePath();
	}
}
