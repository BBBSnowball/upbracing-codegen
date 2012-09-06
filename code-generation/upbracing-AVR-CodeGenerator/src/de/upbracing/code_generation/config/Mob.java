package de.upbracing.code_generation.config;

import java.util.LinkedList;
import java.util.List;

public class Mob {

	private String name;
	private List<DBCMessageConfig> messages;
	private int[] id = null;
	private int[] mask = null;
	private int[] significantDiffs = null;
	private boolean extended = false;
	private boolean tx = false;
	
	public Mob(DBCMessageConfig firstMessage, boolean tx) {
		messages = new LinkedList<DBCMessageConfig>();
		messages.add(firstMessage);
		this.setTx(tx);
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
	
	public boolean isTx() {
		return tx;
	}

	public void setTx(boolean tx) {
		this.tx = tx;
	}

	/**
	 * Calculates the values id, mask, significantDiffs and extended.
	 * If one of these values is read for the first time, the method is called internally.
	 * It only needs to be called externally if the messages of this mob have changed since the last access
	 */
	public void calculateValues() {
		int[] zeros = {0xff, 0xff, 0xff, 0xff}; // if any ID has a zero somewhere, so will this value
		int[] ones = {0, 0, 0, 0}; // if any ID has a one  somewhere, so will this value
		int[] mask = {0xff, 0xff, 0xff, 0xff}; // combined mask (zeros win)
		int[] significant = {0, 0, 0, 0};		// combined significant bits (ones win)
		
		boolean allExtended = true, allNotExtended = true;
		
		for(DBCMessageConfig msg : messages) {
			int[] id = msg.canIdForMob();
			int[] significant1;
			int[] mask1 = {0xff, 0xff, 0xff, 0xfd};
			
			if (msg.isExtended()) significant1 = new int[]{0xff, 0xff, 0xff, 0xfd};
			else significant1 = new int[]{0xff, 0xe0, 0x00, 0x04};
			
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
			mask[4] &= ~1;
		
		// As ID we could use either zeros or ones, as they are now equal in all significant bits.
		this.id = ones;
		this.mask = mask;
		this.significantDiffs = significantDiffs;
		this.extended = allExtended;
		
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
}
