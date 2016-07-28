
#define KEYBOARD_BASE_ADDRESS		0xf0200000
#define KEYBOARD_CONTROL_REGISTER	(KEYBOARD_BASE_ADDRESS + 0)
#define KEYBOARD_DATA_REGISTER		(KEYBOARD_BASE_ADDRESS + 4)

#include "util/random.h"
#include "util/memory.h"

static volatile int leftPressed, rightPressed, downPressed, cwPressed, ccwPressed, anyPressed;
static volatile int breakCode;

void initializeKeyboard (void)
{
	poke32 (KEYBOARD_CONTROL_REGISTER, 2);
}

void handleKeyboardInterrupt (void)
{
	int key, newstate;

	key = peek32 (KEYBOARD_DATA_REGISTER);
	poke32 (KEYBOARD_CONTROL_REGISTER, 2);

	if (key == 0xf0) {
		breakCode = 1;
		return;
	}

	newstate = breakCode ? 0 : 1;
	breakCode = 0;

	switch (key) {
		case 107:
			leftPressed = newstate;
			break;

		case 116:
			rightPressed = newstate;
			break;

		case 114:
			downPressed = newstate;
			break;

		case 31:
			cwPressed = newstate;
			break;

		case 20:
			ccwPressed = newstate;
			break;

		// Enter is our "any" key
		case 90:
			anyPressed = newstate;
			break;
	}
}

int keyLeft (void)
{
	return leftPressed;
}

int keyRight (void)
{
	return rightPressed;
}

int keyDown (void)
{
	return downPressed;
}

int keyClockwise (void)
{
	return cwPressed;
}

int keyCounterClockwise (void)
{
	return ccwPressed;
}

void waitForAnyKey (void)
{
	while (!anyPressed)
		randomAutoSeederTick ();
	autoSeedRandom ();
}
