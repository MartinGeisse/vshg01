/*
 *  BspTestMain.c
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

FixedPointNumber playerX;
FixedPointNumber playerY;
FixedPointNumber playerZ;
Angle playerAngle;
int waitForKeyDuringBsp;
int showSplitLines;

#define NEAR_PLANE_DISTANCE			FIXED_POINT_NUMBER_ONE
#define COMPUTER_MAP_WALL_COLOR		99
#define ONE_SIDED_WALL_COLOR		176

/** the world-to-view transformation **/
static FixedPointNumber worldToViewTranslation[2];
static FixedPointNumber worldToViewTransformationMatrix[4];

/**
 * Transforms the specified world-coordinate vertex to view coordinates and
 * stores it in the structure to which viewVertex points.
 */
static void transformWorldToView(struct Vertex *worldVertex, struct Vertex *viewVertex) {
	FixedPointNumber translatedX = worldVertex->x + worldToViewTranslation[0];
	FixedPointNumber translatedY = worldVertex->y + worldToViewTranslation[1];
	viewVertex->x = fixedPointNumberMultiply(worldToViewTransformationMatrix[0], translatedX) + fixedPointNumberMultiply(worldToViewTransformationMatrix[1], translatedY);
	viewVertex->y = fixedPointNumberMultiply(worldToViewTransformationMatrix[2], translatedX) + fixedPointNumberMultiply(worldToViewTransformationMatrix[3], translatedY);
}

/**
 * Handles a single segment.
 */
static void drawSegment(struct Segment *segment) {
	struct Vertex viewStartVertex, viewEndVertex;
	int x1, x2, y1, y2;
	
	/** transform vertices to view coordinates **/
	transformWorldToView(segment->startVertex, &viewStartVertex);
	transformWorldToView(segment->endVertex, &viewEndVertex);
	
	x1 = 160 + (viewStartVertex.x >> (FIXED_POINT_NUMBER_FRACTIONAL_BITS + 4));
	x2 = 160 + (viewEndVertex.x >> (FIXED_POINT_NUMBER_FRACTIONAL_BITS + 4));
	y1 = 100 - (viewStartVertex.y >> (FIXED_POINT_NUMBER_FRACTIONAL_BITS + 4));
	y2 = 100 - (viewEndVertex.y >> (FIXED_POINT_NUMBER_FRACTIONAL_BITS + 4));
	line(screen, x1, y1, x2, y2, (segment->line->flags & LINE_FLAG_TWO_SIDED) ? makecol(128, 128, 128) : makecol(255, 0, 0));

}

/**
 * Visualizes the split line of a node.
 */
static void drawNode(struct Node *node, int color) {
	struct Vertex worldStartVertex, worldEndVertex;
	struct Vertex viewStartVertex, viewEndVertex;
	int x1, x2, y1, y2;
	
	/** prepare vertices in world coordinates **/
	worldStartVertex.x = node->partitionLineOriginX - 100 * node->partitionLineDeltaX;
	worldStartVertex.y = node->partitionLineOriginY - 100 * node->partitionLineDeltaY;
	worldEndVertex.x = node->partitionLineOriginX + 100 * node->partitionLineDeltaX;
	worldEndVertex.y = node->partitionLineOriginY + 100 * node->partitionLineDeltaY;
	
	/** transform vertices to view coordinates **/
	transformWorldToView(&worldStartVertex, &viewStartVertex);
	transformWorldToView(&worldEndVertex, &viewEndVertex);
	
	x1 = 160 + (viewStartVertex.x >> (FIXED_POINT_NUMBER_FRACTIONAL_BITS + 4));
	x2 = 160 + (viewEndVertex.x >> (FIXED_POINT_NUMBER_FRACTIONAL_BITS + 4));
	y1 = 100 - (viewStartVertex.y >> (FIXED_POINT_NUMBER_FRACTIONAL_BITS + 4));
	y2 = 100 - (viewEndVertex.y >> (FIXED_POINT_NUMBER_FRACTIONAL_BITS + 4));
	line(screen, x1, y1, x2, y2, color);
	
}

static void drawSubsector(int subsectorIndex) {
	struct Subsector *subsector = currentMapData.subsectors + subsectorIndex;
	int i;
	for (i=0; i<subsector->segmentCount; i++) {
		drawSegment(subsector->segments + i);
	}
	if (waitForKeyDuringBsp) {
		while (!key[KEY_SPACE] && !key[KEY_P]);
		while (key[KEY_SPACE] && !key[KEY_P]);
	}
}

static int checkBoundingBoxVisible(struct BoundingBox *box) {
	return 1;
}

/**
 * Draws the node or subsector that is specified by bits 14..0 of the index
 * argument. Bit 15 decides whether the index selects a node (bit cleared)
 * or subsector (bit set).
 */
