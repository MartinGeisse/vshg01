/*
 *  AutomapMain.c
 *  baselib
 *
 *  Created by Martin on 8/23/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

#include <allegro.h>

#include "Common.h"
#include "FixedPointNumber.h"
#include "Angle.h"
#include "BoundingBox.h"
#include "ZoneAllocator.h"
#include "WadFile.h"
#include "SplitLine.h"
#include "MapData.h"
#include "SystemGraphics.h"
#include "StaticGraphicsData.h"
#include "StaticGameData.h"
#include "TestMainCommon.h"
#include "SystemKeyboard.h"
#include "Automap.h"

static volatile int done = 0;
FixedPointNumber playerX;
FixedPointNumber playerY;
Angle playerAngle;
static int counter = 0;

int mapChangeKeys[9] = {
	KEY_1,
	KEY_2,
	KEY_3,
	KEY_4,
	KEY_5,
	KEY_6,
	KEY_7,
	KEY_8,
	KEY_9,
};

void timerCallback() {
	int i;
	
	if (key[KEY_ESC]) {
		done = 1;
	}
	
	for (i=0; i<9; i++) {
		if (key[mapChangeKeys[i]]) {
			loadMap(i + 1);
			prepareAutomap();
		}
	}
	
	drawAutomap();
	sampleKeyboard();
	runAutomapTicker();
	
	playerX = FIXED_POINT_NUMBER_ONE * 2000 + 300 * cosForAngle(counter * (ANGLE_90 / 30));
	playerY = FIXED_POINT_NUMBER_ONE * (-3000) + 300 * sinForAngle(counter * (ANGLE_90 / 30));
	playerAngle = counter * (ANGLE_90 / 100);
	counter++;
}

/**
 * Main function.
 */
int main(int argc, const char *argv[]) {
	
	testMainScreenWidth = 320;
	testMainScreenHeight = 200;
	initializeAllegroStuff();
	zoneAllocatorInitialize();
	initializeWadFile(WAD_FILE_PATH);
	initializeStaticGraphicsData();
	initializeStaticGameData();
	systemGraphicsSetPalette((unsigned char *)getWadFileLumpContentsByName("PLAYPAL"));
	
	loadMap(1);
	prepareAutomap();
	
	install_timer();
	install_int(&timerCallback, 10);
	while (!done);
	
	return 0;
}
END_OF_MAIN()

