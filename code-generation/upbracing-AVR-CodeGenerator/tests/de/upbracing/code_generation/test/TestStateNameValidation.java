package de.upbracing.code_generation.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import Statecharts.NormalState;
import de.upbracing.code_generation.JRubyHelpers;
import de.upbracing.code_generation.Messages;
import de.upbracing.code_generation.Messages.Message;
import de.upbracing.code_generation.Messages.MessageListener;
import de.upbracing.code_generation.generators.fsm.Validator;

public class TestStateNameValidation {
	Messages messages = new Messages();
	Validator validate = new Validator(messages);

	@Test
	public void testNameValidate() {
		NormalState[] state = new NormalState[12];

		for (int i = 0; i < state.length; i++) 
			state[i] = JRubyHelpers.getStatemachineFactory()
					.createNormalState();

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

		Validator validator = new Validator(messages);
		validator.setMessages(messages);
		final StringBuffer sb = new StringBuffer();

		messages.addMessageListener(new MessageListener() {
			public void message(Message msg) {
				msg.format(sb);
			}
		});

		
		for (int i = 0; i < 7; i++) 
			assertEquals(false, validator.nameValidate(state[i]));
		
		for (int i = 7; i < state.length; i++)
			assertEquals(true, validator.nameValidate(state[i]));
		
		assertEquals(TestHelpers.loadResource("/expected_results/statemachines/expectedstatenames.txt"), sb.toString());
	}
}