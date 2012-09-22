package de.upbracing.code_generation.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import Statecharts.NormalState;
import Statecharts.StateMachine;
import de.upbracing.code_generation.JRubyHelpers;
import de.upbracing.code_generation.fsm.model.StateMachineForGeneration;
import de.upbracing.code_generation.generators.StatemachineGenerator;

public class TestStateNameValidation {
	@Test
	public void testNameValidate() {
		//TODO We should use them in some tests.
		StateMachine statemachine = JRubyHelpers.getStatemachineFactory().createStateMachine();
		StateMachineForGeneration smg = new StateMachineForGeneration("test", statemachine);
		
		NormalState[] state = new NormalState[12];
		
		for(int i = 0; i<state.length; i++){
			state[i] = JRubyHelpers.getStatemachineFactory().createNormalState();
			state[i].setParent(statemachine);
		}
		
		//fails validation
		state[0].setName("%state");
		state[1].setName("st_ate-");
		state[2].setName(" state");
		state[3].setName("2state");
		state[4].setName("st ate");
		state[5].setName("st\\ate");
		state[6].setName("state$");
		
		
		//passes validation
		state[7].setName("s2");
		state[8].setName("state");
		state[9].setName("_state");
		state[10].setName(null);
		state[11].setName("");
		
		for(int i=0; i<7; i++)
			assertEquals(false, StatemachineGenerator.nameValidate(state[i], smg));
	
		for(int i=7; i<state.length; i++)
			assertEquals(true, StatemachineGenerator.nameValidate(state[i], smg));
	}
}
