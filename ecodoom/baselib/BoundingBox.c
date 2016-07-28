/*
 *  BoundingBox.c
 *  baselib
 *
 *  Created by Martin on 8/26/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

#include "Common.h"
#include "FixedPointNumber.h"
#include "BoundingBox.h"

/**
 * See header file for information.
 */
void clearBoundingBox(struct BoundingBox *boundingBox) {
	boundingBox->minX = 0x7fffffff;
	boundingBox->minY = 0x7fffffff;
	boundingBox->maxX = 0x80000000;
	boundingBox->maxY = 0x80000000;
}

/**
 * See header file for information.
 */
void addPointToBoundingBox(struct BoundingBox *targetBoundingBox, FixedPointNumber pointX, FixedPointNumber pointY) {
	if (targetBoundingBox->minX > pointX) {
		targetBoundingBox->minX = pointX;
	}
	if (targetBoundingBox->minY > pointY) {
		targetBoundingBox->minY = pointY;
	}
	if (targetBoundingBox->maxX < pointX) {
		targetBoundingBox->maxX = pointX;
	}
	if (targetBoundingBox->maxY < pointY) {
		targetBoundingBox->maxY = pointY;
	}
}

/**
 * See header file for information.
 */
void addBoundingBoxToBoundingBox(struct BoundingBox *targetBoundingBox, struct BoundingBox *boxToAdd) {
	addPointToBoundingBox(targetBoundingBox, boxToAdd->minX, boxToAdd->minY);
	addPointToBoundingBox(targetBoundingBox, boxToAdd->maxX, boxToAdd->maxY);
}

/**
 * See header file for information.
 */
FixedPointNumber getBoundingBoxWidth(struct BoundingBox *boundingBox) {
	return boundingBox->maxX - boundingBox->minX;
}

/**
 * See header file for information.
 */
FixedPointNumber getBoundingBoxHeight(struct BoundingBox *boundingBox) {
	return boundingBox->maxY - boundingBox->minY;
}

/**
 * See header file for information.
 */
FixedPointNumber getBoundingBoxCenterX(struct BoundingBox *boundingBox) {
	return (boundingBox->maxX + boundingBox->minX) / 2;
}

/**
 * See header file for information.
 */
FixedPointNumber getBoundingBoxCenterY(struct BoundingBox *boundingBox) {
	return (boundingBox->maxY + boundingBox->minY) / 2;
}

/**
 * See header file for information.
 */
int isPointInsideBoundingBox(struct BoundingBox *boundingBox, FixedPointNumber pointX, FixedPointNumber pointY) {
	return (boundingBox->minX <= pointX) && (boundingBox->minY <= pointY) && (boundingBox->maxX >= pointX) && (boundingBox->maxY >= pointY);
}
