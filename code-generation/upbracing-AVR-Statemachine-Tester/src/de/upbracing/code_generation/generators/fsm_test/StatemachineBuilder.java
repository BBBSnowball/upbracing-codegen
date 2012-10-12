package de.upbracing.code_generation.generators.fsm_test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import Statecharts.FinalState;
import Statecharts.InitialState;
import Statecharts.NamedItem;
import Statecharts.NormalState;
import Statecharts.Region;
import Statecharts.State;
import Statecharts.StateMachine;
import Statecharts.StateParent;
import Statecharts.StateScope;
import Statecharts.StateWithActions;
import Statecharts.StatechartsFactory;
import Statecharts.StatechartsPackage;
import Statecharts.SuperState;
import Statecharts.Transition;
import de.upbracing.code_generation.fsm.model.StateMachineForGeneration;
import de.upbracing.code_generation.fsm.model.StateVariable;

public class StatemachineBuilder {
	public static final class GaussianProbability {
		public double avg, stddev, right_factor;
		public double min = Integer.MIN_VALUE, max = Integer.MAX_VALUE;
		
		public GaussianProbability(double avg, double stddev) {
			super();
			this.avg = avg;
			this.stddev = stddev;
		}
		
		public GaussianProbability withMin(double min) {
			this.min = min;
			return this;
		}
		
		public GaussianProbability withMax(double max) {
			this.max = max;
			return this;
		}
		
		/** standard deviation over avg is factor_sqr times
		 * higher than the chosen value, below avg it is smaller
		 * by the same factor. Therefore, they differ by a
		 * factor of factor_sqr*factor_sqr.
		 * 
		 * @param factor_sqr the factor
		 * @return itself (fluent interface)
		 */
		public GaussianProbability leanRightBy(double factor_sqr) {
			this.right_factor = factor_sqr;
			return this;
		}
		
		public int nextDouble(Random random) {
			double value;
			do {
				double x = random.nextGaussian()*stddev;
				if (x > 0)
					x *= right_factor;
				else
					x /= right_factor;
				value = x + avg;
			} while (value < min || value > max);
			
			return (int) value;
		}
		
		public int nextInt(Random random) {
			long value;
			do {
				double x = random.nextGaussian()*stddev;
				if (x > 0)
					x *= right_factor;
				else
					x /= right_factor;
				value = Math.round(x + avg);
			} while (value < min || value > max);
			
			return (int) value;
		}
	}
	
	// Those values are public, so they can be tweaked before calling build().
	
	public StatechartsFactory factory;
	
	public Random random;
	
	public double nesting_depth = 3;
	
	public GaussianProbability state_count_per_parent = new GaussianProbability(4, 4).withMin(1).leanRightBy(1.5);
	
	public GaussianProbability regions_per_superstate = new GaussianProbability(2, 1).withMin(0);
	
	public GaussianProbability event_count = new GaussianProbability(10, 10).withMin(1).leanRightBy(1.5);
	
	public double final_state_probability = 0.1;
	
	public GaussianProbability transitions_state_ratio = new GaussianProbability(1, 0.3).withMin(0.1);
	
	public GaussianProbability way_length = new GaussianProbability(20, 10).withMin(0);
	
	public double transition_probability_far_target = 0.1;
	
	public double transition_probability_leaf = 0.5;
	
	public StatemachineBuilder(long seed) {
		random = new Random(seed);
		factory = getDefaultStatemachineFactory();
	}
	
	public static StatechartsFactory getDefaultStatemachineFactory() {
		return new Statecharts.impl.StatechartsFactoryImpl();
	}
	
	@Deprecated
	public StateMachine build() {
		return new Builder().build();
	}
	
	public StatemachineWithWay buildStatemachineWithWay() {
		return new Builder().buildStatemachineWithWay();
	}
	
	public static final class Waypoint {
		public final State active_state;
		public final List<Waypoint> children;
		
		public Waypoint(State active_state, List<Waypoint> children) {
			super();
			this.active_state = active_state;
			this.children = children;
		}

		public int getMaximumDepth() {
			int depth = 1;
			for (Waypoint child : children) {
				int depth2 = child.getMaximumDepth() + 1;
				if (depth2 > depth)
					depth = depth2;
			}
			return depth;
		}
		
		public String toString() {
			StringBuffer sb = new StringBuffer();
			toString("", sb);
			return sb.toString();
		}
		
		public void toString(String indent, StringBuffer sb) {
			sb.append(indent + active_state.getName());
			if (!children.isEmpty()) {
				for (Waypoint child : children) {
					sb.append('\n');
					child.toString(indent + "  ", sb);
				}
			}
		}
	}
	
