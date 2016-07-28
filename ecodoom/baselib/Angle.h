/*
 *  Angle.h
 *  baselib
 *
 *  Created by Martin on 7/31/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

/**
 * Angle value for 0 degrees.
 */
#define ANGLE_0 ((Angle)0)

/**
 * Angle value for 45 degrees.
 */
#define ANGLE_45 ((Angle)(1 << 29))

/**
 * Angle value for 90 degrees.
 */
#define ANGLE_90 ((Angle)(1 << 30))

/**
 * Angle value for 180 degrees.
 */
#define ANGLE_180 ((Angle)(1 << 31))

/**
 * Angle value for 270 degrees.
 */
#define ANGLE_270 ((Angle)((1 << 31) + (1 << 30)))

/**
 * Data type used to represent angles. It uses the range
 * 0..2^32-1 to represent 0..2*PI.
 */
typedef unsigned int Angle;

/**
 * Computes the sine for the specified angle.
 */
FixedPointNumber sinForAngle(Angle angle);

/**
 * Computes the cosine for the specified angle.
 */
FixedPointNumber cosForAngle(Angle angle);

/**
 * Computes the tangent for the specified angle.
 */
FixedPointNumber tanForAngle(Angle angle);

/**
 * Computes the angle for the specified point,
 * using the origin as the measuring center and the
 * x axis as the reference angle.
 */
Angle angleForPoint(FixedPointNumber x, FixedPointNumber y);
