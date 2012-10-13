package de.upbracing.code_generation.test;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import Statecharts.NormalState;
import Statecharts.Region;
import Statecharts.State;
import Statecharts.StateMachine;
import Statecharts.SuperState;
import de.upbracing.code_generation.JRubyHelpers;
import de.upbracing.code_generation.Messages;
import de.upbracing.code_generation.Messages.ContextItem;
import de.upbracing.code_generation.fsm.model.StateMachineForGeneration;
import de.upbracing.code_generation.generators.fsm.Validator;

public class TestStateNameValidation {
	Messages messages = new Messages();
	Validator validate = new Validator(messages);
	
	@Test
	public void testNameValidate() {
		StateMachine statemachine = JRubyHelpers.getStatemachineFactory()
				.createStateMachine();

		NormalState[] state = new NormalState[12];
		SuperState[] sup = new SuperState[4];
		Region[] reg = new Region[4];

		for (int i = 0; i < sup.length; i++) {
			sup[i] = JRubyHelpers.getStatemachineFactory().createSuperState();
			sup[i].setName("super_" + i);
		}

		for (int i = 0; i < reg.length; i++) {
			reg[i] = JRubyHelpers.getStatemachineFactory().createRegion();
			reg[i].setName("region_" + i);
		}

		for (int i = 0; i < state.length; i++) {
			state[i] = JRubyHelpers.getStatemachineFactory()
					.createNormalState();
		}

		// fails validation
		state[0].setName("%state");
		state[1].setName("st_ate-");
		state[2].setName(" state");
		state[3].setName("2state");
		state[4].setName("st ate");
		state[5].setName("st\\ate");
		state[6].setName("state$");

		// passes validation
		state[7].setName("s2");
		state[8].setName("state");
		state[9].setName("_state");
		state[10].setName(null);
		state[11].setName("");
			
		statemachine.getStates().add(state[0]);
		statemachine.getStates().add(state[7]);
		statemachine.getStates().add(sup[0]);

		sup[0].getRegions().add(reg[0]);
		reg[0].getStates().add(state[1]);
		reg[0].getStates().add(state[8]);
		reg[0].getStates().add(sup[1]);

		sup[1].getRegions().add(reg[1]);
		reg[1].getStates().add(state[2]);
		reg[1].getStates().add(state[3]);
		reg[1].getStates().add(state[9]);
		reg[1].getStates().add(sup[2]);

		sup[2].getRegions().add(reg[2]);
		reg[2].getStates().add(state[4]);
		reg[2].getStates().add(state[10]);
		reg[2].getStates().add(sup[3]);

		sup[3].getRegions().add(reg[3]);
		reg[3].getStates().add(state[5]);
		reg[3].getStates().add(state[6]);
		reg[3].getStates().add(state[11]);

		StateMachineForGeneration smg = new StateMachineForGeneration("test",
				statemachine);

		ContextItem statm_context = messages.pushContext(smg);
		evaluteNormalStates(smg.getStates(), smg);
		statm_context.pop();
			
		StringBuffer sb = new StringBuffer();
		messages.summarizeForCode(sb);

		assertEquals(
				TestHelpers
						.loadResource("expected_results/statemachines/expectedstatenames.txt"),
				sb.toString());

	}
	
	private void evaluteNormalStates(List<State> states, StateMachineForGeneration smg) {
		for(State s : states) {
			validate.nameValidate(s);
			
			if(s instanceof SuperState)
				evalutateInnerStates((SuperState) s, smg);
		}	
	}
	
	private void evalutateInnerStates(SuperState st, StateMachineForGeneration smg){
		ContextItem super_context = messages.pushContext(st);
		for(Region r : ((SuperState) st).getRegions()) {
			ContextItem region_context = messages.pushContext(r);
			evaluteNormalStates(r.getStates(), smg);
			region_context.pop();
		}
		super_context.pop();
	}
}