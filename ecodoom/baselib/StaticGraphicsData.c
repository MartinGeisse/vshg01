/*
 *  StaticGraphicsData.c
 *  baselib
 *
 *  Created by Martin on 8/12/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

#include <string.h>
#include <alloca.h>
#include "Common.h"
#include "SystemDebug.h"
#include "ZoneAllocator.h"
#include "WadFile.h"
#include "FixedPointNumber.h"
#include "StaticGraphicsData.h"

/************************************************************************************************************************/
/* Texture handling */
/************************************************************************************************************************/

/**
 * The number of textures.
 */
static int textureCount;

/**
 * The actual textures.
 */
static struct TextureDescriptor **textureDescriptors;

/**
 * Translation table for textures, used for animation.
 */
static int *textureTranslationTable;

/**
 * Initializes data for textures.
 */
static void initializeTextures() {
	
	/** this table maps patch indices to lump indices **/
	int patchLumpCount;
	int *patchLumpIndices;
	
	/** Load the patch lump index table **/
	{
		int i;
		unsigned char *lumpStartPointer;
		unsigned char *lumpReadPointer;
		char lumpNameBuffer[9];
		
		lumpReadPointer = lumpStartPointer = getWadFileLumpContentsByName("PNAMES");
		patchLumpCount = fromLittleEndian32(DESERIALIZE(lumpReadPointer, 0, int));
		lumpReadPointer += 4;
		patchLumpIndices = alloca(sizeof(int) * patchLumpCount);
		for (i=0; i<patchLumpCount; i++) {
			strncpy(lumpNameBuffer, (char *)lumpReadPointer, 8);
			patchLumpIndices[i] = findWadFileLumpSafe(lumpNameBuffer);
			lumpReadPointer += 8;
		}
		zoneAllocatorDispose(lumpStartPointer);
	}
	
	/** Load the texture table **/
	{
		int i, j;
		int lumpIndex, lumpSize;
		unsigned char *lumpStartPointer;
		unsigned char *lumpReadPointer;
		
		lumpIndex = findWadFileLump("TEXTURE1");
		lumpSize = getWadFileLumpSize(lumpIndex);
		lumpReadPointer = lumpStartPointer = getWadFileLumpContentsByIndex(lumpIndex);
		textureCount = fromLittleEndian32(DESERIALIZE(lumpReadPointer, 0, int));
		textureDescriptors = zoneAllocatorAllocate(textureCount * sizeof(struct TextureDescriptor *), STATIC_ALLOCATION_TAG, NULL);
		lumpReadPointer += 4;
		
		for (i=0; i<textureCount; i++, lumpReadPointer += 4) {
			int serializedTextureOffset;
			unsigned char *serializedTextureStartPointer;
			unsigned char *serializedTextureReadPointer;
			int textureDescriptorSize;
			struct TextureDescriptor *textureDescriptor;
			struct TexturePatchDescriptor *currentPatch;
			unsigned char *patchCountForColumn;

			/**
			 * Obtain the offset of the serialized texture in the lump, and perform rough
			 * validation. This will not detect all offending offsets, because it is
			 * (offset + size) which must not exceed the lump size, and the size is unknown
			 * at this point.
			 */
			serializedTextureOffset = fromLittleEndian32(DESERIALIZE(lumpReadPointer, 0, int));
			if (serializedTextureOffset > lumpSize) {
				systemFatalError("invalid serialized texture offset: %d", serializedTextureOffset);
			}
			serializedTextureReadPointer = serializedTextureStartPointer = lumpStartPointer + serializedTextureOffset;

			/**
			 * Obtain the texture descriptor size. We must extract the patch count
			 * for this. Note the -1 modifier in the patch count: This accounts for
			 * the fake patch table size of 1 in the texture descriptor struct definition.
			 */
			textureDescriptorSize = sizeof(struct TextureDescriptor) + (fromLittleEndian16(DESERIALIZE(serializedTextureStartPointer, 20, short)) - 1) * sizeof(struct TexturePatchDescriptor);
			textureDescriptor = textureDescriptors[i] = zoneAllocatorAllocate(textureDescriptorSize, STATIC_ALLOCATION_TAG, NULL);

			/**
			 * De-serialize the texture header
			 */
			memcpy(textureDescriptor->name.value, serializedTextureStartPointer, 8);
			textureDescriptor->width = fromLittleEndian16(DESERIALIZE(serializedTextureStartPointer, 12, short));
			textureDescriptor->height = fromLittleEndian16(DESERIALIZE(serializedTextureStartPointer, 14, short));
			textureDescriptor->patchCount = fromLittleEndian16(DESERIALIZE(serializedTextureStartPointer, 20, short));
			
			// printf("reading texture #%d: %8s (%d patches)\n", i, textureDescriptor->name.value, textureDescriptor->patchCount);

			/**
			 * Read the patch table.
			 */
			serializedTextureReadPointer = serializedTextureStartPointer + 22;
			currentPatch = textureDescriptor->patches;
			for (j=0; j<textureDescriptor->patchCount; j++, serializedTextureReadPointer += 10, currentPatch++) {
				int patchIndex, patchLumpIndex;

				currentPatch->offsetX = fromLittleEndian16(DESERIALIZE(serializedTextureReadPointer, 0, short));
				currentPatch->offsetY = fromLittleEndian16(DESERIALIZE(serializedTextureReadPointer, 2, short));
				
				/**
				 * We defer the check if the patch lump is actually available until here so we don't get errors
				 * for patches that are referenced by the patch table but not by any texture.
				 */
				patchIndex = fromLittleEndian16(DESERIALIZE(serializedTextureReadPointer, 4, short));
				patchLumpIndex = patchLumpIndices[patchIndex];
				if (patchLumpIndex == -1) {
					systemFatalError("texture %8s refers to patch %d for which no WAD file lump can be found", textureDescriptor->name.value, patchIndex);
				}
				currentPatch->patchLumpIndex = patchLumpIndex;
				
				// printf("using patch with lump index %d\n", currentPatch->patchLumpIndex);
			}

			/**
			 * These fields are meant for creating a "composite texture", i.e. a flattened version
			 * of all the patches in a texture, and indicate the lump index and data offset
			 * for each column. For a texture column that is affected only by a single patch (most
			 * columns in the game, especially all columns of single-patch textures), these indicate
			 * where the column can be found in the original texture / patch data. For colums
			 * affected by multiple patches, this is the offset into the composite texture buffer.
			 */
			textureDescriptor->columnLumpIndices = zoneAllocatorAllocate(textureDescriptor->width * sizeof(short), STATIC_ALLOCATION_TAG, NULL);
			textureDescriptor->columnOffsets = zoneAllocatorAllocate(textureDescriptor->width * sizeof(unsigned short), STATIC_ALLOCATION_TAG, NULL);

			/**
			 * Generate a bit mask that can be used to horizontally repeat a texture. ANDind with
			 * this mask has the same effect as a "modulo width" operation. This requires that
			 * the width is a power of 2, with no suitable fallback for textures that have
			 * non-PO2 widths. The only offending texture is texture 0, "AASTINKY", a placeholder
			 * that is never used in the game. We ensure at this point that all other textures
			 * have PO2 widths.
			 */
			int widthLog2 = 0;
			while ((1 << widthLog2) < textureDescriptor->width) {
				widthLog2++;
			}
			if ((1 << widthLog2) != textureDescriptor->width) {
				if (i == 0) {
					widthLog2 = 0;
				} else {
					systemFatalError("non-PO2 width for texture %d (%8s)", i, textureDescriptor->name.value);
				}
			}
			textureDescriptor->widthMask = (1 << widthLog2) - 1;
			
			/**
			 * Next, we build meta-information for the composite texture that contains the combined patches.
			 * To do this, we create several data structures:
			 *
			 * - a column lump table. For each column that is affected only by a single patch, this contains
			 *   the lump index of that patch. For columns affected by multiple patches, it contains -1.
			 *
			 * - a column offset table that describes the offset in either the patch lump (for single-patch
			 *   columns) or the composite texture buffer (for multi-patch columns) where texels can be found.
			 *
			 *   DOOM uses an ugly hack here, and distinguishes between two contexts in which textures are
			 *   drawn:
			 *
			 *   - solid texture: The texture must not be transparent, and the column data is a raw sequence
			 *     of texels. This works for multi-patch columns (the raw texels are stored in the composite
			 *     texture buffer) and single-patch, single-post textures with the post extending across the
			 *     whole height of the texture.
			 *
			 *     It does not work if the post does not cover the whole height (single-post transparent
			 *     columns), causing the meta-data of the posts to be interpreted as texels, warping the
			 *     texture, inserting garbage texels and possibly accessing memory behind the end of the
			 *     texture.
			 *
			 *     It does not work for transparent multi-patch textures either, but the effect in this case
			 *     is just that the transparent texels are replaced by non-transparent garbage texels.
			 *     
			 *   - transparent mid texture: The texture is used as a (possibly transparent) mid texture of
			 *     a line. The column data is interpreted as a sequence of posts. This only works for
			 *     columns that refer to a patch, not for columns that refer to the composite texture
			 *     buffer! On other words, it only works for single-patch columns. Transparency works
			 *     as intended.
			 *
			 *     Any multi-patch column would refer to the composite texture buffer which only contains
			 *     raw texels without post meta-data. It would then interpret the texels as post meta-data,
			 *     causing a "post" of random length to be drawn. It might access memory before and after
			 *     the allocated composite texture buffer. My guess is that this causes the dreaded
			 *     "Medusa Effect". The slowdown probably stems from the fact that, while looping through
			 *     arbitrary memory, it takes a long time until the 0xff is found that marks the end of the
			 *     post list for the column.
			 *
			 *     This effect might be fixed by creating proper post meta-data in the composite
			 *     texture buffer. However, I suspect that the original DOOM levels simply avoid
			 *     the problem, so no fix is needed.
			 *     
			 * - the total size needed for the composite texture buffer, computed as the texture height
			 *   multiplied by the number of columns that are affected by multiple patches.
			 *
			 * Note that the composite texture buffer for the texture is not allocated or filled here.
			 * This is done lazily on first access to a column from the buffer, and consequently never
			 * occurs for textures with only a single patch.
			 */
			
			/**
			 * Apply the patches, and remember for each column how many patches affect it. Columns with
			 * multiple patches will have their lump index and offset overwritten by the last patch, but
			 * we will fix this in the loop below -- that is also why we count the number of patches
			 * for each column, so we can detect columns with multiple patches.
			 */
			patchCountForColumn = alloca(textureDescriptor->width * sizeof(unsigned char));
			{
				struct TexturePatchDescriptor *texturePatchDescriptor;
				memset(patchCountForColumn, 0, textureDescriptor->width * sizeof(unsigned char));
				for (j=0, texturePatchDescriptor = textureDescriptor->patches; j<textureDescriptor->patchCount; j++, texturePatchDescriptor++) {
					unsigned char *patchLump;
					int patchWidth;
					int *serializedPatchColumnOffsets;
					int startX, endX, x;
					
					/** load the patch lump **/
					patchLump = getWadFileLumpContentsByIndex(texturePatchDescriptor->patchLumpIndex);
					patchWidth = fromLittleEndian16(DESERIALIZE(patchLump, 0, short));
					serializedPatchColumnOffsets = (int *)(patchLump + 8);
					
					/** determine affected texture columns **/
					startX = texturePatchDescriptor->offsetX;
					endX = startX + patchWidth;
					x = (startX < 0) ? 0 : startX;
					if (endX > textureDescriptor->width) {
						endX = textureDescriptor->width;
					}
					
					/**
					 * Apply the patch, and assume a single post for each column. Multi-patch, multi-post
					 * columns will be updated to point into the composite texture buffer in the loop below.
					 * Single-patch, multi-post columns will not work correctly, as described above.
					 *
					 * The +3 in the column offset skips the 2-byte post header as well as the padding byte
					 * (DOOM puts one padding byte each at the start and end of each post -- nobody knows why).
					 */
					for (; x < endX; x++) {
						patchCountForColumn[x]++;
						textureDescriptor->columnLumpIndices[x] = texturePatchDescriptor->patchLumpIndex;
						textureDescriptor->columnOffsets[x] = fromLittleEndian32(serializedPatchColumnOffsets[x - startX]) + 3;
					}
					
				}
			}

			/**
			 * Now loop over all texture columns again. Columns without a patch cause an error. Columns with a single
			 * patch already have their patch lump and offset set correctly. Columns with multiple patches get their
			 * patch lump set to -1 and their offset pointing into the composite texture buffer. This loop also determines
			 * the size required for that buffer. The buffer is later allocated and filled lazily on first access
			 * to a column that needs it.
			 */
			textureDescriptor->compositeBuffer = NULL;
			textureDescriptor->compositeBufferSize = 0;
			for (j=0; j<textureDescriptor->width; j++) {
				if (patchCountForColumn[j] == 0) {
					systemFatalError("texture %8s has a column that is not affected by any patch", textureDescriptor->name.value);
				} else if (patchCountForColumn[j] > 1) {
					textureDescriptor->columnLumpIndices[j] = -1;
					textureDescriptor->columnOffsets[j] = textureDescriptor->compositeBufferSize;
					textureDescriptor->compositeBufferSize += textureDescriptor->height;
				}
			}
			
		}

		zoneAllocatorDispose(lumpStartPointer);
	}
	
	/** initialize the texture translation table **/
	{
		int i;
		textureTranslationTable = zoneAllocatorAllocate(textureCount * sizeof(int), STATIC_ALLOCATION_TAG, NULL);
		for (i=0; i<textureCount; i++) {
			textureTranslationTable[i] = i;
		}
	}
	
}

