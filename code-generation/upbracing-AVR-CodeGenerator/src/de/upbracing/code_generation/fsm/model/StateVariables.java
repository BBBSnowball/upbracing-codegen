package de.upbracing.code_generation.fsm.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import statemachine.NamedItem;
import statemachine.StateParent;
import statemachine.StateScope;
import statemachine.SuperState;


public class StateVariables {
	private Set<StateVariable> variables = new HashSet<StateVariable>();
	private Map<StateScope, Map<String, StateVariable>> variables_by_visibility
		= new HashMap<StateScope, Map<String, StateVariable>>();
	private Map<StateScope, Map<StateVariablePurpose, StateVariable>> variables_by_purpose
		= new HashMap<StateScope, Map<StateVariablePurpose,StateVariable>>();

	public void add(StateVariable var) {
		if (variables.contains(var))
			return;
		
		if (!var.validate())
			throw new IllegalArgumentException("variable is not valid");
		
		variables.add(var);
		
		String name = var.getName();
		Set<StateScope> visibility = var.getVisibilityScope();
		for (StateScope vscope : visibility) {
			addToTwoLevelMap(vscope, name, var, variables_by_visibility);
		}
	}
	
	public void add(StateVariable var, StateVariablePurpose purpose, StateScope... scopes) {
		add(var);
		
		for (StateScope scope : scopes) {
			addToTwoLevelMap(scope, purpose, var, variables_by_purpose);
		}
	}

	private <T,U,V> void addToTwoLevelMap(T key1, U key2, V value, Map<T, Map<U, V>> map) {
		Map<U, V> map2 = map.get(key1);
		if (map2 == null) {
			map2 = new HashMap<U, V>();
			map.put(key1, map2);
		}
		
		V oldValue = map2.get(key2);
		if (oldValue != null && oldValue != value)
			throw new IllegalArgumentException("duplicate key: key1 = " + key1
					+ ", key2 = " + key2 + ", for value: " + value);
		
		map2.put(key2, value);
	}
	
	public StateVariable getVisibleVariable(StateScope scope, String name) {
		for (StateScope x : StateVariable.getParents(scope)) {
			if (name.startsWith(":")) {
				// only search in parent scope
				name = name.substring(1);
			} else {
				Map<String, StateVariable> vars = variables_by_visibility.get(x);
				if (vars != null) {
					StateVariable var = vars.get(name);
					if (var != null)
						return var;
				}
			}
		}
		
		return null;
	}
	
	public StateVariable getVisibleVariable(StateScope scope, StateVariablePurpose purpose) {
		for (StateScope x : StateVariable.getParents(scope)) {
			Map<StateVariablePurpose, StateVariable> vars = variables_by_purpose.get(x);
			if (vars != null) {
				StateVariable var = vars.get(purpose);
				if (var != null)
					return var;
			}
		}
		
		return null;
	}
	
	public StateVariable getVariable(StateScope scope, StateVariablePurpose purpose) {
		Map<StateVariablePurpose, StateVariable> vars = variables_by_purpose.get(scope);
		if (vars == null)
			return null;
		else
			return vars.get(purpose);
	}
	
	public Set<StateVariable> getVariables() {
		return Collections.unmodifiableSet(variables);
	}
	
	public static class VariableContainer implements Comparable<VariableContainer>, IHasNameAndId {
		private int id = getNextId();
		private static int next_id = 0;
		private static synchronized int getNextId() { return next_id++; }
		public int getId() { return id; }

		private String getName(StateVariable var) {
			if (var == null)
				return null;
			
			String name = null;
			if (variable_names != null)
				name = variable_names.get(var);
			if (name == null)
				name = var.getName();
			return name;
		}
		
		private <T extends IHasNameAndId> SortedSet<T> sortedSetByName() {
			return new TreeSet<T>(new Comparator<T>() {
				public int compare(T a, T b) {
					if (a == null && b == null)
						return 0;
					else if (a != null && b != null && a.getName() == null && b.getName() == null)
						return a.hashCode() - b.hashCode();
					else if (a == null || a.getName() == null)
						return -1;
					else if (b == null || b.getName() == null)
						return +1;
					
					int result = a.getName().compareTo(b.getName());
					if (result != 0)
						return result;
					
					return a.getId() - b.getId();
				}
			});
		}
		
		private SortedSet<StateVariable> sortedVariableSetByName() {
			return new TreeSet<StateVariable>(new Comparator<StateVariable>() {
				public int compare(StateVariable a, StateVariable b) {
					String name_a = getName(a);
					String name_b = getName(b);
					if (a == null && b == null)
						return 0;
					else if (a != null && b != null && name_a == null && name_b == null)
						return a.hashCode() - b.hashCode();
					else if (a == null || name_a == null)
						return -1;
					else if (b == null || name_b == null)
						return +1;
					
					int result = name_a.compareTo(name_b);
					if (result != 0)
						return result;
					
					return a.getId() - b.getId();
				}
			});
		}
		
