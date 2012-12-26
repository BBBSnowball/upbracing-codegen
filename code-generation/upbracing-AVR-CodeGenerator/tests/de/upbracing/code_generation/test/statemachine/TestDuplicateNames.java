// TEST SCENARIOS
// 
// Test for duplicate names (state/region names) in the following cases (with one statemachine passing
// and one failing the test for each case)
// 
// Case 1 : Simple state-machine (no Super states) 
// Case 2 : Region names (1 Super state is enough). Test for deeply nested Super State
// Case 3 : Deeply nested states in a single Region in a Super State
// 


package de.upbracing.code_generation.test.statemachine;

import static org.junit.Assert.*;

import org.junit.Test;

import statemachine.FinalState;
import statemachine.InitialState;
import statemachine.NormalState;
import statemachine.Region;
import statemachine.StateMachine;
import statemachine.SuperState;

import de.upbracing.code_generation.JRubyHelpers;
import de.upbracing.code_generation.Messages;
import de.upbracing.code_generation.fsm.model.StateMachineForGeneration;
import de.upbracing.code_generation.generators.fsm.Validator;
import de.upbracing.code_generation.test.TestHelpers;


public class TestDuplicateNames {
	Messages[] messages = new Messages[12] ;
	Validator[] validate_duplicate = new Validator[12];
	StringBuffer sb = new StringBuffer();
	
