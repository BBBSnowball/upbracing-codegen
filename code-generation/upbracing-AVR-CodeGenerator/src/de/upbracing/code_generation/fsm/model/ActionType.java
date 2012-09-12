package de.upbracing.code_generation.fsm.model;

import Statecharts.State;
import Statecharts.Transition;

public enum ActionType {
	EXIT, EXIT_NOT_TO_SELF, ALWAYS, DURING, ENTER_NOT_FROM_SELF, ENTER;
	
	public boolean shouldExecuteFor(Transition trans, State self) {
		if (trans != null)
			return shouldExecuteFor(trans.getSource(), trans.getDestination(), self);
		else
			return shouldExecuteFor(null, null, self);
	}
	
	public boolean shouldExecuteFor(State source, State destination, State self) {
		switch (this) {
		case ENTER:
			return destination == self;
		case EXIT:
			return source == self;
		case ALWAYS:
			// will execute if the state is active or becomes active
			return source == null && destination == null || source != self && destination == self;
		case DURING:
			// will execute if the state is active at the start of this time step 
			return source == null && destination == null;
		case ENTER_NOT_FROM_SELF:
			return source != self && destination == self;
		case EXIT_NOT_TO_SELF:
			return source == self && destination != self;
		default:
			throw new IllegalStateException("unexpected enum constant: " + this);
		}
	}
}
