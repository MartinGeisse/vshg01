/*
 *  StaticGraphicsDataTest.c
 *  baselib
 *
 *  Created by Martin on 8/14/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

#include "UnitTestSupport.h"
#include "StaticGraphicsData.c"

#define WAD_FILE_PATH	"/Users/martin/DOOM1.WAD"

int main() {
	zoneAllocatorInitialize();
	initializeWadFile(WAD_FILE_PATH);
	initializeStaticGraphicsData();

	/** rough flat data test. Detailed testing is done by ShowFlatsMain application **/
	assert(getFlatCount() == 56, "flat count");
	assert(getFlatTranslation(5) == 5, "flat translation 5");
	setFlatTranslation(5, 11);
	assert(getFlatTranslation(4) == 4, "flat translation 4");
	assert(getFlatTranslation(5) == 11, "flat translation 5 (modified)");
	assert(getFlatTranslation(6) == 6, "flat translation 6");
	assert(getTranslatedLumpFlatIndex(5) == 1218, "translated lump flat index 5");
	assert(getFlatIndexForName("FLOOR1_1") == 4, "flat index for name");
	assert(getFlatIndexForName("FLOOR1_7") == 5, "flat index for name not affected by translation");
	
	/** rough texture data test. Detailed testing is done by ShowTexturesMain application **/
	assert(getTextureCount() == 125, "texture count");
	assert(getTextureTranslation(5) == 5, "texture translation 5");
	setTextureTranslation(5, 11);
	assert(getTextureTranslation(4) == 4, "texture translation 4");
	assert(getTextureTranslation(5) == 11, "texture translation 5 (modified)");
	assert(getTextureTranslation(6) == 6, "texture translation 6");
	assert(getTextureIndexForName("BIGDOOR2") == 2, "getTextureIndexForName 1");
	assert(getTextureIndexForNameSafe("BIGDOOR2") == 2, "getTextureIndexForNameSafe 1");
	assert(getTextureIndexForNameSafe("FOOBAR") == -1, "getTextureIndexForNameSafe 2");
	
	return 0;
}
