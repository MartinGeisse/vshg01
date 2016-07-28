/*
 *  LowlevelGraphics.c
 *  baselib
 *
 *  Created by Martin on 8/15/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

#include "Common.h"
#include "FixedPointNumber.h"
#include "SystemGraphics.h"
#include "SystemDebug.h"
#include "LowlevelGraphics.h"

/**
 * The clip buffer.
 */
static int clipBufferWindowStart[320];
static int clipBufferWindowEnd[320];

/**
 * See header file for information.
 */
void drawPatchOnScreen(int x, int y, unsigned char *patch) {
	int column;
	
	/** extract meta-data of the patch **/
	short width = fromLittleEndian16(DESERIALIZE(patch, 0, short));
	short height = fromLittleEndian16(DESERIALIZE(patch, 2, short));
	short offsetX = fromLittleEndian16(DESERIALIZE(patch, 4, short));
	short offsetY = fromLittleEndian16(DESERIALIZE(patch, 6, short));
	int *serializedColumnOffsetTable = (int *)(patch + 8);
	
	/** apply translation offset **/
	x -= offsetX;
	y -= offsetY;
	
	/** we don't clip against the screen borders, but we check that the destination is fully inside the screen **/
	if (x < 0 || y < 0 || x + width > SCREEN_WIDTH || y + height > SCREEN_HEIGHT) {
		systemFatalError("drawPatchOnScreen(): drawing outside screen. x: %d, y: %d, w: %d, h: %d", x, y, width, height);
	}
	
	/** loop over columns of the patch **/
	for (column = 0; column < width; column++) {
		unsigned char *postData = patch + fromLittleEndian32(serializedColumnOffsetTable[column]);
		
		/** loop over posts of this column **/
		while (*postData != 0xff) {
			int texel;
			
			/**
			 * Extract post header fields. The +3 offset of the source skips the two-byte header and the beginning
			 * padding byte of the post.
			 */
			int verticalPostOffset = postData[0];
			int postLength = postData[1];
			unsigned char *source = postData + 3;
			
			/**
			 * Draw the texels of this post.
			 */
			for (texel = 0; texel < postLength; texel++) {
				systemGraphicsDrawPixel(x + column, y + verticalPostOffset + texel, source[texel]);
			}
			
			/**
			 * Skip the two-byte header, the two padding bytes and the texels to reach the next post.
			 */
			postData = postData + postLength + 4;
			
		}
		
	}
	
}

/**
 * See header file for information.
 */
void drawScaledPatchColumnOnScreen(int x, int y, int sourceHeight, int destinationHeight, unsigned char *patchColumnData) {
	
	/** prepare scaling **/
	FixedPointNumber texelIncrementPerPixel = (sourceHeight << FIXED_POINT_NUMBER_FRACTIONAL_BITS) / destinationHeight;
	FixedPointNumber pixelIncrementPerTexel = (destinationHeight << FIXED_POINT_NUMBER_FRACTIONAL_BITS) / sourceHeight;
	
	/** loop over posts of this column **/
	while (*patchColumnData != 0xff) {
		
		/**
		 * Extract post header fields. The +3 offset of the source skips the two-byte header and the beginning
		 * padding byte of the post.
		 */
		int verticalPostOffset = patchColumnData[0];
		int postLength = patchColumnData[1];
		unsigned char *source = patchColumnData + 3;
		
		/**
		 * Prepare scaled drawing
		 */
		FixedPointNumber currentTexel = 0;
		FixedPointNumber texelLimit = (postLength << FIXED_POINT_NUMBER_FRACTIONAL_BITS);
		int currentPixel = y + ((verticalPostOffset * pixelIncrementPerTexel) >> FIXED_POINT_NUMBER_FRACTIONAL_BITS);
		
		/**
		 * Draw the pixels of this post.
		 */
		for (; currentTexel < texelLimit; currentTexel += texelIncrementPerPixel, currentPixel++) {
			systemGraphicsDrawPixel(x, currentPixel, source[currentTexel >> FIXED_POINT_NUMBER_FRACTIONAL_BITS]);
		}
		
		/**
		 * Skip the two-byte header, the two padding bytes and the texels to reach the next post.
		 */
		patchColumnData = patchColumnData + postLength + 4;
		
	}
	
}

