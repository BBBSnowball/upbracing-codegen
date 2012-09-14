package info.reflectionsofmind.parser.transform;

import info.reflectionsofmind.parser.node.AbstractNode;

public abstract class ChildTransformer5<T1,T2,T3,T4,T5> extends AbstractChildTransformerN {
	public ChildTransformer5(Class<T1> type1, Class<T2> type2, Class<T3> type3,
			Class<T4> type4, Class<T5> type5, Object... args) {
		super(5, new Class<?>[] { type1, type2, type3, type4, type5 }, args);
	}
	
	@Deprecated
	public ChildTransformer5(Object... args) {
		super(5, null, args);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object transform(AbstractNode node, ITransform transform, Object values[]) {
		return transform((T1)values[0], (T2)values[1], (T3)values[2],
				(T4)values[3], (T5)values[4]);
	}
	
	protected abstract Object transform(T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5);
}