	public static final class TransitionActivation {
		public final String eventname;
		public final List<Transition> activated_transitions = new LinkedList<Transition>();
		
		public TransitionActivation(String eventname) {
			super();
			this.eventname = eventname;
		}
		
		/** transition info for a transition that is activated by exactly this kind of activation */
		public String getTransitionInfo() {
			return eventname;
		}

		/** check whether a transition may be active, if the conditions specified
		 * in this object are met. If the result depends on unknown information,
		 * this method returns true (e.g. "a && b", a is true, b is unknown).
		 * 
		 * This method may fail (with an exception or a wrong result), if the
		 * transition information has not been supplied by the getTransitionInfo
		 * of an instance of this class.
		 * 
		 * @param t the transition to test
		 * @return true, if there is a chance that the transition should be executed in the
		 *          situation described by the TransitionActivation
		 */
		public boolean isActiveFor(Transition t) {
			return t.getTransitionInfo().equals(eventname);
		}

		public void appendExecutionInstructions(String statemachine_name, String indent,
				StringBuffer stringBuffer) {
			stringBuffer.append(indent + statemachine_name + "_event_" + eventname + "();\n");
		}
		
		//TODO some indication of other conditions, e.g. values of global variables
	}
	
	public static final class StatemachineWithWay {
		public final StateMachine statemachine;
		public final List<Waypoint> waypoints;
		public final List<TransitionActivation> transitions;
		
		public StatemachineWithWay(StateMachine statemachine,
				List<Waypoint> waypoints, List<TransitionActivation> transitions) {
			super();
			this.statemachine = statemachine;
			this.waypoints = waypoints;
			this.transitions = transitions;
		}
	}
	
	private class Builder {
		private List<String> event_names;
		// all states that can be used for transitions -> all states except initial states
		private ArrayList<State> all_states = new ArrayList<State>();
		private int uniquename_counter = 0;
		private StateMachine statemachine;
		private int depth = 0;
		private Map<String, List<Transition>> transitions = new HashMap<String, List<Transition>>();
		
		private boolean shouldCreateNestedStates() {
			if (depth > nesting_depth*2)
				return false;
			
			// probability of 1-1/nesting_depth => 5/6, if nesting_depth is 6
			// E(depth) equals nesting_depth for this choice of probability.
			// P(depth) falls exponentially, so small depth are more likely.
			return random.nextDouble() < 1-1/nesting_depth;
		}
		
		/**
		 * @deprecated Generates huge statemachines and needs very long time
		 *               for connectOrphans (or probably it doesn't even finsh
		 *               at all).
		 * @return
		 */
		@Deprecated
		public StateMachine build() {
			buildStates();
			
			createTransitions(statemachine);
			
			connectOrphans(statemachine);
			
			return statemachine;
		}

		private void buildStates() {
			statemachine = factory.createStateMachine();
			statemachine.setBasePeriod("1ms");
			
			createChildrenStates(statemachine);
		}
		
		public StatemachineWithWay buildStatemachineWithWay() {
			buildStates();
			
			StatemachineWithWay smw = createWay();
			/*StatemachineWithWay smw = new StatemachineWithWay(statemachine,
					Arrays.<Waypoint>asList(findInitialWaypointForParent(statemachine)),
					Arrays.<TransitionActivation>asList());*/
			
			removeOrphans(smw);
			
			return smw;
		}

		private void createChildrenStates(StateParent parent) {
			depth++;
			
			int count = state_count_per_parent.nextInt(random);
			
			InitialState initial_state = factory.createInitialState();
			initial_state.setName(getUniqueName("istate"));
			parent.getStates().add(initial_state);
			initial_state.setParent(parent);
			
			if (count > 0 && final_state_probability > random.nextDouble()) {
				--count;
				
				FinalState final_state = factory.createFinalState();
				final_state.setName(getUniqueName("fstate"));
				parent.getStates().add(final_state);
				final_state.setParent(parent);
				all_states.add(final_state);
				
				if (count == 0) {
					// no other states -> initial transition must go to final state
					addTransition(initial_state, final_state).setTransitionInfo("");
				}
			}
			
			for (int i=0;i<count;i++) {
				State state = createState();
				parent.getStates().add(state);
				state.setParent(parent);
				
				if (i == 0)
					// first state is the initial one
					addTransition(initial_state, state).setTransitionInfo("");
			}
			
			depth--;
		}

