package de.upbracing.code_generation.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.LinkedList;


public class Util {
	public static final Charset UTF8 = Charset.forName("utf-8");
	
	public static String loadStream(InputStream stream, Charset charset) throws IOException {
		Reader reader = new InputStreamReader(stream, charset);
		return loadFromReader(reader);
	}
	
	public static String loadFromReader(Reader reader) throws IOException {
		StringBuffer sb = new StringBuffer();
		char buf[] = new char[256];
		int len;
		while ((len = reader.read(buf)) > 0)
			sb.append(buf, 0, len);
		reader.close();
		return sb.toString();
	}

	private static String getResourcePath(Class<?> clazz, String name) {
		return clazz.getPackage().getName().replace('.', '/') + "/" + name;
	}
	
	public static URL getResourceURL(Class<?> clazz, String name) {
		String path = getResourcePath(clazz, name);
		return clazz.getClassLoader().getResource(path);
	}
	
	public static String loadResourceRaw(Class<?> clazz, String name) {
		String path = getResourcePath(clazz, name);
		InputStream stream = clazz.getClassLoader().getResourceAsStream(path);
		if (stream == null)
			throw new IllegalArgumentException("invalid ressource name: " + name
					+ ", relative to class " + clazz.getCanonicalName()
					+ " -> " + path);
		
		try {
			return loadStream(stream, UTF8);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String nl() {
		return System.getProperties().getProperty("line.separator");
	}
	
	public static String fixNL(String string) {
		return string.replaceAll("\r?\n|\r", nl());
	}

	public static String loadResource(Class<?> clazz, String name) {
		return fixNL(loadResourceRaw(clazz, name));
	}

	/** adjust a path to be relative to another directory
	 * 
	 * @param path absolute or relative path (using cwd of the process)
	 * @param new_cwd working directory the path should be relative to
	 * @return a path relative to new_cwd refering to the same object (or an absolute path)
	 */
	public static String adjustToBeRelativeTo(String path,
			String new_cwd) {
		File file = new File(path);
		
		//if (file.isAbsolute())
		//	return file;
		
		file = file.getAbsoluteFile();
		
		File cwd = new File(new_cwd).getAbsoluteFile();
		
		LinkedList<String> path_parents = Util.getParentsIncludingSelf(file);
		LinkedList<String> cwd_parents = Util.getParentsIncludingSelf(cwd);
		
		if (!path_parents.getLast().equals(cwd_parents.getLast()))
			// We have different roots (e.g. different folder on Windows)
			// -> We have to use an absolute path.
			return file.getPath();
		
		// remove common leading path elements
		while (!path_parents.isEmpty() && !cwd_parents.isEmpty()
				&& path_parents.getLast().equals(cwd_parents.getLast())) {
			path_parents.removeLast();
			cwd_parents.removeLast();
		}
		
		// construct relative path: enough ".." to get out of cwd_parents and
		// then the remaining elements of cwd_parents
		File result = new File(".");
		for (int i=0;i<cwd_parents.size();i++) {
			result = new File(result, "..");
		}
		while (!path_parents.isEmpty()) {
			String path_elem = path_parents.removeLast();
			result = new File(result, path_elem);
		}
		
		return result.getPath();
	}

	/** get a list of parent directory names, starting with the file itself and ending with the root folder (or folder on Windows) */
	private static LinkedList<String> getParentsIncludingSelf(File file) {
		LinkedList<String> parents = new LinkedList<String>();
		File next = file;
		File current;
		int ignore_elems = 0;
		do {
			current = next;
			if (current.getName().equals(".."))
				ignore_elems++;
			else if (!current.getName().equals(".")) {
				if (ignore_elems <= 0)
					parents.add(current.getName());
				else
					ignore_elems--;
			}
			
			next = current.getParentFile();
		} while (next != null && !next.equals(current));
		
		return parents;
	}
}
