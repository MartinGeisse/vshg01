
#include <stdlib.h>
#include <stdio.h>
#include "HeapParameters.h"

/**
 * Configuration for the default amount of heap to use.
 */
#define DEFAULT_SIZE		(8 * 1024 * 1024)

/**
 * This flag indicates whether the heap parameters system is initialized.
 */
static int initialized = 0;

/**
 * This is the heap actually allocated, for cleanup
 */
static void *allocatedHeap;

/**
 * The start address of the usable heap range.
 */
static unsigned int startAddress = 0;

/**
 * The size of the usable heap range.
 */
static unsigned int size = DEFAULT_SIZE;

/**
 * This function ensures that the HeapParameters system is not yet initialized.
 */
static void mustNotBeInitialized(const char *function) {
	if (initialized) {
		printf("%s: HeapParameters already initialized.\n", function);
		exit(1);
	}
}

/**
 * This function ensures that the HeapParameters system is initialized.
 */
static void mustBeInitialized(const char *function) {
	if (!initialized) {
		printf("%s: HeapParameters not yet initialized.\n", function);
		exit(1);
	}
}

// see header file
void configureHeapParameters(unsigned int sizeToSet) {
	mustNotBeInitialized("configureHeapParameters");
	size = sizeToSet;
	size -= size % 4096;
}

// see header file
void initializeHeapParameters() {
	mustNotBeInitialized("initializeHeapParameters");
	allocatedHeap = malloc(size + 4096);
	startAddress = (unsigned int)allocatedHeap;
	startAddress += 4096;
	startAddress -= startAddress % 4096;
	initialized = 1;
}

// see header file
void finalizeHeapParameters() {
	mustBeInitialized("finalizeHeapParameters");
	free(allocatedHeap);
	allocatedHeap = NULL;
	initialized = 0;
	startAddress = 0;
	size = DEFAULT_SIZE;
}

// see header file
unsigned int getHeapStartAddress() {
	mustBeInitialized("getHeapStartAddress");
	return startAddress;
}

// see header file
unsigned int getHeapSize() {
	mustBeInitialized("getHeapSize");
	return size;
}
