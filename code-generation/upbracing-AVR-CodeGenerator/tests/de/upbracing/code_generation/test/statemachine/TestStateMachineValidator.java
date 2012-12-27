//Statemachine must have a base rate. It must be set with seconds/ms as unit.
//Test that states in a deeply nested statemachine are correctly iterated.
//Messages are printed in correct context. If error occurs in statemachine
//it should tell in which statemachine the error took place, and similarly 
//states.
//
//Possible test cases :
// 
//Case 1 : States/regions having duplicate names (check Testduplicate names for
//		   testing details). 
//
//Case 2 : Statemachine, states, regions, and events have valid C identifier names.
//
//
//Case 3 : Number of initial states in each level must be one, that is a statemachine
//		   must have only one initial statemachine, and each region of a superstate
//		   must have only one initial state.
//
//Case 4 : Final state cannot have any outgoing transitions.
//
//Case 5 : Initial state has no incoming transitions.
//
//Case 6 : Initial has one and only one outgoing transitions.
//
//Case 7 : Validate transition info for outgoingTransitions from initial state - transitions
//		   cannot have a condition, an event, or a waitType.
//
//Case 8 : Validate transition info for incoming transitions to final, normal, super
// 			states - if transition has waitType "wait" then it must not have an event or
//			condition,
//Case 9 : if transition has waitType "before" then it must have a condition
//          or event. 

package de.upbracing.code_generation.test.statemachine;

import static org.junit.Assert.*;

import java.util.Collections;

import org.eclipse.emf.common.util.URI;
import org.junit.Test;

import statemachine.FinalState;
import statemachine.InitialState;
import statemachine.NormalState;
import statemachine.Region;
import statemachine.StateMachine;
import statemachine.SuperState;
import statemachine.Transition;

import de.upbracing.code_generation.JRubyHelpers;
import de.upbracing.code_generation.Messages;
import de.upbracing.code_generation.Messages.Message;
import de.upbracing.code_generation.Messages.MessageListener;
import de.upbracing.code_generation.config.MCUConfiguration;
import de.upbracing.code_generation.fsm.model.StateMachineForGeneration;
import de.upbracing.code_generation.generators.fsm.Helpers;
import de.upbracing.code_generation.generators.fsm.Validator;
import de.upbracing.code_generation.test.TestHelpers;

public class TestStateMachineValidator {

	Messages[] messages = new Messages[15];
	Validator[] validate = new Validator[15];
	final StringBuffer sb = new StringBuffer();
	MCUConfiguration[] config = new MCUConfiguration[15];

	@Test
	public void test() {

		for (int i = 0; i < messages.length; i++) {
			config[i] = new MCUConfiguration();
			messages[i] = new Messages();
			validate[i] = new Validator(config[i], false, null);
			Helpers.addStatemachineFormatters(messages[i],
					Collections.<StateMachineForGeneration> emptyList());
			validate[i].setMessages(messages[i]);
			messages[i].addMessageListener(new MessageListener() {
				@Override
				public void message(Message msg) {
					msg.format(sb);
				}
			});
		}

		// statemachine fails validation
		StateMachine statemachine = JRubyHelpers.getStatemachineFactory()
				.createStateMachine();

		InitialState[] init = new InitialState[4];
		for (int i = 0; i < init.length; i++)
			init[i] = JRubyHelpers.getStatemachineFactory()
					.createInitialState();

		init[0].setName("Stmach_start1");
		init[1].setName("stmach_start2");
		init[2].setName("Sup1_start");
		init[3].setName("Sup2_start");

		FinalState[] fin = new FinalState[1];
		fin[0] = JRubyHelpers.getStatemachineFactory().createFinalState();

		fin[0].setName("finalstate");

		NormalState[] norm = new NormalState[5];

		for (int i = 0; i < norm.length; i++)
			norm[i] = JRubyHelpers.getStatemachineFactory().createNormalState();

		norm[0].setName("normal_1");
		norm[1].setName("Sup1_normal1");
		norm[2].setName("Sup1_normal2");
		norm[3].setName("Sup2_normal1");
		norm[4].setName("Sup2_normal2");

		SuperState[] sup = new SuperState[2];

		for (int i = 0; i < sup.length; i++)
			sup[i] = JRubyHelpers.getStatemachineFactory().createSuperState();

		Region[] reg = new Region[2];

		for (int i = 0; i < reg.length; i++)
			reg[i] = JRubyHelpers.getStatemachineFactory().createRegion();

		sup[0].setName("super_1");
		sup[1].setName("super_2");
		reg[0].setName("reg_1");
		reg[1].setName("reg_2");

		statemachine.setBasePeriod("1ms");
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
				"FailedStateMachine", statemachine);

