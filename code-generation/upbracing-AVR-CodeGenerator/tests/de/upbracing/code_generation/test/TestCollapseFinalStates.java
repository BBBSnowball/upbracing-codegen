package de.upbracing.code_generation.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.upbracing.code_generation.JRubyHelpers;
import de.upbracing.code_generation.config.MCUConfiguration;
import de.upbracing.code_generation.fsm.model.StateMachineForGeneration;
import de.upbracing.code_generation.generators.fsm.Updater;

import Statecharts.FinalState;
import Statecharts.InitialState;
import Statecharts.NormalState;
import Statecharts.Region;
import Statecharts.State;
import Statecharts.StateMachine;
import Statecharts.SuperState;
import Statecharts.Transition;

public class TestCollapseFinalStates {

	@Test
	public void test() {
		StateMachine statemachine = JRubyHelpers.getStatemachineFactory().createStateMachine();
		
		NormalState[] norm = new NormalState[3];
		InitialState[] init = new InitialState[2];
		FinalState[] fin = new FinalState[5];
		SuperState[] sup = new SuperState[1];
		Region[] reg = new Region[1];
		
		for(int i=0; i<init.length; i++) {
			init[i] = JRubyHelpers.getStatemachineFactory().createInitialState();
			init[i].setName("Init_"+i);
		}
		
		for(int i=0; i<norm.length; i++) {
			norm[i] = JRubyHelpers.getStatemachineFactory().createNormalState();
			norm[i].setName("Norm_"+i);
		}
		
		for(int i=0; i<fin.length; i++) {
			fin[i] = JRubyHelpers.getStatemachineFactory().createFinalState();
			fin[i].setName("Final_"+i);
		}
		
		sup[0] = JRubyHelpers.getStatemachineFactory().createSuperState();
		sup[0].setName("Super_state");
		reg[0] = JRubyHelpers.getStatemachineFactory().createRegion();
		reg[0].setName("Region");
		
		statemachine.getStates().add(init[0]);
		statemachine.getStates().add(norm[0]);
		statemachine.getStates().add(sup[0]);
		statemachine.getStates().add(fin[0]);
		statemachine.getStates().add(fin[1]);
		
		sup[0].getRegions().add(reg[0]);
		reg[0].getStates().add(init[1]);
		reg[0].getStates().add(norm[1]);
		reg[0].getStates().add(norm[2]);
		reg[0].getStates().add(fin[2]);
		reg[0].getStates().add(fin[3]);
		reg[0].getStates().add(fin[4]);
		
		Transition[] trans = new Transition[10];
		
		for(int i=0; i<trans.length; i++)
			trans[i] = JRubyHelpers.getStatemachineFactory().createTransition();
		
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
		
		for (int i=0; i<trans.length; i++)
			statemachine.getTransitions().add(trans[i]);
		
		StateMachineForGeneration smg = new StateMachineForGeneration("Collapsefinalstates", statemachine);
		MCUConfiguration config = new MCUConfiguration();
		config.getStatemachines().add(smg);
		Updater collapsefinal = new Updater(config);
		collapsefinal.updateConfig(config);
		
		List<FinalState> finlist = new ArrayList<FinalState>();
		
		for(State state : smg.getStates()) {
			if (state instanceof FinalState)
				finlist.add((FinalState) state);
			
			if (state instanceof SuperState) {
				for (Region region : ((SuperState) state).getRegions()) {
					for (State st : region.getStates()) {
						if (st instanceof FinalState)
							finlist.add((FinalState) st);
					}
				}
			}
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("List of final states\n");
		for (FinalState finalstate : finlist) {
			sb.append("State " + finalstate.getName() + "\n");
			for (Transition t : finalstate.getIncomingTransitions())
				sb.append("  Transition -> " + t.getSource().getName() + " --> " + t.getDestination().getName() + "\n");
		}
		
		assertEquals("List of final states\n"
				+ "State Final_2\n"
				+ "  Transition -> Norm_1 --> Final_2\n"
				+ "  Transition -> Norm_2 --> Final_2\n"
				+ "State Final_0\n"
				+ "  Transition -> Norm_0 --> Final_0\n"
				+ "  Transition -> Super_state --> Final_0\n",
				sb.toString());
	}

}
