package de.upbracing.code_generation.generators.fsm_test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import Statecharts.FinalState;
import Statecharts.InitialState;
import Statecharts.NormalState;
import Statecharts.Region;
import Statecharts.State;
import Statecharts.StateMachine;
import Statecharts.StateParent;
import Statecharts.StateWithActions;
import Statecharts.StatechartsFactory;
import Statecharts.StatechartsPackage;
import Statecharts.SuperState;
import Statecharts.Transition;

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
	
	public StatemachineBuilder(long seed) {
		random = new Random(seed);
		factory = getDefaultStatemachineFactory();
	}
	
	private static StatechartsFactory getDefaultStatemachineFactory() {
		return new Statecharts.impl.StatechartsFactoryImpl();
	}
	
	public StateMachine build() {
		return new Builder().build();
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
		
		public StateMachine build() {
			statemachine = factory.createStateMachine();
			statemachine.setBasePeriod("1ms");
			
			createChildrenStates(statemachine);
			
			createTransitions(statemachine);
			
			connectOrphans(statemachine);
			
			return statemachine;
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
			private HashSet<State> visited = new HashSet<State>();
			
			public void searchFrom(State state, Map<String, List<Transition>> transitions_by_source) {
				visited.add(state);
				update(transitions_by_source);
			}
			
			public void update(Map<String, List<Transition>> transitions_by_source) {
				Queue<State> queue = new ConcurrentLinkedQueue<State>();
				queue.addAll(visited);
				
				while (!queue.isEmpty()) {
					State state = queue.poll();
					
					List<Transition> ts = transitions_by_source.get(state.getName());
					if (ts != null) {
						for (Transition t : ts) {
							State destination = t.getDestination();
							if (!visited.contains(destination)) {
								visited.add(destination);
								queue.offer(destination);
							}
						}
					}
				}
			}
			
			public boolean visited(State state) {
				return visited.contains(state);
			}

			public Collection<State> getVisitedStates() {
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
			DepthFirstSearch dfs = new DepthFirstSearch();
			dfs.searchFrom(initial_state, transitions);

			// add transitions until all states are connected
			for (State state : parent.getStates()) {
				if (!dfs.visited(state)) {
					// add a connection
					List<State> candidates = new ArrayList<State>(dfs.getVisitedStates());
					
					String event_name;
					State source;
					State destination = state;
					do {
						event_name = choose(this.event_names);
						source = choose(candidates);
					} while (!canCreateTransition(event_name, source, destination));
					
					addTransition(source, destination).setTransitionInfo(event_name);
					
					dfs.update(transitions);
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
	}
	
	public static void main(String[] args) throws IOException {
		int seed = 0;
		StatemachineBuilder builder = new StatemachineBuilder(seed);
		StateMachine sm = builder.build();
		save(sm, "random_statechart_" + seed + ".statecharts");
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
}
