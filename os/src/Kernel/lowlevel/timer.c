
#include "util/memory.h"
#include "timer.h"

#define TIMER_BASE_ADDRESS		0xF0000000
#define TIMER_CONTROL_REGISTER	(TIMER_BASE_ADDRESS + 0)
#define TIMER_DIVISOR_REGISTER	(TIMER_BASE_ADDRESS + 4)

void initializeTimer (void)
{
	poke32 (TIMER_CONTROL_REGISTER, 2);
}

void setTimerDivisor (int divisor)
{
	poke32 (TIMER_DIVISOR_REGISTER, divisor);
}

void waitForTimer (void)
{
	while ((peek32 (TIMER_CONTROL_REGISTER) & 1) == 0);
}

void resetTimer (void)
{
	poke32 (TIMER_CONTROL_REGISTER, 2);
}