/**
 * Disposes of data for textures.
 */
static void disposeTextures() {
}

/**
 * Generates a composite texture for the specified texture.
 */
static void generateCompositeTexture(struct TextureDescriptor *textureDescriptor) {
	int i;
	struct TexturePatchDescriptor *texturePatchDescriptor;
	
	/** sanity check **/
	if (textureDescriptor->compositeBuffer != NULL) {
		systemFatalError("generateCompositeTexture(): texture %8s already has a composite texture", textureDescriptor->name.value);
	}
	
	/** allocate the composite texture buffer **/
	textureDescriptor->compositeBuffer = zoneAllocatorAllocate(textureDescriptor->compositeBufferSize, STATIC_ALLOCATION_TAG, &(textureDescriptor->compositeBuffer));
	
	/** draw all patches, but only to multi-patch columns **/
	for (i = 0, texturePatchDescriptor = textureDescriptor->patches; i<textureDescriptor->patchCount; i++, texturePatchDescriptor++) {
		unsigned char *patchLump;
		int patchWidth;
		int *serializedPatchColumnOffsets;
		int startX, endX, x;
		
		/** load the patch lump **/
		patchLump = getWadFileLumpContentsByIndex(texturePatchDescriptor->patchLumpIndex);
		patchWidth = fromLittleEndian16(DESERIALIZE(patchLump, 0, short));
		serializedPatchColumnOffsets = (int *)(patchLump + 8);
		
		/** determine affected texture columns **/
		startX = texturePatchDescriptor->offsetX;
		endX = startX + patchWidth;
		x = (startX < 0) ? 0 : startX;
		if (endX > textureDescriptor->width) {
			endX = textureDescriptor->width;
		}
		
		/** loop over patch columns **/
		for (; x < endX; x++) {
			int patchColumnOffset;
			unsigned char *post;
			unsigned char *destination;
			
			/** skip single-patch columns **/
			if (textureDescriptor->columnLumpIndices[x] >= 0) {
				continue;
			}
			
			/** locate the column in the patch lump **/
			patchColumnOffset = fromLittleEndian32(serializedPatchColumnOffsets[x - startX]);
			
			/** locate the destination for the texels **/
			destination = textureDescriptor->compositeBuffer + textureDescriptor->columnOffsets[x];
			
			/**
			 * Loop over posts. The difference in memory location between a post and the next one
			 * is the length of the post (found at post[1]), plus the post header size (2 bytes),
			 * plus one padding byte each at the start and end of the post texels (nobody knows why
			 * these exist).
			 */
			for (post = patchLump + patchColumnOffset; *post != 0xff; post = post + post[1] + 4) {
				
				/**
				 * The texels for this post start 3 bytes after the header position: 2 bytes for
				 * the header and one for the beginning padding byte.
				 */
				unsigned char *source = post + 3;
				
				/** obtain the number of texels in this post **/
				int postLength = post[1];
				
				/**
				 * Determine the destination offset as the combined offset of the patch in the
				 * texture and the post in the patch.
				 */
				int destinationOffset = texturePatchDescriptor->offsetY + post[0];
				
				/** clip against top and bottom of the texture **/
				if (destinationOffset < 0) {
					postLength += destinationOffset;
					destinationOffset = 0;
				}
				if (destinationOffset + postLength > textureDescriptor->height) {
					postLength = textureDescriptor->height - destinationOffset;
				}
				
				/** copy the texels from the patch into the texture **/
				memcpy(destination + destinationOffset, source, postLength);

			}
			
		}
		
	}
	
	/** the composite texture buffer is a cached value, we just locked it for drawing **/
	zoneAllocatorChangeTag(textureDescriptor->compositeBuffer, CACHED_ALLOCATION_TAG);
	
}

