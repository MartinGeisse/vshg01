/*
 *  ZoneAllocatorTest.c
 *  baselib
 *
 *  Created by Martin on 7/26/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

#include <stdarg.h>
#include <setjmp.h>
#include "ZoneAllocator.c"
#include "UnitTestSupport.h"

extern int systemPrintDebugMessageCalled;
extern int systemFatalErrorCalled;
extern jmp_buf systemFatalErrorJumpBuffer;
extern int systemFatalErrorJumpBufferActive;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// mock implementations of the system memory allocator
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

int systemMemorySize = 4096;
unsigned char systemMemory[4096];
int systemMemoryInitializeCalled = 0;
int systemMemoryShutdownCalled = 0;

void systemAllocateMemory() {
	systemMemoryInitializeCalled = 1;
}

void *systemGetMemory() {
	return systemMemory;
}

int systemGetMemorySize() {
	return systemMemorySize;
}

void systemDisposeMemory() {
	systemMemoryShutdownCalled = 1;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// testing
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

unsigned char *a;
unsigned char *b;
unsigned char *c;
unsigned char *d;
unsigned char *e;
unsigned char *f;
unsigned char *g;

static void prepareAllocationStartingBlockTests() {
	zoneAllocatorSetCleared();
	a = zoneAllocatorAllocate(16, -5, NULL);
	b = zoneAllocatorAllocate(16, -5, NULL);
	c = zoneAllocatorAllocate(16, -5, NULL);
	d = zoneAllocatorAllocate(16, -5, NULL);
	e = zoneAllocatorAllocate(16, -5, NULL);
	f = zoneAllocatorAllocate(16, -5, NULL);
	g = zoneAllocatorAllocate(16, -5, NULL);
}

static void verifyChainIntegrity(const char *context, int elementCount, ...) {
	int i;
	va_list args;
	struct ZoneAllocatorMemoryBlock *blockTable[elementCount];
	struct ZoneAllocatorMemoryBlock *block;
	
	va_start(args, elementCount);
	for (i=0; i<elementCount; i++) {
		blockTable[i] = va_arg(args, struct ZoneAllocatorMemoryBlock *);
	}
	va_end(args);
	
	block = &data->rootBlock;
	for (i=0; i<elementCount; i++) {
		
		/** make sure the current block matches **/
		assert(block == blockTable[i], "%s chain integrity: current block at position %d", context, i);
		
		/** make sure the back link matches **/
		assert(block->previous == blockTable[(i == 0) ? (elementCount - 1) : (i - 1)], "%s chain integrity: back link at position %d", context, i);
		
		/** make sure the forward link matches **/
		assert(block->next == blockTable[(i == (elementCount - 1)) ? 0 : (i + 1)], "%s chain integrity: forward link at position %d", context, i);
		
		/** move to the next block **/
		block = block->next;
		
	}

}

static void verifyChainTags(const char *context, int elementCount, ...) {
	int i;
	va_list args;
	int tagTable[elementCount];
	struct ZoneAllocatorMemoryBlock *block;
	
	va_start(args, elementCount);
	for (i=0; i<elementCount; i++) {
		tagTable[i] = va_arg(args, int);
	}
	va_end(args);
	
	block = &data->rootBlock;
	for (i=0; i<elementCount; i++) {
		
		/** make sure the tag of the current block matches **/
		assert(block->tag == tagTable[i], "%s chain tags: current tag at position %d", context, i);
		
		/** move to the next block **/
		block = block->next;
		
	}
	
}

static void verifyChainSizes(const char *context, int elementCount, ...) {
	int i;
	va_list args;
	int sizeTable[elementCount];
	struct ZoneAllocatorMemoryBlock *block;
	
	va_start(args, elementCount);
	for (i=0; i<elementCount; i++) {
		sizeTable[i] = va_arg(args, int);
	}
	va_end(args);
	
	block = &data->rootBlock;
	for (i=0; i<elementCount; i++) {
		
		/** make sure the tag of the current block matches **/
		assert(block->size == sizeTable[i], "%s chain sizes: current size at position %d", context, i);
		
		/** move to the next block **/
		block = block->next;
		
	}
	
}

