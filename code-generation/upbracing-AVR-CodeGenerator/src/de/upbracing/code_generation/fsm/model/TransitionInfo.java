package de.upbracing.code_generation.fsm.model;

public class TransitionInfo {
	private String eventName, condition, action;
	private double waitTime;

	public TransitionInfo(String eventName, String condition, String action) {
		super();
		this.eventName = eventName;
		this.waitTime = Double.NaN;
		this.condition = condition;
		this.action = action;
	}

	public TransitionInfo(String eventName, double waitTime, String condition, String action) {
		super();
		this.eventName = eventName;
		this.waitTime = waitTime;
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
	
	public double getWaitTime() {
		return waitTime;
	}
	
	public boolean isWaitTransition() {
		return !Double.isNaN(waitTime);
	}

	@Override
	public String toString() {
		return "TransitionInfo [eventName=" + eventName + ", condition="
				+ condition + ", action=" + action + ", waitTime=" + waitTime
				+ "]";
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
		long temp;
		temp = Double.doubleToLongBits(waitTime);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		if (Double.doubleToLongBits(waitTime) != Double
				.doubleToLongBits(other.waitTime))
			return false;
		return true;
	}
}
