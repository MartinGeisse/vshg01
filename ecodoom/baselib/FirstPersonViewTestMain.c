/*
 *  FirstPersonViewTest.c
 *  baselib
 *
 *  Created by Martin on 8/29/10.
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
#include "FirstPersonView.h"
#include "Automap.h"

static volatile int timerTick = 0;
FixedPointNumber playerX;
FixedPointNumber playerY;
FixedPointNumber playerZ;
Angle playerAngle;

static int mapChangeKeys[9] = {
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
	timerTick = 1;
}

static void showFirstPersonView() {
	clear(screen);
	drawFirstPersonView();
	while (key[KEY_ENTER]);
	while (!key[KEY_ENTER]);
	while (key[KEY_ENTER]);
}

/**
 * Main function.
 */
int main(int argc, const char *argv[]) {
	int i, done;
	
	testMainScreenWidth = 320;
	testMainScreenHeight = 200;
	initializeAllegroStuff();
	zoneAllocatorInitialize();
	systemGraphicsInitialize();
	initializeWadFile(WAD_FILE_PATH);
	initializeStaticGraphicsData();
	initializeStaticGameData();
	systemGraphicsSetPalette((unsigned char *)getWadFileLumpContentsByName("PLAYPAL"));
	
	loadMap(1);
	prepareAutomap();
	
	install_timer();
	install_int(&timerCallback, 10);
	
	done = 0;
	while (!done) {
		
		/** wait for timer tick **/
		while (!timerTick);
		timerTick = 0;
		
		/** update key states **/
		sampleKeyboard();

		/** handle control keys **/
		if (key[KEY_ESC]) {
			done = 1;
		}
		if (key[KEY_ENTER]) {
			showFirstPersonView();
		}
		
		/** handle map change keys **/
		for (i=0; i<9; i++) {
			if (key[mapChangeKeys[i]]) {
				loadMap(i + 1);
				prepareAutomap();
			}
		}
		
		/** handle movement keys **/
		if (!getAutomapMovementKeysCaptured()) {
			int multiplier = key[KEY_LSHIFT] ? 5 : 1;
			Angle angle = key[KEY_LEFT] ? 1 : key[KEY_RIGHT] ? -1 : 0;
			int movement = key[KEY_UP] ? 1 : key[KEY_DOWN] ? -1 : 0;
			int floating = key[KEY_Q] ? 1 : key[KEY_A] ? -1 : 0;
			
			playerAngle += angle << 25;
			playerX += cosForAngle(playerAngle) * movement * multiplier * 5;
			playerY += sinForAngle(playerAngle) * movement * multiplier * 5;
			playerZ += FIXED_POINT_NUMBER_ONE * floating * multiplier * 3;
			
			if (floating != 0 || key[KEY_U]) {
				clear(screen);
				drawFirstPersonView();
				while (!timerTick);
				timerTick = 0;
				while (!timerTick);
				timerTick = 0;
			}
		}
		
		/** regular work **/
		drawAutomap();
		runAutomapTicker();
		
	}
	
	return 0;
}
END_OF_MAIN()

