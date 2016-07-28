/*
 *  FixedPointNumber.h
 *  baselib
 *
 *  Created by Martin on 7/31/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

/**
 * Number of fractional bits of a fixed-point number,
 * total bit count is 32.
 */
#define FIXED_POINT_NUMBER_FRACTIONAL_BITS 16

/**
 * The number 1, expressed as a fixed-point number.
 */
#define FIXED_POINT_NUMBER_ONE (1 << FIXED_POINT_NUMBER_FRACTIONAL_BITS)

/**
 * The greatest possible fixed-point number.
 */
#define FIXED_POINT_NUMBER_MAX_VALUE ((int)0x7fffffff)

/**
 * The least possible fixed-point number.
 */
#define FIXED_POINT_NUMBER_MIN_VALUE ((int)0x80000000)

/**
 * Fixed-point number data type.
 */
typedef int FixedPointNumber;

/**
 * Multiplies two fixed-point numbers and returns the result.
 */
FixedPointNumber fixedPointNumberMultiply(FixedPointNumber x, FixedPointNumber y);

/**
 * Divides two fixed-point numbers and returns the result.
 */
FixedPointNumber fixedPointNumberDivide(FixedPointNumber x, FixedPointNumber y);

/**
 * Computes the square root of the specified fixed-point number.
 */
FixedPointNumber fixedPointNumberSquareRoot(FixedPointNumber x);
