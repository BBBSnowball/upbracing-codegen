package info.reflectionsofmind.parser.transform;

import info.reflectionsofmind.parser.node.AbstractNode;

public abstract class ChildTransformer2<T1,T2> extends AbstractChildTransformerN {
	public ChildTransformer2(Class<T1> type1, Class<T2> type2, Object... args) {
		super(2, new Class<?>[] { type1, type2 }, args);
	}
	
	@Deprecated
	public ChildTransformer2(Object... args) {
		super(2, null, args);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object transform(AbstractNode node, ITransform transform, Object values[]) {
		return transform((T1)values[0], (T2)values[1]);
	}
	
	protected abstract Object transform(T1 arg1, T2 arg2);
}
