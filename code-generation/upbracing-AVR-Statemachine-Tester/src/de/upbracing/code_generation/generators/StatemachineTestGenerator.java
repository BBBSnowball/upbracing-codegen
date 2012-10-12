package de.upbracing.code_generation.generators;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.upbracing.code_generation.config.MCUConfiguration;
import de.upbracing.code_generation.fsm.model.StateMachineForGeneration;
import de.upbracing.code_generation.generators.fsm.StatemachinesCFileTemplate;
import de.upbracing.code_generation.generators.fsm_test.StatemachineBuilder;
import de.upbracing.code_generation.generators.fsm_test.StatemachineBuilder.GaussianProbability;
import de.upbracing.code_generation.generators.fsm_test.StatemachineBuilder.StatemachineWithWay;

import Statecharts.GlobalCode;
import Statecharts.NamedItem;
import Statecharts.Region;
import Statecharts.State;
import Statecharts.StateMachine;
import Statecharts.StateParent;
import Statecharts.StateScope;
import Statecharts.StateWithActions;
import Statecharts.SuperState;

public class StatemachineTestGenerator extends AbstractGenerator {
	public StatemachineTestGenerator() {
		super(StatemachineGenerator.class,
				"test_statemachines.c", new StatemachineTestTemplate());
	}
	
	private Map<StateMachineForGeneration, StatemachineWithWay> ways;

	@Override
	public Object updateConfig(MCUConfiguration config) {
		ways = new HashMap<StateMachineForGeneration, StatemachineWithWay>();
		
		for (int i=0;i<4;i++) {
			int seed = i;
			StatemachineBuilder builder = new StatemachineBuilder(seed);
			
			// shallow statemachine
			//TODO adjust this for each statemachine
			builder.nesting_depth = 2;
			builder.state_count_per_parent = new GaussianProbability(4, 2);
			builder.regions_per_superstate = new GaussianProbability(1, 1);
			
			StatemachineWithWay smw = builder.buildStatemachineWithWay();
			StateMachine sm = smw.statemachine;
			
			StateMachineForGeneration smg = new StateMachineForGeneration("sm"+i, sm);
			config.getStatemachines().add(smg);
			ways.put(smg, smw);
			
			addTestActions(smg);
			
			// parse actions
			smg.update();
			
			if (i == 2) {
				try {
					int step = 10;
					boolean ignore_other_states = true;
					StatemachineBuilder.exportStep(smw, step, "smw_" + seed + "_step" + step + ".dot", ignore_other_states);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return ways;
	}

	private void addTestActions(StateMachineForGeneration smg) {
		String smg_name = smg.getName();
		StringBuffer declarations = new StringBuffer();
		declarations.append("#include <stdint.h>\n\n");
		
		for (State state : getAllStates(smg.getStateMachine())) {
			if (state instanceof StateWithActions) {
				StateWithActions state2 = (StateWithActions) state;
				String in_state_counter = getStateCounterVariable(smg_name,
						state2);
				state2.setActions("ENTER / ++" + in_state_counter + ";\n"
						+ "EXIT / --" + in_state_counter + ";\n");
				declarations.append("int8_t " + in_state_counter + ";\n");
			}
		}
		
		GlobalCode code = StatemachineBuilder.getDefaultStatemachineFactory().createGlobalCode();
		code.setCode(declarations.toString());
		code.setInHeaderFile(true);
		smg.getGlobalCodeBoxes().add(code);
	}

	public static String getStateCounterVariable(String smg_name,
			StateWithActions state) {
		String name = getStateName(state);
		String in_state_counter = smg_name + "_" + name + "_in_state_count";
		return in_state_counter;
	}

	public static String getStateName(StateScope state) {
		return StatemachinesCFileTemplate.getFullStateName(state);
	}

	public static List<State> getAllStates(StateParent parent) {
		List<State> states = new ArrayList<State>();
		getAllStates(parent, states);
		return states;
	}
	
	private static void getAllStates(StateParent parent, List<State> states) {
		states.addAll(parent.getStates());
		
		for (State state : parent.getStates()) {
			if (state instanceof SuperState) {
				SuperState sstate = (SuperState) state;
				for (Region region : sstate.getRegions())
					getAllStates(region, states);
			}
		}
	}
}
