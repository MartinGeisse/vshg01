/*
 *  MapData.c
 *  baselib
 *
 *  Created by Martin on 8/17/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

#include <math.h>
#include "Common.h"
#include "FixedPointNumber.h"
#include "Angle.h"
#include "BoundingBox.h"
#include "ZoneAllocator.h"
#include "WadFile.h"
#include "SystemDebug.h"
#include "StaticGraphicsData.h"
#include "SplitLine.h"
#include "Thinker.h"
#include "MapData.h"

////////////////////////////////////////////////////////////////
// TODO: endian-ness conversion: use unsigned where needed
////////////////////////////////////////////////////////////////

#define PREPARE_MAP_LUMP_LOADING(lumpType) \
	currentLumpIndex = markerLumpIndex + (lumpType); \
	currentLumpSize = getWadFileLumpSize(currentLumpIndex); \
	currentLumpStart = currentLumpReadPointer = getWadFileLumpContentsByIndex(currentLumpIndex);

/**
 * See header file for information.
 */
struct CurrentMapData currentMapData;

/**
 * See header file for information.
 */
void loadMap(int mapNumber) {
	char markerLumpName[9] = "E1M?";
	int markerLumpIndex;
	int currentLumpIndex;
	int currentLumpSize;
	unsigned char *currentLumpStart;
	unsigned char *currentLumpReadPointer;
	int i;
	
	
	/** determine and locate the map marker lump **/
	if (mapNumber < 1 || mapNumber > 9) {
		systemFatalError("invalid map number: %d", mapNumber);
	}
	markerLumpName[3] = mapNumber + '0';
	markerLumpIndex = findWadFileLump(markerLumpName);
	
	/** dispose of the old map, if any **/
	disposeMap();

	/** load things **/
	PREPARE_MAP_LUMP_LOADING(MAP_DATA_LUMP_THINGS);
	currentMapData.thingCount = currentLumpSize / 10;
	currentMapData.things = zoneAllocatorAllocate(currentMapData.thingCount * sizeof(struct Thing), LEVEL_STATIC_ALLOCATION_TAG, NULL);
	for (i=0; i<currentMapData.thingCount; i++) {
		currentMapData.things[i].x = fromLittleEndian16(DESERIALIZE(currentLumpReadPointer, 0, short)) << FIXED_POINT_NUMBER_FRACTIONAL_BITS;
		currentMapData.things[i].y = fromLittleEndian16(DESERIALIZE(currentLumpReadPointer, 2, short)) << FIXED_POINT_NUMBER_FRACTIONAL_BITS;
		currentMapData.things[i].angle = (fromLittleEndian16(DESERIALIZE(currentLumpReadPointer, 4, short)) / 45) * ANGLE_45;
		currentMapData.things[i].doomedTypeIndex = fromLittleEndian16(DESERIALIZE(currentLumpReadPointer, 6, unsigned short));
		currentMapData.things[i].flags = fromLittleEndian16(DESERIALIZE(currentLumpReadPointer, 8, unsigned short));
		currentLumpReadPointer += 10;
		// TODO: P_SpawnMapThing (mt); -- turn into game object
	}
	zoneAllocatorDispose(currentLumpStart);

	/** load vertices **/
	PREPARE_MAP_LUMP_LOADING(MAP_DATA_LUMP_VERTEXES);
	currentMapData.vertexCount = currentLumpSize / 4;
	currentMapData.vertices = zoneAllocatorAllocate(currentMapData.vertexCount * sizeof(struct Vertex), LEVEL_STATIC_ALLOCATION_TAG, NULL);
	for (i=0; i<currentMapData.vertexCount; i++) {
		currentMapData.vertices[i].x = fromLittleEndian16(DESERIALIZE(currentLumpReadPointer, 0, short)) << FIXED_POINT_NUMBER_FRACTIONAL_BITS;
		currentMapData.vertices[i].y = fromLittleEndian16(DESERIALIZE(currentLumpReadPointer, 2, short)) << FIXED_POINT_NUMBER_FRACTIONAL_BITS;
		currentLumpReadPointer += 4;
	}
	zoneAllocatorDispose(currentLumpStart);

	/** load sectors **/
	PREPARE_MAP_LUMP_LOADING(MAP_DATA_LUMP_SECTORS);
	currentMapData.sectorCount = currentLumpSize / 26;
	currentMapData.sectors = zoneAllocatorAllocate(currentMapData.sectorCount * sizeof(struct Sector), LEVEL_STATIC_ALLOCATION_TAG, NULL);
	for (i=0; i<currentMapData.sectorCount; i++) {
		currentMapData.sectors[i].floorHeight = fromLittleEndian16(DESERIALIZE(currentLumpReadPointer, 0, short)) << FIXED_POINT_NUMBER_FRACTIONAL_BITS;
		currentMapData.sectors[i].ceilingHeight = fromLittleEndian16(DESERIALIZE(currentLumpReadPointer, 2, short)) << FIXED_POINT_NUMBER_FRACTIONAL_BITS;
		currentMapData.sectors[i].floorFlatIndex = getFlatIndexForName((char *)(currentLumpReadPointer + 4));
		currentMapData.sectors[i].ceilingFlatIndex = getFlatIndexForName((char *)(currentLumpReadPointer + 12));
		currentMapData.sectors[i].lightLevel = fromLittleEndian16(DESERIALIZE(currentLumpReadPointer, 20, short));
		currentMapData.sectors[i].type = fromLittleEndian16(DESERIALIZE(currentLumpReadPointer, 22, unsigned short));
		currentMapData.sectors[i].sectorTag = fromLittleEndian16(DESERIALIZE(currentLumpReadPointer, 24, unsigned short));
		currentLumpReadPointer += 26;
		// TODO: sector->thinglist = NULL; andere laufzeitfelder?
	}
	zoneAllocatorDispose(currentLumpStart);

	/** load sidedefs **/
	PREPARE_MAP_LUMP_LOADING(MAP_DATA_LUMP_SIDEDEFS);
	currentMapData.sidedefCount = currentLumpSize / 30;
	currentMapData.sidedefs = zoneAllocatorAllocate(currentMapData.sidedefCount * sizeof(struct Sidedef), LEVEL_STATIC_ALLOCATION_TAG, NULL);
	for (i=0; i<currentMapData.sidedefCount; i++) {
		currentMapData.sidedefs[i].textureOffsetX = fromLittleEndian16(DESERIALIZE(currentLumpReadPointer, 0, short)) << FIXED_POINT_NUMBER_FRACTIONAL_BITS;
		currentMapData.sidedefs[i].textureOffsetY = fromLittleEndian16(DESERIALIZE(currentLumpReadPointer, 2, short)) << FIXED_POINT_NUMBER_FRACTIONAL_BITS;
		currentMapData.sidedefs[i].upperTextureIndex = getTextureIndexForName((char *)(currentLumpReadPointer + 4));
		currentMapData.sidedefs[i].lowerTextureIndex = getTextureIndexForName((char *)(currentLumpReadPointer + 12));
		currentMapData.sidedefs[i].middleTextureIndex = getTextureIndexForName((char *)(currentLumpReadPointer + 20));
		currentMapData.sidedefs[i].sector = currentMapData.sectors + fromLittleEndian16(DESERIALIZE(currentLumpReadPointer, 28, unsigned short));
		currentLumpReadPointer += 30;
		// TODO: laufzeitfelder?
	}
	zoneAllocatorDispose(currentLumpStart);

	/** load linedefs **/
	PREPARE_MAP_LUMP_LOADING(MAP_DATA_LUMP_LINEDEFS);
	currentMapData.linedefCount = currentLumpSize / 14;
	currentMapData.linedefs = zoneAllocatorAllocate(currentMapData.linedefCount * sizeof(struct Linedef), LEVEL_STATIC_ALLOCATION_TAG, NULL);
	for (i=0; i<currentMapData.linedefCount; i++) {
		currentMapData.linedefs[i].startVertex = currentMapData.vertices + fromLittleEndian16(DESERIALIZE(currentLumpReadPointer, 0, unsigned short));
		currentMapData.linedefs[i].endVertex = currentMapData.vertices + fromLittleEndian16(DESERIALIZE(currentLumpReadPointer, 2, unsigned short));
		currentMapData.linedefs[i].flags = fromLittleEndian16(DESERIALIZE(currentLumpReadPointer, 4, unsigned short));
		currentMapData.linedefs[i].type = fromLittleEndian16(DESERIALIZE(currentLumpReadPointer, 6, unsigned short));
		currentMapData.linedefs[i].sectorTag = fromLittleEndian16(DESERIALIZE(currentLumpReadPointer, 8, unsigned short));
		currentMapData.linedefs[i].rightSide = currentMapData.sidedefs + fromLittleEndian16(DESERIALIZE(currentLumpReadPointer, 10, unsigned short));
		currentMapData.linedefs[i].leftSide = currentMapData.sidedefs + fromLittleEndian16(DESERIALIZE(currentLumpReadPointer, 12, unsigned short));
		currentLumpReadPointer += 14;
	}
	zoneAllocatorDispose(currentLumpStart);

	/** load segments **/
	PREPARE_MAP_LUMP_LOADING(MAP_DATA_LUMP_SEGS);
	currentMapData.segmentCount = currentLumpSize / 12;
	currentMapData.segments = zoneAllocatorAllocate(currentMapData.segmentCount * sizeof(struct Segment), LEVEL_STATIC_ALLOCATION_TAG, NULL);
	for (i=0; i<currentMapData.segmentCount; i++) {
		struct Linedef *linedef;
		
		currentMapData.segments[i].startVertex = currentMapData.vertices + fromLittleEndian16(DESERIALIZE(currentLumpReadPointer, 0, unsigned short));
		currentMapData.segments[i].endVertex = currentMapData.vertices + fromLittleEndian16(DESERIALIZE(currentLumpReadPointer, 2, unsigned short));
		// TODO: currentMapData.segments[i].angle = ...;
		currentMapData.segments[i].line = linedef = currentMapData.linedefs + fromLittleEndian16(DESERIALIZE(currentLumpReadPointer, 6, unsigned short));
		currentMapData.segments[i].side = fromLittleEndian16(DESERIALIZE(currentLumpReadPointer, 8, unsigned short));
		currentMapData.segments[i].additionalTextureOffsetX = fromLittleEndian16(DESERIALIZE(currentLumpReadPointer, 10, short)) << FIXED_POINT_NUMBER_FRACTIONAL_BITS;
		currentLumpReadPointer += 12;
		
		currentMapData.segments[i].sidedef = currentMapData.segments[i].side ? linedef->leftSide : linedef->rightSide;
		if (currentMapData.segments[i].line->flags & LINE_FLAG_TWO_SIDED) {
			currentMapData.segments[i].backSector = currentMapData.segments[i].side ? linedef->rightSide->sector : linedef->leftSide->sector;
		} else {
			currentMapData.segments[i].backSector = NULL;
		}
		
		/** we cannot use a straightforward fixed-point length computation here -- that would overflow **/
		{
			int dx = (currentMapData.segments[i].endVertex->x - currentMapData.segments[i].startVertex->x) >> 16;
			int dy = (currentMapData.segments[i].endVertex->y - currentMapData.segments[i].startVertex->y) >> 16;
			int l = sqrt(dx * dx + dy * dy);
			currentMapData.segments[i].length = l << 16;
		}
		
	}
	zoneAllocatorDispose(currentLumpStart);
	
	/** load subsectors **/
	PREPARE_MAP_LUMP_LOADING(MAP_DATA_LUMP_SSECTORS);
	currentMapData.subsectorCount = currentLumpSize / 4;
	currentMapData.subsectors = zoneAllocatorAllocate(currentMapData.subsectorCount * sizeof(struct Subsector), LEVEL_STATIC_ALLOCATION_TAG, NULL);
	for (i=0; i<currentMapData.subsectorCount; i++) {
		currentMapData.subsectors[i].sector = ???;
		currentMapData.subsectors[i].segmentCount = fromLittleEndian16(DESERIALIZE(currentLumpReadPointer, 0, unsigned short));
		currentMapData.subsectors[i].segments = currentMapData.segments + fromLittleEndian16(DESERIALIZE(currentLumpReadPointer, 2, unsigned short));
		currentLumpReadPointer += 4;
	}
	zoneAllocatorDispose(currentLumpStart);

	/** load nodes **/
	PREPARE_MAP_LUMP_LOADING(MAP_DATA_LUMP_NODES);
	currentMapData.nodeCount = currentLumpSize / 28;
	currentMapData.nodes = zoneAllocatorAllocate(currentMapData.nodeCount * sizeof(struct Node), LEVEL_STATIC_ALLOCATION_TAG, NULL);
	for (i=0; i<currentMapData.nodeCount; i++) {
		struct Node *node = currentMapData.nodes + i;
		node->partitionLineOriginX = fromLittleEndian16(DESERIALIZE(currentLumpReadPointer, 0, short)) << FIXED_POINT_NUMBER_FRACTIONAL_BITS;
		node->partitionLineOriginY = fromLittleEndian16(DESERIALIZE(currentLumpReadPointer, 2, short)) << FIXED_POINT_NUMBER_FRACTIONAL_BITS;
		node->partitionLineDeltaX = fromLittleEndian16(DESERIALIZE(currentLumpReadPointer, 4, short)) << FIXED_POINT_NUMBER_FRACTIONAL_BITS;
		node->partitionLineDeltaY = fromLittleEndian16(DESERIALIZE(currentLumpReadPointer, 6, short)) << FIXED_POINT_NUMBER_FRACTIONAL_BITS;
		node->rightChildBoundingBox.minY = fromLittleEndian16(DESERIALIZE(currentLumpReadPointer, 8, short)) << FIXED_POINT_NUMBER_FRACTIONAL_BITS;
		node->rightChildBoundingBox.maxY = fromLittleEndian16(DESERIALIZE(currentLumpReadPointer, 10, short)) << FIXED_POINT_NUMBER_FRACTIONAL_BITS;
		node->rightChildBoundingBox.minX = fromLittleEndian16(DESERIALIZE(currentLumpReadPointer, 12, short)) << FIXED_POINT_NUMBER_FRACTIONAL_BITS;
		node->rightChildBoundingBox.maxY = fromLittleEndian16(DESERIALIZE(currentLumpReadPointer, 14, short)) << FIXED_POINT_NUMBER_FRACTIONAL_BITS;
		node->leftChildBoundingBox.minY = fromLittleEndian16(DESERIALIZE(currentLumpReadPointer, 16, short)) << FIXED_POINT_NUMBER_FRACTIONAL_BITS;
		node->leftChildBoundingBox.maxY = fromLittleEndian16(DESERIALIZE(currentLumpReadPointer, 18, short)) << FIXED_POINT_NUMBER_FRACTIONAL_BITS;
		node->leftChildBoundingBox.minX = fromLittleEndian16(DESERIALIZE(currentLumpReadPointer, 20, short)) << FIXED_POINT_NUMBER_FRACTIONAL_BITS;
		node->leftChildBoundingBox.maxY = fromLittleEndian16(DESERIALIZE(currentLumpReadPointer, 22, short)) << FIXED_POINT_NUMBER_FRACTIONAL_BITS;
		node->rightChild = fromLittleEndian16(DESERIALIZE(currentLumpReadPointer, 24, unsigned short));
		node->leftChild = fromLittleEndian16(DESERIALIZE(currentLumpReadPointer, 26, unsigned short));
		
		/** use the unscaled delta values for the split line to prevent overflow **/
		{
			int unscaledDeltaX = fromLittleEndian16(DESERIALIZE(currentLumpReadPointer, 4, short));
			int unscaledDeltaY = fromLittleEndian16(DESERIALIZE(currentLumpReadPointer, 6, short));
			initializeSplitLine(&node->splitLine, node->partitionLineOriginX, node->partitionLineOriginY, unscaledDeltaX, unscaledDeltaY);
		}

		currentLumpReadPointer += 28;
	}
	zoneAllocatorDispose(currentLumpStart);
	
}

/**
 * See header file for information.
 */
void disposeMap() {
	if (currentMapData.loaded) {
		zoneAllocatorDispose(currentMapData.things);
		zoneAllocatorDispose(currentMapData.vertices);
		zoneAllocatorDispose(currentMapData.linedefs);
		currentMapData.loaded = 0;
		clearThinkerList();
	}
}

/**
 * See header file for information.
 */
struct Subsector *getSubsectorForPosition(FixedPointNumber x, FixedPointNumber y) {
	struct Node *currentNode;
	
	/** handle the special case of a map with a single subsector **/
	if (currentMapData.nodeCount == 0) {
		return currentMapData.subsectors;
	}
	
	/** find the subsector by the BSP structure **/
	currentNode = currentMapData.nodes + currentMapData.nodeCount - 1;
	while (1) {
		int side = getSplitLineSide(&currentNode->splitLine, x, y);
		int childCode = side ? currentNode->leftChild : currentNode->rightChild;
		if (childCode & 0x8000) {
			return currentMapData.subsectors + (childCode & 0x7fff);
		} else {
			currentNode = currentMapData.nodes + childCode;
		}
	}

}
