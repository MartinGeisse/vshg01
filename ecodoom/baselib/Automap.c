/*
 *  Automap.c
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
#include "SystemKeyboard.h"
#include "SystemDebug.h"
#include "Automap.h"

/** color definitions **/
#define PLAYER_ARROW_COLOR			209
#define ONE_SIDED_WALL_COLOR		176
#define FLOOR_CHANGE_COLOR			64
#define CEILING_CHANGE_COLOR		231
#define COMPUTER_MAP_WALL_COLOR		99
#define TELEPORTER_EDGE_COLOR		184

/** TODO: remove **/
extern FixedPointNumber playerX;
extern FixedPointNumber playerY;
extern Angle playerAngle;

/** this structure is used for automap data **/
struct AutomapData {

	/** this flag is 1 for overview mode, 0 for normal panning/scaling mode **/
	int overview;

	/** this flag is 1 to follow the player's position, 0 for explicit positioning **/
	int followPlayer;

	/** minimal x coordinate allowed for explicit positioning **/
	FixedPointNumber minExplicitX;

	/** minimal y coordinate allowed for explicit positioning **/
	FixedPointNumber minExplicitY;

	/** maximal x coordinate allowed for explicit positioning **/
	FixedPointNumber maxExplicitX;
	
	/** maximal y coordinate allowed for explicit positioning **/
	FixedPointNumber maxExplicitY;
	
	/** x coordinate of the center of the map **/
	FixedPointNumber mapCenterX;
	
	/** x coordinate of the center of the map **/
	FixedPointNumber mapCenterY;

	/** the scaling factor needed for overview mode **/
	FixedPointNumber overviewScalingFactor;

	/** x coordinate of the explicit position **/
	FixedPointNumber explicitPositionX;

	/** x coordinate of the explicit position **/
	FixedPointNumber explicitPositionY;
	
	/** the map scaling factor to determine screen distances from map distances **/
	FixedPointNumber scalingFactor;
	

};

/** the automap data **/
static struct AutomapData automapData;

/**
 * See header file for information.
 */
void initializeAutomap() {
}

/**
 * See header file for information.
 */
void shutdownAutomap() {
}

/**
 * See header file for information.
 */
void prepareAutomap() {
	int i;
	FixedPointNumber minX = FIXED_POINT_NUMBER_MAX_VALUE;
	FixedPointNumber maxX = FIXED_POINT_NUMBER_MIN_VALUE;
	FixedPointNumber minY = FIXED_POINT_NUMBER_MAX_VALUE;
	FixedPointNumber maxY = FIXED_POINT_NUMBER_MIN_VALUE;
	FixedPointNumber sizeX, sizeY, commonSize;
	
	for (i=0; i<currentMapData.vertexCount; i++) {
		struct Vertex *vertex = currentMapData.vertices + i;
		minX = (vertex->x < minX) ? vertex->x : minX;
		maxX = (vertex->x > maxX) ? vertex->x : maxX;
		minY = (vertex->y < minY) ? vertex->y : minY;
		maxY = (vertex->y > maxY) ? vertex->y : maxY;
	}
	if (minX > maxX || minY > maxY) {
		systemFatalError("cannot determine max extents by vertex positions");
	}
	
	automapData.overview = 0;
	automapData.followPlayer = 0;
	automapData.mapCenterX = automapData.explicitPositionX = (minX + maxX) / 2;
	automapData.mapCenterY = automapData.explicitPositionY = (minY + maxY) / 2;
	automapData.scalingFactor = FIXED_POINT_NUMBER_ONE;
	automapData.minExplicitX = minX - 100 * FIXED_POINT_NUMBER_ONE;
	automapData.minExplicitY = minY - 100 * FIXED_POINT_NUMBER_ONE;
	automapData.maxExplicitX = maxX + 100 * FIXED_POINT_NUMBER_ONE;
	automapData.maxExplicitY = maxY + 100 * FIXED_POINT_NUMBER_ONE;

	sizeX = maxX - minX;
	sizeY = maxY - minY;
	commonSize = (sizeX > sizeY) ? sizeX : sizeY;
	automapData.overviewScalingFactor = fixedPointNumberDivide(300 << FIXED_POINT_NUMBER_FRACTIONAL_BITS, commonSize);

}

