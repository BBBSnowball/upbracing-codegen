package info.reflectionsofmind.parser.transform;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import info.reflectionsofmind.parser.node.AbstractNode;

public class NodePredicateMap<V> {
	//NOTE I'd like to use a LinkedHashMultimap, but I don't want to add Guava as a dependency.
	private Map<String, LinkedList<Entry>> values = new HashMap<String, LinkedList<Entry>>();
	
	private LinkedList<Entry> values_for_any_id = new LinkedList<Entry>();
	
	public NodePredicateMap() { }
	
	private void add(NodePredicate predicate, V value, boolean last) {
		// special case for union of predicates: add the alternatives
		if (predicate instanceof NodePredicates.Union) {
			for (NodePredicate alternative : ((NodePredicates.Union)predicate).getAlternatives())
				add(alternative, value, last);
			return;
		}
		
		Entry entry = new Entry(predicate, value);
		Set<String> ids = predicate.appliesToIDs();
		if (ids == NodePredicate.ANY_ID)
			values_for_any_id.add(entry);
		else {
			for (String name : ids) {
				LinkedList<Entry> entries = values.get(name);
				if (entries == null) {
					entries = new LinkedList<NodePredicateMap<V>.Entry>();
					values.put(name, entries);
				}
				
				if (last)
					entries.addLast(entry);
				else
					entries.addFirst(entry);
			}
		}
	}

	public void addFirst(NodePredicate predicate, V value) {
		add(predicate, value, false);
	}

	public void add(NodePredicate predicate, V value) {
		add(predicate, value, true);
	}

	public void add(NodePredicateMap<V> transformers) {
		values_for_any_id.addAll(transformers.values_for_any_id);
		
		HashSet<Entry> added = new HashSet<Entry>();
		for (java.util.Map.Entry<String, LinkedList<Entry>> entry : transformers.values.entrySet()) {
			for (Entry e2 : entry.getValue()) {
				if (added.contains(e2))
					continue;
				added.add(e2);
				
				add(e2.predicate, e2.value);
			}
		}
	}
	
	public List<V> get(AbstractNode node) {
		LinkedList<V> results = new LinkedList<V>();

		Collection<Entry> entries = values.get(node.id);
		if (entries != null)
			addMatchingResults(node, entries, results);
		
		entries = values_for_any_id;
		if (entries != null)
			addMatchingResults(node, entries, results);
		
		return results;
	}

	private void addMatchingResults(AbstractNode node,
			Collection<Entry> entries, LinkedList<V> results) {
		for (Entry e : entries) {
			if (e.predicate.appliesTo(node))
				results.add(e.value);
		}
	}
	
	public V getUnique(AbstractNode node) {
		return getUniqueOrFirst(node, true, true);
	}
	
	public V getFirst(AbstractNode node) {
		return getUniqueOrFirst(node, true, true);
	}
	
	public V getFirstOrNull(AbstractNode node) {
		return getUniqueOrFirst(node, true, false);
	}
	
	private V getUniqueOrFirst(AbstractNode node, boolean unique, boolean required) {
		boolean first = true;
		V result = null;
		
		Collection<Entry> entries = values.get(node.id);
		if (entries != null) {
			for (Entry e : entries) {
				if (e.predicate.appliesTo(node)) {
					if (!unique)
						return e.value;
					else {
						if (!first && result != e.value)
							throw new IllegalStateException("More than one value for this node");
						else {
							first = false;
							result = e.value;
						}
					}
				}
			}
		}
		
		entries = values_for_any_id;
		if (entries != null) {
			for (Entry e : entries) {
				if (e.predicate.appliesTo(node)) {
					if (!unique)
						return e.value;
					else {
						if (!first && result != e.value)
							throw new IllegalStateException("More than one value for this node");
						else {
							first = false;
							result = e.value;
						}
					}
				}
			}
		}
		
		if (required && (!unique || first))
			throw new IllegalStateException("No value for this node");
		
		return result;
	}

	private class Entry {
		public NodePredicate predicate;
		public V value;
		
		public Entry(NodePredicate predicate, V value) {
			super();
			this.predicate = predicate;
			this.value = value;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((predicate == null) ? 0 : predicate.hashCode());
			result = prime * result + ((value == null) ? 0 : value.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			
			@SuppressWarnings("unchecked")
			Entry other = (Entry) obj;
			return other.predicate == predicate && other.value == value;
		}
	}
}
