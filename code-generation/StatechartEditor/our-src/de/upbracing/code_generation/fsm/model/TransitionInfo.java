package de.upbracing.code_generation.fsm.model;

public class TransitionInfo {
	private String eventName, condition, action;

	public TransitionInfo(String eventName, String condition, String action) {
		super();
		this.eventName = eventName;
		this.condition = condition;
		this.action = action;
	}

	public String getEventName() {
		return eventName;
	}

	public String getCondition() {
		return condition;
	}

	public String getAction() {
		return action;
	}

	@Override
	public String toString() {
		return "TransitionInfo [eventName=" + eventName + ", condition="
				+ condition + ", action=" + action + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result
				+ ((condition == null) ? 0 : condition.hashCode());
		result = prime * result
				+ ((eventName == null) ? 0 : eventName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TransitionInfo other = (TransitionInfo) obj;
		if (action == null) {
			if (other.action != null)
				return false;
		} else if (!action.equals(other.action))
			return false;
		if (condition == null) {
			if (other.condition != null)
				return false;
		} else if (!condition.equals(other.condition))
			return false;
		if (eventName == null) {
			if (other.eventName != null)
				return false;
		} else if (!eventName.equals(other.eventName))
			return false;
		return true;
	}
}
