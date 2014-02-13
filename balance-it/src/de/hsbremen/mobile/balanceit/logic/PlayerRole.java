package de.hsbremen.mobile.balanceit.logic;

import java.util.Hashtable;

/**
 * Enum contains the available player roles.
 */
public enum PlayerRole {
	/**
	 * The Balancer tries to balance the ball on the board.
	 */
	Balancer((byte)1),
	
	/**
	 * The ForceApplier applies a force to the ball in order to knock it off the board.
	 */
	ForceApplier((byte)2),
	
	/**
	 * A match against a computer controlled opponent (balancing only).
	 */
	SinglePlayer((byte)3);
	
private byte value;
	
	private PlayerRole(byte value)
	{
		this.value = value;
	}
	
	public byte getByteValue()
	{
		return value;
	}
	
	//the following  is used for opposite mapping
	//i.e. map byte value to enum value
	
	private static final Hashtable<Byte, PlayerRole> byteToPlayerRole = new Hashtable<Byte, PlayerRole>();
	
	static 
	{
		for (PlayerRole role : PlayerRole.values())
		{
			byteToPlayerRole.put(role.value, role);
		}
		
	}
	
	
	public static PlayerRole fromValue(Byte value)
	{
		return byteToPlayerRole.get(value);
	}
}
