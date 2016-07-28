/*
 *  Intermission.c
 *  baselib
 *
 *  Created by Martin on 8/27/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

#include "Common.h"
#include "ZoneAllocator.h"
#include "WadFile.h"
#include "Player.h"
#include "Widget.h"
#include "SystemKeyboard.h"
#include "SystemDebug.h"
#include "FixedPointNumber.h"
#include "LowlevelGraphics.h"
#include "Intermission.h"

/**
 * The overall states that the intermission screen can be in.
 */
enum IntermissionScreenState {
	
	/**
	 * Currently showing stats for the previous level
	 */
	INTERMISSION_SCREEN_STATE_SHOW_STATS,
	
	/**
	 * Currently showing the position of the next level on the intermission map
	 */
	INTERMISSION_SCREEN_STATE_SHOW_NEXT_LOCATION,
	
	/**
	 * Currently counting a short delay before actually entering the next level
	 */
	INTERMISSION_SCREEN_STATE_FINAL_DELAY,
	
};

/**
 * The sub-state used while counting stats. The numbering of these states using a special
 * trick: even state numbers are "action" states, odd numbers are "delay" states. When
 * a delay state is entered, the system pauses for one second by counting frames, then
 * increases the state number by one.
 */
enum StatsCountingState {
	
	/**
	 * No-op initialization state. This state is immediately left.
	 */
	STATS_COUNTING_STATE_INITIALIZE = 0,
	
	/**
	 * First state assumed when entering the intermission screen.
	 */
	STATS_COUNTING_STATE_INITIALIZE_DELAY = 1,
	
	/**
	 * Kill counting state.
	 */
	STATS_COUNTING_STATE_COUNT_KILLS = 2,

	/**
	 * Delay state after the kill counting state.
	 */
	STATS_COUNTING_STATE_COUNT_KILLS_DELAY = 3,
	
	/**
	 * Item counting state.
	 */
	STATS_COUNTING_STATE_COUNT_ITEMS = 4,
	
	/**
	 * Delay state after the item counting state.
	 */
	STATS_COUNTING_STATE_COUNT_ITEMS_DELAY = 5,
	
	/**
	 * Secret counting state.
	 */
	STATS_COUNTING_STATE_COUNT_SECRETS = 6,
	
	/**
	 * Delay state after the secret counting state.
	 */
	STATS_COUNTING_STATE_COUNT_SECRETS_DELAY = 7,

	/**
	 * Finished stats counting and waiting for the user to press any of
	 * the acceleration keys to switch to the next major state.
	 */
	STATS_COUNTING_STATE_FINISHED = 8
	
};

/**
 * Simple point-on-screen data structure.
 */
struct PointOnScreen {
	
	/** the x position of the point **/
	int x;
	
	/** the y position of the point **/
	int y;
	
};

/**
 * This table contains the locations of the levels on the intermission map.
 */
static struct PointOnScreen levelLocations[9] = {
	{185, 164},
	{148, 143},
	{69, 122},
	{209, 102},
	{116, 89},
	{166, 55},
	{71, 56},
	{135, 29},
	{71, 24}
};

/**
 * Definition of a single animated part of the intermission map.
 */
struct Animation {
	
	/** the x position of the animation **/
	int x;
	
	/** the y position of the animation **/
	int y;
	
	/** the number of animation frames **/
	int frameCount;
	
	/** the animation frames (3 is the maximum that occurs in practice) **/
	void *framePatches[3];
	
	/** the number of screen frames per animation frame **/
	int delay;
	
	/** the current frame number **/
	int currentFrame;
	
	/** the number of screen frames remaining for the current frame **/
	int remainingDelay;
	
};

/**
 * This table contains information about animated parts of the intermission map.
 * Note that animation counters are simply initialized in this table, then animated.
 * There is no need to re-initialize them later.
 */
static struct Animation animations[] = {
	{224, 104, 3, {NULL, NULL, NULL}, 11, 0, 1},
	{184, 160, 3, {NULL, NULL, NULL}, 11, 0, 1},
	{112, 136, 3, {NULL, NULL, NULL}, 11, 0, 1},
	{ 72, 112, 3, {NULL, NULL, NULL}, 11, 0, 1},
	{ 88,  96, 3, {NULL, NULL, NULL}, 11, 0, 1},
	{ 64,  48, 3, {NULL, NULL, NULL}, 11, 0, 1},
	{192,  40, 3, {NULL, NULL, NULL}, 11, 0, 1},
	{136,  16, 3, {NULL, NULL, NULL}, 11, 0, 1},
	{ 80,  16, 3, {NULL, NULL, NULL}, 11, 0, 1},
	{ 64,  24, 3, {NULL, NULL, NULL}, 11, 0, 1},
};