/**
 * See header file for information.
 */
int getTextureCount() {
	return textureCount;
}

/**
 * See header file for information.
 */
struct TextureDescriptor *getTexture(int textureIndex) {
	if (textureIndex < 0 || textureIndex >= textureCount) {
		systemFatalError("getTexture(): trying to get texture at invalid index: %d", textureIndex);
	}
	return textureDescriptors[textureIndex];
}

/**
 * See header file for information.
 */
int getTextureTranslation(int textureIndex) {
	if (textureIndex < 0 || textureIndex >= textureCount) {
		systemFatalError("getTextureTranslation(): trying to read texture translation at invalid index: %d", textureIndex);
	}
	return textureTranslationTable[textureIndex];
}

/**
 * See header file for information.
 */
void setTextureTranslation(int textureIndex, int mappedToTextureIndex) {
	if (textureIndex < 0 || textureIndex >= textureCount) {
		systemFatalError("setTextureTranslation(): trying to set texture translation at invalid index: %d", textureIndex);
	}
	if (mappedToTextureIndex < 0 || mappedToTextureIndex >= textureCount) {
		systemFatalError("setTextureTranslation(): trying to set texture translation target at index %d to invalid index: %d", textureIndex, mappedToTextureIndex);
	}
	textureTranslationTable[textureIndex] = mappedToTextureIndex;
}

