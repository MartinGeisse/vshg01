/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.gltetris.game;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * TODO: document me
 *
 */
public class PieceTest {

	/**
	 * @param piece
	 */
	private void testHelper(Piece piece) {
		assertSame(piece, piece.getInitialShape().getPiece());
	}
	
	/**
	 * @throws Exception on errors
	 */
	@Test
	public void testAll() throws Exception {
		Piece.initialize();
		Shape.initialize();
		for (Piece piece : Piece.values()) {
			testHelper(piece);
		}
	}
}
