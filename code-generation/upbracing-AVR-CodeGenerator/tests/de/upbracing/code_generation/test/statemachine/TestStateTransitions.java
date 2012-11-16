//Test the following conditions for transitions from initial state by setting the 
//transitionInfo for transition from initial state to other states not meeting the 
//following conditions
// 
//Condition 1 : It should have no events
//Condition 2 : It should not have waiting transitions (waitType should be null)
//Condition 3 : It should not have a condition (should not be 1, true, or a 
//				 non-null value)	
//
//Test the following conditions for incoming transitions to normal, super, or
//final state. Three types of waitTypes are possible - before, after, and wait
//
//Condition 1 : If a transition has a condition or an event then it must not have 
//				 waitType set to "wait". 
//Condition 2 : If a transition has a waitType set to "before" then it should have
// 				 a condition or event or both. 
// 
//Test the conditions for transition from initial state/ to normal/final/super state
//in one level statemachine or in nested statemachine (states contained in regions)
// 
//TransitionInfo can be set by using setTransitionInfo method of Transition object. The syntax
//is "event_name : waitType(duration) [condition]". For eg "add : wait(10ms) [a>b]".


package de.upbracing.code_generation.test.statemachine;

import static org.junit.Assert.*;
import org.junit.Test;

import Statecharts.FinalState;
import Statecharts.InitialState;
import Statecharts.NormalState;
import Statecharts.Region;
import Statecharts.StateMachine;
import Statecharts.SuperState;
import Statecharts.Transition;
import de.upbracing.code_generation.JRubyHelpers;
import de.upbracing.code_generation.Messages;
import de.upbracing.code_generation.Messages.Message;
import de.upbracing.code_generation.Messages.MessageListener;
import de.upbracing.code_generation.config.MCUConfiguration;
import de.upbracing.code_generation.fsm.model.StateMachineForGeneration;
import de.upbracing.code_generation.generators.fsm.Helpers;
import de.upbracing.code_generation.generators.fsm.Validator;
import de.upbracing.code_generation.test.TestHelpers;

public class TestStateTransitions {
	final StringBuffer sb = new StringBuffer();
	Messages[] message = new Messages[10];
	MCUConfiguration[] config = new MCUConfiguration[10];
	Validator[] validator = new Validator[10];

