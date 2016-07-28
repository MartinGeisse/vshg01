/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.common.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

/**
 * 
 */
public class IntArrayTraversalTest {

	/**
	 * the traversal
	 */
	private IntArrayTraversal traversal = new IntArrayTraversal(new int[] {
			0, 1, 6, 10, 3, 5, 5, 5, 5, 6, -1, 0
	});

	/**
	 * 
	 */
	@Test
	public void testConstructorsGettersSetters() {
		int[] array = new int[7];
		assertEquals(10, new IntArrayTraversal(10).getArray().length);
		assertEquals(0, new IntArrayTraversal(10).getCursor());
		assertEquals(7, new IntArrayTraversal(array).getArray().length);
		assertEquals(0, new IntArrayTraversal(array).getCursor());

		IntArrayTraversal localTraversal = new IntArrayTraversal(10);
		assertEquals(0, localTraversal.getCursor());
		assertTrue(localTraversal.isAtBeginning());
		assertFalse(localTraversal.isAtEnd());

		localTraversal.setCursor(5);
		assertEquals(5, localTraversal.getCursor());
		assertFalse(localTraversal.isAtBeginning());
		assertFalse(localTraversal.isAtEnd());

		localTraversal.setCursorToBeginning();
		assertEquals(0, localTraversal.getCursor());
		assertTrue(localTraversal.isAtBeginning());
		assertFalse(localTraversal.isAtEnd());

		localTraversal.setCursorToEnd();
		assertEquals(10, localTraversal.getCursor());
		assertFalse(localTraversal.isAtBeginning());
		assertTrue(localTraversal.isAtEnd());

		localTraversal.setCursor(0);
		assertEquals(0, localTraversal.getCursor());
		assertTrue(localTraversal.isAtBeginning());
		assertFalse(localTraversal.isAtEnd());

		localTraversal.setCursor(10);
		assertEquals(10, localTraversal.getCursor());
		assertFalse(localTraversal.isAtBeginning());
		assertTrue(localTraversal.isAtEnd());

		IntArrayTraversal emptyArrayTraversal = new IntArrayTraversal(0);
		assertEquals(0, emptyArrayTraversal.getCursor());
		assertTrue(emptyArrayTraversal.isAtBeginning());
		assertTrue(emptyArrayTraversal.isAtEnd());

	}

	/**
	 * 
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetCursorNegative() {
		traversal.setCursor(-1);
	}

	/**
	 * 
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetCursorBeyondEnd() {
		traversal.setCursor(13);
	}

	/**
	 * 
	 */
	@Test
	public void testRead() {
		assertEquals(0, traversal.read());
		assertEquals(1, traversal.read());
		assertEquals(6, traversal.read());
		assertEquals(10, traversal.read());
		traversal.setCursor(1);
		assertEquals(1, traversal.read());
		assertEquals(6, traversal.read());
		assertEquals(10, traversal.read());
	}

	/**
	 * 
	 */
	@Test(expected = IllegalStateException.class)
	public void testReadAtEnd() {
		traversal.setCursor(12);
		traversal.read();
	}

	/**
	 * 
	 */
	@Test
	public void testReadDontMove() {
		assertEquals(0, traversal.readDontMove());
		assertEquals(0, traversal.readDontMove());
		assertEquals(0, traversal.readDontMove());
		traversal.setCursor(2);
		assertEquals(6, traversal.readDontMove());
		assertEquals(6, traversal.readDontMove());
		assertEquals(6, traversal.readDontMove());
	}

	/**
	 * 
	 */
	@Test(expected = IllegalStateException.class)
	public void testReadDontMoveAtEnd() {
		traversal.setCursor(12);
		traversal.readDontMove();
	}

	/**
	 * 
	 */
	@Test
	public void testReadBackwards() {
		traversal.setCursor(4);
		assertEquals(10, traversal.readBackwards());
		assertEquals(6, traversal.readBackwards());
		assertEquals(1, traversal.readBackwards());
		assertEquals(0, traversal.readBackwards());
	}

	/**
	 * 
	 */
	@Test(expected = IllegalStateException.class)
	public void testReadBackwardsAtBeginning() {
		traversal.readBackwards();
	}

	/**
	 * 
	 */
	@Test
	public void testReadBackwardsDontMove() {
		traversal.setCursor(2);
		assertEquals(1, traversal.readBackwardsDontMove());
		assertEquals(1, traversal.readBackwardsDontMove());
		assertEquals(1, traversal.readBackwardsDontMove());
	}

	/**
	 * 
	 */
	@Test(expected = IllegalStateException.class)
	public void testReadBackwardsDontMoveAtBeginning() {
		traversal.readBackwardsDontMove();
	}

	/**
	 * 
	 */
	@Test
	public void testWrite() {
		traversal.write(11);
		traversal.write(22);
		traversal.write(33);
		traversal.setCursorToBeginning();
		assertEquals(11, traversal.read());
		assertEquals(22, traversal.read());
		assertEquals(33, traversal.read());
		assertEquals(10, traversal.read());
		traversal.setCursor(1);
		assertEquals(22, traversal.read());
		assertEquals(33, traversal.read());
		assertEquals(10, traversal.read());
	}