int main() {
	
	/**
	 * Ensure a basic condition on which the zone allocator builds: sizeof() should
	 * return the same as pointer arithmetic for the allocator control structure.
	 */
	{
		struct ZoneAllocatorMemoryBlock mock;
		unsigned char *pointer1 = (unsigned char *)(&mock);
		unsigned char *pointer2 = (unsigned char *)((&mock) + 1);
		assert(sizeof(struct ZoneAllocatorMemoryBlock) == (pointer2 - pointer1), "sizeof() assumption failed");
	}
	
	/**
	 * Initialize the zone allocator.
	 */
	zoneAllocatorInitialize();
	assert(systemFatalErrorCalled == 0, "zoneAllocatorInitialize failed");
	assert(systemMemoryInitializeCalled == 1, "init: systemMemoryInitializeCalled");
	assert(systemMemoryShutdownCalled == 0, "init: systemMemoryShutdownCalled");

	/**
	 * Allocate two blocks
	 */
	a = zoneAllocatorAllocate(100, -5, NULL);
	assert(systemFatalErrorCalled == 0, "zoneAllocatorAllocate failed (1)");
	b = zoneAllocatorAllocate(160, -5, NULL);
	assert(systemFatalErrorCalled == 0, "zoneAllocatorAllocate failed (2)");
	
	/**
	 * Check ring list integrity
	 */
	verifyChainIntegrity("initial allocation of (a, b)", 4, &data->rootBlock, getBlockHeader(a), getBlockHeader(b), data->rootBlock.previous);
	
	/**
	 * Allocate a third block and ensure that they are stored at the correct address.
	 */
	c = zoneAllocatorAllocate(10, -5, NULL);
	assert(systemFatalErrorCalled == 0, "zoneAllocatorAllocate failed (3)");
	assert((int)a - (int)systemMemory == sizeof(struct ZoneAllocatorData) + sizeof(struct ZoneAllocatorMemoryBlock), "position of (a)");
	assert((int)b - (int)systemMemory == sizeof(struct ZoneAllocatorData) + 2 * sizeof(struct ZoneAllocatorMemoryBlock) + 100, "position of (b)");
	assert((int)c - (int)systemMemory == sizeof(struct ZoneAllocatorData) + 3 * sizeof(struct ZoneAllocatorMemoryBlock) + 260, "position of (c)");
	
	/**
	 * The allocation size of block c should have been rounded up to 12 bytes (full word size)
	 */
	d = zoneAllocatorAllocate(16, -5, NULL);
	assert(systemFatalErrorCalled == 0, "zoneAllocatorAllocate failed (4)");
	assert((int)d - (int)systemMemory == sizeof(struct ZoneAllocatorData) + 4 * sizeof(struct ZoneAllocatorMemoryBlock) + 272, "position of (d)");

	/**
	 * Check some values
	 */
	assert(getBlockHeader(a)->size == 100 + sizeof(struct ZoneAllocatorMemoryBlock), "block size (a)");
	assert(getBlockHeader(c)->size == 12 + sizeof(struct ZoneAllocatorMemoryBlock), "block size (c)");
	assert(getBlockHeader(a)->pointerToUserPointer == NULL, "PTUP (a)");
	assert(getBlockHeader(a)->tag == -5, "tag (a)");
	assert(getBlockHeader(a)->corruptionGuard == CORRUPTION_GUARD_VALUE, "corruption guard (a)");
	
	/**
	 * Allocate some more blocks to test de-allocation.
	 */
	e = zoneAllocatorAllocate(16, -5, NULL);
	f = zoneAllocatorAllocate(16, -5, NULL);
	g = zoneAllocatorAllocate(16, -5, NULL);
	assert(systemFatalErrorCalled == 0, "zoneAllocatorAllocate failed (e, f, g)");
	
	/**
	 * We now have a, b, c, d, e, f, g. First we de-allocate b and f and test the integrity of the data structure.
	 * Note that b and f don't disappear, they just become free!
	 */
	zoneAllocatorDispose(b);
	zoneAllocatorDispose(f);
	{
		struct ZoneAllocatorMemoryBlock *freeBlock = data->rootBlock.previous;
		verifyChainIntegrity("dispose(1)", 9, &data->rootBlock, getBlockHeader(a), getBlockHeader(b), getBlockHeader(c), getBlockHeader(d), getBlockHeader(e), getBlockHeader(f), getBlockHeader(g), freeBlock);
		verifyChainTags("dispose(1)", 9, 0, -5, 0, -5, -5, -5, 0, -5, 0);
		verifyChainSizes("dispose(1)", 9, HEADER_SIZE, HEADER_SIZE + 100, HEADER_SIZE + 160, HEADER_SIZE + 12, HEADER_SIZE + 16, HEADER_SIZE + 16, HEADER_SIZE + 16, HEADER_SIZE + 16, 4096 - sizeof(struct ZoneAllocatorData) - 7*HEADER_SIZE - 336);
	}
	
	/**
	 * Next we dispose of c and e, which should merge with b and f, respectively to
	 * form a bigger free block. These are available as b and e (blocks at
	 * smaller addresses are kept).
	 */
	zoneAllocatorDispose(c);
	zoneAllocatorDispose(e);
	{
		struct ZoneAllocatorMemoryBlock *freeBlock = data->rootBlock.previous;
		verifyChainIntegrity("dispose(2)", 7, &data->rootBlock, getBlockHeader(a), getBlockHeader(b), getBlockHeader(d), getBlockHeader(e), getBlockHeader(g), freeBlock);
		verifyChainTags("dispose(2)", 7, 0, -5, 0, -5, 0, -5, 0);
		verifyChainSizes("dispose(2)", 7, HEADER_SIZE, HEADER_SIZE + 100, 2*HEADER_SIZE + 172, HEADER_SIZE + 16, 2*HEADER_SIZE + 32, HEADER_SIZE + 16, 4096 - sizeof(struct ZoneAllocatorData) - 7*HEADER_SIZE - 336);
	}

	/**
	 * Next in line is d. This should cause b, d, e to merge to one big free block
	 * known as b. This leaves us with (root, a, b = free, g, rest = free).
	 */
	zoneAllocatorDispose(d);
	{
		struct ZoneAllocatorMemoryBlock *freeBlock = data->rootBlock.previous;
		verifyChainIntegrity("dispose(3)", 5, &data->rootBlock, getBlockHeader(a), getBlockHeader(b), getBlockHeader(g), freeBlock);
		verifyChainTags("dispose(3)", 5, 0, -5, 0, -5, 0);
		verifyChainSizes("dispose(3)", 5, HEADER_SIZE, HEADER_SIZE + 100, 5*HEADER_SIZE + 220, HEADER_SIZE + 16, 4096 - sizeof(struct ZoneAllocatorData) - 7*HEADER_SIZE - 336);
	}
	
	/**
	 * Now dispose of a. This cannot formally merge with the remaining free block because
	 * the root block is in the way. In practice, of course, it cannot merge because the
	 * end of the "rest" block is at the ending address of the allocation zone, while
	 * the root block is at the beginning -- the list is only logically a ring.
	 *
	 * This test also ensures that free space is not acidentally merged with the root
	 * block, which happens to have a tag value of 0 like free blocks do.
	 *
	 * However, a *can* merge with b.
	 */
	zoneAllocatorDispose(a);
	{
		struct ZoneAllocatorMemoryBlock *freeBlock = data->rootBlock.previous;
		verifyChainIntegrity("dispose(4)", 4, &data->rootBlock, getBlockHeader(a), getBlockHeader(g), freeBlock);
		verifyChainTags("dispose(4)", 4, 0, 0, -5, 0);
		verifyChainSizes("dispose(4)", 4, HEADER_SIZE, 6*HEADER_SIZE + 320, HEADER_SIZE + 16, 4096 - sizeof(struct ZoneAllocatorData) - 7*HEADER_SIZE - 336);
	}

	/**
	 * Finish testing by disposing of g. This should leave the root block and a single free block
	 * at address a.
	 */
	zoneAllocatorDispose(g);
	{
		verifyChainIntegrity("dispose(5)", 2, &data->rootBlock, getBlockHeader(a));
		verifyChainTags("dispose(5)", 2, 0, 0);
		verifyChainSizes("dispose(5)", 2, HEADER_SIZE, 4096 - sizeof(struct ZoneAllocatorData));
	}

	/**
	 * Now test how the allocation starting point moves around when blocks are disposed.
	 */
	prepareAllocationStartingBlockTests();
	data->allocationStartingBlock = getBlockHeader(c);
	zoneAllocatorDispose(c);
	assert(data->allocationStartingBlock == getBlockHeader(c), "dispose starting point");
	zoneAllocatorDispose(b);
	assert(data->allocationStartingBlock == getBlockHeader(b), "dispose before free starting point");
	zoneAllocatorDispose(d);
	assert(data->allocationStartingBlock == getBlockHeader(b), "dispose after free starting point");

	prepareAllocationStartingBlockTests();
	data->allocationStartingBlock = getBlockHeader(c);
	zoneAllocatorDispose(b);
	assert(data->allocationStartingBlock == getBlockHeader(c), "dispose before used starting point");
	zoneAllocatorDispose(d);
	assert(data->allocationStartingBlock == getBlockHeader(c), "dispose after used starting point");
	zoneAllocatorDispose(c);
	assert(data->allocationStartingBlock == getBlockHeader(b), "dispose 'island' starting point block");
	
	/**
	 * Test PTUP for manual disposal
	 */
	{
		void *userPointer;
		zoneAllocatorSetCleared();
		a = zoneAllocatorAllocate(16, -5, &userPointer);
		assert(a == userPointer, "user pointer value equals return value");
		assert((int)userPointer - (int)data ==  + sizeof(struct ZoneAllocatorData) + sizeof(struct ZoneAllocatorMemoryBlock), "user pointer set, manual disposal");
		zoneAllocatorDispose(userPointer);
		assert(userPointer == NULL, "user pointer clear, manual disposal");
		assert(getBlockHeader(a)->pointerToUserPointer == NULL, "PTUP is null, manual disposal");
	}
	
	/**
	 * Allocation algorithm: Should find the next free block that is large enough, and skip non-purgeable blocks on the way.
	 */
	prepareAllocationStartingBlockTests();
	zoneAllocatorDispose(c);
	data->allocationStartingBlock = getBlockHeader(a);
	assert(zoneAllocatorAllocate(4, -5, NULL) == c, "allocate: find free block and skip used ones");

	/**
	 * Allocation algorithm: Should purge a purgeable block instead if there is one at an earlier address,
	 * going from the starting point.
	 */
	prepareAllocationStartingBlockTests();
	getBlockHeader(a)->tag = 5;
	data->allocationStartingBlock = getBlockHeader(b);
	getBlockHeader(c)->tag = 5;
	zoneAllocatorDispose(e);
	assert(zoneAllocatorAllocate(4, -5, NULL) == c, "allocate: find purgeable block");

	/**
	 * Allocation algorithm: Should purge a purgeable block after a free one if necessary to increase size.
	 */
	prepareAllocationStartingBlockTests();
	zoneAllocatorDispose(d);
	getBlockHeader(e)->tag = 5;
	data->allocationStartingBlock = getBlockHeader(b);
	assert(zoneAllocatorAllocate(20, -5, NULL) == d, "allocate: purge purgeable block after free block to increase size");

	/**
	 * Allocation algorithm: Should purge a purgeable block after a free one if necessary to increase size.
	 */
	prepareAllocationStartingBlockTests();
	getBlockHeader(d)->tag = 5;
	zoneAllocatorDispose(e);
	data->allocationStartingBlock = getBlockHeader(b);
	assert(zoneAllocatorAllocate(20, -5, NULL) == d, "allocate: purge purgeable block before free block to increase size");

	/**
	 * Allocation algorithm: Should purge a purgeable block after a free one if necessary to increase size.
	 */
	prepareAllocationStartingBlockTests();
	getBlockHeader(d)->tag = 5;
	getBlockHeader(e)->tag = 5;
	data->allocationStartingBlock = getBlockHeader(b);
	assert(zoneAllocatorAllocate(20, -5, NULL) == d, "allocate: purge purgeable block before purgeable block to increase size");

	/**
	 * Allocation algorithm: Should purge a purgeable block even if the total size is not enough, because the algorithm works that way
	 */
	prepareAllocationStartingBlockTests();
	getBlockHeader(b)->tag = 5;
	getBlockHeader(e)->tag = 5;
	getBlockHeader(f)->tag = 5;
	data->allocationStartingBlock = getBlockHeader(a);
	assert(zoneAllocatorAllocate(20, -5, NULL) == e, "allocate: purge purgeable block even if total size is not enough; return value");
	assert(getBlockHeader(b)->tag == 0, "allocate: purge purgeable block even if total size is not enough; block purged");

	
	/**
	 * Allocation algorithm: Should find a free block before the starting point, because the algorithm works that way
	 */
	prepareAllocationStartingBlockTests();
	zoneAllocatorDispose(b);
	data->allocationStartingBlock = getBlockHeader(c);
	assert(zoneAllocatorAllocate(4, -5, NULL) == b, "allocate: find free block directly before starting point");
	
	/**
	 * Test user pointer reset for purged blocks
	 */
	{
		void *userPointer;
		void *userPointer2;
		prepareAllocationStartingBlockTests();
		zoneAllocatorDispose(f);
		data->allocationStartingBlock = getBlockHeader(a);
		f = zoneAllocatorAllocate(16, 5, &userPointer);
		assert(f == userPointer, "purge block: initial user pointer");
		data->allocationStartingBlock = getBlockHeader(a);
		zoneAllocatorAllocate(16, 3, &userPointer2);
		assert(userPointer == NULL, "purge block: purged user pointer");
		assert(userPointer2 == f, "purge block: new user pointer");
	}

	/**
	 * Test minimal fragment size: a too small free fragment isn't turned into a free block.
	 */
	prepareAllocationStartingBlockTests();
	data->allocationStartingBlock = getBlockHeader(a);
	zoneAllocatorDispose(b);
	assert(b == zoneAllocatorAllocate(8, -5, NULL), "minimal fragment size: allocate");
	assert(getBlockHeader(b)->size == sizeof(struct ZoneAllocatorMemoryBlock) + 16, "minimal fragment size: size");
	assert(getBlockHeader(b)->next == getBlockHeader(c), "minimal fragment size: next");
	assert(getBlockHeader(b)->next->previous == getBlockHeader(b), "minimal fragment size: next->prev");

	/**
	 * Test dispose by tag: Some test values, single and double block.
	 */
	prepareAllocationStartingBlockTests();
	getBlockHeader(a)->tag = -10;
	getBlockHeader(b)->tag = -3;
	getBlockHeader(c)->tag = -1;
	getBlockHeader(d)->tag = 7;
	getBlockHeader(e)->tag = -6;
	getBlockHeader(f)->tag = -5;
	getBlockHeader(g)->tag = 1;
	zoneAllocatorDisposeByTag(-6, -3);
	{
		struct ZoneAllocatorMemoryBlock *freeBlock = data->rootBlock.previous;
		verifyChainIntegrity("dispose by tag(1)", 8, &data->rootBlock, getBlockHeader(a), getBlockHeader(b), getBlockHeader(c), getBlockHeader(d), getBlockHeader(e), getBlockHeader(g), freeBlock);
		verifyChainTags("dispose by tag(1)", 8, 0, -10, 0, -1, 7, 0, 1, 0);
		verifyChainSizes("dispose by tag(1)", 7, HEADER_SIZE, HEADER_SIZE + 16, HEADER_SIZE + 16, HEADER_SIZE + 16, HEADER_SIZE + 16, 2*HEADER_SIZE + 32, HEADER_SIZE + 16);
	}
	
	/**
	 * Test dispose by tag: Merge with previous free; merge with previous free and following disposed
	 */
	prepareAllocationStartingBlockTests();
	getBlockHeader(a)->tag = 0;
	getBlockHeader(b)->tag = -3;
	getBlockHeader(c)->tag = -1;
	getBlockHeader(d)->tag = 0;
	getBlockHeader(e)->tag = -6;
	getBlockHeader(f)->tag = -5;
	getBlockHeader(g)->tag = 1;
	zoneAllocatorDisposeByTag(-6, -3);
	{
		struct ZoneAllocatorMemoryBlock *freeBlock = data->rootBlock.previous;
		verifyChainIntegrity("dispose by tag(2)", 6, &data->rootBlock, getBlockHeader(a), getBlockHeader(c), getBlockHeader(d), getBlockHeader(g), freeBlock);
		verifyChainTags("dispose by tag(2)", 6, 0, 0, -1, 0, 1, 0);
		verifyChainSizes("dispose by tag(2)", 5, HEADER_SIZE, 2*HEADER_SIZE + 32, HEADER_SIZE + 16, 3*HEADER_SIZE + 48, HEADER_SIZE + 16);
	}
	
	/**
	 * Test dispose by tag: Merge with following free; merge with following free and previous disposed
	 */
	prepareAllocationStartingBlockTests();
	getBlockHeader(a)->tag = -3;
	getBlockHeader(b)->tag = 0;
	getBlockHeader(c)->tag = -1;
	getBlockHeader(d)->tag = -6;
	getBlockHeader(e)->tag = -5;
	getBlockHeader(f)->tag = 0;
	getBlockHeader(g)->tag = 1;
	zoneAllocatorDisposeByTag(-6, -3);
	{
		struct ZoneAllocatorMemoryBlock *freeBlock = data->rootBlock.previous;
		verifyChainIntegrity("dispose by tag(3)", 6, &data->rootBlock, getBlockHeader(a), getBlockHeader(c), getBlockHeader(d), getBlockHeader(g), freeBlock);
		verifyChainTags("dispose by tag(3)", 6, 0, 0, -1, 0, 1, 0);
		verifyChainSizes("dispose by tag(3)", 5, HEADER_SIZE, 2*HEADER_SIZE + 32, HEADER_SIZE + 16, 3*HEADER_SIZE + 48, HEADER_SIZE + 16);
	}

	/**
	 * Test dispose by tag: single island
	 */
	prepareAllocationStartingBlockTests();
	getBlockHeader(a)->tag = -1;
	getBlockHeader(b)->tag = -2;
	getBlockHeader(c)->tag = 0;
	getBlockHeader(d)->tag = -3;
	getBlockHeader(e)->tag = 0;
	getBlockHeader(f)->tag = -7;
	getBlockHeader(g)->tag = -8;
	zoneAllocatorDisposeByTag(-6, -3);
	{
		struct ZoneAllocatorMemoryBlock *freeBlock = data->rootBlock.previous;
		verifyChainIntegrity("dispose by tag(3)", 7, &data->rootBlock, getBlockHeader(a), getBlockHeader(b), getBlockHeader(c), getBlockHeader(f), getBlockHeader(g), freeBlock);
		verifyChainTags("dispose by tag(3)", 7, 0, -1, -2, 0, -7, -8, 0);
		verifyChainSizes("dispose by tag(3)", 6, HEADER_SIZE, HEADER_SIZE + 16, HEADER_SIZE + 16, 3*HEADER_SIZE + 48, HEADER_SIZE + 16, HEADER_SIZE + 16);
	}

	/**
	 * Test dispose by tag: double island
	 */
	prepareAllocationStartingBlockTests();
	getBlockHeader(a)->tag = -1;
	getBlockHeader(b)->tag = 0;
	getBlockHeader(c)->tag = -4;
	getBlockHeader(d)->tag = -3;
	getBlockHeader(e)->tag = 0;
	getBlockHeader(f)->tag = -7;
	getBlockHeader(g)->tag = -8;
	zoneAllocatorDisposeByTag(-6, -3);
	{
		struct ZoneAllocatorMemoryBlock *freeBlock = data->rootBlock.previous;
		verifyChainIntegrity("dispose by tag(3)", 6, &data->rootBlock, getBlockHeader(a), getBlockHeader(b), getBlockHeader(f), getBlockHeader(g), freeBlock);
		verifyChainTags("dispose by tag(3)", 6, 0, -1, 0, -7, -8, 0);
		verifyChainSizes("dispose by tag(3)", 5, HEADER_SIZE, HEADER_SIZE + 16, 4*HEADER_SIZE + 64, HEADER_SIZE + 16, HEADER_SIZE + 16);
	}
	
	/**
	 * Test dispose by tag: multiple islands
	 */
	prepareAllocationStartingBlockTests();
	getBlockHeader(a)->tag = 0;
	getBlockHeader(b)->tag = -4;
	getBlockHeader(c)->tag = 0;
	getBlockHeader(d)->tag = -6;
	getBlockHeader(e)->tag = -5;
	getBlockHeader(f)->tag = -0;
	getBlockHeader(g)->tag = -9;
	zoneAllocatorDisposeByTag(-6, -3);
	{
		struct ZoneAllocatorMemoryBlock *freeBlock = data->rootBlock.previous;
		verifyChainIntegrity("dispose by tag(3)", 4, &data->rootBlock, getBlockHeader(a), getBlockHeader(g), freeBlock);
		verifyChainTags("dispose by tag(3)", 4, 0, 0, -9, 0);
		verifyChainSizes("dispose by tag(3)", 3, HEADER_SIZE, 6*HEADER_SIZE + 96, HEADER_SIZE + 16);
	}
	
	/**
	 * Test change tag.
	 */
	prepareAllocationStartingBlockTests();
	zoneAllocatorChangeTag(d, -7);
	{
		struct ZoneAllocatorMemoryBlock *freeBlock = data->rootBlock.previous;
		verifyChainIntegrity("dispose by tag(3)", 9, &data->rootBlock, getBlockHeader(a), getBlockHeader(b), getBlockHeader(c), getBlockHeader(d), getBlockHeader(e), getBlockHeader(f), getBlockHeader(g), freeBlock);
		verifyChainTags("dispose by tag(3)", 9, 0, -5, -5, -5, -7, -5, -5, -5, 0);
		verifyChainSizes("dispose by tag(3)", 8, HEADER_SIZE, HEADER_SIZE + 16, HEADER_SIZE + 16, HEADER_SIZE + 16, HEADER_SIZE + 16, HEADER_SIZE + 16, HEADER_SIZE + 16, HEADER_SIZE + 16);
	}
	
	/**
	 * Change tag to 0 should never work, neither with a PTUP set...
	 */
	systemFatalErrorCalled = 0;
	prepareAllocationStartingBlockTests();
	getBlockHeader(d)->pointerToUserPointer = (void*)(&d);
	zoneAllocatorChangeTag(d, 0);
	assert(systemFatalErrorCalled == 1, "change tag to 0, PTUP set");

	/**
	 * nor without
	 */
	systemFatalErrorCalled = 0;
	prepareAllocationStartingBlockTests();
	getBlockHeader(d)->pointerToUserPointer = NULL;
	zoneAllocatorChangeTag(d, 0);
	assert(systemFatalErrorCalled == 1, "change tag to 0, PTUP cleared");

	/**
	 * Setting to a negative tag with no PTUP set has been tried in the basic test above.
	 * Now try with a PTUP set -- should work.
	 */
	systemFatalErrorCalled = 0;
	prepareAllocationStartingBlockTests();
	getBlockHeader(d)->pointerToUserPointer = (void*)(&d);
	zoneAllocatorChangeTag(d, -1);
	assert(systemFatalErrorCalled == 0, "change tag to negative, PTUP set");
	
	/**
	 * Change tag to positive should work with a PTUP set...
	 */
	systemFatalErrorCalled = 0;
	prepareAllocationStartingBlockTests();
	getBlockHeader(d)->pointerToUserPointer = (void*)(&d);
	zoneAllocatorChangeTag(d, 5);
	assert(systemFatalErrorCalled == 0, "change tag to positive, PTUP set");
	
	/**
	 * but not without
	 */
	systemFatalErrorCalled = 0;
	prepareAllocationStartingBlockTests();
	getBlockHeader(d)->pointerToUserPointer = NULL;
	zoneAllocatorChangeTag(d, 5);
	assert(systemFatalErrorCalled == 1, "change tag to positive, PTUP cleared");

	/**
	 * Try running out of memory
	 */
	systemFatalErrorCalled = 0;
	zoneAllocatorSetCleared();
	a = zoneAllocatorAllocate(2048, -5, NULL);
	assert(systemFatalErrorCalled == 0, "before running out of memory");
	b = zoneAllocatorAllocate(2048, -5, NULL);
	assert(systemFatalErrorCalled == 1, "after running out of memory");

	/**
	 * Test various conditions zoneAllocatorVerifyBlockConsistency should regard valid.
	 */
	systemFatalErrorCalled = 0;
	zoneAllocatorSetCleared();
	zoneAllocatorVerifyBlockConsistency(&data->rootBlock);
	assert(systemFatalErrorCalled == 0, "block consistent (1)");
	
	systemFatalErrorCalled = 0;
	prepareAllocationStartingBlockTests();
	zoneAllocatorVerifyBlockConsistency(&data->rootBlock);
	zoneAllocatorVerifyBlockConsistency(getBlockHeader(a));
	assert(systemFatalErrorCalled == 0, "block consistent (2)");

	systemFatalErrorCalled = 0;
	prepareAllocationStartingBlockTests();
	getBlockHeader(a)->tag = 0;
	zoneAllocatorVerifyBlockConsistency(getBlockHeader(a));
	assert(systemFatalErrorCalled == 0, "block consistent (3a)");
	zoneAllocatorVerifyBlockConsistency(data->rootBlock.previous);
	assert(systemFatalErrorCalled == 0, "block consistent (3b)");
	
	/**
	 * Test various conditions zoneAllocatorVerifyBlockConsistency should regard invalid.
	 */
	
	systemFatalErrorCalled = 0;
	prepareAllocationStartingBlockTests();
	getBlockHeader(a)->corruptionGuard = 1;
	systemFatalErrorJumpBufferActive = 1;
	if (!setjmp(systemFatalErrorJumpBuffer)) {
		zoneAllocatorVerifyBlockConsistency(getBlockHeader(a));
	}
	systemFatalErrorJumpBufferActive = 0;
	assert(systemFatalErrorCalled == 1, "block inconsistent (1)");

	systemFatalErrorCalled = 0;
	prepareAllocationStartingBlockTests();
	getBlockHeader(a)->size = 36;
	zoneAllocatorVerifyBlockConsistency(getBlockHeader(a));
	assert(systemFatalErrorCalled == 1, "block inconsistent (2)");

	systemFatalErrorCalled = 0;
	prepareAllocationStartingBlockTests();
	getBlockHeader(a)->previous = getBlockHeader(d);
	zoneAllocatorVerifyBlockConsistency(getBlockHeader(a));
	assert(systemFatalErrorCalled == 1, "block inconsistent (3)");

	systemFatalErrorCalled = 0;
	prepareAllocationStartingBlockTests();
	getBlockHeader(a)->next = getBlockHeader(d);
	zoneAllocatorVerifyBlockConsistency(getBlockHeader(a));
	assert(systemFatalErrorCalled == 1, "block inconsistent (4)");

	systemFatalErrorCalled = 0;
	prepareAllocationStartingBlockTests();
	getBlockHeader(c)->tag = 0;
	getBlockHeader(d)->tag = 0;
	zoneAllocatorVerifyBlockConsistency(getBlockHeader(c));
	assert(systemFatalErrorCalled == 1, "block inconsistent (5)");

	systemFatalErrorCalled = 0;
	prepareAllocationStartingBlockTests();
	getBlockHeader(c)->tag = 5;
	getBlockHeader(c)->pointerToUserPointer = NULL;
	zoneAllocatorVerifyBlockConsistency(getBlockHeader(c));
	assert(systemFatalErrorCalled == 1, "block inconsistent (6)");

	/**
	 * Now test some things the overall validator should regard invalid, including
	 * one of the block inconsistencies tested above.
	 */
	systemFatalErrorCalled = 0;
	prepareAllocationStartingBlockTests();
	getBlockHeader(c)->tag = +5;
	getBlockHeader(c)->pointerToUserPointer = NULL;
	zoneAllocatorVerifyConsistency();
	assert(systemFatalErrorCalled == 1, "zone allocator inconsistent (1)");

	/**
	 * Shutdown the zone allocator.
	 */
	systemFatalErrorCalled = 0;
	zoneAllocatorShutdown();
	assert(systemFatalErrorCalled == 0, "zoneAllocatorShutdown failed");
	assert(systemMemoryInitializeCalled == 1, "shutdown: systemMemoryInitializeCalled");
	assert(systemMemoryShutdownCalled == 1, "shutdown: systemMemoryShutdownCalled");

	return 0;
}

