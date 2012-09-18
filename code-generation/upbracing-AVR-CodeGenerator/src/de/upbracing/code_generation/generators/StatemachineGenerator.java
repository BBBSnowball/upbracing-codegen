package de.upbracing.code_generation.generators;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.regex.Pattern;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;

import Statecharts.FinalState;
import Statecharts.InitialState;
import Statecharts.NormalState;
import Statecharts.Region;
import Statecharts.State;
import Statecharts.StateParent;
import Statecharts.StateWithActions;
import Statecharts.SuperState;
import Statecharts.Transition;
import de.upbracing.code_generation.StatemachinesHeaderTemplate;
import de.upbracing.code_generation.config.MCUConfiguration;
import de.upbracing.code_generation.fsm.model.Action;
import de.upbracing.code_generation.fsm.model.ActionType;
import de.upbracing.code_generation.fsm.model.FSMParsers;
import de.upbracing.code_generation.fsm.model.ParserException;
import de.upbracing.code_generation.fsm.model.StateMachineForGeneration;
import de.upbracing.code_generation.fsm.model.StateVariable;
import de.upbracing.code_generation.fsm.model.StateVariablePurposes;
import de.upbracing.code_generation.fsm.model.TransitionInfo;

/**
 * Generator for statemachine code
 * 
 * @author benny
 * 
 */
public class StatemachineGenerator extends AbstractGenerator {
	public StatemachineGenerator() {
		super("statemachines.h", new StatemachinesHeaderTemplate(),
				"statemachines.c", new StatemachinesCFileTemplate());
	}

