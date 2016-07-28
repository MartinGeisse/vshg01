/*
 *  SplitLine.h
 *  baselib
 *
 *  Created by Martin on 9/2/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

/**
 * A split line is a straight infinite line that devides 2d space into
 * two halves. The split line distinguished front side and back side,
 * i.e. the two half-spaces are ordered.
 *
 * The representation of a split line is usually hard to use directly.
 * It is a triple of numbers A, B, C that define the line
 * equation
 *
 *   ax + by + c = 0		the line itself
 *   ax + by + c > 0		the front half-space
 *   ax + by + c < 0		the back half-space
 *
 */
struct SplitLine {
	
	/** the a value from the equation **/
	FixedPointNumber a;
	
	/** the b value from the equation **/
	FixedPointNumber b;
	
	/** the c value from the equation **/
	FixedPointNumber c;
	
};

/**
 * Initializes a split line from an origin and a direction vector. The front side of the split line is, by definition,
 * the side that appears right when starting at the origin and walking by dx, dy.
 */
void initializeSplitLine(struct SplitLine *splitLine, FixedPointNumber originX, FixedPointNumber originY, FixedPointNumber dx, FixedPointNumber dy);

/**
 * Returns 0 if the specified point is on the front side of the split line or directly on the split line, 1
 * if it is on the back side.
 */
int getSplitLineSide(struct SplitLine *splitLine, FixedPointNumber x, FixedPointNumber y);
