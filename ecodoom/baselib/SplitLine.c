/*
 *  SplitLine.c
 *  baselib
 *
 *  Created by Martin on 9/2/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

#include "FixedPointNumber.h"
#include "SplitLine.h"

/**
 * See header file for information.
 */
void initializeSplitLine(struct SplitLine *splitLine, FixedPointNumber originX, FixedPointNumber originY, FixedPointNumber dx, FixedPointNumber dy) {
	splitLine->a = dy;
	splitLine->b = -dx;
	splitLine->c = fixedPointNumberMultiply(dx, originY) - fixedPointNumberMultiply(dy, originX);
}

/**
 * See header file for information.
 */
int getSplitLineSide(struct SplitLine *splitLine, FixedPointNumber x, FixedPointNumber y) {
	FixedPointNumber ax = fixedPointNumberMultiply(splitLine->a, x);
	FixedPointNumber by = fixedPointNumberMultiply(splitLine->b, y);
	return (ax + by + splitLine->c) < 0;
}
