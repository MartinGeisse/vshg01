/*
 *  StatusBar.c
 *  baselib
 *
 *  Created by Martin on 8/26/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

#include "Common.h"
#include "ZoneAllocator.h"
#include "WadFile.h"
#include "Widget.h"
#include "LowlevelGraphics.h"
#include "StatusBar.h"

/**
 * The y position of the upper border of the status bar on screen.
 */
#define POSITION_STATUS_BAR_X 0
#define POSITION_STATUS_BAR_Y 168

/**
 * The position of the weapons background.
 */
#define POSITION_WEAPONS_BACKGROUND_X 104
#define POSITION_WEAPONS_BACKGROUND_Y 168

/**
 * The number of different player injury levels that are shown
 * by using a different set of player face icons.
 */
#define PLAYER_INJURY_LEVEL_COUNT 5

/**
 * The offset of the straight player faces within each injury level.
 */
#define PLAYER_STRAIGHT_FACE_OFFSET 0

/**
 * The number of animation frames for the straight player face
 * (other faces than straight are not animated). This count applies
 * individually to each injury level.
 */
#define PLAYER_STRAIGHT_FACE_COUNT 3

/**
 * The offset of the right turned player face within each injury level.
 */
#define PLAYER_FACE_TURNED_RIGHT_OFFSET (PLAYER_STRAIGHT_FACE_OFFSET + PLAYER_STRAIGHT_FACE_COUNT)

/**
 * The offset of the left turned player face within each injury level.
 */
#define PLAYER_FACE_TURNED_LEFT_OFFSET (PLAYER_FACE_TURNED_RIGHT_OFFSET + 1)

/**
 * The offset of the twisted-with-pain player face within each injury level.
 * This face is used when the player gets a lot of damage at once.
 */
#define PLAYER_FACE_PAIN_OFFSET (PLAYER_FACE_TURNED_LEFT_OFFSET + 1)

/**
 * The offset of the evil grinning player face within each injury level.
 * This face is used when the player picks up a new weapon.
 */
#define PLAYER_FACE_EVIL_GRIN_OFFSET (PLAYER_FACE_PAIN_OFFSET + 1)

/**
 * The offset of the rampaging player face within each injury level.
 * This face is used when the player doesn't stop firing for some time.
 */
#define PLAYER_FACE_RAMPAGE_OFFSET (PLAYER_FACE_EVIL_GRIN_OFFSET + 1)

/**
 * This is the total number of faces per injury level.
 */
#define PLAYER_FACES_PER_INJURY_LEVEL (PLAYER_FACE_RAMPAGE_OFFSET + 1)

/**
 * This face index is independent of injury levels and is used when the
 * player is invulnerable.
 */
#define PLAYER_FACE_INVULNERABLE (PLAYER_INJURY_LEVEL_COUNT * PLAYER_FACES_PER_INJURY_LEVEL)

/**
 * This face index is independent of injury levels and is used when the
 * player is dead.
 */
#define PLAYER_FACE_DEAD (PLAYER_FACE_INVULNERABLE + 1)

/**
 * The total number of player faces.
 */
#define PLAYER_FACES (PLAYER_FACE_DEAD + 1)

/**
 * This structure keeps the static and runtime data for the status bar.
 */
struct StatusBarData {
	
	/**
	 * The status bar background.
	 */
	void *background;
	
	/**
	 * The background patch for the weapons display
	 */
	void *weaponsBackground;
	
	/**
	 * "weapon present" indicator digits 2..7, re-used from the widget system
	 */
	void *weaponPresentIndicatorDigits[6];

	/**
	 * "weapon absent" indicator digits 2..7
	 */
	void *weaponAbsentIndicatorDigits[6];
	
	/**
	 * The keycard / skullkey icons
	 */
	void *keyIcons[6];
	
	/**
	 * The player faces.
	 */
	void *faces[PLAYER_FACES];
	
};

/**
 * The data for the status bar.
 */
static struct StatusBarData data;

/**
 * See header file for information.
 */
