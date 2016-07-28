/*
 *  draw.c
 *  Blocks-in-a-Row
 *
 *  Created by Martin Geisse on 30.11.07.
 *  Copyright 2007 __MyCompanyName__. All rights reserved.
 *
 */

#include "util/memory.h"
#include "lowlevel/display.h"
#include "draw.h"
#include "shapes.h"

static const unsigned char titleScreenTemplate [40*30] = {
	 8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,
	 8,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  8,
	 8,  0,  1,  1,  1,  1,  0,  0,  2,  0,  0,  0,  0,  0,  0,  3,  3,  3,  3,  0,  0,  0,  4,  4,  4,  0,  0,  5,  0,  0,  0,  5,  0,  0,  6,  6,  6,  0,  0,  8,
	 8,  0,  1,  0,  0,  0,  1,  0,  2,  0,  0,  0,  0,  0,  3,  0,  0,  0,  0,  3,  0,  4,  0,  0,  0,  4,  0,  5,  0,  0,  5,  0,  0,  6,  0,  0,  0,  6,  0,  8,
	 8,  0,  1,  0,  0,  0,  1,  0,  2,  0,  0,  0,  0,  0,  3,  0,  0,  0,  0,  3,  0,  4,  0,  0,  0,  0,  0,  5,  0,  5,  0,  0,  0,  6,  0,  0,  0,  0,  0,  8,
	 8,  0,  1,  1,  1,  1,  0,  0,  2,  0,  0,  0,  0,  0,  3,  0,  0,  0,  0,  3,  0,  4,  0,  0,  0,  0,  0,  5,  5,  0,  0,  0,  0,  0,  6,  6,  6,  0,  0,  8,
	 8,  0,  1,  0,  0,  0,  1,  0,  2,  0,  0,  0,  0,  0,  3,  0,  0,  0,  0,  3,  0,  4,  0,  0,  0,  0,  0,  5,  0,  5,  0,  0,  0,  0,  0,  0,  0,  6,  0,  8,
	 8,  0,  1,  0,  0,  0,  1,  0,  2,  0,  0,  0,  0,  0,  3,  0,  0,  0,  0,  3,  0,  4,  0,  0,  0,  4,  0,  5,  0,  0,  5,  0,  0,  6,  0,  0,  0,  6,  0,  8,
	 8,  0,  1,  1,  1,  1,  0,  0,  2,  2,  2,  2,  2,  0,  0,  3,  3,  3,  3,  0,  0,  0,  4,  4,  4,  0,  0,  5,  0,  0,  0,  5,  0,  0,  6,  6,  6,  0,  0,  8,
	 8,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  8,
	 8,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  8,
	 8,  0,  0,  0,  0,  4,  0,  5,  0,  0,  0,  5,  0,  0,  0,  6,  6,  0,  0,  0,  1,  1,  0,  0,  0,  2,  2,  0,  0,  3,  0,  0,  0,  3,  0,  0,  0,  0,  0,  8,
	 8,  0,  0,  0,  0,  4,  0,  5,  5,  0,  0,  5,  0,  0,  6,  0,  0,  6,  0,  0,  1,  0,  1,  0,  2,  0,  0,  2,  0,  3,  0,  0,  0,  3,  0,  0,  0,  0,  0,  8,
	 8,  0,  0,  0,  0,  4,  0,  5,  0,  5,  0,  5,  0,  0,  6,  6,  6,  6,  0,  0,  1,  1,  0,  0,  2,  0,  0,  2,  0,  3,  0,  3,  0,  3,  0,  0,  0,  0,  0,  8,
	 8,  0,  0,  0,  0,  4,  0,  5,  0,  0,  5,  5,  0,  0,  6,  0,  0,  6,  0,  0,  1,  0,  1,  0,  2,  0,  0,  2,  0,  3,  0,  3,  0,  3,  0,  0,  0,  0,  0,  8,
	 8,  0,  0,  0,  0,  4,  0,  5,  0,  0,  0,  5,  0,  0,  6,  0,  0,  6,  0,  0,  1,  0,  1,  0,  0,  2,  2,  0,  0,  0,  3,  0,  3,  0,  0,  0,  0,  0,  0,  8,
	 8,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  8,
	 8,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  8,
	 8,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  8,
	 8,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  8,
	 8,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  8,
	 8,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  8,
	 8,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  8,
	 8,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  8,
	 8,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  8,
	 8,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  8,
	 8,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  8,
	 8,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  8,
	 8,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  8,
	 8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,  8,
};

