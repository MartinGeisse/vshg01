/*
 *  gamestate.h
 *  Blocks-in-a-Row
 *
 *  Created by Martin Geisse on 30.11.07.
 *  Copyright 2007 __MyCompanyName__. All rights reserved.
 *
 */

typedef struct
{
	// state of the game area (block character values, with 0 being 'free')
	unsigned char gameArea [10 * 20];

	/* Preview pieces. The piece index is stored in the lowest 8 bits (7 to 0)
	 * and the color character in the next higher 8 bits (15 to 8).
	 */
	int preview0, preview1, preview2;

	// completed rows (this also indicates the game level by rows/10).
	int rows;

	// the player's score
	int score;

	/* Position of the currently moving shape. These values may be negative and/or exceed 9 since they only
	 * indicate the position of the upper left corner of the shape's 4x4 box, not of the shape itself.
	 * Also, when entering the game area, the actual shape also crosses the y=0 border.
	 */
	int shapeX, shapeY;

	// shape index (0-6) of the current shape
	int shapeIndex;

	// drawing character of the current shape
	char shapeCharacter;

} GAMESTATE;

// construct a new game state
void initializeGameState (GAMESTATE *state);

// clear the game area of a game state
void clearGameArea (GAMESTATE *state);

/* This function moves the preview pieces by one position; fills the now-free
 * slot with the supplied piece, and returns the shifted-out piece.
 * The piece index is stored in the lowest 8 bits (7 to 0)
 * and the color character in the next higher 8 bits (15 to 8).
 */
int shiftPreview (GAMESTATE *state, int shiftIn);

/* Generate a random piece.
 * The piece index is stored in the lowest 8 bits (7 to 0)
 * and the color character in the next higher 8 bits (15 to 8).
 */
int randomPiece (void);

/* Shift in a random piece, and return the shifted-out piece.
 * The piece index is stored in the lowest 8 bits (7 to 0)
 * and the color character in the next higher 8 bits (15 to 8), for
 * both the shifted-in and shifted-out pieces.
 */
int shiftPreviewRandom (GAMESTATE *state);

/* Return the awarded score for completing the specified number of rows at once
 * while playing at the specified level.
 */
int getRowScore (int level, int num);

/* Update the row count and score for completed rows. Returns a nonzero value iff
 * a new level is reached, 0 otherwise.
 */
int addRows (GAMESTATE *state, int num);

/* Initialize the specified shape to enter the game area.
 */
void enterShape (GAMESTATE *state, int shapeIndex, char shapeCharacter);

/* Shift a random piece into the preview; move all preview pieces; and initialize
 * the normal shape for the shifted out piece to enter the game area.
 */
void nextPiece (GAMESTATE *state);

/* Check if the specified shape index and position specify a valid (non-blocked)
 * position for a shape. Blocking by the currently moving shape is not considered.
 * Returns nonzero if valid, zero if blocked.
 */
int unblockedShapePosition (GAMESTATE *state, int x, int y, int shape);

/* Move the current shape downwards. Returns nonzero iff successful. Returns zero
 * iff blocked, i.e. the shape has landed.
 */
int moveCurrentShapeDown (GAMESTATE *state);

/* Move the current shape to the left. Returns nonzero iff successful, zero iff blocked.
 */
int moveCurrentShapeLeft (GAMESTATE *state);

/* Move the current shape to the right. Returns nonzero iff successful, zero iff blocked.
 */
int moveCurrentShapeRight (GAMESTATE *state);

/* Rotate the current shape clockwise. Returns nonzero iff successful, zero iff blocked.
 */
int rotateCurrentShapeClockwise (GAMESTATE *state);

/* Rotate the current shape counter-clockwise. Returns nonzero iff successful, zero iff blocked.
 */
int rotateCurrentShapeCounterClockwise (GAMESTATE *state);

/* Paste the specified shape at its position into the game area. Returns nonzero if the
 * shape crosses the game area borders, 0 if the shape is fully inside the game area.
 */
int pasteShape (GAMESTATE *state, int x, int y, int shapeIndex, char shapeCharacter);

/* Paste the current shape at its position into the game area. Returns nonzero if the
 * shape crosses the game area borders, 0 if the shape is fully inside the game area.
 */
int pasteCurrentShape (GAMESTATE *state);

/* Test for the specified row if it is completed, i.e. filled with nonzero values.
 * Returns nonzero for completed, 0 is incomplete.
 */
int isRowCompleted (GAMESTATE *state, int rowIndex);

/* Copy block data from row 'source' to row 'dest'
 */
void copyRow (GAMESTATE *state, int source, int dest);

/* Clear a row in the game area.
 */
void clearRow (GAMESTATE *state, int y);

/* Find completed rows in the game area, starting at row number firstRowIndex and
 * testing maxRowCount rows (to index firstRowIndex+maxRowCount-1). A maximum of
 * maxRowCount rows will therefore be detected.
 *
 * The row indices are stored in the rowIndices array and their count is returned. The
 * rowIndices must be at least of size maxRowCount.
 */
int findCompletedRows (GAMESTATE *state, int firstRowIndex, int maxRowCount, int *rowIndices);

/* Remove rowCount rows from the game area at the specified indices.
 */
void removeRows (GAMESTATE *state, int rowCount, int *rowIndices);
