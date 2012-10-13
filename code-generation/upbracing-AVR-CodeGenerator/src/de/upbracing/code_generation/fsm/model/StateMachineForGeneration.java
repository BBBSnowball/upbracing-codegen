package de.upbracing.code_generation.fsm.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.emf.common.util.EList;

import Statecharts.FinalState;
import Statecharts.GlobalCode;
import Statecharts.InitialState;
import Statecharts.NamedItem;
import Statecharts.Region;
import Statecharts.State;
import Statecharts.StateMachine;
import Statecharts.StateParent;
import Statecharts.StateScope;
import Statecharts.StateWithActions;
import Statecharts.SuperState;
import Statecharts.Transition;

public class StateMachineForGeneration {
	private StateMachine inner;
	
	// configuration values
	
	// name of the state machine
	// This could be set in the model, but I think the name should
	// be set by the program that is using the statemachine. That
	// way, we can have more than one option for a statemachine or
	// several instances of the same statemachine.
	private String name;
	
	// allow access to certain values that would otherwise be private
	// e.g. the state
	private boolean test;
	
	// print debug information while the statemachine is executed
	private int trace_level;
	private String trace_printer;
	
	// the kind of lock that should be used by the statemachine
	// This is the global lock all around statemachine_tick. You can
	// still use semaphores within the statemachine, if you use
	// a different lock here.
	//NOTE This must be INTERRUPT, if any part of the statemachine
	//     could be called from an interrupt.
	private StatemachineLockMethod lock_method = StatemachineLockMethod.NO_LOCK;
	
	// values computed for the inner statemachine
	
	private SortedMap<String, Set<Transition>> events;
	private Map<State, List<Action>> actions;
	private Map<Transition, TransitionInfo> transition_infos;
	private StateVariables state_variables = new StateVariables();

	public StateMachineForGeneration(String name, StateMachine inner) {
		this.name = name;
		this.inner = inner;
		
		update();
	}

	public StateMachine getStateMachine() {
		return inner;
	}

	public EList<Transition> getTransitions() {
		return inner.getTransitions();
	}

	public EList<State> getStates() {
		return inner.getStates();
	}

	public EList<GlobalCode> getGlobalCodeBoxes() {
		return inner.getGlobalCodeBoxes();
	}
	
	public String getBasePeriodAsString() {
		return inner.getBasePeriod();
	}

	public void setBasePeriod(String value) {
		inner.setBasePeriod(value);
	}
	
	
	public double getBasePeriod() {
		return FSMParsers.parseTime(getBasePeriodAsString());
	}
	

	public SortedMap<String, Set<Transition>> getEvents() {
		return events;
	}

	public List<Action> getActions(State state) {
		return actions.get(state);
	}

	public List<Action> getOrCreateActions(StateWithActions state) {
		List<Action> state_actions = this.actions.get(state);
		if (state_actions == null) {
			state_actions = new LinkedList<Action>();
			this.actions.put(state,  state_actions);
		}
		return state_actions;
	}

	public TransitionInfo getTransitionInfo(Transition t) {
		return transition_infos.get(t);
	}

	public boolean hasHeaderCodeBoxes() {
		for (GlobalCode box : getGlobalCodeBoxes()) {
			if (box.getInHeaderFile())
				return true;
		}
		return false;
	}

	public boolean hasCFileCodeBoxes() {
		for (GlobalCode box : getGlobalCodeBoxes()) {
			if (!box.getInHeaderFile())
				return true;
		}
		return false;
	}
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isForTest() {
		return test;
	}

	public void setForTest(boolean test) {
		this.test = test;
	}
	public int getTraceLevel() {
		return trace_level;
	}

	public String getTracePrinter() {
		return trace_printer;
	}
	
	public void enableTracing(int level, String printer) {
		this.trace_level = level;
		this.trace_printer = printer;
	}
	
	public void disableTracing() {
		this.trace_level = 0;
		this.trace_printer = null;
	}
	
	public boolean shouldPrintTraceForLevel(int level) {
		return trace_printer != null && level <= this.trace_level;
	}

	public StatemachineLockMethod getLockMethod() {
		return lock_method;
	}

	public void setLockMethod(StatemachineLockMethod lock_method) {
		this.lock_method = lock_method;
	}

