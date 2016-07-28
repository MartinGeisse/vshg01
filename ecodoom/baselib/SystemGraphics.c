/*
 *  SystemGraphics.c
 *  baselib
 *
 *  Created by Martin on 8/12/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

#include <allegro.h>
#include "SystemGraphics.h"

/**
 * The palette used by the graphics system.
 */
static int systemGraphicsPalette[256];

/**
 * The default colormap.
 */
static unsigned char systemGraphicsDefaultColormap[256];

/**
 * The current colormap.
 */
static unsigned char *systemGraphicsColormap;

/**
 * See header file for information.
 */
void systemGraphicsInitialize() {
	int i;
	systemGraphicsColormap = systemGraphicsDefaultColormap;
	for (i=0; i<256; i++) {
		systemGraphicsDefaultColormap[i] = i;
	}
}

/**
 * See header file for information.
 */
void systemGraphicsShutdown() {
}

/**
 * See header file for information.
 */
void clearScreen() {
	rectfill(screen, 0, 0, screen->w - 1, screen->h - 1, makecol(0, 0, 0));
}

/**
 * See header file for information.
 */
void systemGraphicsSetPalette(unsigned char *palette) {
	int i;
	for (i=0; i<256; i++) {
		unsigned char r = palette[0];
		unsigned char g = palette[1];
		unsigned char b = palette[2];
		palette += 3;
		systemGraphicsPalette[i] = makecol(r, g, b);
	}
}

/**
 * See header file for information.
 */
void systemGraphicsSetColormap(unsigned char *colormap) {
	systemGraphicsColormap = (colormap == NULL ? systemGraphicsDefaultColormap : colormap);
}

/**
 * See header file for information.
 */
void systemGraphicsDrawPixel(int x, int y, unsigned char colorIndex) {
	putpixel(screen, x, y, systemGraphicsPalette[systemGraphicsColormap[colorIndex]]);
}

/**
 * See header file for information.
 */
void systemGraphicsDrawLine(int x1, int y1, int x2, int y2, unsigned char colorIndex) {
	line(screen, x1, y1, x2, y2, systemGraphicsPalette[systemGraphicsColormap[colorIndex]]);
}

