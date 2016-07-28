/*
 *  FirstPersonView.c
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
#include "SplitLine.h"
#include "MapData.h"
#include "SolidOcclusionCullingMap.h"
#include "ViewRasterizer.h"
#include "StaticGraphicsData.h"
#include "StaticGameData.h"
#include "LowlevelGraphics.h"
#include "Visplane.h"
#include "FirstPersonView.h"

#define FIELD_OF_VIEW_MULTIPLIER	(120 << FIXED_POINT_NUMBER_FRACTIONAL_BITS)
#define NEAR_PLANE_DISTANCE			FIXED_POINT_NUMBER_ONE

/** TODO: remove **/
extern FixedPointNumber playerX;
extern FixedPointNumber playerY;
extern FixedPointNumber playerZ;
extern Angle playerAngle;

/**
 * See header file for information.
 */
struct RenderState renderState;

/**
 * The segment being rendered.
 */
static struct Segment *currentSegment;

/**
 * The front sector, i.e. the sector that is adjacent to the current segment
 * and on the player's side of the segment.
 */
static struct Sector *frontSector;

/**
 * The back sector, i.e. the sector that is adjacent to the current segment
 * and on the opposide side of the player's side of the segment.
 */
static struct Sector *backSector;

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
 * This function assumes that its arguments are one vertex behind the near plane that shall be clipped,
 * and one visible vertex. It then modifies the clipped vertex to be on the near plane without
 * affecting the line orientation.
 */
static void clipToNearPlane(struct Vertex *clippedVertex, struct Vertex *visibleVertex) {
	FixedPointNumber deltaX = visibleVertex->x - clippedVertex->x;
	FixedPointNumber deltaY = visibleVertex->y - clippedVertex->y;
	FixedPointNumber clippedX = NEAR_PLANE_DISTANCE - clippedVertex->x;
	clippedVertex->y += fixedPointNumberMultiply(clippedX, fixedPointNumberDivide(deltaY, deltaX));
	clippedVertex->x = NEAR_PLANE_DISTANCE;
}

/**
 * This function divides the value by x, multiplies it by the field of view, and converts
 * it from a fixed-point number to an integer.
 */
static int projectionHelper(FixedPointNumber value, FixedPointNumber x) {
	/**
	 * This expression is prone to both overflow and rounding errors, so we apply the
	 * shifting conversion from fixed point to integer in two steps instead of one.
	 */
	FixedPointNumber ratio = fixedPointNumberDivide(value, x);
	return ((ratio >> 8) * 240) >> 8;
}

/**
 * See header file for information.
 */