	@Override
	public boolean validate(MCUConfiguration config,
			boolean after_update_config, Object generator_data) {
		boolean valid = true;
		
		// make sure that we don't have any unexpected null values
		for (StateMachineForGeneration smg : config.getStatemachines()) {
			if (!validateNotNull(smg.getGlobalCodeBoxes(), "global code boxes"))
				return false;
			
			if (!validateNotNull(smg.getStates(), "states"))
				return false;
			
			if (!validateNotNull(smg.getTransitions(), "transitions"))
				return false;
			
			if (!validateStatesNotNull(smg.getStates()))
				return false;
			
			for (Transition t : smg.getTransitions()) {
				if (t.getSource() == null || t.getDestination() == null) {
					System.err.println("FATAL: Each transition must have a valid source and destination. Invalid transition: " + t);
					return false;
				}
			}
		}
		
		// make sure that we have the appropiate values on the root object
		for (StateMachineForGeneration smg : config.getStatemachines()) {
			if (smg.getBasePeriodAsString() == null) {
				System.err.println("ERROR: Statemachine needs a base rate");
				
				// set some rate because the program will crash, if it find a null in there
				smg.setBasePeriod("1s");
			}
			
			try {
				smg.getBasePeriod();
			} catch (ParserException e) {
				System.err.println("Cannot parse time ('" + smg.getBasePeriodAsString()
						+ "'): " + e.toString());
				
				smg.setBasePeriod("1s");
			}
		}

		if (!after_update_config) {
			for (StateMachineForGeneration smg : config.getStatemachines()) {
				// TODO for Rishab: validate state machines
				// - statemachine names, regions and event names are valid C
				// identifiers; however, all
				// of them except the statemachine name can be empty
				// done // - no edges going to initial states or starting at
				// final states
				// done // - initial states have exactly one transition
				// done // - edges from initial states cannot have events, wait
				// conditions or conditions
				// - if an edge has a condition or an event, the waitType cannot
				// be "wait" ("after" and all the others are ok)
				// - if an edge has waitType=="before", it must have a condition
				// or an event (or both)
				// Probably, you will find some more things to check.

				// If an error is detected, the validator should continue and
				// display all
				// other errors.

				// For each error, print a meaningful message and the location.
				// The location could
				// look like this: "SuperState abc -> Region x -> State foo"

				// statemachine validation

	String statemachine_name = smg.getName();
				
				if(statemachine_name == null){
					System.err
							.println("There is no statemachine!");
				}

				//validate statemachine name
				nameValidator(statemachine_name);
				
				//event validation
				SortedMap<String, Set<Transition>> events;
				
				if (smg.getEvents().keySet().size()> 0)
					{	
					events = smg.getEvents();
					Iterator<String> event_names = events.keySet().iterator();
					
					while(event_names.hasNext())
						eventNameValidator(statemachine_name, event_names.next());
					}
				
				// state validation
				List<State> states;
				
				EList<Transition> out_transitions;
				EList<Transition> in_transitions;
				EList<FinalState> final_states = new BasicEList<FinalState>();
				EList<FinalState> super_final_states = new BasicEList<FinalState>();
				
				int initial_state_count = 0;
				
				if(smg.getStates().isEmpty()){
					System.err
							.println("Statemachine -> "
									+ statemachine_name
									+ " | Statemachine does not have any states!");
				}
				else
				{	
					
					states = smg.getStates();
					
				for (int i = 0; i < states.size(); i++) {
					State state = states.get(i);
					
					
					// initial state validity
					if (state instanceof InitialState) {
						 InitialState initial_state = (InitialState) state;
						 
						 // number of initial states
						 initial_state_count++;
						 
						 if(initial_state_count != 1){
						    	System.err
						    			.println("Statemachine -> " 
						    					+ statemachine_name 
						    					+ " | Statemachine must have only one initial state.");
						    			}
						
						 //initialstate name validation ??
						 //initialstate name validation ??
							nameValidator(statemachine_name, initial_state.getName());
							  
							 // no incoming transitions
							incomingTransitionCounter(initial_state, statemachine_name);
							 
							//check whether outgoing transitions exist
							if(outgoingTransitionCounter(initial_state, statemachine_name)){
								out_transitions = initial_state.getOutgoingTransitions();
																  				    
							for (Transition t : out_transitions) 
								
								//validate outgoing transitions
								initialTransitionValidator(smg.getTransitionInfo(t), statemachine_name, t.getSource().toString(), t.getDestination().toString());		
							}
						}
					
					// final state validity
					else if (state instanceof FinalState) {
						FinalState final_state = (FinalState) state;
						final_states.add(final_state);
												
						//name validation
					    nameValidator(statemachine_name, final_state.getName());
					        			 
						//check whether final state has incoming transitions
					    incomingTransitionCounter(final_state, statemachine_name);
					    outgoingTransitionCounter(final_state, statemachine_name);
					    
						
						// removing the previous final state and setting its incoming transitions to the current 
						// final state. Then finally removing the previous final state from the list
						
						//if incoming and outgoing transitions exist but number of states greater than 1
						if(final_states.size() > 1 
								&& incomingTransitionCounter(final_state, statemachine_name)
									&& outgoingTransitionCounter(final_state, statemachine_name)){
									
								EList<Transition> previous_final_transitions = final_states.get(0).getIncomingTransitions();
							
								for(Transition t : previous_final_transitions){
									t.setDestination(final_state);
									}
							
								final_states.set(0, final_state);
								updateConfig(config);
							}
						
						//transition validity 
						if(incomingTransitionCounter(final_state, statemachine_name)){
						
						in_transitions = final_state.getIncomingTransitions();
						
						for (Transition t : in_transitions) {
							TransitionInfo ti = smg.getTransitionInfo(t);
													
							//check whether the incoming transition is from initial state
							if(!(t.getSource() instanceof InitialState))
								
								normalTransitionValidator(ti, statemachine_name, t.getSource().toString(), t.getDestination().toString());
						}
					}
				}
			

					// normal state validity
					else if (state instanceof NormalState) {
						NormalState normal_state = (NormalState) state;
						
						//check normal state name
						//check for normal states having same name?
						nameValidator(statemachine_name, normal_state.getName());
						
						//check whether normal state has incoming transitions
						incomingTransitionCounter(normal_state, statemachine_name);
						outgoingTransitionCounter(normal_state, statemachine_name);
						
		
						//perform validation of specific inncoming transitions if they exist
						if(incomingTransitionCounter(normal_state, statemachine_name)){
							
							in_transitions = normal_state.getIncomingTransitions();	        		        			
						
							//validation of incoming transitions
						for (Transition t : in_transitions) {
							TransitionInfo transitionInfo = smg.getTransitionInfo(t);
														
							//ensure that incoming transition is not from initial state
							if(!(t.getSource() instanceof InitialState))
								
							//transition type validation
							normalTransitionValidator(transitionInfo, statemachine_name, t.getSource().toString(), t.getDestination().toString());
					}
				}
						
					//check rules for outgoing transitions if they fo exist	
					if(outgoingTransitionCounter(normal_state, statemachine_name)){
						out_transitions = normal_state.getIncomingTransitions();
		
						//validation of outgoing transitions
						for(Transition t : out_transitions){
							TransitionInfo transitionInfo = smg.getTransitionInfo(t);
							
							normalTransitionValidator(transitionInfo, statemachine_name, t.getSource().toString(), t.getDestination().toString());				    			 	
						}
					}
				}
					
					else if (state instanceof SuperState) {
						SuperState super_state = (SuperState) state;
						
						// Superstate name validity check
						nameValidator(statemachine_name, super_state.getName());
						
						//check whether the superstate has outgoing transitions
						outgoingTransitionCounter(super_state, statemachine_name);
						
						//check whether the superstate has incoming transitions
						incomingTransitionCounter(super_state, statemachine_name);
						
						//check specific rules of incoming transitions of superstate if they do exist
						if(incomingTransitionCounter(super_state, statemachine_name)){
						
							in_transitions = super_state.getIncomingTransitions();
					    // incoming transition validation
						for(Transition t : in_transitions){
							TransitionInfo transitionInfo = smg.getTransitionInfo(t);
							
							//check whether incoming transition is from start state
							if(!(t.getSource() instanceof InitialState))
															
								normalTransitionValidator(transitionInfo, statemachine_name, t.getSource().toString(), t.getDestination().toString());
							}		
						}	
						//check specific rules of outgoing transitions of superstate if they do exist
						if(outgoingTransitionCounter(super_state, statemachine_name)){
						
						out_transitions = super_state.getIncomingTransitions();
					    //validation for outgoing transitions
						for(Transition t : out_transitions){
							
							TransitionInfo transitionInfo = smg.getTransitionInfo(t);
							normalTransitionValidator(transitionInfo, statemachine_name, t.getSource().toString(), t.getDestination().toString());					 	

							}
						}
						
						//check whether the superstate has any regions
						if(super_state.getRegions().size() == 0)
							System.err.println("Statemachine -> " + statemachine_name + " | SuperState -> " + 
									super_state.getName() + " | Superstate must have atleast one region.");
						
						else {
						//get regions of the superstate
						EList<Region> super_regions = super_state.getRegions();

						for (Region r : super_regions) {
							
							//validity of region name
							nameValidator(statemachine_name, super_state.getName(), r.getName());
			
							//check whether the region has states
							if(r.getStates().isEmpty())
								System.err.println("Statemachine -> " + statemachine_name 
					        					+ " | State name -> "  + super_state.getName() 
					        					+ " | Region name -> " + r.getName()
					        					+ " | Does not have any states.");
							
							//if the region has states perform validity for each of the contained states
							else{
							
							EList<State> region_states = r.getStates();
							for (State s : region_states) {
								{
									int region_inital_count = 0;
									
									InitialState initial_state_region = null;
									
									//validation of initial state in the region
									if(s instanceof InitialState){
										initial_state_region = (InitialState) s;
										
										//validation of name
										nameValidator(statemachine_name, super_state.getName(), r.getName(), 
												initial_state_region.getName());
										
								       
										//check whether any outgoing transitions exist
										incomingTransitionCounter(initial_state_region, statemachine_name);
																			
									    region_inital_count++;
									    
									    //number of inital states in the region
									    if( region_inital_count != 1){
									    	System.err.println("Statemachine -> " + statemachine_name + " | SuperState -> " + super_state.getName() + " | Region ->" +
									    			r.getName() +" | Statemachine must have only one initial state.");
									    }
									    
									    //if the initial state has outgoing transitions perform checks
										if(outgoingTransitionCounter(initial_state_region, statemachine_name)){
										
											out_transitions = initial_state_region.getOutgoingTransitions();    
										
									    for(Transition t : out_transitions){
									    	TransitionInfo transitionInfo = smg.getTransitionInfo(t);
									    	
									    	initialTransitionValidator(transitionInfo, statemachine_name, t.getSource().toString()
									    			, t.getDestination().toString(), super_state.getName(), r.getName());
									    	
									    
									    	if(initial_state_region.getOutgoingTransitions().size()>1)
										    	System.err.println("Statemachine -> " + statemachine_name 
										    					+ " | SuperState -> " + super_state.getName() 
										    					+ " | Region -> " + r.getName() 
										    					+ " | State  -> " + initial_state_region.getName()
										    					+ " : Initial state cannot have more than one outgoing transitions!");
										}
									}
								}
									
									else if (s instanceof FinalState){
										FinalState final_state_region = (FinalState) s;
										super_final_states.add(final_state_region);
										
										//validation of name
										nameValidator(statemachine_name, super_state.getName(), r.getName(),
												final_state_region.getName());
										
									 	//check whether there are any incoming transitions
										incomingTransitionCounter(final_state_region, statemachine_name);
										
									 	//check whether there are any outgoing transitions
										outgoingTransitionCounter(final_state_region, statemachine_name);
										
										//check whether there are more than one final states
										if(super_final_states.size() > 1
												&& incomingTransitionCounter(final_state_region, statemachine_name)
												&& (!outgoingTransitionCounter(final_state_region, statemachine_name))){
											
											EList<Transition> transition = super_final_states.get(0).getIncomingTransitions();
											
											for(Transition trans : transition){
												trans.setDestination(final_state_region);
											}
											
											super_final_states.set(0, final_state_region);
											updateConfig(config);
										}
										
										//if incoming transitions exist then perform checks
										if(incomingTransitionCounter(final_state_region, statemachine_name)){
									    
										in_transitions = final_state_region.getIncomingTransitions();
									    
										 for(Transition t : in_transitions){
											 TransitionInfo transitionInfo = smg.getTransitionInfo(t);
											 
											
											 //test whether the source is an initial state
											 if(!(t.getSource() instanceof InitialState))	
												
												 normalTransitionValidator(transitionInfo, statemachine_name, t.getSource().toString(),
														 t.getDestination().toString(), super_state.getName(), r.getName());
												 	 	
										 }
										}
									}
											 
									else if(s instanceof NormalState){
										
										NormalState normal_state_region = (NormalState) s;
										
										//check validity of normal state name
										nameValidator(statemachine_name, super_state.getName(), r.getName(),
												normal_state_region.getName());
										
										//check whether the state has incoming or outgoing transitions
										incomingTransitionCounter(normal_state_region, statemachine_name);
										outgoingTransitionCounter(normal_state_region, statemachine_name);
										
										//perform checks on incoming transitions if they exist
										if(!emptyOrNull(normal_state_region.getIncomingTransitions().toString())){
											
											in_transitions = normal_state_region.getIncomingTransitions();
									    
										for(Transition t : in_transitions){
											TransitionInfo transitioninfo = smg.getTransitionInfo(t);
											
											if(!(t.getSource() instanceof InitialState)){
												
												normalTransitionValidator(transitioninfo, statemachine_name, t.getSource().toString(),
														t.getDestination().toString(), super_state.getName(), r.getName());
										 	
											}
										}
									}
										
										//perform checks on outgoing transitions if they exist
										if(outgoingTransitionCounter(normal_state_region, statemachine_name)){
											
											out_transitions = normal_state_region.getOutgoingTransitions();
										for(Transition t : out_transitions){
											TransitionInfo transitioninfo = smg.getTransitionInfo(t);
											
											if(!(t.getDestination() instanceof InitialState))
												normalTransitionValidator(transitioninfo, statemachine_name, t.getSource().toString(),
														t.getDestination().toString(), super_state.getName(), r.getName());
										
												}									
										}
						}
								}
					}
				}
				}
				}
				}
				}
			}
					}
		}
	return valid;
	}
	