	@Test
	public void test() {
		// create messages object, config object, and add statemachineformatters
		for (int i = 0; i < message.length; i++) {
			message[i] = new Messages();
			config[i] = new MCUConfiguration();
			Helpers.addStatemachineFormatters(message[i],
					config[i].getStatemachines());

			// add message listener
			message[i].addMessageListener(new MessageListener() {
				@Override
				public void message(Message msg) {
					msg.format(sb);
				}
			});

			// create object of type Validator and set message for the validator
			validator[i] = new Validator(config[i], false, null);
			validator[i].setMessages(message[i]);
		}

		/** Statemachine that does not meet conditions at level 1 and level 2 */

		StateMachine statemachine = JRubyHelpers.getStatemachineFactory()
				.createStateMachine();

		InitialState[] init = new InitialState[4];

		// create initial state
		for (int i = 0; i < init.length; i++)
			init[i] = JRubyHelpers.getStatemachineFactory()
					.createInitialState();

		init[0].setName("Stmach_start1");
		init[1].setName("stmach_start2");
		init[2].setName("Sup1_start");
		init[3].setName("Sup2_start");

		// create final state and assign name
		FinalState[] fin = new FinalState[1];
		fin[0] = JRubyHelpers.getStatemachineFactory().createFinalState();
		fin[0].setName("finalstate");

		// create normal state
		NormalState[] norm = new NormalState[5];

		for (int i = 0; i < norm.length; i++)
			norm[i] = JRubyHelpers.getStatemachineFactory().createNormalState();

		// assign names to normal state
		norm[0].setName("normal_1");
		norm[1].setName("Sup1_normal1");
		norm[2].setName("Sup1_normal2");
		norm[3].setName("Sup2_normal1");
		norm[4].setName("Sup2_normal2");

		SuperState[] sup = new SuperState[2];

		// create super state
		for (int i = 0; i < sup.length; i++)
			sup[i] = JRubyHelpers.getStatemachineFactory().createSuperState();

		Region[] reg = new Region[2];

		// create region
		for (int i = 0; i < reg.length; i++)
			reg[i] = JRubyHelpers.getStatemachineFactory().createRegion();

		// set names of regions and super states
		sup[0].setName("super_1");
		sup[1].setName("super_2");
		reg[0].setName("reg_1");
		reg[1].setName("reg_2");

		statemachine.getStates().add(init[0]);
		statemachine.getStates().add(init[1]);
		statemachine.getStates().add(norm[0]);
		statemachine.getStates().add(sup[0]);
		statemachine.getStates().add(sup[1]);
		statemachine.getStates().add(fin[0]);

		sup[0].getRegions().add(reg[0]);
		reg[0].getStates().add(init[2]);
		reg[0].getStates().add(norm[1]);
		reg[1].getStates().add(norm[2]);

		sup[1].getRegions().add(reg[1]);
		reg[1].getStates().add(init[3]);
		reg[1].getStates().add(norm[3]);
		reg[1].getStates().add(norm[4]);

		Transition[] trans = new Transition[12];

		for (int i = 0; i < trans.length; i++)
			trans[i] = JRubyHelpers.getStatemachineFactory().createTransition();

		// statemachine
		trans[0].setSource(init[0]);
		trans[0].setDestination(norm[0]);
		trans[0].setTransitionInfo("start");

		trans[1].setSource(norm[0]);
		trans[1].setDestination(sup[0]);
		trans[1].setTransitionInfo("add : wait(10ms) [a=b]");

		trans[2].setSource(sup[0]);
		trans[2].setDestination(norm[0]);
		trans[2].setTransitionInfo("multiply : wait(10ms) [c=a]");

		trans[3].setSource(sup[0]);
		trans[3].setDestination(sup[1]);
		trans[3].setTransitionInfo("wait(10ms)");

		trans[4].setSource(sup[1]);
		trans[4].setDestination(fin[0]);
		trans[4].setTransitionInfo("before(10ms)");

		trans[5].setSource(fin[0]);
		trans[5].setDestination(sup[1]);
		trans[5].setTransitionInfo("before(10ms) [c>a+b]");

		trans[6].setSource(init[0]);
		trans[6].setDestination(sup[0]);
		trans[6].setTransitionInfo("");

		// superstate 1
		trans[7].setSource(init[2]);
		trans[7].setDestination(norm[1]);
		trans[7].setTransitionInfo("");

		trans[8].setSource(norm[1]);
		trans[8].setDestination(norm[2]);
		trans[8].setTransitionInfo("push : before(10ms) [a>c]");

		trans[9].setSource(norm[2]);
		trans[9].setDestination(norm[1]);
		trans[9].setTransitionInfo("wait(10ms)");

		// superstate 2
		trans[10].setSource(init[3]);
		trans[10].setDestination(norm[3]);
		trans[10].setTransitionInfo("mark : before(10ms) [b=c]");

		trans[11].setSource(norm[3]);
		trans[11].setDestination(init[3]);
		trans[11].setTransitionInfo("push : before(10ms) [b=c]");

		for (int i = 0; i < trans.length; i++)
			statemachine.getTransitions().add(trans[i]);

		StateMachineForGeneration smg_1 = new StateMachineForGeneration(
				"Testtransitions", statemachine);

		config[0].getStatemachines().add(smg_1);

		assertEquals(false, validator[0].validate());
		assertEquals(
				TestHelpers
						.loadResource("expected_results/statemachines/expectedTransitionTestResults/ExpectedTransitionTestResults.txt"),
				sb.toString());

		/** One level statemachine */

		/** Statesmachines that do not meet the conditions for initial state */

		// Transition from initial state has a waitType

		// create statemachine
		StateMachine statm_1 = JRubyHelpers.getStatemachineFactory()
				.createStateMachine();
		statm_1.setBasePeriod("1s");

		// create initial and final state
		InitialState init_1 = JRubyHelpers.getStatemachineFactory()
				.createInitialState();
		FinalState fin_1 = JRubyHelpers.getStatemachineFactory()
				.createFinalState();
		Transition trans_1 = JRubyHelpers.getStatemachineFactory()
				.createTransition();

		// set name of initial and final state
		init_1.setName("initial_state");
		fin_1.setName("final_state");

		// add states to statemachine
		statm_1.getStates().add(init_1);
		statm_1.getStates().add(fin_1);

		// set source and destination of transition
		trans_1.setSource(init_1);
		trans_1.setDestination(fin_1);
		trans_1.setTransitionInfo("wait(10ms)");

		// add transition to statemachine
		statm_1.getTransitions().add(trans_1);

		// create object of statemachineforGeneration and add it to the config
		StateMachineForGeneration smg_2 = new StateMachineForGeneration(
				"initalHasWait", statm_1);
		config[1].getStatemachines().add(smg_2);

		sb.setLength(0);

		assertEquals(false, validator[1].validate());
		assertEquals(
				TestHelpers
						.loadResource("expected_results/statemachines/expectedTransitionTestResults/expectedTransitionTestResults_1.txt"),
				sb.toString());

		// Transition from initial state has an event

		// create statemachine
		StateMachine statm_2 = JRubyHelpers.getStatemachineFactory()
				.createStateMachine();
		statm_2.setBasePeriod("1s");

		// create initial and final state
		InitialState init_2 = JRubyHelpers.getStatemachineFactory()
				.createInitialState();
		FinalState fin_2 = JRubyHelpers.getStatemachineFactory()
				.createFinalState();
		Transition trans_2 = JRubyHelpers.getStatemachineFactory()
				.createTransition();

		// set name of initial and final state

		init_2.setName("initial_state");
		fin_2.setName("final_state");

		// add states to statemachine
		statm_2.getStates().add(init_2);
		statm_2.getStates().add(fin_2);

		// set source and destination of transition
		trans_2.setSource(init_2);
		trans_2.setDestination(fin_2);

		// add event
		trans_2.setTransitionInfo("add");

		// add transition to statemachine
		statm_2.getTransitions().add(trans_2);

		// create object of statemachineforGeneration and add it to the config
		StateMachineForGeneration smg_3 = new StateMachineForGeneration(
				"initalHasEvent", statm_2);
		config[2].getStatemachines().add(smg_3);

		sb.setLength(0);

		assertEquals(false, validator[2].validate());
		assertEquals(
				TestHelpers
						.loadResource("expected_results/statemachines/expectedTransitionTestResults/expectedTransitionTestResults_2.txt"),
				sb.toString());

		// Transition from initial state has a condition

		// create statemachine and set the base period
		StateMachine statm_3 = JRubyHelpers.getStatemachineFactory()
				.createStateMachine();
		statm_3.setBasePeriod("1s");

		// create initial and final state and transition
		InitialState init_3 = JRubyHelpers.getStatemachineFactory()
				.createInitialState();
		FinalState fin_3 = JRubyHelpers.getStatemachineFactory()
				.createFinalState();
		Transition trans_3 = JRubyHelpers.getStatemachineFactory()
				.createTransition();

		// set name of initial state and final state
		init_3.setName("initial_state");
		fin_3.setName("final_state");

		// add states to the statemachine
		statm_3.getStates().add(init_3);
		statm_3.getStates().add(fin_3);

		// set source and destination of the transition
		trans_3.setSource(init_3);
		trans_3.setDestination(fin_3);
		// set condition for the transition
		trans_3.setTransitionInfo("[a>b]");

		// add transition to the statemachine
		statm_3.getTransitions().add(trans_3);

		StateMachineForGeneration smg_4 = new StateMachineForGeneration(
				"initalHasCondition", statm_3);
		config[3].getStatemachines().add(smg_4);

		sb.setLength(0);

		assertEquals(false, validator[3].validate());
		assertEquals(
				TestHelpers
						.loadResource("expected_results/statemachines/expectedTransitionTestResults/expectedTransitionTestResults_3.txt"),
				sb.toString());

		/** Statemachine that meets the condition for the initial state */

		// create statemachine and set the base period
		StateMachine statm_4 = JRubyHelpers.getStatemachineFactory()
				.createStateMachine();
		statm_4.setBasePeriod("1s");

		// create initial and final state and transition
		InitialState init_4 = JRubyHelpers.getStatemachineFactory()
				.createInitialState();
		FinalState fin_4 = JRubyHelpers.getStatemachineFactory()
				.createFinalState();
		Transition trans_4 = JRubyHelpers.getStatemachineFactory()
				.createTransition();

		// set name of initial state and final state
		init_4.setName("initial_state");
		fin_4.setName("final_state");

		// add states to the statemachine
		statm_4.getStates().add(init_4);
		statm_4.getStates().add(fin_4);

		// set source and destination of the transition
		trans_4.setSource(init_4);
		trans_4.setDestination(fin_4);
		// no transition info
		trans_4.setTransitionInfo("");

		// add transition to the statemachine
		statm_4.getTransitions().add(trans_4);

		StateMachineForGeneration smg_5 = new StateMachineForGeneration(
				"initalmeetsConditions", statm_4);
		config[4].getStatemachines().add(smg_5);

		sb.setLength(0);

		assertEquals(true, validator[4].validate());
		assertEquals("", sb.toString());

		/** Statemachine that fail conditions for normal/super states */
		
		/** Statemachines that fail*/
		
		//super state has incoming transition with an event when wait type
		//is wait
		
		// create statemachine and assign base period
		StateMachine statm_5 = JRubyHelpers.getStatemachineFactory()
				.createStateMachine();
		statm_5.setBasePeriod("1s");

		// create intitial, final, normal, and super states
		InitialState init_5 = JRubyHelpers.getStatemachineFactory()
				.createInitialState();
		FinalState fin_5 = JRubyHelpers.getStatemachineFactory()
				.createFinalState();
		NormalState norm_5 = JRubyHelpers.getStatemachineFactory()
				.createNormalState();
		SuperState sup_5 = JRubyHelpers.getStatemachineFactory()
				.createSuperState();

		// array of transitions
		Transition[] trans_5 = new Transition[3];

		// create transitions
		for (int i = 0; i < trans_5.length; i++)
			trans_5[i] = JRubyHelpers.getStatemachineFactory()
					.createTransition();

		// set names of initial, final, normal, and super states
		init_5.setName("initial_state");
		fin_5.setName("final_state");
		norm_5.setName("normal");
		sup_5.setName("super");

		// add states to the statemachine
		statm_5.getStates().add(init_5);
		statm_5.getStates().add(fin_5);
		statm_5.getStates().add(norm_5);
		statm_5.getStates().add(sup_5);

		// set source and destination of normal state
		trans_5[0].setSource(init_5);
		trans_5[0].setDestination(norm_5);
		trans_5[0].setTransitionInfo("");

		trans_5[1].setSource(norm_5);
		trans_5[1].setDestination(sup_5);
		// set event with wait type wait
		trans_5[1].setTransitionInfo("add: wait(10ms)");

		trans_5[2].setSource(sup_5);
		trans_5[2].setDestination(fin_5);
		trans_5[2].setTransitionInfo("");

		// add transitions to the statemachine
		for (int i = 0; i < trans_5.length; i++)
			statm_5.getTransitions().add(trans_5[i]);

		StateMachineForGeneration smg_6 = new StateMachineForGeneration(
				"transitionHasEvent", statm_5);
		config[5].getStatemachines().add(smg_6);

		sb.setLength(0);

		assertEquals(false, validator[5].validate());
		assertEquals(
				TestHelpers
						.loadResource("expected_results/statemachines/expectedTransitionTestResults/expectedTransitionTestResults_4.txt"),
				sb.toString());
		
		//normal state has incoming transition with condition when 
		//wait type is wait
		
		//create statemachine and assign base period
		StateMachine statm_6 = JRubyHelpers.getStatemachineFactory().createStateMachine();
		statm_6.setBasePeriod("1s");
		
		//create intial, final, normal, and super states
		InitialState init_6 = JRubyHelpers.getStatemachineFactory().createInitialState();
		FinalState fin_6 = JRubyHelpers.getStatemachineFactory().createFinalState();
		NormalState norm_6 = JRubyHelpers.getStatemachineFactory().createNormalState();
		SuperState sup_6 = JRubyHelpers.getStatemachineFactory().createSuperState();
		
		//array of Transitions
		Transition[] trans_6 = new Transition[3];
		
		//create transitions
		for (int i=0; i<trans_6.length; i++) 
			trans_6[i] = JRubyHelpers.getStatemachineFactory().createTransition();
		
		//set names of initial, final, normal, and super states
		init_6.setName("initial_state");
		fin_6.setName("final_state");
		norm_6.setName("normal");
		sup_6.setName("super");
		
		//add states to the statemachine
		statm_6.getStates().add(init_6);
		statm_6.getStates().add(fin_6);
		statm_6.getStates().add(norm_6);
		statm_6.getStates().add(sup_6);
		
		//set source and destination of transitions
		trans_6[0].setSource(init_6);
		trans_6[0].setDestination(sup_6);
		trans_6[0].setTransitionInfo("");
		
		trans_6[1].setSource(sup_6);
		trans_6[1].setDestination(norm_6);
		//set condition when wait type is "wait"
		trans_6[1].setTransitionInfo("wait(10ms) [a>b]");
		
		trans_6[2].setSource(norm_6);
		trans_6[2].setDestination(fin_6);
		trans_6[2].setTransitionInfo("");
		
		for (int i=0; i < trans_6.length; i++)
			statm_6.getTransitions().add(trans_6[i]);

		StateMachineForGeneration smg_7 = new StateMachineForGeneration(
				"transitionHasCondition", statm_6);
		config[6].getStatemachines().add(smg_7);

		sb.setLength(0);

		assertEquals(false, validator[6].validate());
		assertEquals(
				TestHelpers
						.loadResource("expected_results/statemachines/expectedTransitionTestResults/expectedTransitionTestResults_5.txt"),
				sb.toString());
		
		//normal state has incoming transition with no condition and no event when 
		//wait type is before
		
		//create statemachine and assign base period
		StateMachine statm_7 = JRubyHelpers.getStatemachineFactory().createStateMachine();
		statm_7.setBasePeriod("1s");
		
		//create initial, final, normal, and super state
		InitialState init_7 = JRubyHelpers.getStatemachineFactory().createInitialState();
		FinalState fin_7 = JRubyHelpers.getStatemachineFactory().createFinalState();
		NormalState norm_7 = JRubyHelpers.getStatemachineFactory().createNormalState();
		SuperState sup_7 = JRubyHelpers.getStatemachineFactory().createSuperState();
		
		//array of transitions
		Transition[] trans_7 = new Transition[3];
		
		//create transitions
		for (int i=0; i<trans_7.length; i++)
			trans_7[i] = JRubyHelpers.getStatemachineFactory().createTransition();
		
		//assign names to initial, final, normal, and super states
		init_7.setName("initial_state");
		fin_7.setName("final_state");
		norm_7.setName("normal");
		sup_7.setName("super");
		
		//add states to the statemachine
		statm_7.getStates().add(init_7);
		statm_7.getStates().add(fin_7);
		statm_7.getStates().add(norm_7);
		statm_7.getStates().add(sup_7);
		
		//set source and destination of transitions
		trans_7[0].setSource(init_7);
		trans_7[0].setDestination(sup_7);
		trans_7[0].setTransitionInfo("");
		
		trans_7[1].setSource(sup_7);
		trans_7[1].setDestination(norm_7);
		trans_7[1].setTransitionInfo("before(10ms)");
		
		trans_7[2].setSource(norm_7);
		trans_7[2].setDestination(fin_7);
		trans_7[2].setTransitionInfo("");
		
		//add transitions to the statemachine
		for (int i=0; i<trans_7.length; i++)
			statm_7.getTransitions().add(trans_7[i]);
		
		StateMachineForGeneration smg_8 = new StateMachineForGeneration(
				"transitionHasNoConditionOrEvent", statm_7);
		config[7].getStatemachines().add(smg_8);

		sb.setLength(0);

		assertEquals(false, validator[7].validate());
		assertEquals(
				TestHelpers
						.loadResource("expected_results/statemachines/expectedTransitionTestResults/expectedTransitionTestResults_6.txt"),
				sb.toString());
		
		/**Statemachines that pass the test*/
		
		//statemachine that has wait type wait but has no condition or event
		
		//create statemachine and assign base period
		StateMachine statm_8 = JRubyHelpers.getStatemachineFactory().createStateMachine();
		statm_8.setBasePeriod("1s");
		
		//create initial,final, normal, and super states
		InitialState init_8 = JRubyHelpers.getStatemachineFactory().createInitialState();
		FinalState fin_8 = JRubyHelpers.getStatemachineFactory().createFinalState();
		NormalState norm_8 = JRubyHelpers.getStatemachineFactory().createNormalState();
		SuperState sup_8 = JRubyHelpers.getStatemachineFactory().createSuperState();
		
		//array of transitions
		Transition[] trans_8 = new Transition[3];
		
		//create transitions
		for (int i=0; i<trans_8.length; i++) 
			trans_8[i] = JRubyHelpers.getStatemachineFactory().createTransition();
		
		//assign names to initial, final, normal, and super states
		init_8.setName("initial_state");
		fin_8.setName("final_state");
		norm_8.setName("normal");
		sup_8.setName("super");
		
		//add states to the statemachine
		statm_8.getStates().add(init_8);
		statm_8.getStates().add(fin_8);
		statm_8.getStates().add(norm_8);
		statm_8.getStates().add(sup_8);
		
		//set source and destination of the transition
		trans_8[0].setSource(init_8);
		trans_8[0].setDestination(sup_8);
		trans_8[0].setTransitionInfo("");
		
		trans_8[1].setSource(sup_8);
		trans_8[1].setDestination(norm_8);
		trans_8[1].setTransitionInfo("wait(10ms)");
		
		trans_8[2].setSource(norm_8);
		trans_8[2].setDestination(fin_8);
		trans_8[2].setTransitionInfo("");
		
		//add transitions to the statemachine
		for (int i=0; i<trans_8.length; i++) 
			statm_8.getTransitions().add(trans_8[i]);
		
		StateMachineForGeneration smg_9 = new StateMachineForGeneration(
				"transitionHasNoEventOrCondition", statm_8);
		config[8].getStatemachines().add(smg_9);

		sb.setLength(0);

		assertEquals(true, validator[8].validate());
		assertEquals("", sb.toString());
		
		
		//statemachine that has wait type "before" but has condition
		
		//create statemachine and assign base period
		StateMachine statm_9 = JRubyHelpers.getStatemachineFactory().createStateMachine();
		statm_9.setBasePeriod("1s");
		
		//create initial,final, normal, and super states
		InitialState init_9 = JRubyHelpers.getStatemachineFactory().createInitialState();
		FinalState fin_9 = JRubyHelpers.getStatemachineFactory().createFinalState();
		NormalState norm_9 = JRubyHelpers.getStatemachineFactory().createNormalState();
		SuperState sup_9 = JRubyHelpers.getStatemachineFactory().createSuperState();
		
		//array of transitions
		Transition[] trans_9 = new Transition[3];
		
		//create transitions
		for (int i=0; i<trans_9.length; i++) 
			trans_9[i] = JRubyHelpers.getStatemachineFactory().createTransition();
		
		//assign names to initial, final, normal, and super states
		init_9.setName("initial_state");
		fin_9.setName("final_state");
		norm_9.setName("normal");
		sup_9.setName("super");
		
		//add states to the statemachine
		statm_9.getStates().add(init_9);
		statm_9.getStates().add(fin_9);
		statm_9.getStates().add(norm_9);
		statm_9.getStates().add(sup_9);
		
		//set source and destination of the transition
		trans_9[0].setSource(init_9);
		trans_9[0].setDestination(sup_9);
		trans_9[0].setTransitionInfo("");
		
		trans_9[1].setSource(sup_9);
		trans_9[1].setDestination(norm_9);
		trans_9[1].setTransitionInfo("add : before(10ms) [a>b]");
		
		trans_9[2].setSource(norm_9);
		trans_9[2].setDestination(fin_9);
		trans_9[2].setTransitionInfo("");
		
		//add transitions to the statemachine
		for (int i=0; i<trans_9.length; i++) 
			statm_9.getTransitions().add(trans_9[i]);
		
		StateMachineForGeneration smg_10 = new StateMachineForGeneration(
				"transitionHasCondition", statm_9);
		config[9].getStatemachines().add(smg_10);

		sb.setLength(0);

		assertEquals(true, validator[9].validate());
		assertEquals("", sb.toString());
	}
}
