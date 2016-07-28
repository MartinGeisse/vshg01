/*
 *  StatusBar.h
 *  baselib
 *
 *  Created by Martin on 8/26/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

/**
 * Initializes the status bar.
 */
void initializeStatusBar();

/**
 * De-initializes the status bar.
 */
void shutdownStatusBar();

/**
 * Binds the state pointers from the status bar to the game
 * data variables.
 */
void bindStatusBarState();

/**
 * Draws the status bar on screen, using the current game data.
 */
void drawStatusBar();