/**
 * See header file for information.
 */
int getTextureIndexForName(const char *name) {
	int index = getTextureIndexForNameSafe(name);
	if (index == -1) {
		systemFatalError("texture not found: %s", name);
	}
	return index;
}

/**
 * See header file for information.
 */
int getTextureIndexForNameSafe(const char *name) {
	int nameLength, i;
	struct DoomName internalName;
	
	/** return a placeholder texture for the "no texture" marker name **/
	if (name[0] == '-' && name[1] == 0) {
		return 0;
	}
	
	/** convert the argument to an internal 8-character name **/
	nameLength = strlen(name);
	if (nameLength < 8) {
		memcpy(internalName.value, name, nameLength);
		internalName.value[nameLength] = 0;
	} else {
		memcpy(internalName.value, name, 8);
	}
	
	/** search for the name **/
	for (i=0; i<textureCount; i++) {
		if (doomNamesEqual(&textureDescriptors[i]->name, &internalName)) {
			return i;
		}
	}
	return -1;
	
}

/**
 * See header file for information.
 */
unsigned char *getTextureColumnTexels(struct TextureDescriptor *textureDescriptor, int column) {
	int lumpIndex;
	int offset;
	
	/** repeat the texture horizontally **/
	column &= textureDescriptor->widthMask;
	
	/** obtain lump index and offset **/
	lumpIndex = textureDescriptor->columnLumpIndices[column];
	offset = textureDescriptor->columnOffsets[column];
	
	/** single-patch columns are easy **/
	if (lumpIndex >= 0) {
		unsigned char *lump = getWadFileLumpContentsByIndex(lumpIndex);
		return lump + offset;
	}
	
	/** create a composite texture if not yet present **/
	if (textureDescriptor->compositeBuffer == NULL) {
		generateCompositeTexture(textureDescriptor);
	}

	/** obtain texels from the composite texture **/
	return textureDescriptor->compositeBuffer + offset;

}

