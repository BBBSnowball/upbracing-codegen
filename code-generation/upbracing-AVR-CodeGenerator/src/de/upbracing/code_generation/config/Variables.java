package de.upbracing.code_generation.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.simpleframework.xml.Default;

@Default
public abstract class Variables<VAR extends Variable> implements Map<String, VAR>, Iterable<VAR> {
	protected TreeMap<String, VAR> vars = new TreeMap<String, VAR>();
	protected ArrayList<VAR> order = new ArrayList<VAR>();

	public Variables(boolean keep_order) {
		if (keep_order)
			order = new ArrayList<VAR>();
	}
	
	public boolean add(VAR v) {
		if (vars.containsKey(v.getName()))
			throw new IllegalStateException("There already is a variable with the same name.");
		vars.put(v.getName(), v);
		if (order != null)
			return order.add(v);
		else
			return true;
	}
	
	public boolean add(int index, VAR v) {
		if (order == null)
			throw new UnsupportedOperationException("only supported by ordered lists of variables.");
		
		if (vars.containsKey(v.getName()))
			throw new IllegalStateException("There already is a variable with the same name.");
		vars.put(v.getName(), v);
		if (order != null)
			order.add(index, v);
		
		return true;
	}
	
	public boolean addAll(Collection<? extends VAR> vars) {
		for (VAR v : vars)
			add(v);
		return true;
	}

	public void clear() {
		vars.clear();
		if (order != null)
			order.clear();
	}
	
	public boolean contains(Object obj) {
		return (obj instanceof Variable) && (vars.get(((Variable)obj).getName()) == obj);
	}

	public boolean containsKey(Object key) {
		return vars.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return contains(value);
	}

	public VAR get(Object key) {
		return vars.get(key);
	}
	
	public VAR get(int index) {
		if (order != null)
			return order.get(index);
		else
			throw new UnsupportedOperationException("only supported by ordered lists of variables.");
	}
	
	public int indexOf(Object obj) {
		if (order != null)
			return order.indexOf(obj);
		else
			throw new UnsupportedOperationException("only supported by ordered lists of variables.");
	}

	public boolean isEmpty() {
		return vars.isEmpty();
	}

	public Iterator<VAR> iterator() {
		return values().iterator();
	}

	public VAR remove(int index) {
		if (order != null) {
			VAR var = order.remove(index);
			if (var != null)
				vars.remove(var.getName());
			return var;
		} else
			throw new UnsupportedOperationException("only supported by ordered lists of variables.");
	}

	public VAR remove(Object key) {
		VAR var = vars.remove(key);
		if (var != null && order != null)
			order.remove(var);
		return var;
	}
	
	public VAR set(int index, VAR value) {
		if (order == null)
			throw new UnsupportedOperationException("only supported by ordered lists of variables.");
		
		VAR old = remove(index);
		add(index, value);
		
		return old;
	}

	public int size() {
		return vars.size();
	}

	public VAR put(String key, VAR value) {
		if (!key.equals(value.getName()))
			throw new IllegalArgumentException("The name of the variable must match the key.");
		add(value);
		return value;
	}

	public void putAll(Map<? extends String, ? extends VAR> map) {
		for (java.util.Map.Entry<? extends String, ? extends VAR> e : map.entrySet())
			put(e.getKey(), e.getValue());
	}

	public Set<java.util.Map.Entry<String, VAR>> entrySet() {
		return Collections.unmodifiableSet(vars.entrySet());
	}

	public Set<String> keySet() {
		return Collections.unmodifiableSet(vars.keySet());
	}

	public Collection<VAR> values() {
		if (order != null)
			return Collections.unmodifiableCollection(order);
		else
			return Collections.unmodifiableCollection(vars.values());
	}
}
