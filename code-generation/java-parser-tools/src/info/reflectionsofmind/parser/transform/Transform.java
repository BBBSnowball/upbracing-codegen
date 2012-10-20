package info.reflectionsofmind.parser.transform;

import info.reflectionsofmind.parser.node.AbstractNode;

import java.util.Arrays;
import java.util.List;

public class Transform implements ITransform {
	private NodePredicateMap<Transformer> transformers = new NodePredicateMap<Transformer>();
	private ITransform immutable_self = new ImmutableTransformer(this);
	
	public Transform(Transformer... transformers) {
		addAll(Arrays.asList(transformers));
	}

	public void add(Transformer transformer) {
		this.transformers.add(transformer.getPredicate(), transformer);
	}
	
	public void add(Transform transform) {
		this.transformers.add(transform.transformers);
	}

	private void addAll(List<Transformer> transformers) {
		for (Transformer transformer : transformers)
			add(transformer);
	}

	@Override
	public void transform(AbstractNode node) {
		transform(node, NodePredicates.always());
	}

	@Override
	public void transform(Iterable<AbstractNode> nodes) {
		transform(nodes, NodePredicates.always());
	}

	@Override
	public void transform(Iterable<AbstractNode> nodes, NodePredicate predicate) {
		for (AbstractNode node : nodes)
			transform(node, predicate);
	}

	@Override
	public void transform(AbstractNode node, NodePredicate predicate) {
		if (! predicate.appliesTo(node))
			return;
		
		Transformer transformer = transformers.getFirstOrNull(node);
		if (transformer != null)
			transformer.transform(node, immutable_self);
		else
			transform(node.children);
	}
	
	public ITransform asImmutable() {
		return immutable_self;
	}
}
