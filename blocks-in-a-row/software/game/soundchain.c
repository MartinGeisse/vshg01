/*
 *  soundchain.c
 *  Blocks-in-a-Row
 *
 *  Created by Martin Geisse on 30.11.07.
 *  Copyright 2007 __MyCompanyName__. All rights reserved.
 *
 */

#include "util/kernel.h"
#include "lowlevel/sound.h"
#include "soundchain.h"

static SOUND_CHAIN_ELEMENT *chains [4];
static int chainLength [4];
static int chainIndex [4];
static int chainDuration [4];

static void setupSound (int channel)
{
	SOUND_CHAIN_ELEMENT *element = chains [channel] + chainIndex [channel];
	chainDuration [channel] = element->duration;

	switch (element->style) {
		case 0:
			playRectangle (element->freq);
			break;

		case 1:
			playSawtooth (element->freq);
			break;

		case 2:
			playTriangle (element->freq);
			break;
	}
}

void startSoundChain (int channel, int count, SOUND_CHAIN_ELEMENT *chain)
{
	chains [channel] = chain;
	chainLength [channel] = count;
	chainIndex [channel] = 0;
	setupSound (channel);
}

void advanceSoundChains (void)
{
	int channel;

	for (channel = 0; channel < 4; channel++)
	{
		if (chains [channel] == NULL) continue;

		chainDuration [channel]--;
		if (chainDuration [channel] > 0) continue;

		chainIndex [channel]++;
		if (chainIndex [channel] == chainLength [channel]) {
			stopSound ();
			chains [channel] = NULL;
		} else {
			setupSound (channel);
		}
	}
}


