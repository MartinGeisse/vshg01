/*
 *  BoundingBox.h
 *  baselib
 *
 *  Created by Martin on 8/26/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

struct BoundingBox {
	
	/** the minimal x value contained in this box **/
	FixedPointNumber minX;

	/** the minimal y value contained in this box **/
	FixedPointNumber minY;

	/** the maximal x value contained in this box **/
	FixedPointNumber maxX;
	
	/** the maximal y value contained in this box **/
	FixedPointNumber maxY;
	
};

/**
 * Clears the specified bounding box to make it empty.
 */
void clearBoundingBox(struct BoundingBox *boundingBox);

/**
 * Adds a point to the specified bounding box.
 */
void addPointToBoundingBox(struct BoundingBox *targetBoundingBox, FixedPointNumber pointX, FixedPointNumber pointY);

/**
 * Merges a bounding box into the specified bounding box.
 */
void addBoundingBoxToBoundingBox(struct BoundingBox *targetBoundingBox, struct BoundingBox *boxToAdd);

/**
 * Returns the width of the specified bounding box.
 */
FixedPointNumber getBoundingBoxWidth(struct BoundingBox *boundingBox);

/**
 * Returns the height of the specified bounding box.
 */
FixedPointNumber getBoundingBoxHeight(struct BoundingBox *boundingBox);

/**
 * Returns the x coordinate of the center of the specified bounding box.
 */
FixedPointNumber getBoundingBoxCenterX(struct BoundingBox *boundingBox);

/**
 * Returns the y coordinate of the center of the specified bounding box.
 */
FixedPointNumber getBoundingBoxCenterY(struct BoundingBox *boundingBox);

/**
 * Returns 1 if the point lies inside the bounding box, 0 if not.
 */
int isPointInsideBoundingBox(struct BoundingBox *boundingBox, FixedPointNumber pointX, FixedPointNumber pointY);
