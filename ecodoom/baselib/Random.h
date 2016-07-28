/*
 *  Random.h
 *  baselib
 *
 *  Created by Martin on 8/15/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

/**
 * This function is used for the game logic. It returns deterministic
 * results as long as it is called in a deterministic way (and especially,
 * a deterministic number of times). This should be the case for
 * the game logic.
 */
int getDeterministicRandomNumber();

/**
 * This function is used in nondeterministic cases. The caller need not
 * ensure a deterministic use of this index, but in turn cannot
 * expect deterministic results.
 */
int getNonDeterministicRandomNumber();

/**
 * Resets the random number generators to their initial state.
 */
void resetRandomNumbers();
