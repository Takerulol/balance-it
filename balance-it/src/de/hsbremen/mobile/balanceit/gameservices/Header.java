package de.hsbremen.mobile.balanceit.gameservices;

import java.util.Hashtable;

/**
 * Possible network header.
 */
public enum Header
{
	/**
	 * Payload:
	 * Vector3 force vector.
	 */
	FORCE_VECTOR((byte)0),
	
	/**
	 * Payload:
	 * Matrix4 sphere transform.
	 */
	SPHERE_MATRIX((byte)1),
	
	/**
	 * Payload:
	 * Matrix4 rotation matrix.
	 */
	GROUND_ROTATION((byte)2),
	
	/**
	 * Payload: see SendPhysicsProxy
	 */
	WORLD_UPDATE((byte)3);
	
	private byte value;
	
	private Header(byte value)
	{
		this.value = value;
	}
	
	public byte getByteValue()
	{
		return value;
	}
	
	//the following  is used for opposite mapping
	//i.e. map byte value to enum value
	
	private static final Hashtable<Byte, Header> byteToHeader = new Hashtable<Byte, Header>();
	
	static 
	{
		for (Header header : Header.values())
		{
			byteToHeader.put(header.value, header);
		}
		
	}
	
	
	public static Header fromValue(Byte value)
	{
		return byteToHeader.get(value);
	}
	
}