	private boolean validateStatesNotNull(Iterable<State> states) {
		for (State state : states) {
			validateNotNull(state.getIncomingTransitions(), "incoming transitions of " + state);
			validateNotNull(state.getOutgoingTransitions(), "outgoing transitions of " + state);
			
			if (state instanceof SuperState) {
				for (Region region : ((SuperState)state).getRegions()) {
					if (!validateStatesNotNull(region.getStates()))
						return false;
				}
			}
		}
		
		return true;
	}

	private boolean validateNotNull(Iterable<?> xs, String list_name) {
		for (Object x : xs) {
			if (x == null) {
				System.err.println("FATAL: At least one element in the list of " + list_name + " is null");
				return false;
			}
		}
		
		return true;
	}

	private boolean emptyOrNull(String obj) {
		return obj == null || obj.isEmpty();
	}

	@Override
	public Object updateConfig(MCUConfiguration config) {
		updateParents(config);
		assignNames(config);
		addStateVariables(config);
		convertWaitToActionsAndConditions(config);

		return super.updateConfig(config);
	}

	private void updateParents(MCUConfiguration config) {
		// Unfortunately, the parents of most elements are not set
		// automatically. We correct this here. The information
		// remains valid because the structure of the statemachine
		// won't change anymore.

		for (StateMachineForGeneration smg : config.getStatemachines()) {
			updateParents(smg.getStates(), smg.getStateMachine());
		}
	}

