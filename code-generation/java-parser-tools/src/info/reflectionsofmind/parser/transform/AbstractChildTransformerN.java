package info.reflectionsofmind.parser.transform;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import info.reflectionsofmind.parser.node.AbstractNode;
import info.reflectionsofmind.parser.node.Navigation;

public abstract class AbstractChildTransformerN implements Transformer {
	private NodePredicate predicate;
	private NodePredicate child_predicate;
	private String child_ids[];
	private Class<?> value_classes[];
	private boolean use_find_named;

	public AbstractChildTransformerN(int n, Class<?> types[], Object args[]) {
		boolean have_predicate = false;
		List<String> at_nodes_with_id = new LinkedList<String>();
		final Set<String> not_children_with_id = new HashSet<String>();
		child_ids = new String[n];
		int child_i = 0;
		int cls_i = 0;
		if (types != null) {
			if (types.length != n)
				throw new IllegalArgumentException("there must be n elements in types");
			value_classes = types;
			cls_i = n;
		} else
			value_classes = new Class[n];
		
		for (Object arg_ : args) {
			if (arg_ instanceof String) {
				String arg = (String) arg_;
				
				if (arg.startsWith("~"))
					not_children_with_id.add(arg.substring(1));
				else if (arg.startsWith(">") || arg.equals("*") || arg.startsWith("?>")) {
					if (child_i >= n)
						throw new IllegalArgumentException("too many child IDs");
					child_ids[child_i++] = arg.substring(1);
				} else
					at_nodes_with_id.add(arg);
			} else if (arg_ instanceof Class) {
				if (child_i >= n)
					throw new IllegalArgumentException("too many classes");
				value_classes[++cls_i] = (Class<?>) arg_;
			} else if (arg_ instanceof NodePredicate) {
				if (have_predicate)
					throw new IllegalArgumentException("only one predicate allowed");
				predicate = (NodePredicate) arg_;
				have_predicate = true;
			} else
				throw new IllegalArgumentException("unexpected argument of type " + arg_.getClass().getName());
		}
		
		if (have_predicate) {
			if (!at_nodes_with_id.isEmpty())
				throw new IllegalArgumentException("You must give me either IDs ('@...') or a NodePredicate, but not both");
		} else {
			if (at_nodes_with_id.isEmpty())
				predicate = NodePredicates.always();
			else
				predicate = NodePredicates.withIDs(at_nodes_with_id);
		}
		
		if (!not_children_with_id.isEmpty())
			child_predicate = new NodePredicate() {
				@Override
				public Set<String> appliesToIDs() {
					return NodePredicate.ANY_ID;
				}
				
				@Override
				public boolean appliesTo(AbstractNode node) {
					return node.id != null && !node.id.equals("") && !node.id.startsWith("#")
							&& !not_children_with_id.contains(node.id);
				}
			};
		
		if (child_i == 0) {
			for (int i=0;i<child_ids.length;i++)
				child_ids[i] = "*";
			use_find_named = true;
		} else if (child_i != n)
			throw new IllegalArgumentException("wrong count of child IDs: " + child_i + " instead of " + n);
		else {
			use_find_named = false;
			for (String child_id : child_ids) {
				if (child_id.startsWith(">>")) {
					int count = 0;
					for (String id2 : child_ids)
						if (id2 == child_id)
							++count;
					if (count > 1)
						throw new IllegalArgumentException("with a child ID like '>>...' you find the same node every time, but '" + child_id + "' is used " + count + " times");
				} else {
					use_find_named = true;
					break;
				}
			}
		}
		
		if (cls_i == 0)
			value_classes = null;
		else if (cls_i != n)
			throw new IllegalArgumentException("wrong count of value types: " + cls_i + " instead of " + n);
	}

	@Override
	public final NodePredicate getPredicate() {
		return predicate;
	}

	@Override
	public void transform(AbstractNode root, ITransform transform) {
		transform.transform(root.children);
		
		Object values[] = new Object[child_ids.length];
		
		List<? extends AbstractNode> named = null;
		if (use_find_named) {
			if (child_predicate != null)
				named = root.findChildren(child_predicate);
			else
				named = Navigation.getNamedChildren(root);
		}
		
		for (int i=0;i<child_ids.length;i++) {
			String spec = child_ids[i];
			AbstractNode node;
			List<AbstractNode> nodes = null;
			boolean list = false;
			boolean optional = false;
			
			if (spec.startsWith("?")) {
				optional = true;
				spec = spec.substring(1);
			}
			
			if (spec.equals("*") || spec.equals(">*")) {
				node = named.get(0);
				named.remove(0);
			} else if (spec.startsWith(">>>")) {
				list = true;
				nodes = Navigation.findAllDecendentsById(root, spec.substring(3));
				node = null;
			} else if (spec.startsWith(">>")) {
				node = Navigation.findDecendentById(root, spec.substring(3));
			} else if (spec.startsWith(">")) {
				spec = spec.substring(1);
				Iterator<? extends AbstractNode> it = named.iterator();
				node = null;
				while (it.hasNext()) {
					AbstractNode node2 = it.next();
					if (node2.id.equals(spec)) {
						node = node2;
						break;
					}
				}
			} else
				throw new RuntimeException("should never get here");
			
			if (!optional && node == null && nodes == null)
				throw new IllegalStateException("couldn't find child #" + (i+1) + ": " + child_ids[i]);
			
			Object value;
			if (value_classes != null && AbstractNode.class.isAssignableFrom(value_classes[i])) {
				if (list) {
					value = nodes;
					
					if (nodes != null) {
						for (Object obj : nodes) {
							if (!value_classes[i].isAssignableFrom(obj.getClass()))
								throw new IllegalStateException("element has wrong type " + obj.getClass().getName());
						}
					}
				} else {
					value = node;

					if (node != null && !value_classes[i].isAssignableFrom(value.getClass()))
						throw new IllegalStateException("object has wrong type " + value.getClass().getName());
				}
			} else if (list && nodes != null) {
				List<Object> objs = new ArrayList<Object>(nodes.size());
				value = objs;
				
				for (AbstractNode node2 : nodes) {
					if (!optional && node2.value == null)
						throw new IllegalStateException("element in list is null");

					if (node2.value != null && !value_classes[i].isAssignableFrom(node2.value.getClass()))
						throw new IllegalStateException("element has wrong type " + node2.value.getClass().getName());
					
					objs.add(node2.value);
				}
			} else {
				value = node;

				if (node != null && !value_classes[i].isAssignableFrom(value.getClass()))
					throw new IllegalStateException("object has wrong type " + value.getClass().getName());
			}
			
			values[i] = value;
		}
		
		transform(root, transform, values);
	}

	protected abstract void transform(AbstractNode node, ITransform transform, Object values[]);
}