		MCUConfiguration config_fail = new MCUConfiguration();
		config_fail.getStatemachines().add(smg_1);
		Validator validate_fail = new Validator(config_fail, false, null);

		validate_fail.setMessages(messages[0]);
		assertEquals(false, validate_fail.validate());
		assertEquals(
				TestHelpers
						.loadResource("/expected_results/statemachines/expectedValidatorResults/" +
								"expectedValidatorResults.txt"), sb.toString());

		// passes validation
		StateMachine statem_pass = JRubyHelpers.getStatemachineFactory()
				.createStateMachine();
		InitialState[] init_pass = new InitialState[3];

		for (int i = 0; i < init_pass.length; i++)
			init_pass[i] = JRubyHelpers.getStatemachineFactory()
					.createInitialState();

		init_pass[0].setName("Statm_init");
		init_pass[1].setName("Sup0_init");
		init_pass[2].setName("Sup1_init");

		FinalState[] fin_pass = new FinalState[1];
		fin_pass[0] = JRubyHelpers.getStatemachineFactory().createFinalState();
		fin_pass[0].setName("Statm_final");

		NormalState[] norm_pass = new NormalState[3];

		for (int i = 0; i < norm_pass.length; i++)
			norm_pass[i] = JRubyHelpers.getStatemachineFactory()
					.createNormalState();

		norm_pass[0].setName("Statm_norm");
		norm_pass[1].setName("Sup0_norm");
		norm_pass[2].setName("Sup1_norm");

		SuperState[] sup_pass = new SuperState[2];
		Region[] reg_pass = new Region[2];

		for (int i = 0; i < sup_pass.length; i++) {
			sup_pass[i] = JRubyHelpers.getStatemachineFactory()
					.createSuperState();
			sup_pass[i].setName("Sup_" + i);
			reg_pass[i] = JRubyHelpers.getStatemachineFactory().createRegion();
			reg_pass[i].setName("Reg_" + i);
		}

		statem_pass.setBasePeriod("1ms");
		statem_pass.getStates().add(init_pass[0]);
		statem_pass.getStates().add(norm_pass[0]);
		statem_pass.getStates().add(sup_pass[0]);
		statem_pass.getStates().add(sup_pass[1]);
		statem_pass.getStates().add(fin_pass[0]);

		sup_pass[0].getRegions().add(reg_pass[0]);
		reg_pass[0].getStates().add(init_pass[1]);
		reg_pass[0].getStates().add(norm_pass[1]);

		sup_pass[1].getRegions().add(reg_pass[1]);
		reg_pass[1].getStates().add(init_pass[2]);
		reg_pass[1].getStates().add(norm_pass[2]);

		Transition[] trans_pass = new Transition[8];

		for (int i = 0; i < trans_pass.length; i++)
			trans_pass[i] = JRubyHelpers.getStatemachineFactory()
					.createTransition();

		trans_pass[0].setSource(init_pass[0]);
		trans_pass[0].setDestination(norm_pass[0]);
		trans_pass[0].setTransitionInfo("");

		trans_pass[1].setSource(norm_pass[0]);
		trans_pass[1].setDestination(sup_pass[0]);
		trans_pass[1].setTransitionInfo("add : before(10ms) [a>b]");

		trans_pass[2].setSource(sup_pass[0]);
		trans_pass[2].setDestination(sup_pass[1]);
		trans_pass[2].setTransitionInfo("sub : after(10ms)");

		trans_pass[3].setSource(sup_pass[1]);
		trans_pass[3].setDestination(norm_pass[0]);
		trans_pass[3].setTransitionInfo(" : wait(10ms)");

		trans_pass[4].setSource(sup_pass[1]);
		trans_pass[4].setDestination(fin_pass[0]);
		trans_pass[4].setTransitionInfo("mul : before(10ms) [a!=b]");

		trans_pass[5].setSource(norm_pass[0]);
		trans_pass[5].setDestination(fin_pass[0]);
		trans_pass[5].setTransitionInfo("div : after(10ms)");

		trans_pass[6].setSource(init_pass[1]);
		trans_pass[6].setDestination(norm_pass[1]);
		trans_pass[6].setTransitionInfo("");

		trans_pass[7].setSource(init_pass[2]);
		trans_pass[7].setDestination(norm_pass[2]);
		trans_pass[7].setTransitionInfo("");

