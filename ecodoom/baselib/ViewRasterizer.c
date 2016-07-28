/*
 *  ViewRasterizer.c
 *  baselib
 *
 *  Created by Martin on 9/4/10.
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
#include "FirstPersonView.h"
#include "StaticGraphicsData.h"
#include "StaticGameData.h"
#include "LowlevelGraphics.h"
#include "SystemGraphics.h"
#include "Visplane.h"
#include "ViewRasterizer.h"

/**
 * See header file for information.
 */
void drawSegmentFragment(int firstColumn, int lastColumn) {
	int x;
	int segmentStartX;
	int segmentDeltaX;
	int floorStartY;
	int floorDeltaY;
	int ceilingStartY;
	int ceilingDeltaY;
	int lowerWindowBorderStartY;
	int lowerWindowBorderDeltaY;
	int upperWindowBorderStartY;
	int upperWindowBorderDeltaY;
	int hasCeilingVisplane;
	int hasFloorVisplane;
		
	/**
	 * Perform high-level computations that are only required for visible segments.
	 */
	onBeforeDrawFragment();

	/**
	 * Obtain information from the render state that is now computed.
	 */
	segmentStartX = renderState.screenStartX;
	segmentDeltaX = (renderState.screenEndX - renderState.screenStartX);
	floorStartY = renderState.floorStartY;
	floorDeltaY = (renderState.floorEndY - renderState.floorStartY);
	ceilingStartY = renderState.ceilingStartY;
	ceilingDeltaY = (renderState.ceilingEndY - renderState.ceilingStartY);
	lowerWindowBorderStartY = renderState.lowerWindowBorderStartY;
	lowerWindowBorderDeltaY = (renderState.lowerWindowBorderEndY - renderState.lowerWindowBorderStartY);
	upperWindowBorderStartY = renderState.upperWindowBorderStartY;
	upperWindowBorderDeltaY = (renderState.upperWindowBorderEndY - renderState.upperWindowBorderStartY);
	hasCeilingVisplane = (renderState.frontSectorCeilingFlatIndex == staticGameData.skyFlatIndex) || ((renderState.frontSectorCeilingFlatIndex != -1) && (renderState.viewFrontSectorCeilingHeight > 0));
	hasFloorVisplane = (renderState.frontSectorFloorFlatIndex != -1) && (renderState.viewFrontSectorFloorHeight < 0);
	
	/**
	 * Prepare prespective-correct texture mapping.
	 */
	FixedPointNumber segmentLength = renderState.segmentLength;
	FixedPointNumber quotientU1ByZ1 = fixedPointNumberDivide(segmentLength, renderState.viewEndX);
	FixedPointNumber quotient1ByZ0 = fixedPointNumberDivide(FIXED_POINT_NUMBER_ONE, renderState.viewStartX);
	FixedPointNumber quotient1ByZ1 = fixedPointNumberDivide(FIXED_POINT_NUMBER_ONE, renderState.viewEndX);

	/** allocate a visplane for the front-sector ceiling **/
	struct Visplane *ceilingVisplane;
	if (hasCeilingVisplane) {
		ceilingVisplane = allocateVisplane(renderState.viewFrontSectorCeilingHeight, renderState.frontSectorCeilingFlatIndex, renderState.worldSectorLight);
		if (ceilingVisplane != NULL) {
			expandVisplaneArray(ceilingVisplane, firstColumn, lastColumn);
		}
	} else {
		ceilingVisplane = NULL;
	}

	/** allocate a visplane for the front-sector floor **/
	struct Visplane *floorVisplane;
	if (hasFloorVisplane) {
		floorVisplane = allocateVisplane(renderState.viewFrontSectorFloorHeight,renderState.frontSectorFloorFlatIndex, renderState.worldSectorLight);
		if (floorVisplane != NULL) {
			expandVisplaneArray(floorVisplane, firstColumn, lastColumn);
		}
	} else {
		floorVisplane = NULL;
	}
	
	/**
	 * Ignore back-facing segments as well as 1-column segments. The latter cause
	 * divide-by-zero problems, and circumventing the problem isn't worth the trouble.
	 */
	if (segmentDeltaX > 0) {
		for (x = firstColumn; x <= lastColumn; x++) {
			
			/** compute line end properties **/
			int currentDeltaX = x - segmentStartX;
			int ceilingY = ceilingStartY + ceilingDeltaY * currentDeltaX / segmentDeltaX;
			int floorY = floorStartY + floorDeltaY * currentDeltaX / segmentDeltaX;
			
			/** compute horizontal texture offset (u) **/
			FixedPointNumber alpha = (currentDeltaX << FIXED_POINT_NUMBER_FRACTIONAL_BITS) / segmentDeltaX;
			FixedPointNumber num = fixedPointNumberMultiply(alpha, quotientU1ByZ1);
			FixedPointNumber denom = fixedPointNumberMultiply(FIXED_POINT_NUMBER_ONE - alpha, quotient1ByZ0) + fixedPointNumberMultiply(alpha, quotient1ByZ1);
			FixedPointNumber u = fixedPointNumberDivide(num, denom) + renderState.horizontalTexturePanning;

			/** add the front-sector ceiling to the visplane **/
			if (ceilingVisplane != NULL && renderState.frontSectorCeilingFlatIndex != -1) {
				/** we need to clip against the screen bottom here solely for sky handling. TODO: 200 is not the actual 3d view height **/
				int effectiveCeilingY = (ceilingY > 200 ? 200 : ceilingY);
				int ceilingPixels = getUpperClipperSpace(x, effectiveCeilingY - 1);
				if (ceilingPixels > 0) {
					ceilingVisplane->firstRow[x] = effectiveCeilingY - ceilingPixels;
					ceilingVisplane->lastRow[x] = effectiveCeilingY - 1;
				}
			}

			/** add the front-sector floor to the visplane **/
			if (floorVisplane != NULL && renderState.frontSectorFloorFlatIndex != -1) {
				int floorPixels = getLowerClipperSpace(x, floorY);
				if (floorPixels > 0) {
					floorVisplane->firstRow[x] = floorY;
					floorVisplane->lastRow[x] = floorY + floorPixels - 1;
				}
			}
			
			/** draw the segment fragment **/
			systemGraphicsSetColormap(getColorMap(getLightingColorMapIndexFromScale(renderState.worldSegmentLight, denom >> 6)));
			if (!renderState.twoSidedFlag) {

				/** draw the column **/
				int texelColumn = (u >> FIXED_POINT_NUMBER_FRACTIONAL_BITS) & renderState.middleTexture->widthMask;
				unsigned char *columnTexels = getTextureColumnTexels(renderState.middleTexture, texelColumn);
				FixedPointNumber sourceHeight = renderState.viewFrontSectorCeilingHeight - renderState.viewFrontSectorFloorHeight;
				drawScaledTextureColumnOnScreen(x, ceilingY, sourceHeight, floorY - ceilingY, columnTexels, renderState.middleTexturePanning);

			} else {

				/**
				 * This special way of disabling ceiling drawing is needed for skies. Ceiling drawing is
				 * actually just delayed; multiple sky "ceilings" are drawn at once as soon as a real
				 * wall is hit.
				 */
				if (renderState.frontSectorCeilingFlatIndex != -1) {
					
					/** draw the upper window border column if applicable **/
					if (renderState.viewFrontSectorCeilingHeight > renderState.viewBackSectorCeilingHeight) {
						int upperWindowBorderY = upperWindowBorderStartY + upperWindowBorderDeltaY * currentDeltaX / segmentDeltaX;
						if (upperWindowBorderY > ceilingY) {
							int texelColumn = (u >> FIXED_POINT_NUMBER_FRACTIONAL_BITS) & renderState.upperTexture->widthMask;
							unsigned char *columnTexels = getTextureColumnTexels(renderState.upperTexture, texelColumn);
							FixedPointNumber sourceHeight = renderState.viewFrontSectorCeilingHeight - renderState.viewBackSectorCeilingHeight;
							drawScaledTextureColumnOnScreen(x, ceilingY, sourceHeight, upperWindowBorderY - ceilingY, columnTexels, renderState.upperTexturePanning);
							insertUpperClipper(x, upperWindowBorderY - 1);
						}
					} else {
						// TODO: if there is no visible ceiling change, i.e. no change in height, texture, or lighting,
						// it makes sense to leave the clipper alone so we create a joint visplane for both subsectors.
						// I.e. the same as for skies.
						insertUpperClipper(x, ceilingY - 1);
					}
					
				}

				/**
				 * Disabling works for floors too, just to be consistent.
				 */
				if (renderState.frontSectorFloorFlatIndex != -1) {

					/** draw the lower window border column if applicable **/
					if (renderState.viewFrontSectorFloorHeight < renderState.viewBackSectorFloorHeight) {
						int lowerWindowBorderY = lowerWindowBorderStartY + lowerWindowBorderDeltaY * currentDeltaX / segmentDeltaX;
						if (lowerWindowBorderY < floorY) {
							int texelColumn = (u >> FIXED_POINT_NUMBER_FRACTIONAL_BITS) & renderState.lowerTexture->widthMask;
							unsigned char *columnTexels = getTextureColumnTexels(renderState.lowerTexture, texelColumn);
							FixedPointNumber sourceHeight = renderState.viewBackSectorFloorHeight - renderState.viewFrontSectorFloorHeight;
							drawScaledTextureColumnOnScreen(x, lowerWindowBorderY, sourceHeight, floorY - lowerWindowBorderY, columnTexels, renderState.lowerTexturePanning);
							insertLowerClipper(x, lowerWindowBorderY);
						}
					} else {
						// TODO: if there is no visible floor change, i.e. no change in height, texture, or lighting,
						// it makes sense to leave the clipper alone so we create a joint visplane for both subsectors
						insertLowerClipper(x, floorY);
					}
					
				}

			}
			systemGraphicsSetColormap(NULL);
			
		}
	}
}

