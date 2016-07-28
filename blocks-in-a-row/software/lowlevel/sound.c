
#include "util/memory.h"
#include "sound.h"

#define SOUND_BASE_ADDRESS		0xF0800000
#define SOUND_SHAPE_REGISTER	(SOUND_BASE_ADDRESS + 0)
#define SOUND_DELAY_REGISTER	(SOUND_BASE_ADDRESS + 4)
#define SOUND_SLOPE_REGISTER	(SOUND_BASE_ADDRESS + 8)

void initializeSound (void)
{
}

void setSoundShape (int shape)
{
	poke32 (SOUND_SHAPE_REGISTER, shape);
}

void setSoundDelay (int delay)
{
	poke32 (SOUND_DELAY_REGISTER, delay);
}

void setSoundSlope (int slope)
{
	poke32 (SOUND_SLOPE_REGISTER, slope);
}

void playRectangle (int freq)
{
	setSoundShape (0);
	setSoundDelay (0);
	setSoundSlope (freq * 1024 / 48000);
}

void playSawtooth (int freq)
{
	setSoundShape (1);
	setSoundDelay (0);
	setSoundSlope (freq * 1024 / 48000);
}

void playTriangle (int freq)
{
	setSoundShape (2);
	setSoundDelay (0);
	setSoundSlope (freq * 2048 / 48000);
}

void stopSound (void)
{
	setSoundShape (0);
	setSoundSlope (0);
}