		private State createState() {
			String name = getUniqueName("state");
			
			StateWithActions state;
			
			if (shouldCreateNestedStates()) {
				SuperState sstate = factory.createSuperState();
				state = sstate;
				sstate.setName(name);
				
				int region_count = regions_per_superstate.nextInt(random);
				for (int i=0;i<region_count;i++) {
					Region region = factory.createRegion();
					region.setParent(sstate);
					sstate.getRegions().add(region);
					region.setName(getUniqueName("region"));
					
					createChildrenStates(region);
				}
			} else {
				NormalState nstate = factory.createNormalState();
				state = nstate;
				nstate.setName(name);
			}
			
			all_states.add(state);
			
			return state;
		}

		private void createTransitions(StateMachine sm) {
			// create some event names
			String[] event_names = new String[event_count.nextInt(random)];
			
			for (int i=0;i<event_names.length;i++)
				event_names[i] = getUniqueName("event");
			
			this.event_names = Arrays.asList(event_names);
			
			
			// create transitions
			int count = (int)Math.round(all_states.size() * transitions_state_ratio.nextDouble(random));
			for (int i=0;i<count;i++) {
				String event_name;
				State source, destination;
				
				do {
					event_name = choose(this.event_names);
					source = choose(all_states);
					destination = choose(all_states);
				} while (!canCreateTransition(event_name, source, destination));
				
				Transition t = addTransition(source, destination);
				t.setTransitionInfo(event_name);
			}
		}

		private boolean canCreateTransition(String event_name, State source,
				State destination) {
			if (source instanceof InitialState)
				return false;
			
			List<Transition> ts = transitions.get(source.getName());
			if (ts != null) {
				for (Transition t : ts) {
					if (event_name.equals(t.getTransitionInfo()))
						return false;
				}
			}
			
			return true;
		}

		private <T> T choose(List<T> xs) {
			return xs.get(random.nextInt(xs.size()));
		}

		private Transition addTransition(State source,
				State destination) {
			Transition t = factory.createTransition();
			t.setSource(source);
			t.setDestination(destination);
			statemachine.getTransitions().add(t);
			
			List<Transition> ts = transitions.get(source.getName());
			if (ts == null) {
				ts = new LinkedList<Transition>();
				transitions.put(source.getName(), ts);
			}
			ts.add(t);
			
			return t;
		}

		private String getUniqueName(String prefix) {
			return prefix + Integer.toString(++uniquename_counter, Character.MAX_RADIX)
					.toUpperCase();
		}
		
		private class DepthFirstSearch {
			private HashSet<StateScope> visited = new HashSet<StateScope>();
			private Map<String, List<Transition>> transitions_by_source;
			
			public DepthFirstSearch(Map<String, List<Transition>> transitions_by_source) {
				this.transitions_by_source = transitions_by_source;
			}

			public void searchFrom(StateScope state) {
				visited.add(state);
				update();
			}
			
			public void update() {
				Queue<StateScope> queue = new ConcurrentLinkedQueue<StateScope>();
				queue.addAll(visited);
				
				while (!queue.isEmpty()) {
					StateScope state = queue.poll();
					
					for (StateScope destination : getDestinationsFor(state)) {
						if (!visited.contains(destination)) {
							visited.add(destination);
							queue.offer(destination);
						}
					}
				}
			}
			
			public Iterable<StateScope> getDestinationsFor(StateScope state) {
				final List<Transition> ts = transitions_by_source.get(((State)state).getName());
				
				if (ts == null)
					return Collections.emptyList();
				
				return new Iterable<StateScope>() {
					@Override
					public Iterator<StateScope> iterator() {
						final Iterator<Transition> it = ts.iterator();
						return new Iterator<StateScope>() {
							@Override
							public boolean hasNext() {
								return it.hasNext();
							}
							
							@Override
							public StateScope next() {
								return it.next().getDestination();
							}
							
							@Override
							public void remove() {
								throw new IllegalStateException("not supported");
							}
						};
					}
				};
			}
			
			public boolean visited(StateScope state) {
				return visited.contains(state);
			}

			public Collection<StateScope> getVisitedStates() {
				return visited;
			}
		}

		private void connectOrphans(StateParent parent) {
			// find initial state
			InitialState initial_state = null;
			for (State state : parent.getStates()) {
				if (state instanceof InitialState) {
					initial_state = (InitialState)state;
					break;
				}
			}
			
			// find states that are connected to the initial state
			DepthFirstSearch dfs = new DepthFirstSearch(transitions);
			dfs.searchFrom(initial_state);

			// add transitions until all states are connected
			for (State state : parent.getStates()) {
				if (!dfs.visited(state)) {
					// add a connection
					List<StateScope> candidates = new ArrayList<StateScope>(dfs.getVisitedStates());
					
					String event_name;
					State source;
					State destination = state;
					do {
						event_name = choose(this.event_names);
						source = (State)choose(candidates);
					} while (!canCreateTransition(event_name, source, destination));
					
					addTransition(source, destination).setTransitionInfo(event_name);
					
					dfs.update();
				}
			}
			
			// do the same thing for all regions in superstates
			for (State child : parent.getStates()) {
				if (child instanceof SuperState) {
					for (Region region : ((SuperState)child).getRegions())
						connectOrphans(region);
				}
			}
		}