/**
 * This macro determines the number of animations.
 */
#define ANIMATION_COUNT (sizeof(animations) / sizeof(struct Animation))

/**
 * This defines the data structures for data used in
 * the intermission screen.
 */
struct IntermissionData {
	
	/**
	 * The patch for the intermission screen background.
	 */
	void *backgroundPatch;
	
	/**
	 * The patches for level name texts.
	 */
	void *levelNamePatches[9];
	
	/**
	 * The "finished!" text patch.
	 */
	void *finishedPatch;
	
	/**
	 * The "entering" text patch.
	 */
	void *enteringPatch;
	
	/**
	 * The "splat" graphics patch that is used to indicate a completed level.
	 */
	void *splatPatch;
	
	/**
	 * The "you are here" graphics patches used to indicate the next level.
	 */
	void *youAreHerePatches[2];
	
	/**
	 * The "kills" text patch
	 */
	void *killsPatch;

	/**
	 * The "items" text patch
	 */
	void *itemsPatch;

	/**
	 * The "secrets" text patch
	 */
	void *secretsPatch;
	
	/**
	 * The "kills" widget
	 */
	struct NumberWidget killsWidget;

	/**
	 * The "items" widget
	 */
	struct NumberWidget itemsWidget;

	/**
	 * The "secrets" widget
	 */
	struct NumberWidget secretsWidget;
	
	/**
	 * The displayed kills percentage
	 */
	int killsPercentage;

	/**
	 * The target kills percentage to count to
	 */
	int targetKillsPercentage;
	
	/**
	 * The displayed items percentage
	 */
	int itemsPercentage;

	/**
	 * The target items percentage to count to
	 */
	int targetItemsPercentage;
	
	/**
	 * The displayed secrets percentage
	 */
	int secretsPercentage;

	/**
	 * The target secrets percentage to count to
	 */
	int targetSecretsPercentage;
	
	/**
	 * The overall state of the intermission screen.
	 */
	enum IntermissionScreenState state;
	
	/**
	 * The state used while counting level stats
	 */
	enum StatsCountingState statsCountingState;
	
	/**
	 * This counter is used to start a sound in regular intervals while stats are ticking
	 */
	int statsTickingSoundCounter;
	
	/**
	 * Generic delay counter. This is used in different ways by the different states.
	 */
	int delayCounter;
	
};

/**
 * The data for the intermission screen.
 */
static struct IntermissionData data;

/**
 * See header file for information.
 */
void initializeIntermissionData() {
	int i, j;
	char levelNamePatchNameBuffer[8] = {'W', 'I', 'L', 'V', '0', 0, 0, 0};
	char animationPatchNameBuffer[8] = {'W', 'I', 'A', '0', 0, 0, 0, 0};
	
	data.backgroundPatch = getWadFileLumpContentsByName("WIMAP0");
	for (i=0; i<9; i++) {
		levelNamePatchNameBuffer[5] = '0' + i;
		data.levelNamePatches[i] = getWadFileLumpContentsByName(levelNamePatchNameBuffer);
	}
	data.finishedPatch = getWadFileLumpContentsByName("WIF");
	data.enteringPatch = getWadFileLumpContentsByName("WIENTER");
	data.splatPatch = getWadFileLumpContentsByName("WISPLAT");
	data.youAreHerePatches[0] = getWadFileLumpContentsByName("WIURH0");
	data.youAreHerePatches[1] = getWadFileLumpContentsByName("WIURH1");
	data.killsPatch = getWadFileLumpContentsByName("WIOSTK");
	data.itemsPatch = getWadFileLumpContentsByName("WIOSTI");
	data.secretsPatch = getWadFileLumpContentsByName("WISCRT2");
	for (i=0; i<ANIMATION_COUNT; i++) {
		struct Animation *animation = (animations + i);
		animationPatchNameBuffer[4] = '0' + i / 10;
		animationPatchNameBuffer[5] = '0' + i % 10;
		
		for (j=0; j<animation->frameCount; j++) {
			animationPatchNameBuffer[6] = '0' + j / 10;
			animationPatchNameBuffer[7] = '0' + j % 10;
			animation->framePatches[j] = getWadFileLumpContentsByName(animationPatchNameBuffer);
		}
	}
}

