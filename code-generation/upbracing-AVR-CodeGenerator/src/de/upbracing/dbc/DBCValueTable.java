package de.upbracing.dbc;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.simpleframework.xml.Default;

/**
 * value table definition in a DBC file
 * 
 * Map with pairs of raw value and name/meaning.
 * 
 * @author benny
 */
@Default
public class DBCValueTable implements SortedMap<String, String> {
	private TreeMap<String, String> map;
	
	/** constructor */
	public DBCValueTable() {
		map = new TreeMap<String, String>();
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public Object clone() {
		return map.clone();
	}

	@Override
	public Comparator<? super String> comparator() {
		return map.comparator();
	}

	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	@Override
	public Set<java.util.Map.Entry<String, String>> entrySet() {
		return map.entrySet();
	}

	@Override
	public String firstKey() {
		return map.firstKey();
	}
	
	@Override
	public String get(Object key) {
		return map.get(key);
	}

	@Override
	public SortedMap<String, String> headMap(String toKey) {
		return map.headMap(toKey);
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public Set<String> keySet() {
		return map.keySet();
	}

	@Override
	public String lastKey() {
		return map.lastKey();
	}

	@Override
	public String put(String key, String value) {
		return map.put(key, value);
	}

	@Override
	public void putAll(Map<? extends String, ? extends String> map) {
		this.map.putAll(map);
	}

	@Override
	public String remove(Object key) {
		return map.remove(key);
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public SortedMap<String, String> subMap(String fromKey, String toKey) {
		return map.subMap(fromKey, toKey);
	}

	@Override
	public SortedMap<String, String> tailMap(String fromKey) {
		return map.tailMap(fromKey);
	}

	@Override
	public String toString() {
		return map.toString();
	}

	@Override
	public Collection<String> values() {
		return map.values();
	}

}
