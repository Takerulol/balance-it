package de.hsbremen.mobile.balanceit.logic;

/**
 * Enum contains the available player roles.
 */
public enum PlayerRole {
	/**
	 * The Balancer tries to balance the ball on the board.
	 */
	Balancer,
	
	/**
	 * The ForceApplier applies a force to the ball in order to knock it off the board.
	 */
	ForceApplier,
	
	/**
	 * A match against a computer controlled opponent (balancing only).
	 */
	SinglePlayer,
}