		public String name;
		public StateScope forScope;
		public Collection<StateVariable> variables;
		public Collection<VariableContainer> children;
		public VariableContainer parent;
		// only contains a value, if the variable doesn't have its default value
		public Map<StateVariable, String> variable_names;
		public boolean important_for_hierarchy;
		
		
		public VariableContainer(Map<StateVariable, String> variable_names) {
			this.variable_names = variable_names;
			this.variables = sortedSetByName();
			this.children = sortedSetByName();
		}
		
		@Override
		public int compareTo(VariableContainer other) {
			int result = name.compareTo(other.name);
			if (result != 0)
				return result;
			
			return this.getId() - other.getId();
		}
		
		@Override
		public String getName() {
			return name;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + id;
			return result;
		}
		
		public void sortVariables() {
			SortedSet<StateVariable> sorted = sortedVariableSetByName();
			sorted.addAll(variables);
			variables = sorted;
		}
	}

	public static class AllOf extends VariableContainer {

		public AllOf(Map<StateVariable, String> variable_names) {
			super(variable_names);
		}
	}

	public static class OneOf extends VariableContainer {

		public OneOf(Map<StateVariable, String> variable_names) {
			super(variable_names);
		}
	}
	
	private class PlanStructure {
		// Each region and diagram has an oneOf
		// container which contains the containers for the
		// child states.
		// Each state has an allOf container which contains
		// the variables that it needs.
		// Each superstate has an allOf container that
		// contains its regions.
		
		Map<StateScope, VariableContainer> variableCs
			= new HashMap<StateScope, VariableContainer>();
		Map<StateScope, VariableContainer> stateCs
			= new HashMap<StateScope, VariableContainer>();
		Map<StateVariable, String> names
			= new HashMap<StateVariable, String>();
		
		private VariableContainer getVariableContainerFor(StateScope scope) {
			VariableContainer vars = variableCs.get(scope);
			if (vars == null) {
				vars = new AllOf(names);
				vars.forScope = scope;
				variableCs.put(scope, vars);
				if (scope instanceof NamedItem)
					vars.name = ((NamedItem)scope).getName();
				else
					vars.name = "no_name";
				StateParent parent = scope.getParent();
				if (parent != null) {
					vars.parent = getChildrenContainerFor(parent);
					vars.parent.children.add(vars);
				}
				vars.important_for_hierarchy = true;
			}
			return vars;
		}

		private VariableContainer getChildrenContainerFor(StateParent scope) {
			VariableContainer vars = stateCs.get(scope);
			if (vars != null)
				return vars;
			
			if (scope instanceof SuperState)
				vars = new AllOf(names);
			else
				vars = new OneOf(names);
			
			vars.forScope = scope;
			stateCs.put(scope, vars);
			vars.name = "states";
			vars.parent = getVariableContainerFor(scope);
			vars.parent.children.add(vars);
			vars.important_for_hierarchy = false;
			
			return vars;
		}
		
		public VariableContainer plan(String root_name) {
			addVariables();
			collapseEmptyContainers();
			makeNamesUnique();
			sortVariablesByName();
			
			VariableContainer root = variableCs.get(StateVariable.findCommonParent(variableCs.keySet()));
			root.name = root_name;
			
			setVariableRealNames();
			
			sortVariables();
			
			return root;
		}

		private void addVariables() {
			//TODO We could save some memory by putting variables into OneOf containers which
			//     are children of the variable container that we use here. Of course, all
			//     variables in a container must have distinct lifetime scopes.
			//     Probably, that could be implemented as an optimization step which runs
			//     after this method.
			for (StateVariable var : getVariables()) {
				StateScope scope = StateVariable.findCommonParent(var.getLifetimeScope());
				VariableContainer vars = getVariableContainerFor(scope);
				vars.variables.add(var);
			}
		}
		
		@SuppressWarnings("unused")
		private void debugPrint() {
			VariableContainer root = variableCs.get(StateVariable.findCommonParent(variableCs.keySet()));
			debugPrint("", root);
		}

		private void debugPrint(String indent, VariableContainer container) {
			System.out.println(indent + container.name + "\t\t\t" + container);
			indent += "\t";
			
			for (StateVariable var : container.variables)
				System.out.println(indent + "V: " + var.getName() + " (" + getName(var) + ")");
			
			for (VariableContainer child : container.children)
				debugPrint(indent, child);
		}
		
