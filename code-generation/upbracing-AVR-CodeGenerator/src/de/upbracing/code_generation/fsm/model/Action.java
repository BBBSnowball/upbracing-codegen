package de.upbracing.code_generation.fsm.model;

public class Action {
	private ActionType type;
	private String action;
	
	public Action(ActionType type, String action) {
		this.type = type;
		this.action = action;
	}
	
	public ActionType getType() {
		return type;
	}
	public String getAction() {
		return action;
	}

	@Override
	public String toString() {
		return "Action [type=" + type + ", action=" + action + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Action other = (Action) obj;
		if (action == null) {
			if (other.action != null)
				return false;
		} else if (!action.equals(other.action))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
}