	private void updateParents(Iterable<State> states, StateParent parent) {
		for (State state : states) {
			state.setParent(parent);

			if (state instanceof SuperState) {
				SuperState superstate = (SuperState)state;
				for (Region region : superstate.getRegions()) {
					region.setParent(superstate);
					
					updateParents(region.getStates(), region);
				}
			}
		}
	}

private void nameValidator(String nametobevalidated, String...names){
	Pattern digit = Pattern.compile("\\D\\w*");
	String[] name = names;
	
	if(!(digit.matcher(nametobevalidated).matches())
				|| emptyOrNull(nametobevalidated)){
		switch(name.length){
			case 0:
				System.err.println("Statemachine name-> " + nametobevalidated + " is not a valid C identifier!");
				break;
			case 1:
				System.err.println("Statemachine -> " + name[0] + " | State name -> " + nametobevalidated + " is not a valid C identifier!");
				break;
			case 2:
				System.err.println("Statemachine -> " + name[0] + " | SuperState -> " + name[1] 
					+ " | Region name " + nametobevalidated + " is not a valid C identfier");
				break;
			case 3:
				System.err.println("Statemachine -> " + name[0] + " | SuperState -> " + name[1] 
					+ " | Region -> " + name[2] + " | State name " + nametobevalidated + " is not a valid C identfier");
			}
		}
}

//event name validator
private void eventNameValidator(String statemachine, String event){
	Pattern digit = Pattern.compile("\\D\\w*");
	
	if(!(digit.matcher(event).matches()) &&
			(!event.isEmpty())){
		System.err.println("Statemachine -> " + statemachine + " | Event name -> "+ 
				event + " is not a valid C identifier!");
	}
}


private void initialTransitionValidator(TransitionInfo transitionInfo, String statemachine_name, String source,
		String destination, String...names){
	
	String[] name = names;
	
	switch(name.length){
	case 0:
		if (transitionInfo.isWaitTransition())
			System.err.println("Statemachine -> " + statemachine_name
								+ " | Transition -> " + "Source : " + source + " Destination : " + destination
								+ " : Start state cannot have waiting transition!");

		else if (!emptyOrNull(transitionInfo.getEventName()))
			System.err.println("Statemachine -> " + statemachine_name
								+ " | Transition -> " + "Source : " + source + " Destination : " + destination
								+ " : Start state cannot have events!");

		else if (!emptyOrNull(transitionInfo.getCondition()))
			System.err.println("Statemachine -> " + statemachine_name
								+ " | Transition -> " + "Source : " + source + " Destination : " + destination
								+ " : Start state cannot have conditions !");
		break;
	
	case 1:
		break;
		
	case 2:
		
		if(transitionInfo.isWaitTransition())
    		System.err.println("Statemachine -> " + statemachine_name 
    						+ " | SuperState -> " + name[0] 
    						+ " | Region -> "  + name[1] 
    						+ " | Transition -> Source : "  + source + " Destination : " + destination 
    						+ " : Start state cannot have waiting transition!");
    	
    	else if(!emptyOrNull(transitionInfo.getEventName()))
    		System.err.println("Statemachine -> " + statemachine_name 
							+ " | SuperState -> " + name[0] 
							+ " | Region -> "  + name[1] 
							+ " | Transition -> Source : "  + source + " Destination : " + destination 
							+ " : Start state cannot have events!");
    	
    	else if(!emptyOrNull(transitionInfo.getCondition()))
    		System.err.println("Statemachine -> " + statemachine_name 
							+ " | SuperState -> " + name[0] 
							+ " | Region -> "  + name[1] 
							+ " | Transition -> Source : "  + source + " Destination : " + destination 
							+ " : Start state cannot have conditions !");
    	
		
	}	
}

private void normalTransitionValidator(TransitionInfo ti, String statemachine_name, String source,
		String destination, String...names){
	
	String[] name = names;
	
	switch(name.length){
	case 0:
		if (!emptyOrNull(ti.getCondition()) || !emptyOrNull(ti.getEventName())) {
			// check wait type is "wait"?
			if (ti.getWaitType() == "wait")
				System.err.println("Statemachine -> " + statemachine_name
								+ " | Transition -> Source : " + source + " Destination : " + destination
								+ " Transition having condition or event cannot have wait type wait!");
		}
		
		// ensure that when wait type is "before" either
		// getcondition is true or event is true
		else if (ti.getWaitType() == "before") {
			if (ti.getCondition().isEmpty() && ti.getEventName().isEmpty()) {
				System.err.println("Statemachine -> " + statemachine_name
								+ " | Transition -> Source : " + source + " Destination : " + destination
								+ " Transition must either have a condition or an event!");
			}
		}
		
		break;
		
	case 1:
		break;
		
	case 2:
			if(!ti.getCondition().isEmpty() || !ti.getEventName().isEmpty())
		 		
		 		//check wait type is "wait"?
		 		if(ti.getWaitType().equals("wait"))
		 			System.err.println("Statemachine -> " + statemachine_name 
		 							+ " | SuperState -> " + name[0] 
		 							+ " | Region -> " + name[1] 
		 							+ " | Transition -> Source : " + source + " Destination : " + destination 
		 							+ " Transition having condition or event cannot have wait type wait!");
		 	
		 	
		 	// ensure that when wait type is "before" either getcondition is true or event is true
		 	else if(ti.getWaitType().equals("before")){
		 		if(ti.getCondition().isEmpty() && ti.getEventName().isEmpty()){
		 			System.err.println("Statemachine -> " + statemachine_name 
 									+ " | SuperState -> " + name[0] 
 									+ " | Region -> " + name[1] 
 									+ " | Transition -> Source : " + source + " Destination : " + destination 
		 							+ " Transition must either have a condition or an event!");
		 		}
		 	}
			break;
		}
}


private boolean incomingTransitionCounter(State state, String statemachine_name){
	
	boolean hasicoming = false;
	
	if(state instanceof InitialState){
		 if(!((InitialState) state).getIncomingTransitions().isEmpty())
			 System.err.println("Statemachine -> " + statemachine_name 
        	 				+ " | State name -> " + ((InitialState) state).getName() 
        	 				+ " Initial state does not have any incoming transitions! ");
		 else
			 hasicoming = true;
	}
	else if(state instanceof FinalState){
		if(((FinalState) state).getIncomingTransitions().isEmpty())
			System.err.println("Statemachine -> " 
        	 				+ statemachine_name
        	 				+ " | State name -> " + ((FinalState) state).getName()
        	 				+ " | Final state does not have any incoming transitions!");
		else
			hasicoming = true;
	}
	else if(state instanceof NormalState){
		if(((NormalState) state).getIncomingTransitions().isEmpty())
			System.err.println("Statemachine -> " 
	        	 			+ statemachine_name 
	        	 			+ "| State name -> " 
	        	 			+ ((NormalState) state).getName()
	        	 			+ " | Doesn't have any incoming transitions!");
		else 
			hasicoming = true;
	}
	else if (state instanceof SuperState){
		if (emptyOrNull(((SuperState) state).getIncomingTransitions().toString()))
			System.err.println("Statemachine -> " + statemachine_name 
							+ "| State name -> " + ((SuperState) state).getName()
							+ " | Doesn't have any incoming transitions!");
		else
			hasicoming = true;
	}
	return hasicoming;
}

private boolean outgoingTransitionCounter(State state, String statemachine_name){
	boolean hasoutgoing = false;
	
	if(state instanceof InitialState){
		if(((InitialState) state).getOutgoingTransitions().isEmpty())
			 System.err.println("Statemachine -> " + statemachine_name 
	        					+ "| State name -> "+ ((InitialState) state).getName()
	        					+ " does not have any outgoing transitions!");
		
		else if (((InitialState) state).getOutgoingTransitions().size() > 1) {
				System.err.println("Statemachine " + " -> " + statemachine_name 
								+ " | State " + " -> " + ((InitialState) state).getName()
								+ " : Initial state has more than one outgoing transitions!");
		hasoutgoing = true;
		}
		else
			hasoutgoing = true;
	}
	else if(state instanceof FinalState){
		if(!((FinalState) state).getOutgoingTransitions().isEmpty()) 
			System.err.println("Statemachine -> " + statemachine_name 
					+ " | State -> " + ((FinalState) state).getName()
					+ " : Validation failed.");
		else
			hasoutgoing = true;
	}
	else if (state instanceof NormalState){
		if (((NormalState) state).getOutgoingTransitions().isEmpty())
			System.err.println("Statemachine -> " 
							+ statemachine_name 
							+ "| State name -> " 
							+ ((NormalState) state).getName()
							+ " | Doesn't have any outgoing transitions!");
		
		else
			hasoutgoing = true;
	}
	else if(state instanceof SuperState){
		if (emptyOrNull(((SuperState) state).getOutgoingTransitions().toString()))
			System.err.println("Statemachine -> " + statemachine_name 
							+ "| State name -> " + ((SuperState) state).getName()
							+ " | Doesn't have any outgoing transitions!");
	}
	return hasoutgoing;
}