		private StatemachineWithWay createWay() {
			// choose a length for the way to generate
			int way_length = StatemachineBuilder.this.way_length.nextInt(random);
			
			// those lists describe the way
			List<Waypoint> waypoints = new ArrayList<StatemachineBuilder.Waypoint>(way_length+1);
			List<TransitionActivation> transitions = new ArrayList<StatemachineBuilder.TransitionActivation>(way_length);

			// get initial state and put it in a waypoint
			Waypoint initial = findInitialWaypoint(statemachine);
			waypoints.add(initial);
			
			// create way_length steps
			Waypoint waypoint = initial;
			for (int i=0;i<way_length;i++) {
				// choose some criterion that can be used to activate the transition
				// It musn't activate any existing transitions (created by previous
				// steps) because that wouldn't make the tests better, but it would
				// be a lot of work to deal with the complications.
				TransitionActivation activation;
				do {
					activation = getRandomTransitionActivation();
				} while (hasActiveTransitions(waypoint, activation));
				
				// create transitions on some levels of the statemachine
				createWaypointTransitions(waypoint, activation);
				
				// apply the transitions to find the next waypoint
				waypoint = applyTransitions(waypoint, activation);
				
				// add it to the lists
				waypoints.add(waypoint);
				transitions.add(activation);
			}
			
			return new StatemachineWithWay(statemachine, waypoints, transitions);
		}

		private Waypoint findInitialWaypoint(StateMachine statemachine) {
			return findInitialWaypointForParent(statemachine);
		}

		private Waypoint findInitialWaypointForParent(StateParent parent) {
			if (parent instanceof SuperState)
				throw new IllegalArgumentException("A superstate is not allowed here because it has more than one waypoint.");
			
			State first = StateMachineForGeneration.findFirstState(statemachine, parent.getStates());
			return getInitialWaypointForState(first);
		}
		
		private Waypoint getInitialWaypointForState(State state) {
			if (state instanceof SuperState) {
				SuperState super_state = (SuperState)state;
				Waypoint wp = new Waypoint(super_state, new ArrayList<StatemachineBuilder.Waypoint>(super_state.getRegions().size()));
				for (Region region : super_state.getRegions()) {
					wp.children.add(findInitialWaypointForParent(region));
				}
				return wp;
			} else
				return new Waypoint(state, Arrays.<Waypoint>asList());
		}

		private TransitionActivation getRandomTransitionActivation() {
			String eventname = getUniqueName("event");
			return new TransitionActivation(eventname);
		}

		private void createWaypointTransitions(Waypoint waypoint,
				TransitionActivation activation) {
			do {
				createWaypointTransitions(waypoint, activation, 1.0/waypoint.getMaximumDepth());
			} while (activation.activated_transitions.isEmpty());
		}

		private StateScope createWaypointTransitions(Waypoint waypoint,
				TransitionActivation activation,
				double transition_probability_per_level) {
			// We mustn't introduce shadowing transitions (e.g. a transition in
			// the parent layer will be executed first and by that it prevents
			// a transition in the lower layer, which is therefore shadowed by
			// the parent transition; same thing can happen for regions and
			// transitions on the same state). We avoid that with a set of
			// waypoints that mustn't have a transition in the current step.
			
			// This method returns the common parent of the created transitions.
			// This is the deepest state that will still be active after the
			// transition. By that, the caller can determine whether it may
			// create transitions for other children.
			
			//TODO Create some transitions that should not be executed. By
			//      that we can make sure that the statemachine executes
			//      only the transitions that should be executed.
			// Examples of such transitions:
			// - shadowed transitions (parent or parallel region 'jumps' away,
			//   so the transition won't executed)
			// - activated by a transition in the parent (source is activated
			//   only by a transition that happens somewhere else). Two types:
			//   - transition happens in the parent; initial state is activated
			//   - transition happens in a child, but activates the initial
			//     state of a parent
			//   - parent 'jumps' to a child and activates several layers of
			//     children and initial states below the target
			// - slightly different activation, e.g. condition or event differs
			// - same source and activation but different priority. Must have
			//   a lower priority, so it doesn't change the way, but in the
			//   list of transitions such transitions should appear before
			//   and after the 
			
			if (waypoint.children.isEmpty())
				transition_probability_per_level = transition_probability_leaf;
			
			if (random.nextDouble() < transition_probability_per_level) {
				// choose a target
				State source = waypoint.active_state;
				State destination;
				if (random.nextDouble() < transition_probability_far_target)
					destination = chooseDestination(all_states);
				else
					destination = chooseDestination(source.getParent().getStates());
				
				// create the transition
				createTransition(source, destination, activation);
				
				//NOTE Do not create transitions for any children because they would be shadowed
				//     by the transition that has been created here.
				
				return StateVariable.findCommonParent(source, destination);
			} else {
				for (Waypoint child : waypoint.children) {
					StateScope common_parent = createWaypointTransitions(child, activation, transition_probability_per_level);
					if (isParentOf(common_parent, waypoint.active_state.getParent()))
						return common_parent;
				}
				
				return waypoint.active_state;
			}
		}

