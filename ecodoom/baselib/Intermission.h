/*
 *  Intermission.h
 *  baselib
 *
 *  Created by Martin on 8/27/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

/**
 * Initializes static data for the intermission screen.
 */
void initializeIntermissionData();

/**
 * Disposes of static data for the intermission screen.
 */
void disposeIntermissionData();

/**
 * Prepares the intermission data structures for a new intermission.
 * This function must be called after the player data structures
 * have been set up for a level transit.
 */
void prepareIntermission();

/**
 * Draws the intermission screen.
 */
void drawIntermission();

/**
 * The frame logic for the intermission screen. This basically
 * proceeds with the animations and stat counting, and checks
 * for user input to accelerate or leave the intermission.
 *
 * Returns 1 if the intermission screen is finished, 0 if not.
 */
int runIntermissionTicker();
