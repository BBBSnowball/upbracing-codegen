package info.reflectionsofmind.parser.transform;

import info.reflectionsofmind.parser.node.AbstractNode;

public abstract class AbstractTransformer implements Transformer {
	private NodePredicate predicate;
	
	public AbstractTransformer(NodePredicate predicate) {
		this.predicate = predicate;
	}

	public AbstractTransformer(String... ids) {
		this.predicate = NodePredicates.withID(ids);
	}

	@Override
	public final NodePredicate getPredicate() {
		return predicate;
	}

	@Override
	public abstract void transform(AbstractNode node, ITransform transform);

}