		private State chooseDestination(List<State> states) {
			if (states.isEmpty() || states.size() == 1 && states.get(0) instanceof InitialState)
				throw new IllegalArgumentException("The list doesn't have any usable states");
			
			State state;
			do {
				state = choose(states);
			} while (state instanceof InitialState);
			
			return state;
		}

		private Transition createTransition(State source, State destination,
				TransitionActivation activation) {
			Transition t = addTransition(source, destination);
			
			t.setTransitionInfo(activation.getTransitionInfo());
			
			statemachine.getTransitions().add(t);
			
			activation.activated_transitions.add(t);
			
			return t;
		}

		private boolean isParentOf(StateScope parent, StateScope child) {
			if (parent == child)
				return false;
			
			StateParent real_parent = child.getParent();
			while (real_parent != null) {
				if (real_parent == parent)
					return true;
				
				real_parent = real_parent.getParent();
			}
			
			return false;
		}

		private boolean hasActiveTransitions(Waypoint waypoint, TransitionActivation activation) {
			// is any of our own transitions active?
			State state = waypoint.active_state;
			List<Transition> ts = transitions.get(state);
			if (ts != null) {
				for (Transition t : ts)
					if (activation.isActiveFor(t))
						return true;
			}
			
			// do the children have active transitions?
			for (Waypoint child : waypoint.children)
				if (hasActiveTransitions(child, activation))
					return true;
			
			// no active transition could be found
			return false;
		}
		
		//TODO for validation: Those transitions are forbidden:
		// - transition to a parallel region
		// - probably all transitions that go into regions
		// - transitions from parents to children, if there is more than one region

		//TODO create lists of entered and left states (both to self and to all); we can use that information
		//     to test the execution count of actions; probably DURING and ALWAYS, as well
		private Waypoint applyTransitions(Waypoint waypoint,
				TransitionActivation activation) {
			// find transitions for the current state (highest priority)
			State state = waypoint.active_state;
			List<Transition> ts = transitions.get(state.getName());
			Transition active_transition = null;
			if (ts != null) {
				for (Transition t : ts)
					if (activation.isActiveFor(t))
						if (active_transition == null || active_transition.getPriority() > t.getPriority())
							active_transition = t;
			}
			
			// If we have a transition, we execute it.
			if (active_transition != null) {
				Waypoint next_waypoint = getInitialWaypointForState(active_transition.getDestination());
				
				if (isParentOf(waypoint.active_state.getParent(), next_waypoint.active_state.getParent())) {
					// The next waypoint is deeper than the source and it is within the same parent. We have
					// to copy the layers above that state.
					next_waypoint = copyLayersUpTo(next_waypoint, waypoint);
				}
				
				// don't consider any transitions for the children because they are shadowed by this transition
				return next_waypoint;
			}
			
			// The current states doesn't have an active transition, so the children get their chance.
			List<Waypoint> updated_children = new ArrayList<StatemachineBuilder.Waypoint>(waypoint.children.size());
			for (Waypoint child : waypoint.children) {
				Waypoint updated_child = applyTransitions(child, activation);
				
				if (updated_child.active_state.getParent() != child.active_state.getParent()) {
					// This transition has led us outside of this state. Therefore, all the states that
					// we may have entered here, have been left now.
					//TODO We should still make sure that those states have been entered briefly. We have
					//      to pass that information to the tests in some way.
					return updated_child;
				}
				
				updated_children.add(updated_child);
			}
			
			// return a waypoint with the updated children and the old state for this level
			return new Waypoint(state, updated_children);
		}