	@Test
	public void testDuplicateNames() {
		
		for (int i=0; i<messages.length; i++) {
			messages[i] = new Messages();
			validate_duplicate[i] = new Validator(messages[i]);
		}
		
		/** Test Case 1 */

		// fails validation
		StateMachine statemachine_fail_1 = JRubyHelpers
				.getStatemachineFactory().createStateMachine();

		InitialState[] init_fail_1 = new InitialState[1];
		FinalState[] fin_fail_1 = new FinalState[1];
		NormalState[] norm_fail_1 = new NormalState[3];

		// create initial state and add it to statemachine
		init_fail_1[0] = JRubyHelpers.getStatemachineFactory()
				.createInitialState();
		init_fail_1[0].setName("initial_state");
		statemachine_fail_1.getStates().add(init_fail_1[0]);

		// create final state and add it to statemachine
		fin_fail_1[0] = JRubyHelpers.getStatemachineFactory()
				.createFinalState();
		fin_fail_1[0].setName("final_state");
		statemachine_fail_1.getStates().add(fin_fail_1[0]);

		// create normal state and add it to statemachine
		for (int i = 0; i < norm_fail_1.length; i++) {
			norm_fail_1[i] = JRubyHelpers.getStatemachineFactory()
					.createNormalState();
			norm_fail_1[i].setName("normal_state");
			statemachine_fail_1.getStates().add(norm_fail_1[i]);
		}

		StateMachineForGeneration smg_fail_1 = new StateMachineForGeneration(
				"withDuplicates", statemachine_fail_1);

		assertEquals(
				false,
				validate_duplicate[0].duplicateNames(smg_fail_1,
						smg_fail_1.getStates()));

		messages[0].summarizeForCode(sb);
		
		assertEquals(TestHelpers.loadResource("/expected_results/statemachines/expectedDuplicateNames/expectedDuplicateNameResults_1.txt"),
		sb.toString());

		// passes validation
		StateMachine statemachine_pass_1 = JRubyHelpers
				.getStatemachineFactory().createStateMachine();

		InitialState[] init_pass_1 = new InitialState[1];
		FinalState[] fin_pass_1 = new FinalState[1];
		NormalState[] norm_pass_1 = new NormalState[3];

		// create initial state and add it to statemachine
		init_pass_1[0] = JRubyHelpers.getStatemachineFactory()
				.createInitialState();
		init_pass_1[0].setName("inital_state");
		statemachine_pass_1.getStates().add(init_pass_1[0]);

		// create final state and add it to statemachine
		fin_pass_1[0] = JRubyHelpers.getStatemachineFactory()
				.createFinalState();
		fin_pass_1[0].setName("final_state");
		statemachine_pass_1.getStates().add(fin_pass_1[0]);

		// create normal state and add it to statemachine
		for (int i = 0; i < norm_pass_1.length; i++) {
			norm_pass_1[i] = JRubyHelpers.getStatemachineFactory()
					.createNormalState();
			norm_pass_1[i].setName("normal_" + i);
			statemachine_pass_1.getStates().add(norm_pass_1[i]);
		}
		
		sb.setLength(0);		
		StateMachineForGeneration smg_pass_1 = new StateMachineForGeneration(
				"withoutDuplicates", statemachine_pass_1);
		
		messages[1].summarizeForCode(sb);

		assertEquals(
				true,
				validate_duplicate[1].duplicateNames(smg_pass_1,
						smg_pass_1.getStates()));
		
		assertEquals("", sb.toString());

		/** Test Case 2 */

		// Two level statemachine that fails
		StateMachine statemachine_fail_2 = JRubyHelpers
				.getStatemachineFactory().createStateMachine();

		InitialState[] init_fail_2 = new InitialState[7];
		FinalState[] fin_fail_2 = new FinalState[1];
		NormalState[] norm_fail_2 = new NormalState[4];
		SuperState[] sup_fail_2 = new SuperState[3];
		Region[] region_fail_2 = new Region[6];

		// create initial state
		for (int i = 0; i < init_fail_2.length; i++) {
			init_fail_2[i] = JRubyHelpers.getStatemachineFactory()
					.createInitialState();
			init_fail_2[i].setName("initial_state" + i);
		}

		// create final state
		fin_fail_2[0] = JRubyHelpers.getStatemachineFactory()
				.createFinalState();
		fin_fail_2[0].setName("final_state");

		// create normal state
		for (int i = 0; i < norm_fail_2.length; i++) {
			norm_fail_2[i] = JRubyHelpers.getStatemachineFactory()
					.createNormalState();
			norm_fail_2[i].setName("normal_state" + i);
		}

		// create super state
		for (int i = 0; i < sup_fail_2.length; i++) {
			sup_fail_2[i] = JRubyHelpers.getStatemachineFactory()
					.createSuperState();
			sup_fail_2[i].setName("Super_state" + i);
		}

		// create regions with same names
		for (int i = 0; i < region_fail_2.length; i++)
			region_fail_2[i] = JRubyHelpers.getStatemachineFactory()
					.createRegion();

		// set region names
		region_fail_2[0].setName("region_1");
		region_fail_2[1].setName("region_2");
		region_fail_2[2].setName("region");
		region_fail_2[3].setName("region");
		region_fail_2[4].setName("region_3");
		region_fail_2[5].setName("region_4");

		// add states to statemachine
		statemachine_fail_2.getStates().add(init_fail_2[0]);
		statemachine_fail_2.getStates().add(fin_fail_2[0]);
		statemachine_fail_2.getStates().add(sup_fail_2[0]);

		// add regions to the super state
		sup_fail_2[0].getRegions().add(region_fail_2[0]);
		sup_fail_2[0].getRegions().add(region_fail_2[1]);

		// add states to first region including one super state
		region_fail_2[0].getStates().add(init_fail_2[1]);
		region_fail_2[0].getStates().add(sup_fail_2[1]);

		// add states to second region including one super state
		region_fail_2[1].getStates().add(init_fail_2[2]);
		region_fail_2[2].getStates().add(sup_fail_2[2]);

		// add regions to super state of first region
		sup_fail_2[1].getRegions().add(region_fail_2[2]);
		sup_fail_2[1].getRegions().add(region_fail_2[3]);

		// add states to the regions
		region_fail_2[2].getStates().add(init_fail_2[3]);
		region_fail_2[2].getStates().add(norm_fail_2[0]);

		region_fail_2[3].getStates().add(init_fail_2[4]);
		region_fail_2[3].getStates().add(norm_fail_2[1]);

		// add regions to super state of second region
		sup_fail_2[2].getRegions().add(region_fail_2[4]);
		sup_fail_2[2].getRegions().add(region_fail_2[5]);

		// add states to the regions
		region_fail_2[4].getStates().add(init_fail_2[5]);
		region_fail_2[4].getStates().add(norm_fail_2[2]);

		region_fail_2[5].getStates().add(init_fail_2[6]);
		region_fail_2[5].getStates().add(norm_fail_2[3]);
		
		sb.setLength(0);
		
		StateMachineForGeneration smg_fail_2 = new StateMachineForGeneration(
				"duplicateRegions", statemachine_fail_2);
				
		assertEquals(
				false,
				validate_duplicate[2].duplicateNames(smg_fail_2,
						smg_fail_2.getStates()));
		
		messages[2].summarizeForCode(sb);

		assertEquals(TestHelpers.loadResource("/expected_results/statemachines/expectedDuplicateNames/expectedDuplicateNameResults_2.txt"),
				sb.toString());

		// Three level statemachine that fails

		StateMachine statm_fail_3 = JRubyHelpers.getStatemachineFactory()
				.createStateMachine();

		InitialState[] init_fail_3 = new InitialState[5];
		SuperState[] sup_fail_3 = new SuperState[3];
		Region[] reg_fail_3 = new Region[4];

		// create initial states and assign names to them
		for (int i = 0; i < init_fail_3.length; i++) {
			init_fail_3[i] = JRubyHelpers.getStatemachineFactory()
					.createInitialState();
			init_fail_3[i].setName("initial_state" + i);
		}

		// create final state and assign name to it
		FinalState fin_fail_3 = JRubyHelpers.getStatemachineFactory()
				.createFinalState();
		fin_fail_3.setName("final_state");

		// create super states and assign names to them
		for (int i = 0; i < sup_fail_3.length; i++) {
			sup_fail_3[i] = JRubyHelpers.getStatemachineFactory()
					.createSuperState();
			sup_fail_3[i].setName("super" + i);
		}

		// create regions and assign names to them
		for (int i = 0; i < reg_fail_3.length; i++) {
			reg_fail_3[i] = JRubyHelpers.getStatemachineFactory()
					.createRegion();
		}

		reg_fail_3[0].setName("region_1");
		reg_fail_3[1].setName("region_2");
		reg_fail_3[2].setName("region");
		reg_fail_3[3].setName("region");

		// add states to the statemachine
		statm_fail_3.getStates().add(init_fail_3[0]);
		statm_fail_3.getStates().add(fin_fail_3);
		statm_fail_3.getStates().add(sup_fail_3[0]);

		// add region to the super state
		sup_fail_3[0].getRegions().add(reg_fail_3[0]);

		// add initial and super state to the region
		reg_fail_3[0].getStates().add(init_fail_3[1]);
		reg_fail_3[0].getStates().add(sup_fail_3[1]);

		// add region to the super state
		sup_fail_3[1].getRegions().add(reg_fail_3[1]);

		// add initial and super state to the region
		reg_fail_3[1].getStates().add(init_fail_3[2]);
		reg_fail_3[1].getStates().add(sup_fail_3[2]);

		// add regions to the super state
		sup_fail_3[1].getRegions().add(reg_fail_3[2]);
		sup_fail_3[1].getRegions().add(reg_fail_3[3]);

		// add states to first region
		reg_fail_3[2].getStates().add(init_fail_3[3]);

		// add states to second region
		reg_fail_3[3].getStates().add(init_fail_3[4]);

		StateMachineForGeneration smg_fail_3 = new StateMachineForGeneration(
				"duplicateRegions", statm_fail_3);
		
		sb.setLength(0);
		
		assertEquals(
				false,
				validate_duplicate[3].duplicateNames(smg_fail_3,
						smg_fail_3.getStates()));
		
		messages[3].summarizeForCode(sb);

		assertEquals(TestHelpers.loadResource("/expected_results/statemachines/expectedDuplicateNames/expectedDuplicateNameResults_3.txt"),
				sb.toString());
		
		// Two level state machine that passes

		StateMachine statm_pass_2 = JRubyHelpers.getStatemachineFactory()
				.createStateMachine();

		InitialState[] init_pass_2 = new InitialState[7];
		SuperState[] sup_pass_2 = new SuperState[3];
		Region[] reg_pass_2 = new Region[6];

		// create final state
		FinalState fin_pass_2 = JRubyHelpers.getStatemachineFactory()
				.createFinalState();
		fin_pass_2.setName("final_state");

		// create initial state
		for (int i = 0; i < init_pass_2.length; i++) {
			init_pass_2[i] = JRubyHelpers.getStatemachineFactory()
					.createInitialState();
			init_pass_2[i].setName("initial_state" + i);
		}

		// create super state
		for (int i = 0; i < sup_pass_2.length; i++) {
			sup_pass_2[i] = JRubyHelpers.getStatemachineFactory()
					.createSuperState();
			sup_pass_2[i].setName("super_state" + i);
		}

		// create region
		for (int i = 0; i < reg_pass_2.length; i++) {
			reg_pass_2[i] = JRubyHelpers.getStatemachineFactory()
					.createRegion();
			reg_pass_2[i].setName("region" + i);
		}

		// add states to the statemachine
		statm_pass_2.getStates().add(init_pass_2[0]);
		statm_pass_2.getStates().add(fin_pass_2);
		statm_pass_2.getStates().add(sup_pass_2[0]);

		// add region to super state
		sup_pass_2[0].getRegions().add(reg_pass_2[0]);
		sup_pass_2[0].getRegions().add(reg_pass_2[1]);

		// add states to first region
		reg_pass_2[0].getStates().add(init_pass_2[1]);
		reg_pass_2[0].getStates().add(sup_pass_2[1]);

		// add states to second region
		reg_pass_2[1].getStates().add(init_pass_2[2]);
		reg_pass_2[1].getStates().add(sup_pass_2[2]);

		// add regions to super state of first region
		sup_pass_2[1].getRegions().add(reg_pass_2[2]);
		sup_pass_2[1].getRegions().add(reg_pass_2[3]);

		// add states
		reg_pass_2[2].getStates().add(init_pass_2[3]);

		reg_pass_2[3].getStates().add(init_pass_2[4]);

		// add regions to super state of second region
		sup_pass_2[2].getRegions().add(reg_pass_2[4]);
		sup_pass_2[2].getRegions().add(reg_pass_2[5]);

		// add states
		reg_pass_2[4].getStates().add(init_pass_2[4]);

		reg_pass_2[5].getStates().add(init_pass_2[5]);

		StateMachineForGeneration smg_pass_2 = new StateMachineForGeneration(
				"noduplicateRegions", statm_pass_2);
		
		sb.setLength(0);
		
		assertEquals(
				true,
				validate_duplicate[4].duplicateNames(smg_pass_2,
						smg_pass_2.getStates()));
		
		assertEquals("", sb.toString());

		// Three level statemachine that passes

		StateMachine statm_pass_3 = JRubyHelpers.getStatemachineFactory()
				.createStateMachine();

		InitialState[] init_pass_3 = new InitialState[5];
		SuperState[] sup_pass_3 = new SuperState[3];
		Region[] reg_pass_3 = new Region[4];

		// create final state
		FinalState fin_pass_3 = JRubyHelpers.getStatemachineFactory()
				.createFinalState();
		fin_pass_3.setName("final_state");

		// create initial state
		for (int i = 0; i < init_pass_3.length; i++) {
			init_pass_3[i] = JRubyHelpers.getStatemachineFactory()
					.createInitialState();
			init_pass_3[i].setName("intial_state" + i);
		}

		// create super state
		for (int i = 0; i < sup_pass_3.length; i++) {
			sup_pass_3[i] = JRubyHelpers.getStatemachineFactory()
					.createSuperState();
			sup_pass_3[i].setName("sup_state" + i);
		}

		// create region
		for (int i = 0; i < reg_pass_3.length; i++) {
			reg_pass_3[i] = JRubyHelpers.getStatemachineFactory()
					.createRegion();
			reg_pass_3[i].setName("region" + i);
		}

		// add states to statemachine
		statm_pass_3.getStates().add(init_pass_3[0]);
		statm_pass_3.getStates().add(fin_pass_3);
		statm_pass_3.getStates().add(sup_pass_3[0]);

		// add region to superstate
		sup_pass_3[0].getRegions().add(reg_pass_3[0]);

		// add states to region
		reg_pass_3[0].getStates().add(init_pass_3[1]);
		reg_pass_3[0].getStates().add(sup_pass_3[1]);

		// add region to superstate
		sup_pass_3[1].getRegions().add(reg_pass_3[1]);

		// add states to region
		reg_pass_3[1].getStates().add(init_pass_3[2]);
		reg_pass_3[1].getStates().add(sup_pass_3[2]);

		// add regions to superstate
		sup_pass_3[2].getRegions().add(reg_pass_3[2]);
		sup_pass_3[2].getRegions().add(reg_pass_3[3]);

		// add states to each region
		reg_pass_3[3].getStates().add(init_pass_3[3]);

		reg_pass_3[3].getStates().add(init_pass_3[4]);

		StateMachineForGeneration smg_pass_3 = new StateMachineForGeneration(
				"noduplicateRegions", statm_pass_3);
		sb.setLength(0);
		
		assertEquals(
				true,
				validate_duplicate[5].duplicateNames(smg_pass_3,
						smg_pass_3.getStates()));
		
		assertEquals("", sb.toString());


		/** Test Case 3 */

		// One level statemachine that fails

		StateMachine statm_fail_4 = JRubyHelpers.getStatemachineFactory()
				.createStateMachine();

		InitialState[] init_fail_4 = new InitialState[2];
		NormalState[] norm_fail_4 = new NormalState[2];

		// create final state and assign name
		FinalState fin_fail_4 = JRubyHelpers.getStatemachineFactory()
				.createFinalState();
		fin_fail_4.setName("final_state");

		// create superstate and assign name
		SuperState sup_fail_4 = JRubyHelpers.getStatemachineFactory()
				.createSuperState();
		sup_fail_4.setName("super_state");

		// create region and assign name
		Region reg_fail_4 = JRubyHelpers.getStatemachineFactory()
				.createRegion();
		reg_fail_4.setName("region");

		// create intial state and assign names to them
		for (int i = 0; i < init_fail_4.length; i++) {
			init_fail_4[i] = JRubyHelpers.getStatemachineFactory()
					.createInitialState();
			init_fail_4[i].setName("init_state" + i);
		}

		// create normal states and assign names to them
		for (int i = 0; i < norm_fail_4.length; i++) {
			norm_fail_4[i] = JRubyHelpers.getStatemachineFactory()
					.createNormalState();
			norm_fail_4[i].setName("normal_state");
		}

		// add states to the statemachine
		statm_fail_4.getStates().add(init_fail_4[0]);
		statm_fail_4.getStates().add(fin_fail_4);
		statm_fail_4.getStates().add(sup_fail_4);

		// add region to the superstate
		sup_fail_4.getRegions().add(reg_fail_4);

		// add states to the region
		reg_fail_4.getStates().add(init_fail_4[1]);
		reg_fail_4.getStates().add(norm_fail_4[0]);
		reg_fail_4.getStates().add(norm_fail_4[1]);

		StateMachineForGeneration smg_fail_4 = new StateMachineForGeneration(
				"duplicateStates", statm_fail_4);
		
		sb.setLength(0);
		
		assertEquals(
				false,
				validate_duplicate[6].duplicateNames(smg_fail_4,
						smg_fail_4.getStates()));
		
		
		
		// Two level statemachine that fails

		StateMachine statm_fail_5 = JRubyHelpers.getStatemachineFactory()
				.createStateMachine();
		
		InitialState[] init_fail_5 = new InitialState[3];
		NormalState[] norm_fail_5 = new NormalState[4];
		SuperState[] sup_fail_5 = new SuperState[2];
		Region[] reg_fail_5 = new Region[2];
		
		//create final state and assign name
		FinalState fin_fail_5 = JRubyHelpers.getStatemachineFactory().createFinalState();
		fin_fail_5.setName("final_state");
		
		//create initial state and assign names
		for (int i=0; i<init_fail_5.length; i++) {
			init_fail_5[i] = JRubyHelpers.getStatemachineFactory().createInitialState();
			init_fail_5[i].setName("initial_state");
		}
		
		//create super state and assign names
		for (int i=0; i<sup_fail_5.length; i++) {
			sup_fail_5[i] = JRubyHelpers.getStatemachineFactory().createSuperState();
			sup_fail_5[i].setName("super_state" + i);
		}
		
		//create region and assign names
		for (int i=0; i<reg_fail_5.length; i++) {
			reg_fail_5[i] = JRubyHelpers.getStatemachineFactory().createRegion();
			reg_fail_5[i].setName("region" + i);
		}
		
		//create normal state
		for (int i=0; i<norm_fail_5.length; i++)
			norm_fail_5[i] = JRubyHelpers.getStatemachineFactory().createNormalState();
		
		//assign names to normal state
		norm_fail_5[0].setName("normal_state1");
		norm_fail_5[1].setName("normal_state2");
		norm_fail_5[2].setName("normal_state");
		norm_fail_5[3].setName("normal_state");
		
		//add states to statemachine
		statm_fail_5.getStates().add(init_fail_5[0]);
		statm_fail_5.getStates().add(sup_fail_5[0]);
		statm_fail_5.getStates().add(fin_fail_5);
		
		//add region to super state
		sup_fail_5[0].getRegions().add(reg_fail_5[0]);
		
		//add states to region
		reg_fail_5[0].getStates().add(init_fail_5[1]);
		reg_fail_5[0].getStates().add(norm_fail_5[0]);
		reg_fail_5[0].getStates().add(norm_fail_5[1]);
		reg_fail_5[0].getStates().add(sup_fail_5[1]);
		
		//add region to super state
		sup_fail_5[1].getRegions().add(reg_fail_5[1]);
		
		//add states to the region
		reg_fail_5[1].getStates().add(init_fail_5[2]);
		reg_fail_5[1].getStates().add(norm_fail_5[2]);
		reg_fail_5[1].getStates().add(norm_fail_5[3]);
		
		StateMachineForGeneration smg_fail_5 = new StateMachineForGeneration(
				"duplicateStates", statm_fail_5);
		
		sb.setLength(0);
		
		assertEquals(
				false,
				validate_duplicate[7].duplicateNames(smg_fail_5,
						smg_fail_5.getStates()));
		
		messages[7].summarizeForCode(sb);

		assertEquals(TestHelpers.loadResource("/expected_results/statemachines/expectedDuplicateNames/expectedDuplicateNameResults_4.txt"),
				sb.toString());

		//Three level statemachine that fails
		
		StateMachine statm_fail_6 = JRubyHelpers.getStatemachineFactory().createStateMachine();
		
		InitialState[] init_fail_6 = new InitialState[4];
		NormalState[] norm_fail_6 = new NormalState[6];
		SuperState[] sup_fail_6 = new SuperState[3];
		Region[] reg_fail_6 = new Region[3];
		
		//create final state and assign a name
		FinalState fin_fail_6 = JRubyHelpers.getStatemachineFactory().createFinalState();
		fin_fail_6.setName("final_state");
		
		//create Initial state and assign name
		for (int i=0; i<init_fail_6.length; i++) {
			init_fail_6[i] = JRubyHelpers.getStatemachineFactory().createInitialState();
			init_fail_6[i].setName("init_state" + i);
		}
		
		//create super state and assign name
		for (int i=0; i<sup_fail_6.length; i++) {
			sup_fail_6[i] = JRubyHelpers.getStatemachineFactory().createSuperState();
			sup_fail_6[i].setName("super_state" + i);
		}
		
		//create region and assign name
		for (int i=0; i<reg_fail_6.length; i++) {
			reg_fail_6[i] = JRubyHelpers.getStatemachineFactory().createRegion();
			reg_fail_6[i].setName("region" + i);
		}
		
		//create normal state
		for (int i=0; i<norm_fail_6.length; i++)
			norm_fail_6[i] = JRubyHelpers.getStatemachineFactory().createNormalState();
		
		//assign names
		norm_fail_6[0].setName("normal_state1");
		norm_fail_6[1].setName("normal_state2");
		norm_fail_6[2].setName("normal_state3");
		norm_fail_6[3].setName("normal_state4");
		norm_fail_6[4].setName("normal_state");
		norm_fail_6[5].setName("normal_state");
		
		//add states to statemachine
		statm_fail_6.getStates().add(init_fail_6[0]);
		statm_fail_6.getStates().add(fin_fail_6);
		statm_fail_6.getStates().add(sup_fail_6[0]);
		
		//add region to the superstate
		sup_fail_6[0].getRegions().add(reg_fail_6[0]);
		
		//add states to region
		reg_fail_6[0].getStates().add(init_fail_6[1]);
		reg_fail_6[0].getStates().add(norm_fail_6[0]);
		reg_fail_6[0].getStates().add(norm_fail_6[1]);
		reg_fail_6[0].getStates().add(sup_fail_6[1]);
		
		//add region to super state
		sup_fail_6[1].getRegions().add(reg_fail_6[1]);
		
		//add states to region
		reg_fail_6[1].getStates().add(init_fail_6[2]);
		reg_fail_6[1].getStates().add(norm_fail_6[2]);
		reg_fail_6[1].getStates().add(norm_fail_6[3]);
		reg_fail_6[1].getStates().add(sup_fail_6[2]);
		
		//add region to super state
		sup_fail_6[2].getRegions().add(reg_fail_6[2]);
		
		//add states to the region
		reg_fail_6[2].getStates().add(init_fail_6[3]);
		reg_fail_6[2].getStates().add(norm_fail_6[4]);
		reg_fail_6[2].getStates().add(norm_fail_6[5]);
		
		StateMachineForGeneration smg_fail_6 = new StateMachineForGeneration(
				"duplicateStates", statm_fail_6);
		
		sb.setLength(0);
		
		assertEquals(
				false,
				validate_duplicate[8].duplicateNames(smg_fail_6,
						smg_fail_6.getStates()));
		
		messages[8].summarizeForCode(sb);

		assertEquals(TestHelpers.loadResource("/expected_results/statemachines/expectedDuplicateNames/expectedDuplicateNameResults_5.txt"),
				sb.toString());
		
		//One level statemachine that passes
		StateMachine statm_pass_4 = JRubyHelpers.getStatemachineFactory()
				.createStateMachine();

		InitialState[] init_pass_4 = new InitialState[2];
		NormalState[] norm_pass_4 = new NormalState[2];

		// create final state and assign name
		FinalState fin_pass_4 = JRubyHelpers.getStatemachineFactory()
				.createFinalState();
		fin_pass_4.setName("final_state");

		// create superstate and assign name
		SuperState sup_pass_4 = JRubyHelpers.getStatemachineFactory()
				.createSuperState();
		sup_pass_4.setName("super_state");

		// create region and assign name
		Region reg_pass_4 = JRubyHelpers.getStatemachineFactory()
				.createRegion();
		reg_pass_4.setName("region");

		// create intial state and assign names to them
		for (int i = 0; i < init_pass_4.length; i++) {
			init_pass_4[i] = JRubyHelpers.getStatemachineFactory()
					.createInitialState();
			init_pass_4[i].setName("init_state" + i);
		}

		// create normal states and assign names to them
		for (int i = 0; i < norm_pass_4.length; i++) {
			norm_pass_4[i] = JRubyHelpers.getStatemachineFactory()
					.createNormalState();
			norm_pass_4[i].setName("normal_state" + i);
		}

		// add states to the statemachine
		statm_pass_4.getStates().add(init_pass_4[0]);
		statm_pass_4.getStates().add(fin_pass_4);
		statm_pass_4.getStates().add(sup_pass_4);

		// add region to the superstate
		sup_pass_4.getRegions().add(reg_pass_4);

		// add states to the region
		reg_pass_4.getStates().add(init_pass_4[1]);
		reg_pass_4.getStates().add(norm_pass_4[0]);
		reg_pass_4.getStates().add(norm_pass_4[1]);

		StateMachineForGeneration smg_pass_4 = new StateMachineForGeneration(
				"duplicateStates", statm_pass_4);
		sb.setLength(0);
		
		assertEquals(
				true,
				validate_duplicate[9].duplicateNames(smg_pass_4,
						smg_pass_4.getStates()));
		
		assertEquals("", sb.toString());
		
		//level two statemachine that passes
		
		StateMachine statm_pass_5 = JRubyHelpers.getStatemachineFactory()
				.createStateMachine();
		
		InitialState[] init_pass_5 = new InitialState[3];
		NormalState[] norm_pass_5 = new NormalState[4];
		SuperState[] sup_pass_5 = new SuperState[2];
		Region[] reg_pass_5 = new Region[2];
		
		//create final state and assign name
		FinalState fin_pass_5 = JRubyHelpers.getStatemachineFactory().createFinalState();
		fin_pass_5.setName("final_state");
		
		//create initial state and assign names
		for (int i=0; i<init_pass_5.length; i++) {
			init_pass_5[i] = JRubyHelpers.getStatemachineFactory().createInitialState();
			init_pass_5[i].setName("initial_state");
		}
		
		//create super state and assign names
		for (int i=0; i<sup_pass_5.length; i++) {
			sup_pass_5[i] = JRubyHelpers.getStatemachineFactory().createSuperState();
			sup_pass_5[i].setName("super_state" + i);
		}
		
		//create region and assign names
		for (int i=0; i<reg_pass_5.length; i++) {
			reg_pass_5[i] = JRubyHelpers.getStatemachineFactory().createRegion();
			reg_pass_5[i].setName("region" + i);
		}
		
		//create normal state
		for (int i=0; i<norm_pass_5.length; i++)
			norm_pass_5[i] = JRubyHelpers.getStatemachineFactory().createNormalState();
		
		//assign names to normal state
		norm_pass_5[0].setName("normal_state1");
		norm_pass_5[1].setName("normal_state2");
		norm_pass_5[2].setName("normal_state3");
		norm_pass_5[3].setName("normal_state4");
		
		//add states to statemachine
		statm_pass_5.getStates().add(init_pass_5[0]);
		statm_pass_5.getStates().add(sup_pass_5[0]);
		statm_pass_5.getStates().add(fin_pass_5);
		
		//add region to super state
		sup_pass_5[0].getRegions().add(reg_pass_5[0]);
		
		//add states to region
		reg_pass_5[0].getStates().add(init_pass_5[1]);
		reg_pass_5[0].getStates().add(norm_pass_5[0]);
		reg_pass_5[0].getStates().add(norm_pass_5[1]);
		reg_pass_5[0].getStates().add(sup_pass_5[1]);
		
		//add region to super state
		sup_pass_5[1].getRegions().add(reg_pass_5[1]);
		
		//add states to the region
		reg_pass_5[1].getStates().add(init_pass_5[2]);
		reg_pass_5[1].getStates().add(norm_pass_5[2]);
		reg_pass_5[1].getStates().add(norm_pass_5[3]);
		
		StateMachineForGeneration smg_pass_5 = new StateMachineForGeneration(
				"duplicateStates", statm_pass_5);
		
		sb.setLength(0);
		
		assertEquals(
				true,
				validate_duplicate[10].duplicateNames(smg_pass_5,
						smg_pass_5.getStates()));
		
		assertEquals("", sb.toString());
		
		//Three level statemachine that passes
		
		StateMachine statm_pass_6 = JRubyHelpers.getStatemachineFactory().createStateMachine();
		
		InitialState[] init_pass_6 = new InitialState[4];
		NormalState[] norm_pass_6 = new NormalState[6];
		SuperState[] sup_pass_6 = new SuperState[3];
		Region[] reg_pass_6 = new Region[3];
		
		//create final state and assign a name
		FinalState fin_pass_6 = JRubyHelpers.getStatemachineFactory().createFinalState();
		fin_pass_6.setName("final_state");
		
		//create Initial state and assign name
		for (int i=0; i<init_pass_6.length; i++) {
			init_pass_6[i] = JRubyHelpers.getStatemachineFactory().createInitialState();
			init_pass_6[i].setName("init_state" + i);
		}
		
		//create super state and assign name
		for (int i=0; i<sup_pass_6.length; i++) {
			sup_pass_6[i] = JRubyHelpers.getStatemachineFactory().createSuperState();
			sup_pass_6[i].setName("super_state" + i);
		}
		
		//create region and assign name
		for (int i=0; i<reg_pass_6.length; i++) {
			reg_pass_6[i] = JRubyHelpers.getStatemachineFactory().createRegion();
			reg_pass_6[i].setName("region" + i);
		}
		
		//create normal state
		for (int i=0; i<norm_pass_6.length; i++)
			norm_pass_6[i] = JRubyHelpers.getStatemachineFactory().createNormalState();
		
		//assign names
		norm_pass_6[0].setName("normal_state1");
		norm_pass_6[1].setName("normal_state2");
		norm_pass_6[2].setName("normal_state3");
		norm_pass_6[3].setName("normal_state4");
		norm_pass_6[4].setName("normal_state5");
		norm_pass_6[5].setName("normal_state6");
		
		//add states to statemachine
		statm_pass_6.getStates().add(init_pass_6[0]);
		statm_pass_6.getStates().add(fin_pass_6);
		statm_pass_6.getStates().add(sup_pass_6[0]);
		
		//add region to the superstate
		sup_pass_6[0].getRegions().add(reg_pass_6[0]);
		
		//add states to region
		reg_pass_6[0].getStates().add(init_pass_6[1]);
		reg_pass_6[0].getStates().add(norm_pass_6[0]);
		reg_pass_6[0].getStates().add(norm_pass_6[1]);
		reg_pass_6[0].getStates().add(sup_pass_6[1]);
		
		//add region to super state
		sup_pass_6[1].getRegions().add(reg_pass_6[1]);
		
		//add states to region
		reg_pass_6[1].getStates().add(init_pass_6[2]);
		reg_pass_6[1].getStates().add(norm_pass_6[2]);
		reg_pass_6[1].getStates().add(norm_pass_6[3]);
		reg_pass_6[1].getStates().add(sup_pass_6[2]);
		
		//add region to super state
		sup_pass_6[2].getRegions().add(reg_pass_6[2]);
		
		//add states to the region
		reg_pass_6[2].getStates().add(init_pass_6[3]);
		reg_pass_6[2].getStates().add(norm_pass_6[4]);
		reg_pass_6[2].getStates().add(norm_pass_6[5]);
		
		StateMachineForGeneration smg_pass_6 = new StateMachineForGeneration(
				"duplicateStates", statm_pass_6);
		
		sb.setLength(0);
		
		assertEquals(
				true,
				validate_duplicate[11].duplicateNames(smg_pass_6,
						smg_pass_6.getStates()));
		
		assertEquals("", sb.toString());
	}
}
