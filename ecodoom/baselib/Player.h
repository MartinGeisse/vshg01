/*
 *  Player.h
 *  baselib
 *
 *  Created by Martin on 8/27/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

/**
 * This data structure represents information about the player that is
 * not directly related to the player's role as a map object.
 */
struct Player {

	/**
	 * This section contains data that applies to the player itself.
	 */
	struct {
	} status;

	/**
	 * This section contains data that applies to the game as a whole.
	 */
	struct {

		/**
		 * The game skill, 0-based (0 = too young to die, 4 = nightmare)
		 */
		int skill;
		
		/**
		 * The number of the current level, 1-based.
		 */
		int level;
		
	} game;
	
	/**
	 * This section contains data that applies only to the current level. That is,
	 * on changing the current level, this structure is re-initialized.
	 */
	struct {
	} currentLevel;
	
	/**
	 * These scores are displayed to the player to show his/her success.
	 */
	struct {
		
		/**
		 * Level score: The number of enemies killed so far in the current level.
		 */
		int kills;

		/**
		 * Level score: The total number of enemies that can be killed in the current level.
		 */
		int maxKills;
		
		/**
		 * Level score: The number of bonus items collected so far in the current level.
		 */
		int items;

		/**
		 * Level score: The total number of bonus items that can be collected so far in the current level.
		 */
		int maxItems;
		
		/**
		 * Level score: The number of secret rooms found so far in the current level.
		 */
		int secrets;

		/**
		 * Level score: The total number of secret rooms that can be found so far in the current level.
		 */
		int maxSecrets;
		
		/**
		 * World score: This flag is set when the player completes the secret level.
		 */
		int secretLevel;
		
	} score;
	
	/**
	 * This data section applies only when the player exits a level and moves on.
	 */
	struct {
		
		/**
		 * The level number just left, 1-based.
		 */
		int previousLevel;
		
		/**
		 * The level to go to, 1-based.
		 */
		int nextLevel;
		
	} levelTransit;
	
};

/**
 * The player data.
 */
extern struct Player player;

/**
 * Initializes the player data for a new game. This resets world-scoped
 * variables in the player data and sets a level transit to level 1.
 */
void initializePlayerForNewGame();

/**
 * Initializes the player data for a new level. This resets level-scoped
 * variables in the player data. The level to move to is taken from the
 * level transit section.
 */
void initializePlayerForNewLevel();

/**
 * Initializes the player data for a level transit using the normal exit.
 * The current level structure is used to find the level being left.
 *
 * If the current level is the secret level, this function also records
 * that the player has finished it.
 */
void initializePlayerForLevelTransit();

/**
 * Initializes the player data for a level transit using the secret exit.
 * The current level structure is used to find the level being left.
 */
void initializePlayerForSecretLevelTransit();
