package de.upbracing.code_generation.config;

import java.util.LinkedList;
import java.util.List;

public class Mob {

	private String name;
	private List<String> aliases = new LinkedList<String>();
	private int mobId;
	private List<DBCMessageConfig> rxMessages = new LinkedList<DBCMessageConfig>();
	private List<DBCMessageConfig> txMessages = new LinkedList<DBCMessageConfig>();

	private int[] id = null;
	private int[] mask = null;
	private int[] significantDiffs = null;
	private boolean extended = false;
	private boolean disabled = false;
	
	private String on_rx;
	
	public Mob(DBCMessageConfig firstMessage, int mobId, String name, boolean tx) {
		if (firstMessage != null) {
			if (tx)
				txMessages.add(firstMessage);
			else
				rxMessages.add(firstMessage);
		}

		this.mobId = mobId;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void addAlias(String alias) {
		aliases.add(alias);
	}
	
	public List<String> getAliases() {
		return aliases;
	}
	
	public int getMobId() {
		return mobId;
	}

	public List<DBCMessageConfig> getRxMessages() {
		return rxMessages;
	}
	
	public List<DBCMessageConfig> getTxMessages() {
		return txMessages;
	}
	
	public int[] getID() {
		if (id == null)
			calculateValues();
		return id;
	}
	
	public int[] getMask() {
		if (mask == null)
			calculateValues();
		return mask;
	}
	
	public int[] getSignificantDiffs() {
		if (significantDiffs == null)
			calculateValues();
		return significantDiffs;
	}
	
	public boolean isExtended() {
		if (id == null)
			calculateValues();
		return extended;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	
	/** Return significant bits in CANIDTn
	 * @param extended is it an extended message/MOb?
	 * @return an array of bit masks (mask for CANIDT1 is at
	 *     index 0): a 1 means that this bit is significant
	 */
	public int[] getSignificantBits(boolean extended) {
		if (extended)
			// CANIDT4 has some special bits
			// We include RTRTAG, but not RB{0,1}TAG.
			return new int[] { 0xff, 0xff, 0xff, 0xfc };
		else
			return new int[] { 0xff, 0xe0, 0x00, 0x04 };
	}

	/**
	 * Calculates the values id, mask, significantDiffs and extended.
	 * If one of these values is read for the first time, the method is called internally.
	 * It only needs to be called externally if the messages of this mob have changed since the last access
	 */
	private void calculateValues() {
		int[] zeros = {0xff, 0xff, 0xff, 0xff}; // if any ID has a zero somewhere, so will this value
		int[] ones = {0, 0, 0, 0}; // if any ID has a one  somewhere, so will this value
		int[] mask = {0xff, 0xff, 0xff, 0xff}; // combined mask (zeros win)
		int[] significant = {0, 0, 0, 0};		// combined significant bits (ones win)
		
		boolean allExtended = true, allNotExtended = true;
		
		for(DBCMessageConfig msg : rxMessages) {
			int[] id = msg.canIdForMob();
			int[] significant1;
			int[] mask1 = {0xff, 0xff, 0xff, 0xfc};
			
			significant1 = getSignificantBits(msg.isExtended());
			
			allExtended = allExtended && msg.isExtended();
			allNotExtended = allNotExtended && !msg.isExtended();
			
			significant = bitOr(significant, significant1);
			mask = bitAnd(mask, mask1);
			
			// update ones and zeros using the obvious operators
			// Bits that are not significant (0 in significant1) mustn't change anything, so we
			// make them zero when we collect ones and v.v.
			ones  = bitOr (ones,  bitAnd(id, significant1));
			zeros = bitAnd(zeros, bitOr(id, bitNeg(significant1)));
		}
		// If zeros and ones differ in a significant position (1 in significant), we need
		// to clear those bits in the mask, as a zero in the mask is a "don't care". 
		int[] diffs = bitXor(zeros, ones);	// different bits become 1, equal bits 0
		int[] significantDiffs = bitAnd(diffs, significant);
		mask = bitAnd(mask, bitNeg(significantDiffs));
		
		// The IDEMSK bit cannot be computed like this, as the corresponding bit in
		// CANIDT4 has a different meaning. Therefore we set it here.
		boolean idemsk = allExtended || allNotExtended;
		if (idemsk)
			mask[3] |= 1;
		else
			mask[3] &= ~1;
		
		// As ID we could use either zeros or ones, as they are now equal in all significant bits.
		this.id = ones;
		this.mask = mask;
		this.significantDiffs = significantDiffs;
		this.extended = allExtended;
	}
	
	/** Check whether this MOb would receive messages that
	 * are meant for the other mob, if this MOb had a higher
	 * priority.
	 * NOTE: (a.mayStealMessagesOf(b) && b.mayStealMessagesOf(a)) can be true!
	 * @param mob the other MOb (that we might steal messages from)
	 * @return true, if this MOb might steal messages; false, if that isn't possible
	 */
	public boolean mayStealMessagesOf(Mob mob) {
		for (DBCMessageConfig msg : mob.getRxMessages()) {
			if (this.doesCaptureMessage(msg))
				return true;
		}
		
		return false;
	}
	
	/** Check whether this MOb can receive this message (probably
	 * not intended, but permitted by the mask).
	 * @param msg the message
	 * @return whether the message would pass the filter
	 */
	private boolean doesCaptureMessage(DBCMessageConfig msg) {
		// first check IDE (extended or not)
		int mask[] = getMask();
		if ((mask[3] & 1) != 1) {
			// MOb receives both kinds of messages
			// -> We will definitely have a match here.
		} else {
			if (this.isExtended() != msg.isExtended())
				// IDE bit will prevent a match
				return false;
		}
		
		int msg_id[] = msg.canIdForMob();
		int significant[] = getSignificantBits(msg.isExtended());
		
		// mask bits in ID (non-significant bits become 0)
		msg_id = bitAnd(bitAnd(msg_id, mask), significant);
		
		// do the same thing to the ID of the MOb
		// (probably not necessary)
		int mob_id[] = bitAnd(bitAnd(getID(), mask), significant);
		
		// If they are equal now, we have a match.
		return bitEqual(msg_id, mob_id);
	}

	private int[] bitAnd(int[] a, int[] b) {
		if (a != null && b != null && a.length == b.length) {
			int[] result = new int[a.length];
			for(int i = 0; i<a.length; i++)
				result[i] = a[i] & b[i];
			return result;
		}
		return null;
	}
	
	private int[] bitOr(int[] a, int[] b) {
		if (a != null && b != null && a.length == b.length) {
			int[] result = new int[a.length];
			for(int i = 0; i<a.length; i++)
				result[i] = a[i] | b[i];
			return result;
		}
		return null;
	}
	
	private int[] bitXor(int[] a, int[] b) {
		if (a != null && b != null && a.length == b.length) {
			int[] result = new int[a.length];
			for(int i = 0; i<a.length; i++)
				result[i] = a[i] ^ b[i];
			return result;
		}
		return null;
	}
	
	private int[] bitNeg(int[] a) {
		if (a != null) {
			int[] result = new int[a.length];
			for(int i = 0; i<a.length; i++)
				result[i] = ~a[i];
			return result;
		}
		return null;
	}
	
	private boolean bitEqual(int[] a, int[] b) {
		for(int i = 0; i<a.length; i++)
			if (a[i] != b[i])
				return false;
		return true;
	}

	public String getOnRx() {
		return on_rx;
	}

	public void setOnRx(String on_rx) {
		this.on_rx = on_rx;
	}

	/** change the MOb ID after creation - don't use, unless you know what you are doing! */
	public void updateMobId(int mobId) {
		this.mobId = mobId;
	}
}
