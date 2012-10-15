package de.upbracing.code_generation.generators;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.List;

import de.upbracing.code_generation.CanHeaderTemplate;
import de.upbracing.code_generation.CanCFileTemplate;
import de.upbracing.code_generation.CanValueTablesTemplate;
import de.upbracing.code_generation.config.DBCEcuConfig;
import de.upbracing.code_generation.config.DBCMessageConfig;
import de.upbracing.code_generation.config.DBCSignalConfig;
import de.upbracing.code_generation.config.MCUConfiguration;
import de.upbracing.code_generation.config.Mob;
import de.upbracing.dbc.DBCMessage;
import de.upbracing.dbc.DBCSignal;


/**
 * Generator for the CAN Communication
 * 
 * @author sven
 */
public class CanGenerator extends AbstractGenerator {
	public CanGenerator() {
		super(GlobalVariableGenerator.class, "can.h", new CanHeaderTemplate(), 
											 "can.c", new CanCFileTemplate(), 
											 "can_valuetables.h", new CanValueTablesTemplate());
	}
	
	@Override
	public boolean validate(MCUConfiguration config, boolean after_update_config, Object generator_data) {
		if (after_update_config) {
			DBCEcuConfig dbcEcu = (DBCEcuConfig)config.getCan().getEcu(config.getCurrentEcu().getNodeName());

			//Check if combined RX TX mob exists (not allowed)
			for(Mob mob : dbcEcu.getMobs()) {
				if (mob.getRxMessages().size() > 0 && mob.getTxMessages().size() > 0) {
					System.err.println("ERROR: Found MOB with Rx and Tx messages which is not allowed.");
					return false;
				}
			}		
			
			//Check if there are periodic tasks with signals without global variable
			for(List<DBCMessageConfig> list : dbcEcu.getSendingTasks()) {
				for (DBCMessageConfig msg : list) {
					
					boolean signalWithoutVar = false;
					
					for (DBCSignal signal : msg.getSignalOrder()) {
						DBCSignalConfig signalconfig = (DBCSignalConfig)signal;
						if (signalconfig.isNoGlobalVar() && signalconfig.getReadValueTask() == null) {
							//Signal has no global variable and no read value code replacement
							signalWithoutVar = true;
						}
					}
					
					if(signalWithoutVar && (msg.getTaskAll() == null)) {
						System.err.println("ERROR: CAN message " + msg.getName() + 
								" set to periodic sending, but at least one signal doesn't have a global variable or" + 
								" readValue code replacement");
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	@Override
	public Object updateConfig(MCUConfiguration config) {
		if (config.getCan() == null)
			return null;
		
		DBCEcuConfig dbcEcu = (DBCEcuConfig)config.getCan().getEcu(config.getCurrentEcu().getNodeName());

		//Sort messages and signals by raw id
		java.util.Collections.sort((List<DBCMessage>)dbcEcu.getRxMsgs(), new Comparator<DBCMessage>() {
			@Override
			public int compare( DBCMessage a, DBCMessage b ) {
				return a.getRawId().compareTo(b.getRawId());
			}
		});
		java.util.Collections.sort((List<DBCSignal>)dbcEcu.getRxSignals(), new Comparator<DBCSignal>() {
			@Override
			public int compare( DBCSignal a, DBCSignal b ) {
				int result = a.getMessage().getRawId().compareTo(b.getMessage().getRawId());
				if (result == 0) {
					if(a.getStart() < b.getStart()) result = -1;
					if(a.getStart() > b.getStart()) result = 1;
				}
				return result;
			}
		});
		
		//Create MOBs		
		Map<String, Mob> mobs = new HashMap<String, Mob>();
		
		int mobNumber = 1;
		
		// RX Messages
		for (DBCMessage msg : dbcEcu.getRxMsgs()) {
			DBCMessageConfig msgconfig = (DBCMessageConfig) msg;
			
			String mobName = null;
			
			if (msgconfig.getRxMob() != null) {
				mobName = msgconfig.getRxMob();
			} else {
				if (dbcEcu.getTxMsgs().contains(msg))
					mobName = "rx" + msgconfig.getName(); //add prefix if it is also a tx message
				else
					mobName = msgconfig.getName();
				
				msgconfig.setRxMob(mobName);
			}
			
			if (mobs.containsKey(mobName)) {
				mobs.get(mobName).getRxMessages().add(msgconfig);
				if (msgconfig.isMobDisabled()) mobs.get(mobName).setDisabled(true);
			} else {
				Mob mob = new Mob(msgconfig, mobNumber, mobName, false);
				if (msgconfig.getAliases().size() > 0) {
					for(String alias : msgconfig.getAliases()) {
						if (dbcEcu.getTxMsgs().contains(msg))
							mob.addAlias("rx" + alias);
						else
							mob.addAlias(alias);
					}
				}
				if (msgconfig.isMobDisabled()) mob.setDisabled(true);
				mobs.put(mobName, mob);
				dbcEcu.addMob(mob);
				
				mobNumber++;
			}
		}
		
		//TX Messages
		for (DBCMessage msg : dbcEcu.getTxMsgs()) {
			DBCMessageConfig msgconfig = (DBCMessageConfig) msg;
			if (!msgconfig.isUsingGeneralTransmitter()) {
				
				String mobName = null;
				
				if (msgconfig.getTxMob() != null) {
					mobName = msgconfig.getTxMob();
				} else {
					if (dbcEcu.getRxMsgs().contains(msg))
						mobName = "tx" + msgconfig.getName(); //add prefix if it is also an rx message
					else
						mobName = msgconfig.getName();
					
					msgconfig.setTxMob(mobName);
				}
				
				if (mobs.containsKey(mobName)) {
					mobs.get(mobName).getTxMessages().add(msgconfig);
					if (msgconfig.isMobDisabled()) mobs.get(mobName).setDisabled(true);
				} else {
					Mob mob = new Mob(msgconfig, mobNumber, mobName, true);
					if (msgconfig.getAliases().size() > 0) {
						for(String alias : msgconfig.getAliases()) {
							if (dbcEcu.getRxMsgs().contains(msg))
								mob.addAlias("tx" + alias);
							else
								mob.addAlias(alias);
						}
					}
					if (msgconfig.isMobDisabled()) mob.setDisabled(true);
					mobs.put(mobName, mob);
					dbcEcu.addMob(mob);

					mobNumber++;
				}
			}
		}
		
		//Create a global variable for all RX signals
		for (DBCSignal sig : dbcEcu.getRxSignals()) {
			DBCSignalConfig signal = (DBCSignalConfig)sig;
			if (signal.isNoGlobalVar()) continue;
			
			addGlobalVariable(config, signal);
		}
		
		//Create a global variable for all signals in all TX messages
		//Also create OS tasks for messages with periodic sending
		for (DBCMessage msg : dbcEcu.getTxMsgs()) {
			DBCMessageConfig msgconfig = (DBCMessageConfig) msg;
			
			if (msgconfig.isNoSendMessage()) continue;

			//Create global variables
			for(Map.Entry<String, DBCSignal> entry : msg.getSignals().entrySet()) { 
				DBCSignalConfig signal = (DBCSignalConfig) entry.getValue();
				
				if (signal.isNoGlobalVar()) continue;

				addGlobalVariable(config, signal);
			}
			
			//Create OS task for periodic sending
			if (msgconfig.isPeriodic()) {		
				//Check if another message already had the same period
				boolean foundMessage = false;
				for(List<DBCMessageConfig> list : dbcEcu.getSendingTasks()) {
					if (list.get(0).getPeriod() == msgconfig.getPeriod()) {
						//Found a message with the same period, we simply add this message to the list
						list.add(msgconfig);
						foundMessage = true;
					}
				}
				
				if (!foundMessage) {
					//Create new task
					int ticksPerBase = (int)(msgconfig.getPeriod() * ((double)config.getRtos().getTickFrequency()));
					config.getRtos().addTask(msgconfig.getName(), ticksPerBase);

					//Add this task in a new list to the task list
					List<DBCMessageConfig> list = new LinkedList<DBCMessageConfig>();
					list.add(msgconfig);
					dbcEcu.getSendingTasks().add(list);
				}
				
			}
			
			
		}
		
		//Add declaration for value tables to global variables
		config.getGlobalVariables().addDeclaration("#include \"can_valuetables.h\"");
		
		
		return null;
	}
	
	private void addGlobalVariable(MCUConfiguration config, DBCSignalConfig signal) {
		
		//Check if global variable with this name already exists
		if (config.getGlobalVariables().containsKey(signal.getGlobalVarName())) {
			
			//Append a suffix to change the name
			int suffix = 1;
			while(config.getGlobalVariables().containsKey(signal.getGlobalVarName() + "_" + suffix)) {
				suffix++;
			}
			
			//set the changed name as a new custom global variable name
			signal.setGlobalVarName(signal.getGlobalVarName() + "_" + suffix);
		} 
		
		config.getGlobalVariables().add(signal.getGlobalVarName(), signal.getCType());

	}
}