/**
 * See header file for information.
 */
void drawUnscaledTextureColumnOnScreen(int x, int y, int height, unsigned char *textureColumnData, int verticalPanning) {
	/** prepare clip buffer access **/
	int clipWindowStart = clipBufferWindowStart[x];
	int clipWindowEnd = clipBufferWindowEnd[x];
	if (clipWindowStart > clipWindowEnd) {
		return;
	}
	
	/** loop over posts of this column **/
	int pixel;
	for (pixel = 0; pixel < height; pixel++) {
		
		/** TODO: optimize clipping **/
		int currentY = y + pixel;
		int currentTexel = verticalPanning + pixel;
		if (currentY >= clipWindowStart && currentY <= clipWindowEnd) {
			
			/**
			 * The AND-127 of the texel position causes vertical texture tiling but requires vertically tiled
			 * textures to be exactly 128 texels tall. This limitation comes from the original DOOM.
			 */
			systemGraphicsDrawPixel(x, currentY, textureColumnData[currentTexel & 127]);
			
		}
		
	}
}

/**
 * See header file for information.
 */
void drawScaledTextureColumnOnScreen(int x, int y, FixedPointNumber sourceHeight, int destinationHeight, unsigned char *textureColumnData, FixedPointNumber verticalPanning) {
	
	/** prepare clip buffer access **/
	int *clipWindowStart = clipBufferWindowStart + x;
	int *clipWindowEnd = clipBufferWindowEnd + x;
	if (*clipWindowStart > *clipWindowEnd) {
		return;
	}
	
	/** prepare scaling **/
	FixedPointNumber texelIncrementPerPixel = sourceHeight / destinationHeight;
	
	/** loop over posts of this column **/
	FixedPointNumber currentTexel = verticalPanning;
	int pixel;
	for (pixel = 0; pixel < destinationHeight; pixel++) {
		
		/** TODO: optimize clipping **/
		int currentY = y + pixel;
		if (currentY >= *clipWindowStart && currentY <= *clipWindowEnd) {

			/**
			 * The AND-127 of the texel position causes vertical texture tiling but requires vertically tiled
			 * textures to be exactly 128 texels tall. This limitation comes from the original DOOM.
			 */
			systemGraphicsDrawPixel(x, currentY, textureColumnData[(currentTexel >> FIXED_POINT_NUMBER_FRACTIONAL_BITS) & 127]);
			
		}
		currentTexel += texelIncrementPerPixel;
		
	}
	
}

/**
 * See header file for information.
 */
void clearClipBuffer() {
	int i;
	for (i=0; i<320; i++) {
		clipBufferWindowStart[i] = 0;
		clipBufferWindowEnd[i] = 199;
	}
}

/**
 * See header file for information.
 */
void insertUpperClipper(int x, int y) {
	if (clipBufferWindowStart[x] < y + 1) {
		clipBufferWindowStart[x] = y + 1;
	}
}

/**
 * See header file for information.
 */
void insertLowerClipper(int x, int y) {
	if (clipBufferWindowEnd[x] > y - 1) {
		clipBufferWindowEnd[x] = y - 1;
	}
}

/**
 * See header file for information.
 */
int getUpperClipperSpace(int x, int y) {
	int delta = y - clipBufferWindowStart[x] + 1;
	return (delta < 0) ? 0 : delta;
}

/**
 * See header file for information.
 */
int getLowerClipperSpace(int x, int y) {
	int delta = clipBufferWindowEnd[x] - y + 1;
	return (delta < 0) ? 0 : delta;
}
