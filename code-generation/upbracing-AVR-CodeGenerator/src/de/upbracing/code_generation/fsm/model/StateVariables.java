package de.upbracing.code_generation.fsm.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import Statecharts.NamedItem;
import Statecharts.Region;
import Statecharts.StateParent;
import Statecharts.StateScope;
import Statecharts.SuperState;

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
	
	public class VariableContainer implements Comparable<VariableContainer> {
		public String name;
		public StateScope forScope;
		public Set<StateVariable> variables = new HashSet<StateVariable>();
		public Set<VariableContainer> children = new HashSet<StateVariables.VariableContainer>();
		public VariableContainer parent;
		
		@Override
		public int compareTo(VariableContainer other) {
			return name.compareTo(other.name);
		}
	}
	
	public class AllOf extends VariableContainer { }
	
	public class OneOf extends VariableContainer { }
	
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
				vars = new AllOf();
				vars.forScope = scope;
				variableCs.put(scope, vars);
				if (scope instanceof NamedItem)
					vars.name = ((NamedItem)scope).getName();
				else
					vars.name = "no_name";	//TODO could that be anything but the diagram?
				StateParent parent = scope.getParent();
				if (parent != null) {
					vars.parent = getChildrenContainerFor(parent);
					vars.parent.children.add(vars);
				}
			}
			return vars;
		}

		private VariableContainer getChildrenContainerFor(StateParent scope) {
			VariableContainer vars = stateCs.get(scope);
			if (vars != null)
				return vars;
			
			if (scope instanceof SuperState)
				vars = new AllOf();
			else
				vars = new OneOf();
			
			vars.forScope = scope;
			stateCs.put(scope, vars);
			vars.name = "states";
			vars.parent = getVariableContainerFor(scope);
			vars.parent.children.add(vars);
			
			return vars;
		}
		
		public VariableContainer plan() {
			addVariables();
			collapseEmptyContainers();
			makeNamesUnique();
			sortVariablesByName();
			setVariableRealNames();
			
			return variableCs.get(StateVariable.findCommonParent(variableCs.keySet()));
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
		
		private void collapseEmptyContainers() {
			collapseEmptyContainers(variableCs);
			collapseEmptyContainers(stateCs);
		}

		private void collapseEmptyContainers(Map<StateScope, VariableContainer> containers) {
			for (VariableContainer container : containers.values()) {
				if (container.parent == null)
					continue;

				VariableContainer parent = container.parent;
				
				if (container.getClass() == parent.getClass() || (container.variables.size() + container.children.size() <= 1)) {
					for (VariableContainer container2 : container.children) {
						container2.name = container.name + "_" + container2.name;
						parent.children.add(container2);
					}
					
					for (StateVariable var : container.variables) {
						addPrefix(var, container.name + "_");
						parent.variables.add(var);
					}
				}
			}
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
	}
	
	public VariableContainer planStructure() {
		return new PlanStructure().plan();
	}
}
