/*
 *  gamestate.c
 *  Blocks-in-a-Row
 *
 *  Created by Martin Geisse on 30.11.07.
 *  Copyright 2007 __MyCompanyName__. All rights reserved.
 *
 */

#include "util/memory.h"
#include "util/random.h"
#include "shapes.h"
#include "gamestate.h"

void initializeGameState (GAMESTATE *state)
{
	clearGameArea (state);
	state->preview0 = randomPiece ();
	state->preview1 = randomPiece ();
	state->preview2 = randomPiece ();
	state->rows = 0;
	state->score = 0;
	nextPiece (state);
}

void clearGameArea (GAMESTATE *state)
{
	memset (state->gameArea, 0, sizeof(char) * 10 * 20);
}

int shiftPreview (GAMESTATE *state, int shiftIn)
{
	int shiftOut = state->preview0;
	state->preview0 = state->preview1;
	state->preview1 = state->preview2;
	state->preview2 = shiftIn;
	return shiftOut;
}

int randomPiece (void)
{
	char character;
	int piece;

	character = getRandom () % 7 + 1;
	if (character == 7) character = 8;
	piece = getRandom () % NUM_PIECES;
	return piece + (character<<8);
}

int shiftPreviewRandom (GAMESTATE *state)
{
	return shiftPreview (state, randomPiece ());
}

int getRowScore (int level, int num)
{
	switch (num) {
		case 1: return 40 * (level + 1);
		case 2: return 100 * (level + 1);
		case 3: return 300 * (level + 1);
		case 4: return 1200 * (level + 1);
		default: return 0;
	}
}

int addRows (GAMESTATE *state, int num)
{
	int oldLevel = state->rows / 10;
	state->score += getRowScore (oldLevel, num);
	state->rows += num;

	return (state->rows / 10) != oldLevel;
}

void enterShape (GAMESTATE *state, int shapeIndex, char shapeCharacter)
{
	state->shapeX = 3;
	state->shapeY = -4;
	state->shapeIndex = shapeIndex;
	state->shapeCharacter = shapeCharacter;
}

void nextPiece (GAMESTATE *state)
{
	int pieceAndCharacter = shiftPreviewRandom (state);
	int character = pieceAndCharacter >> 8;
	int piece = pieceAndCharacter & 0xff;
	int shape = normalShapeByPiece [piece];
	enterShape (state, shape, character);
}

int unblockedShapePosition (GAMESTATE *state, int x, int y, int shape)
{
	int i, j;
	unsigned char *matrix = shapeOccupationMatrices [shape];

	for (i=0; i<4; i++) {
		for (j=0; j<4; j++) {
			int x2, y2;

			if (matrix [j*4+i] == 0) continue;
			x2 = x + i;
			y2 = y + j;
			if (x2 < 0 || x2 > 9 || y2 > 19) return 0;
			if (y2 < 0) continue;

			if (state->gameArea [y2*10+x2] != 0) return 0;
		}
	}

	return 1;
}

int moveCurrentShapeDown (GAMESTATE *state)
{
	if (!unblockedShapePosition (state, state->shapeX, state->shapeY + 1, state->shapeIndex))
		return 0;

	state->shapeY++;
	return 1;
}

int moveCurrentShapeLeft (GAMESTATE *state)
{
	if (!unblockedShapePosition (state, state->shapeX - 1, state->shapeY, state->shapeIndex))
		return 0;

	state->shapeX--;
	return 1;
}

int moveCurrentShapeRight (GAMESTATE *state)
{
	if (!unblockedShapePosition (state, state->shapeX + 1, state->shapeY, state->shapeIndex))
		return 0;

	state->shapeX++;
	return 1;
}

int rotateCurrentShapeClockwise (GAMESTATE *state)
{
	int newShape = shapeRotatedClockwise [state->shapeIndex];
	if (!unblockedShapePosition (state, state->shapeX, state->shapeY, newShape))
		return 0;

	state->shapeIndex = newShape;
	return 1;
}

int rotateCurrentShapeCounterClockwise (GAMESTATE *state)
{
	int newShape = shapeRotatedCounterClockwise [state->shapeIndex];
	if (!unblockedShapePosition (state, state->shapeX, state->shapeY, newShape))
		return 0;

	state->shapeIndex = newShape;
	return 1;
}

int pasteShape (GAMESTATE *state, int x, int y, int shapeIndex, char shapeCharacter)
{
	int i, j;
	unsigned char *matrix = shapeOccupationMatrices [shapeIndex];
	int crossedBorder = 0;

	for (i=0; i<4; i++) {
		for (j=0; j<4; j++) {
			int x2, y2;

			if (matrix [j*4+i] == 0) continue;
			x2 = x + i;
			y2 = y + j;
			if (x2 < 0 || x2 > 9 || y2 < 0 || y2 > 19) {
				crossedBorder = 1;
				continue;
			}
			state->gameArea [y2*10+x2] = shapeCharacter;
		}
	}

	return crossedBorder;
}

int pasteCurrentShape (GAMESTATE *state)
{
	return pasteShape (state, state->shapeX, state->shapeY, state->shapeIndex, state->shapeCharacter);
}

int isRowCompleted (GAMESTATE *state, int rowIndex)
{
	int c;
	unsigned char *row = state->gameArea + rowIndex*10;

	for (c=0; c<10; c++) {
		if (row [c] == 0)
			return 0;
	}

	return 1;
}

void clearRow (GAMESTATE *state, int y)
{
	memset (state->gameArea + y*10, 0, 10 * sizeof (unsigned char));
}

void copyRow (GAMESTATE *state, int source, int dest)
{
	memcpy (state->gameArea + dest*10, state->gameArea + source*10, 10 * sizeof(unsigned char));
}

int findCompletedRows (GAMESTATE *state, int firstRowIndex, int maxRowCount, int *rowIndices)
{
	int i;
	int count = 0;
	for (i=0; i<maxRowCount; i++) {
		int row = firstRowIndex + i;
		if (row < 0 || row > 19) continue;

		if (isRowCompleted (state, row)) {
			rowIndices [count] = row;
			count++;
		}
	}

	return count;
}

static int intArrayContains (int count, int *array, int item)
{
	int i;
	for (i=0; i<count; i++) {
		if (array [i] == item)
			return 1;
	}

	return 0;
}

void removeRows (GAMESTATE *state, int rowCount, int *rowIndices)
{
	int r;

	int stack = 19;
	for (r=19; r>=0; r--) {
		if (intArrayContains (rowCount, rowIndices, r))
			continue;

		copyRow (state, r, stack);
		stack--;
	}

	while (stack >= 0) {
		clearRow (state, stack);
		stack--;
	}
}
