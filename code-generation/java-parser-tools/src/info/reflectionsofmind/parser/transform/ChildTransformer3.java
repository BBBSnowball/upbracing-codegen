package info.reflectionsofmind.parser.transform;

import info.reflectionsofmind.parser.node.AbstractNode;

public abstract class ChildTransformer3<T1,T2,T3> extends AbstractChildTransformerN {
	public ChildTransformer3(Class<T1> type1, Class<T2> type2, Class<T3> type3, Object... args) {
		super(3, new Class<?>[] { type1, type2, type3 }, args);
	}
	
	@Deprecated
	public ChildTransformer3(Object... args) {
		super(3, null, args);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object transform(AbstractNode node, ITransform transform, Object values[]) {
		return transform((T1)values[0], (T2)values[1], (T3)values[2]);
	}
	
	protected abstract Object transform(T1 arg1, T2 arg2, T3 arg3);
}
