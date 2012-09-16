package de.upbracing.code_generation.fsm.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.emf.common.util.EList;

import Statecharts.GlobalCode;
import Statecharts.State;
import Statecharts.StateMachine;
import Statecharts.StateWithActions;
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
	private boolean hasHeaderCodeBoxes, hasCFileCodeBoxes;

	public StateMachineForGeneration(String name, StateMachine inner) {
		this.name = name;
		this.inner = inner;
		
		update();
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

	public TransitionInfo getTransitionInfo(Transition t) {
		return transition_infos.get(t);
	}

	public boolean hasHeaderCodeBoxes() {
		return hasHeaderCodeBoxes;
	}

	public boolean hasCFileCodeBoxes() {
		return hasCFileCodeBoxes;
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
	

	public void update() {
		initActions();
		initTransitionInfos();
		initEvents();
		initCodeBoxBools();
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
		
		for (State state : getStates()) {
			if (state instanceof StateWithActions) {
				String action_str = ((StateWithActions)state).getActions();
				//TODO handle errors
				actions.put(state, FSMParsers.parseStateActions(action_str));
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
	
	private void initCodeBoxBools() {
		hasHeaderCodeBoxes = false;
		hasCFileCodeBoxes = false;
		for (GlobalCode box : getGlobalCodeBoxes()) {
			if (box.getInHeaderFile())
				hasHeaderCodeBoxes = true;
			else
				hasCFileCodeBoxes = true;
			
			if (hasHeaderCodeBoxes && hasCFileCodeBoxes)
				break;
		}
	}
}
