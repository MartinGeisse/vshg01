/*
 *  SplitLineTest.c
 *  baselib
 *
 *  Created by Martin on 9/2/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

#include "UnitTestSupport.h"
#include "SplitLine.c"

#define ONE FIXED_POINT_NUMBER_ONE

int main() {
	struct SplitLine splitLine;

	initializeSplitLine(&splitLine, 0, 0, ONE, 0);
	assert(getSplitLineSide(&splitLine, 0, 0) == 0, "horizontal split line, 1");
	assert(getSplitLineSide(&splitLine, -ONE, 0) == 0, "horizontal split line, 2");
	assert(getSplitLineSide(&splitLine, ONE, 0) == 0, "horizontal split line, 3");
	assert(getSplitLineSide(&splitLine, 0, -ONE) == 0, "horizontal split line, 4");
	assert(getSplitLineSide(&splitLine, 0, ONE) == 1, "horizontal split line, 5");
	assert(getSplitLineSide(&splitLine, -ONE, -ONE) == 0, "horizontal split line, 6");
	assert(getSplitLineSide(&splitLine, -ONE, ONE) == 1, "horizontal split line, 7");
	assert(getSplitLineSide(&splitLine, ONE, -ONE) == 0, "horizontal split line, 8");
	assert(getSplitLineSide(&splitLine, ONE, ONE) == 1, "horizontal split line, 9");

	initializeSplitLine(&splitLine, 0, 0, 0, ONE);
	assert(getSplitLineSide(&splitLine, 0, 0) == 0, "vertical split line, 1");
	assert(getSplitLineSide(&splitLine, -ONE, 0) == 1, "vertical split line, 2");
	assert(getSplitLineSide(&splitLine, ONE, 0) == 0, "vertical split line, 3");
	assert(getSplitLineSide(&splitLine, 0, -ONE) == 0, "vertical split line, 4");
	assert(getSplitLineSide(&splitLine, 0, ONE) == 0, "vertical split line, 5");
	assert(getSplitLineSide(&splitLine, -ONE, -ONE) == 1, "vertical split line, 6");
	assert(getSplitLineSide(&splitLine, -ONE, ONE) == 1, "vertical split line, 7");
	assert(getSplitLineSide(&splitLine, ONE, -ONE) == 0, "vertical split line, 8");
	assert(getSplitLineSide(&splitLine, ONE, ONE) == 0, "vertical split line, 9");

	initializeSplitLine(&splitLine, 0, 0, ONE, ONE);
	assert(getSplitLineSide(&splitLine, 0, 0) == 0, "diagonal split line, 1");
	assert(getSplitLineSide(&splitLine, -ONE, 0) == 1, "diagonal split line, 2");
	assert(getSplitLineSide(&splitLine, ONE, 0) == 0, "diagonal split line, 3");
	assert(getSplitLineSide(&splitLine, 0, -ONE) == 0, "diagonal split line, 4");
	assert(getSplitLineSide(&splitLine, 0, ONE) == 1, "diagonal split line, 5");
	assert(getSplitLineSide(&splitLine, -ONE, -ONE) == 0, "diagonal split line, 6");
	assert(getSplitLineSide(&splitLine, -ONE, ONE) == 1, "diagonal split line, 7");
	assert(getSplitLineSide(&splitLine, ONE, -ONE) == 0, "diagonal split line, 8");
	assert(getSplitLineSide(&splitLine, ONE, ONE) == 0, "diagonal split line, 9");

	initializeSplitLine(&splitLine, 0, 0, -ONE, 0);
	assert(getSplitLineSide(&splitLine, 0, 0) == 0, "reverse horizontal split line, 1");
	assert(getSplitLineSide(&splitLine, -ONE, 0) == 0, "reverse horizontal split line, 2");
	assert(getSplitLineSide(&splitLine, ONE, 0) == 0, "reverse horizontal split line, 3");
	assert(getSplitLineSide(&splitLine, 0, -ONE) == 1, "reverse horizontal split line, 4");
	assert(getSplitLineSide(&splitLine, 0, ONE) == 0, "reverse horizontal split line, 5");
	assert(getSplitLineSide(&splitLine, -ONE, -ONE) == 1, "reverse horizontal split line, 6");
	assert(getSplitLineSide(&splitLine, -ONE, ONE) == 0, "reverse horizontal split line, 7");
	assert(getSplitLineSide(&splitLine, ONE, -ONE) == 1, "reverse horizontal split line, 8");
	assert(getSplitLineSide(&splitLine, ONE, ONE) == 0, "reverse horizontal split line, 9");
	
	initializeSplitLine(&splitLine, 0, ONE, ONE, 2*ONE);
	assert(getSplitLineSide(&splitLine, 0, 0) == 0, "displaced split line, 1");
	assert(getSplitLineSide(&splitLine, -ONE/2, 0) == 0, "displaced split line, 2");
	assert(getSplitLineSide(&splitLine, -2*ONE, 0) == 1, "displaced split line, 3");
	assert(getSplitLineSide(&splitLine, 0, ONE/2) == 0, "displaced split line, 4");
	assert(getSplitLineSide(&splitLine, 0, 2*ONE) == 1, "displaced split line, 5");
	assert(getSplitLineSide(&splitLine, -ONE, ONE) == 1, "displaced split line, 6");
	
	return 0;
}