static const unsigned char backgroundTemplate [40*30] = {
	 9, 11, 11,  9,  9,  9,  9,  9,  9,  9,  9,  9, 11,  9,  9,  9,  9,  9,  9,  9,  9,  9, 11, 11,  9,  9,  9,  9,  9,  9,  9,  9,  9,  9,  9,  9,  9, 11,  9,  9,
	 9, 13, 12, 10, 10, 10, 10, 16,  9,  9,  9,  9, 11,  9,  9,  9,  9,  9,  9,  9,  9,  9, 11, 11,  9,  9,  9,  9,  9,  9,  9, 15, 10, 10, 10, 10, 10,  7,  9,  9,
	 9,  9, 11,  9,  9,  9,  9, 11,  9,  9, 15, 10, 12, 10, 10, 10, 10, 10, 10, 10, 10, 16, 11, 11,  9,  9, 15, 16,  9,  9,  9, 11, 15, 10, 10, 10, 10,  7,  9,  9,
	 9,  9, 11,  9,  9,  9,  9, 11,  9,  9, 11,  9, 11,  9,  9,  9,  9,  9,  9,  9,  9, 11, 11, 11,  9,  9, 11, 11,  9,  9,  9, 11, 11, 15, 10, 10, 10,  7,  9,  9,
	 9,  9, 11,  9,  9,  7,  7,  7,  7,  7,  7,  7,  7,  7,  7,  7,  7,  9,  9,  9,  9, 11, 11, 11,  9,  9, 11, 11,  9,  9,  9, 11, 11, 11,  9,  9,  9, 11,  9,  9,
	 9,  9, 11,  9,  9,  7,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  7,  9,  9,  7,  7,  7,  7,  7,  7,  7,  7,  7,  7,  7,  7,  7,  7,  7,  7,  9,  9, 11,  9,  9,
	 9,  9, 11,  9,  9,  7,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  7, 10, 10,  7,  0,  0,  0,  0,  7,  0,  0,  0,  0,  7,  0,  0,  0,  0,  7,  9,  9, 11,  9,  9,
	10, 10, 12, 10, 10,  7,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  7, 10, 10,  7,  0,  0,  0,  0,  7,  0,  0,  0,  0,  7,  0,  0,  0,  0,  7,  9,  9, 11,  9,  9,
	 9,  9, 11,  9,  9,  7,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  7, 10, 10,  7,  0,  0,  0,  0,  7,  0,  0,  0,  0,  7,  0,  0,  0,  0,  7,  9,  9, 11,  9,  9,
	 9,  9, 11,  9,  9,  7,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  7, 10, 10,  7,  0,  0,  0,  0,  7,  0,  0,  0,  0,  7,  0,  0,  0,  0,  7, 10, 10, 12, 10, 10,
	 9,  9, 11,  9,  9,  7,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  7, 10, 10,  7,  0,  0,  0,  0,  7,  0,  0,  0,  0,  7,  0,  0,  0,  0,  7,  9,  9, 11,  9,  9,
	 9,  9, 11,  9,  9,  7,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  7, 10, 16,  7,  0,  0,  0,  0,  7,  0,  0,  0,  0,  7,  0,  0,  0,  0,  7,  9,  9, 11,  9,  9,
	 9,  9, 13, 10, 10,  7,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  7,  9, 11,  7,  7,  7,  7,  7,  7,  7,  7,  7,  7,  7,  7,  7,  7,  7,  7,  9,  9, 11,  9,  9,
	 9,  9,  9,  9,  9,  7,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  7,  9, 11,  9,  9,  9,  9,  9,  9,  9,  9,  9,  9,  9,  9,  9,  9,  9,  9,  9,  9, 11,  9,  9,
	 9,  9,  9,  9,  9,  7,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  7,  9, 11,  9,  9,  9,  9,  9,  9,  9,  9,  9,  9,  9,  9,  9,  9,  9,  9,  9,  9, 11,  9,  9,
	 9,  9,  9,  9,  9,  7,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  7,  9, 11,  9,  7,  7,  7,  7,  7,  7,  7,  7,  7,  7,  7,  7,  7,  7,  9,  9,  9, 11,  9,  9,
	 9,  9,  7, 10, 10,  7,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  7,  9, 11,  9,  7,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  7,  9,  9,  9, 11,  9,  9,
	 9,  9,  7, 10, 10,  7,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  7,  9, 11,  9,  7,  0,  0,  0, 35, 19, 31, 34, 21, 53,  0,  0,  0,  7,  9,  9,  9, 11,  9,  9,
	 9,  9,  9,  9,  9,  7,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  7,  9, 11,  9,  7,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  7,  9,  9,  9, 11,  9,  9,
	10,  7, 10, 10, 10,  7,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  7,  9, 11,  9,  7,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  7,  9,  9,  9, 11,  9,  9,
	 9, 11,  9,  9,  9,  7,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  7,  9, 11,  9,  7,  0,  0,  0, 28, 21, 38, 21, 28, 53,  0,  0,  0,  7, 10, 10, 10, 12, 10, 10,
	 9, 11,  9,  9,  9,  7,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  7, 10,  7, 10,  7,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  7, 10, 10, 10, 12, 10, 10,
	 9, 11,  9,  9,  9,  7,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  7,  9, 11,  9,  7,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  7,  9,  9,  9, 11,  9,  9,
	10, 14,  9,  9,  9,  7,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  7,  9, 11,  9,  7,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  7,  9,  9,  9, 11,  9,  9,
	 9,  9,  9,  9,  9,  7,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  7,  9, 11,  9,  7,  7,  7,  7,  7,  7,  7,  7,  7,  7,  7,  7,  7,  7,  9,  9,  9, 11,  9,  9,
	 9,  9,  9,  9,  9,  7,  7,  7,  7,  7,  7,  7,  7,  7,  7,  7,  7,  9, 11,  9,  9,  9, 11,  9,  9,  9,  9, 11,  9,  9, 11,  9,  9,  9,  9,  9,  9, 11,  9,  9,
	10, 10, 10,  7,  9,  9,  9,  9,  9,  9,  9,  9, 11,  9,  9,  9,  9,  9, 11,  9,  9,  9, 11,  9,  9,  9,  9, 11,  9,  9, 11,  9,  9,  9,  9,  9,  9, 11,  9,  9,
	 9,  9,  9,  9,  9,  9,  9,  9,  9,  9,  9,  9, 11,  9,  9,  9,  9,  9, 13, 10, 10, 10, 12, 10, 10, 10, 10,  7, 10, 10, 14,  9,  9,  9,  9, 15, 10,  7, 10, 10,
	 9,  9,  9,  9,  9,  9,  9,  9,  9,  9,  9,  9, 11,  9,  9,  9,  9,  9,  9,  9,  9,  9, 11,  9,  9,  9,  9,  9,  9,  9,  9,  9,  9,  9,  9, 11,  9,  9,  9,  9,
	 9,  9,  9,  9,  9,  9,  9,  9,  9,  9,  9,  9, 11,  9,  9,  9,  9,  9,  9,  9,  9,  9, 11,  9,  9,  9,  9,  9,  9,  9,  9,  9,  9,  9,  9, 11,  9,  9,  9,  9
};