/**
 * See header file for information.
 */
void disposeIntermissionData() {
	int i, j;
	zoneAllocatorDispose(data.backgroundPatch);
	data.backgroundPatch = NULL;
	for (i=0; i<9; i++) {
		zoneAllocatorDispose(data.levelNamePatches[i]);
		data.levelNamePatches[i] = NULL;
	}
	zoneAllocatorDispose(data.finishedPatch);
	data.finishedPatch = NULL;
	zoneAllocatorDispose(data.enteringPatch);
	data.enteringPatch = NULL;
	zoneAllocatorDispose(data.splatPatch);
	data.splatPatch = NULL;
	zoneAllocatorDispose(data.youAreHerePatches[0]);
	data.youAreHerePatches[0] = NULL;
	zoneAllocatorDispose(data.youAreHerePatches[1]);
	data.youAreHerePatches[1] = NULL;
	zoneAllocatorDispose(data.killsPatch);
	data.killsPatch = NULL;
	zoneAllocatorDispose(data.itemsPatch);
	data.itemsPatch = NULL;
	zoneAllocatorDispose(data.secretsPatch);
	data.secretsPatch = NULL;
	for (i=0; i<ANIMATION_COUNT; i++) {
		struct Animation *animation = (animations + i);
		for (j=0; j<animation->frameCount; j++) {
			zoneAllocatorDispose(animation->framePatches[j]);
			animation->framePatches[j] = NULL;
		}
	}
}

/**
 * Returns the Y position on screen of the specified stats line (0..2).
 */
static int getStatsLineY(int lineNumber) {
	int patchHeight = ((unsigned short *)(staticWidgetData.intermissionDigits[0]))[1];
	return 50 + (3 * patchHeight / 2) * lineNumber;
}

/**
 * Computes a percentage to express (actual of maximum). This implementation is
 * safe in the sense that if the maximum is less than 1 (and the percentage is
 * thus undefined), 0 is returned.
 */
static int computePercentageSafe(int actual, int maximum) {
	return (maximum < 1) ? 0 : (actual * 100 / maximum);
}

/**
 * See header file for information.
 */
void prepareIntermission() {
	initializeNumberWidget(&data.killsWidget, NUMBER_WIDGET_STYLE_INTERMISSION_PERCENT, 270, getStatsLineY(0), 3, &(data.killsPercentage));
	initializeNumberWidget(&data.itemsWidget, NUMBER_WIDGET_STYLE_INTERMISSION_PERCENT, 270, getStatsLineY(1), 3, &(data.itemsPercentage));
	initializeNumberWidget(&data.secretsWidget, NUMBER_WIDGET_STYLE_INTERMISSION_PERCENT, 270, getStatsLineY(2), 3, &(data.secretsPercentage));
	data.killsPercentage = 0x80000000;
	data.targetKillsPercentage = computePercentageSafe(player.score.kills, player.score.maxKills);
	data.itemsPercentage = 0x80000000;
	data.targetItemsPercentage = computePercentageSafe(player.score.items, player.score.maxItems);
	data.secretsPercentage = 0x80000000;
	data.targetSecretsPercentage = computePercentageSafe(player.score.secrets, player.score.maxSecrets);
	data.state = INTERMISSION_SCREEN_STATE_SHOW_STATS;
	data.statsCountingState = STATS_COUNTING_STATE_INITIALIZE;
	data.delayCounter = 35;
	data.statsTickingSoundCounter = 0;
	// TODO: S_ChangeMusic(mus_inter, true); 
}

/**
 * Draws a horizontally centered patch on screen.
 */
static void drawPatchCentered(int y, unsigned char *patch) {
	int width = *(unsigned short *)patch;
	drawPatchOnScreen((320 - width) / 2, y, patch);
}

/**
 * Draws the "finished <previous level>!" text.
 */
static void drawLevelFinishedText() {
	void *levelNamePatch = data.levelNamePatches[player.levelTransit.previousLevel - 1];
	int firstPatchY = 2;
	int firstPatchHeight = ((unsigned short *)levelNamePatch)[1];
	int secondPatchY = firstPatchY + 5 * firstPatchHeight / 4;
	drawPatchCentered(firstPatchY, levelNamePatch);
	drawPatchCentered(secondPatchY, data.finishedPatch);
}

/**
 * Draws the "entering <next level>!" text.
 */
