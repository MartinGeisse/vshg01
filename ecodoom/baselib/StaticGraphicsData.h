/*
 *  StaticGraphicsData.h
 *  baselib
 *
 *  Created by Martin on 8/12/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

/************************************************************************************************************************/
/* Front-end */
/************************************************************************************************************************/

/**
 * Initializes the static graphics data system. This precaches meta-information
 * about the actual graphics.
 */
void initializeStaticGraphicsData();

/**
 * Cleans up data allocated by the static graphics system, i.e. meta-data as well
 * as cached graphics data.
 */
void shutdownStaticGraphicsData();

/************************************************************************************************************************/
/* Flats handling */
/************************************************************************************************************************/

/**
 * Returns the translated flat index for the specified flat index.
 */
int getFlatTranslation(int flatIndex);

/**
 * Sets the translated flat index for the specified flat index.
 */
void setFlatTranslation(int flatIndex, int mappedToFlatIndex);

/**
 * Returns the translated lump index for the specified flat index.
 * TODO: should be called getTranslatedFlatLumpIndex
 */
int getTranslatedLumpFlatIndex(int flatIndex);

/**
 * Returns the number of valid flat indices, starting at 0. This can be
 * interpreted as the total nubmer of flats, although doing so would
 * could each frame of an animated flat independently.
 */
int getFlatCount();

/**
 * Returns the flat index for the flat with the specified flat (lump) name.
 * 
 * This is basically the lump index of the flat measured relative to the
 * lump index of the flat start marker.
 */
int getFlatIndexForName(const char *name);

/************************************************************************************************************************/
/* Texture handling */
/************************************************************************************************************************/

/**
 * A texture patch, which is basically a reference to a patch
 * descriptor with a translation offset applied.
 */
struct TexturePatchDescriptor {
	
	/**
	 * x translation offset
	 */
	int offsetX;
	
	/**
	 * y translation offset
	 */
	int offsetY;
	
	/**
	 * Lump index of the patch to draw at that offset.
	 */
	int patchLumpIndex;
	
};

/**
 * A texture, which is composed of one or more patches. This is a variable-sized
 * structure since it can contain an unbounded number of patch descriptors.
 */
struct TextureDescriptor {
	
	/**
	 * Texture name.
	 */
	struct DoomName name;
	
	/**
	 * Texture width.
	 */
	short width;
	
	/**
	 * Texture height.
	 */
	short height;
	
	/**
	 * ANDing with this mask has the same effect as a
	 * "modulo width" operation.
	 */
	int widthMask;
	
	/**
	 * TODO: This is just the (height) field scalled by 2^16 to convert it
	 * to a fixed-point number. needed?
	 */
	// FixedPointNumber textureheight;		
	
	/**
	 * The lump index where the patches for each column can be found, or -1 to
	 * indicate that this column cannot be built from patches from a lump (i.e.
	 * the column must be read from the composite texture buffer instead.
	 */
	short *columnLumpIndices;
	
	/**
	 * For each column, this stores
	 * - the ??? if the corresponsing column lump exists
	 * - the offset into the composite texture buffer if not (the lump index is -1)
	 */
	unsigned short *columnOffsets;
	
	/**
	 * Total size of the composite texture buffer. Note that this may be set with no
	 * buffer present yet because the buffer is allocated and filled lazily.
	 */
	int compositeBufferSize;
	
	/**
	 * The composite texture buffer, or NULL if not yet initialized.
	 */
	unsigned char *compositeBuffer;
	
	/**
	 * Number of patches contained in this texture.
	 */
	short patchCount;
	
	/**
	 * The actual patches.
	 */
	struct TexturePatchDescriptor patches[1];
    
};

/**
 * Returns the number of textures.
 */
int getTextureCount();

/**
 * Returns the texture for the specified index.
 */
struct TextureDescriptor *getTexture(int textureIndex);

/**
 * Returns the translated texture index for the specified texture index.
 */
int getTextureTranslation(int textureIndex);

/**
 * Sets the translated texture index for the specified texture index.
 */
void setTextureTranslation(int textureIndex, int mappedToTextureIndex);

/**
 * Returns the texture index for the specified texture name. Note that neither the texture
 * index nor the texture name select a WAD file lump; both select a de-serialized entry
 * from the texture collection lump.
 */
int getTextureIndexForName(const char *name);

/**
 * Like getTextureIndexForName(name), but does not signal a fatal error if the texture
 * cannot be found. Instead, this function returns -1 if not found.
 */
int getTextureIndexForNameSafe(const char *name);

/**
 * Returns a pointer to the texels for the specified texture and column. This function
 * creates a composite texture if needed.
 */
unsigned char *getTextureColumnTexels(struct TextureDescriptor *textureDescriptor, int column);

/************************************************************************************************************************/
/* Sprite handling */
/************************************************************************************************************************/

/**
 * Meta-data for sprites.
 */
struct SpriteDescriptor {
	
	/**
	 * TODO: ???
	 */
	FixedPointNumber spritewidth;	
	
	/**
	 * TODO: ???
	 */
	FixedPointNumber spriteoffset;
	
	/**
	 * TODO: ???
	 */
	FixedPointNumber spritetopoffset;
	
};

/**
 * Returns the number of sprites.
 */
int getSpriteCount();

/**
 * Returns the lump index for the specified sprite index.
 */
int spriteIndexToLumpIndex(int spriteIndex);

/**
 * Returns the sprite index for the specified lump index.
 */
int lumpIndexToSpriteIndex(int lumpIndex);

/**
 * Returns the sprite descriptor for the specified sprite index.
 */
struct SpriteDescriptor *getSpriteDescriptor(int spriteIndex);

/************************************************************************************************************************/
/* Color map handling */
/************************************************************************************************************************/

/**
 * Returns the number of color maps.
 */
int getColorMapCount();

/**
 * Returns a pointer to the color map with the specified index. The color map is
 * a byte array with 256 entries that re-maps color index values.
 */
unsigned char *getColorMap(int index);

/************************************************************************************************************************/
/* Lighting tables */
/************************************************************************************************************************/

/**
 * Returns the color map that is appropriate for the specified lighting parameters.
 * 
 * The worldLight is the view-independent brightness of an object, in the range 0..15.
 * Larger numbers mean "brighter".
 *
 * The scaleIndex is a modifier to make distant objects look darker, and is in the
 * range 0..47. Larger numbers mean "the object is larger on the screen", i.e. closer, 
 * and thus brighter.
 *
 * Both parameters are capped to the allowed range by this function.
 */
int getLightingColorMapIndexFromScale(int worldLight, int scaleIndex);

/**
 * Returns the color map that is appropriate for the specified lighting parameters.
 * 
 * The worldLight is the view-independent brightness of an object, in the range 0..15.
 * Larger numbers mean "brighter".
 *
 * The distance is intended to reflect the world-scale distance to the object to draw.
 * Larger numbers mean "the object is farther away", and thus darker.
 *
 * Both parameters are capped to the allowed range by this function.
 */
int getLightingColorMapIndexFromDistance(int worldLight, FixedPointNumber distance);
