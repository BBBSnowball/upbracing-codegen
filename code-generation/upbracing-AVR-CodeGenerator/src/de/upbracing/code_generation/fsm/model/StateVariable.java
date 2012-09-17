package de.upbracing.code_generation.fsm.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Statecharts.StateParent;
import Statecharts.StateScope;

public class StateVariable implements Comparable<StateVariable> {
	public static final String TYPE_AUTONAME = "--- new type with automatic name ---";
	
	private static int next_unique_id = 1;
	private static final Map<String, StateVariable> variables_with_temporary_name = new HashMap<String, StateVariable>();
	private synchronized String getUniqueTemporaryName() {
		String name = "$<temp" + next_unique_id + ">";
		next_unique_id++;
		variables_with_temporary_name.put(name, this);
		return name;
	}
	
	public static StateVariable getVariableByTemporaryID(String id) {
		return variables_with_temporary_name.get(id);
	}
	
	public static String replaceTemporaryVariables(String text) {
		Matcher m = Pattern.compile("\\$<temp[0-9]+>").matcher(text);
		 StringBuffer sb = new StringBuffer();
		 int append_pos = 0;
		 while (m.find()) {
		     //m.appendReplacement(sb, "...");
			 sb.append(text, append_pos, m.start());
			 append_pos = m.end();
			 
			 String matched_text = m.group(0);
			 StateVariable var = getVariableByTemporaryID(matched_text);
			 if (var != null)
				 sb.append(var.getRealName());
			 else
				 sb.append(matched_text);
		 }
		 //m.appendTail(sb);
		 sb.append(text, append_pos, text.length());
		 return sb.toString();
	}

	/** the name */
	private String name;
	
	/** c type of this variable; can be null, if the declaration is present */
	private String type;
	
	/** declaration of the type (without typedef and name) or null */
	private String declaration;
	
	/** the variable is visible within this scope, used for information hiding */
	private final Set<StateScope> visibility_scope;
	
	/** the variable may loose its value, if it leaves this scope, used to optimize memory footprint */
	private final Set<StateScope> lifetime_scope;
	
	/** a name that you can use in the C program to access the variable */
	private String real_name;

	public StateVariable(String name, String type, String declaration,
			Collection<StateScope> visibility_scope,
			Collection<StateScope> lifetime_scope) {
		this.name = name;
		this.type = type;
		this.declaration = declaration;
		this.visibility_scope = Collections.unmodifiableSet(
				optimizeScope(new HashSet<StateScope>(visibility_scope)));
		this.lifetime_scope = Collections.unmodifiableSet(
				optimizeScope(new HashSet<StateScope>(lifetime_scope)));
	}

	public StateVariable(String name, String type, String declaration,
			StateScope... scopes) {
		this.name = name;
		this.type = type;
		this.declaration = declaration;
		this.visibility_scope = Collections.unmodifiableSet(
				optimizeScope(new HashSet<StateScope>(Arrays.asList(scopes))));
		this.lifetime_scope = visibility_scope;
	}
	
	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDeclaration() {
		return declaration;
	}

	public void setDeclaration(String declaration) {
		this.declaration = declaration;
	}

	public Set<StateScope> getVisibilityScope() {
		return visibility_scope;
	}

	public Set<StateScope> getLifetimeScope() {
		return lifetime_scope;
	}

	public String getRealName() {
		if (real_name == null)
			real_name = getUniqueTemporaryName();
		
		return real_name;
	}

	// should only be set by StateVariables.process()
	void setRealName(String real_name) {
		this.real_name = real_name;
	}

	/** remove superfluous elements from the scope set without changing its meaning
	 * 
	 * Precondition: All states in those sets have correct parent values. Use
	 * {@link StateMachineForGeneration#updateParents()} to make sure of this.
	 * 
	 * @return the set; nevertheless, the method modifies the argument!
	 */
	private static Set<StateScope> optimizeScope(Set<StateScope> scope) {
		Iterator<StateScope> it = scope.iterator();
		while (it.hasNext()) {
			StateScope item = it.next();
			// if we have any of the parents...
			StateParent parent = item.getParent();
			if (parent != null && scopeContains(scope, parent))
				// ..., we don't need the child
				it.remove();
		}
		return scope;
	}

	private static boolean scopeContains(final Set<StateScope> scope,
			StateScope subscope) {
		do {
			if (scope.contains(subscope))
				return true;
			subscope = subscope.getParent();
		} while (subscope != null);
		return false;
	}
	
	/** test whether this object is valid */
	public boolean validate() {
		if (name == null || name.isEmpty())
			return false;
		
		if ((type == null || type.isEmpty() || type == TYPE_AUTONAME)
				&& (declaration == null || declaration.isEmpty()))
			return false;
		
		if (visibility_scope.isEmpty() || lifetime_scope.isEmpty())
			return false;
		
		return isSubsetOf(visibility_scope, lifetime_scope);
	}

	private static boolean isSubsetOf(Set<StateScope> subset,
			Set<StateScope> set) {
		for (StateScope scope : subset) {
			if (!scopeContains(set, scope))
				return false;
		}
		return true;
	}
	
	public static List<StateScope> getParents(StateScope scope) {
		List<StateScope> parents = new LinkedList<StateScope>();
		do {
			parents.add(scope);
			
			scope = scope.getParent();
		} while (scope != null);
		return parents;
	}
	
	public static StateScope findCommonParent(StateScope scope1, StateScope scope2) {
		Set<StateScope> parents1 = new HashSet<StateScope>(getParents(scope1));
		for (StateScope parent2 : getParents(scope2))
			if (parents1.contains(parent2))
				return parent2;
		
		throw new RuntimeException("no common parent");
	}
	
	public static StateScope findCommonParent(Iterable<StateScope> scopes) {
		Iterator<StateScope> it = scopes.iterator();
		if (!it.hasNext())
			throw new IllegalArgumentException("the list mustn't be empty");
		StateScope common = it.next();
		while (it.hasNext())
			common = findCommonParent(common, it.next());
		return common;
	}
	
	public static boolean areDistinctScopes(Set<StateScope> scope1, Set<StateScope> scope2) {
		for (StateScope st : scope2) {
			if (scopeContains(scope1, st))
				return false;
		}
		
		return true;
	}

	@Override
	public int compareTo(StateVariable var) {
		return name.compareTo(var.name);
	}
}
