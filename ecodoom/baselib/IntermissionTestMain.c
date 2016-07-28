/*
 *  IntermissionTestMain.c
 *  baselib
 *
 *  Created by Martin on 8/27/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

#include <allegro.h>
#include "Common.h"
#include "SystemGraphics.h"
#include "ZoneAllocator.h"
#include "WadFile.h"
#include "FixedPointNumber.h"
#include "StaticGraphicsData.h"
#include "Player.h"
#include "Widget.h"
#include "Intermission.h"
#include "SystemKeyboard.h"
#include "TestMainCommon.h"

volatile int tick = 0;
volatile int keyPressed = 0;

void timerCallback() {
	tick = 1;
}

void animateAndWaitForKey() {
	while (keyPressed != 2) {
		
		/** wait for next tick **/
		while (!tick);
		tick = 0;
		
		/** regular work **/
		sampleKeyboard();
		drawIntermission();
		if (runIntermissionTicker()) {
			keyPressed = 2;
		}
		
		/** check quit key **/
		if (key[KEY_ESC]) {
			exit(0);
		}
		
		/** check "go on" key **/
		if (key[KEY_A]) {
			if (keyPressed == 0) {
				keyPressed = 1;
			}
		} else {
			if (keyPressed == 1) {
				keyPressed = 2;
			}
		}
		
	}
	keyPressed = 0;
}

static void setFakeScores() {
	player.score.kills = 123;
	player.score.maxKills = 123;
	player.score.items = 15;
	player.score.maxItems = 20;
	player.score.secrets = 1;
	player.score.maxSecrets = 3;
}

/**
 * Main function.
 */
int main(int argc, const char *argv[]) {
	int i;
	
	testMainScreenWidth = 320;
	testMainScreenHeight = 200;
	initializeAllegroStuff();
	zoneAllocatorInitialize();
	initializeWadFile(WAD_FILE_PATH);
	initializeStaticGraphicsData();
	initializeStaticWidgetData();
	initializeIntermissionData();
	systemGraphicsSetPalette((unsigned char *)getWadFileLumpContentsByName("PLAYPAL"));
	clearScreen();

	install_timer();
	install_int(&timerCallback, 29);

	/** simulate finishing the levels one after another **/
	initializePlayerForNewGame();
	for (i=0; i<8; i++) {
		initializePlayerForNewLevel();
		setFakeScores();
		initializePlayerForLevelTransit();
		prepareIntermission();
		animateAndWaitForKey();
	}

	/** now the same including the secret level **/
	initializePlayerForNewGame();
	for (i=0; i<9; i++) {
		initializePlayerForNewLevel();
		setFakeScores();
		if (i == 2) {
			initializePlayerForSecretLevelTransit();
		} else {
			initializePlayerForLevelTransit();
		}
		prepareIntermission();
		animateAndWaitForKey();
	}
	
	return 0;
}
END_OF_MAIN()
