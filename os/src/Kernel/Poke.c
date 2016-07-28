
#include <Poke.h>

// see header file
void writeMemory8 (unsigned int address, char value)
{
	*((char*)address) = value;
}

// see header file
char readMemory8 (unsigned int address)
{
	return *((char*)address);
}

// see header file
void writeMemory16 (unsigned int address, short value)
{
	*((short*)address) = value;
}

// see header file
short readMemory16 (unsigned int address)
{
	return *((short*)address);
}

// see header file
void writeMemory32 (unsigned int address, int value)
{
	*((int*)address) = value;
}

// see header file
int readMemory32 (unsigned int address)
{
	return *((int*)address);
}

// see header file
void copyMemory8 (char *dest, const char *src, int n)
{
	int i;
	for (i=0; i<n; i++)
		dest [i] = src [i];
}

// see header file
void copyMemory16 (short *dest, const short *src, int n)
{
	int i;
	for (i=0; i<n; i++)
		dest [i] = src [i];
}

// see header file
void copyMemory32 (int *dest, const int *src, int n)
{
	int i;
	for (i=0; i<n; i++)
		dest [i] = src [i];
}

// see header file
void fillMemory8 (char *dest, char v, int n)
{
	int i;
	for (i=0; i<n; i++)
		dest [i] = v;
}

// see header file
void fillMemory16 (short *dest, short v, int n)
{
	int i;
	for (i=0; i<n; i++)
		dest [i] = v;
}

// see header file
void fillMemory32 (int *dest, int v, int n)
{
	int i;
	for (i=0; i<n; i++)
		dest [i] = v;
}
