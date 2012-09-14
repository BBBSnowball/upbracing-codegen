package info.reflectionsofmind.parser.transform;

import info.reflectionsofmind.parser.node.AbstractNode;

public interface ITransform {
	void transform(AbstractNode node);
	void transform(Iterable<AbstractNode> nodes);
	void transform(AbstractNode node, NodePredicate predicate);
	void transform(Iterable<AbstractNode> nodes, NodePredicate predicate);
}
