package info.reflectionsofmind.parser.transform;

import info.reflectionsofmind.parser.node.AbstractNode;

public abstract class ChildTransformer1<T1> extends AbstractChildTransformerN {
	public ChildTransformer1(Class<T1> type1, Object... args) {
		super(1, new Class<?>[] { type1 }, args);
	}
	
	@Deprecated
	public ChildTransformer1(Object... args) {
		super(1, null, args);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object transform(AbstractNode node, ITransform transform, Object values[]) {
		return transform((T1)values[0]);
	}
	
	protected abstract Object transform(T1 arg1);
}
