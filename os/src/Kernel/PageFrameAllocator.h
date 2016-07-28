
#ifndef __KERNEL_PAGEFRAMEALLOCATOR_H__
#define __KERNEL_PAGEFRAMEALLOCATOR_H__

/**
 * Initializes the page frame allocation system.
 */
void initializePageFrameAllocator(void);

/**
 * De-initializes the page frame allocation system.
 */
void finalizePageFrameAllocator(void);

/**
 * Returns the total number of heap pages.
 */
unsigned int getTotalPageFrameCount(void);

/**
 * Returns the remaining number of heap pages.
 */
unsigned int getRemainingPageFrameCount(void);

/**
 * Allocates a page frame and returns its base address. The page frame
 * 4k bytes long and its base address is aligned to that size.
 */
void *allocatePageFrame(void);

/**
 * De-allocates the page frame starting at the specified base address.
 * The argument must be aligned to 4k (the page size) and must point
 * to an allocated page frame.
 */
void deallocatePageFrame(void *baseAddress);

#endif
