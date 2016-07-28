
#include "memory.h"

void baselib_memory_poke8(unsigned int address, char value) {
	*((char*)address) = value;
}

char baselib_memory_peek8(unsigned int address) {
	return *((char*)address);
}

void baselib_memory_poke16(unsigned int address, short value) {
	*((short*)address) = value;
}

short baselib_memory_peek16(unsigned int address) {
	return *((short*)address);
}

void baselib_memory_poke32(unsigned int address, int value) {
	*((int*)address) = value;
}

int baselib_memory_peek32(unsigned int address) {
	return *((int*)address);
}

void *memcpy(void *dest, const void *src, int n) {
	baselib_memory_copy8(dest, src, n);
	return dest;
}

void baselib_memory_copy8(char *dest, const char *src, int n) {
	int i;
	for (i=0; i<n; i++) {
		dest[i] = src[i];
	}
}

void baselib_memory_copy16(short *dest, const short *src, int n) {
	int i;
	for (i=0; i<n; i++) {
		dest[i] = src[i];
	}
}

void baselib_memory_copy32(int *dest, const int *src, int n) {
	int i;
	for (i=0; i<n; i++) {
		dest[i] = src[i];
	}
}

void *memset(void *dest, char v, int n) {
	baselib_memory_fill8(dest, v, n);
	return dest;
}

void baselib_memory_fill8(char *dest, char v, int n) {
	int i;
	for (i=0; i<n; i++) {
		dest[i] = v;
	}
}

void baselib_memory_fill16(short *dest, short v, int n) {
	int i;
	for (i=0; i<n; i++) {
		dest[i] = v;
	}
}

void baselib_memory_fill32(int *dest, int v, int n) {
	int i;
	for (i=0; i<n; i++) {
		dest[i] = v;
	}
}