static void drawEnteringLevelText() {
	void *levelNamePatch = data.levelNamePatches[player.levelTransit.nextLevel - 1];
	int firstPatchY = 2;
	int firstPatchHeight = ((unsigned short *)data.enteringPatch)[1];
	int secondPatchY = firstPatchY + 5 * firstPatchHeight / 4;
	drawPatchCentered(firstPatchY, data.enteringPatch);
	drawPatchCentered(secondPatchY, levelNamePatch);
}

/**
 * Draws a patch on the location of the specified (1-based) level
 * on the intermission map. This function will try different patches
 * if the original patch overlaps the screen border.
 */
static void drawPatchOnLevelLocation(int levelNumber, void **patches, int patchCount) {
	int i;
	int levelLocationX = levelLocations[levelNumber - 1].x;
	int levelLocationY = levelLocations[levelNumber - 1].y;
	
	/** try all patches **/
	for (i=0; i<patchCount; i++) {
		
		/** extract patch properties **/
		void *patch = patches[i];
		int patchWidth = ((unsigned short *)patch)[0];
		int patchHeight = ((unsigned short *)patch)[1];
		int patchLeftOffset = ((unsigned short *)patch)[2];
		int patchTopOffset = ((unsigned short *)patch)[3];
		
		/** determine the patch borders **/
		int leftPatchBorder = levelLocationX - patchLeftOffset;
		int rightPatchBorder = leftPatchBorder + patchWidth;
		int topPatchBorder = levelLocationY - patchTopOffset;
		int bottomPatchBorder = topPatchBorder + patchHeight;
		
		/** make sure the patch fits, or try the next one **/
		if (leftPatchBorder < 0 || topPatchBorder < 0 || rightPatchBorder >= 320 || bottomPatchBorder >= 200) {
			continue;
		}
		
		/** draw the patch **/
		drawPatchOnScreen(levelLocationX, levelLocationY, patch);
		return;
		
	}
	
	/** none of the patches fits **/
	systemFatalError("Intermission: drawPatchOnLevelLocation(): none of the patches fits");
	
}

/**
 * Draws the animations.
 */
void drawAnimations() {
	int i;
	for (i=0; i<ANIMATION_COUNT; i++) {
		struct Animation *animation = (animations + i);
		drawPatchOnScreen(animation->x, animation->y, animation->framePatches[animation->currentFrame]);
	}
}

/**
 * See header file for information.
 */
void drawIntermission() {
	int finishedNormalLevelsUntil;
	int i;
	
	/** draw the background and animations **/
	drawPatchOnScreen(0, 0, data.backgroundPatch);
	drawAnimations();
	
	/** further drawing depends on the state **/
	if (data.state == INTERMISSION_SCREEN_STATE_SHOW_STATS) {
		
		/** show "finished" text **/
		drawLevelFinishedText();
		
		/** show previous level stats **/
		drawPatchOnScreen(50, getStatsLineY(0), data.killsPatch);
		drawNumberWidget(&data.killsWidget);
		drawPatchOnScreen(50, getStatsLineY(1), data.itemsPatch);
		drawNumberWidget(&data.itemsWidget);
		drawPatchOnScreen(50, getStatsLineY(2), data.secretsPatch);
		drawNumberWidget(&data.secretsWidget);
		
	} else {

		/** show "entering" text **/
		drawEnteringLevelText();
		
		/** draw a "splat" on all finished levels. This requires some special handling of the secret level **/
		finishedNormalLevelsUntil = (player.levelTransit.previousLevel == 9) ? (player.levelTransit.nextLevel - 1) : player.levelTransit.previousLevel;
		for (i = 1; i <= finishedNormalLevelsUntil; i++) {
			drawPatchOnLevelLocation(i, &(data.splatPatch), 1);
		}
		if (player.score.secretLevel) {
			drawPatchOnLevelLocation(9, &(data.splatPatch), 1);
		}
		
		/** show next level location **/
		drawPatchOnLevelLocation(player.levelTransit.nextLevel, data.youAreHerePatches, 2);
		
	}
}

/**
 * Advances animation frames.
 */
static void updateAnimations() {
	int i;
	for (i=0; i<ANIMATION_COUNT; i++) {
		struct Animation *animation = (animations + i);
		animation->remainingDelay--;
		if (animation->remainingDelay == 0) {
			animation->remainingDelay = animation->delay;
			animation->currentFrame++;
			if (animation->currentFrame == animation->frameCount) {
				animation->currentFrame = 0;
			}
		}
	}
}

