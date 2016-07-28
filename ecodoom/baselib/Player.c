/*
 *  Player.c
 *  baselib
 *
 *  Created by Martin on 8/27/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

#include "Player.h"

/**
 * See header file for information.
 */
struct Player player;

/**
 * See header file for information.
 */
void initializePlayerForNewGame() {
	player.game.skill = 2;
	player.score.secretLevel = 0;
	player.levelTransit.nextLevel = 1;
}

/**
 * See header file for information.
 */
void initializePlayerForNewLevel() {
	player.game.level = player.levelTransit.nextLevel;
	player.score.kills = 0;
	player.score.maxKills = 0;
	player.score.items = 0;
	player.score.maxItems = 0;
	player.score.secrets = 0;
	player.score.maxSecrets = 0;
}

/**
 * See header file for information.
 */
void initializePlayerForLevelTransit() {
	int current = player.game.level;
	player.levelTransit.previousLevel = current;
	if (current == 9) {
		player.levelTransit.nextLevel = 4;
		player.score.secretLevel = 1;
	} else {
		player.levelTransit.nextLevel = current + 1;
	}
}

/**
 * See header file for information.
 */
void initializePlayerForSecretLevelTransit() {
	int current = player.game.level;
	player.levelTransit.previousLevel = current;
	player.levelTransit.nextLevel = 9;
}
