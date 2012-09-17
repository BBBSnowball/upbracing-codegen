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
import de.upbracing.code_generation.fsm.model.StateMachineForGeneration;
import de.upbracing.code_generation.fsm.model.StateVariable;
import de.upbracing.code_generation.fsm.model.StateVariablePurposes;
import de.upbracing.code_generation.fsm.model.StateVariables;
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

				Pattern digit = Pattern.compile("\\d");
				Pattern word = Pattern.compile("\\w*");
				
				//statemachine name validation
				if(digit.matcher(statemachine_name.subSequence(0, 1)).matches()){
		        	 System.out.println("Statemachine name -> " + statemachine_name + " is not a valid C identifier!");
		        }
		        else if(!word.matcher(statemachine_name).matches()){
		        	 System.out.println("Statemachine name -> " + statemachine_name + " is not a valid C identifier!");
		        }
		        else{
		        	System.out.println("Statemachine name -> " + statemachine_name + " is a valid C identifier!");
		        }
				
				
				//event validation
				SortedMap<String, Set<Transition>> events;
				
				if(smg.getEvents().keySet().size() == 0)
					System.out.println("Statemachine -> "
										+ statemachine_name
										+ " does not have any events!");
				else
					{	
					events = smg.getEvents();
					Iterator<String> event_names = events.keySet().iterator();
					
					while(event_names.hasNext()){
						
						String event = event_names.next();
							
						if(emptyOrNull(event))
					        	 System.err.println("Statemachine name -> " + statemachine_name + "| Event name -> " + event  
					        			 + " is empty or null!");
						
						else if(digit.matcher(event.subSequence(0, 1)).matches())
				        	 System.err.println("Statemachine name -> " + statemachine_name + "| Event name -> " + event 
				        			 + " is not a valid C identifier!");
				        
				        else if(!word.matcher(event).matches())
				        	 System.err.println("Statemachine name -> " + statemachine_name + "| Event name -> " + event
				        			 + " is not a valid C identifier!");
				        
				        else
				        	System.out.println("Statemachine name -> " + statemachine_name + "| Event name -> " + event
				        			+ " is a valid C identifier");
				     			
				}
//				
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
					assignNames(config);
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
						    if(digit.matcher(initial_state.getName().subSequence(0, 1)).find(0))
					    	 		System.err
					        	 			.println("Statemachine -> " 
					        	 					+ statemachine_name 
					        	 					+ "| State name -> " 
					        	 					+ initial_state.getName()  
					        	 					+ " : state name cannot start with a digit!");
					        			 
					        
					        else if(!word.matcher(initial_state.getName()).matches())
					        		System.err
					        				.println("Statemachine -> " 
					        						+ statemachine_name 
					        						+ "| State name -> "
					        						+ initial_state.getName() 
					        						+ " is not a valid C identifier!");
					        			 
					        else
					        		System.out
					        				.println("Statemachine -> "
					        						+ statemachine_name 
					        						+ "| State name -> " 
					        						+ initial_state.getName()  
					        						+ " is a valid C identifier!");
					     
						  
						 // no incoming transitions
						 if(!initial_state.getIncomingTransitions().isEmpty())
							 System.err
							 		.println("Statemachine -> " 
				        	 				+ statemachine_name 
				        	 				+ " | State name -> " 
				        	 				+ initial_state.getName() 
				        	 				+ " Initial state does not have any incoming transitions! ");
						 
						    
						// validation for outgoing transitions
						 
						//check whether outgoing transitions exist
						if(initial_state.getOutgoingTransitions().isEmpty())
							 System.err
							 		.println("Statemachine -> " 
					        						+ statemachine_name 
					        						+ "| State name -> "
					        						+ initial_state.getName()
					        						+ " does not have any outgoing transitions!");
						else {
						
							out_transitions = initial_state.
						    		getOutgoingTransitions();
						
									  				    
						for (Transition t : out_transitions) {
							TransitionInfo transitionInfo = smg.getTransitionInfo(t);
							if (transitionInfo.isWaitTransition())
								System.err
										.println("Statemachine "
												+ " -> "
												+ statemachine_name
												+ " | Transition "
												+ " -> "
												+ "Source : "
												+ t.getSource()
												+ " Destination : "
												+ t.getDestination()
												+ " : Start state cannot have waiting transition!");

							else if (!emptyOrNull(transitionInfo.getEventName()))
								System.err.println("Statemachine " + " -> "
										+ statemachine_name + " | Transition "
										+ " -> " + "Source : " + t.getSource()
										+ " Destination : "
										+ t.getDestination()
										+ " : Start state cannot have events!");

							else if (!emptyOrNull(transitionInfo.getCondition()))
								System.err
										.println("Statemachine "
												+ " -> "
												+ statemachine_name
												+ " | Transition "
												+ " -> "
												+ "Source : "
												+ t.getSource()
												+ " Destination : "
												+ t.getDestination()
												+ " : Start state cannot have conditions !");

							else if (initial_state.getOutgoingTransitions().size() > 1) {
								System.err.println("Statemachine " + " -> "
										+ statemachine_name + " | State "
										+ " -> " + initial_state.getName()
										+ " : Validation failed.");
							}
						}
					}
					}
					
					// final state validity
					else if (state instanceof FinalState) {
						FinalState final_state = (FinalState) state;
						final_states.add(final_state);
												
						//name validation
					    if(digit.matcher(final_state.getName().subSequence(0, 1)).find(0))
	    		        	 System.err
	    		        	 		.println("Statemachine -> " 
	    		        	 				+ statemachine_name
	    		        	 				+ "| State name -> " 
	    		        	 				+ final_state.getName()  
	    		        	 				+ " : state name cannot start with a digit!");
					        			 
					        
					    else if(!word.matcher(final_state.getName()).matches())
					        	 System.err
					        	 		.println("Statemachine -> " 
					        	 				+ statemachine_name
					        	 				+ "| State name -> " 
					        					+ final_state.getName()  
					       	 					+ " is not a valid C identifier!");
					   
					    else
					        	System.out
					        			.println("Statemachine -> " 
					        					+ statemachine_name 
					        					+ "| State name -> " 
					        					+ final_state.getName()  
					        					+ " is a valid C identifier!");
					        			
					       
						//check whether final state has incoming transitions
						if(final_state.getIncomingTransitions().isEmpty())
							System.err
									.println("Statemachine -> " 
	    		        	 				+ statemachine_name
	    		        	 				+ "| State name -> " 
	    		        	 				+ final_state.getName()
	    		        	 				+ " | Final state does not have any incoming transitions!");
						
						//check whether final state has outgoing transitions
						else if(!final_state.getOutgoingTransitions()
									.isEmpty()) 
								System.err.println("Statemachine " + " -> "
										+ statemachine_name + " | State "
										+ " -> " + final_state.getName()
										+ " : Validation failed.");
					
						// removing the previous final state and setting its incoming transitions to the current 
						// final state. Then finally removing the previous final state from the list
						
						//if incoming and outgoing transitions exist but number of states greater than 1
						if(final_states.size() > 1 
								&& final_state.getIncomingTransitions().isEmpty()
									&& (!final_state.getOutgoingTransitions().isEmpty())){
									
								EList<Transition> previous_final_transitions = final_states.get(0).getIncomingTransitions();
							
								for(Transition t : previous_final_transitions){
									t.setDestination(final_state);
									}
							
								final_states.set(0, final_state);
								updateConfig(config);
							}
						
						//transition validity 
						//if incoming and outgoing transitions exist but number of states greater than 1
						if(emptyOrNull(final_state.getIncomingTransitions().toString())
								&&!final_state.getOutgoingTransitions()
								.isEmpty()){
						
						in_transitions = final_state
								.getIncomingTransitions();
						
						    
						for (Transition t : in_transitions) {
							TransitionInfo ti = smg.getTransitionInfo(t);
						
							
							//check whether the incoming transition is from initial state
							if(!(t.getSource() instanceof InitialState)){
								
							if (!emptyOrNull(ti.getCondition())
									|| !emptyOrNull(ti.getEventName())) {
								// check wait type is "wait"?
								if (ti.getWaitType().equals("wait"))
									System.err
											.println("Statemachine "
													+ " -> "
													+ statemachine_name
													+ " | Transition "
													+ " -> "
													+ " Source : "
													+ t.getSource()
													+ " Destination : "
													+ t.getDestination()
													+ " Transition having condition or event cannot have wait type wait!");
							}
							// ensure that when wait type is "before" either
							// getcondition is true or event is true
							else if (ti.getWaitType().equals("before")) {
								if (ti.getCondition()
										.isEmpty()
										&& ti
												.getEventName().isEmpty()) {
									System.err
											.println("Statemachine "
													+ " -> "
													+ statemachine_name
													+ " | Transition "
													+ " -> "
													+ " Source : "
													+ t.getSource()
													+ " Destination : "
													+ t.getDestination()
													+ " Transition must either have a condition or an event!");
								}
							}						
						}									
					}
						}
					
					}
			

					// normal state validity
					else if (state instanceof NormalState) {
						NormalState normal_state = (NormalState) state;
						
						//check normal state name
						//check for normal states having same name?
						if (digit.matcher(normal_state.getName().subSequence(0, 1)).find(0))
							System.err
									.println("Statemachine -> " 
											+ statemachine_name 
											+ "| State name -> " 
											+ normal_state.getName()  
											+ " : state name cannot start with a digit!");
										      
				        else if (!word.matcher(normal_state.getName()).matches())
				        	 System.err
				        	 		.println("Statemachine -> " 
				        	 				+ statemachine_name 
				        	 				+ "| State name -> " 
				        	 				+ normal_state.getName() 
				        	 				+ " is not a valid C identifier!");
				        			
				        else
				        	System.out
				        			.println("Statemachine -> " 
				        					+ statemachine_name 
				        					+ "| State name -> " 
				        					+ normal_state.getName()  
				        					+ " is a valid C identifier!");
						
						
						//check whether normal state has incoming transitions
						if(normal_state.getIncomingTransitions().isEmpty())
							System.err
									.println("Statemachine -> " 
					        	 			+ statemachine_name 
					        	 			+ "| State name -> " 
					        	 			+ normal_state.getName()
					        	 			+ " | Doesn't have any incoming transitions!");
						
						//check whether normal state has outgoing transitions
						else if (normal_state.getOutgoingTransitions().isEmpty())
							System.err
									.println("Statemachine -> " 
											+ statemachine_name 
											+ "| State name -> " 
											+ normal_state.getName()
											+ " | Doesn't have any outgoing transitions!");
						
						
						//perform validation of specific inncoming transitions if they exist
						if(!normal_state.getIncomingTransitions().isEmpty()){
							
							in_transitions = normal_state
												.getIncomingTransitions();	        		        			
						//validation of incoming transitions
						for (Transition t : in_transitions) {
							TransitionInfo transitionInfo = smg.getTransitionInfo(t);
														
							//ensure that incoming transition is not from initial state
							if(!(t.getSource() instanceof InitialState)){
								
							//transition type validation
							if (!emptyOrNull(transitionInfo.getCondition())
									|| !emptyOrNull(transitionInfo.getEventName())) {
								if (transitionInfo.getWaitType() == "wait")
									System.err
											.println("Statemachine "
													+ " -> "
													+ statemachine_name
													+ " | Transition "
													+ " -> "
													+ " Source : "
													+ t.getSource()
													+ " Destination : "
													+ t.getDestination()
													+ " Transition having condition or event cannot have wait type wait!");
							} else if (transitionInfo.getWaitType() == "before") {
								if (transitionInfo.getCondition()
										.isEmpty()
										&& transitionInfo
												.getEventName().isEmpty()) {
									System.err
											.println("Statemachine "
													+ " -> "
													+ statemachine_name
													+ " | Transition "
													+ " -> "
													+ " Source : "
													+ t.getSource()
													+ " Destination : "
													+ t.getDestination()
													+ " Transition must either have a condition or an event!");
								}
							}
						}
					}
						}
						
					//check rules for outgoing transitions if they fo exist	
					if(!normal_state.getOutgoingTransitions().isEmpty()){
						out_transitions = normal_state
								.getIncomingTransitions();
		
						//validation of outgoing transitions
						for(Transition t : out_transitions){
							TransitionInfo transitionInfo = smg.getTransitionInfo(t);
							
							
							if (!emptyOrNull(transitionInfo.getCondition())
									|| !emptyOrNull(transitionInfo.getEventName())) {
								if (transitionInfo.getWaitType()=="wait")
									System.err
											.println("Statemachine "
													+ " -> "
													+ statemachine_name
													+ " | Transition "
													+ " -> "
													+ " Source : "
													+ t.getSource()
													+ " Destination : "
													+ t.getDestination()
													+ " Transition having condition or event cannot have wait type wait!");
							} else if (transitionInfo.getWaitType() == "before") {
								if (transitionInfo.getCondition()
										.isEmpty()
										&& transitionInfo
												.getEventName().isEmpty()) {
									System.err
											.println("Statemachine "
													+ " -> "
													+ statemachine_name
													+ " | Transition "
													+ " -> "
													+ " Source : "
													+ t.getSource()
													+ " Destination : "
													+ t.getDestination()
													+ " Transition must either have a condition or an event!");
								}
							}							    			 	
						}
						
					}
					}
					
					// Superstates, regions, and contained states validation
					else if (state instanceof SuperState) {
						SuperState super_state = (SuperState) state;
						
						// Superstate name validity check
						if(digit.matcher(super_state.getName().subSequence(0, 1)).find(0))
				        	 System.err
				        	 	.println("Statemachine -> " 
				        	 			+ statemachine_name 
				        	 			+ "| State name -> " 
				        	 			+ super_state.getName()  
				        	 			+ " : state name cannot start with a digit!");
				        				        
				        else if(!word.matcher(super_state.getName()).matches())
				        	 System.err
				        	 	.println("Statemachine -> " 
				        	 			+ statemachine_name 
				        	 			+ "| State name -> " 
				        	 			+ super_state.getName()  
				        	 			+  " is not a valid C identifier!");
					    
				        else
				        	System.err
				        		.println("Statemachine -> " 
				        				+ statemachine_name 
				        				+ "| State name -> " 
				        				+ super_state.getName()  
				        				+ " is a valid C identifier!");
				        
						//check whether the superstate has incoming transitions
						if(emptyOrNull(super_state.getIncomingTransitions().toString()))
							System.err
									.println("Statemachine -> " 
					        	 			+ statemachine_name 
					        	 			+ "| State name -> " 
					        	 			+ super_state.getName()
					        	 			+ " | Doesn't have any incoming transitions!");
						
						//check whether the superstate has outgoing transitions
						else if (emptyOrNull(super_state.getOutgoingTransitions().toString()))
							System.err
									.println("Statemachine -> " 
											+ statemachine_name 
											+ "| State name -> " 
											+ super_state.getName()
											+ " | Doesn't have any outgoing transitions!");
						
						
					
						
					 	//check specific rules of incoming transitions of superstate if they do exist
						if(super_state.getIncomingTransitions().isEmpty()){
						
							in_transitions = super_state.getIncomingTransitions();
					    // incoming transition validation
						for(Transition t : in_transitions){
							TransitionInfo transitionInfo = smg.getTransitionInfo(t);
							
							//check whether incoming transition is from start state
							if(!(t.getSource() instanceof InitialState)){
																												
							if (!emptyOrNull(transitionInfo.getCondition())
									|| !emptyOrNull(transitionInfo.getEventName())) {
								if (transitionInfo.getWaitType().equals("wait"))
									System.err
											.println("Statemachine "
													+ " -> "
													+ statemachine_name
													+ " | Transition "
													+ " -> "
													+ " Source : "
													+ t.getSource()
													+ " Destination : "
													+ t.getDestination()
													+ " Transition having condition or event cannot have wait type wait!");
							} else if (transitionInfo.getWaitType().equals("before")) {
								if (transitionInfo.getCondition()
										.isEmpty()
										&& transitionInfo
												.getEventName().isEmpty()) {
									System.err
											.println("Statemachine "
													+ " -> "
													+ statemachine_name
													+ " | Transition "
													+ " -> "
													+ " Source : "
													+ t.getSource()
													+ " Destination : "
													+ t.getDestination()
													+ " Transition must either have a condition or an event!");
								}
							}
						}
					}		
					}	
						//check specific rules of outgoing transitions of superstate if they do exist
						if(!super_state.getOutgoingTransitions().isEmpty()){
						
						out_transitions = super_state.getIncomingTransitions();
					    //validation for outgoing transitions
						for(Transition t : out_transitions){
							
							TransitionInfo transitionInfo = smg.getTransitionInfo(t);
							
							if (!emptyOrNull(transitionInfo.getCondition())
									|| !emptyOrNull(transitionInfo.getEventName())) {
								if (transitionInfo.getWaitType().equals("wait"))
									System.err
											.println("Statemachine "
													+ " -> "
													+ statemachine_name
													+ " | Transition "
													+ " -> "
													+ " Source : "
													+ t.getSource()
													+ " Destination : "
													+ t.getDestination()
													+ " Transition having condition or event cannot have wait type wait!");
							} else if (transitionInfo.getWaitType().equals("before")) {
								if (transitionInfo.getCondition()
										.isEmpty()
										&& transitionInfo
												.getEventName().isEmpty()) {
									System.err
											.println("Statemachine "
													+ " -> "
													+ statemachine_name
													+ " | Transition "
													+ " -> "
													+ " Source : "
													+ t.getSource()
													+ " Destination : "
													+ t.getDestination()
													+ " Transition must either have a condition or an event!");
								}
							}				 							 	
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
							 if (digit.matcher(r.getName().subSequence(0, 1)).find(0))
					        	 System.err
					        	 		.println("Statemachine -> " 
					        	 				+ statemachine_name 
					        	 				+ "| State name -> " 
					        	 				+ super_state.getName() 
					        	 				+ " | Region name -> " 
					        	 				+ r.getName() 
					        	 				+ " : State name cannot start with a digit!");
					        
					        else if (!word.matcher(r.getName()).matches())
					        	 System.err
					        	 		.println("Statemachine -> "
					        	 				+ statemachine_name 
					        	 				+ "| State name -> " 
					        	 				+ super_state.getName() 
					        	 				+ " | Region name -> " 
					        	 				+  r.getName()
					        	 				+ " is not a valid C identifier!");
					        
					        else if(emptyOrNull(r.getName())){
					        	System.err
					        			.println("Statemachine -> " 
					        					+ statemachine_name 
					        					+ "| State name -> " 
					        					+ super_state.getName() 
					        					+ " | Region name -> " 
					        					+ r.getName()
					        					+ " region must have a name!");
					        	
					        }
							 
					        else
					        	System.err
					        			.println("Statemachine -> "
					        					+ statemachine_name 
					        					+ "| State name -> "  
					        					+ super_state.getName() 
					        					+ " | Region name -> " 
					        					+ r.getName()
					        					+ " is a valid C identifier!");
					        
							//check whether the region has states
							if(r.getStates().isEmpty())
								System.err
										.println("Statemachine -> "
					        					+ statemachine_name 
					        					+ "| State name -> "  
					        					+ super_state.getName() 
					        					+ " | Region name -> " 
					        					+ r.getName()
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
										 if (digit.matcher(initial_state_region.getName().subSequence(0, 1)).find(0))
								        	 System.err
								        	 		.println("Statemachine -> " 
								        	 				+ statemachine_name 
								        	 				+ "| State name -> " 
								        	 				+ super_state.getName() 
								        	 				+ " | Region name -> " 
								        	 				+ r.getName() 
								        	 				+ " State -> "
								        	 				+ initial_state_region.getName()
								        	 				+ " : State name cannot start with a digit!");
								        
								        else if (!word.matcher(initial_state_region.getName()).matches())
								        	 System.err
								        	 		.println("Statemachine -> "
								        	 				+ statemachine_name 
								        	 				+ "| State name -> " 
								        	 				+ super_state.getName() 
								        	 				+ " | Region name -> " 
								        	 				+  r.getName()
								        	 				+ " State -> "
								        	 				+ initial_state_region.getName()
								        	 				+ " is not a valid C identifier!");
			
								        else
								        	System.err
								        			.println("Statemachine -> "
								        					+ statemachine_name 
								        					+ "| State name -> "  
								        					+ super_state.getName() 
								        					+ " | Region name -> " 
								        					+ r.getName()
								        						+ " State -> "
								        	 				+ initial_state_region.getName()
								        					+ " is a valid C identifier!");
										
										//check whether any outgoing transitions exist
										if(initial_state_region.getOutgoingTransitions().isEmpty()){
											System.err
													.println("Statemachine -> " 
								    						+ statemachine_name 
								    						+ " | SuperState -> "
								    						+ super_state.getName() 
								    						+ " | Region -> " 
								    						+ r.getName() 
								    						+ " | Initial State -> "
								    						+ initial_state_region.getName()
								    						+ " | State does not have any outgoing transitions ! ");
										}
									
									    region_inital_count++;
									    
									    //number of inital states in the region
									    if( region_inital_count != 1){
									    	System.err.println("Statemachine -> " + statemachine_name + " | SuperState -> " + super_state.getName() + " | Region ->" +
									    			r.getName() +" | Statemachine must have only one initial state.");
									    }
									    
									    //if the initial state has outgoing transitions perform checks
										if(!initial_state_region.getOutgoingTransitions().isEmpty()){
										
											out_transitions = initial_state_region.getOutgoingTransitions();
									    
									    for(Transition t : out_transitions){
									    	TransitionInfo transitionInfo = smg.getTransitionInfo(t);
									    
									    	if(transitionInfo.isWaitTransition())
									    		System.err
									    				.println("Statemachine -> " 
									    						+ statemachine_name 
									    						+ " | SuperState -> "
									    						+ super_state.getName() 
									    						+ " | Region -> " 
									    						+ r.getName() 
									    						+ " | Transition -> " 
									    						+ "Source : "  
									    						+ t.getSource() 
									    						+ " Destination : " 
									    						+ t.getDestination() 
									    						+ " : Start state cannot have waiting transition!");
									    	
									    	else if(!emptyOrNull(transitionInfo.getEventName()))
									    		System.err
									    			.println("Statemachine -> " 
									    					+ statemachine_name 
									    					+ " | SuperState -> "
									    					+ super_state.getName() 
									    					+ " | Region -> " 
									    					+ r.getName() 
									    					+ " | Transition -> " 
									    					+ "Source : "  
									    					+ t.getSource() 
									    					+ " Destination : " 
									    					+ t.getDestination() 
									    					+ " : Start state cannot have events!");
									    	
									    	else if(!emptyOrNull(transitionInfo.getCondition()))
									    		System.err
									    			.println("Statemachine -> " 
									    					+ statemachine_name 
									    					+ " | SuperState -> "
									    					+ super_state.getName() 
									    					+ " | Region -> " 
									    					+ r.getName() 
									    					+ " | Transition -> " 
									    					+ "Source : " 
									    					+ t.getSource() 
									    					+ " Destination : " 
									    					+ t.getDestination() 
									    					+ " : Start state cannot have conditions !");
									    	
									    	else if(initial_state_region.getOutgoingTransitions().size()>1)
										    	System.err
										    			.println("Statemachine -> " 
										    					+ statemachine_name 
										    					+ " | SuperState -> "
										    					+ super_state.getName() 
										    					+ " | Region -> " 
										    					+ r.getName() 
										    					+ " | State  -> " 
										    					+ initial_state_region.getName()
										    					+ " : Initial state cannot have more than one outgoing transitions!");
										    							    	
									    }
									}
								}
									
									else if (s instanceof FinalState){
										FinalState final_state_region = (FinalState) s;
										super_final_states.add(final_state_region);
										
										//validation of name
										 if (digit.matcher(final_state_region.getName().subSequence(0, 1)).find(0))
								        	 System.err
								        	 		.println("Statemachine -> " 
								        	 				+ statemachine_name 
								        	 				+ "| State name -> " 
								        	 				+ super_state.getName() 
								        	 				+ " | Region name -> " 
								        	 				+ r.getName() 
								        	 				+ " State -> "
								        	 				+ final_state_region.getName()
								        	 				+ " : State name cannot start with a digit!");
								        
								        else if (!word.matcher(final_state_region.getName()).matches())
								        	 System.err
								        	 		.println("Statemachine -> "
								        	 				+ statemachine_name 
								        	 				+ "| State name -> " 
								        	 				+ super_state.getName() 
								        	 				+ " | Region name -> " 
								        	 				+  r.getName()
								        	 				+ " State -> "
								        	 				+ final_state_region.getName()
								        	 				+ " is not a valid C identifier!");
			
								        else
								        	System.err
								        			.println("Statemachine -> "
								        					+ statemachine_name 
								        					+ "| State name -> "  
								        					+ super_state.getName() 
								        					+ " | Region name -> " 
								        					+ r.getName()
								        					+ " State -> "
								        					+ final_state_region.getName()
								        					+ " is a valid C identifier!");
										
										
										if(final_state_region.getIncomingTransitions().isEmpty()){
											 System.err
										 		.println("Statemachine -> "
										 				+ statemachine_name
										 				+ " | State -> "
										 				+ super_state.getName()
										 				+ " | Region -> "
										 				+ r.getName()
										 				+ " | Final State -> "
										 				+ final_state_region.getName()
										 				+ " | There must be at be atleast one incoming transition to the final state!");
										}
										
									 	//check whether there are any outgoing transitions
										else if(!final_state_region.getOutgoingTransitions().isEmpty())
									 			System.err
									 					.println("Statemachine -> " 
									 							+ statemachine_name 
									 							+ " | SuperState -> " 
									 							+ super_state.getName()  
									 							+ " | Region -> "
									 							+ r.getName() 
									 							+ " | State -> " 
									 							+ final_state_region.getName()
									 							+ " : Validation failed.");		
										
										//check whether there are more than one final states
										if(super_final_states.size() > 1
												&& final_state_region.getIncomingTransitions().isEmpty()
												&& !final_state_region.getOutgoingTransitions().isEmpty()){
											
											EList<Transition> transition = super_final_states.get(0).getIncomingTransitions();
											
											for(Transition trans : transition){
												trans.setDestination(final_state_region);
											}
											
											super_final_states.set(0, final_state_region);
											updateConfig(config);
										}
										
										//if incoming transitions exist then perform checks
										if(!final_state_region.getIncomingTransitions().isEmpty()
												&& final_state_region.getOutgoingTransitions().isEmpty()){
									    
										in_transitions = final_state_region.getIncomingTransitions();
									    
									    
										 for(Transition t : in_transitions){
											 TransitionInfo transitionInfo = smg.getTransitionInfo(t);
											 
											 //test whether the source is an initial state
											 if(!(t.getSource() instanceof InitialState)){		
												if(!transitionInfo.getCondition().isEmpty() || !transitionInfo.getEventName().isEmpty())
											 		
											 		//check wait type is "wait"?
											 		if(transitionInfo.getWaitType().equals("wait"))
											 			System.err
											 					.println("Statemachine -> " 
											 							+ statemachine_name 
											 							+ " | SuperState -> " 
											 							+ super_state.getName() 
											 							+ " | Region -> "
											 							+ r.getName() 
											 							+ " | Transition -> " 
											 							+ " Source : " 
											 							+ t.getSource() 
											 							+ " Destination : " 
											 							+ t.getDestination() 
											 							+ " Transition having condition or event cannot have wait type wait!");
											 	
											 	
											 	// ensure that when wait type is "before" either getcondition is true or event is true
											 	else if(transitionInfo.getWaitType().equals("before")){
											 		if(transitionInfo.getCondition().isEmpty() && transitionInfo.getEventName().isEmpty()){
											 			System.err
											 					.println("Statemachine -> " 
											 							+ statemachine_name 
											 							+ " | SuperState -> " 
											 							+ super_state.getName()  
											 							+ " | Region -> "
											 							+ r.getName() 
											 							+ " | Transition -> " 
											 							+ " Source : " 
											 							+ t.getSource() 
											 							+ " Destination : " 
											 							+ t.getDestination() 
											 							+ " Transition must either have a condition or an event!");
											 		}
											 	}
										 }
											 	
										 }
										 }
									}
											 
									else if(s instanceof NormalState){
										
										NormalState normal_state_region = (NormalState) s;
										
										//check validity of normal state name
										if (digit.matcher(normal_state_region.getName().subSequence(0, 1)).find(0))
								        	 System.err
								        	 		.println("Statemachine -> " 
								        	 				+ statemachine_name 
								        	 				+ "| State name -> " 
								        	 				+ super_state.getName() 
								        	 				+ " | Region name -> " 
								        	 				+ r.getName() 
								        	 				+ " State -> "
								        	 				+ normal_state_region.getName()
								        	 				+ " : State name cannot start with a digit!");
								        
								        else if (!word.matcher(normal_state_region.getName()).matches())
								        	 System.err
								        	 		.println("Statemachine -> "
								        	 				+ statemachine_name 
								        	 				+ "| State name -> " 
								        	 				+ super_state.getName() 
								        	 				+ " | Region name -> " 
								        	 				+  r.getName()
								        	 				+ " State -> "
								        	 				+ normal_state_region.getName()
								        	 				+ " is not a valid C identifier!");
			
								        else
								        	System.err
								        			.println("Statemachine -> "
								        					+ statemachine_name 
								        					+ "| State name -> "  
								        					+ super_state.getName() 
								        					+ " | Region name -> " 
								        					+ r.getName()
								        					+ " State -> "
								        					+ normal_state_region.getName()
								        					+ " is a valid C identifier!");
										
										//check whether the state has incoming or outgoing transitions
										
										if(emptyOrNull(normal_state_region.getOutgoingTransitions().toString()))
											System.err
													.println("Statemachine -> " 
								        	 				+ statemachine_name 
								        	 				+ "| SuperState name -> " 
								        	 				+ super_state.getName()  
								        	 				+ " | Region name -> " 
								        	 				+ r.getName() 
								        	 				+ " | State name" 
								        	 				+ normal_state_region.getName()
								        	 				+ " | Does not have any incoming transitions!");
										
										else if(emptyOrNull(normal_state_region.getIncomingTransitions().toString())){
											System.err
													.println("Statemachine -> " 
															+ statemachine_name 
															+ "| SuperState name -> " 
															+ super_state.getName()  
															+ " | Region name -> " 
															+ r.getName() 
															+ " | State name" 
															+ normal_state_region.getName()
															+ " | Does not have any outgoing transitions!");
										}
										
										
										//perform checks on incoming transitions if they exist
										if(!emptyOrNull(normal_state_region.getIncomingTransitions().toString())){
											
											in_transitions = normal_state_region.getIncomingTransitions();
									    
										for(Transition t : in_transitions){
											TransitionInfo transitioninfo = smg.getTransitionInfo(t);
											
											if(!(t.getSource() instanceof InitialState)){
									
										
										 	if(!transitioninfo.getCondition().isEmpty() || !transitioninfo.getEventName().isEmpty()){
										 		if(transitioninfo.getWaitType().equals("wait"))
										 			System.err
										 				.println("Statemachine -> " 
										 						+ statemachine_name 
										 						+ " | SuperState -> " 
										 						+ super_state.getName()  
										 						+ " | Region -> "
										 						+ r.getName() 
										 						+ " | Transition -> " 
										 						+ " Source : " 
										 						+ t.getSource() 
										 						+ " Destination : " 
										 						+ t.getDestination() 
										 						+ " Transition having condition or event cannot have wait type wait!");
										 	}
										 	
										 	else if(transitioninfo.getWaitType().equals("before")){
										 		if(transitioninfo.getCondition().isEmpty() && transitioninfo.getEventName().isEmpty()){
										 			System.err
										 				.println("Statemachine -> " 
										 						+ statemachine_name 
										 						+ " | SuperState -> " 
										 						+ super_state.getName()  
										 						+ " | Region -> "
										 						+ r.getName() 
										 						+ " | Transition -> " 
										 						+ " Source : " 
										 						+ t.getSource() 
										 						+ " Destination : " 
										 						+ t.getDestination() 
										 						+ " Transition must either have a condition or an event!");
										 		}
										 	}
										}
										}
										}
										
										//perform checks on outgoing transitions if they exist
										if(emptyOrNull(normal_state_region.getOutgoingTransitions().toString())){
											
											out_transitions = normal_state_region.getOutgoingTransitions();
										for(Transition t : out_transitions){
											TransitionInfo transitioninfo = smg.getTransitionInfo(t);
											
											
											if(!(t.getDestination() instanceof InitialState))
											{
												if(!transitioninfo.getCondition().isEmpty() || !transitioninfo.getEventName().isEmpty()){
											 		if(transitioninfo.getWaitType().equals("wait"))
											 			System.err
											 				.println("Statemachine -> " 
											 						+ statemachine_name 
											 						+ " | SuperState -> " 
											 						+ super_state.getName()  
											 						+ " | Region -> "
											 						+ r.getName() 
											 						+ " | Transition -> " 
											 						+ " Source : " 
											 						+ t.getSource() 
											 						+ " Destination : " 
											 						+ t.getDestination() 
											 						+ " Transition having condition or event cannot have wait type wait!");
											 	}
											 	
											 	else if(transitioninfo.getWaitType().equals("before")){
											 		if(transitioninfo.getCondition().isEmpty() && transitioninfo.getEventName().isEmpty()){
											 			System.err
											 				.println("Statemachine -> " 
											 						+ statemachine_name 
											 						+ " | SuperState -> " 
											 						+ super_state.getName()  
											 						+ " | Region -> "
											 						+ r.getName() 
											 						+ " | Transition -> " 
											 						+ " Source : " 
											 						+ t.getSource() 
											 						+ " Destination : " 
											 						+ t.getDestination() 
											 						+ " Transition must either have a condition or an event!");
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
					if (region.getParent() != state) {
						System.err.println("FATAL: Corrupted hierarchy");
						return false;
					}
					
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
				// regions have the right parent without our help
				for (Region region : ((SuperState)state).getRegions()) {
					// but the contained states need some help...
					updateParents(region.getStates(), region);
				}
			}
		}
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
									
									else if((sub instanceof NormalState)
											&& emptyOrNull(((FinalState) sub).getName())){
									
									((NormalState) state).setName("normalState_" + runningCounter);
									runningCounter++;
									}
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
		for (State state : smg.sortStates(parent.getStates())) {
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

					String timerVar = timerVariableForState(smg, source);
					addCondition(
							tinfo,
							waitConditionFor(timerVar, tinfo,
									smg.getBasePeriod()));
				} else {
					// TODO use Sven's Warnings class
					System.err
							.format("ERROR: Transition with wait condition starts at state '%s', which doesn't support actions\n",
									smg.getName(source_));
				}
			}

			for (StateWithActions state : statesWithWait) {
				List<Action> actions = smg.getActions(state);

				String timerVar = timerVariableForState(smg, state);

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
					.format("WARN: Actual wait time is off by %0.2%% (%s instead of %s)\n",
							error, FSMParsers.formatTime(ticks * basePeriod),
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

	private String timerVariableForState(StateMachineForGeneration smg,
			StateWithActions state) {
		return StatemachinesCFileTemplate.timeVariableForState(smg, state);
	}
}
