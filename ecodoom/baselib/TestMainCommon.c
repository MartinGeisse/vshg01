/*
 *  TestMainCommon.c
 *  baselib
 *
 *  Created by Martin on 8/15/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

#include <allegro.h>
#include "SystemGraphics.h"
#include "TestMainCommon.h"

/**
 * See header file for information.
 */
int testMainScreenWidth = 640;

/**
 * See header file for information.
 */
int testMainScreenHeight = 480;

/**
 * See header file for information.
 */
void printDescription(const char *description) {
	textout_ex(screen, font, description, 0, 0, makecol(255, 255, 255), makecol(0, 0, 0));
}

/**
 * See header file for information.
 */
void initializeAllegroStuff() {
	allegro_init();
	set_color_depth(24);
	set_gfx_mode(GFX_AUTODETECT, testMainScreenWidth, testMainScreenHeight, 0, 0);
	install_keyboard();
	acquire_screen();
	clearScreen();
	release_screen();
}