void onBeforeDrawFragment() {

	/**
	 * Mark the line as "visible on the automap". This must be done in this callback
	 * so it only occurs for lines that have actually been seen by the player and not
	 * obscured by other walls -- we might even want to push this check further down
	 * to make it more accurate.
	 */
	currentSegment->line->flags |= LINE_FLAG_MAPPED;
	
	/**
	 * Prepare texture mapping. TODO: We can cache this information in the render state since it
	 * is the same for all fragments of a segment. Might be overkill though -- there is seldom more
	 * than one fragment.
	 */
	
	/** compute horizontal texture panning **/
	renderState.horizontalTexturePanning = currentSegment->sidedef->textureOffsetX + currentSegment->additionalTextureOffsetX;
	
	/** transform floor / ceiling heights into view space **/
	if (frontSector != NULL) {
		renderState.viewFrontSectorCeilingHeight = frontSector->ceilingHeight - playerZ;
		renderState.viewFrontSectorFloorHeight = frontSector->floorHeight - playerZ;
		renderState.frontSectorFloorFlatIndex = frontSector->floorFlatIndex;
		
		/** combine multiple sky ceilings **/
		if (backSector != NULL && frontSector->ceilingFlatIndex == staticGameData.skyFlatIndex && backSector->ceilingFlatIndex == staticGameData.skyFlatIndex) {
			renderState.frontSectorCeilingFlatIndex = -1;
		} else {
			renderState.frontSectorCeilingFlatIndex = frontSector->ceilingFlatIndex;
		}
		
	}
	if (backSector != NULL) {
		renderState.viewBackSectorCeilingHeight = backSector->ceilingHeight - playerZ;
		renderState.viewBackSectorFloorHeight = backSector->floorHeight - playerZ;
	}

	/** different handling for one-sided / two-sided lines **/
	if (!(currentSegment->line->flags & LINE_FLAG_TWO_SIDED)) {
		
		/** set middle texture **/
		renderState.middleTexture = getTexture(currentSegment->sidedef->middleTextureIndex);
		
		/** compute middle texture panning **/
		if (currentSegment->line->flags & LINE_FLAG_LOWER_TEXTURE_UNPEGGED) {
			
			/**
			 * The texture is "drawn from the bottom upwards":
			 *
			 * original formula: H(TexTop, Player) = H(Floor, 0) + TexHeight - H(Player, 0)
			 * 
			 * H(TexTop, Ceiling) + H(Ceiling, Player) = H(Floor, Player) + TexHeight
			 *
			 * Result + H(Ceiling, Floor) = TexHeight
			 *
			 * Result = TexHeight - H(Ceiling, Floor)
			 *
			 * (add manual panning to get the formula below)
			 */
			int manualPanning = currentSegment->sidedef->textureOffsetY;
			int texHeight = renderState.middleTexture->height;
			int ceilingToFloor = renderState.viewFrontSectorCeilingHeight - renderState.viewFrontSectorFloorHeight;
			renderState.middleTexturePanning = manualPanning + texHeight - ceilingToFloor;
			
		} else {

			/**
			 * The texture is "drawn from the ceiling downwards. Since that is the way texture mapping
			 * actually works in ecodoom, this case is trivial -- we just have to consider manual padding.
			 */
			renderState.middleTexturePanning = currentSegment->sidedef->textureOffsetY;
			
		}
		
	} else {
		
		/** set middle texture **/
		renderState.middleTexture = NULL;
		
		/** set upper texture **/
		renderState.upperTexture = getTexture(currentSegment->sidedef->upperTextureIndex);

		/** compute upper texture panning **/
		if (currentSegment->line->flags & LINE_FLAG_UPPER_TEXTURE_UNPEGGED) {
			
			/**
			 * The texture is "drawn from the front-sector ceiling downwards". Since that is the way texture mapping
			 * actually works in ecodoom, this case is trivial -- we just have to consider manual padding.
			 */
			renderState.upperTexturePanning = currentSegment->sidedef->textureOffsetY;
			
		} else {

			/**
			 * The texture is "drawn from the back-sector ceiling upwards".
			 *
			 * original formula: H(TexTop, Player) = H(BackSectorCeiling, 0) + TexHeight - H(Player, 0)
			 *
			 * H(TexTop, FrontSectorCeiling) + H(FrontSectorCeiling, 0) - H(Player, 0) = H(BackSectorCeiling, 0) + TexHeight - H(Player, 0)
			 *
			 * result + H(FrontSetorCeiling, 0) = H(BackSectorCeiling, 0) + TexHeight
			 *
			 * result = TexHeight - H(FrontSectorCeiling, BackSectorCeiling)
			 *
			 * (add manual panning to get the formula below)
			 */
			int manualPanning = currentSegment->sidedef->textureOffsetY;
			int texHeight = renderState.upperTexture->height;
			int ceilingWallHeight = renderState.viewFrontSectorCeilingHeight - renderState.viewBackSectorCeilingHeight;
			renderState.upperTexturePanning = manualPanning + texHeight - ceilingWallHeight;
			
		}

		/** set lower texture **/
		renderState.lowerTexture = getTexture(currentSegment->sidedef->lowerTextureIndex);

		/** compute lower texture panning **/
		if (currentSegment->line->flags & LINE_FLAG_LOWER_TEXTURE_UNPEGGED) {
			
			/**
			 * The texture top is attached to the front sector ceiling.
			 *
			 * original formula: H(TexTop, Player) = H(FrontSectorCeiling, Player)
			 *
			 * result = H(TexTop, BackSectorFloor) = H(FrontSectorCeiling, BackSectorFloor)
			 *
			 * (add manual panning to get the formula below)
			 */
			int manualPanning = currentSegment->sidedef->textureOffsetY;
			int frontSectorCeiling = renderState.viewFrontSectorCeilingHeight;
			int backSectorFloor = renderState.viewBackSectorFloorHeight;
			renderState.lowerTexturePanning = manualPanning + frontSectorCeiling - backSectorFloor;
			
		} else {

			/**
			 * The texture is "drawn from the back-sector floor downwards". Since that is the way texture mapping
			 * actually works in ecodoom, this case is trivial -- we just have to consider manual padding.
			 */
			renderState.lowerTexturePanning = currentSegment->sidedef->textureOffsetY;
			
		}
		
	}

	/** compute lighting **/
	// TODO: the constant 4 is named LIGHTSEGSHIFT in the original source code
	// also, there is an extralight modifier that is not yet implemented here
	renderState.worldSegmentLight = frontSector->lightLevel >> 4;
	if (currentSegment->startVertex->y == currentSegment->endVertex->y) {
		renderState.worldSegmentLight--;
	} else if (currentSegment->startVertex->x == currentSegment->endVertex->x) {
		renderState.worldSegmentLight++;
	}
	renderState.worldSectorLight = frontSector->lightLevel >> 4;
}

