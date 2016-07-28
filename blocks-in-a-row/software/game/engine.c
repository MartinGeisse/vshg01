/*
 *  engine.c
 *  Blocks-in-a-Row
 *
 *  Created by Martin Geisse on 30.11.07.
 *  Copyright 2007 __MyCompanyName__. All rights reserved.
 *
 */

#include "lowlevel/timer.h"
#include "lowlevel/keyboard.h"
#include "soundchain.h"
#include "draw.h"
#include "shapes.h"
#include "gamestate.h"
#include "engine.h"

typedef struct {
	//
	GAMESTATE gameState;

	// this flag signals outwards to stop immediately
	int gameOver;

	// key cooldowns
	int leftCooldown, rightCooldown, downCooldown, clockwiseCooldown, counterClockwiseCooldown;

	// fast-drop accumulation (for additional score)
	int fastDrop;

} ENGINE_DATA;

SOUND_CHAIN_ELEMENT bipSound[] = {
	{1, 1500, 1},
};

SOUND_CHAIN_ELEMENT landSound[] = {
	{1, 500, 2},
	{1, 300, 2},
};

SOUND_CHAIN_ELEMENT rotateSound[] = {
	{1, 1300, 2},
	{1, 800, 2},
};

SOUND_CHAIN_ELEMENT completeRowSound[] = {
	{1, 500, 2},
	{1, 800, 2},
	{1, 1000, 2},
	{1, 1600, 2},
	{1, 1000, 2},
	{1, 800, 2},
	{1, 500, 2},
	{1, 800, 2},
	{1, 1000, 2},
	{1, 1600, 2},
};
const int completeRowSoundLength = 10;

SOUND_CHAIN_ELEMENT gameOverFillSound[] = {
	{1, 800, 2},
	{1, 500, 2},
};

SOUND_CHAIN_ELEMENT nextLevelSound[] = {
	{1, 500, 3},
	{1, 1000, 3},
	{1, 2000, 3},
	{1, 500, 3},
	{1, 1000, 3},
	{1, 2000, 3},
	{1, 500, 3},
	{1, 1000, 3},
	{1, 2000, 3},
};
const int nextLevelSoundLength = 9;

int delayByLevel [] = {
	30, 27, 24, 21, 18, 15, 12, 8, 5, 2
};
int delayLevels = 10;

void engineFrame (void)
{
	waitForTimer ();
	resetTimer ();
	advanceSoundChains ();
}

void engineFrames (int frameCount)
{
	int i;
	for (i=0; i<frameCount; i++)
		engineFrame ();
}

void flashRows (int count, int *rows)
{
	fillGameRows (count, rows, 7);
	engineFrames (5);
	fillGameRows (count, rows, 8);
	engineFrames (5);
	fillGameRows (count, rows, 0);
	engineFrames (5);
	fillGameRows (count, rows, 8);
	engineFrames (5);
	fillGameRows (count, rows, 7);
	engineFrames (5);
	fillGameRows (count, rows, 8);
	engineFrames (5);
	fillGameRows (count, rows, 0);
	engineFrames (5);
}

void recolorTiles (ENGINE_DATA *data, unsigned char color)
{
	int i, j;
	unsigned char *gameArea = data->gameState.gameArea;

	for (i=0; i<10; i++) {
		for (j=0; j<20; j++) {
			if (gameArea [j*10+i] != 0)
				gameArea [j*10+i] = color;
		}
	}

	drawGameArea (gameArea);
}

void newLevel (ENGINE_DATA *data)
{
	drawLevel (data->gameState.rows / 10);

	startSoundChain (0, nextLevelSoundLength, nextLevelSound);
	recolorTiles (data, 4);
	engineFrames (5);
	recolorTiles (data, 6);
	engineFrames (5);
	recolorTiles (data, 7);
	engineFrames (5);
	recolorTiles (data, 6);
	engineFrames (5);
	recolorTiles (data, 4);
	engineFrames (5);
	recolorTiles (data, 8);
	engineFrames (5);
}

void gameOverFill (void)
{
	int i;
	for (i=19; i>=0; i--) {
		fillGameRow (i, 7);
		startSoundChain (0, 2, gameOverFillSound);
		engineFrames (5);
	}
}

static void clearPreview (ENGINE_DATA *data)
{
	drawPieceInPreview (0, data->gameState.preview0 & 0xff, 0);
	drawPieceInPreview (1, data->gameState.preview1 & 0xff, 0);
	drawPieceInPreview (2, data->gameState.preview2 & 0xff, 0);
}

static void drawPreview (ENGINE_DATA *data)
{
	drawPieceInPreview (0, data->gameState.preview0 & 0xff, data->gameState.preview0 >> 8);
	drawPieceInPreview (1, data->gameState.preview1 & 0xff, data->gameState.preview1 >> 8);
	drawPieceInPreview (2, data->gameState.preview2 & 0xff, data->gameState.preview2 >> 8);
}

void engineNewGame (ENGINE_DATA *data)
{
	initializeGameState (&data->gameState);
	data->gameOver = 0;
	data->leftCooldown = 0;
	data->rightCooldown = 0;
	data->downCooldown = 0;
	data->clockwiseCooldown = 0;
	data->counterClockwiseCooldown = 0;
	data->fastDrop = 0;

	drawBackground ();
	drawPreview (data);
	drawScore (data->gameState.score);
	drawLevel (data->gameState.rows / 10);
}