void drawScreen (const unsigned char *template)
{
	int i, j;

	for (i=0; i<40; i++) {
		for (j=0; j<30; j++) {
			displayPutChar (i, j, template [j*40+i]);
		}
	}
}

void drawBackground (void)
{
	drawScreen (backgroundTemplate);
}

void drawTitleScreen (void)
{
	drawScreen (titleScreenTemplate);
	drawCenteredASCIIString (18, "FPGA DEMONSTRATION PROJECT");
	drawCenteredASCIIString (19, "FH GIESSEN-FRIEDBERG");
	drawHalfCenteredASCIIString (0, 21, "HELLWIG GEISSE");
	drawHalfCenteredASCIIString (1, 21, "MARTIN GEISSE");
	drawHalfCenteredASCIIString (0, 22, "FELIX GRUETZMACHER");
	drawHalfCenteredASCIIString (1, 22, "DENNIS KUHN");
	drawHalfCenteredASCIIString (0, 23, "THILO STADELMANN");
	drawHalfCenteredASCIIString (1, 23, "NORMAN ULBRICH");
	drawHalfCenteredASCIIString (0, 24, "ROLF VIEHMANN");
	drawHalfCenteredASCIIString (1, 24, "DANIEL WEBELSIEP");
	drawHalfCenteredASCIIString (0, 25, "TOBIAS WEBELSIEP");
}

