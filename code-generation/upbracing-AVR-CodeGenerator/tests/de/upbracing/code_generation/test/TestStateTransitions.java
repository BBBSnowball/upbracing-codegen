package de.upbracing.code_generation.test;

import static org.junit.Assert.*;
import java.util.List;

import org.junit.Test;

import Statecharts.FinalState;
import Statecharts.InitialState;
import Statecharts.NormalState;
import Statecharts.Region;
import Statecharts.State;
import Statecharts.StateMachine;
import Statecharts.SuperState;
import Statecharts.Transition;
import de.upbracing.code_generation.JRubyHelpers;
import de.upbracing.code_generation.Messages;
import de.upbracing.code_generation.Messages.ContextItem;
import de.upbracing.code_generation.fsm.model.StateMachineForGeneration;
import de.upbracing.code_generation.generators.fsm.Validator;

public class TestStateTransitions {

	Messages messages = new Messages();
	Validator validate = new Validator(messages);

	@Test
	public void test() {
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

		for (int i=0; i<trans.length; i++)
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
		

		for(int i=0; i<trans.length; i++)
			statemachine.getTransitions().add(trans[i]);
		

		StateMachineForGeneration smg = new StateMachineForGeneration(
				"Testtransitions", statemachine);
		
		ContextItem statm_context = messages.pushContext(smg);
		evaluteTransitions(smg.getStates(), smg);
		statm_context.pop();

		StringBuffer sb = new StringBuffer();
		messages.summarizeForCode(sb);
		
		assertEquals(TestHelpers
				.loadResource("expected_results/statemachines/ExpectedTransitionTestResults.txt"), sb.toString());

	}

	private void evaluteTransitions(List<State> states,
			StateMachineForGeneration smg) {
		
		validate.initialCount(states);
		
		for (State s : states) {
			if (s instanceof InitialState) {
				ContextItem initial_context = messages.pushContext(s);
				validate.initalValidate(s, smg);
				initial_context.pop();
			}
			
			if (s instanceof FinalState) {
				ContextItem final_context = messages.pushContext(s);
				validate.finalValidate(s, smg);
				final_context.pop();
			}
			
			if (s instanceof NormalState) {
				ContextItem normal_context = messages.pushContext(s);
				validate.normSupValidate(s, smg);
				normal_context.pop();
			}
			
			if (s instanceof SuperState) {
				validate.normSupValidate(s, smg);
				evalutateInnerTransitions((SuperState) s, smg);
			}
		}
	}

	private void evalutateInnerTransitions(SuperState st,
			StateMachineForGeneration smg) {
		ContextItem super_context = messages.pushContext(st);
		for (Region r : ((SuperState) st).getRegions()) {
			ContextItem region_context = messages.pushContext(r);
			evaluteTransitions(r.getStates(), smg);
			region_context.pop();
		}
		super_context.pop();
	}
}