/**
 * See header file for information.
 */
void runAutomapTicker() {
	FixedPointNumber panningAmount = fixedPointNumberDivide(4 << FIXED_POINT_NUMBER_FRACTIONAL_BITS, automapData.scalingFactor);
	FixedPointNumber scalingAmount = FIXED_POINT_NUMBER_ONE + 2000;
	
	/**
	 * If we are not currently following the player, we can move the map around.
	 */
	if (!automapData.followPlayer) {
		if (getCurrentKeyState(DOOM_KEY_CODE_UP) && automapData.explicitPositionY < automapData.maxExplicitY) {
			automapData.explicitPositionY += panningAmount;
		}
		if (getCurrentKeyState(DOOM_KEY_CODE_DOWN) && automapData.explicitPositionY > automapData.minExplicitY) {
			automapData.explicitPositionY -= panningAmount;
		}
		if (getCurrentKeyState(DOOM_KEY_CODE_LEFT) && automapData.explicitPositionX > automapData.minExplicitX) {
			automapData.explicitPositionX -= panningAmount;
		}
		if (getCurrentKeyState(DOOM_KEY_CODE_RIGHT) && automapData.explicitPositionX < automapData.maxExplicitX) {
			automapData.explicitPositionX += panningAmount;
		}
	}

	/**
	 * The remaining keys work regardles of follow mode.
	 */
	if (getCurrentKeyState(DOOM_KEY_CODE_AUTOMAP_ZOOM_IN) && automapData.scalingFactor < FIXED_POINT_NUMBER_ONE) {
		automapData.scalingFactor = fixedPointNumberMultiply(automapData.scalingFactor, scalingAmount);
	}
	if (getCurrentKeyState(DOOM_KEY_CODE_AUTOMAP_ZOOM_OUT) && automapData.scalingFactor > (automapData.overviewScalingFactor >> 1)) {
		automapData.scalingFactor = fixedPointNumberDivide(automapData.scalingFactor, scalingAmount);
	}
	if (getKeyJustPressed(DOOM_KEY_CODE_AUTOMAP_TOGGLE_FOLLOW_PLAYER)) {
		automapData.followPlayer = !automapData.followPlayer;
	}
	if (getKeyJustPressed(DOOM_KEY_CODE_AUTOMAP_TOGGLE_OVERVIEW)) {
		automapData.overview = !automapData.overview;
	}
			
}

/**
 * Transforms the x coordinate from the map coordinate system to the screen coordinate system.
 */
static int transformX(FixedPointNumber x) {
	FixedPointNumber referencePointX = automapData.overview ? automapData.mapCenterX : automapData.followPlayer ? playerX : automapData.explicitPositionX;
	FixedPointNumber scalingFactor = automapData.overview ? automapData.overviewScalingFactor : automapData.scalingFactor;
	return 160 + (fixedPointNumberMultiply(x - referencePointX, scalingFactor) >> 16);
}

/**
 * Transforms the y coordinate from the map coordinate system to the screen coordinate system.
 */
static int transformY(FixedPointNumber y) {
	FixedPointNumber referencePointY = automapData.overview ? automapData.mapCenterY : automapData.followPlayer ? playerY : automapData.explicitPositionY;
	FixedPointNumber scalingFactor = automapData.overview ? automapData.overviewScalingFactor : automapData.scalingFactor;
	return 100 - (fixedPointNumberMultiply(y - referencePointY, scalingFactor) >> 16);
}

/**
 * Transforms a point from the player's own coordinate system into screen coordinates.
 */