		private void collapseEmptyContainers() {
			boolean something_changed;
			do {
				something_changed = collapseEmptyContainers(variableCs);
				something_changed |= collapseEmptyContainers(stateCs);
			} while (something_changed);
		}

		private boolean collapseEmptyContainers(Map<StateScope, VariableContainer> containers) {
			boolean something_changed = false;
			
			Iterator<VariableContainer> it = containers.values().iterator();
			while (it.hasNext()) {
				VariableContainer container = it.next();
				
				if (container.parent == null)
					continue;

				VariableContainer parent = container.parent;
				
				if (container.getClass() == parent.getClass() || (container.variables.size() + container.children.size() <= 1)) {
					something_changed = true;
					
					String container_name = container.name;
					
					for (VariableContainer container2 : container.children) {
						if (container.important_for_hierarchy) {
							if (container2.important_for_hierarchy || container.children.size() > 1) {
								container2.name = container_name + "__" + container2.name;
							} else {
								container2.name = container_name;
							}
							container2.important_for_hierarchy = true;
						}
						container2.parent = parent;
						if (parent.children.contains(container2))
							System.out.println("ERROR: PARENT ALREADY CONTAINS THIS CONTAINER!");
						parent.children.add(container2);
					}
					
					for (StateVariable var : container.variables) {
						if (container.important_for_hierarchy)
							addPrefix(var, container_name + "__");
						parent.variables.add(var);
					}
					
					it.remove();
					container.parent.children.remove(container);
					
					container.name = "--removed--(" + container_name + ")--";
				}
			}
			
			return something_changed;
		}

		private void addPrefix(StateVariable var, String prefix) {
			prefix += getName(var);
			names.put(var, prefix);
		}

		private void makeNamesUnique() {
			makeNamesUnique(variableCs.values());
			makeNamesUnique(stateCs.values());
		}

		private void makeNamesUnique(Collection<VariableContainer> containers) {
			for (VariableContainer container : containers) {
				makeNamesUnique(container);
			}
		}

		private void makeNamesUnique(VariableContainer container) {
			Set<String> usedNames = new HashSet<String>();
			
			for (VariableContainer container2 : container.children) {
				String name = container2.name;
				if (usedNames.contains(name))
					container2.name = name = findUniqueName(usedNames, name);
				
				usedNames.add(name);
			}
			
			for (StateVariable var : container.variables) {
				String name = getName(var);
				
				if (usedNames.contains(name)) {
					name = findUniqueName(usedNames, name);
					names.put(var, name);
				}
				
				usedNames.add(name);
			}
		}

		private String findUniqueName(Set<String> usedNames, String name) {
			int i = 2;
			while (usedNames.contains(name + i))
				++i;
			name = name + i;
			return name;
		}

		private void sortVariablesByName() {
			sortVariablesByName(variableCs.values());
			sortVariablesByName(stateCs.values());
		}

		private void sortVariablesByName(Collection<VariableContainer> containers) {
			for (VariableContainer container : containers) {
				if (container.children.size() > 1)
					container.children = new TreeSet<StateVariables.VariableContainer>(container.children);
				
				if (container.variables.size() > 1)
					container.variables = new TreeSet<StateVariable>(container.variables);
			}
		}

		private void setVariableRealNames() {
			setVariableRealNames(variableCs.values());
			setVariableRealNames(stateCs.values());
		}

		private void setVariableRealNames(Collection<VariableContainer> containers) {
			for (VariableContainer container : containers) {
				String container_real_name = getRealName(container);
				for (StateVariable var : container.variables) {
					String name = getName(var);
					
					var.setRealName(joinName(container_real_name, name));
					
					if (var.getType() == StateVariable.TYPE_AUTONAME)
						var.setType(var.getRealName().replace(".", "__") + "_t");
				}
				
				setVariableRealNames(container.children);
			}
		}
		
		private String getRealName(VariableContainer container) {
			if (container.parent == null)
				return container.name;
			else
				return joinName(getRealName(container.parent), container.name);
		}

		private String joinName(String name1, String name2) {
			if (name1 == null || name1.isEmpty())
				return name2;
			else if (name2 == null || name2.isEmpty())
				return name1;
			else
				return name1 + "." + name2;
		}

		private String getName(StateVariable var) {
			String name = names.get(var);
			if (name == null)
				name = var.getName();
			return name;
		}

		private void sortVariables() {
			sortVariables(variableCs.values());
			sortVariables(stateCs.values());
		}

		private void sortVariables(Collection<VariableContainer> containers) {
			for (VariableContainer container : containers) {
				container.sortVariables();
				
				sortVariables(container.children);
			}
		}
	}
	
	public VariableContainer planStructure(String root_name) {
		return new PlanStructure().plan(root_name);
	}
}
