package de.upbracing.code_generation;

import java.util.Map;

public interface IGenerator {
	String getName();
	Map<String, ITemplate> getFiles();
}