	/**
	 * 
	 */
	@Test(expected = IllegalStateException.class)
	public void testWriteAtEnd() {
		traversal.setCursor(12);
		traversal.write(0);
	}

	/**
	 * 
	 */
	@Test
	public void testWriteDontMove() {
		traversal.setCursor(2);
		traversal.writeDontMove(11);
		traversal.writeDontMove(22);
		traversal.writeDontMove(33);
		traversal.setCursorToBeginning();
		assertEquals(0, traversal.read());
		assertEquals(1, traversal.read());
		assertEquals(33, traversal.read());
		assertEquals(10, traversal.read());
		traversal.setCursor(1);
		assertEquals(1, traversal.read());
		assertEquals(33, traversal.read());
		assertEquals(10, traversal.read());
	}

	/**
	 * 
	 */
	@Test(expected = IllegalStateException.class)
	public void testWriteDontMoveAtEnd() {
		traversal.setCursor(12);
		traversal.writeDontMove(0);
	}

	/**
	 * 
	 */
	@Test
	public void testWriteBackwards() {
		traversal.setCursor(3);
		traversal.writeBackwards(11);
		traversal.writeBackwards(22);
		traversal.setCursorToBeginning();
		assertEquals(0, traversal.read());
		assertEquals(22, traversal.read());
		assertEquals(11, traversal.read());
		assertEquals(10, traversal.read());
	}


	/**
	 * 
	 */
	@Test(expected = IllegalStateException.class)
	public void testWriteBackwardsAtBeginning() {
		traversal.writeBackwards(0);
	}


	/**
	 * 
	 */
	@Test
	public void testWriteBackwardsDontMove() {
		traversal.setCursor(3);
		traversal.writeBackwardsDontMove(11);
		traversal.writeBackwardsDontMove(22);
		traversal.setCursorToBeginning();
		assertEquals(0, traversal.read());
		assertEquals(1, traversal.read());
		assertEquals(22, traversal.read());
		assertEquals(10, traversal.read());
	}


	/**
	 * 
	 */
	@Test(expected = IllegalStateException.class)
	public void testWriteBackwardsDontMoveAtBeginning() {
		traversal.writeBackwardsDontMove(0);
	}
	
	/**
	 * 
	 */
	@Test
	public void testSkip() {
		IIntPredicate predicate = new IntPredicates.Equal(5);
		
		traversal.setCursor(3);
		traversal.skip(predicate);
		assertEquals(3, traversal.getCursor());

		traversal.setCursor(5);
		traversal.skip(predicate);
		assertEquals(9, traversal.getCursor());

		traversal.setCursor(7);
		traversal.skip(predicate);
		assertEquals(9, traversal.getCursor());

		traversal.setCursor(9);
		traversal.skip(predicate);
		assertEquals(9, traversal.getCursor());

		traversal.setCursor(12);
		traversal.skip(predicate);
		assertEquals(12, traversal.getCursor());

	}

	/**
	 * 
	 */
	@Test
	public void testBackward() {
		IIntPredicate predicate = new IntPredicates.Equal(5);

		traversal.setCursor(0);
		traversal.skipBackward(predicate);
		assertEquals(0, traversal.getCursor());

		traversal.setCursor(3);
		traversal.skipBackward(predicate);
		assertEquals(3, traversal.getCursor());

		traversal.setCursor(5);
		traversal.skipBackward(predicate);
		assertEquals(5, traversal.getCursor());

		traversal.setCursor(7);
		traversal.skipBackward(predicate);
		assertEquals(5, traversal.getCursor());

		traversal.setCursor(9);
		traversal.skipBackward(predicate);
		assertEquals(5, traversal.getCursor());

	}

	/**
	 * 
	 */
	@Test
	public void testGetSubArray() {
	
		traversal.setCursor(0);
		assertTrue(Arrays.equals(new int[] {}, traversal.getSubArrayBeforeCursor()));
		assertTrue(Arrays.equals(new int[] {0, 1, 6, 10, 3, 5, 5, 5, 5, 6, -1, 0}, traversal.getSubArrayAfterCursor()));

		traversal.setCursor(5);
		assertTrue(Arrays.equals(new int[] {0, 1, 6, 10, 3}, traversal.getSubArrayBeforeCursor()));
		assertTrue(Arrays.equals(new int[] {5, 5, 5, 5, 6, -1, 0}, traversal.getSubArrayAfterCursor()));

		traversal.setCursor(12);
		assertTrue(Arrays.equals(new int[] {0, 1, 6, 10, 3, 5, 5, 5, 5, 6, -1, 0}, traversal.getSubArrayBeforeCursor()));
		assertTrue(Arrays.equals(new int[] {}, traversal.getSubArrayAfterCursor()));

	}
}
