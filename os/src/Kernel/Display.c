
#include <Poke.h>
#include <Display.h>

#define DISPLAY_BASE_ADDRESS		0xF0100000

void initializeDisplay(void)
{
}

void writeCharacterOnDisplay(int x, int y, char c)
{
	writeMemory32(DISPLAY_BASE_ADDRESS + (y*64 + x)*4, c);
}
