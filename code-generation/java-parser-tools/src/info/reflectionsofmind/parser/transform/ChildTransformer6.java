package info.reflectionsofmind.parser.transform;

import info.reflectionsofmind.parser.node.AbstractNode;

public abstract class ChildTransformer6<T1,T2,T3,T4,T5,T6> extends AbstractChildTransformerN {
	public ChildTransformer6(Class<T1> type1, Class<T2> type2, Class<T3> type3,
			Class<T4> type4, Class<T5> type5, Class<T6> type6, Object... args) {
		super(6, new Class<?>[] { type1, type2, type3, type4, type5, type6 }, args);
	}
	
	@Deprecated
	public ChildTransformer6(Object... args) {
		super(6, null, args);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object transform(AbstractNode node, ITransform transform, Object values[]) {
		return transform((T1)values[0], (T2)values[1], (T3)values[2],
				(T4)values[3], (T5)values[4], (T6)values[5]);
	}
	
	protected abstract Object transform(T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5, T6 arg6);
}
