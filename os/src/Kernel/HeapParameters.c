
#include <HeapParameters.h>
#include <Kernel.h>

/**
 * Configuration for the default amount of heap to use.
 */
#define HEAP_START_ADDRESS		(0xC0000000 + 2*1024*1024)

/**
 * Configuration for the default amount of heap to use.
 */
#define HEAP_SIZE				(30 * 1024 * 1024)

/**
 * This flag indicates whether the heap parameters system is initialized.
 */
static int initialized = 0;

/**
 * This function ensures that the HeapParameters system is not yet initialized.
 */
static void mustNotBeInitialized(const char *function) {
	if (initialized) {
		panic(function);
	}
}

/**
 * This function ensures that the HeapParameters system is initialized.
 */
static void mustBeInitialized(const char *function) {
	if (!initialized) {
		panic(function);
	}
}

// see header file
void configureHeapParameters(unsigned int sizeToSet) {
	panic("cannot configure simulated or real heap");
}

// see header file
void initializeHeapParameters(void) {
	mustNotBeInitialized("initializeHeapParameters");
	initialized = 1;
}

// see header file
void finalizeHeapParameters(void) {
	mustBeInitialized("finalizeHeapParameters");
	initialized = 0;
}

// see header file
unsigned int getHeapStartAddress(void) {
	mustBeInitialized("getHeapStartAddress");
	return HEAP_START_ADDRESS;
}

// see header file
unsigned int getHeapSize(void) {
	mustBeInitialized("getHeapSize");
	return HEAP_SIZE;
}
