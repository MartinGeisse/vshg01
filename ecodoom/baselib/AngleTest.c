/*
 *  AngleTest.c
 *  baselib
 *
 *  Created by Martin on 8/10/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

#include "UnitTestSupport.h"
#include "math.h"
#include "Angle.c"

void checkRange(FixedPointNumber x, FixedPointNumber min, FixedPointNumber max, const char *message) {
	assert(x >= min && x <= max, message);
}

void checkNear(FixedPointNumber x, FixedPointNumber origin, FixedPointNumber maxDistance, const char *message) {
	checkRange(x - origin, -maxDistance, +maxDistance, message);
}

void checkAngleRange(Angle x, Angle min, Angle max, const char *message) {
	if (min <= max) {
		assert(x >= min && x <= max, message);
	} else {
		assert(x >= min || x <= max, message);
	}
}

void checkAngleNear(Angle x, Angle origin, Angle maxDistance, const char *message) {
	checkAngleRange(x - origin, -maxDistance, +maxDistance, message);
}

int main() {
	Angle x;
	
	checkNear(sinForAngle(ANGLE_0), 0, 50, "sin(0°)");
	checkNear(sinForAngle(ANGLE_90 / 3), 65536.0 * 0.5, 50, "sin(30°)");
	checkNear(sinForAngle(ANGLE_45), 65536.0 * (1.0 / sqrt(2.0)), 50, "sin(45°)");
	checkNear(sinForAngle(ANGLE_90 * 2 / 3), 65536.0 * sqrt(3.0) * 0.5, 50, "sin(60°)");
	checkNear(sinForAngle(ANGLE_90), 65536.0 * 1.0, 50, "sin(90°)");

	for (x = ANGLE_0; x < ANGLE_90; x += 20000) {
		checkNear(sinForAngle(x), sinForAngle(ANGLE_180 - x), 100, "sin up to 180°");
	}

	for (x = ANGLE_0; x < ANGLE_180; x += 20000) {
		checkNear(sinForAngle(x), -sinForAngle(ANGLE_180 + x), 100, "sin up to 360°");
	}

	for (x = 20001; x > 20000; x += 20000) {
		checkNear(sinForAngle(x + ANGLE_90), cosForAngle(x), 100, "cos");
	}

	for (x = 20001; x > 20000; x += 20000) {
		FixedPointNumber sinDirect = sinForAngle(x);
		FixedPointNumber cosDirect = cosForAngle(x);
		FixedPointNumber tanDirect = tanForAngle(x);
		FixedPointNumber tanComputed = (cosDirect == 0) ? FIXED_POINT_NUMBER_MAX_VALUE : fixedPointNumberDivide(sinDirect, cosDirect);
		FixedPointNumber maxDistance = ((tanDirect < 0) ? -tanDirect : tanDirect) / 100;
		checkNear(tanDirect, tanComputed, maxDistance, "tan");
	}
	
	checkAngleNear(angleForPoint(0, 0), 0, 5, "AFP(0,0)");
	checkAngleNear(angleForPoint(1000, 0), 0, 5, "AFP(1000,0)");
	checkAngleNear(angleForPoint(2000, 0), 0, 5, "AFP(2000,0)");
	checkAngleNear(angleForPoint(sqrt(3) * 500, 500), ANGLE_90 / 3, ANGLE_90 / 90, "AFP(0.5*sqrt(3), 0.5)");
	checkAngleNear(angleForPoint(1000, 1000), ANGLE_45, ANGLE_90 / 90, "AFP 45");
	checkAngleNear(angleForPoint(0, 1000), ANGLE_90, ANGLE_90 / 90, "AFP 90");
	checkAngleNear(angleForPoint(-1000, 1000), ANGLE_90 + ANGLE_45, ANGLE_90 / 90, "AFP 135");
	checkAngleNear(angleForPoint(-1000, 0), ANGLE_180, ANGLE_90 / 90, "AFP 180");
	checkAngleNear(angleForPoint(-1000, -1000), ANGLE_180 + ANGLE_45, ANGLE_90 / 90, "AFP 225");
	checkAngleNear(angleForPoint(0, -1000), ANGLE_270, ANGLE_90 / 90, "AFP 270");
	checkAngleNear(angleForPoint(1000, -1000), ANGLE_270 + ANGLE_45, ANGLE_90 / 90, "AFP 315");

	for (x = 20001; x > 20000; x += 20000) {
		FixedPointNumber sinDirect = sinForAngle(x);
		FixedPointNumber cosDirect = cosForAngle(x);
		FixedPointNumber computedAngle = angleForPoint(cosDirect, sinDirect);
		checkAngleNear(computedAngle, x, ANGLE_90 / 90, "AFP loop");
	}
	
	return 0;
}