void initializeStatusBar() {
	int i, j, faceIndex;
	static char keyIconNameBuffer[8] = {'S', 'T', 'K', 'E', 'Y', 'S', 0, 0};
	static char weaponAbsentNameBuffer[8] = {'S', 'T', 'G', 'N', 'U', 'M', 0, 0};
	static char straightFaceNameBuffer[8] = {'S', 'T', 'F', 'S', 'T', 0, 0, 0};
	static char turnedFaceNameBuffer[8] = {'S', 'T', 'F', 'T', 0, 0, '0', 0};
	static char painFaceNameBuffer[8] = {'S', 'T', 'F', 'O', 'U', 'C', 'H', 0};
	static char evilGrinFaceNameBuffer[8] = {'S', 'T', 'F', 'E', 'V', 'L', 0, 0};
	static char rampageFaceNameBuffer[8] = {'S', 'T', 'F', 'K', 'I', 'L', 'L', 0};
	
	data.background = getWadFileLumpContentsByName("STBAR");
	data.weaponsBackground = getWadFileLumpContentsByName("STARMS");
	for (i=0; i<6; i++) {
		keyIconNameBuffer[6] = '0' + i;
		data.keyIcons[i] = getWadFileLumpContentsByName(keyIconNameBuffer);
	}
	for (i=0; i<6; i++) {
		weaponAbsentNameBuffer[6] = '0' + i;
		data.weaponAbsentIndicatorDigits[i] = getWadFileLumpContentsByName(weaponAbsentNameBuffer);
		data.weaponPresentIndicatorDigits[i] = staticWidgetData.smallDigits[i + 2];
	}
	
	faceIndex = 0;
	for (i=0; i<PLAYER_INJURY_LEVEL_COUNT; i++) {
		int base = i * PLAYER_FACES_PER_INJURY_LEVEL;
		char digit = '0' + i;
		
		/** load straight faces **/
		straightFaceNameBuffer[5] = digit;
		for (j=0; j<PLAYER_STRAIGHT_FACE_COUNT; j++) {
			straightFaceNameBuffer[6] = '0' + j;
			data.faces[base + PLAYER_STRAIGHT_FACE_OFFSET + j] = getWadFileLumpContentsByName(straightFaceNameBuffer);
		}
		
		/** load turned faces **/
		turnedFaceNameBuffer[5] = digit;
		turnedFaceNameBuffer[4] = 'R';
		data.faces[base + PLAYER_FACE_TURNED_RIGHT_OFFSET] = getWadFileLumpContentsByName(turnedFaceNameBuffer);
		turnedFaceNameBuffer[4] = 'L';
		data.faces[base + PLAYER_FACE_TURNED_LEFT_OFFSET] = getWadFileLumpContentsByName(turnedFaceNameBuffer);
		
		/** load other faces **/
		painFaceNameBuffer[7] = digit;
		data.faces[base + PLAYER_FACE_PAIN_OFFSET] = getWadFileLumpContentsByName(painFaceNameBuffer);
		evilGrinFaceNameBuffer[6] = digit;
		data.faces[base + PLAYER_FACE_EVIL_GRIN_OFFSET] = getWadFileLumpContentsByName(evilGrinFaceNameBuffer);
		rampageFaceNameBuffer[7] = digit;
		data.faces[base + PLAYER_FACE_RAMPAGE_OFFSET] = getWadFileLumpContentsByName(rampageFaceNameBuffer);

	}
	
	/** load faces that are independent of injury levels **/
	data.faces[PLAYER_FACE_INVULNERABLE] = getWadFileLumpContentsByName("STFGOD0");
	data.faces[PLAYER_FACE_DEAD] = getWadFileLumpContentsByName("STFDEAD0");

}

/**
 * See header file for information.
 */
void bindStatusBarState() {
}

/**
 * See header file for information.
 */
void shutdownStatusBar() {
	int i;
	zoneAllocatorDispose(data.background);
	data.background = NULL;
	zoneAllocatorDispose(data.weaponsBackground);
	data.weaponsBackground = NULL;
	for (i=0; i<6; i++) {
		zoneAllocatorDispose(data.keyIcons[i]);
		data.keyIcons[i] = NULL;
	}
	/** note: "weapon present" indicator digits were not allocated here, so don't dispose them! **/
	for (i=0; i<6; i++) {
		zoneAllocatorDispose(data.weaponAbsentIndicatorDigits[i]);
		data.weaponAbsentIndicatorDigits[i] = NULL;
	}
	for (i=0; i<PLAYER_FACES; i++) {
		zoneAllocatorDispose(data.faces[i]);
		data.faces[i] = NULL;
	}
}

/**
 * See header file for information.
 */
void drawStatusBar() {
	static int counter = 0;
	if (counter == PLAYER_FACES) {
		counter = 0;
	}
	
	drawPatchOnScreen(POSITION_STATUS_BAR_X, POSITION_STATUS_BAR_Y, data.background);
	drawPatchOnScreen(POSITION_WEAPONS_BACKGROUND_X, POSITION_WEAPONS_BACKGROUND_Y, data.weaponsBackground);

	drawPatchOnScreen(143, 168, data.faces[counter]);
	counter++;
}