/************************************************************************************************************************/
/* Flats handling */
/************************************************************************************************************************/

/**
 * Computes the number of flats from the marker lump indices.
 */
#define FLAT_COUNT (lastFlatLumpIndex - firstFlatLumpIndex + 1)

/**
 * Lump index of the first lump after the F_START marker lump that marks the beginning of the flats in the WAD file.
 */
static int firstFlatLumpIndex;

/**
 * Lump index of the last lump before the F_END marker lump that marks the end of the flats in the WAD file.
 */
static int lastFlatLumpIndex;

/**
 * Translation table for flats, used for animation. TODO: This was allocated with one additional element
 * in the original DOOM code - why? needed? for what?
 */
static int *flatTranslationTable;

/**
 * Initializes data for flats.
 */
static void initializeFlats() {
	int i;
	firstFlatLumpIndex = findWadFileLump("F_START") + 1;
	lastFlatLumpIndex = findWadFileLump("F_END") - 1;
	flatTranslationTable = zoneAllocatorAllocate(sizeof(int) * FLAT_COUNT, STATIC_ALLOCATION_TAG, NULL);
	for (i=0; i<FLAT_COUNT; i++) {
		flatTranslationTable[i] = i;
	}
}

/**
 * Disposes of data for flats.
 */
static void disposeFlats() {
	if (flatTranslationTable != NULL) {
		zoneAllocatorDispose(flatTranslationTable);
		flatTranslationTable = NULL;
	}
	// flat data is held by the WAD file, not by us
}

