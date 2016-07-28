/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.timer;

/**
 * 
 */
public class MyIntervalTimer extends AbstractIntervalTimer {

	/**
	 * the expireCount
	 */
	private int expireCount;

	/**
	 * Constructor
	 * @param interval the initial interval
	 */
	public MyIntervalTimer(int interval) {
		super(interval);
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
