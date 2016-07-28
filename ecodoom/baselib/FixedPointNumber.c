/*
 *  FixedPointNumber.c
 *  baselib
 *
 *  Created by Martin on 7/31/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

#include <math.h>
#include "SystemDebug.h"
#include "FixedPointNumber.h"

/**
 * See header file for information.
 */
FixedPointNumber fixedPointNumberMultiply(FixedPointNumber x, FixedPointNumber y) {
/*
	int xhigh = x >> 16;
	int xlow = x & 0xffff;
	int yhigh = y >> 16;
	int ylow = y & 0xffff;
	int xlowylow = xlow * ylow;
	int xlowyhigh = xlow * yhigh;
	int xhighylow = xhigh * ylow;
	int xhighyhigh = xhigh * yhigh;
	return (xhighyhigh << 16) + xhighylow + xlowyhigh + (xlowylow >> 16);
*/
	long long xx = x;
	long long yy = y;
	return (FixedPointNumber)((xx * yy) >> 16);
}

/**
 * See header file for information.
 */
FixedPointNumber fixedPointNumberDivide(FixedPointNumber x, FixedPointNumber y) {
	long long xx = x;
	long long yy = y;
	return (FixedPointNumber)((xx << 16) / yy);
}

/**
 * See header file for information.
 */
FixedPointNumber fixedPointNumberSquareRoot(FixedPointNumber x) {
	if (x < 0) {
		systemFatalError("trying to compute fixedPointNumberSquareRoot() of a negative number");
		return 0;
	} else {
		int r = sqrt(x);
		return (r << 8);
	}
}
