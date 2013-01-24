// Test whether there is more than one final state in the statemachine 
// or in the region. If there is more than more than one final state
// then the collapseFinalStates method of {@link Updater} removes the 
// extra final states and sets the incoming transitions of those final states
// to the surviving final state.
// 
// There could be several levels at which you can test it. For eg:-
//  
// Case 1 : More than one final states in a one level statemachine (states contained 
//   		 in the statemachine.
// Case 2 : More than one final states in a two level statemachine (states contained
//  		 in a region of the Super state contained by the statemachine
// Case 3 : More than one final states in a three level statemachine (states conatined
//   		 in a region of the Super state, which is contained in a region of another
//   		 Super state that is in turn contained in the statemachine
// 

package de.upbracing.code_generation.test.statemachine;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import statemachine.FinalState;
import statemachine.InitialState;
import statemachine.NormalState;
import statemachine.Region;
import statemachine.State;
import statemachine.StateMachine;
import statemachine.SuperState;
import statemachine.Transition;

import de.upbracing.code_generation.JRubyHelpers;
import de.upbracing.code_generation.config.CodeGeneratorConfigurations;
import de.upbracing.code_generation.config.StatemachinesConfigProvider;
import de.upbracing.code_generation.fsm.model.StateMachineForGeneration;
import de.upbracing.code_generation.generators.fsm.Updater;
import de.upbracing.code_generation.test.TestHelpers;


public class TestCollapseFinalStates {
	
	StringBuilder sb = new StringBuilder();
	String NL = System.getProperty("line.separator");
	
