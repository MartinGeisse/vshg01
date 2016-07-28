/*
 *  Automap.h
 *  baselib
 *
 *  Created by Martin on 8/23/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

/**
 * Initializes the automap subsystem.
 */
void initializeAutomap();

/**
 * De-initializes the automap subsystem.
 */
void shutdownAutomap();

/**
 * Prepares automap data for a newly loaded level.
 */
void prepareAutomap();

/**
 * Handles a frame for the automap.
 */
void runAutomapTicker();

/**
 * Draws the automap.
 */
void drawAutomap();

/**
 * Returns 1 if the automap has "captured" the movement keys, 0 if not.
 * This is important to other game code that needs to know whether the
 * movement keys should cause the player to move.
 */
int getAutomapMovementKeysCaptured();