	private void assignNames(MCUConfiguration config) {
		int runningCounter = 0;
		for (StateMachineForGeneration smg : config.getStatemachines()) {
			// TODO for Rishab: Assign a name to each state and region that
			// doesn't have a
			// name, yet. You can generate unique names like this:
			// ("state" + (++runningCounter))

			// TODO for Rishab: Make sure that there is at most one final state
			// in each region
			// and top-level of a statemachine. If there is more than one final
			// state, remove it and update the transitions accordingly. There
			// can be more than one final state in a statemachine (e.g. one is
			// in a region and the other one on the top-level), so be careful
			// to set each transition to the right one.
			
//			
//			Iterator<String> event_names = smg.getEvents().keySet().iterator();
//			
//			while(event_names.hasNext()){
//				String event = event_names.next();
//				
//				if(emptyOrNull(event)){
//					event = "event" + "_" + runningCounter;
//					runningCounter++;
//				}
//			}
//			
			List<State> state_names = smg.getStates();
			
			for(State state : state_names){
				if ((state instanceof InitialState) 
						&& emptyOrNull(((InitialState) state).getName())){
					
					((InitialState) state).setName("initialState_" + runningCounter);
					runningCounter++;
				}
				else if (state instanceof FinalState 
						&& emptyOrNull(((InitialState) state).getName())){
						
					((FinalState) state).setName("finalState_" + runningCounter);
					runningCounter++;
				}
				else if (state instanceof NormalState
						&& emptyOrNull(((NormalState) state).getName())){
					
					((NormalState) state).setName("normalState_" + runningCounter);
					runningCounter++;
				}
				else if (state instanceof SuperState) {
					
					SuperState superstate = (SuperState) state;
					
					if(emptyOrNull((superstate).getName())){
						((SuperState) state).setName("superState_" + runningCounter);
						runningCounter++;
					}
					
					else if(!superstate.getRegions().isEmpty()){
						EList<Region> region = superstate.getRegions();
						
						for(Region reg : region){
							if(!reg.getStates().isEmpty()){
								EList<State> sub_states = reg.getStates();
								
								for(State sub : sub_states){
									if((sub instanceof InitialState)
											&& emptyOrNull(((InitialState) sub).getName())){
									
									((InitialState) state).setName("initialState_" + runningCounter);
									runningCounter++;
									}
									
									else if((sub instanceof FinalState) 
											&& emptyOrNull(((FinalState) sub).getName())){
										
									((FinalState) state).setName("finalState_" + runningCounter);
									runningCounter++;
									}
									
									//TODO for Rishab: ClassCastException in this code
									/*else if((sub instanceof NormalState)
											&& emptyOrNull(((FinalState) sub).getName())){
									
									((NormalState) state).setName("normalState_" + runningCounter);
									runningCounter++;
									}*/
								}
							}
						}
					}
				}
			}
		}
	}