void engineLeft (ENGINE_DATA *data)
{
	if (moveCurrentShapeLeft (&data->gameState)) {
		drawShapeOnGameArea (data->gameState.shapeX + 1, data->gameState.shapeY,
			data->gameState.shapeIndex, 0);
		drawShapeOnGameArea (data->gameState.shapeX, data->gameState.shapeY,
			data->gameState.shapeIndex, data->gameState.shapeCharacter);
		startSoundChain (0, 1, bipSound);
	}
}

void engineRight (ENGINE_DATA *data)
{
	if (moveCurrentShapeRight (&data->gameState)) {
		drawShapeOnGameArea (data->gameState.shapeX - 1, data->gameState.shapeY,
			data->gameState.shapeIndex, 0);
		drawShapeOnGameArea (data->gameState.shapeX, data->gameState.shapeY,
			data->gameState.shapeIndex, data->gameState.shapeCharacter);
		startSoundChain (0, 1, bipSound);
	}
}

void engineDown (ENGINE_DATA *data)
{
	if (moveCurrentShapeDown (&data->gameState)) {
		drawShapeOnGameArea (data->gameState.shapeX, data->gameState.shapeY - 1,
			data->gameState.shapeIndex, 0);
		drawShapeOnGameArea (data->gameState.shapeX, data->gameState.shapeY,
			data->gameState.shapeIndex, data->gameState.shapeCharacter);
	} else {
		int completedRows [4];
		int count;

		if (pasteCurrentShape (&data->gameState)) {
			data->gameOver = 1;
			gameOverFill ();
			return;
		}

		data->gameState.score += data->fastDrop;
		data->fastDrop = 0;

		count = findCompletedRows (&data->gameState, data->gameState.shapeY, 4, completedRows);
		if (count > 0) {
			startSoundChain (0, completeRowSoundLength, completeRowSound);
			flashRows (count, completedRows);
			removeRows (&data->gameState, count, completedRows);
			if (addRows (&data->gameState, count))
				newLevel (data);
			else
				drawGameArea (data->gameState.gameArea);
		} else {
			startSoundChain (0, 2, landSound);
		}

		clearPreview (data);
		nextPiece (&data->gameState);
		drawPreview (data);

		drawScore (data->gameState.score);
	}
}

void engineRotateClockwise (ENGINE_DATA *data)
{
	if (rotateCurrentShapeClockwise (&data->gameState)) {
		drawShapeOnGameArea (data->gameState.shapeX, data->gameState.shapeY,
			shapeRotatedCounterClockwise [data->gameState.shapeIndex], 0);
		drawShapeOnGameArea (data->gameState.shapeX, data->gameState.shapeY,
			data->gameState.shapeIndex, data->gameState.shapeCharacter);
		startSoundChain (0, 2, rotateSound);
	}
}

void engineRotateCounterClockwise (ENGINE_DATA *data)
{
	if (rotateCurrentShapeCounterClockwise (&data->gameState)) {
		drawShapeOnGameArea (data->gameState.shapeX, data->gameState.shapeY,
			shapeRotatedClockwise [data->gameState.shapeIndex], 0);
		drawShapeOnGameArea (data->gameState.shapeX, data->gameState.shapeY,
			data->gameState.shapeIndex, data->gameState.shapeCharacter);
		startSoundChain (0, 2, rotateSound);
	}
}

void gameStep (ENGINE_DATA *data, int stepCounter)
{
	if (keyLeft ()) {
		if (data->leftCooldown == 0) {
			engineLeft (data);
			data->leftCooldown = 3;
		} else data->leftCooldown--;
	} else {
		data->leftCooldown = 0;
	}

	if (keyRight ()) {
		if (data->rightCooldown == 0) {
			engineRight (data);
			data->rightCooldown = 3;
		} else data->rightCooldown--;
	} else {
		data->rightCooldown = 0;
	}

	if (keyDown ()) {
		if (data->downCooldown == 0) {
			data->downCooldown = 1;
			data->fastDrop++;
			engineDown (data);
		} else data->downCooldown--;
	} else {
		int level = data->gameState.rows / 10;
		data->downCooldown = 0;
		data->fastDrop = 0;
		if ((level > delayLevels) ||
				(stepCounter % delayByLevel [level] == 0)) {
			engineDown (data);
		}
	}

	if (keyClockwise ()) {
		if (data->clockwiseCooldown == 0) {
			engineRotateClockwise (data);
			data->clockwiseCooldown = 10;
		} else data->clockwiseCooldown--;
	} else {
		data->clockwiseCooldown = 0;
	}

	if (keyCounterClockwise ()) {
		if (data->counterClockwiseCooldown == 0) {
			engineRotateCounterClockwise (data);
			data->counterClockwiseCooldown = 10;
		} else data->counterClockwiseCooldown--;
	} else {
		data->counterClockwiseCooldown = 0;
	}
}

void titleScreen (void)
{
	drawTitleScreen ();
	waitForAnyKey ();
}

void engineMainLoop (void)
{
	ENGINE_DATA data;
	int stepCounter = 0;

	while (1) {
		titleScreen ();
		engineNewGame (&data);
		stepCounter = 0;

		while (!data.gameOver) {
			engineFrame ();
			gameStep (&data, stepCounter);
			stepCounter++;
		}
	}
}