void drawClippedShapeOnScreen (int x, int y, int shapeIndex, char c, int minx, int miny, int maxx, int maxy)
{
	int i, j;
	unsigned char *matrix = shapeOccupationMatrices [shapeIndex];

	for (i=0; i<4; i++) {
		int x2 = x + i;
		if (x2 < minx || x2 > maxx) continue;

		for (j=0; j<4; j++) {
			int y2 = y + j;
			if (y2 < miny || y2 > maxy) continue;

			if (matrix [j*4+i])
				displayPutChar (x2, y2, c);
		}
	}
}

void drawShapeOnScreen (int x, int y, int shapeIndex, char c)
{
	drawClippedShapeOnScreen (x, y, shapeIndex, c, 0, 0, 39, 29);
}


void drawShapeOnGameArea (int x, int y, int shapeIndex, char c)
{
	drawClippedShapeOnScreen (x + GAME_AREA_X_ON_SCREEN, y + GAME_AREA_Y_ON_SCREEN,
		shapeIndex, c,
		GAME_AREA_X_ON_SCREEN, GAME_AREA_Y_ON_SCREEN,
		GAME_AREA_X_ON_SCREEN + 9, GAME_AREA_Y_ON_SCREEN + 19);
}

void drawShapeInPreview (int previewIndex, int shapeIndex, char c)
{
	drawShapeOnScreen (PREVIEW_X_ON_SCREEN + previewIndex * PREVIEW_X_DELTA,
		PREVIEW_Y_ON_SCREEN, shapeIndex, c);
}

void drawPieceInPreview (int previewIndex, int pieceIndex, char c)
{
	drawShapeInPreview (previewIndex, normalShapeByPiece [pieceIndex], c);
}

void fillGameRow (int y, char c)
{
	int i;
	for (i=0; i<10; i++) {
		displayPutChar (GAME_AREA_X_ON_SCREEN + i, GAME_AREA_Y_ON_SCREEN + y, c);
	}
}

void fillGameRows (int count, int *rows, char c)
{
	int i;
	for (i=0; i<count; i++)
		fillGameRow (rows [i], c);
}

void drawGameArea (unsigned char *data)
{
	int i, j;
	for (i=0; i<10; i++) {
		for (j=0; j<20; j++) {
			displayPutChar (GAME_AREA_X_ON_SCREEN + i, GAME_AREA_Y_ON_SCREEN + j,
				data [j*10+i]);
		}
	}
}

void drawString (int x, int y, int length, unsigned char *characters)
{
	int i;
	for (i=0; i<length; i++)
		displayPutChar (x+i, y, characters [i]);
}

void drawASCIICharacter (int x, int y, char c)
{
	unsigned char code = 0;
	if (c >= 'A' && c <= 'Z')
		code = c - 'A' + 17;
	else if (c == ':')
		code = 53;
	else if (c == '!')
		code = 54;
	else if (c == '-')
		code = 55;
	else if (c == '.')
		code = 56;

	displayPutChar (x, y, code);
}

void drawASCIIString (int x, int y, char *s)
{
	while (*s != 0) {
		drawASCIICharacter (x, y, *s);
		x++;
		s++;
	}
}

void drawCenteredASCIIString(int y, char *s)
{
	int length = 0;
	while (s [length] != 0)
		length++;
	drawASCIIString ((40 - length) / 2, y, s);
}

void drawHalfCenteredASCIIString(int half, int y, char *s)
{
	int length = 0;
	while (s [length] != 0)
		length++;
	drawASCIIString ((20 - length) / 2 + (half ? 19 : 1), y, s);
}

void drawNumber (int x, int y, unsigned int value, int digits, unsigned char padding)
{
	int i;
	unsigned char buffer [40];
	memset (buffer, padding, digits * sizeof (unsigned char));
	buffer [digits - 1] = 43;

	for (i=digits-1; i>=0; i--) {
		if (value == 0) break;
		buffer [i] = (value % 10) + 43;
		value = value / 10;
	}

	drawString (x, y, digits, buffer);
}

void drawScore (int score)
{
	drawNumber (23, 18, score, 8, 0);
}

void drawLevel (int level)
{
	drawNumber (26, 21, level, 2, 43);
}
