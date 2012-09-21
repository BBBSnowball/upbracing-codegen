package de.upbracing.code_generation.generators;

import java.util.HashMap;
import java.util.Map;

import de.upbracing.code_generation.CanTemplate;
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
		super(GlobalVariableGenerator.class, "can.h", new CanTemplate());
	}
	
	@Override
	public boolean validate(MCUConfiguration config, boolean after_update_config, Object generator_data) {
		if (!after_update_config) {
			//Test C Types
				
			
		} else {
			//Check MOBs
			
			//Check if combined RX TX mob exists (not allowed)
		
		}
		
		return true;
	}
	
	@Override
	public Object updateConfig(MCUConfiguration config) {
		if (config.getCan() == null)
			return null;
		
		DBCEcuConfig dbcEcu = (DBCEcuConfig)config.getCanConfig().getEcu(config.getCurrentEcu().getName());
		
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
		for (DBCMessage msg : dbcEcu.getTxMsgs()) {
			DBCMessageConfig msgconfig = (DBCMessageConfig) msg;
			
			if (msgconfig.isNoSendMessage()) continue;

			for(Map.Entry<String, DBCSignal> entry : msg.getSignals().entrySet()) { 
				DBCSignalConfig signal = (DBCSignalConfig) entry.getValue();
				
				if (signal.isNoGlobalVar()) continue;
				
				addGlobalVariable(config, signal);
			}
		}
		
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