	@Test
	public void test() {
		
		/**One level statemachine*/
		StateMachine statm_1 = JRubyHelpers.getStatemachineFactory().createStateMachine();
		
		FinalState[] fin_1 = new FinalState[3];
		NormalState[] norm_1 = new NormalState[2];
		Transition[] trans_1 = new Transition[6];
		
		//create Initial state and add to statemachine
		InitialState init_1 = JRubyHelpers.getStatemachineFactory().createInitialState();
		init_1.setName("Initial_state");
		statm_1.getStates().add(init_1);
		
		//create final state and add to statemachine
		for (int i=0; i<fin_1.length; i++) {
			fin_1[i] = JRubyHelpers.getStatemachineFactory().createFinalState();
			fin_1[i].setName("final_state" + i);
			statm_1.getStates().add(fin_1[i]);
		}
		
		//create normal state and add to statemachine
		for (int i=0; i<norm_1.length; i++) {
			norm_1[i] = JRubyHelpers.getStatemachineFactory().createNormalState();
			norm_1[i].setName("normal_state" + i);
			statm_1.getStates().add(norm_1[i]);
		}
		
		//create transitions
		for (int i=0; i<trans_1.length; i++)
			trans_1[i] = JRubyHelpers.getStatemachineFactory().createTransition();
		
		//set source and destination of transitions
		trans_1[0].setSource(init_1);
		trans_1[0].setDestination(norm_1[0]);
		
		trans_1[1].setSource(norm_1[0]);
		trans_1[1].setDestination(fin_1[0]);
		
		trans_1[2].setSource(norm_1[0]);
		trans_1[2].setDestination(fin_1[1]);
		
		trans_1[3].setSource(norm_1[0]);
		trans_1[3].setDestination(fin_1[2]);
		
		trans_1[4].setSource(norm_1[1]);
		trans_1[4].setDestination(fin_1[1]);
		
		trans_1[5].setSource(norm_1[1]);
		trans_1[5].setDestination(fin_1[2]);
		
		//add transitions to the statemachine
		for (int i=0; i<trans_1.length; i++)
			statm_1.getTransitions().add(trans_1[i]);
		
		StateMachineForGeneration smg_1 = new StateMachineForGeneration("Collapsefinalstates_1", statm_1);
		CodeGeneratorConfigurations config_1 = new CodeGeneratorConfigurations();
		StatemachinesConfigProvider.get(config_1).add(smg_1);
		Updater collapsefinal_1 = new Updater(config_1);
		collapsefinal_1.updateConfig(config_1);
		
		List<FinalState> finlist_1 = new ArrayList<FinalState>();
		
		for (State fin : statm_1.getStates())
			if (fin instanceof FinalState) 
				finlist_1.add((FinalState)fin);
			
		sb.append("Remaining final state and its incoming transitions" + NL + NL);
		for (FinalState finalstate : finlist_1) {
			sb.append("State " + finalstate.getName() + NL);
			for (Transition t : finalstate.getIncomingTransitions())
				sb.append("  Transition -> " + t.getSource().getName() + " --> " + t.getDestination().getName() + NL);
		}
		
		assertEquals(
				TestHelpers
						.loadResource("/expected_results/statemachines/collapsibleFinalStates/collapsibleFinalStates_1.txt"),
				sb.toString());
		
		/** Two level statemachine*/
		
		//statemachine that collapses extra final states contained both in the statemachine
		//and in the super state that is contained by the statemachine
		StateMachine statemachine = JRubyHelpers.getStatemachineFactory().createStateMachine();
		
		NormalState[] norm = new NormalState[3];
		InitialState[] init = new InitialState[2];
		FinalState[] fin = new FinalState[5];
		SuperState[] sup = new SuperState[1];
		Region[] reg = new Region[1];
		
		//create initial state and assign a name
		for(int i=0; i<init.length; i++) {
			init[i] = JRubyHelpers.getStatemachineFactory().createInitialState();
			init[i].setName("Init_"+i);
		}
		
		//create normal state and assign a name
		for(int i=0; i<norm.length; i++) {
			norm[i] = JRubyHelpers.getStatemachineFactory().createNormalState();
			norm[i].setName("Norm_"+i);
		}
		
		//create final state and assign a name
		for(int i=0; i<fin.length; i++) {
			fin[i] = JRubyHelpers.getStatemachineFactory().createFinalState();
			fin[i].setName("Final_"+i);
		}
		
		//create super state and assign a name
		sup[0] = JRubyHelpers.getStatemachineFactory().createSuperState();
		sup[0].setName("Super_state");
		
		//create region and assign a name
		reg[0] = JRubyHelpers.getStatemachineFactory().createRegion();
		reg[0].setName("Region");
		
		//add states to the statemachine
		statemachine.getStates().add(init[0]);
		statemachine.getStates().add(norm[0]);
		statemachine.getStates().add(sup[0]);
		statemachine.getStates().add(fin[0]);
		statemachine.getStates().add(fin[1]);
		
		//add region to the super state
		sup[0].getRegions().add(reg[0]);
		
		//add states to the region
		reg[0].getStates().add(init[1]);
		reg[0].getStates().add(norm[1]);
		reg[0].getStates().add(norm[2]);
		reg[0].getStates().add(fin[2]);
		reg[0].getStates().add(fin[3]);
		reg[0].getStates().add(fin[4]);
		
		Transition[] trans = new Transition[10];
		
		//create transitions
		for(int i=0; i<trans.length; i++)
			trans[i] = JRubyHelpers.getStatemachineFactory().createTransition();
		
		//set source and destination of transitions
		trans[0].setSource(init[0]);
		trans[0].setDestination(norm[0]);
		
		trans[1].setSource(norm[0]);
		trans[1].setDestination(fin[0]);
		
		trans[2].setSource(norm[0]);
		trans[2].setDestination(sup[0]);
		
		trans[3].setSource(sup[0]);
		trans[3].setDestination(fin[1]);
		
		trans[4].setSource(init[1]);
		trans[4].setDestination(norm[1]);
		
		trans[5].setSource(norm[1]);
		trans[5].setDestination(fin[3]);
		
		trans[6].setSource(norm[1]);
		trans[6].setDestination(norm[2]);
		
		trans[7].setSource(norm[2]);
		trans[7].setDestination(fin[3]);
		
		//add transitions to the statemachine
		for (int i=0; i<trans.length; i++)
			statemachine.getTransitions().add(trans[i]);
		
		StateMachineForGeneration smg = new StateMachineForGeneration("Collapsefinalstates", statemachine);
		CodeGeneratorConfigurations config = new CodeGeneratorConfigurations();
		StatemachinesConfigProvider.get(config).add(smg);
		Updater collapsefinal = new Updater(config);
		collapsefinal.updateConfig(config);
		
		List<FinalState> finlist = new ArrayList<FinalState>();
		
		for (State state : smg.getStates()) {
			if (state instanceof FinalState)
				finlist.add((FinalState) state);
		}
		
		for (State state : reg[0].getStates()) {
			if (state instanceof FinalState)
				finlist.add((FinalState) state);
		}
		
		sb.setLength(0);
		
		sb.append("Remaining final state(s) and its(their) incoming transitions" + NL);
		for (FinalState finalstate : finlist) {
			sb.append("Final State : " + finalstate.getName() + NL);
			for (Transition t : finalstate.getIncomingTransitions())
				sb.append("  Transition -> " + t.getSource().getName() + " --> " + t.getDestination().getName() + NL);
		}
		
		assertEquals(TestHelpers
				.loadResource("/expected_results/statemachines/collapsibleFinalStates/collapsibleFinalStates_2.txt"),
				sb.toString());
		
		//Two level statemachine with extra final states in more than one regions
		
		StateMachine statm_3 = JRubyHelpers.getStatemachineFactory().createStateMachine();
		
		InitialState[] init_3 = new InitialState[3];
		FinalState[] fin_3	 = new FinalState[5];
		NormalState[] norm_3 = new NormalState[4];
		Region[] reg_3 = new Region[2];
		Transition[] trans_3 = new Transition[13];
		
		//create initial state and assign name
		for (int i=0; i<init_3.length; i++) {
			init_3[i] = JRubyHelpers.getStatemachineFactory().createInitialState();
			init_3[i].setName("initial_state" + i);
		}
		
		//create final state and assign name
		for (int i=0; i<fin_3.length; i++) {
			fin_3[i] = JRubyHelpers.getStatemachineFactory().createFinalState();
			fin_3[i].setName("final_state" + i);
		}
		
		//create normal state and assign name
		for (int i=0; i<norm_3.length; i++) {
			norm_3[i] = JRubyHelpers.getStatemachineFactory().createNormalState();
			norm_3[i].setName("normal_state" + i);
		}
		
		//create region and assign name
		for (int i=0; i<reg_3.length; i++) {
			reg_3[i] = JRubyHelpers.getStatemachineFactory().createRegion();
			reg_3[i].setName("region" + i);
		}
		
		//create super state and assign name
		SuperState sup_3 = JRubyHelpers.getStatemachineFactory().createSuperState();
		sup_3.setName("super_state");
		
		//add states to statemachine
		statm_3.getStates().add(init_3[0]);
		statm_3.getStates().add(fin_3[0]);
		statm_3.getStates().add(sup_3);
		
		//add regions to super state
		sup_3.getRegions().add(reg_3[0]);
		sup_3.getRegions().add(reg_3[1]);
		
		//add states to first region
		reg_3[0].getStates().add(init_3[1]);
		reg_3[0].getStates().add(norm_3[0]);
		reg_3[0].getStates().add(norm_3[1]);
		reg_3[0].getStates().add(fin_3[0]);
		reg_3[0].getStates().add(fin_3[1]);
		
		//add states to second region
		reg_3[1].getStates().add(init_3[2]);
		reg_3[1].getStates().add(norm_3[2]);
		reg_3[1].getStates().add(norm_3[3]);
		reg_3[1].getStates().add(fin_3[2]);
		reg_3[1].getStates().add(fin_3[3]);
		
		//create transitions
		for (int i=0; i<trans_3.length; i++)
			trans_3[i] = JRubyHelpers.getStatemachineFactory().createTransition();
			
		//set source and destination of transitions
		trans_3[0].setSource(init_3[0]);
		trans_3[0].setDestination(sup_3);
		
		trans_3[1].setSource(sup_3);
		trans_3[1].setDestination(fin_3[0]);
		
		trans_3[2].setSource(init_3[1]);
		trans_3[2].setDestination(norm_3[0]);
		
		trans_3[3].setSource(norm_3[0]);
		trans_3[3].setDestination(fin_3[1]);
		
		trans_3[4].setSource(norm_3[0]);
		trans_3[4].setDestination(norm_3[1]);
		
		trans_3[5].setSource(norm_3[1]);
		trans_3[5].setDestination(fin_3[2]);
		
		trans_3[6].setSource(norm_3[0]);
		trans_3[6].setDestination(fin_3[2]);
		
		trans_3[7].setSource(norm_3[1]);
		trans_3[7].setDestination(fin_3[1]);
		
		trans_3[8].setSource(init_3[2]);
		trans_3[8].setDestination(norm_3[2]);
		
		trans_3[9].setSource(norm_3[2]);
		trans_3[9].setDestination(norm_3[3]);
		
		trans_3[10].setSource(norm_3[2]);
		trans_3[10].setDestination(fin_3[3]);
		
		trans_3[11].setSource(norm_3[2]);
		trans_3[11].setDestination(fin_3[4]);
		
		trans_3[12].setSource(norm_3[3]);
		trans_3[12].setDestination(fin_3[4]);
		
		for (int i=0; i<trans.length; i++)
			statemachine.getTransitions().add(trans[i]);
		
		StateMachineForGeneration smg_3 = new StateMachineForGeneration("Collapsefinalstates_3", statm_3);
		CodeGeneratorConfigurations config_3 = new CodeGeneratorConfigurations();
		StatemachinesConfigProvider.get(config_3).add(smg_3);
		Updater collapsefinal_3 = new Updater(config_3);
		collapsefinal_3.updateConfig(config_3);
		
		List<FinalState> finlist_3 = new ArrayList<FinalState>();
		
		for (State state : smg_3.getStates()) {
			if (state instanceof FinalState)
				finlist_3.add((FinalState) state);
		}
		
		for (int i=0; i<reg_3.length; i++) {
			for (State state : reg_3[i].getStates())
				if (state instanceof FinalState)
					finlist_3.add((FinalState) state);
		}
		
		sb.setLength(0);
		
		sb.append("Remaining final state(s) and its(their) incoming transitions" + NL);
		for (FinalState finalstate : finlist_3) {
			sb.append("Final State : " + finalstate.getName() + NL);
			for (Transition t : finalstate.getIncomingTransitions())
				sb.append("  Transition -> " + t.getSource().getName() + " --> " + t.getDestination().getName() + NL);
		}
		
		assertEquals(TestHelpers
				.loadResource("/expected_results/statemachines/collapsibleFinalStates/collapsibleFinalStates_4.txt"),
				sb.toString());
		
		/** Three level statemachine*/
		
		StateMachine statm_2 = JRubyHelpers.getStatemachineFactory().createStateMachine();
		
		InitialState[] init_2 = new InitialState[3];
		FinalState[] fin_2 = new FinalState[4];
		NormalState[] norm_2 = new NormalState[3];
		SuperState[] sup_2 = new SuperState[2];
		Region[] reg_2 = new Region[2];
		
		//create initial state and assign a name
		for (int i=0; i<init_2.length; i++) {
			init_2[i] = JRubyHelpers.getStatemachineFactory().createInitialState();
			init_2[i].setName("initial_state" + i);
		}
		
		//create final state and assign a name
		for (int i=0; i<fin_2.length; i++) {
			fin_2[i] = JRubyHelpers.getStatemachineFactory().createFinalState();
			fin_2[i].setName("final_state" + i);
		}
		
		//create normal state and assign a name
		for (int i=0; i<norm_2.length; i++) {
			norm_2[i] = JRubyHelpers.getStatemachineFactory().createNormalState();
			norm_2[i].setName("normal_state" + i);
		}
		
		//create super state and assign a name
		for (int i=0; i<sup_2.length; i++) {
			sup_2[i] = JRubyHelpers.getStatemachineFactory().createSuperState();
			sup_2[i].setName("super_state" + i);
		}
		
		//create region and assign a name
		for (int i=0; i<reg_2.length; i++) {
			reg_2[i] = JRubyHelpers.getStatemachineFactory().createRegion();
			reg_2[i].setName("region" + i);
		}
		
		//add states to statemachine
		statm_2.getStates().add(init_2[0]);
		statm_2.getStates().add(sup_2[0]);
		statm_2.getStates().add(fin_2[0]);
		
		//add region to the super state
		sup_2[0].getRegions().add(reg_2[0]);
		
		//add states to the region
		reg_2[0].getStates().add(init_2[1]);
		reg_2[0].getStates().add(sup_2[1]);
		
		//add region to the super state
		sup_2[1].getRegions().add(reg_2[1]);
		
		//add states to the region
		reg_2[1].getStates().add(init_2[2]);
		reg_2[1].getStates().add(norm_2[0]);
		reg_2[1].getStates().add(norm_2[1]);
		reg_2[1].getStates().add(fin_2[1]);
		reg_2[1].getStates().add(fin_2[2]);
		reg_2[1].getStates().add(fin_2[3]);
		
		Transition[] tran_2 = new Transition[15];
		
		//create transitions
		for (int i=0; i<tran_2.length; i++)
			tran_2[i] = JRubyHelpers.getStatemachineFactory().createTransition();
		
		//set source and destination of transitions
		tran_2[0].setSource(init_2[0]);
		tran_2[0].setDestination(sup_2[0]);
		
		tran_2[1].setSource(sup_2[0]);
		tran_2[1].setDestination(fin_2[0]);
		
		tran_2[2].setSource(init_2[1]);
		tran_2[2].setDestination(sup_2[1]);
		
		tran_2[3].setSource(init_2[2]);
		tran_2[3].setDestination(norm_2[0]);
		
		tran_2[4].setSource(norm_2[0]);
		tran_2[4].setDestination(fin_2[1]);
		
		tran_2[5].setSource(norm_2[0]);
		tran_2[5].setDestination(norm_2[1]);
		
		tran_2[6].setSource(norm_2[0]);
		tran_2[6].setDestination(norm_2[2]);
		
		tran_2[7].setSource(norm_2[0]);
		tran_2[7].setDestination(fin_2[2]);
		
		tran_2[8].setSource(norm_2[0]);
		tran_2[8].setDestination(fin_2[3]);
		
		tran_2[9].setSource(norm_2[1]);
		tran_2[9].setDestination(fin_2[2]);
		
		tran_2[10].setSource(norm_2[1]);
		tran_2[10].setDestination(fin_2[3]);
		
		tran_2[11].setSource(norm_2[1]);
		tran_2[11].setDestination(fin_2[1]);
		
		tran_2[12].setSource(norm_2[2]);
		tran_2[12].setDestination(fin_2[1]);
		
		tran_2[13].setSource(norm_2[2]);
		tran_2[13].setDestination(fin_2[2]);
		
		tran_2[14].setSource(norm_2[2]);
		tran_2[14].setDestination(fin_2[3]);
		
		//add transitions to the statemachine
		for (int i=0; i<tran_2.length; i++)
			statm_2.getTransitions().add(tran_2[i]);
		
		StateMachineForGeneration smg_2 = new StateMachineForGeneration("Collapsefinalstates_2", statm_2);
		CodeGeneratorConfigurations config_2 = new CodeGeneratorConfigurations();
		StatemachinesConfigProvider.get(config_2).add(smg_2);
		Updater collapsefinal_2 = new Updater(config_2);
		collapsefinal_2.updateConfig(config_2);
		
		//list to collect the remaining final states
		List<FinalState> finlist_2 = new ArrayList<FinalState>();
		
		//add remaining final states to the list
		for (State state : smg_2.getStates()) 
			if (state instanceof FinalState)
				finlist_2.add((FinalState) state);
		
		for (int i=0; i<reg_2.length; i++) {
			for (State state : reg_2[i].getStates())
				if (state instanceof FinalState)
					finlist_2.add((FinalState) state);
		}
		
		sb.setLength(0);
		
		sb.append("Remaining final state(s) and its(their) incoming transitions" + NL);
		for (FinalState finalstate : finlist_2) {
			sb.append("State " + finalstate.getName() + NL);
			for (Transition t : finalstate.getIncomingTransitions())
				sb.append("  Transition -> " + t.getSource().getName() + " --> " + t.getDestination().getName() + NL);
		}
		
		assertEquals(TestHelpers
				.loadResource("/expected_results/statemachines/collapsibleFinalStates/collapsibleFinalStates_3.txt"),
				sb.toString());
	}

}
