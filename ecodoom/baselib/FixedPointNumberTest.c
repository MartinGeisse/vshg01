/*
 *  FixedPointNumberTest.c
 *  baselib
 *
 *  Created by Martin on 7/31/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

#include "UnitTestSupport.h"
#include "FixedPointNumber.c"

int main() {
	FixedPointNumber x, y, z;

	/** check number one **/
	assert(FIXED_POINT_NUMBER_ONE >> FIXED_POINT_NUMBER_FRACTIONAL_BITS == 1, "fixed point one");
	assert(FIXED_POINT_NUMBER_ONE >> (FIXED_POINT_NUMBER_FRACTIONAL_BITS + 1) == 0, "fixed point one");
	
	/** min < 0, max > 0 **/
	assert(FIXED_POINT_NUMBER_MAX_VALUE > 0, "fixed point max");
	assert(FIXED_POINT_NUMBER_MIN_VALUE < 0, "fixed point min");
	
	/** max + 1 = max **/
	x = FIXED_POINT_NUMBER_MAX_VALUE;
	x = x + 1;
	assert(x == FIXED_POINT_NUMBER_MIN_VALUE, "fixed point min+1=max");
	
	/** multiplication: 2 x 2 **/
	x = FIXED_POINT_NUMBER_ONE;
	x = x + x;
	x = fixedPointNumberMultiply(x, x);
	assert(x == (4 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "multiply (1)");
	
	/** multiplication: 1.5 x 3.5 = (3/2) x (7 / 2) = (21 / 4) **/
	x = 3 << (FIXED_POINT_NUMBER_FRACTIONAL_BITS - 1);
	y = 7 << (FIXED_POINT_NUMBER_FRACTIONAL_BITS - 1);
	z = 21 << (FIXED_POINT_NUMBER_FRACTIONAL_BITS - 2);
	assert(fixedPointNumberMultiply(x, y) == z, "multiply (2)");
	
	/** multiplication: don't overflow if the result fits **/
	x = 255 << FIXED_POINT_NUMBER_FRACTIONAL_BITS;
	y = 255 << FIXED_POINT_NUMBER_FRACTIONAL_BITS;
	z = (255 * 255) << FIXED_POINT_NUMBER_FRACTIONAL_BITS;
	assert(fixedPointNumberMultiply(x, y) == z, "multiply (3)");
	
	/** division: 5 / 2 **/
	x = 5 << FIXED_POINT_NUMBER_FRACTIONAL_BITS;
	y = 2 << FIXED_POINT_NUMBER_FRACTIONAL_BITS;
	z = 5 << (FIXED_POINT_NUMBER_FRACTIONAL_BITS - 1);
	assert(fixedPointNumberDivide(x, y) == z, "divide (1)");

	/** division: SMALL / 2*SMALL **/
	x = 1;
	y = 2;
	z = 1 << (FIXED_POINT_NUMBER_FRACTIONAL_BITS - 1);
	assert(fixedPointNumberDivide(x, y) == z, "divide (2)");
	
	/** square root of 0 **/
	x = 0;
	z = 0;
	assert(fixedPointNumberSquareRoot(x) == z, "square root (0)");
	
	/** square root of 1 **/
	x = FIXED_POINT_NUMBER_ONE;
	z = FIXED_POINT_NUMBER_ONE;
	assert(fixedPointNumberSquareRoot(x) == z, "square root (1)");

	/** square root of a very small number **/
	x = 100;
	z = 10 << 8;
	assert(fixedPointNumberSquareRoot(x) == z, "square root (small)");

	/** square root of a typical integral number **/
	x = 9 << FIXED_POINT_NUMBER_FRACTIONAL_BITS;
	z = 3 << FIXED_POINT_NUMBER_FRACTIONAL_BITS;
	assert(fixedPointNumberSquareRoot(x) == z, "square root (9)");

	/** square root of a fractional number **/
	x = 9 << (FIXED_POINT_NUMBER_FRACTIONAL_BITS - 4);
	z = 3 << (FIXED_POINT_NUMBER_FRACTIONAL_BITS - 2);
	assert(fixedPointNumberSquareRoot(x) == z, "square root (9 / 16)");
	
	return 0;
}
