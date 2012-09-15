package info.reflectionsofmind.parser.transform;

import info.reflectionsofmind.parser.node.AbstractNode;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class NodePredicates {
	private NodePredicates() { }
	
	public static NodePredicate always() {
		return new NodePredicateAny() {
			@Override
			public boolean appliesTo(AbstractNode node) {
				return true;
			}
		};
	}
	
	public static NodePredicate never() {
		return new NodePredicate() {
			@SuppressWarnings("unchecked")
			@Override
			public Set<String> appliesToIDs() {
				return Collections.EMPTY_SET;
			}
			
			@Override
			public boolean appliesTo(AbstractNode node) {
				return false;
			}
		};
	}
	
	public static NodePredicate not(final NodePredicate predicate) {
		return new NodePredicateAny() {
			@Override
			public boolean appliesTo(AbstractNode node) {
				return ! predicate.appliesTo(node);
			}
		};
	}
	
	public static NodePredicate or(NodePredicate... predicates) {
		return new Union(Arrays.asList(predicates.clone()));
	}
	
	public static NodePredicate and(NodePredicate... predicates) {
		return new Intersection(Arrays.asList(predicates.clone()));
	}
	
	public static NodePredicate withIDs(Collection<String> ids) {
		if (ids.size() == 0)
			return never();
		if (ids.size() == 1) {
			final String id = ids.iterator().next();
			
			return new NodePredicate() {
				@Override
				public Set<String> appliesToIDs() {
					return Collections.singleton(id);
				}
				
				@Override
				public boolean appliesTo(AbstractNode node) {
					return id.equals(node.id);
				}
			};
		} else {
			final Set<String> id_set = Collections.unmodifiableSet(new HashSet<String>(ids));
			
			return new NodePredicate() {
				@Override
				public Set<String> appliesToIDs() {
					return id_set;
				}
				
				@Override
				public boolean appliesTo(AbstractNode node) {
					return id_set.contains(node.id);
				}
			};
		}
	}
	
	public static NodePredicate withID(String... ids) {
		return withIDs(Arrays.asList(ids));
	}
	
	private static abstract class NodePredicateAny implements NodePredicate {
		@Override
		public final Set<String> appliesToIDs() {
			return NodePredicate.ANY_ID;
		}

		public abstract boolean appliesTo(AbstractNode node);
	}

	static class Union implements NodePredicate {
		private Iterable<NodePredicate> alternatives;

		public Union(Collection<NodePredicate> alternatives) {
			this.alternatives = Collections.unmodifiableCollection(alternatives);
		}
		
		@Override
		public Set<String> appliesToIDs() {
			Set<String> ids = new HashSet<String>();
			
			for (NodePredicate pred : alternatives) {
				if (pred.appliesToIDs() == NodePredicate.ANY_ID)
					return NodePredicate.ANY_ID;
				
				ids.addAll(pred.appliesToIDs());
			}
			
			return ids;
		}
		
		@Override
		public boolean appliesTo(AbstractNode node) {
			for (NodePredicate pred : alternatives)
				if (pred.appliesTo(node))
					return true;
			
			return false;
		}

		public Iterable<NodePredicate> getAlternatives() {
			return alternatives;
		}
	}

	static class Intersection implements NodePredicate {
		private Iterable<NodePredicate> predicates;

		public Intersection(Collection<NodePredicate> alternatives) {
			this.predicates = Collections.unmodifiableCollection(alternatives);
		}
		
		@Override
		public Set<String> appliesToIDs() {
			Set<String> ids = null;
			
			boolean first = true;
			for (NodePredicate pred : predicates) {
				Set<String> pred_ids = pred.appliesToIDs();
				
				if (pred_ids == NodePredicate.ANY_ID)
					continue;
				
				if (first) {
					ids = new HashSet<String>(pred_ids);
					first = false;
				} else
					ids.retainAll(pred_ids);
			}
			
			if (first)
				return NodePredicate.ANY_ID;
			else
				return ids;
		}
		
		@Override
		public boolean appliesTo(AbstractNode node) {
			for (NodePredicate pred : predicates)
				if (!pred.appliesTo(node))
					return false;
			
			return true;
		}
	}
}
