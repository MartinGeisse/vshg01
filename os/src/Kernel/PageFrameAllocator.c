
#include <Base.h>
#include <Poke.h>
#include <Kernel.h>
#include <HeapParameters.h>
#include <PageFrameAllocator.h>

/**
 * This flag indicates whether the heap parameters system is initialized.
 */
static int initialized = 0;

/**
 * Contains the base address of the
 * first page frame in the free list. It is zero
 * if there are no free page frames left or if
 * the page frame allocation system is not initialized.
 */
static unsigned int nextFreePageFrameAddress = 0;

/**
 * This variable is initialized to the total page count and keeps
 * that value.
 */
static unsigned int totalPageFrameCount = 0;

/**
 * This variable is kept to the current number of available pages,
 * such that this number is available without traversing the free
 * page frame list.
 */
static unsigned int remainingPageFrameCount = 0;

/**
 * This function ensures that the PageFrameAllocator system is not yet initialized.
 */
static void mustNotBeInitialized(const char *function) {
	if (initialized) {
		printPanicMessage("%s: PageFrameAllocator already initialized.\n", function);
	}
}

/**
 * This function ensures that the PageFrameAllocator system is initialized.
 */
static void mustBeInitialized(const char *function) {
	if (!initialized) {
		printPanicMessage("%s: PageFrameAllocator not yet initialized.\n", function);
	}
}

// see header file
void initializePageFrameAllocator(void) {
	unsigned int heapStartAddress;
	unsigned int heapSize;
	int i;
	
	/** ensure that the page frame allocator is not yet initialized **/
	mustNotBeInitialized("initializePageFrameAllocator");
	
	/** query heap parameters **/
	heapStartAddress = getHeapStartAddress();
	heapSize = getHeapSize();
	
	/** set up global variables **/
	remainingPageFrameCount = totalPageFrameCount = heapSize / 4096;
	nextFreePageFrameAddress = heapStartAddress;
	
	/** initialize the linked list of free page frames **/
	for (i=0; i<totalPageFrameCount; i++) {
		unsigned int currentPageFrameStartAddress = heapStartAddress + i * 4096;
		unsigned int nextPageFrameStartAddress = heapStartAddress + (i + 1) * 4096;
		if (i == totalPageFrameCount - 1) {
			writeMemory32(currentPageFrameStartAddress, 0);
		} else {
			writeMemory32(currentPageFrameStartAddress, nextPageFrameStartAddress);
		}
	}
	
	/** the page frame allocator is now initialized **/
	initialized = 1;
	
}

// see header file
void finalizePageFrameAllocator(void) {
	
	/** the page frame allocator must have been initialized **/
	mustBeInitialized("finalizePageFrameAllocator");
	
	/** clear global variables **/
	remainingPageFrameCount = totalPageFrameCount = 0;
	nextFreePageFrameAddress = 0;
	
	/** the page frame allocator is now no longer initialized **/
	initialized = 0;
	
}

// see header file
unsigned int getTotalPageFrameCount(void) {
	mustBeInitialized("getTotalPageFrameCount");
	return totalPageFrameCount;
}

// see header file
unsigned int getRemainingPageFrameCount(void) {
	mustBeInitialized("getRemainingPageFrameCount");
	return remainingPageFrameCount;
}

// see header file
void *allocatePageFrame(void) {
	unsigned int allocatedPageFrameAddress;
	
	/** the page frame allocator must have been initialized **/
	mustBeInitialized("allocatePageFrame");
	
	/** check if any free page frames are left **/
	if (nextFreePageFrameAddress == 0) {
		return NULL;
	}
	
	/** de-queue one apge frame from the beginning of the free list and return it **/
	allocatedPageFrameAddress = nextFreePageFrameAddress;
	nextFreePageFrameAddress = (unsigned int)readMemory32(allocatedPageFrameAddress);
	remainingPageFrameCount--;
	return (void*)allocatedPageFrameAddress;
	
}

// see header file
void deallocatePageFrame(void *basePointer) {
	unsigned int baseAddress;
	
	/** the page frame allocator must have been initialized **/
	mustBeInitialized("deallocatePageFrame");
	
	/** check that the base pointer is aligned and convert it to an address **/
	baseAddress = (unsigned int)basePointer;
	if (baseAddress % 4096 != 0) {
		printPanicMessage("deallocatePageFrame(): mis-aligned base address: %d", baseAddress);
	}
	
	/** enqueue the frame in the free list **/
	writeMemory32(baseAddress, nextFreePageFrameAddress);
	nextFreePageFrameAddress = baseAddress;
	remainingPageFrameCount++;
	
}
