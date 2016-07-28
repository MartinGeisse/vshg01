/*
 *  LowlevelGraphics.h
 *  baselib
 *
 *  Created by Martin on 8/15/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

/**
 * Draws a patch onto the screen. The patch must be stored in serialized
 * patch format, as if just loaded from a patch lump.
 */
void drawPatchOnScreen(int x, int y, unsigned char *patch);

/**
 * Draws a patch column onto the screen using a modified destination height.
 * The patch column must be stored in serialized format, as if just loaded
 * from within a patch lump.
 *
 * The source height (height of the patch that contains the column) must be
 * specified here because the enclosing patch itself isn't specified).
 */
void drawScaledPatchColumnOnScreen(int x, int y, int sourceHeight, int destinationHeight, unsigned char *patchColumn);

/**
 * Draws a texture column onto the screen. The texture column must be stored in texture cache format.
 *
 * The texture is tiled, but not scaled. Consequently, only one height indicator is given, and
 * both that height indicate as well as panning are specified as integer values, not fixed-point.
 *
 * The verticalPanning parameter specifies the position in the texture that is associated with the first pixel drawn.
 */
void drawUnscaledTextureColumnOnScreen(int x, int y, int height, unsigned char *textureColumn, int verticalPanning);

/**
 * Draws a texture column onto the screen using the specified destination height.
 * The texture column must be stored in texture cache format.
 *
 * The source height is specified as a fixed-point number in texel coordinates.
 * Its ratio to the texture height (in texels) determines how often the texture
 * is tiled vertically. The destinationHeight specifies the number of pixels to
 * draw.
 *
 * The verticalPanning parameter specifies the position in the texture (as a
 * fixed-point number) that is associated with the first pixel drawn.
 */
void drawScaledTextureColumnOnScreen(int x, int y, FixedPointNumber sourceHeight, int destinationHeight, unsigned char *textureColumn, FixedPointNumber verticalPanning);

/**
 * Clears the clip buffer to empty.
 */
void clearClipBuffer();

/**
 * Inserts an upper clipper into the clip buffer, extending from the specified
 * point to the upper screen border.
 */
void insertUpperClipper(int x, int y);

/**
 * Inserts a lower clipper into the clip buffer, extending from the specified
 * point to the lower screen border.
 */
void insertLowerClipper(int x, int y);

/**
 * Returns the number of unclipped pixels starting at the specified point and going upwards
 * to the upper clipper. This function never returns a negative number, i.e. returns 0
 * even if the point lies somewhere inside the upper clipper or even above the upper screen border.
 */
int getUpperClipperSpace(int x, int y);

/**
 * Returns the number of unclipped pixels starting at the specified point and going downwards
 * to the lower clipper. This function never returns a negative number, i.e. returns 0
 * even if the point lies somewhere inside the lower clipper or even below the lower screen border.
 */
int getLowerClipperSpace(int x, int y);
