/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.common.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * 
 */
public class IntPredicatesTest {

	/**
	 * the predicate
	 */
	private IIntPredicate predicate;

	/**
	 * @param value the value to check
	 * @param expectedResult the expected predicate result
	 */
	private void assertResult(int value, boolean expectedResult) {
		assertEquals(expectedResult, predicate.evaluate(value));
	}

	/**
	 * 
	 */
	@Test
	public void testEqual() {
		predicate = new IntPredicates.Equal(5);
		assertResult(3, false);
		assertResult(5, true);
		assertResult(7, false);
	}

	/**
	 * 
	 */
	@Test
	public void testNotEqual() {
		predicate = new IntPredicates.NotEqual(5);
		assertResult(3, true);
		assertResult(5, false);
		assertResult(7, true);
	}

	/**
	 * 
	 */
	@Test
	public void testLess() {
		predicate = new IntPredicates.Less(5);
		assertResult(3, true);
		assertResult(5, false);
		assertResult(7, false);
	}

	/**
	 * 
	 */
	@Test
	public void testLessEqual() {
		predicate = new IntPredicates.LessEqual(5);
		assertResult(3, true);
		assertResult(5, true);
		assertResult(7, false);
	}

	/**
	 * 
	 */
	@Test
	public void testGreater() {
		predicate = new IntPredicates.Greater(5);
		assertResult(3, false);
		assertResult(5, false);
		assertResult(7, true);
	}

	/**
	 * 
	 */
	@Test
	public void testGreaterEqual() {
		predicate = new IntPredicates.GreaterEqual(5);
		assertResult(3, false);
		assertResult(5, true);
		assertResult(7, true);
	}

}
