/*
 *  ShowMapMain.c
 *  baselib
 *
 *  Created by Martin on 8/17/10.
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
#include "SystemDebug.h"
#include "TestMainCommon.h"

// note: viewRegion is the range covered by the screen height
static FixedPointNumber viewX;
static FixedPointNumber viewY;
static FixedPointNumber viewRegion;
static volatile int done = 0;

#define TRANSFORM(x, y)			transformX(x), transformY(y)

int transformX(FixedPointNumber x) {
	// note: scale isotropically -- use 480 for x scaling too
	return 320 + ((fixedPointNumberDivide(x - viewX, viewRegion) * 480) >> 16);
}

int transformY(FixedPointNumber y) {
	return 240 - ((fixedPointNumberDivide(y - viewY, viewRegion) * 480) >> 16);
}

void switchToMap(int mapNumber) {
	FixedPointNumber minX = FIXED_POINT_NUMBER_MAX_VALUE;
	FixedPointNumber maxX = FIXED_POINT_NUMBER_MIN_VALUE;
	FixedPointNumber minY = FIXED_POINT_NUMBER_MAX_VALUE;
	FixedPointNumber maxY = FIXED_POINT_NUMBER_MIN_VALUE;
	int i;

	loadMap(mapNumber);
	for (i=0; i<currentMapData.thingCount; i++) {
		struct Thing *thing = currentMapData.things + i;
		minX = (thing->x < minX) ? thing->x : minX;
		maxX = (thing->x > maxX) ? thing->x : maxX;
		minY = (thing->y < minY) ? thing->y : minY;
		maxY = (thing->y > maxY) ? thing->y : maxY;
	}
	if (minX > maxX || minY > maxY) {
		systemFatalError("cannot determine max extents by thing positions");
	}
	
	// not entirely correct, but at the worst, makes the initial resion a big too large
	// since it fits the region width into 480, not 640, pixels
	viewX = (minX + maxX) / 2;
	viewY = (minY + maxY) / 2;
	viewRegion = ((maxX - minX) > (maxY - minY)) ? (maxX - minX) : (maxY - minY);
}

void drawMap() {
	int i;
	char buf[256];
	int black = makecol(0, 0, 0);
	int darkRed = makecol(128, 0, 0);
	// int darkGreen = makecol(0, 128, 0);
	int darkBlue = makecol(0, 0, 128);
	int red = makecol(255, 0, 0);
	int green = makecol(0, 255, 0);
	// int blue = makecol(0, 0, 255);
	int white = makecol(255, 255, 255);
	
	clearScreen();
	
	/** draw things **/
	for (i=0; i<currentMapData.thingCount; i++) {
		struct Thing *thing = currentMapData.things + i;
		int x = transformX(thing->x);
		int y = transformY(thing->y);
		
		circle(screen, x, y, 5, green);
		sprintf(buf, "%d", thing->type);
		textout_ex(screen, font, buf, x + 5, y + 5, red, black);
	}
	
	/** draw lines **/
	for (i=0; i<currentMapData.linedefCount; i++) {
		struct Linedef *linedef = currentMapData.linedefs + i;
		int x1 = transformX(linedef->startVertex->x);
		int y1 = transformY(linedef->startVertex->y);
		int x2 = transformX(linedef->endVertex->x);
		int y2 = transformY(linedef->endVertex->y);
		line(screen, x1, y1, x2, y2, (linedef->flags & LINE_FLAG_TWO_SIDED) ? darkRed : white);
	}
	
	/** draw vertices **/
	for (i=0; i<currentMapData.vertexCount; i++) {
		struct Vertex *vertex = currentMapData.vertices + i;
		int x = transformX(vertex->x);
		int y = transformY(vertex->y);
		rect(screen, x - 3, y - 3, x + 3, y + 3, darkBlue);
	}
	
}

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
	int delta, i;
	
	delta = key[KEY_LEFT] ? -1 : key[KEY_RIGHT] ? 1 : 0;
	viewX += delta * (viewRegion / 20);
	
	delta = key[KEY_UP] ? 1 : key[KEY_DOWN] ? -1 : 0;
	viewY += delta * (viewRegion / 20);

	if (key[KEY_Q]) {
		viewRegion = 0.9 * viewRegion;
	}
	
	if (key[KEY_A]) {
		viewRegion = 1.1 * viewRegion;
		if (viewRegion < 0) {
			viewRegion = FIXED_POINT_NUMBER_MAX_VALUE;
		}
	}
	
	if (key[KEY_ESC]) {
		done = 1;
	}
	
	for (i=0; i<9; i++) {
		if (key[mapChangeKeys[i]]) {
			switchToMap(i + 1);
		}
	}
	
	drawMap();
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

	switchToMap(1);
	install_timer();
	install_int(&timerCallback, 10);
	while (!done);
	
	return 0;
}
END_OF_MAIN()
