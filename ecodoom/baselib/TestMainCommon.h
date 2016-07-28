/*
 *  TestMainCommon.h
 *  baselib
 *
 *  Created by Martin on 8/15/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

/**
 * The location of the DOOM WAD file.
 */
#define WAD_FILE_PATH	"/Users/martin/DOOM1.WAD"

/**
 * The width used for screen initialization
 */
extern int testMainScreenWidth;

/**
 * The height used for screen initialization
 */
extern int testMainScreenHeight;

/**
 * Prints white-on-black text to the upper left screen corner.
 */
void printDescription(const char *description);

/**
 * Initializes the Allegro subsystems needed for testing.
 */
void initializeAllegroStuff();
