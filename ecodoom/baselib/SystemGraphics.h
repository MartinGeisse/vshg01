/*
 *  SystemGraphics.h
 *  baselib
 *
 *  Created by Martin on 8/12/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

/**
 * Initializes the graphics system.
 */
void systemGraphicsInitialize();

/**
 * De-initializes the graphics system.
 */
void systemGraphicsShutdown();

/**
 * Clears the screen to black.
 */
void clearScreen();

/**
 * Copies 768 bytes starting at the specified palette pointer
 * into the graphics system and uses them as the RGB values
 * for color indices.
 *
 * The palette is a global object used when sending the screen
 * contents to the monitor. Changing the palette affects
 * graphical primitives already drawn.
 *
 * TODO: this is currently implemented wrongly: The palette currently
 * works like the colormap, in that it only affects primitives drawn
 * in the future.
 */
void systemGraphicsSetPalette(unsigned char *palette);

/**
 * Installs a remapping table for color indices. The color of each pixel
 * of each primitive is first remapped by this colormap, then used to look
 * up the actual color using the palette.
 *
 * The initial state is to omit the remapping step. This state can be
 * restored by calling this function with NULL as the argument.
 *
 * Note that, unlike the palette, the colormap is referenced, not copied. Also,
 * the colormap is applied when drawing primitives, not when sending them to
 * the monitor. Changing the colormap therefore only affects primitives drawn
 * _after_ the change, not those already drawn.
 */
void systemGraphicsSetColormap(unsigned char *colormap);

/**
 * Draws a single pixel using the specified color index to look up the
 * color in the current palette.
 */
void systemGraphicsDrawPixel(int x, int y, unsigned char colorIndex);

/**
 * Draws a line using the specified color index to look up the
 * color in the current palette.
 */
void systemGraphicsDrawLine(int x1, int y1, int x2, int y2, unsigned char colorIndex);
