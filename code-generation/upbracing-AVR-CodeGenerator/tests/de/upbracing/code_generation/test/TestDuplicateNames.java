package de.upbracing.code_generation.test;

import static org.junit.Assert.*;

import org.junit.Test;

import de.upbracing.code_generation.JRubyHelpers;
import de.upbracing.code_generation.Messages;
import de.upbracing.code_generation.fsm.model.StateMachineForGeneration;
import de.upbracing.code_generation.generators.fsm.Validator;

import Statecharts.FinalState;
import Statecharts.InitialState;
import Statecharts.NormalState;
import Statecharts.Region;
import Statecharts.StateMachine;
import Statecharts.SuperState;

public class TestDuplicateNames {
	Messages messages = new Messages();
	Validator validate_duplicate = new Validator(messages);
	
	@Test
	public void testDuplicateNames() {
		//fails validation
		StateMachine statemachine = JRubyHelpers.getStatemachineFactory().createStateMachine();
		
		InitialState[] init = new InitialState[5];
		FinalState[] fin = new FinalState[2];
		NormalState[] norm = new NormalState[9];
		SuperState[] sup = new SuperState[2];
		Region[] reg = new Region[4];
		
		for (int i=0; i<init.length; i++)
			init[i] = JRubyHelpers.getStatemachineFactory().createInitialState();
		
		for (int i=0; i<fin.length; i++) 
			fin[i] = JRubyHelpers.getStatemachineFactory().createFinalState();
		
		for (int i=0; i<norm.length; i++)
			norm[i] = JRubyHelpers.getStatemachineFactory().createNormalState();
		
		for (int i=0; i<sup.length; i++)
			sup[i] = JRubyHelpers.getStatemachineFactory().createSuperState();
		
		for (int i=0; i<reg.length; i++)
			reg[i] = JRubyHelpers.getStatemachineFactory().createRegion();
		
		
		init[0].setName("state");
		init[1].setName("Init_0");
		init[2].setName("Init_1");
		init[3].setName("Init_2");
		init[4].setName("Init_3");
		
		fin[0].setName("fin_0");
		fin[1].setName("fin_1");
		
		norm[0].setName("state");
		norm[1].setName("norm_0");
		norm[2].setName("norm_0");
		norm[3].setName("norm_1");
		norm[4].setName("norm_2");
		norm[5].setName("norm_3");
		norm[6].setName("norm_4");
		norm[7].setName("norm_5");
		norm[8].setName("norm_5");
		
		sup[0].setName("state");
		sup[1].setName("sup_2");
		
		reg[0].setName("reg_0");
		reg[1].setName("reg_0");
		reg[2].setName("reg_1");
		reg[3].setName("reg_2");
		
		statemachine.getStates().add(init[0]);
		statemachine.getStates().add(norm[0]);
		statemachine.getStates().add(sup[0]);
		statemachine.getStates().add(sup[1]);
		
		sup[0].getRegions().add(reg[0]);
		sup[0].getRegions().add(reg[1]);
		
		reg[0].getStates().add(init[1]);
		reg[0].getStates().add(norm[1]);
		reg[0].getStates().add(norm[2]);
		
		reg[1].getStates().add(init[2]);
		reg[1].getStates().add(norm[3]);
		reg[1].getStates().add(norm[4]);
		
		sup[1].getRegions().add(reg[2]);
		sup[1].getRegions().add(reg[3]);
		
		reg[2].getStates().add(init[3]);
		reg[2].getStates().add(norm[5]);
		reg[2].getStates().add(norm[6]);
		
		reg[3].getStates().add(init[4]);
		reg[3].getStates().add(norm[7]);
		reg[3].getStates().add(norm[8]);
		reg[3].getStates().add(fin[1]);
		
		//passes validation
		StateMachine statemachine_pass = JRubyHelpers.getStatemachineFactory().createStateMachine();
		
		InitialState[] init_pass = new InitialState[5];
		FinalState[] fin_pass = new FinalState[2];
		NormalState[] norm_pass = new NormalState[9];
		SuperState[] sup_pass = new SuperState[2];
		Region[] reg_pass = new Region[4];
		
		for (int i=0; i<init.length; i++)
			init_pass[i] = JRubyHelpers.getStatemachineFactory().createInitialState();
		
		for (int i=0; i<fin.length; i++) 
			fin_pass[i] = JRubyHelpers.getStatemachineFactory().createFinalState();
		
		for (int i=0; i<norm.length; i++)
			norm_pass[i] = JRubyHelpers.getStatemachineFactory().createNormalState();
		
		for (int i=0; i<sup.length; i++)
			sup_pass[i] = JRubyHelpers.getStatemachineFactory().createSuperState();
		
		for (int i=0; i<reg.length; i++)
			reg_pass[i] = JRubyHelpers.getStatemachineFactory().createRegion();
		
		
		init_pass[0].setName("state");
		init_pass[1].setName("Init_0");
		init_pass[2].setName("Init_1");
		init_pass[3].setName("Init_2");
		init_pass[4].setName("Init_3");
		
		fin_pass[0].setName("fin_0");
		fin_pass[1].setName("fin_1");
		
		norm_pass[0].setName("statm_norm");
		norm_pass[1].setName("norm_0");
		norm_pass[2].setName("norm_1");
		norm_pass[3].setName("norm_2");
		norm_pass[4].setName("norm_3");
		norm_pass[5].setName("norm_4");
		norm_pass[6].setName("norm_5");
		norm_pass[7].setName("norm_6");
		norm_pass[8].setName("norm_7");
		
		sup_pass[0].setName("sup_1");
		sup_pass[1].setName("sup_2");
		
		reg_pass[0].setName("reg_0");
		reg_pass[1].setName("reg_1");
		reg_pass[2].setName("reg_2");
		reg_pass[3].setName("reg_3");
		
		statemachine_pass.getStates().add(init_pass[0]);
		statemachine_pass.getStates().add(norm_pass[0]);
		statemachine_pass.getStates().add(sup_pass[0]);
		statemachine_pass.getStates().add(sup_pass[1]);
		
		sup_pass[0].getRegions().add(reg_pass[0]);
		sup_pass[0].getRegions().add(reg_pass[1]);
		
		reg_pass[0].getStates().add(init_pass[1]);
		reg_pass[0].getStates().add(norm_pass[1]);
		reg_pass[0].getStates().add(norm_pass[2]);
		
		reg_pass[1].getStates().add(init_pass[2]);
		reg_pass[1].getStates().add(norm_pass[3]);
		reg_pass[1].getStates().add(norm_pass[4]);
		
		sup_pass[1].getRegions().add(reg_pass[2]);
		sup_pass[1].getRegions().add(reg_pass[3]);
		
		reg_pass[2].getStates().add(init_pass[3]);
		reg_pass[2].getStates().add(norm_pass[5]);
		reg_pass[2].getStates().add(norm_pass[6]);
		
		reg_pass[3].getStates().add(init_pass[4]);
		reg_pass[3].getStates().add(norm_pass[7]);
		reg_pass[3].getStates().add(norm_pass[8]);
		reg_pass[3].getStates().add(fin_pass[1]);
		
		//passes
		StateMachineForGeneration smg_pass = new StateMachineForGeneration("withNoDuplicates", statemachine_pass);
		
		assertEquals(true, validate_duplicate.duplicateNames(smg_pass, smg_pass.getStates()));
		StringBuffer sb_pass = new StringBuffer();
		messages.summarizeForCode(sb_pass);
		assertEquals("", sb_pass.toString());
		
		//fails
		StateMachineForGeneration smg = new StateMachineForGeneration("withDuplicates", statemachine);
		
		assertEquals(false, validate_duplicate.duplicateNames(smg, smg.getStates()));
		final StringBuffer sb = new StringBuffer();
		messages.summarizeForCode(sb);
		assertEquals(TestHelpers.loadResource("/expected_results/statemachines/expectedDuplicateNameResults.txt"), sb.toString());
	}

}
