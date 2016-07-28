/*
 *  StaticGameData.c
 *  baselib
 *
 *  Created by Martin on 8/28/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

#include "Common.h"
#include "WadFile.h"
#include "FixedPointNumber.h"
#include "StaticGraphicsData.h"
#include "StaticGameData.h"

/**
 * See header file for information.
 */
struct StaticGameData staticGameData;

/**
 * See header file for information.
 */
void initializeStaticGameData() {
	staticGameData.skyFlatIndex = getFlatIndexForName("F_SKY1");
	staticGameData.skyTextureIndex = getTextureIndexForName("SKY1");
	// staticGameData.skytexturemid = 100 << FIXED_POINT_NUMBER_FRACTIONAL_BITS;
}

/**
 * See header file for information.
 */
void disposeStaticGameData() {
	staticGameData.skyFlatIndex = -1;
	staticGameData.skyTextureIndex = -1;
}
