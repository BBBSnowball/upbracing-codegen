package de.upbracing.code_generation.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.jruby.compiler.ir.operands.Array;
import org.junit.Test;

import Statecharts.NormalState;
import Statecharts.State;
import Statecharts.StateMachine;
import de.upbracing.code_generation.JRubyHelpers;
import de.upbracing.code_generation.config.MCUConfiguration;
import de.upbracing.code_generation.fsm.model.StateMachineForGeneration;
import de.upbracing.code_generation.generators.StatemachineGenerator;

public class TestStateNameValidation {
	@Test
	public void testNameValidate() {
		StateMachine statemachine = new JRubyHelpers().getStatemachineFactory().createStateMachine();
		StateMachineForGeneration smg = new StateMachineForGeneration("test", statemachine);
		StatemachineGenerator sm = new StatemachineGenerator();
		
		NormalState[] state = new NormalState[10];
		
		for(int i = 0; i<state.length; i++){
			state[i] = new JRubyHelpers().getStatemachineFactory().createNormalState();
			state[i].setParent(statemachine);
		}
		
		//fails validation
		state[0].setName("%state");
		state[1].setName(null);
		state[2].setName(" state");
		state[3].setName("2state");
		state[4].setName("st ate");
		state[5].setName("st\\ate");
		state[6].setName("state$");
		
		
		//passes validation
		state[7].setName("s2");
		state[8].setName("state");
		state[9].setName("_state");
		
		for(int i=0; i<7; i++)
			assertEquals(false, sm.nameValidate(state[i], smg));
	
		for(int i=7; i<10; i++)
			assertEquals(true, sm.nameValidate(state[i], smg));
	}
}
