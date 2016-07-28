/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.timer;

/**
 * 
 */
public class MyPrescaledIntervalTimer extends AbstractPrescaledIntervalTimer {

	/**
	 * the expireCount
	 */
	private int expireCount;

	/**
	 * Constructor
	 * @param microInterval the initial micro interval
	 * @param macroInterval the initial macro interval
	 */
	public MyPrescaledIntervalTimer(int microInterval, int macroInterval) {
		super(microInterval, macroInterval);
		expireCount = 0;
	}

	/**
	 * @return Returns the expireCount.
	 */
	public int getExpireCount() {
		return expireCount;
	}

	/**
	 * Sets the expireCount.
	 * @param expireCount the new value to set
	 */
	public void setExpireCount(int expireCount) {
		this.expireCount = expireCount;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.timer.AbstractIntervalTimer#onExpire()
	 */
	@Override
	protected void onExpire() {
		expireCount++;
	}

}