		private Waypoint copyLayersUpTo(Waypoint updated_child_waypoint,
				Waypoint expected_level_and_previous_states) {
			// are we at the expected level?
			if (expected_level_and_previous_states.active_state.getParent() == updated_child_waypoint.active_state.getParent())
				return updated_child_waypoint;
			
			// find parent of updated_child_waypoint that is on the current level
			for (StateScope parent : StateVariable.getParents(updated_child_waypoint.active_state)) {
				if (parent.getParent() == expected_level_and_previous_states.active_state.getParent()) {
					// This is the way to go to. If it is the same state as before, we update the children
					// using the old waypoint expected_level. If we have changed the state here, we use
					// the initial state for that new state.
					if (parent != expected_level_and_previous_states.active_state)
						expected_level_and_previous_states = getInitialWaypointForState((State)parent);

					// This level is copied and the children are updated recursively.
					List<Waypoint> updated_children = new ArrayList<StatemachineBuilder.Waypoint>(expected_level_and_previous_states.children.size());
					for (Waypoint child : expected_level_and_previous_states.children) {
						updated_children.add(copyLayersUpTo(updated_child_waypoint, child));
					}
					
					// Return the updated waypoint.
					return new Waypoint(expected_level_and_previous_states.active_state, updated_children);
				}
			}
			
			// We couldn't find the parent in here, so we are in a branch that is not affected by
			// the changes. This waypoint remains the same as before.
			return expected_level_and_previous_states;
		}

		private void removeOrphans(StatemachineWithWay smw) {
			DepthFirstSearch dfs = new DepthFirstSearch(transitions) {
				@Override
				public Iterable<StateScope> getDestinationsFor(StateScope state) {
					List<StateScope> reachable = new LinkedList<StateScope>();
					
					// If the state is entered, the parent is active as well. Therefore,
					// the parent is reachable, if the child is.
					StateScope parent = state.getParent();
					if (parent != null)
						reachable.add(parent);
					
					// A superstate can reach its regions because they will be
					// activated whenever the superstate becomes active.
					if (state instanceof SuperState)
						for (Region region : ((SuperState)state).getRegions())
							reachable.add(region);
					
					// If a region is the destination of a transition, the first state
					// will be activated. A region can be activated in other ways, so
					// we may have a false positive here.
					if (state instanceof Region || state instanceof StateMachine) {
						State first_state = StateMachineForGeneration.findFirstState(
								statemachine, ((StateParent)state).getStates());
						if (first_state != null)
							reachable.add(first_state);
					}
					
					// Of course states can be reached by transitions. This is implemented
					// in the super class, so we simply call that method.
					if (state instanceof State)
						for (StateScope destination : super.getDestinationsFor(state))
							reachable.add(destination);
					
					return reachable;
				}
			};
			
			dfs.searchFrom(smw.statemachine);

			// find states that are not connected
			Set<State> orphans = new HashSet<State>();
			for (State state : all_states) {
				if (!dfs.visited(state))
					orphans.add(state);
			}
			
			// remove the orphans
			for (State orphan : orphans) {
				orphan.getParent().getStates().remove(orphan);
				all_states.remove(orphan);
			}
			
			// remove transitions of orphaned nodes
			Iterator<Transition> it = statemachine.getTransitions().iterator();
			while (it.hasNext()) {
				Transition t = it.next();
				if (orphans.contains(t.getSource()) || orphans.contains(t.getDestination()))
					it.remove();
			}
		}

		/*private void removeOrphans(StateParent parent) {
			// do the same thing for all regions in superstates
			for (State child : parent.getStates()) {
				if (child instanceof SuperState) {
					for (Region region : ((SuperState)child).getRegions()) {
						removeOrphans(region);
					}
				}
			}
			
			// find initial state
			InitialState initial_state = null;
			for (State state : parent.getStates()) {
				if (state instanceof InitialState) {
					initial_state = (InitialState)state;
					break;
				}
			}
			
			// find states that are connected to the initial state
			DepthFirstSearch dfs = new DepthFirstSearch();
			dfs.searchFrom(initial_state, transitions);

			// find states that are not connected
			List<State> orphans = new LinkedList<State>();
			for (State state : parent.getStates()) {
				if (!dfs.visited(state))
					orphans.add(state);
			}
			
			// remove the orphans
			for (State orphan : orphans)
				parent.getStates().remove(orphan);
		}*/
	}

	public static void save(StateMachine sm, String statecharts_file) throws IOException {
		save(sm, URI.createURI(statecharts_file));
	}
	
	public static void save(StateMachine sm, URI statechart_file) throws IOException {
	    // initialize the model
	    StatechartsPackage.eINSTANCE.eClass();
	    StatechartsFactory.eINSTANCE.eClass();
	    
	    // register XMI resource factory for .statecharts extension
	    Resource.Factory.Registry res_factory_registry = Resource.Factory.Registry.INSTANCE;
	    Map<String, Object> m = res_factory_registry.getExtensionToFactoryMap();
	    m.put("statecharts", new XMIResourceFactoryImpl());

	    // create resource
	    Resource resource = new ResourceSetImpl().createResource(statechart_file);
	    
	    // save it
	    resource.getContents().add(sm);
	    resource.save(Collections.emptyMap());
	}
	