static void drawBspNodeOrSubsector(int index) {
	
	if (index & 0x8000) {
		drawSubsector(index & 0x7fff);
	} else {
		/** render child nodes recursively **/
		struct Node *node = currentMapData.nodes + index;
		
		if (getSplitLineSide(&node->splitLine, playerX, playerY)) {
//			if (showSplitLines) {
//				drawNode(node, makecol(0, 255, 0));
//			}
			drawBspNodeOrSubsector(node->leftChild);
//			if (showSplitLines) {
//				drawNode(node, makecol(0, 0, 255));
//			}
			if (checkBoundingBoxVisible(&node->rightChildBoundingBox)) {
				drawBspNodeOrSubsector(node->rightChild);
			}
//			if (showSplitLines) {
//				drawNode(node, makecol(0, 0, 0));
//			}
		} else {
//			if (showSplitLines) {
//				drawNode(node, makecol(0, 255, 0));
//			}
			drawBspNodeOrSubsector(node->rightChild);
//			if (showSplitLines) {
//				drawNode(node, makecol(0, 0, 255));
//			}
			if (checkBoundingBoxVisible(&node->leftChildBoundingBox)) {
				drawBspNodeOrSubsector(node->leftChild);
			}
//			if (showSplitLines) {
//				drawNode(node, makecol(0, 0, 0));
//			}
		}
	}
}

/**
 * See header file for information.
 */
void drawFirstPersonView() {
	printf("----------------------------------------\n");
	clear(screen);
	line(screen, 155, 100, 165, 100, makecol(255, 255, 255));
	line(screen, 162, 103, 165, 100, makecol(255, 255, 255));
	line(screen, 162, 97, 165, 100, makecol(255, 255, 255));
	
	/** prepare world-to-view coordinate transformation **/
	FixedPointNumber playerAngleSine = sinForAngle(playerAngle);
	FixedPointNumber playerAngleCosine = cosForAngle(playerAngle);
	worldToViewTransformationMatrix[0] = playerAngleCosine;
	worldToViewTransformationMatrix[1] = playerAngleSine;
	worldToViewTransformationMatrix[2] = -playerAngleSine;
	worldToViewTransformationMatrix[3] = playerAngleCosine;
	worldToViewTranslation[0] = -playerX;
	worldToViewTranslation[1] = -playerY;
	
	/** draw the root node **/
	if (currentMapData.nodeCount == 0) {
		/** special case of a map with a single subsector **/
		drawSubsector(0);
	} else {
		/** the last node in the node list is the root node **/
		drawBspNodeOrSubsector(currentMapData.nodeCount - 1);
	}
	
}

// ------------------------------------------------------------------------------------------------------------

void drawRandomSplitLineTest() {
	int i;
	struct Node *node;

	/** draw the map **/
	drawFirstPersonView();

	/** select a split line randomly **/
	node = currentMapData.nodes + rand() % currentMapData.nodeCount;
	
	/** draw the split line **/
	drawNode(node, makecol(0, 255, 255));
	
	/** classify some random points **/
	for (i = 0; i<1000; i++) {
		struct Vertex worldVertex, viewVertex;
		int x, y, color;
		worldVertex.x = playerX + ((rand() % 5000) << FIXED_POINT_NUMBER_FRACTIONAL_BITS) - (2500 << FIXED_POINT_NUMBER_FRACTIONAL_BITS);
		worldVertex.y = playerY + ((rand() % 5000) << FIXED_POINT_NUMBER_FRACTIONAL_BITS) - (2500 << FIXED_POINT_NUMBER_FRACTIONAL_BITS);
		transformWorldToView(&worldVertex, &viewVertex);
		x = 160 + (viewVertex.x >> (FIXED_POINT_NUMBER_FRACTIONAL_BITS + 4));
		y = 100 - (viewVertex.y >> (FIXED_POINT_NUMBER_FRACTIONAL_BITS + 4));
		
		color = getSplitLineSide(&node->splitLine, worldVertex.x, worldVertex.y) ? makecol(0, 0, 255) : makecol(0, 255, 0);
		line(screen, x-3, y-3, x+3, y+3, color);
		line(screen, x-3, y+3, x+3, y-3, color);
	}
	
	while (key[KEY_S]);
}

// ------------------------------------------------------------------------------------------------------------

static volatile int timerTick = 0;

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
	timerTick = 1;
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
			waitForKeyDuringBsp = 1;
			showSplitLines = 1;
			drawFirstPersonView();
		}
		if (key[KEY_S]) {
			waitForKeyDuringBsp = 0;
			showSplitLines = 0;
			drawRandomSplitLineTest();
		}
		for (i=0; i<9; i++) {
			if (key[mapChangeKeys[i]]) {
				loadMap(i + 1);
				prepareAutomap();
			}
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
			playerZ += FIXED_POINT_NUMBER_ONE * floating * multiplier * 5;
		}
		
		/** regular work **/
		drawAutomap();
		runAutomapTicker();
		
	}
	
	return 0;
}
END_OF_MAIN()