/**
 * See header file for information.
 */
int getFlatTranslation(int flatIndex) {
	if (flatIndex < 0 || flatIndex >= FLAT_COUNT) {
		systemFatalError("getFlatTranslation(): trying to read flat translation at invalid index: %d", flatIndex);
	}
	return flatTranslationTable[flatIndex];
}

/**
 * See header file for information.
 */
void setFlatTranslation(int flatIndex, int mappedToFlatIndex) {
	if (flatIndex < 0 || flatIndex >= FLAT_COUNT) {
		systemFatalError("setFlatTranslation(): trying to set flat translation at invalid index: %d", flatIndex);
	}
	if (mappedToFlatIndex < 0 || mappedToFlatIndex >= FLAT_COUNT) {
		systemFatalError("setFlatTranslation(): trying to set flat translation target at index %d to invalid index: %d", flatIndex, mappedToFlatIndex);
	}
	flatTranslationTable[flatIndex] = mappedToFlatIndex;
}

/**
 * See header file for information.
 */
int getTranslatedLumpFlatIndex(int flatIndex) {
	if (flatIndex < 0 || flatIndex >= FLAT_COUNT) {
		systemFatalError("getTranslatedLumpFlatIndex(): trying to read flat translation at invalid index: %d", flatIndex);
	}
	return firstFlatLumpIndex + flatTranslationTable[flatIndex];
}

