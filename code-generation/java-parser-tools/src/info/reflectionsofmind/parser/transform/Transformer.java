package info.reflectionsofmind.parser.transform;

import info.reflectionsofmind.parser.node.AbstractNode;

public interface Transformer {
	NodePredicate getPredicate();
	void transform(AbstractNode node, ITransform transform);
}
