
#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>

#include <CrossTestUtils.h>
#include <HeapParameters.h>
#include <PageFrameAllocator.h>
#include <Poke.h>

// *****************************************************************************************************************************************
// test functions
// *****************************************************************************************************************************************

void testPageFrameCount() {
	int total, remaining;
	void *pageFrame1;
	void *pageFrame2;
	
	total = getTotalPageFrameCount();
	remaining = getRemainingPageFrameCount();
	assertTrue(total == 9, "wrong total page frame count: %d", total);
	assertTrue(remaining == 9, "wrong initial remaining page frame count: %d", remaining);
	
	pageFrame1 = allocatePageFrame();
	
	total = getTotalPageFrameCount();
	remaining = getRemainingPageFrameCount();
	assertTrue(total == 9, "wrong total page frame count: %d", total);
	assertTrue(remaining == 8, "wrong modified(1) remaining page frame count: %d", remaining);

	pageFrame2 = allocatePageFrame();
	
	total = getTotalPageFrameCount();
	remaining = getRemainingPageFrameCount();
	assertTrue(total == 9, "wrong total page frame count: %d", total);
	assertTrue(remaining == 7, "wrong modified(2) remaining page frame count: %d", remaining);
	
	deallocatePageFrame(pageFrame1);

	total = getTotalPageFrameCount();
	remaining = getRemainingPageFrameCount();
	assertTrue(total == 9, "wrong total page frame count: %d", total);
	assertTrue(remaining == 8, "wrong modified(3) remaining page frame count: %d", remaining);

}

/**
 * Note: This method specifically tests internals of the allocator. This unnecessarily fixes
 * the test code to the implementation of the allocator. On the other hand, it gives
 * greater certainty about the correctness of the allocator code.
 */
void testAllocateAndReturn() {
	void *pageFrame1 = allocatePageFrame();
	void *pageFrame2 = allocatePageFrame();
	void *pageFrame3 = allocatePageFrame();
	assertTrue(pageFrame2 - pageFrame1 == 4096, "wrong page address difference (1): %d", pageFrame2 - pageFrame1);
	assertTrue(pageFrame3 - pageFrame2 == 4096, "wrong page address difference (2): %d", pageFrame3 - pageFrame2);
	
	deallocatePageFrame(pageFrame2);
	deallocatePageFrame(pageFrame1);
	assertTrue(readMemory32((unsigned int)pageFrame1) == (int)pageFrame2, "returned page not correctly linked");
	
	assertTrue(allocatePageFrame() == pageFrame1, "just returned page not re-allocated");
}

void testOutOfPages() {
	void *pageFrame0 = allocatePageFrame();
	void *pageFrame1 = allocatePageFrame();
	void *pageFrame2 = allocatePageFrame();
	void *pageFrame3 = allocatePageFrame();
	void *pageFrame4 = allocatePageFrame();
	void *pageFrame5;
	void *pageFrame6;
	void *pageFrame7;
	
	assertTrue(pageFrame0 != NULL, "allocator returned NULL with memory left (0)");
	assertTrue(pageFrame1 != NULL, "allocator returned NULL with memory left (1)");
	assertTrue(pageFrame2 != NULL, "allocator returned NULL with memory left (2)");
	assertTrue(pageFrame3 == NULL, "allocator returned non-NULL with no memory left (1)");
	assertTrue(pageFrame4 == NULL, "allocator returned non-NULL with no memory left (2)");
	
	deallocatePageFrame(pageFrame1);
	deallocatePageFrame(pageFrame0);
	pageFrame5 = allocatePageFrame();
	pageFrame6 = allocatePageFrame();
	pageFrame7 = allocatePageFrame();
	
	assertTrue(pageFrame5 == pageFrame0, "page not re-allocated correctly (1)");
	assertTrue(pageFrame6 == pageFrame1, "page not re-allocated correctly (2)");
	assertTrue(pageFrame7 == NULL, "allocator returned non-NULL with no memory left (3)");
}

// *****************************************************************************************************************************************
// test helper functions
// *****************************************************************************************************************************************

void test(int size, void (*proc)()) {
	configureHeapParameters(size);
	initializeHeapParameters();
	initializePageFrameAllocator();
	(*proc)();
	finalizePageFrameAllocator();
	finalizeHeapParameters();
}

// *****************************************************************************************************************************************
// test suite main function
// *****************************************************************************************************************************************

int main(int argc, char *argv[]) {
	test(9 * 4096 + 15, testPageFrameCount);
	test(9 * 4096, testAllocateAndReturn);
	test(3 * 4096, testOutOfPages);
	return 0;
}
