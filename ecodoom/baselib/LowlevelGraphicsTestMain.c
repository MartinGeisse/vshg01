/*
 *  LowlevelGraphicsTestMain.c
 *  baselib
 *
 *  Created by Martin on 8/15/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

#include <allegro.h>
#include "Common.h"
#include "SystemGraphics.h"
#include "ZoneAllocator.h"
#include "WadFile.h"
#include "FixedPointNumber.h"
#include "StaticGraphicsData.h"
#include "LowlevelGraphics.h"
#include "TestMainCommon.h"

const char *testScaledPatchName;
const char *testScaledTextureName;

static void testScaledPatch(int destinationHeight) {
	int i;
	unsigned char *patch = getWadFileLumpContentsByName(testScaledPatchName);
	int patchWidth = fromLittleEndian16u(DESERIALIZE(patch, 0, unsigned short));
	int patchHeight = fromLittleEndian16u(DESERIALIZE(patch, 2, unsigned short));
	int *serializedPostTable = (int*)(patch + 8);
	
	acquire_screen();
	clear(screen);
	for (i=0; i<patchWidth; i++) {
		unsigned char *post = patch + fromLittleEndian16u(serializedPostTable[i]);
		drawScaledPatchColumnOnScreen(i, 0, patchHeight, destinationHeight, post);
	}
	release_screen();
	readkey();
}

static void testScaledTexture(int destinationHeight) {
	int textureIndex = getTextureIndexForName(testScaledTextureName);
	struct TextureDescriptor *texture = getTexture(textureIndex);
	int i;
	
	acquire_screen();
	clear(screen);
	for (i=0; i<texture->width; i++) {
		unsigned char *column = getTextureColumnTexels(texture, i);
		drawScaledTextureColumnOnScreen(i, 0, texture->height, destinationHeight, column);
	}
	release_screen();
	readkey();
}

/**
 * Main function.
 */
int main(int argc, const char *argv[]) {
	
	initializeAllegroStuff();
	zoneAllocatorInitialize();
	initializeWadFile(WAD_FILE_PATH);
	initializeStaticGraphicsData();
	systemGraphicsSetPalette((unsigned char *)getWadFileLumpContentsByName("PLAYPAL"));

	acquire_screen();
	clear(screen);
	drawPatchOnScreen(320, 240, getWadFileLumpContentsByName("SARGA2A8"));
	release_screen();
	readkey();

	testScaledPatchName = "DOOR2_4";
	testScaledPatch(10);
	testScaledPatch(50);
	testScaledPatch(100);
	testScaledPatch(200);
	testScaledPatch(400);

	testScaledPatchName = "W113_1";
	testScaledPatch(10);
	testScaledPatch(50);
	testScaledPatch(100);
	testScaledPatch(200);
	testScaledPatch(400);

	testScaledTextureName = "SW1BRCOM";
	testScaledTexture(10);
	testScaledTexture(50);
	testScaledTexture(100);
	testScaledTexture(200);
	testScaledTexture(400);
	
	return 0;
}
END_OF_MAIN()