	private void addStateVariables(MCUConfiguration config) {
		for (StateMachineForGeneration smg : config.getStatemachines()) {
			addStateVariables(smg, smg.getStateMachine());
		}
	}

	private void addStateVariables(StateMachineForGeneration smg,
			StateParent parent) {
		addStateVariable(smg, parent);
		
		for (State state : parent.getStates())
			if (state instanceof StateParent)
				addStateVariables(smg, (StateParent)state);
	}

	private void addStateVariable(StateMachineForGeneration smg, StateParent parent) {
		StringBuilder declaration = new StringBuilder();
		declaration.append("enum {\n");
		for (State state : smg.sortStatesForEnum(parent.getStates())) {
			if (state instanceof InitialState)
				continue;
			declaration.append("\t");
			declaration.append(smg.stateName(state));
			declaration.append(",\n");
		}
		declaration.append("}");
		
		StateVariable var = new StateVariable("state", StateVariable.TYPE_AUTONAME,
				declaration.toString(),
				parent);
		smg.getStateVariables().add(var, StateVariablePurposes.STATE, parent);
	}

	/**
	 * wait events can be implemented with actions and conditions. This method
	 * does the conversion so the code generator doesn't have to deal with this.
	 * 
	 * @param config
	 *            the configuration to convert
	 */
	private void convertWaitToActionsAndConditions(MCUConfiguration config) {
		for (StateMachineForGeneration smg : config.getStatemachines()) {
			Set<StateWithActions> statesWithWait = new HashSet<StateWithActions>();

			for (Transition t : smg.getTransitions()) {
				TransitionInfo tinfo = smg.getTransitionInfo(t);
				if (!tinfo.isWaitTransition())
					continue;

				State source_ = t.getSource();
				if (source_ != null && source_ instanceof StateWithActions) {
					StateWithActions source = (StateWithActions) source_;

					statesWithWait.add(source);
					
					StateVariable timerVar = smg.getStateVariables().getVariable(source, StateVariablePurposes.WAIT);
					if (timerVar == null) {
						//TODO use appropiate type
						timerVar = new StateVariable("wait_time", "uint8_t", null, source);
						smg.getStateVariables().add(timerVar, StateVariablePurposes.WAIT, source);
					}

					addCondition(
							tinfo,
							waitConditionFor(timerVar.getRealName(), tinfo,
									smg.getBasePeriod()));
				} else {
					// TODO use Sven's Warnings class
					System.err
							.format("ERROR: Transition with wait condition starts at state '%s', which doesn't support actions\n",
									smg.getName(source_));
				}
			}

			for (StateWithActions state : statesWithWait) {
				List<Action> actions = smg.getOrCreateActions(state);

				String timerVar = smg.getStateVariables()
						.getVariable(state, StateVariablePurposes.WAIT)
						.getRealName();

				// reset timer, when the state is entered
				actions.add(new Action(ActionType.ENTER,
						"// reset time for wait(...)\n" + timerVar + " = 0"));

				// increment timer, when remaining in state (but not when the
				// state is entered)
				actions.add(new Action(ActionType.DURING,
						"// increment time for wait(...)\n" + "++" + timerVar));
			}
		}
	}

