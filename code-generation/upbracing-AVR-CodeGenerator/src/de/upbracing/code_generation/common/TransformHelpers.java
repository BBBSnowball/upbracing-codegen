package de.upbracing.code_generation.common;

import info.reflectionsofmind.parser.node.AbstractNode;
import info.reflectionsofmind.parser.node.NamedNode;
import info.reflectionsofmind.parser.node.Navigation;
import info.reflectionsofmind.parser.transform.AbstractTransformer;
import info.reflectionsofmind.parser.transform.ChildTransformer1;
import info.reflectionsofmind.parser.transform.ITransform;
import info.reflectionsofmind.parser.transform.NodePredicate;
import info.reflectionsofmind.parser.transform.NodePredicates;
import info.reflectionsofmind.parser.transform.Transform;

import java.util.ArrayList;
import java.util.List;

public final class TransformHelpers {
	private TransformHelpers() { }

	public static void transformChoice(Transform transform, String... ids) {
		transform.add(new AbstractTransformer(ids) {
			@Override
			public void transform(AbstractNode node, ITransform transform) {
				transform.transform(node.children);
				
				List<NamedNode> children = Navigation.getNamedChildren(node);
				assert children.size() == 1;
				
				node.value = children.get(0).value;
			}
		});
	}

	public static void transformUseTextAsValue(Transform transform, String... ids) {
		transform.add(new AbstractTransformer(ids) {
			@Override
			public void transform(AbstractNode node, ITransform transform) {
				node.value = node.getText();
			}
		});
	}

	public static void transformToList(Transform transform, String node_id,
			String... children_ids) {
		final NodePredicate predicate = NodePredicates.withID(children_ids);
		transform.add(new AbstractTransformer(node_id) {
			@Override
			public void transform(AbstractNode node, ITransform transform) {
				transform.transform(node.children);
				
				List<AbstractNode> children = node.findChildren(predicate);
				ArrayList<Object> values = new ArrayList<Object>();
				for (AbstractNode child : children)
					values.add(child.value);
				
				node.value = values;
			}
		});
	}

	public static void transformCopyValueFromChild(Transform transform, String... node_and_child_ids) {
		transform.add(new ChildTransformer1<Object>(Object.class, (Object[])node_and_child_ids) {
			@Override
			protected Object transform(Object child_value) {
				return child_value;
			}
		});
	}

}