/**
 * Handles a single segment.
 */
static void drawSegment(struct Segment *segment) {
	struct Vertex viewStartVertex, viewEndVertex;
	int solid;

	/** initialize render state **/
	currentSegment = segment;
	frontSector = segment->sidedef->sector;
	backSector = segment->backSector;
	renderState.segmentLength = currentSegment->length;
	renderState.twoSidedFlag = ((currentSegment->line->flags & LINE_FLAG_TWO_SIDED) != 0);
	
	/** transform vertices to view coordinates **/
	transformWorldToView(segment->startVertex, &viewStartVertex);
	transformWorldToView(segment->endVertex, &viewEndVertex);

	/** near plane culling **/
	if (viewStartVertex.x < NEAR_PLANE_DISTANCE && viewEndVertex.x < NEAR_PLANE_DISTANCE) {
		return;
	}
	
	/** near plane clipping **/
	if (viewStartVertex.x < NEAR_PLANE_DISTANCE) {
		clipToNearPlane(&viewStartVertex, &viewEndVertex);
	} else if (viewEndVertex.x < NEAR_PLANE_DISTANCE) {
		clipToNearPlane(&viewEndVertex, &viewStartVertex);
	}
	
	/** store view coordinates as needed **/
	renderState.viewStartX = viewStartVertex.x;
	renderState.viewEndX = viewEndVertex.x;

	/** transform vertices to screen coordinates (projection) **/
	// TODO: the 3d view isn't 100 pixels tall
	renderState.screenStartX = 160 - projectionHelper(viewStartVertex.y, viewStartVertex.x);
	renderState.screenEndX = 160 - projectionHelper(viewEndVertex.y, viewEndVertex.x);
	renderState.floorStartY = 100 - projectionHelper(frontSector->floorHeight - playerZ, viewStartVertex.x);
	renderState.floorEndY = 100 - projectionHelper(frontSector->floorHeight - playerZ, viewEndVertex.x);
	renderState.ceilingStartY = 100 - projectionHelper(frontSector->ceilingHeight - playerZ, viewStartVertex.x);
	renderState.ceilingEndY = 100 - projectionHelper(frontSector->ceilingHeight - playerZ, viewEndVertex.x);
	
	if (renderState.twoSidedFlag && backSector != NULL) {
		renderState.upperWindowBorderStartY = 100 - projectionHelper(backSector->ceilingHeight - playerZ, viewStartVertex.x);
		renderState.upperWindowBorderEndY = 100 - projectionHelper(backSector->ceilingHeight - playerZ, viewEndVertex.x);
		renderState.lowerWindowBorderStartY = 100 - projectionHelper(backSector->floorHeight - playerZ, viewStartVertex.x);
		renderState.lowerWindowBorderEndY = 100 - projectionHelper(backSector->floorHeight - playerZ, viewEndVertex.x);
	}

	/**
	 * Backface culling. We sort out one-column segments here because the renderer
	 * can't handle them anyway, so we save some work.
	 */
	if (renderState.screenStartX >= renderState.screenEndX) {
		return;
	}

	/**
	 * Determine whether the segment is treated as solid or as non-solid. The difference only affects the
	 * SOCM contents, not the way the segment is actually rendered.
	 */
	if (backSector == NULL) {
		/** single-sided lines are always solid **/
		solid = 1;
	} else if (backSector->ceilingHeight <= frontSector->floorHeight || frontSector->ceilingHeight <= backSector->floorHeight ) {
		/**
		 * No gap between floor and ceiling look through, so this counts as solid. It is important
		 * that this check uses '<=', not '<', for comparison to count closed doors as solid.
		 */
		solid = 1;
	} else {
		/**
		 * There is a special case in which the segment isn't drawn at all. The explanation used in the original source
		 * code is a bit vague. My guess: The special case is that the segment is simply not visible, so no time should
		 * be wasted drawing it. This occurs for trigger lines etc. It is unclear how effective this optimization really is.
		 * The conditions to make a segment invisible are:
		 *
		 * - no differences in floor height or ceiling height
		 * - no differences in floor texture or ceiling texture. Such differences would not make the
		 *   segment visible directly, but would require the segment to be drawn to affect how
		 *   floor and ceiling are drawn.
		 * - no differences in light level
		 * - no middle texture
		 */
		if (backSector->ceilingHeight == frontSector->ceilingHeight &&
				backSector->floorHeight == frontSector->floorHeight &&
				backSector->ceilingFlatIndex == frontSector->ceilingFlatIndex &&
				backSector->floorFlatIndex == frontSector->floorFlatIndex &&
				backSector->lightLevel == frontSector->lightLevel &&
				segment->sidedef->middleTextureIndex == 0) {
			// TODO: return;
		} else {
			solid = 0;
		}
	}

	/** finally, invoke the SOCM to split the segment into fragments and render them **/
	invokeSolidOcclusionCullingMap(renderState.screenStartX, renderState.screenEndX, drawSegmentFragment, solid);
	
}

