
#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>

#include <CrossTestUtils.h>
#include <HeapParameters.h>

// *****************************************************************************************************************************************
// test functions
// *****************************************************************************************************************************************

void testStartAddressAligned() {
	unsigned int startAddress = getHeapStartAddress();
	assertTrue(startAddress % 4096 == 0, "getHeapStartAddress() not page-aligned: %d", startAddress);
}

void testNonzeroSize() {
	unsigned int size = getHeapSize();
	assertTrue(size > 0, "getHeapSize() returns zero");
}

void testDefaultSizeAligned() {
	unsigned int size = getHeapSize();
	assertTrue(size % 4096 == 0, "getHeapSize() not page-aligned: %d", size);
}

void testConfiguredMisalignedSizeBecomesAligned() {
	unsigned int size = getHeapSize();
	assertTrue(size % 4096 == 0, "getHeapSize() not page-aligned: %d (was configured mis-aligned)", size);
}

void testNoWrapAround() {
	unsigned int startAddress = getHeapStartAddress();
	unsigned int size = getHeapSize();
	unsigned int endAddress = (startAddress + size) % 0xFFFFFFFF;
	assertTrue(endAddress > startAddress, "heap wraps around the address space (start: %d, size: %d)", startAddress, size);
}

void testSetResetSize() {
	unsigned int defaultSize = getHeapSize();
	unsigned int otherSize = 4096 * 79;
	finalizeHeapParameters();
	configureHeapParameters(otherSize);
	initializeHeapParameters();
	assertTrue(getHeapSize() == otherSize, "could not set heap size");
	finalizeHeapParameters();
	initializeHeapParameters();
	assertTrue(getHeapSize() == defaultSize, "could not reset heap size");
}

// *****************************************************************************************************************************************
// test helper functions
// *****************************************************************************************************************************************

void test(void (*proc)()) {
	initializeHeapParameters();
	(*proc)();
	finalizeHeapParameters();
}

void testWithSize(int size, void (*proc)()) {
	configureHeapParameters(size);
	initializeHeapParameters();
	(*proc)();
	finalizeHeapParameters();
}

// *****************************************************************************************************************************************
// test suite main function
// *****************************************************************************************************************************************

int main(int argc, char *argv[]) {
	test(testStartAddressAligned);
	test(testNonzeroSize);
	test(testDefaultSizeAligned);
	testWithSize(999999, testConfiguredMisalignedSizeBecomesAligned);
	test(testNoWrapAround);
	test(testSetResetSize);
	return 0;
}
