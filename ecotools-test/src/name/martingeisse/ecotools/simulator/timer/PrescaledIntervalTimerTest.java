/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.timer;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 */
public class PrescaledIntervalTimerTest {

	/**
	 * the timer
	 */
	private MyPrescaledIntervalTimer timer;
	
	/**
	 * 
	 */
	@Before
	public void setUp() {
		timer = new MyPrescaledIntervalTimer(3, 2);
	}
	
	/**
	 * 
	 */
	@After
	public void tearDown() {
	}
	
	/**
	 * 
	 */
	@Test
	public void testGetterSetter() {
		assertEquals(3, timer.getMicroInterval());
		assertEquals(3, timer.getMicroTicksLeft());
		assertEquals(2, timer.getMacroInterval());
		assertEquals(2, timer.getMacroTicksLeft());
		assertEquals(0, timer.getExpireCount());
		
		timer.setMicroInterval(50);
		assertEquals(50, timer.getMicroInterval());
		assertEquals(3, timer.getMicroTicksLeft());
		assertEquals(2, timer.getMacroInterval());
		assertEquals(2, timer.getMacroTicksLeft());
		assertEquals(0, timer.getExpireCount());

		timer.setMicroTicksLeft(300);
		assertEquals(50, timer.getMicroInterval());
		assertEquals(300, timer.getMicroTicksLeft());
		assertEquals(2, timer.getMacroInterval());
		assertEquals(2, timer.getMacroTicksLeft());
		assertEquals(0, timer.getExpireCount());

		timer.setMacroInterval(500);
		assertEquals(50, timer.getMicroInterval());
		assertEquals(300, timer.getMicroTicksLeft());
		assertEquals(500, timer.getMacroInterval());
		assertEquals(2, timer.getMacroTicksLeft());
		assertEquals(0, timer.getExpireCount());

		timer.setMacroTicksLeft(77);
		assertEquals(50, timer.getMicroInterval());
		assertEquals(300, timer.getMicroTicksLeft());
		assertEquals(500, timer.getMacroInterval());
		assertEquals(77, timer.getMacroTicksLeft());
		assertEquals(0, timer.getExpireCount());

		timer.setExpireCount(99);
		assertEquals(50, timer.getMicroInterval());
		assertEquals(300, timer.getMicroTicksLeft());
		assertEquals(500, timer.getMacroInterval());
		assertEquals(77, timer.getMacroTicksLeft());
		assertEquals(99, timer.getExpireCount());

	}

	/**
	 * 
	 */
	@Test
	public void testTick() {

		timer.tick();
		assertEquals(3, timer.getMicroInterval());
		assertEquals(2, timer.getMicroTicksLeft());
		assertEquals(2, timer.getMacroInterval());
		assertEquals(2, timer.getMacroTicksLeft());
		assertEquals(0, timer.getExpireCount());

		timer.tick();
		assertEquals(3, timer.getMicroInterval());
		assertEquals(1, timer.getMicroTicksLeft());
		assertEquals(2, timer.getMacroInterval());
		assertEquals(2, timer.getMacroTicksLeft());
		assertEquals(0, timer.getExpireCount());

		timer.tick();
		assertEquals(3, timer.getMicroInterval());
		assertEquals(3, timer.getMicroTicksLeft());
		assertEquals(2, timer.getMacroInterval());
		assertEquals(1, timer.getMacroTicksLeft());
		assertEquals(0, timer.getExpireCount());

		timer.tick();
		assertEquals(3, timer.getMicroInterval());
		assertEquals(2, timer.getMicroTicksLeft());
		assertEquals(2, timer.getMacroInterval());
		assertEquals(1, timer.getMacroTicksLeft());
		assertEquals(0, timer.getExpireCount());

		timer.tick();
		assertEquals(3, timer.getMicroInterval());
		assertEquals(1, timer.getMicroTicksLeft());
		assertEquals(2, timer.getMacroInterval());
		assertEquals(1, timer.getMacroTicksLeft());
		assertEquals(0, timer.getExpireCount());

		timer.tick();
		assertEquals(3, timer.getMicroInterval());
		assertEquals(3, timer.getMicroTicksLeft());
		assertEquals(2, timer.getMacroInterval());
		assertEquals(2, timer.getMacroTicksLeft());
		assertEquals(1, timer.getExpireCount());
		
	}
	
}
