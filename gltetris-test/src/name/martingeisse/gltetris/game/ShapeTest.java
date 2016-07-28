/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.gltetris.game;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * TODO: document me
 *
 */
public class ShapeTest {

	private void rotationTestHelper(Shape shape, int shapesForPiece, boolean clockwise) {
		boolean initialShapeFound = false;
		int rotations = 0;
		Shape tempShape = shape;
		do {
			initialShapeFound |= (tempShape == shape.getPiece().getInitialShape());
			assertSame(tempShape, tempShape.getNextShapeClockwise().getNextShapeCounterclockwise());
			assertSame(tempShape, tempShape.getNextShapeCounterclockwise().getNextShapeClockwise());
			tempShape = (clockwise ? tempShape.getNextShapeClockwise() : tempShape.getNextShapeCounterclockwise());
			rotations++;
		} while (tempShape != shape);
		assertEquals(shapesForPiece, rotations);
		assertTrue(initialShapeFound);
	}
	
	/**
	 * @param shape
	 * @param shapesForPiece
	 */
	private void testHelper(Shape shape, int shapesForPiece) {
		rotationTestHelper(shape, shapesForPiece, true);
		rotationTestHelper(shape, shapesForPiece, false);
	}
	
	/**
	 * @throws Exception on errors
	 */
	@Test
	public void testAll() throws Exception {
		Piece.initialize();
		Shape.initialize();
		testHelper(Shape.O_0, 1);
		testHelper(Shape.I_0, 2);
		testHelper(Shape.I_1, 2);
		testHelper(Shape.T_0, 4);
		testHelper(Shape.T_1, 4);
		testHelper(Shape.T_2, 4);
		testHelper(Shape.T_3, 4);
		testHelper(Shape.L_0, 4);
		testHelper(Shape.L_1, 4);
		testHelper(Shape.L_2, 4);
		testHelper(Shape.L_3, 4);
		testHelper(Shape.J_0, 4);
		testHelper(Shape.J_1, 4);
		testHelper(Shape.J_2, 4);
		testHelper(Shape.J_3, 4);
		testHelper(Shape.S_0, 2);
		testHelper(Shape.S_1, 2);
		testHelper(Shape.Z_0, 2);
		testHelper(Shape.Z_1, 2);
	}
	
}
