/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator;

import static org.junit.Assert.assertEquals;
import name.martingeisse.ecotools.simulator.timer.ITickable;

import org.junit.Test;

/**
 * 
 */
public class SimulatorUtilsTest {

	/**
	 * 
	 */
	@Test
	public void testMultiTick() {
		MyTickable t = new MyTickable();
		assertEquals(0, t.ticks);
		t.tick();
		assertEquals(1, t.ticks);
		SimulatorUtils.multiTick(t, 5);
		assertEquals(6, t.ticks);
		SimulatorUtils.multiTick(t, -1);
		assertEquals(6, t.ticks);
	}
	
	/**
	 * 
	 */
	private class MyTickable implements ITickable {
		
		/**
		 * the ticks
		 */
		private int ticks = 0;

		/* (non-Javadoc)
		 * @see name.martingeisse.ecotools.simulator.timer.ITickable#tick()
		 */
		@Override
		public void tick() {
			ticks++;
		}
		
		
	}
	
}
