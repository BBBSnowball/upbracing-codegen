package de.upbracing.code_generation.generators;

import de.upbracing.code_generation.StatemachinesCFileTemplate;
import de.upbracing.code_generation.StatemachinesHeaderTemplate;
import de.upbracing.code_generation.config.MCUConfiguration;
import de.upbracing.code_generation.fsm.model.StateMachineForGeneration;

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
	public boolean validate(MCUConfiguration config, boolean after_update_config) {
		boolean valid = true;
		
		if (!after_update_config) {
			for (StateMachineForGeneration smg : config.getStatemachines()) {
				//TODO for Rishab: validate state machines
				// - statemachine names, regions and event names are valid C identifiers; however, all
				//   of them except the statemachine name can be empty
				// - no edges going to initial states or starting at final states
				// - initial states have exactly one transition
				// - if an edge has a condition or an event, the waitType cannot be "wait" ("after" and all the others are ok)
				// - if an edge has waitType=="before", it must have a condition or an event (or both)
				// Probably, you will find some more things to check.
				
				// If an error is detected, the validator should continue and display all
				// other errors.
				
				// For each error, print a meaningful message and the location. The location could
				// look like this: "SuperState abc -> Region x -> State foo"
			}
		}
		
		return valid;
	}
	
	@Override
	public void updateConfig(MCUConfiguration config) {
		int runningCounter = 0;
		for (StateMachineForGeneration smg : config.getStatemachines()) {
			//TODO for Rishab: Assign a name to each state and region that doesn't have a
			//                 name, yet. You can generate unique names like this:
			//                 ("state" + (++runningCounter))
		}
	}
}
