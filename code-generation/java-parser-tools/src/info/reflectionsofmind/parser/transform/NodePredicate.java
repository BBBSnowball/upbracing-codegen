package info.reflectionsofmind.parser.transform;

import info.reflectionsofmind.parser.node.AbstractNode;

import java.util.Collections;
import java.util.Set;

public interface NodePredicate {
	/** Can be returned by {@link #appliesToIDs()}. Treated like a list of all existing IDs
	 * 
	 * Use the '==' operator to compare it. It is not the same as any other Set with the same content!
	 */
	static final Set<String> ANY_ID = Collections.singleton("------ any name ------");
	
	/** list of IDs that this predicate can apply to
	 * 
	 * This can be used to find matching predicates faster.
	 * {@link #appliesTo(AbstractNode)} must return false, if
	 * {@code node.id} is not in the list.
	 * 
	 * @return the list
	 */
	Set<String> appliesToIDs();
	
	/** does this predicate apply to the node
	 * 
	 * @param node the node to test
	 * @return the result
	 */
	boolean appliesTo(AbstractNode node);
}