	public interface ExportFilter {
		boolean include(State state);
		boolean include(Transition transition);
	}

	private static void exportDot(StateMachine sm, String filename,
			Map<Transition, String> transition_attrs, Map<State, String> state_attrs) throws FileNotFoundException {
		exportDot(sm, filename, transition_attrs, state_attrs, null);
	}

	private static void exportDot(StateMachine sm, String filename,
			Map<Transition, String> transition_attrs, Map<State, String> state_attrs, ExportFilter filter) throws FileNotFoundException {
		PrintStream w = new PrintStream(filename);
		w.println("digraph mygraph {");
		
		for (State state : sm.getStates())
			exportDot(w, "\t", state, state_attrs, filter);
		
		for (Transition t : sm.getTransitions())
			exportDot(w, "\t", t, transition_attrs, filter);
		
		w.println("}");
		w.close();
	}

	private static void exportDot(PrintStream w, String indent, State state,
			Map<State, String> state_attrs, ExportFilter filter) {
		if (filter != null && !filter.include(state))
			return;
			
		String statename = getStateNameForDot(state);
		String style = state_attrs.get(state);
		
		if (state instanceof SuperState) {
			SuperState super_state = (SuperState) state;
			w.println(indent + "subgraph cluster_" + statename + " {");
			w.println(indent + "\tstyle=solid;");
			
			String superstate_style = "shape=rectangle";
			if (style != null)
				superstate_style += "," + style;
			w.println(indent + "\t" + statename + "[" + superstate_style + "];");
			
			for (Region region : super_state.getRegions()) {
				w.println(indent + "\tsubgraph cluster_" + statename + "_" + region.getName() + " {");
				w.println(indent + "\tstyle=dashed;");
				
				for (State child : region.getStates())
					exportDot(w, indent+"\t\t", child, state_attrs, filter);
				
				w.println(indent + "\t}");
			}
			
			w.println(indent + "}");
		} else {
			if (state instanceof InitialState || state instanceof FinalState) {
				String extra_style = "shape=diamond";
				if (style != null)
					style = extra_style + "," + style;
				else
					style = extra_style;
			}
			
			w.println(indent + statename + (style != null ? " [" + style + "]" : "") + ";");
		}
	}

	private static void exportDot(PrintStream w, String indent, Transition transition,
			Map<Transition, String> transition_attrs, ExportFilter filter) {
		if (filter != null && (!filter.include(transition) || !filter.include(transition.getSource()) || !filter.include(transition.getDestination())))
			return;
		
		String style = transition_attrs.get(transition);
		String source = getStateNameForDot(transition.getSource());
		String destination = getStateNameForDot(transition.getDestination());
		
		String t_info = transition.getTransitionInfo();
		if (t_info != null) {
			if (style != null)
				style += ",label=\"" + t_info + "\"";
			else
				style = "label=\"" + t_info + "\"";
		}
		
		w.println(indent + source + " -> " + destination + (style != null ? " [" + style + "]" : "") + ";");
	}

	private static String getStateNameForDot(StateScope state) {
		if (state instanceof StateMachine)
			return null;
		else if (state instanceof NamedItem) {
			StateParent parent = state.getParent();
			String parent_name = (parent != null ? getStateNameForDot(parent) : null);
			String name = ((NamedItem)state).getName();
			if (parent_name != null)
				name = parent_name + "_" + name;
			return name;
		} else
			throw new IllegalArgumentException("Expecting a StateMachine or something with a name (a state or region)");
	}

	public static List<State> getStatesInWaypoint(Waypoint waypoint) {
		List<State> states = new LinkedList<State>();
		getStatesInWaypoint(waypoint, states);
		return states;
	}
	
	public static Set<State> getStatesInWaypointSet(Waypoint waypoint) {
		HashSet<State> states = new HashSet<State>();
		getStatesInWaypoint(waypoint, states);
		return states;
	}
	
	private static void getStatesInWaypoint(Waypoint waypoint, Collection<State> states) {
		states.add(waypoint.active_state);
		
		for (Waypoint child : waypoint.children)
			getStatesInWaypoint(child, states);
	}
	
	public static Set<State> getCommonStates(Waypoint wp1, Waypoint wp2) {
		Set<State> states1 = getStatesInWaypointSet(wp1);
		Set<State> states2 = getStatesInWaypointSet(wp2);
		states1.retainAll(states2);	// intersection operation
		return states1;
	}

	private static Map<State, String> highlight(String style, Waypoint waypoint) {
		return highlight(style, waypoint, new HashMap<State, String>());
	}
	