static void drawSubsector(int subsectorIndex) {
	struct Subsector *subsector = currentMapData.subsectors + subsectorIndex;
	int i;
/*
 TODO   
 
 sector = subsector->sector;
 if (sector->floorheight < viewz) {
	floorplane = R_FindPlane (sector->floorheight,
	sector->floorpic,
	sector->lightlevel);
 } else {
	floorplane = NULL;
 }
 
 if (sector->ceilingheight > viewz || sector->ceilingpic == skyflatnum) {
	ceilingplane = R_FindPlane (sector->ceilingheight,
	sector->ceilingpic,
	sector->lightlevel);
 } else {
	ceilingplane = NULL;
 }
 
 R_AddSprites (sector);	
 
*/ 
	renderState.color = makecol(rand() & 255, rand() & 255, rand() & 255);
	
	/** draw the segments (walls, stairs, etc.) for this subsector **/
	for (i=0; i<subsector->segmentCount; i++) {
		drawSegment(subsector->segments + i);
	}
	
}

/**
 * This callback ensures that the specified vertex is behind the near plane.
 */
static int checkBoundingBoxVisibleBehindNearPlaneCallback(struct Vertex *vertex) {
	return vertex->x < NEAR_PLANE_DISTANCE;
}

/**
 * This callback ensures that the specified vertex is beyond the left screen border.
 */
static int checkBoundingBoxVisibleBeyondLeftScreenBorder(struct Vertex *vertex) {
	return vertex->y > vertex->x;
}

/**
 * This callback ensures that the specified vertex is beyond the right screen border.
 */
