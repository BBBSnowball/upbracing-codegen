package de.upbracing.code_generation.fsm.model;

public class TransitionInfo {
	private EventName eventName;
	private String condition, action, waitType;
	private double waitTime;

	public TransitionInfo(EventName eventName, String condition, String action,
			String waitType, double waitTime) {
		super();
		this.eventName = eventName;
		this.condition = condition;
		this.action = action;
		this.waitType = waitType;
		this.waitTime = waitTime;
	}

	public TransitionInfo(EventName eventName, String condition, String action) {
		this(eventName, condition, action, null, Double.NaN);
	}

	public TransitionInfo(String eventName, String condition, String action) {
		this(new NormalEventName(eventName), condition, action);
	}

	public TransitionInfo(String eventName, String condition, String action,
			String waitType, double waitTime) {
		this(new NormalEventName(eventName), condition, action, waitType, waitTime);
	}

	public EventName getEventName() {
		return eventName;
	}

	public String getCondition() {
		return condition;
	}

	public String getAction() {
		return action;
	}
	
	public String getWaitType() {
		return waitType;
	}
	
	public double getWaitTime() {
		return waitTime;
	}
	
	public boolean isWaitTransition() {
		return waitType != null && !Double.isNaN(waitTime);
	}

	
	public void setEventName(EventName eventName) {
		this.eventName = eventName;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setWait(String waitType, double waitTime) {
		this.waitType = waitType;
		this.waitTime = waitTime;
	}
	
	public void removeWait() {
		setWait(null, Double.NaN);
	}
	

	@Override
	public String toString() {
		return "TransitionInfo [eventName=" + eventName + ", condition="
				+ condition + ", action=" + action + ", waitType=" + waitType
				+ ", waitTime=" + waitTime + "]";
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
		result = prime * result
				+ ((waitType == null) ? 0 : waitType.hashCode());
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
		if (waitType == null) {
			if (other.waitType != null)
				return false;
		} else if (!waitType.equals(other.waitType))
			return false;
		return true;
	}
	
	public static abstract class EventName {
		protected String name;
		
		public EventName(String name) {
			this.name = name;
		}
		
		public abstract String getEventName();
		
		@Override
		public String toString() {
			return getEventName();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = this.getClass().hashCode();
			result = prime * result + ((name == null) ? 0 : name.hashCode());
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
			EventName other = (EventName) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}
	}
	
	public static class NormalEventName extends EventName {
		public NormalEventName(String name) {
			super(name);
		}

		public String getEventName() {
			return name;
		}
	}
	
	public static class ISREventName extends EventName {
		public ISREventName(String name) {
			super(name);
		}

		public String getEventName() {
			return "ISR_" + name;
		}

		public String getInterruptName() {
			return name;
		}
	}
}
