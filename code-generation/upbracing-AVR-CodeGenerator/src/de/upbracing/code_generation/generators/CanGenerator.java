package de.upbracing.code_generation.generators;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.List;
import java.util.Set;

import de.upbracing.code_generation.CanHeaderTemplate;
import de.upbracing.code_generation.CanCFileTemplate;
import de.upbracing.code_generation.CanValueTablesTemplate;
import de.upbracing.code_generation.Messages;
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
		if (config.getCan() == null)
			return true;
		
		if (config.getCurrentEcu() == null) {
			System.err.println("ERROR: Ecu not set.");
			return false;
		}
		
		if (after_update_config) {
			DBCEcuConfig dbcEcu = (DBCEcuConfig)config.getCan().getEcu(config.getCurrentEcu().getNodeName());

			if (dbcEcu == null) //If node name fails, try normal name
				dbcEcu = (DBCEcuConfig)config.getCan().getEcu(config.getCurrentEcu().getName());
			
			if (dbcEcu == null) {
				System.err.println("ERROR: Could not find the ecu.");
				return false;
			}

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
		ArrayList<Mob> mob_natural_order = new ArrayList<Mob>(15);
		
		int mobNumber = 0;
		
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
				mob_natural_order.add(mob);
				
				mobNumber++;
			}
		}
		
		for (Mob mob : mob_natural_order) {
			if ((mob.getMask()[3] & 1) != 1) {
				// From my experiments:
				// - If you don't set the IDE bit, the MCU will only use 11 bits in the
				//   comparison, even if an extended message is received. This means that
				//   the MOb will "steal" a lot of messages...
				// - If you do set the IDE bit, all the bits are compared (at least that's
				//   my guess). Well, I noticed that a standard message cannot be received.
				// - If I use a mask that allows arbitrary values in those positions
				//   (CANIDT3, CANIDT4 and part of CANIDT2), the MOb "eats" messages, but
				//   it doesn't receive them - very weird.
				// - The CANIDTn registers are updated with the values from the message, but
				//   that doesn't change anything for the following message because the
				//   changed bits are masked, anyway. However, we must correct that for the
				//   IDE bit, if we need it a certain way.
				// - The errata say nothing about that (neither does the datasheet) :-(
				// - It seems like that should work - why would we have a IDEMSK bit, if that
				//   wasn't possible?!
				// => We don't allow it. Period.
				config.getMessages().error("MOb '%s' has standard and extended messages. The "
						+ "AT90CAN doesn't handle that well. Most likely, it won't work and "
						+ "break some of the other MObs!",
						mob.getName());
			}
		}
		
		orderMobs(config.getMessages(), mob_natural_order, 0);
		
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
		
		// AT90CAN supports 15 MOBs, but we need one as MOB_GENERAL_MESSAGE_TRANSMITTER
		if (mobNumber > 15-1) {
			config.getMessages().error("Too many message objects: We need %d, but we only have 14 (without MOB_GENERAL_MESSAGE_TRANSMITTER). Please consider sharing a MOb for some messages.", mobNumber);
		}
		
		// add user MObs
		int user_mob_id = 0;
		for (;mobNumber < 14;mobNumber++) {
			String mobName = "USER" + user_mob_id;
			if (mobs.containsKey(mobName))
				config.getMessages().error("MOb name clashes with user MOb: %s", mobName);
			else {
				Mob mob = new Mob(null, mobNumber, mobName, true);
				mobs.put(mobName, mob);
				dbcEcu.addMob(mob);
				
				String rx_handler = config.getCan().getUserMobRxHandlers().get(user_mob_id);
				if (rx_handler != null)
					mob.setOnRx(rx_handler);
			}
			
			++user_mob_id;
		}
		
		// make sure that we don't use any MOB for rx AND tx
		for (Mob mob : mobs.values()) {
			if (!mob.getRxMessages().isEmpty() && !mob.getTxMessages().isEmpty())
				config.getMessages().error("MOb '%s' is used for receiving and transmitting, which is not allowed", mob.getName());
		}
		
		// Build lists of global variables to resolve name clashes
		HashMap<String, LinkedList<DBCSignalConfig>> rx_signals_with_gvars = new HashMap<String, LinkedList<DBCSignalConfig>>();
		for (DBCSignal sig : dbcEcu.getRxSignals()) {
			DBCSignalConfig signal = (DBCSignalConfig)sig;
			if (signal.isNoGlobalVar())
				continue;
			
			String name = signal.getGlobalVarName();
			
			if (!rx_signals_with_gvars.containsKey(name))
				rx_signals_with_gvars.put(name, new LinkedList<DBCSignalConfig>());
			
			rx_signals_with_gvars.get(name).add(signal);
		}
		
		// Same thing for transmitted signals
		HashMap<String, LinkedList<DBCSignalConfig>> tx_signals_with_gvars = new HashMap<String, LinkedList<DBCSignalConfig>>();
		for (DBCMessage msg : dbcEcu.getTxMsgs()) {
			DBCMessageConfig msgconfig = (DBCMessageConfig) msg;
			
			// We only need global variables to read data from, if we
			// sent it out automatically.
			if (msgconfig.isNoSendMessage() || !msgconfig.isPeriodic())
				continue;

			for(DBCSignal sig : msg.getSignals().values()) { 
				DBCSignalConfig signal = (DBCSignalConfig) sig;
				
				if (signal.isNoGlobalVar())
					continue;
				
				String name = signal.getGlobalVarName();
				
				if (!tx_signals_with_gvars.containsKey(name))
					tx_signals_with_gvars.put(name, new LinkedList<DBCSignalConfig>());
				
				tx_signals_with_gvars.get(name).add(signal);
			}
		}
		
		// Create the global variables
		addGlobalVariables(config, rx_signals_with_gvars, tx_signals_with_gvars, "rx");
		addGlobalVariables(config, tx_signals_with_gvars, rx_signals_with_gvars, "tx");
				
		//Create OS tasks for messages with periodic sending
		for (DBCMessage msg : dbcEcu.getTxMsgs()) {
			DBCMessageConfig msgconfig = (DBCMessageConfig) msg;
			
			if (msgconfig.isNoSendMessage()) continue;

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

	/** Shared MObs can match more messages than they should, so they can steal
	 * messages that should go into other MObs. This message tries to change the
	 * MOb priorities to avoid that problem (first MOb wins). The existing order
	 * will be preserved, if possible. The method will return false, if it cannot
	 * find a working order. An error will be reported in that case. If the method
	 * returns true, the parameter mobs will contain the new order; if it returns
	 * false, the value of that list is undefined.
	 * @param messages messages object to report warnings and errors
	 * @param mobs a list of MObs to re-order - it will be changed
	 * @param first_mob_id the MObs will get IDs starting with this one (0 to 15)
	 * @return true, if successful; false, if impossible
	 */
	private boolean orderMobs(Messages messages, ArrayList<Mob> mobs, int first_mob_id) {
		HashMap<Mob, List<Mob>> steals_from = new HashMap<Mob, List<Mob>>();
		for (Mob mob : mobs)
			steals_from.put(mob, new LinkedList<Mob>());
		for (Mob mob1 : mobs)
			for (Mob mob2 : mobs)
				if (mob1 != mob2 && mob1.mayStealMessagesOf(mob2))
					steals_from.get(mob1).add(mob2);

		Set<Mob> todo = new HashSet<Mob>(mobs);
		ArrayList<Mob> result = new ArrayList<Mob>();
		// Put each of them into the result list one by one.
		while (todo.size() > 0) {
			// Find a MOb that doesn't have any conflicts. We prefer the
			// MObs near the start of the list to preserve its order.
			boolean found_one = false;
			boolean in_order = true;
			for (Mob mob : mobs) {
				// ignore MObs that we already put in there
				if (!todo.contains(mob))
					continue;
				
				// does it have any conflicts left?
				boolean has_conflict = false;
				for (Mob conflict : steals_from.get(mob)) {
					if (todo.contains(conflict)) {
						has_conflict = true;
						break;
					}
				}
				if (has_conflict) {
					// not ready, yet
					in_order = false;
					continue;
				}
				
				// we can use that one
				todo.remove(mob);
				result.add(mob);
				found_one = true;
				
				// report
				if (!in_order) {
					StringBuffer sb = new StringBuffer();
					LinkedList<Mob> stealing = new LinkedList<Mob>();
					for (Mob mob2 : mobs)
						if (todo.contains(mob2) && steals_from.get(mob2).contains(mob))
							stealing.add(mob2);
					if (!stealing.isEmpty()) {
						appendMobList(sb, stealing);
						messages.info("MOb '%s' gets a higher priority, so it won't be shadowed by other MObs: %s",
									mob.getName(), sb.toString());
					}
				}
				
				// we abort the loop here and continue checking the
				// front of the list again (we prefer those, remember?)
				break;
			}
			
			// avoid an endless loop, if we don't have any options left
			if (!found_one) {
				StringBuffer sb = new StringBuffer();
				sb.append("I cannot figure out an order for your shared MObs. I'm stuck with those:");
				for (Mob mob : mobs) {
					if (!todo.contains(mob))
						continue;
					
					sb.append("\n  - '" + mob.getName() + "' would steal from ");

					List<Mob> conflicting_mobs = steals_from.get(mob);
					appendMobList(sb, conflicting_mobs);
				}
				
				messages.error("%s", sb.toString());
				return false;
			}
		}
		
		// We have a valid order
		// -> almost done :-)
		
		// put the new order into the provided list
		//NOTE This won't change anything because the source file will use the order in dbcEcu.getMobs(). However,
		//     this is quite good because that way we can easily see which MObs got another ID.
		mobs.clear();
		mobs.addAll(result);
		
		// assign new MOb IDs
		int mob_id = first_mob_id;
		for (Mob mob : result) {
			//if (mob.getMobId() != mob_id)
			//	messages.info("MOb '%s' gets another ID (%d -> %d), so it won't be shadowed by or shadow another MOb.",
			//			mob.getName(), mob.getMobId(), mob_id);
			mob.updateMobId(mob_id);
			++mob_id;
		}
		
		return true;
	}

	private static void appendMobList(StringBuffer sb, List<Mob> mobs) {
		boolean first = true;
		for (Mob mob : mobs) {
			if (first)
				first = false;
			else
				sb.append(", ");
			
			sb.append("'" + mob.getName() + "'");
		}
	}

	private void addGlobalVariables(MCUConfiguration config,
			HashMap<String, LinkedList<DBCSignalConfig>> variables_to_add,
			HashMap<String, LinkedList<DBCSignalConfig>> conflicting_variables,
			String direction_prefix) {
		for (LinkedList<DBCSignalConfig> signals : variables_to_add.values()) {
			boolean use_msg_prefix = signals.size() > 1;
			
			for (DBCSignalConfig signal : signals) {
				String name = signal.getGlobalVarName();
				
				// We only change the name, if the user hasn't set it specifically.
				boolean fixed_name = signal.hasGlobalVarName();
				if (!fixed_name) {
					boolean use_direction_prefix = conflicting_variables.containsKey(name) && conflicting_variables.get(name).contains(signal);
					boolean use_msg_prefix2 = use_msg_prefix || conflicting_variables.containsKey(name) && !conflicting_variables.get(name).isEmpty();
					
					if (use_direction_prefix) {
						// We cannot use two names - both directions have to use the same variable.
						//name = direction_prefix + "_" + name;
						config.getMessages().warn("Signal '%s' in message '%s' will use the same variable for transmitting and receiving. You can "
								+ "set the name explicitely to get rid of this warning.",
								signal.getName(), signal.getMessage().getName());
						
						// Avoid a warning for the opposite direction
						conflicting_variables.get(name).remove(signal);
					}
					
					if (use_msg_prefix2)
						name = signal.getMessage().getName() + "_"  + name;
					
					signal.setGlobalVarName(name);
				} else {
					// The user must avoid name clashes herself. We don't even issue
					// a warning because they may be intentional.
				}
				
				if (!config.getGlobalVariables().containsKey(name))
					config.getGlobalVariables().add(name, signal.getCType());
				else {
					if (!fixed_name) {
						// Append a suffix to change the name and make it unique
						int suffix = 1;
						while(config.getGlobalVariables().containsKey(name + "_" + suffix)) {
							suffix++;
						}
						
						// set the changed name as a new custom global variable name
						signal.setGlobalVarName(name + "_" + suffix);
						
						// Report the problem
						config.getMessages().warn("The global variable '%s' (for signal '%s' in message '%s') cannot be created because a variable with "
								+ "the same name exists. It will be renamed to '%s', but don't expect that name to be stable - it can change every time the "
								+ "code generator runs. Please set the variable name in your configuration or remove the offending variable.",
								name, signal.getName(), signal.getMessage().getName(), signal.getGlobalVarName());
						
						config.getGlobalVariables().add(signal.getGlobalVarName(), signal.getCType());
					} else {
						config.getMessages().info("Not creating global variable '%s' (for signal '%s' in message '%s') because it exists and the name has been set by the user",
								name, signal.getName(), signal.getMessage().getName());
					}
				}
			}
		}
	}
}
