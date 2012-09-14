package info.reflectionsofmind.parser.transform;

import info.reflectionsofmind.parser.node.AbstractNode;

public class ImmutableTransformer implements ITransform {
	private ITransform inner;

	public ImmutableTransformer(ITransform inner) {
		super();
		this.inner = inner;
	}

	public void transform(AbstractNode node) {
		inner.transform(node);
	}

	public void transform(Iterable<AbstractNode> nodes) {
		inner.transform(nodes);
	}

	public void transform(AbstractNode node, NodePredicate predicate) {
		inner.transform(node, predicate);
	}

	public void transform(Iterable<AbstractNode> nodes, NodePredicate predicate) {
		inner.transform(nodes, predicate);
	}
}