		for (int i = 0; i < trans_pass.length; i++)
			statem_pass.getTransitions().add(trans_pass[i]);

		StateMachineForGeneration smg_2 = new StateMachineForGeneration(
				"PassedStatemachine", statem_pass);

		MCUConfiguration config_pass = new MCUConfiguration();
		config_pass.getStatemachines().add(smg_2);

		Validator validate_pass = new Validator(config_pass, false, null);
		sb.setLength(0);

		validate_pass.setMessages(messages[1]);
		assertEquals(true, validate_pass.validate());
		assertEquals("", sb.toString());
		
		/**Statemachine fails the test because two states (with statemachine as parent) and two
		 * states in region_1 have same names
		 */

		config[0].getStatemachines().load("duplicate_names", URI.createURI(TestHelpers
					.getResourceURL("files/validator_statemachines/duplicate_names.statemachine").toString()));
		
		sb.setLength(0);
		assertEquals(false, validate[0].validate());
		assertEquals(TestHelpers.
				loadResource("/expected_results/statemachines/expectedValidatorResults/duplicateNames.txt"), sb.toString());
		
		/**Statemachine fails the test because two states - one in super state and one in
		 * statemachine have invalid C identifier names
		 */
		
		//load statemachine to the configuration
		config[1].getStatemachines().load("invalidName", URI.createURI(TestHelpers
				.getResourceURL("files/validator_statemachines/invalidName.statemachine").toString()));
		
		//reset stringbuffer
		sb.setLength(0);
		assertEquals(false, validate[1].validate());
		assertEquals(TestHelpers
				.loadResource("/expected_results/statemachines/expectedValidatorResults/invalidNames.txt"), sb.toString());
		
		/**Statemachine fails test because there are two initial states in statemachine*/
		
		//load statemachine to the configuration
		config[2].getStatemachines().load("moreThanOneInitial_1", URI.createURI(TestHelpers
				.getResourceURL("files/validator_statemachines/Initial_statemachine.statemachine").toString()));
		
		//reset stringbuffer
		sb.setLength(0);
		assertEquals(false, validate[2].validate());
		assertEquals(TestHelpers
				.loadResource("/expected_results/statemachines" +
						"/expectedValidatorResults/moreThanOneInitial_1.txt"), sb.toString());
	
		/**Statemachine fails test because there are more than one initial states in super state
		 * at region_3 and region_2
		 */
		
		config[3].getStatemachines().load("moreThanOneInitial_2", URI.createURI(TestHelpers
				.getResourceURL("files/validator_statemachines/initial_superstate.statemachine").toString()));
		
		//reset stringbuffer
		sb.setLength(0);
		assertEquals(false, validate[3].validate());
		assertEquals(TestHelpers
				.loadResource("/expected_results/statemachines/" +
						"expectedValidatorResults/moreThanOneInitial_2.txt"), sb.toString());
		
		/**Statemachine fails test because final state has outgoing transitions in statemachine
		 * and in super state super_1 and super_2
		 */
		
		config[4].getStatemachines().load("final", URI.createURI(TestHelpers
				.getResourceURL("files/validator_statemachines/final_statemachine.statemachine").toString()));
		
		//reset stringbuffer
		sb.setLength(0);
		assertEquals(false, validate[4].validate());
		assertEquals(TestHelpers
				.loadResource("/expected_results/statemachines/" +
						"expectedValidatorResults/finalOutgoing.txt"), sb.toString());
		
		/**Statemachine fails test because initial states in statemachine, region_1, and region_3
		 * have incoming transitions
		 */
		
		config[5].getStatemachines().load("initial_incoming", URI.createURI(TestHelpers
				.getResourceURL("files/validator_statemachines/initial_incoming.statemachine").toString()));
		
		//reset stringbuffer
		sb.setLength(0);
		assertEquals(false, validate[5].validate());
		assertEquals(TestHelpers
				.loadResource("/expected_results/statemachines/" +
						"expectedValidatorResults/initial_incoming.txt"), sb.toString());
		
		/**Statemachine fails test because initial states in statemachine, region_1, and region_3
		 * have more than one outgoing transitions
		 */
		
		config[6].getStatemachines().load("initial_outgoing", URI.createURI(TestHelpers
				.getResourceURL("files/validator_statemachines/initial_outgoing.statemachine").toString()));
		
		//reset stringbuffer
		sb.setLength(0);
		assertEquals(false, validate[6].validate());
		assertEquals(TestHelpers
				.loadResource("/expected_results/statemachines/" +
						"expectedValidatorResults/initial_outgoing.txt"), sb.toString());
		
