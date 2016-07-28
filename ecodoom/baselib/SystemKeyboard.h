/*
 *  SystemKeyboard.h
 *  baselib
 *
 *  Created by Martin on 8/23/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

/**
 * The virtual key codes used by the game.
 */
enum DoomKeyCode {
	
	DOOM_KEY_CODE_LEFT = 0,
	
	DOOM_KEY_CODE_RIGHT,
	
	DOOM_KEY_CODE_UP,
	
	DOOM_KEY_CODE_DOWN,
	
	DOOM_KEY_CODE_FIRE,

	DOOM_KEY_CODE_USE,

	DOOM_KEY_CODE_MENU_ENABLE,

	DOOM_KEY_CODE_MENU_SELECT,

	DOOM_KEY_CODE_AUTOMAP_ENABLE,
	
	DOOM_KEY_CODE_AUTOMAP_ZOOM_IN,
	
	DOOM_KEY_CODE_AUTOMAP_ZOOM_OUT,
	
	DOOM_KEY_CODE_AUTOMAP_TOGGLE_FOLLOW_PLAYER,
	
	DOOM_KEY_CODE_AUTOMAP_TOGGLE_OVERVIEW
	
};

/**
 * Samples the current key states and saves them for synchronous use, and
 * also saves the previous states to detect state edges.
 */
void sampleKeyboard();

/**
 * Returns the current key state for the specified virtual key code.
 */
int getCurrentKeyState(enum DoomKeyCode keyCode);

/**
 * Returns the previous key state for the specified virtual key code.
 */
int getPreviousKeyState(enum DoomKeyCode keyCode);

/**
 * Returns 1 if the key just made a "down" transition, 0 otherwise.
 */
int getKeyJustPressed(enum DoomKeyCode keyCode);

/**
 * Returns 1 if the key just made an "up" transition, 0 otherwise.
 */
int getKeyJustReleased(enum DoomKeyCode keyCode);
