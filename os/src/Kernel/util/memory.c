
#include "memory.h"

void poke8 (unsigned int address, char value)
{
	*((char*)address) = value;
}

char peek8 (unsigned int address)
{
	return *((char*)address);
}

void poke16 (unsigned int address, short value)
{
	*((short*)address) = value;
}

short peek16 (unsigned int address)
{
	return *((short*)address);
}

void poke32 (unsigned int address, int value)
{
	*((int*)address) = value;
}

int peek32 (unsigned int address)
{
	return *((int*)address);
}

void *memcpy (void *dest, const void *src, int n)
{
	copy8 (dest, src, n);
	return dest;
}

void copy8 (char *dest, const char *src, int n)
{
	int i;
	for (i=0; i<n; i++)
		dest [i] = src [i];
}

void copy16 (short *dest, const short *src, int n)
{
	int i;
	for (i=0; i<n; i++)
		dest [i] = src [i];
}

void copy32 (int *dest, const int *src, int n)
{
	int i;
	for (i=0; i<n; i++)
		dest [i] = src [i];
}

void *memset (void *dest, char v, int n)
{
	fill8 (dest, v, n);
	return dest;
}

void fill8 (char *dest, char v, int n)
{
	int i;
	for (i=0; i<n; i++)
		dest [i] = v;
}

void fill16 (short *dest, short v, int n)
{
	int i;
	for (i=0; i<n; i++)
		dest [i] = v;
}

void fill32 (int *dest, int v, int n)
{
	int i;
	for (i=0; i<n; i++)
		dest [i] = v;
}