	public StateVariables getStateVariables() {
		return state_variables;
	}
	

	public void update() {
		initActions();
		initTransitionInfos();
		initEvents();
		
		updateParents();
	}

	//NOTE Must be called after initTransitionInfos()
	private void initEvents() {
		events = new TreeMap<String, Set<Transition>>();
		
		for (Transition transition : getTransitions()) {
			TransitionInfo ti = getTransitionInfo(transition);
			String eventName = ti.getEventName();
			
			// If eventName is null, it will be put into the
			// map nonetheless. Those transitions are used
			// for the tick function. However, the map wouldn't
			// accept null keys, so we use the empty string instead.
			if (eventName == null)
				eventName = "";
			
			Set<Transition> transitions = events.get(eventName);
			if (transitions == null) {
				transitions = new HashSet<Transition>();
				events.put(eventName, transitions);
			}
			
			transitions.add(transition);
		}
	}

	private void initActions() {
		actions = new HashMap<State, List<Action>>();
		
		List<State> states = getStates();
		initActions(states);
	}

	private void initActions(List<State> states) {
		for (State state : states) {
			if (state instanceof StateWithActions) {
				String action_str = ((StateWithActions)state).getActions();
				//TODO handle errors
				actions.put(state, FSMParsers.parseStateActions(action_str));
			}
			
			if (state instanceof StateParent) {
				initActions(((StateParent)state).getStates());
			}
		}
	}

	private void initTransitionInfos() {
		transition_infos = new HashMap<Transition, TransitionInfo>();
		
		for (Transition transition : getTransitions()) {
			String transition_str = transition.getTransitionInfo();
			//TODO handle errors
			transition_infos.put(transition, FSMParsers.parseTransitionInfo(transition_str));
		}
	}
	
	/** set parent for all objects that don't update it automatically */
	public void updateParents() {
		updateParents(getStates(), inner);
	}

	private void updateParents(EList<State> states, StateParent parent) {
		for (State state : states) {
			state.setParent(parent);
			
			if (state instanceof SuperState) {
				SuperState superState = (SuperState) state;
				for (Region region : superState.getRegions()) {
					region.setParent(superState);
					
					updateParents(region.getStates(), region);
				}
			} else if (state instanceof StateParent) {
				StateParent parent2 = (StateParent) state;
				updateParents(parent2.getStates(), parent2);
			}
		}
	}


	private final Comparator<State> cmpStates =
			new Comparator<State>() {
				@Override
				public int compare(State a,
						State b) {
					if (a == b)
						return 0;
					
					int typeCmp = type(a) - type(b);
					if (typeCmp != 0)
						return typeCmp;
					
					String nameA = getName(a);
					String nameB = getName(b);

					int cmp = nameA.compareToIgnoreCase(nameB);
					if (cmp != 0)
						return cmp;
					
					cmp = -nameA.compareTo(nameB);
					if (cmp != 0)
						return cmp;
					
					// We don't want any to loose any states, if the
					// set things they are equal, so we try very hard
					// to return a non-zero result.
					return a.hashCode() - b.hashCode();
				}

				private int type(State a) {
					if (a instanceof InitialState)
						// initial states come first
						return -10;
					else if (a instanceof FinalState)
						// final states come last
						return 10;
					else
						return 0;
				}
			};
	
	private final Comparator<Transition> cmpTransitions =
			new Comparator<Transition>() {
				@Override
				public int compare(Transition t1, Transition t2) {
					int cmp = t1.getPriority() - t2.getPriority();
					if (cmp != 0)
						return cmp;
					
					cmp = cmpStates.compare(t1.getDestination(), t2.getDestination());
					if (cmp != 0)
						return cmp;
					
					cmp = t1.getTransitionInfo().compareTo(t2.getTransitionInfo());
					return cmp;
				}
			};
	
	public Collection<State> sortStates(Collection<State> states) {
		SortedSet<State> sorted = new TreeSet<State>(cmpStates);
		sorted.addAll(states);
		return sorted;
	}

	public Collection<State> sortStatesForEnum(Collection<State> states) {
		Collection<State> sorted = sortStates(states);

		State firstState = findFirstState(states);
		if (firstState == null)
			return sorted;
		
		// first state should be the first one
		sorted.remove(firstState);
		List<State> result = new ArrayList<State>(states.size());
		result.add(firstState);
		result.addAll(sorted);
		
		return result;
	}


