package de.upbracing.code_generation.generators;

import java.util.List;
import java.util.Map;
import java.util.Set;

import Statecharts.State;
import Statecharts.StateWithActions;

import de.upbracing.code_generation.ITemplate;
import de.upbracing.code_generation.config.MCUConfiguration;
import de.upbracing.code_generation.fsm.model.StateMachineForGeneration;
import de.upbracing.code_generation.generators.fsm_test.StatemachineBuilder;
import de.upbracing.code_generation.generators.fsm_test.StatemachineBuilder.StatemachineWithWay;
import de.upbracing.code_generation.generators.fsm_test.StatemachineBuilder.TransitionActivation;
import de.upbracing.code_generation.generators.fsm_test.StatemachineBuilder.Waypoint;

public class StatemachineTestTemplate implements ITemplate {

	@Override
	public String generate(MCUConfiguration config, Object generator_data) {
		@SuppressWarnings("unchecked")
		Map<StateMachineForGeneration, StatemachineWithWay> ways
			= (Map<StateMachineForGeneration, StatemachineWithWay>) generator_data;
		
		StringBuffer stringBuffer = new StringBuffer();
		
		printHeader(stringBuffer);
		
		for (StateMachineForGeneration smg : config.getStatemachines()) {
			StatemachineWithWay smw = ways.get(smg);
			if (smw != null)
				generateTest(smg, smw, stringBuffer);
		}
		
		printFooter(config, ways, stringBuffer);
		
		return stringBuffer.toString();
	}

	private void generateTest(StateMachineForGeneration smg,
			StatemachineWithWay smw, StringBuffer stringBuffer) {
		String name = smg.getName();
		
		stringBuffer.append("\nint init_suite_" + name + "(void) {\n\treturn 0;\n}\n");
		stringBuffer.append("\nint clean_suite_" + name + "(void) {\n\treturn 0;\n}\n");
		
		printInitialTest(smg, smw, "test_" + name + "_initial_state", smw.waypoints.get(0), stringBuffer);
		
		for (int i=0;i<smw.transitions.size();i++) {
			printTransitionTest(smg, smw, "test_" + name + "_step" + i,
					smw.transitions.get(i), smw.waypoints.get(i+1), stringBuffer);
		}
		
		stringBuffer.append("uint8_t add_test_suite_" + name + "(void) {\n");
		
		stringBuffer.append(
			  "	CU_pSuite suite = CU_add_suite(\"statemachine: " + name + "\","
					  + "init_suite_" + name + ", clean_suite_" + name + ");\n"
			+ "	if (NULL == suite) {\n"
			+ "		return 0;\n"
			+ "	}\n"
			+ "\n");

		stringBuffer.append("	/* add the tests to the suite */\n");
		stringBuffer.append("\tif (NULL == CU_add_test(suite, \"initial state\", test_"
				+ name + "_initial_state))\n\t\treturn 0;\n");
		for (int i=0;i<smw.transitions.size();i++) {
			stringBuffer.append("\tif (NULL == CU_add_test(suite, \"step " + i + ": "
					+ smw.transitions.get(i).getTransitionInfo() + "\", "
					+ "test_" + name + "_step" + i + "))\n\t\treturn 0;\n");
		}
		
		stringBuffer.append("\n\treturn 1;\n");
		stringBuffer.append("}\n");
	}

	private void printInitialTest(StateMachineForGeneration smg,
			StatemachineWithWay smw, String test_function_name, Waypoint waypoint,
			StringBuffer stringBuffer) {
		stringBuffer.append("\nvoid " + test_function_name + "(void) {\n");
		stringBuffer.append("\t" + smg.getName() + "_init();\n\n");
		
		printWaypointTestCode("", smg.getName(), waypoint, StatemachineTestGenerator.getAllStates(smg.getStateMachine()), stringBuffer);
		
		stringBuffer.append("}\n");
	}

	private void printTransitionTest(StateMachineForGeneration smg,
			StatemachineWithWay smw, String test_function_name,
			TransitionActivation transition, Waypoint resulting_waypoint,
			StringBuffer stringBuffer) {
		stringBuffer.append("\nvoid " + test_function_name + "(void) {\n");
		
		stringBuffer.append("\t// execute transition: " + transition.getTransitionInfo() + "\n");
		transition.appendExecutionInstructions(smg.getName(), "\t", stringBuffer);
		stringBuffer.append("\n");
		
		stringBuffer.append("\t// check resulting state\n");
		printWaypointTestCode("\t", smg.getName(), resulting_waypoint,
				StatemachineTestGenerator.getAllStates(smg.getStateMachine()), stringBuffer);

		stringBuffer.append("}\n");
	}

	private void printWaypointTestCode(String indent, String smg_name,
			Waypoint waypoint, List<State> list, StringBuffer stringBuffer) {
		Set<State> in_waypoint = StatemachineBuilder.getStatesInWaypointSet(waypoint);
		
		for (State state : list) {
			if (!(state instanceof StateWithActions))
				continue;
			
			String in_state_counter = StatemachineTestGenerator.getStateCounterVariable(smg_name, (StateWithActions) state);
			String expected_value = (in_waypoint.contains(state) ? "1" : "0");
			
			stringBuffer.append("\tCU_ASSERT(" + in_state_counter + " == " + expected_value + ");\n");
		}
	}

	private void printHeader(StringBuffer stringBuffer) {
		stringBuffer.append("#include <stdio.h>\n");
		stringBuffer.append("#include <stdlib.h>\n");
		stringBuffer.append("#include <CUnit/Basic.h>\n");
		stringBuffer.append("\n");
		stringBuffer.append("#include \"statemachines.h\"\n");
		stringBuffer.append("\n");
		stringBuffer.append("\n");

	}

	private void printFooter(MCUConfiguration config, Map<StateMachineForGeneration, StatemachineWithWay> ways, StringBuffer stringBuffer) {
		stringBuffer.append("int random_test_add_suites(void) {\n");
		for (StateMachineForGeneration smg : config.getStatemachines()) {
			StatemachineWithWay smw = ways.get(smg);
			if (smw != null) {
				stringBuffer.append(
					  "	if (!add_test_suite_" + smg.getName() + "()) {\n"
					+ "		return 0;\n"
					+ "	}\n");
			}
		}
		stringBuffer.append("\treturn 1;\n}\n");
		
		stringBuffer.append("\n"
			+ "int random_test_main(void) {\n"
			+ "	if (CUE_SUCCESS != CU_initialize_registry())\n"
			+ "		return CU_get_error();\n"
			+ "\n"
			+ "	/* add a suites to the registry */\n");
		
		stringBuffer.append("\trandom_test_add_suites();\n");

		stringBuffer.append("\n"
			+ "	/* Run all tests using the CUnit Basic interface */\n"
			+ "	CU_basic_set_mode(CU_BRM_VERBOSE);\n"
			+ "	CU_basic_run_tests();\n"
			+ "	CU_cleanup_registry();\n"
			+ "	return CU_get_error();\n"
			+ "}\n");
	}
}
