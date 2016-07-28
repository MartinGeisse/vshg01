/*
 *  ShowFlatsMain.c
 *  baselib
 *
 *  Created by Martin on 8/12/10.
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
#include "TestMainCommon.h"

void drawFlat(int flatIndex) {
	int baseY = (flatIndex / 10) * 64, baseX = (flatIndex % 10) * 64;
	int x, y;
	unsigned char *flatData = (unsigned char *)getWadFileLumpContentsByIndex(getTranslatedLumpFlatIndex(flatIndex));
	for (x=0; x<64; x++) {
		for (y=0; y<64; y++) {
			systemGraphicsDrawPixel(baseX + x, baseY + y, flatData[y * 64 + x]);
		}
	}
}

void drawColorMappedFlat(int flatIndex, int colorMapIndex) {
	unsigned char *colorMap = getColorMap(colorMapIndex);
	int baseY = (flatIndex / 10) * 64, baseX = (flatIndex % 10) * 64;
	int x, y;
	unsigned char *flatData = (unsigned char *)getWadFileLumpContentsByIndex(getTranslatedLumpFlatIndex(flatIndex));
	for (x=0; x<64; x++) {
		for (y=0; y<64; y++) {
			unsigned char original = flatData[y * 64 + x];
			unsigned char mapped = colorMap[original];
			systemGraphicsDrawPixel(baseX + x, baseY + y, mapped);
		}
	}
}

/**
 * Main function.
 */
int main(int argc, const char *argv[]) {
	int i, j;
	char description[256];

	initializeAllegroStuff();
	zoneAllocatorInitialize();
	initializeWadFile(WAD_FILE_PATH);
	initializeStaticGraphicsData();
	systemGraphicsSetPalette((unsigned char *)getWadFileLumpContentsByName("PLAYPAL"));

	clearScreen();
	for (i=0; i<getFlatCount(); i++) {
		drawFlat(i);
	}
	printDescription("normal");
	readkey();

	for (i=0; i<getColorMapCount(); i++) {
		clearScreen();
		for (j=0; j<getFlatCount(); j++) {
			drawColorMappedFlat(j, i);
		}
		sprintf(description, "color map %d / %d", i, getColorMapCount());
		printDescription(description);
		readkey();
	}
	
	return 0;
}
END_OF_MAIN()
