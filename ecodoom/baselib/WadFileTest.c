/*
 *  WadFileTest.c
 *  baselib
 *
 *  Created by Martin on 7/31/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

#define WAD_FILE_PATH	"/Users/martin/DOOM1.WAD"

#include "UnitTestSupport.h"
#include "ZoneAllocator.h"
#include "WadFile.c"

int main() {
	unsigned char *testLump;
	unsigned char expectedTestLumpContents[] = {
		0x6D, 0x02, 0x01, 0x07, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x32, 0x00, 0xF2
	};
	unsigned char *testLump2;
	
	/** initialize **/
	zoneAllocatorInitialize();
	initializeWadFile(WAD_FILE_PATH);
	// dumpWadFile();

	/** check lump count **/
	assert(wadFile.lumpCount == 1264, "lump count: %d", wadFile.lumpCount);

	/** find a lump that occurs only once **/
	assert(findWadFileLumpSafe("E1M1") == 6, "find E1M1");

	/** do not find by a prefix of the name **/
	assert(findWadFileLumpSafe("E1M") == -1, "do not find E1M");

	/** do not find if the name is a prefix of the search string **/
	assert(findWadFileLumpSafe("E1M1X") == -1, "do not find E1M1X");

	/** searching is case-insensitive **/
	assert(findWadFileLumpSafe("e1m1") == 6, "find e1m1");

	/** find a lump that occurs multiple times -- should find the last occurence **/
	assert(findWadFileLumpSafe("things") == 95, "find things");

	/** obtain lump size **/
	assert(getWadFileLumpSize(2) == 4000, "get lump size");
	
	/** obtain lump name **/
	assert(strcmp(getWadFileLumpName(2)->value, "ENDOOM") == 0, "get lump name");

	/** get a lump by name **/
	testLump = getWadFileLumpContentsByName("DEMO3");
	assert(memcmp(testLump, expectedTestLumpContents, 16) == 0, "test lump contents");
	
	/** get another lump from the cache **/
	testLump2 = getWadFileLumpContentsByName("DEMO2");

	/** get both lumps again: this should return them at the same address **/
	assert(getWadFileLumpContentsByName("DEMO3") == testLump, "get cached lump 1 again");
	assert(getWadFileLumpContentsByName("DEMO2") == testLump2, "get cached lump 2 again");
	
	/** shotdown the WAD file system (just make sure this doesn't crash) **/
	shutdownWadFile();
	
	return 0;
}
