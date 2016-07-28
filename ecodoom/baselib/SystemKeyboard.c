/*
 *  SystemKeyboard.c
 *  baselib
 *
 *  Created by Martin on 8/23/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

#include <string.h>
#include "allegro.h"
#include "SystemKeyboard.h"

/**
 * The current key states.
 */
static char currentKeyStates[KEY_MAX];

/**
 * The previous key states.
 */
static char previousKeyStates[KEY_MAX];

/**
 * The mapping table from virtual key codes to Allegro key codes.
 */
static int keyCodeMappingTable[] = {

	// DOOM_KEY_CODE_LEFT
	KEY_LEFT,
	
	// DOOM_KEY_CODE_RIGHT
	KEY_RIGHT,
	
	// DOOM_KEY_CODE_UP
	KEY_UP,
	
	// DOOM_KEY_CODE_DOWN
	KEY_DOWN,
	
	// DOOM_KEY_CODE_FIRE
	KEY_LCONTROL,
	
	// DOOM_KEY_CODE_USE,
	KEY_SPACE,
	
	// DOOM_KEY_CODE_MENU_ENABLE
	KEY_ESC,
	
	// DOOM_KEY_CODE_MENU_SELECT
	KEY_ENTER,
	
	// DOOM_KEY_CODE_AUTOMAP_ENABLE
	KEY_TAB,
	
	// DOOM_KEY_CODE_AUTOMAP_ZOOM_IN
	KEY_EQUALS,
	
	// DOOM_KEY_CODE_AUTOMAP_ZOOM_OUT
	KEY_MINUS,
	
	// DOOM_KEY_CODE_AUTOMAP_TOGGLE_FOLLOW_PLAYER
	KEY_F,
	
	// DOOM_KEY_CODE_AUTOMAP_TOGGLE_OVERVIEW
	KEY_0,
	
};

/**
 * See header file for information.
 */
void sampleKeyboard() {
	/**
	 * Cast away "volatile" from the allegro keys array. For some odd reason, memcpy()
	 * formally declares that it cannot handle volatile input data...
	 */
	char *allegroKeys = (char*)key;
	memcpy(previousKeyStates, currentKeyStates, KEY_MAX * sizeof(char));
	memcpy(currentKeyStates, allegroKeys, KEY_MAX * sizeof(char));
}

/**
 * See header file for information.
 */
int getCurrentKeyState(enum DoomKeyCode keyCode) {
	return currentKeyStates[keyCodeMappingTable[keyCode]];
}

/**
 * See header file for information.
 */
int getPreviousKeyState(enum DoomKeyCode keyCode) {
	return previousKeyStates[keyCodeMappingTable[keyCode]];
}

/**
 * See header file for information.
 */
int getKeyJustPressed(enum DoomKeyCode keyCode) {
	return (currentKeyStates[keyCodeMappingTable[keyCode]] && !previousKeyStates[keyCodeMappingTable[keyCode]]);
}

/**
 * See header file for information.
 */
int getKeyJustReleased(enum DoomKeyCode keyCode) {
	return (!currentKeyStates[keyCodeMappingTable[keyCode]] && previousKeyStates[keyCodeMappingTable[keyCode]]);
}