		/**Statemachine fails test because initial states in statemachine , region_1, region_3
		 * have a waiting Transitions, or event, or condition
		 */
		
		config[7].getStatemachines().load("initial_transitionInfo", URI.createURI(TestHelpers
				.getResourceURL("files/validator_statemachines/initial_transitionInfo.statemachine").toString()));
		
		//reset stringbuffer
		sb.setLength(0);
		assertEquals(false, validate[7].validate());
		assertEquals(TestHelpers
				.loadResource("/expected_results/statemachines/" +
						"expectedValidatorResults/initial_transitionInfo.txt"), sb.toString());
		
		/**Statemachine fails test because incoming transitions to normal states
		 * in statemachine, region_1, and region_3 have event or condition when waitType is "wait"
		 */
		
		config[8].getStatemachines().load("wait_transitionInfo", URI.createURI(TestHelpers
				.getResourceURL("files/validator_statemachines/wait_TransitionInfo.statemachine").toString()));
		
		//reset stringbuffer
		sb.setLength(0);
		assertEquals(false, validate[8].validate());
		assertEquals(TestHelpers
				.loadResource("/expected_results/statemachines/" +
						"expectedValidatorResults/wait_transitionInfo.txt"), sb.toString());
		
		/**Statemachine fails test because incoming transitions to normal states in statemachine,
		 * region_1, and region_3 do not have event or condition or both when waitType is "before"
		 */
		
		config[9].getStatemachines().load("before_transitionInfo", URI.createURI(TestHelpers
				.getResourceURL("files/validator_statemachines/before_transitionInfo.statemachine").toString()));
		
		//reset stringbuffer
		sb.setLength(0);
		assertEquals(false, validate[9].validate());
		assertEquals(TestHelpers
				.loadResource("/expected_results/statemachines/" +
						"expectedValidatorResults/before_transitionInfo.txt"), sb.toString());
		
		/**Statemachine that fails because there are duplicate names and some states/regions
		 * do not have valid C identifier names
		 */
		
		config[10].getStatemachines().load("dup_inavalidnames", URI.createURI(TestHelpers
				.getResourceURL("files/validator_statemachines/dup_invalidnames.statemachine").toString()));
		
		//reset stringbuffer
		sb.setLength(0);
		assertEquals(false, validate[10].validate());
		assertEquals(TestHelpers
				.loadResource("/expected_results/statemachines/" +
						"expectedValidatorResults/dup_invalidnames.txt"), sb.toString());
		
		/**Statemachine fails test because some regions and statemachine has more than one initial states, 
		 * duplicate names, and invalid names
		 */
		
		config[11].getStatemachines().load("dup_invalidnames_moreInitial", URI.createURI(TestHelpers
				.getResourceURL("files/validator_statemachines/dup_invName_moreInit.statemachine").toString()));
		
		//reset stringbuffer
		sb.setLength(0);
		assertEquals(false, validate[11].validate());
		assertEquals(TestHelpers
				.loadResource("/expected_results/statemachines/" +
						"expectedValidatorResults/dup_inv_moreInit.txt"), sb.toString());
		
		/**Statemachine that fails because there are some duplicate names, invalid state/region names, more
		 * than one initial states, and initial state with incoming transitions/ more than one outgoing transitions
		 */
		

		config[12].getStatemachines().load("dup_invNames_initVal", URI.createURI(TestHelpers
				.getResourceURL("files/validator_statemachines/dup_invN_moreIn_IncIn.statemachine").toString()));
		
		//reset stringbuffer
		sb.setLength(0);
		assertEquals(false, validate[12].validate());
		assertEquals(TestHelpers
				.loadResource("/expected_results/statemachines/" +
						"expectedValidatorResults/dup_invN_initVal.txt"), sb.toString());
		
		/**Statemachine that fails test because there are some duplicate names, invalid state/region names, more
		 * than one initial states, and initial state with incoming/more than 1 outgoing transitions/
		 * condition/event/wait transition
		 */
		config[13].getStatemachines().load("fails_first4_cases", URI.createURI(TestHelpers
				.getResourceURL("files/validator_statemachines/fails_alltests.statemachine").toString()));
		
		//reset stringbuffer
		sb.setLength(0);
		assertEquals(false, validate[13].validate());
		assertEquals(TestHelpers
				.loadResource("/expected_results/statemachines/" +
						"expectedValidatorResults/fails_alltests.txt"), sb.toString());
		
	}
}
