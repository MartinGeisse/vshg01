
#include "display.h"
#include "util/memory.h"

#define DISPLAY_BASE_ADDRESS		0xF0100000

void initializeDisplay (void)
{
}

void displayPutChar (int x, int y, char c)
{
	poke32 (DISPLAY_BASE_ADDRESS + (y*64 + x)*4, c);
}
