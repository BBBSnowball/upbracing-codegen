package de.upbracing.dbc;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.simpleframework.xml.Default;

@Default
public class DBCValueTable implements SortedMap<String, String> {
	private TreeMap<String, String> map;
	
	public DBCValueTable() {
		map = new TreeMap<String, String>();
	}

	public java.util.Map.Entry<String, String> ceilingEntry(String key) {
		return map.ceilingEntry(key);
	}

	public String ceilingKey(String key) {
		return map.ceilingKey(key);
	}

	public void clear() {
		map.clear();
	}

	public Object clone() {
		return map.clone();
	}

	public Comparator<? super String> comparator() {
		return map.comparator();
	}

	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	public NavigableSet<String> descendingKeySet() {
		return map.descendingKeySet();
	}

	public NavigableMap<String, String> descendingMap() {
		return map.descendingMap();
	}

	public Set<java.util.Map.Entry<String, String>> entrySet() {
		return map.entrySet();
	}

	public boolean equals(Object arg0) {
		return map.equals(arg0);
	}

	public java.util.Map.Entry<String, String> firstEntry() {
		return map.firstEntry();
	}

	public String firstKey() {
		return map.firstKey();
	}

	public java.util.Map.Entry<String, String> floorEntry(String key) {
		return map.floorEntry(key);
	}

	public String floorKey(String key) {
		return map.floorKey(key);
	}

	public String get(Object key) {
		return map.get(key);
	}

	public int hashCode() {
		return map.hashCode();
	}

	public NavigableMap<String, String> headMap(String toKey, boolean inclusive) {
		return map.headMap(toKey, inclusive);
	}

	public SortedMap<String, String> headMap(String toKey) {
		return map.headMap(toKey);
	}

	public java.util.Map.Entry<String, String> higherEntry(String key) {
		return map.higherEntry(key);
	}

	public String higherKey(String key) {
		return map.higherKey(key);
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public Set<String> keySet() {
		return map.keySet();
	}

	public java.util.Map.Entry<String, String> lastEntry() {
		return map.lastEntry();
	}

	public String lastKey() {
		return map.lastKey();
	}

	public java.util.Map.Entry<String, String> lowerEntry(String key) {
		return map.lowerEntry(key);
	}

	public String lowerKey(String key) {
		return map.lowerKey(key);
	}

	public NavigableSet<String> navigableKeySet() {
		return map.navigableKeySet();
	}

	public java.util.Map.Entry<String, String> pollFirstEntry() {
		return map.pollFirstEntry();
	}

	public java.util.Map.Entry<String, String> pollLastEntry() {
		return map.pollLastEntry();
	}

	public String put(String key, String value) {
		return map.put(key, value);
	}

	public void putAll(Map<? extends String, ? extends String> map) {
		this.map.putAll(map);
	}

	public String remove(Object key) {
		return map.remove(key);
	}

	public int size() {
		return map.size();
	}

	public NavigableMap<String, String> subMap(String fromKey,
			boolean fromInclusive, String toKey, boolean toInclusive) {
		return map.subMap(fromKey, fromInclusive, toKey, toInclusive);
	}

	public SortedMap<String, String> subMap(String fromKey, String toKey) {
		return map.subMap(fromKey, toKey);
	}

	public NavigableMap<String, String> tailMap(String fromKey,
			boolean inclusive) {
		return map.tailMap(fromKey, inclusive);
	}

	public SortedMap<String, String> tailMap(String fromKey) {
		return map.tailMap(fromKey);
	}

	public String toString() {
		return map.toString();
	}

	public Collection<String> values() {
		return map.values();
	}

}