static int checkBoundingBoxVisibleBeyondRightScreenBorder(struct Vertex *vertex) {
	return vertex->y < -vertex->x;
}

/**
 * Invokes the specified callback on each of the four corners and returns nonzero if all calls returned nonzero.
 */
static int checkBoundingBoxVisibleCaller(struct Vertex *corners, int (*callback)(struct Vertex *vertex)) {
	return callback(corners) && callback(corners + 1) && callback(corners + 2) && callback(corners + 3);
}

/**
 * This function performs some quick checks to find out whether a bounding box
 * is visible by the player.
 *
 * Note: This function does not perform the obvious check whether the player is inside
 * the box. This function is called for the "opposite side" of a BSP node, so the
 * player can never be inside such a box.
 */
static int checkBoundingBoxVisible(struct BoundingBox *box) {
	struct Vertex tempCorner;
	struct Vertex viewSpaceCorners[4];
	
	/** TODO: enable this function **/
	if (1 == 1) return 1;

	/** transform the box to view space **/
	tempCorner.x = box->minX;
	tempCorner.y = box->minY;
	transformWorldToView(&tempCorner, viewSpaceCorners + 0);
	tempCorner.x = box->maxX;
	transformWorldToView(&tempCorner, viewSpaceCorners + 1);
	tempCorner.y = box->maxY;
	transformWorldToView(&tempCorner, viewSpaceCorners + 2);
	tempCorner.x = box->minX;
	transformWorldToView(&tempCorner, viewSpaceCorners + 3);

	/** check if totally behind the near plane **/
	if (checkBoundingBoxVisibleCaller(viewSpaceCorners, &checkBoundingBoxVisibleBehindNearPlaneCallback)) {
		return 0;
	}
	
	/** check if totally beyond the left screen border **/
	if (checkBoundingBoxVisibleCaller(viewSpaceCorners, &checkBoundingBoxVisibleBeyondLeftScreenBorder)) {
		return 0;
	}
	
	/** check if totally beyond the right screen border **/
	if (checkBoundingBoxVisibleCaller(viewSpaceCorners, &checkBoundingBoxVisibleBeyondRightScreenBorder)) {
		return 0;
	}
	
	/** TODO: check against solid occlusion culling map? **/
	// ...
	
	/** bounding box is visible **/
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
			drawBspNodeOrSubsector(node->leftChild);
			if (checkBoundingBoxVisible(&node->rightChildBoundingBox)) {
				drawBspNodeOrSubsector(node->rightChild);
			}
		} else {
			drawBspNodeOrSubsector(node->rightChild);
			if (checkBoundingBoxVisible(&node->leftChildBoundingBox)) {
				drawBspNodeOrSubsector(node->leftChild);
			}
		}
	}
}

/**
 * See header file for information.
 */
void drawFirstPersonView() {

	printf("start frame\n");
	
	/** prepare world-to-view coordinate transformation **/
	FixedPointNumber playerAngleSine = sinForAngle(playerAngle);
	FixedPointNumber playerAngleCosine = cosForAngle(playerAngle);
	worldToViewTransformationMatrix[0] = playerAngleCosine;
	worldToViewTransformationMatrix[1] = playerAngleSine;
	worldToViewTransformationMatrix[2] = -playerAngleSine;
	worldToViewTransformationMatrix[3] = playerAngleCosine;
	worldToViewTranslation[0] = -playerX;
	worldToViewTranslation[1] = -playerY;
	
	/** initialize various global renderer variables **/
	clearSolidOcclusionCullingMap();
	clearClipBuffer();
	clearVisplanes();
	setVisplanePointOfView(playerX, playerY, playerAngle);
	
	/** draw the root node **/
	if (currentMapData.nodeCount == 0) {
		/** special case of a map with a single subsector **/
		drawSubsector(0);
	} else {
		/** the last node in the node list is the root node **/
		drawBspNodeOrSubsector(currentMapData.nodeCount - 1);
	}
	
	/** draw visplanes **/
	clearClipBuffer();
	drawVisplanes();
	
}
