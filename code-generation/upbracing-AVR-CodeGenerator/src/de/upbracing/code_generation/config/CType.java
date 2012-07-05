package de.upbracing.code_generation.config;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class CType {
	private static Map<String, Integer> sizeof;
	private static Pattern type_modifiers = Pattern.compile("^((signed|unsigned|const|volatile) )*");
	
	static {
		sizeof = new HashMap<String, Integer>();
		
		String byte_types[] = { "byte","char","u8","s8","uint8_t","int8_t" };
		String word_types[] = { "short","u16","s16","uint16_t","int16_t" };
		String dword_types[] = { "int","long","u32","s32","uint32_t","int32_t","float" };
		String qword_types[] = { "long long","u64","s64","uint64_t","int64_t", "double" };

		for (String type : byte_types)
			sizeof.put(type, 1);
		for (String type : word_types)
			sizeof.put(type, 2);
		for (String type : dword_types)
			sizeof.put(type, 4);
		for (String type : qword_types)
			sizeof.put(type, 8);
	}
	
	public static int getSizeOf(String type) {
		// remove type prefixes that don't change the size
		type = type_modifiers.matcher(type).replaceAll("");
		
		Integer size = sizeof.get(type);
		if (size == null)
			return -1;
		else
			return (int)size;
	}
}