static void transformPlayerArrowPoint(FixedPointNumber sourceX, FixedPointNumber sourceY, int *destinationX, int *destinationY) {
	
	/** determine sine and cosine of the player angle **/
	FixedPointNumber playerAngleSine = sinForAngle(playerAngle);
	FixedPointNumber playerAngleCosine = cosForAngle(playerAngle);
	
	/** determine map coordinates **/
	FixedPointNumber mapX = playerX + fixedPointNumberMultiply(playerAngleCosine, sourceX) - fixedPointNumberMultiply(playerAngleSine, sourceY);
	FixedPointNumber mapY = playerY + fixedPointNumberMultiply(playerAngleSine, sourceX) + fixedPointNumberMultiply(playerAngleCosine, sourceY);
	
	/** use regular transformation **/
	*destinationX = transformX(mapX);
	*destinationY = transformY(mapY);
	
}

/**
 * Draws a line of the player arrow, with the coordinates specified in the player's own coordinate system.
 */
static void drawPlayerArrowLine(FixedPointNumber playerX1, FixedPointNumber playerY1, FixedPointNumber playerX2, FixedPointNumber playerY2) {
	int screenX1, screenY1, screenX2, screenY2;
	transformPlayerArrowPoint(playerX1, playerY1, &screenX1, &screenY1);
	transformPlayerArrowPoint(playerX2, playerY2, &screenX2, &screenY2);
	systemGraphicsDrawLine(screenX1, screenY1, screenX2, screenY2, PLAYER_ARROW_COLOR);
}

/**
 * See header file for information.
 */
void drawAutomap() {
	int i;
	
	/** clear the screen to black **/
	clearScreen();
	
	/** draw lines **/
	for (i=0; i<currentMapData.linedefCount; i++) {
		struct Linedef *linedef = currentMapData.linedefs + i;
		int x1 = transformX(linedef->startVertex->x);
		int y1 = transformY(linedef->startVertex->y);
		int x2 = transformX(linedef->endVertex->x);
		int y2 = transformY(linedef->endVertex->y);
		systemGraphicsDrawLine(x1, y1, x2, y2, (linedef->flags & LINE_FLAG_TWO_SIDED) ? COMPUTER_MAP_WALL_COLOR : ONE_SIDED_WALL_COLOR);
		
/*
 
 if (lines[i].flags & ML_MAPPED))
 {
	if ((lines[i].flags & LINE_NEVERSEE))
		continue;
	if (!lines[i].backsector)
	{
		AM_drawMline(&l, ONE_SIDED_WALL_COLOR);
	}
	else
	{
		if (lines[i].special == 39)
		{ // teleporters
			AM_drawMline(&l, TELEPORTER_EDGE_COLOR);
		}
		else if (lines[i].flags & ML_SECRET) // secret door
		{
			AM_drawMline(&l, ONE_SIDED_WALL_COLOR);
		}
		else if (lines[i].backsector->floorheight != lines[i].frontsector->floorheight) {
			AM_drawMline(&l, FLOOR_CHANGE_COLOR); // floor level change
		} else if (lines[i].backsector->ceilingheight != lines[i].frontsector->ceilingheight) {
			AM_drawMline(&l, CEILING_CHANGE_COLOR); // ceiling level change
		}
	}
 }
 else if (plr->powers[pw_allmap])
 {
 if (!(lines[i].flags & LINE_NEVERSEE)) AM_drawMline(&l, COMPUTER_MAP_WALL_COLOR);
 }
 */
		
	}

	/** draw player arrow **/
	{
		int unit = 50 << FIXED_POINT_NUMBER_FRACTIONAL_BITS;
		int step = unit / 4;
		drawPlayerArrowLine(-unit + step, 0, +unit, 0);
		drawPlayerArrowLine(+unit - step, -step, +unit, 0);
		drawPlayerArrowLine(+unit - step, +step, +unit, 0);
		drawPlayerArrowLine(-unit + step, 0, -unit, -step);
		drawPlayerArrowLine(-unit + step, 0, -unit, +step);
	}
	
	/** draw crosshair **/
	line(screen, 155, 100, 165, 100, makecol(0, 255, 0));
	line(screen, 160, 95, 160, 105, makecol(0, 255, 0));

}

/**
 * See header file for information.
 */
int getAutomapMovementKeysCaptured() {
	return !automapData.followPlayer;
}
