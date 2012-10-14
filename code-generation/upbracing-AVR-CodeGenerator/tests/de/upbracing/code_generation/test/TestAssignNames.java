package de.upbracing.code_generation.test;

import static org.junit.Assert.*;

import org.junit.Test;

import de.upbracing.code_generation.JRubyHelpers;
import de.upbracing.code_generation.config.MCUConfiguration;
import de.upbracing.code_generation.fsm.model.StateMachineForGeneration;
import de.upbracing.code_generation.generators.fsm.Updater;

import Statecharts.FinalState;
import Statecharts.InitialState;
import Statecharts.NormalState;
import Statecharts.Region;
import Statecharts.StateMachine;
import Statecharts.SuperState;

public class TestAssignNames {

	@Test
	public void testAssignNames() {
		StateMachine statemachine = JRubyHelpers.getStatemachineFactory()
				.createStateMachine();
		
		String NL = System.getProperty("line.separator");

		InitialState[] init = new InitialState[4];
		FinalState[] fin = new FinalState[3];
		NormalState[] norm = new NormalState[4];
		SuperState[] sup = new SuperState[2];
		Region[] reg = new Region[2];
		
		for (int i=0; i<init.length; i++)
			init[i] = JRubyHelpers.getStatemachineFactory().createInitialState();
		
		for (int i=0; i<fin.length; i++)
			fin[i] = JRubyHelpers.getStatemachineFactory().createFinalState();
		
		for (int i=0; i<norm.length; i++)
			norm[i] = JRubyHelpers.getStatemachineFactory().createNormalState();
		
		for (int i=0; i<sup.length; i++) {
			sup[i] = JRubyHelpers.getStatemachineFactory().createSuperState();
			reg[i] = JRubyHelpers.getStatemachineFactory().createRegion();
			reg[i].setName("reg_"+i);
		}
		
		statemachine.getStates().add(init[0]);
		statemachine.getStates().add(norm[0]);
		statemachine.getStates().add(norm[3]);
		statemachine.getStates().add(sup[0]);
		statemachine.getStates().add(sup[1]);
		statemachine.getStates().add(fin[0]);
		
		sup[0].getRegions().add(reg[0]);
		reg[0].getStates().add(init[1]);
		reg[0].getStates().add(norm[1]);
		reg[0].getStates().add(fin[1]);
		
		sup[1].getRegions().add(reg[1]);
		reg[1].getStates().add(init[2]);
		reg[1].getStates().add(norm[2]);
		reg[1].getStates().add(fin[2]);
		
		norm[0].setName("Statm_norm");
		sup[0].setName("Sup_1");
		reg[0].setName("reg_1");
		init[1].setName("Sup1_init");
		fin[1].setName("Sup1_fin");
		reg[1].setName("reg_2");
		norm[2].setName("Sup2_norm");
		
		StateMachineForGeneration smg = new StateMachineForGeneration("AssignNames", statemachine);
		MCUConfiguration config = new MCUConfiguration();
		config.getStatemachines().add(smg);
				
		Updater updatenames = new Updater(config);
		updatenames.assignNames(config);
		
		StringBuilder sb = new StringBuilder();
		
		for (int i=0; i<init.length; i++)
			sb.append("Initial state : " + init[i].getName() + NL);
		
		for (int i=0; i<norm.length; i++)
			sb.append("Normal state : " + norm[i].getName() + NL);
		
		for (int i=0; i<sup.length; i++)
			sb.append("Super state : " + sup[i].getName() + NL);
		
		for (int i=0; i<fin.length; i++)
			sb.append("Final state : " + fin[i].getName() + NL);
		
		assertEquals(TestHelpers.loadResource("/expected_results/statemachines/expectedAssignedNames.txt"), sb.toString());
	
	}
}
