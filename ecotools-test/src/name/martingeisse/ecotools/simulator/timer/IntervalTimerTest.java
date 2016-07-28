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
public class IntervalTimerTest {

	/**
	 * the timer
	 */
	private MyIntervalTimer timer;
	
	/**
	 * 
	 */
	@Before
	public void setUp() {
		timer = new MyIntervalTimer(3);
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
		assertEquals(3, timer.getInterval());
		assertEquals(3, timer.getTicksLeft());
		assertEquals(0, timer.getExpireCount());
		
		timer.setInterval(50);
		assertEquals(50, timer.getInterval());
		assertEquals(3, timer.getTicksLeft());
		assertEquals(0, timer.getExpireCount());

		timer.setTicksLeft(300);
		assertEquals(50, timer.getInterval());
		assertEquals(300, timer.getTicksLeft());
		assertEquals(0, timer.getExpireCount());

		timer.setExpireCount(99);
		assertEquals(50, timer.getInterval());
		assertEquals(300, timer.getTicksLeft());
		assertEquals(99, timer.getExpireCount());

	}

	/**
	 * 
	 */
	@Test
	public void testTick() {
		
		timer.tick();
		assertEquals(3, timer.getInterval());
		assertEquals(2, timer.getTicksLeft());
		assertEquals(0, timer.getExpireCount());
		
		timer.tick();
		assertEquals(3, timer.getInterval());
		assertEquals(1, timer.getTicksLeft());
		assertEquals(0, timer.getExpireCount());
		
		timer.tick();
		assertEquals(3, timer.getInterval());
		assertEquals(3, timer.getTicksLeft());
		assertEquals(1, timer.getExpireCount());
		
		timer.tick();
		assertEquals(3, timer.getInterval());
		assertEquals(2, timer.getTicksLeft());
		assertEquals(1, timer.getExpireCount());
		
	}
	
	/**
	 * 
	 */
	@Test
	public void setIntervalDuringOperation() {

		timer.setInterval(2);
		assertEquals(2, timer.getInterval());
		assertEquals(3, timer.getTicksLeft());
		assertEquals(0, timer.getExpireCount());

		timer.tick();
		assertEquals(2, timer.getInterval());
		assertEquals(2, timer.getTicksLeft());
		assertEquals(0, timer.getExpireCount());
		
		timer.tick();
		assertEquals(2, timer.getInterval());
		assertEquals(1, timer.getTicksLeft());
		assertEquals(0, timer.getExpireCount());

		timer.tick();
		assertEquals(2, timer.getInterval());
		assertEquals(2, timer.getTicksLeft());
		assertEquals(1, timer.getExpireCount());
		
		timer.setInterval(9);
		assertEquals(9, timer.getInterval());
		assertEquals(2, timer.getTicksLeft());
		assertEquals(1, timer.getExpireCount());

		timer.tick();
		assertEquals(9, timer.getInterval());
		assertEquals(1, timer.getTicksLeft());
		assertEquals(1, timer.getExpireCount());
		
		timer.tick();
		assertEquals(9, timer.getInterval());
		assertEquals(9, timer.getTicksLeft());
		assertEquals(2, timer.getExpireCount());

	}

}