/**
 * Returns 1 if any of the acceleration keys was just pressed, 0 if not.
 */
static int getAnyAccelerationKeyJustPressed() {
	return getKeyJustPressed(DOOM_KEY_CODE_FIRE) || getKeyJustPressed(DOOM_KEY_CODE_USE);
}

/**
 * This method is used when the displayed percentage of one of the stats
 * should actually change.
 */
static void performStatsCountingTick(int *percentagePointer, int *targetPercentagePointer) {

	/** start a pistol firing sound in regular intervals **/
	if (data.statsTickingSoundCounter == 0) {
		// TODO: S_StartSound(0, sfx_pistol);
	}
	
	/** increase value and see if target value is reached **/
	*percentagePointer += 2;
	if (*percentagePointer >= *targetPercentagePointer) {
		
		/** handle overshoot **/
		*percentagePointer = *targetPercentagePointer;
		
		/** on to the next state **/
		data.statsCountingState++;
		
		/** play an explosion sound **/
		// S_StartSound(0, sfx_barexp);
		
	}
	
}

/**
 * Ticker implementation for stats counting.
 */
static void runStatsTicker() {
	
	/** count frames to start a ticking sound in regular intervals **/
	data.statsTickingSoundCounter++;  
	if (data.statsTickingSoundCounter == 8) {
		data.statsTickingSoundCounter = 0;
	}

	/** react to the acceleration key **/
	if (getAnyAccelerationKeyJustPressed()) {
		if (data.statsCountingState == STATS_COUNTING_STATE_FINISHED) {
			data.state = INTERMISSION_SCREEN_STATE_SHOW_NEXT_LOCATION;
			// TODO: S_StartSound(0, sfx_sgcock);
		} else {
			data.killsPercentage = data.targetKillsPercentage;
			data.itemsPercentage = data.targetItemsPercentage;
			data.secretsPercentage = data.secretsPercentage;
			data.statsCountingState = STATS_COUNTING_STATE_FINISHED;
			// TODO: S_StartSound(0, sfx_barexp);
		}
		return;
	}
	
	/** handle delay states **/
	if (data.statsCountingState & 1) {
		data.delayCounter--;
		if (data.delayCounter == 0) {
			data.delayCounter = 35;
			data.statsCountingState++;
		}
		return;
	}
	
	/** handle ticking states **/
	switch (data.statsCountingState) {
			
		case STATS_COUNTING_STATE_INITIALIZE:
			data.statsCountingState = STATS_COUNTING_STATE_INITIALIZE_DELAY;
			break;

		case STATS_COUNTING_STATE_COUNT_KILLS:
			if (data.killsPercentage < 0) {
				data.killsPercentage = 0;
			}
			performStatsCountingTick(&data.killsPercentage, &data.targetKillsPercentage);
			break;

		case STATS_COUNTING_STATE_COUNT_ITEMS:
			if (data.itemsPercentage < 0) {
				data.itemsPercentage = 0;
			}
			performStatsCountingTick(&data.itemsPercentage, &data.targetItemsPercentage);
			break;

		case STATS_COUNTING_STATE_COUNT_SECRETS:
			if (data.secretsPercentage < 0) {
				data.secretsPercentage = 0;
			}
			performStatsCountingTick(&data.secretsPercentage, &data.targetSecretsPercentage);
			break;

		case STATS_COUNTING_STATE_FINISHED:
			break;

		default:
			systemFatalError("unknown stats counting state: %d", data.statsCountingState);

	}
	
}

/**
 * See header file for information.
 */
int runIntermissionTicker() {
	updateAnimations();
	
	switch (data.state) {
	
		case INTERMISSION_SCREEN_STATE_SHOW_STATS:
			runStatsTicker();
			break;
			
		case INTERMISSION_SCREEN_STATE_SHOW_NEXT_LOCATION:
			if (getAnyAccelerationKeyJustPressed()) {
				data.state = INTERMISSION_SCREEN_STATE_FINAL_DELAY;
				data.delayCounter = 10;
			}
			break;
			
		case INTERMISSION_SCREEN_STATE_FINAL_DELAY:
			data.delayCounter--;
			if (data.delayCounter == 0) {
				// TODO: G_WorldDone();
				return 1;
			}
			break;
			
		default:
			systemFatalError("unknown intermission screen state: %d", data.state);
			
	}
	return 0;
}
