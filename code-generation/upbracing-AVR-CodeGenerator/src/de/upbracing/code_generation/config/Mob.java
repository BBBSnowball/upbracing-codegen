package de.upbracing.code_generation.config;

import java.util.LinkedList;
import java.util.List;

public class Mob {

	private String name;
	private List<DBCMessageConfig> messages;
	
	public Mob(DBCMessageConfig firstMessage) {
		messages = new LinkedList<DBCMessageConfig>();
		messages.add(firstMessage);
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public List<DBCMessageConfig> getMessages() {
		return messages;
	}
	
	
	
	
	
}