/**
 * See header file for information.
 */
int getFlatCount() {
	return FLAT_COUNT;
}

/**
 * See header file for information.
 */
int getFlatIndexForName(const char *name) {
	return findWadFileLump(name) - firstFlatLumpIndex;
}

/************************************************************************************************************************/
/* Sprite handling */
/************************************************************************************************************************/

/**
 * Computes the number of sprites from the marker lump indices.
 */
#define SPRITE_COUNT (lastSpriteLumpIndex - firstSpriteLumpIndex + 1)

/**
 * Lump index of the first lump after the S_START marker lump that marks the beginning of the sprites in the WAD file.
 */
static int firstSpriteLumpIndex;

/**
 * Lump index of the last lump before the S_END marker lump that marks the end of the sprites in the WAD file.
 */
static int lastSpriteLumpIndex;

/**
 * The sprite descriptors.
 */
static struct SpriteDescriptor *spriteDescriptors;

/**
 * Initializes data for sprites.
 */
static void initializeSprites() {
	int i;
	firstSpriteLumpIndex = findWadFileLump("S_START") + 1;
	lastSpriteLumpIndex = findWadFileLump("S_END") - 1;
	spriteDescriptors = zoneAllocatorAllocate(SPRITE_COUNT * sizeof(struct SpriteDescriptor), STATIC_ALLOCATION_TAG, NULL);
	for (i=0; i<SPRITE_COUNT; i++) {
		int lumpIndex = firstSpriteLumpIndex + i;
		unsigned char *lump = getWadFileLumpContentsByIndex(lumpIndex);
		struct SpriteDescriptor *spriteDescriptor = spriteDescriptors + i;
		spriteDescriptor->spritewidth = fromLittleEndian16(DESERIALIZE(lump, 0, short)) << FIXED_POINT_NUMBER_FRACTIONAL_BITS;
		spriteDescriptor->spriteoffset = fromLittleEndian16(DESERIALIZE(lump, 4, short)) << FIXED_POINT_NUMBER_FRACTIONAL_BITS;
		spriteDescriptor->spritetopoffset = fromLittleEndian16(DESERIALIZE(lump, 6, short)) << FIXED_POINT_NUMBER_FRACTIONAL_BITS;
	}
}

/**
 * Disposes of data for sprites.
 */
static void disposeSprites() {
	if (spriteDescriptors != NULL) {
		zoneAllocatorDispose(spriteDescriptors);
		spriteDescriptors = NULL;
	}
}

/**
 * Returns the number of sprites.
 */
int getSpriteCount() {
	return SPRITE_COUNT;
}

/**
 * Returns the lump index for the specified sprite index.
 */
int spriteIndexToLumpIndex(int spriteIndex) {
	if (spriteIndex < 0 || spriteIndex >= SPRITE_COUNT) {
		systemFatalError("spriteIndexToLumpIndex(): trying to get lump index for sprite at invalid index: %d", spriteIndex);
	}
	return spriteIndex + firstSpriteLumpIndex;
}

/**
 * Returns the sprite index for the specified lump index.
 */
int lumpIndexToSpriteIndex(int lumpIndex) {
	int spriteIndex = lumpIndex - firstSpriteLumpIndex;
	if (spriteIndex < 0 || spriteIndex >= SPRITE_COUNT) {
		systemFatalError("lumpIndexToSpriteIndex(): trying to get sprite index for non-sprite lump index: %d", lumpIndex);
	}
	return spriteIndex;
}

/**
 * Returns the sprite descriptor for the specified sprite index.
 */
struct SpriteDescriptor *getSpriteDescriptor(int spriteIndex) {
	if (spriteIndex < 0 || spriteIndex >= SPRITE_COUNT) {
		systemFatalError("getSpriteDescriptor(): trying to get sprite descriptor for sprite at invalid index: %d", spriteIndex);
	}
	return spriteDescriptors + spriteIndex;
}

