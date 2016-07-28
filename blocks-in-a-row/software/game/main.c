/*
 *  Blocks-in-a-Row
 *
 *	Created by Martin Geisse on 29.11.07.
 *	Copyright (c) 2007 __MyCompanyName__. All rights reserved.
 */

#include "lowlevel/display.h"
#include "lowlevel/sound.h"
#include "lowlevel/timer.h"
#include "lowlevel/keyboard.h"
#include "util/random.h"
#include "util/cpu.h"
#include "soundchain.h"
#include "draw.h"
#include "gamestate.h"
#include "engine.h"

void handleInterrupt (void)
{
	if (getInterruptPriority () == 4)
		handleKeyboardInterrupt ();
}

void main(void)
{
	if (!isROMCode ())
		useRAMInterruptHandler ();
	enableInterruptChannel (4);
	enableInterrupts ();

	initializeDisplay ();
	initializeKeyboard ();
	initializeSound ();
	initializeTimer ();
	// TODO: 30 for XESS board -- setTimerDivisor (30);
	setTimerDivisor (30000);
	engineMainLoop ();
}
