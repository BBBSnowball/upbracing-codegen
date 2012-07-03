package de.upbracing.code_generation.config;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.simpleframework.xml.Default;

@Default
public abstract class Variables<VAR extends Variable> implements Map<String, VAR> {
	protected TreeMap<String, VAR> vars = new TreeMap<String, VAR>();
	
	public void add(VAR v) {
		put(v.getName(), v);
	}

	public void clear() {
		vars.clear();
	}

	public Object clone() {
		return vars.clone();
	}

	public boolean containsKey(Object key) {
		return vars.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return vars.containsValue(value);
	}

	public VAR get(Object key) {
		return vars.get(key);
	}

	public boolean isEmpty() {
		return vars.isEmpty();
	}

	public int size() {
		return vars.size();
	}

	public VAR put(String key, VAR value) {
		if (!key.equals(value.getName()))
			throw new IllegalArgumentException("The name of the variable must match the key.");
		return vars.put(key, value);
	}

	public void putAll(Map<? extends String, ? extends VAR> map) {
		for (java.util.Map.Entry<? extends String, ? extends VAR> e : map.entrySet()) {
			if (!e.getKey().equals(e.getValue().getName()))
				throw new IllegalArgumentException("The name of the variable must match the key.");
		}
		vars.putAll(map);
	}

	public Set<java.util.Map.Entry<String, VAR>> entrySet() {
		return vars.entrySet();
	}

	public Set<String> keySet() {
		return vars.keySet();
	}

	public VAR remove(Object key) {
		return vars.remove(key);
	}

	public Collection<VAR> values() {
		return vars.values();
	}
}