	public static String getName(Object state) {
		if (state instanceof NamedItem)
			return ((NamedItem) state).getName();
		else if (state instanceof StateMachine)
			return "#diagram";
		else if (state instanceof Transition) {
			Transition t = (Transition) state;
			return "transition(" + getName(t.getSource()) + " -> "
					+ getName(t.getDestination()) + ")";
		} else if (state == null)
			return "(null)";
		else
			throw new RuntimeException("should not get here, unexpected object is " + state);
	}
	
	public static String describeSelf(Object state) {
		if (state instanceof NamedItem)
			return ((NamedItem) state).getName();
		else if (state instanceof StateMachine)
			return "#diagram";
		else if (state instanceof Transition) {
			Transition t = (Transition) state;
			return "transition(" + describe(t.getSource()) + " -> "
					+ describe(t.getDestination()) + ")";
		} else if (state == null)
			return "(null)";
		else
			throw new RuntimeException("should not get here, unexpected object is " + state);
	}
	
	public static String describe(Object state) {
		if (state instanceof StateScope) {
			StateParent parent = ((StateScope)state).getParent();
			if (parent != null)
				return describe(parent) + "." + describeSelf(state);
		}
		
		return describeSelf(state);
	}

	public State findFirstState(Collection<State> states) {
		return findFirstState(getStateMachine(), states);
	}

	public static State findFirstState(StateMachine statemachine, Collection<State> states) {
		List<Transition> transitions = statemachine.getTransitions();
		State finalStateIsAlsoFirst = null;

		for (State state : states) {
			if (state instanceof InitialState) {
				for (Transition t : transitions) {
					if (t.getSource() == state
							&& t.getDestination() instanceof StateWithActions)
						return t.getDestination();
					else if (t.getSource() == state
							&& t.getDestination() instanceof FinalState)
						finalStateIsAlsoFirst = t.getDestination();
				}
			}
		}

		return finalStateIsAlsoFirst;
	}

	public Transition findInitialTransition(Collection<State> states) {
		List<Transition> transitions = getTransitions();
		Transition finalStateIsAlsoFirst = null;

		for (State state : states) {
			if (state instanceof InitialState) {
				for (Transition t : transitions) {
					if (t.getSource() == state
							&& t.getDestination() instanceof StateWithActions)
						return t;
					else if (t.getSource() == state
							&& t.getDestination() instanceof FinalState)
						finalStateIsAlsoFirst = t;
				}
			}
		}

		return finalStateIsAlsoFirst;
	}

	public State findFinalState(List<State> states) {
		for (State state : states) {
			if (state instanceof FinalState)
				return state;
		}

		return null;
	}

	public List<StateWithActions> filterStatesWithActions(
			Collection<State> states) {
		List<StateWithActions> states_with_actions = new ArrayList<StateWithActions>();
		for (State state : states) {
			if (state instanceof StateWithActions)
				states_with_actions.add((StateWithActions)state);
		}
		return states_with_actions;
	}

	public String stateName(State state) {
		StringBuffer sb = new StringBuffer();
		for (StateParent parent : getParentsTopToBottom(state)) {
			if (parent == getStateMachine())
				sb.append(this.getName());
			else
				sb.append(((NamedItem)parent).getName());
			sb.append('_');
		}
		sb.append(state.getName());
		sb.append('_');
		sb.append("state");
		return sb.toString();
	}

	private static LinkedList<StateParent> getParents(State state, boolean bottom_most_first) {
		LinkedList<StateParent> parents = new LinkedList<StateParent>();
		StateParent parent = state.getParent();
		while (parent != null) {
			if (bottom_most_first)
				parents.addLast(parent);
			else
				parents.addFirst(parent);
			
			parent = parent.getParent();
		}
		return parents;
	}
	
	public static LinkedList<StateParent> getParentsBottomToTop(State state) {
		return getParents(state, true);
	}
	
	public static LinkedList<StateParent> getParentsTopToBottom(State state) {
		return getParents(state, false);
	}

	public SortedSet<Transition> sortedTransitionSet() {
		return new TreeSet<Transition>(cmpTransitions);
	}
}