/************************************************************************************************************************/
/* Color map handling */
/************************************************************************************************************************/

/**
 * The number of color maps.
 */
static int colorMapCount;

/**
 * The COLORMAP lump which simply contains all color maps in sequence.
 */
static unsigned char *colorMaps;

/**
 * Initializes data for color maps.
 */
static void initializeColorMaps() {
	int lumpIndex = findWadFileLump("COLORMAP");
	int lumpSize = getWadFileLumpSize(lumpIndex);
	if (lumpSize % 256 != 0) {
		systemFatalError("Invalid COLORMAP lump size is not a multiple of 256: %d", lumpSize);
	}
	colorMaps = getWadFileLumpContentsByIndex(lumpIndex);
	colorMapCount = lumpSize / 256;
}

/**
 * Disposes of data for color maps.
 */
static void disposeColorMaps() {
	// data is held by the WAD file, not by us
}

/**
 * See header file for information.
 */
int getColorMapCount() {
	return colorMapCount;
}

/**
 * See header file for information.
 */
unsigned char *getColorMap(int index) {
	if (index < 0 || index >= colorMapCount) {
		systemFatalError("Trying to get color map for invalid index %d, number of color maps is %d", index, colorMapCount);
	}
	return colorMaps + index * 256;
}

/************************************************************************************************************************/
/* Lighting tables */
/************************************************************************************************************************/

// TODO: table-ize this for faster access

/**
 * See header file for information.
 */
int getLightingColorMapIndexFromScale(int worldLight, int scaleIndex) {
	int colorMapIndex;
	
	/** cap worldLight to its limits **/
	if (worldLight < 0) {
		worldLight = 0;
	} else if (worldLight > 15) {
		worldLight = 15;
	}
	
	/** cap distanceIndex to its limits **/
	if (scaleIndex < 0) {
		scaleIndex = 0;
	} else if (scaleIndex > 47) {
		scaleIndex = 47;
	}
	
	/** determine the color map index **/
	colorMapIndex = (15 - worldLight) * 4 - scaleIndex * 2;
	
	/** cap the result to its limits **/
	if (colorMapIndex < 0) {
		colorMapIndex = 0;
	} else if (colorMapIndex > 31) {
		colorMapIndex = 31;
	}
	
	return colorMapIndex;
	
}

/**
 * See header file for information.
 */
int getLightingColorMapIndexFromDistance(int worldLight, FixedPointNumber distance) {
	int distanceIndex, colorMapIndex;
	
	/** cap worldLight to its limits **/
	if (worldLight < 0) {
		worldLight = 0;
	} else if (worldLight > 15) {
		worldLight = 15;
	}

	/** transform the distance to an index (we don't need to cap to a maximum since the colorMapIndex is capped to 0 anyway) **/
	if (distance == 0) {
		distance = 1;
	}
	distanceIndex = fixedPointNumberDivide(FIXED_POINT_NUMBER_ONE >> 5, distance);
	if (distanceIndex < 0) {
		distanceIndex = 0;
	}
	
	/** determine the color map index **/
	colorMapIndex = (15 - worldLight) * 4 - distanceIndex;
	
	/** cap the result to its limits **/
	if (colorMapIndex < 0) {
		colorMapIndex = 0;
	} else if (colorMapIndex > 31) {
		colorMapIndex = 31;
	}

	return colorMapIndex;

}

/************************************************************************************************************************/
/* Front-end */
/************************************************************************************************************************/

/**
 * See header file for information.
 */
void initializeStaticGraphicsData() {
	initializeTextures();
	initializeFlats();
	initializeSprites();
	initializeColorMaps();
}

/**
 * See header file for information.
 */
void shutdownStaticGraphicsData() {
	disposeTextures();
	disposeFlats();
	disposeSprites();
	disposeColorMaps();
}

