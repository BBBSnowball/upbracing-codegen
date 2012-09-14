package de.upbracing.code_generation.generators;

import java.util.Map;

import de.upbracing.code_generation.CanTemplate;
import de.upbracing.code_generation.config.DBCEcuConfig;
import de.upbracing.code_generation.config.DBCMessageConfig;
import de.upbracing.code_generation.config.DBCSignalConfig;
import de.upbracing.code_generation.config.GlobalVariableConfig;
import de.upbracing.code_generation.config.MCUConfiguration;
import de.upbracing.dbc.DBCMessage;
import de.upbracing.dbc.DBCSignal;


/**
 * Generator for the CAN Communication
 * 
 * @author sven
 */
public class CanGenerator extends AbstractGenerator {
	public CanGenerator() {
		super("can.h", new CanTemplate());
	}
	
	@Override
	public Object updateConfig(MCUConfiguration config) {
				
		DBCEcuConfig dbcEcu = (DBCEcuConfig)config.getCanConfig().getEcu(config.getCurrentEcu().getName());
		
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
