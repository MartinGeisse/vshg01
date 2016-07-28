/*
 *  soundchain.h
 *  Blocks-in-a-Row
 *
 *  Created by Martin Geisse on 30.11.07.
 *  Copyright 2007 __MyCompanyName__. All rights reserved.
 *
 */

typedef struct {
	int style;
	int freq;
	int duration;
} SOUND_CHAIN_ELEMENT;

void startSoundChain (int channel, int count, SOUND_CHAIN_ELEMENT *chain);
void advanceSoundChains (void);
