//  Try different sorts of combinations of statemachines in which the states/regions 
//  don't have names (each type of state - Initial, Normal, Final, and Super state- must have a valid name).
//  Also each region must have a name.
//  
//  Case 1: Some states in simple non-nested statemachine don't have names
//  Case 2: Some states in one level deep statemachine don't have names
//  Case 3: Some states multiple level deep don't have names

package de.upbracing.code_generation.test.statemachine;

import static org.junit.Assert.*;

import org.junit.Test;

import statemachine.FinalState;
import statemachine.InitialState;
import statemachine.NormalState;
import statemachine.Region;
import statemachine.State;
import statemachine.StateMachine;
import statemachine.SuperState;

import de.upbracing.code_generation.JRubyHelpers;
import de.upbracing.code_generation.config.CodeGeneratorConfigurations;
import de.upbracing.code_generation.fsm.model.StateMachineForGeneration;
import de.upbracing.code_generation.generators.fsm.Updater;
import de.upbracing.code_generation.test.TestHelpers;


public class TestAssignNames {

	StringBuilder sb = new StringBuilder();
	String NL = System.getProperty("line.separator");

	@Test
	public void testAssignNames() {
		StateMachine statemachine = JRubyHelpers.getStatemachineFactory()
				.createStateMachine();
	
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
		CodeGeneratorConfigurations config = new CodeGeneratorConfigurations();
		config.getStatemachines().add(smg);
				
		Updater updatenames = new Updater(config);
		updatenames.updateConfig(config);
		
		sb.append("Statemachine with unnamed states under different combinations" + NL + NL);
		
		for (int i=0; i<init.length; i++)
			sb.append("Initial state : " + init[i].getName() + NL);
		
		for (int i=0; i<norm.length; i++)
			sb.append("Normal state : " + norm[i].getName() + NL);
		
		for (int i=0; i<sup.length; i++)
			sb.append("Super state : " + sup[i].getName() + NL);
		
		for (int i=0; i<fin.length; i++)
			sb.append("Final state : " + fin[i].getName() + NL);
		
		assertEquals(TestHelpers.loadResource("expected_results/statemachines/expectedAssignedNames/expectedAssignedNames_1.txt"), sb.toString());
		
		/**One level statemachine*/
		
		StateMachine statm_1 = JRubyHelpers.getStatemachineFactory().createStateMachine();
		
		//create initial state, final state, normal state, super state, and region
		
		InitialState init_1 = JRubyHelpers.getStatemachineFactory().createInitialState(); 
		NormalState norm_1 = JRubyHelpers.getStatemachineFactory().createNormalState();
		FinalState fin_1 = JRubyHelpers.getStatemachineFactory().createFinalState();
		SuperState sup_1 = JRubyHelpers.getStatemachineFactory().createSuperState();
		Region reg_1 = JRubyHelpers.getStatemachineFactory().createRegion();
		
		//add states to statemachine
		statm_1.getStates().add(init_1);
		statm_1.getStates().add(fin_1);
		statm_1.getStates().add(norm_1);
		statm_1.getStates().add(sup_1);
		
		//add region to super state
		sup_1.getRegions().add(reg_1);
		
		sb.setLength(0);
		sb.append("One level state machine with unnamed states and region" + NL + NL);
		
		StateMachineForGeneration smg_1 = new StateMachineForGeneration("noNames_1", statm_1);
		CodeGeneratorConfigurations config_1 = new CodeGeneratorConfigurations();
		config_1.getStatemachines().add(smg_1);
		
		Updater update_1 = new Updater(config_1);
		update_1.updateConfig(config_1);
		
		for (State state : statm_1.getStates())
			sb.append("State : " + state.getName() + NL);
		
		sb.append("Region : " + reg_1.getName());
		
		assertEquals(TestHelpers.loadResource("expected_results/statemachines/expectedAssignedNames/expectedAssignedNames_2.txt"), sb.toString());
		
		/**Two level statemachine*/
		
		StateMachine statm_2 = JRubyHelpers.getStatemachineFactory().createStateMachine();
		
		InitialState[] init_2 = new InitialState[3];
		FinalState[] fin_2 = new FinalState[3];
		NormalState[] norm_2 = new NormalState[4];
		Region[] reg_2 = new Region[2];
		
		//create super state and assign name to it
		SuperState sup_2 = JRubyHelpers.getStatemachineFactory().createSuperState();
		sup_2.setName("super_state");
		
		//create initial, final, and normal states and regions 
		for (int i=0; i<init_2.length; i++) {
			init_2[i] = JRubyHelpers.getStatemachineFactory().createInitialState();
			init_2[i].setName("initial_state" + i);
		}
		
		for (int i=0; i<fin_2.length; i++) {
			fin_2[i] = JRubyHelpers.getStatemachineFactory().createFinalState();
			fin_2[i].setName("final_state" + i);
		}
		
		for (int i=0; i<norm_2.length; i++) 
			norm_2[i] = JRubyHelpers.getStatemachineFactory().createNormalState();
		
		for (int i=0; i<reg_2.length; i++)
			reg_2[i] = JRubyHelpers.getStatemachineFactory().createRegion();
		
		reg_2[0].setName("region_1");
		
		//add states to statemachine
		statm_2.getStates().add(init_2[0]);
		statm_2.getStates().add(sup_2);
		statm_2.getStates().add(fin_2[0]);
		
		//add regions to super state
		sup_2.getRegions().add(reg_2[0]);
		sup_2.getRegions().add(reg_2[1]);
		
		//add states to first region
		reg_2[0].getStates().add(init_2[1]);
		reg_2[0].getStates().add(norm_2[0]);
		reg_2[0].getStates().add(norm_2[1]);
		reg_2[0].getStates().add(fin_2[1]);
		
		//add states to second region
		reg_2[1].getStates().add(init_2[2]);
		reg_2[1].getStates().add(norm_2[2]);
		reg_2[1].getStates().add(norm_2[3]);
		reg_2[1].getStates().add(fin_2[2]);
		
		sb.setLength(0);
		sb.append("Two level state machine with unnamed states and region(s) assigned names" + NL + NL);
		
		StateMachineForGeneration smg_2 = new StateMachineForGeneration("noNames_2", statm_2);
		CodeGeneratorConfigurations config_2 = new CodeGeneratorConfigurations();
		config_2.getStatemachines().add(smg_2);
		
		Updater update_2 = new Updater(config_2);
		update_2.updateConfig(config_2);
		
		for (int i=0; i<init.length; i++)
			sb.append("Initial state : " + init[i].getName() + NL);
		
		for (int i=0; i<norm.length; i++)
			sb.append("Normal state : " + norm[i].getName() + NL);
		
		for (int i=0; i<sup.length; i++)
			sb.append("Super state : " + sup[i].getName() + NL);
		
		for (int i=0; i<fin.length; i++)
			sb.append("Final state : " + fin[i].getName() + NL);
		
		for (Region region : sup_2.getRegions())
			sb.append("Region : " + region.getName() + NL);
		
		assertEquals(TestHelpers.loadResource("expected_results/statemachines/expectedAssignedNames/expectedAssignedNames_3.txt"), sb.toString());
		
		/** Three level statemachine */
		
		StateMachine statm_3 = JRubyHelpers.getStatemachineFactory().createStateMachine();

		InitialState[] init_3 = new InitialState[3]; 
		SuperState[] sup_3 = new SuperState[2];
		Region[] reg_3 = new Region[2];
		NormalState[] norm_3 = new NormalState[2];
		
		//create and assign names to final state
		FinalState fin_3 = JRubyHelpers.getStatemachineFactory().createFinalState();
		fin_3.setName("Final_state");
		
		//create initial state and assign names
		for (int i=0; i<init_3.length; i++) {
			init_3[i] = JRubyHelpers.getStatemachineFactory().createInitialState();
			init_3[i].setName("inital_state" + i);
		}
		
		//create super state and assign names
		for (int i=0; i<sup_3.length; i++) {
			sup_3[i] = JRubyHelpers.getStatemachineFactory().createSuperState();
			sup_3[i].setName("super_state" + i);
		}
		
		//create regions
		for (int i=0; i<reg_3.length; i++) 
			reg_3[i] = JRubyHelpers.getStatemachineFactory().createRegion();
		
		//create normal state
		for (int i=0; i<norm_3.length; i++) 
			norm_3[i] = JRubyHelpers.getStatemachineFactory().createNormalState();
				
		//add states to statemachine
		statm_3.getStates().add(init_3[0]);
		statm_3.getStates().add(fin_3);
		statm_3.getStates().add(sup_3[0]);
		
		//add region to super state
		sup_3[0].getRegions().add(reg_3[0]);
		
		//add states to region
		reg_3[0].getStates().add(init_3[1]);
		reg_3[0].getStates().add(sup_3[1]);
		
		//add region to super state
		sup_3[1].getRegions().add(reg_3[1]);
		
		//add states to region
		reg_3[1].getStates().add(init_3[2]);
		reg_3[1].getStates().add(norm_3[0]);
		reg_3[1].getStates().add(norm_3[1]);
		
		sb.setLength(0);
		sb.append("Three level state machine with unnamed states and region(s) assigned names" + NL + NL);
		
		StateMachineForGeneration smg_3 = new StateMachineForGeneration("noNames_2", statm_3);
		CodeGeneratorConfigurations config_3 = new CodeGeneratorConfigurations();
		config_3.getStatemachines().add(smg_3);
		
		Updater update_3 = new Updater(config_3);
		update_3.updateConfig(config_3);
		
		for (int i=0; i<init.length; i++)
			sb.append("Initial state : " + init[i].getName() + NL);
		
		for (int i=0; i<norm.length; i++)
			sb.append("Normal state : " + norm[i].getName() + NL);
		
		for (int i=0; i<sup.length; i++)
			sb.append("Super state : " + sup[i].getName() + NL);
		
		for (int i=0; i<fin.length; i++)
			sb.append("Final state : " + fin[i].getName() + NL);
		
		for (int i=0; i<reg_3.length; i++)
			sb.append("Region : " + reg_3[i].getName() + NL); 
		
		assertEquals(TestHelpers.loadResource("expected_results/statemachines/expectedAssignedNames/expectedAssignedNames_4.txt"), sb.toString());
	}
}
