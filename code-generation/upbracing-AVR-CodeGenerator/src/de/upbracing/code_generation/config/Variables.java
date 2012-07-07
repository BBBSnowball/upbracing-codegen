package de.upbracing.code_generation.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.simpleframework.xml.Default;

/**
 * A list of variables. Base class for several lists of variables used in the configuration.
 * 
 * This class should implement List<VAR>, but unfortunately some methods conflict with the
 * Map interface.
 * 
 * @author benny
 *
 * @param <VAR> type of variables to store
 */
@Default
public abstract class Variables<VAR extends Variable> implements Map<String, VAR>, Iterable<VAR> {
	protected TreeMap<String, VAR> vars = new TreeMap<String, VAR>();
	protected ArrayList<VAR> order = new ArrayList<VAR>();

	/** constructor
	 * 
	 * @param keep_order should the order of the variables be maintained
	 */
	public Variables(boolean keep_order) {
		if (keep_order)
			order = new ArrayList<VAR>();
	}
	
	/** @see java.util.List#add(Object) */
	public boolean add(VAR v) {
		if (vars.containsKey(v.getName()))
			throw new IllegalStateException("There already is a variable with the same name.");
		vars.put(v.getName(), v);
		if (order != null)
			return order.add(v);
		else
			return true;
	}

	/** @see java.util.List#add(int, Object) */
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

	/** @see java.util.List#addAll(Collection) */
	public boolean addAll(Collection<? extends VAR> vars) {
		for (VAR v : vars)
			add(v);
		return true;
	}

	@Override
	public void clear() {
		vars.clear();
		if (order != null)
			order.clear();
	}

	/** @see java.util.List#contains(Object) */
	public boolean contains(Object obj) {
		return (obj instanceof Variable) && (vars.get(((Variable)obj).getName()) == obj);
	}

	@Override
	public boolean containsKey(Object key) {
		return vars.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return contains(value);
	}

	@Override
	public VAR get(Object key) {
		return vars.get(key);
	}
	
	/** @see java.util.List#get(int) */
	public VAR get(int index) {
		if (order != null)
			return order.get(index);
		else
			throw new UnsupportedOperationException("only supported by ordered lists of variables.");
	}

	/** @see java.util.List#indexOf(Object) */
	public int indexOf(Object obj) {
		if (order != null)
			return order.indexOf(obj);
		else
			throw new UnsupportedOperationException("only supported by ordered lists of variables.");
	}

	@Override
	public boolean isEmpty() {
		return vars.isEmpty();
	}

	@Override
	public Iterator<VAR> iterator() {
		return values().iterator();
	}

	/** @see java.util.List#remove(int) */
	public VAR remove(int index) {
		if (order != null) {
			VAR var = order.remove(index);
			if (var != null)
				vars.remove(var.getName());
			return var;
		} else
			throw new UnsupportedOperationException("only supported by ordered lists of variables.");
	}

	/** @see java.util.List#remove(Object) */
	public VAR remove(Object key) {
		VAR var = vars.remove(key);
		if (var != null && order != null)
			order.remove(var);
		return var;
	}

	/** @see java.util.List#set(int, Object) */
	public VAR set(int index, VAR value) {
		if (order == null)
			throw new UnsupportedOperationException("only supported by ordered lists of variables.");
		
		VAR old = remove(index);
		add(index, value);
		
		return old;
	}

	@Override
	public int size() {
		return vars.size();
	}

	@Override
	public VAR put(String key, VAR value) {
		if (!key.equals(value.getName()))
			throw new IllegalArgumentException("The name of the variable must match the key.");
		add(value);
		return value;
	}

	@Override
	public void putAll(Map<? extends String, ? extends VAR> map) {
		for (java.util.Map.Entry<? extends String, ? extends VAR> e : map.entrySet())
			put(e.getKey(), e.getValue());
	}

	@Override
	public Set<java.util.Map.Entry<String, VAR>> entrySet() {
		return Collections.unmodifiableSet(vars.entrySet());
	}

	@Override
	public Set<String> keySet() {
		return Collections.unmodifiableSet(vars.keySet());
	}

	@Override
	public Collection<VAR> values() {
		if (order != null)
			return Collections.unmodifiableCollection(order);
		else
			return Collections.unmodifiableCollection(vars.values());
	}
}