	private void addCondition(TransitionInfo tinfo, String and_condition) {
		String condition = tinfo.getCondition();

		if (condition == null || condition.trim().equals(""))
			condition = and_condition;
		else
			condition = "(" + condition + ") && (" + and_condition + ")";

		tinfo.setCondition(condition);
	}

	private String waitConditionFor(String timerVar, TransitionInfo tinfo,
			double basePeriod) {
		String waitType = tinfo.getWaitType();
		double waitTime = tinfo.getWaitTime();
		if (waitType == null || Double.isNaN(waitTime))
			return null;

		double ticksD = waitTime / basePeriod;
		long ticks = Math.round(ticksD);
		double error = Math.abs((ticks - ticksD) / ticksD);
		if (Math.abs((ticks - ticksD) / ticksD) > 0.1)
			// TODO use Sven's Warnings class
			System.err
					.format("WARN: Actual wait time is off by %1.2f%% (%s instead of %s)\n",
							error*100, FSMParsers.formatTime(ticks * basePeriod),
							FSMParsers.formatTime(waitTime));

		String operator;
		if (waitType.equals("wait") || waitType.equals("after"))
			operator = ">=";
		else if (waitType.equals("at"))
			operator = "==";
		else if (waitType.equals("before"))
			operator = "<=";
		else {
			// TODO use Sven's Warnings class
			System.err
					.format("ERROR: Unsupported wait type '%s' is treated like 'wait'\n",
							waitType);

			operator = ">=";
		}

		return timerVar + " " + operator + " " + ticks;
	}
}
