package de.upbracing.code_generation.test;

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
import de.upbracing.code_generation.generators.fsm.Validator;

public class TestStateMachineValidator {

	Messages[] messages = new Messages[12];
	Validator[] validate = new Validator[12];
	final StringBuffer sb = new StringBuffer();

	@Test
	public void test() {
		
		for (int i=0; i<messages.length; i++) {
			
			messages[i] = new Messages();
			validate[i] = new Validator(messages[i]);
			messages[i].addMessageListener(new MessageListener() {
				@Override
				public void message(Message msg) {
					msg.format(sb);
				}
			});
		}
		
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
						.loadResource("/expected_results/statemachines/expectedValidatorResults.txt"),
				sb.toString());
				
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
		
		
	}
}