	private static Map<State, String> highlight(String style, Waypoint waypoint,
			Map<State, String> attrs) {
		return highlight(style, getStatesInWaypoint(waypoint), attrs);
	}

	private static Map<State, String> highlight(String style,
			Iterable<State> states, Map<State, String> attrs) {
		for (State state : states)
			attrs.put(state, style);
		
		return attrs;
	}

	private static Map<Transition, String> highlight(String style,
			TransitionActivation transitionActivation) {
		return highlight(style, transitionActivation, new HashMap<Transition, String>());
	}

	private static Map<Transition, String> highlight(String style,
			TransitionActivation transitionActivation,
			Map<Transition, String> transition_attrs) {
		return highlight(style, transitionActivation.activated_transitions, transition_attrs);
	}

	private static Map<Transition, String> highlight(String style,
			List<Transition> transitions,
			Map<Transition, String> transition_attrs) {
		for (Transition transition : transitions)
			highlight(style, transition, transition_attrs);
		return transition_attrs;
	}

	private static void highlight(String style, Transition transition,
			Map<Transition, String> transition_attrs) {
		transition_attrs.put(transition, style);
	}

	public static void exportStep(StatemachineWithWay smw, int step, String dot_file) throws IOException {
		exportStep(smw, step, dot_file, false);
	}
	
	public static void exportStep(StatemachineWithWay smw, int step, String dot_file, boolean ignore_other_states) throws IOException {
		StateMachine sm = smw.statemachine;
		
		final Map<State, String> state_attrs = highlight("color=red,style=bold", smw.waypoints.get(step+1));
		highlight("color=blue,style=bold", smw.waypoints.get(step), state_attrs);
		highlight("color=purple,style=bold", getCommonStates(smw.waypoints.get(step), smw.waypoints.get(step+1)), state_attrs);
		Map<Transition, String> transition_attrs = highlight("color=red,style=bold", smw.transitions.get(step));
		
		ExportFilter filter = new ExportFilter() {
			@Override
			public boolean include(Transition transition) {
				return true;
			}
			
			@Override
			public boolean include(State state) {
				return state_attrs.containsKey(state);
			}
		};

		exportDot(sm, dot_file, transition_attrs, state_attrs, filter);
		Runtime.getRuntime().exec("dot -O -Tpdf " + dot_file);
	}

	public static void main(String[] args) throws IOException {
		int seed = 0;
		StatemachineBuilder builder = new StatemachineBuilder(seed);
		
		// shallow statemachine
		builder.nesting_depth = 2;
		builder.state_count_per_parent = new GaussianProbability(4, 2);
		builder.regions_per_superstate = new GaussianProbability(1, 1);
		
		//StateMachine sm = builder.build();
		//save(sm, "random_statechart_" + seed + ".statecharts");
		
		StatemachineWithWay smw = builder.buildStatemachineWithWay();
		StateMachine sm = smw.statemachine;
		//save(sm, "random_statechart_with_way_" + seed + ".statecharts");
		
		for (Waypoint waypoint : smw.waypoints)
			System.out.println(waypoint);
		
		boolean all_in_one = false;
		if (all_in_one) {
			int count = smw.transitions.size();
			Map<State, String> state_attrs = new HashMap<State, String>();
			Map<Transition, String> transition_attrs = new HashMap<Transition, String>();
			for (int i=0;i<count;i++) {
				String color = String.format("%f,%f,%f", 1.0/(count+1)*i, 1.0, 0.5);
				highlight("color=\"" + color + "\"", smw.waypoints.get(i), state_attrs);
				highlight("color=\"" + color + "\"", smw.transitions.get(i), transition_attrs);
			}
	
			String dot_file = "random_statechart_with_way_" + seed + ".dot";
			exportDot(sm, dot_file, transition_attrs, state_attrs);
			Runtime.getRuntime().exec("dot -O -Tpdf " + dot_file);
		} else {
			int count = smw.transitions.size();
			for (int i=0;i<count;i++) {
				Map<State, String> state_attrs = highlight("color=red,style=bold", smw.waypoints.get(i+1));
				highlight("color=blue,style=bold", smw.waypoints.get(i), state_attrs);
				highlight("color=purple,style=bold", getCommonStates(smw.waypoints.get(i), smw.waypoints.get(i+1)), state_attrs);
				Map<Transition, String> transition_attrs = highlight("color=red,style=bold", smw.transitions.get(i));
		
				String dot_file = "random_statechart_with_way_" + seed + "_step" + i + ".dot";
				exportDot(sm, dot_file, transition_attrs, state_attrs);
				Runtime.getRuntime().exec("dot -O -Tpdf " + dot_file);
			}
		}
	}
}
