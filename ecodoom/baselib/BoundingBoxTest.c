/*
 *  BoundingBoxTest.c
 *  baselib
 *
 *  Created by Martin on 8/26/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

#include "UnitTestSupport.h"
#include "BoundingBox.c"

static struct BoundingBox a, b;

void prepare() {
	clearBoundingBox(&a);
	addPointToBoundingBox(&a, 50 << FIXED_POINT_NUMBER_FRACTIONAL_BITS, 100 << FIXED_POINT_NUMBER_FRACTIONAL_BITS);
}

int main() {

	prepare();
	assert(a.minX == (50 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "a.minX 1");
	assert(a.maxX == (50 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "a.maxX 1");
	assert(a.minY == (100 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "a.minY 1");
	assert(a.maxY == (100 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "a.maxY 1");
	assert(getBoundingBoxWidth(&a) == (0 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "a width 1");
	assert(getBoundingBoxHeight(&a) == (0 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "a height 1");
	assert(getBoundingBoxCenterX(&a) == (50 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "a center x 1");
	assert(getBoundingBoxCenterY(&a) == (100 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "a center y 1");
	assert(!isPointInsideBoundingBox(&a, 49 << FIXED_POINT_NUMBER_FRACTIONAL_BITS, 99 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "point inside 1 a");
	assert(!isPointInsideBoundingBox(&a, 49 << FIXED_POINT_NUMBER_FRACTIONAL_BITS, 100 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "point inside 1 a");
	assert(!isPointInsideBoundingBox(&a, 49 << FIXED_POINT_NUMBER_FRACTIONAL_BITS, 101 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "point inside 1 a");
	assert(!isPointInsideBoundingBox(&a, 50 << FIXED_POINT_NUMBER_FRACTIONAL_BITS, 99 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "point inside 1 a");
	assert(isPointInsideBoundingBox(&a, 50 << FIXED_POINT_NUMBER_FRACTIONAL_BITS, 100 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "point inside 1 a");
	assert(!isPointInsideBoundingBox(&a, 50 << FIXED_POINT_NUMBER_FRACTIONAL_BITS, 101 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "point inside 1 a");
	assert(!isPointInsideBoundingBox(&a, 51 << FIXED_POINT_NUMBER_FRACTIONAL_BITS, 99 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "point inside 1 a");
	assert(!isPointInsideBoundingBox(&a, 51 << FIXED_POINT_NUMBER_FRACTIONAL_BITS, 100 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "point inside 1 a");
	assert(!isPointInsideBoundingBox(&a, 51 << FIXED_POINT_NUMBER_FRACTIONAL_BITS, 101 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "point inside 1 a");
	
	prepare();
	addPointToBoundingBox(&a, 60 << FIXED_POINT_NUMBER_FRACTIONAL_BITS, 100 << FIXED_POINT_NUMBER_FRACTIONAL_BITS);
	assert(a.minX == (50 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "a.minX 2a");
	assert(a.maxX == (60 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "a.maxX 2a");
	assert(a.minY == (100 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "a.minY 2a");
	assert(a.maxY == (100 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "a.maxY 2a");
	addPointToBoundingBox(&a, 54 << FIXED_POINT_NUMBER_FRACTIONAL_BITS, 100 << FIXED_POINT_NUMBER_FRACTIONAL_BITS);
	assert(a.minX == (50 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "a.minX 2b");
	assert(a.maxX == (60 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "a.maxX 2b");
	assert(a.minY == (100 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "a.minY 2b");
	assert(a.maxY == (100 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "a.maxY 2b");
	addPointToBoundingBox(&a, 44 << FIXED_POINT_NUMBER_FRACTIONAL_BITS, 100 << FIXED_POINT_NUMBER_FRACTIONAL_BITS);
	assert(a.minX == (44 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "a.minX 2c");
	assert(a.maxX == (60 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "a.maxX 2c");
	assert(a.minY == (100 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "a.minY 2c");
	assert(a.maxY == (100 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "a.maxY 2c");
	assert(getBoundingBoxWidth(&a) == (16 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "a width 2");
	assert(getBoundingBoxHeight(&a) == (0 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "a height 2");
	assert(getBoundingBoxCenterX(&a) == (52 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "a center x 2");
	assert(getBoundingBoxCenterY(&a) == (100 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "a center y 2");
	assert(!isPointInsideBoundingBox(&a, 43 << FIXED_POINT_NUMBER_FRACTIONAL_BITS, 100 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "point inside 1 a");
	assert(isPointInsideBoundingBox(&a, 44 << FIXED_POINT_NUMBER_FRACTIONAL_BITS, 100 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "point inside 1 a");
	assert(isPointInsideBoundingBox(&a, 49 << FIXED_POINT_NUMBER_FRACTIONAL_BITS, 100 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "point inside 1 a");
	assert(isPointInsideBoundingBox(&a, 60 << FIXED_POINT_NUMBER_FRACTIONAL_BITS, 100 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "point inside 1 a");
	assert(!isPointInsideBoundingBox(&a, 61 << FIXED_POINT_NUMBER_FRACTIONAL_BITS, 100 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "point inside 1 a");

	prepare();
	addPointToBoundingBox(&a, 56 << FIXED_POINT_NUMBER_FRACTIONAL_BITS, 120 << FIXED_POINT_NUMBER_FRACTIONAL_BITS);
	assert(a.minX == (50 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "a.minX 3a");
	assert(a.maxX == (56 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "a.maxX 3a");
	assert(a.minY == (100 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "a.minY 3a");
	assert(a.maxY == (120 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "a.maxY 3a");
	addPointToBoundingBox(&a, 51 << FIXED_POINT_NUMBER_FRACTIONAL_BITS, 70 << FIXED_POINT_NUMBER_FRACTIONAL_BITS);
	assert(a.minX == (50 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "a.minX 3b");
	assert(a.maxX == (56 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "a.maxX 3b");
	assert(a.minY == (70 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "a.minY 3b");
	assert(a.maxY == (120 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "a.maxY 3b");
	assert(getBoundingBoxWidth(&a) == (6 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "a width 3");
	assert(getBoundingBoxHeight(&a) == (50 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "a height 3");
	assert(getBoundingBoxCenterX(&a) == (53 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "a center x 3");
	assert(getBoundingBoxCenterY(&a) == (95 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "a center y 3");
	assert(!isPointInsideBoundingBox(&a, 53 << FIXED_POINT_NUMBER_FRACTIONAL_BITS, 122 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "point inside 1 a");
	assert(isPointInsideBoundingBox(&a, 53 << FIXED_POINT_NUMBER_FRACTIONAL_BITS, 120 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "point inside 1 a");
	
	prepare();
	clearBoundingBox(&b);
	addPointToBoundingBox(&b, 10 << FIXED_POINT_NUMBER_FRACTIONAL_BITS, 90 << FIXED_POINT_NUMBER_FRACTIONAL_BITS);
	addPointToBoundingBox(&b, 20 << FIXED_POINT_NUMBER_FRACTIONAL_BITS, 110 << FIXED_POINT_NUMBER_FRACTIONAL_BITS);
	addBoundingBoxToBoundingBox(&a, &b);
	assert(a.minX == (10 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "a.minX 4");
	assert(a.maxX == (50 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "a.maxX 4");
	assert(a.minY == (90 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "a.minY 4");
	assert(a.maxY == (110 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "a.maxY 4");
	assert(b.minX == (10 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "b.minX 4");
	assert(b.maxX == (20 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "b.maxX 4");
	assert(b.minY == (90 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "b.minY 4");
	assert(b.maxY == (110 << FIXED_POINT_NUMBER_FRACTIONAL_BITS), "b.maxY 4");
	
	return 0;
}

