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
	private SortedMap<String, Set<Transition>> events;
	private Map<State, List<Action>> actions;
	private Map<Transition, TransitionInfo> transition_infos;

	public StateMachineForGeneration(StateMachine inner) {
		this.inner = inner;
		
		initActions();
		initTransitionInfos();
		initEvents();
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

	
	public SortedMap<String, Set<Transition>> getEvents() {
		return events;
	}

	public List<Action> getActions(State state) {
		return actions.get(state);
	}

	public TransitionInfo getTransitionInfo(Transition t) {
		return transition_infos.get(t);
	}

	

	//NOTE Must be called after initTransitionInfos()
	private void initEvents() {
		events = new TreeMap<String, Set<Transition>>();
		
		for (Transition transition : getTransitions()) {
			TransitionInfo ti = getTransitionInfo(transition);
			String eventName = ti.getEventName();
			if (eventName != null) {
				Set<Transition> transitions = events.get(eventName);
				if (transitions == null) {
					transitions = new HashSet<Transition>();
					events.put(eventName, transitions);
				}
				
				transitions.add(transition);
			}
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
}
