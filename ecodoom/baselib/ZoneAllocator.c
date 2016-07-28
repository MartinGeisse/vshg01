/*
 *  ZoneAllocator.c
 *  baselib
 *
 *  Created by Martin on 7/25/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

#include "Common.h"
#include "SystemDebug.h"
#include "SystemMemory.h"
#include "ZoneAllocator.h"

/**
 * This value is used to protect the memory allocation structures against corruption.
 */
#define CORRUPTION_GUARD_VALUE 0x1d4a11

/**
 * The number of bytes reserved for the allocator at the beginning of each block before the user area
 */
#define HEADER_SIZE sizeof(struct ZoneAllocatorMemoryBlock)

/**
 * The minimal size of a block's remaining memory to turn it into a free block on its own
 */
#define MINIMAL_FRAGMENT_SIZE (2 * HEADER_SIZE)

/**
 * Executes the body for every block except the root block.
 */
#define FOREACH_BLOCK(iterationVariable,body) \
	for (iterationVariable = data->rootBlock.next; iterationVariable != &data->rootBlock; iterationVariable = iterationVariable->next) { \
		body; \
	}

/**
 * This structure describes a memory block. Blocks are kept in a double-linked
 * ring list, ordered by their address. The list contains both allocated and free
 * blocks, but never two free blocks in a row. 
 */
struct ZoneAllocatorMemoryBlock {
	
	/**
	 * The size of this block, including this header structure and optional unused
	 * padding bytes at the end.
	 */
	int size;
	
	/**
	 * If non-NULL, then the target of this pointer is set to NULL at de-allocation.
	 * This field is NULL for free blocks.
	 */
	void **pointerToUserPointer;
	
	/**
	 * The tag that controls automatic de-allocation and can be used for client-specific
	 * purposes. This field is 0 to indicate a free block or root block.
	 */
	int tag;
	
	/**
	 * This field is set to CORRUPTION_GUARD_VALUE at allocation time and never
	 * explicitly changed. The field is then checked when other functions of this allocator
	 * are called. If the value has changed, then the allocation control structures
	 * have been overwritten by the client and must be considered corrupted.
	 */
	int corruptionGuard;
	
	/**
	 * The previous block in the list.
	 */
	struct ZoneAllocatorMemoryBlock *previous;
	
	/**
	 * The next block in the list.
	 */
	struct ZoneAllocatorMemoryBlock *next;

};

/**
 * This structure describes the main information for the allocation algorithm.
 */
struct ZoneAllocatorData {
	
	/**
	 * The size of the underlying memory zone allocated from the system,
	 * including this header and the root block.
	 */
	int size;
	
	/**
	 * This block is used as the basis for the allocation algorithm. This is an
	 * optimization that prevents the algorithm to skip a sequence of
	 * already-allocated blocks from the root block again and again.
	 * Instead, the algorithm starts at its last location.
	 * This also makes sure that we don't purge blocks at the beginning
	 * of the list needlessly.
	 */
	struct ZoneAllocatorMemoryBlock *allocationStartingBlock;
	
	/**
	 * The root block. This block appears routinely in the ring list of memory blocks,
	 * and can be used as a starting point to access that list. It is the only
	 * non-free block that uses a tag value of zero, uses an allocation size that
	 * just fits the header, and its address is the lowest address that occurs in
	 * the block list.
	 */
	struct ZoneAllocatorMemoryBlock rootBlock;
	
};

/**
 * The main allocator data header. Since the header appears at the start of the
 * allocation zone, this pointer is identical to the allocation area pointer
 * of the underlying system memory allocation module.
 */
static struct ZoneAllocatorData *data;

/**
 * Returns a pointer to the block header for a given block user data, and also
 * ensures that the header's corruption guard is intact.
 */
static struct ZoneAllocatorMemoryBlock *getBlockHeader(void *blockUserData) {
	struct ZoneAllocatorMemoryBlock *header = ((struct ZoneAllocatorMemoryBlock *)blockUserData) - 1;
	if (header->corruptionGuard != CORRUPTION_GUARD_VALUE) {
		systemFatalError("Heap corrupted! Block has a damaged corruption guard.");
		return NULL;
	}
	return header;
}

/**
 * Tries to merge this block with the next block. This is possible if both blocks
 * are free. If so, the next block is merged into the argument block. The argument
 * thus stays present in any case, but the next block may disappear.
 */
static void tryMergeFreeBlocks(struct ZoneAllocatorMemoryBlock *block) {
	struct ZoneAllocatorMemoryBlock *nextBlock = block->next;
	
	/** check if the argument block is empty **/
	if (block->tag != 0 || block == &data->rootBlock) {
		return;
	}

	/** check if the next block is empty **/
	if (nextBlock->tag != 0 || nextBlock == &data->rootBlock) {
		return;
	}
	
	/** merge the blocks **/
	block->size += nextBlock->size;
	block->next = nextBlock->next;
	block->next->previous = block;
	if (data->allocationStartingBlock == nextBlock) {
		data->allocationStartingBlock = block;
	}
	
}

/**
 * Disposes of the specified memory block.
 */
static void zoneAllocatorDisposeInternal(struct ZoneAllocatorMemoryBlock *block) {
	
	/** ensure that this is a disposable block **/
	if (block->tag == 0) {
		systemFatalError("Trying to dispose non-disposable memory block");
		return;
	}
	
	/**
	 * If the user has specified a pointerToUserPointer, we reset
	 * the user's pointer now.
	 */
	if (block->pointerToUserPointer != NULL) {
		*(block->pointerToUserPointer) = NULL;
	}
	
	/** mark the block as free **/
	block->pointerToUserPointer = NULL;
	block->tag = 0;
	
	/** try to merge adjacent free blocks **/
	tryMergeFreeBlocks(block);
	tryMergeFreeBlocks(block->previous);
	
}

/**
 * See header file for information.
 */
void zoneAllocatorInitialize() {
	systemAllocateMemory();
	data = systemGetMemory();
	data->size = systemGetMemorySize();
	zoneAllocatorSetCleared();
}

/**
 * See header file for information.
 */
void zoneAllocatorShutdown() {
	systemDisposeMemory();
}

/**
 * See header file for information.
 */
void *zoneAllocatorAllocate(int size, int tag, void *pointerToUserPointer) {
	struct ZoneAllocatorMemoryBlock *baseBlock;

	/**
	 * If no pointerToUserPointer is available, the block must not be
	 * purgeable, because the user could not detect purging.
	 */
	if (tag > 0 && pointerToUserPointer == NULL) {
		systemFatalError("Trying to allocate a purgeable block without pointerToUserPointer");
		return NULL;
	}

	/**
	 * Determine the block size we actually need. First we round up to full words,
	 * then we add the header size.
	 */
	size = ((size + 3) & ~3) + HEADER_SIZE;

	/**
	 * Determine the starting block. This is the allocation starting block from the
	 * main allocation control structure, except if its previous block is free.
	 * This is needed to ensure the loop invariant described below.
	 */
	baseBlock = data->allocationStartingBlock;
	if (baseBlock->previous->tag == 0 && baseBlock->previous != &data->rootBlock) {
		baseBlock = baseBlock->previous;
	}

	/**
	 * Move around the ring list to find free memory
	 */
	while (1) {
		
		/** we can't touch non-purgeable blocks nor the root block **/
		if (baseBlock->tag >= 0 && baseBlock != &data->rootBlock) {

			/**
			 * Loop invariant: If the base block is a purgeable block or free block, then its
			 * previous block is not a free block. This is trivially true if the base block is a
			 * free block, because two free blocks don't occur in a row. 
			 *
			 * If the base block is a purgeable allocated block, this invariant is ensured by
			 *
			 * (1) the preparation step above that starts allocation with the previous block of
			 *     the allocation starting point if that block is a free block
			 *
			 * (2) the fact that, during allocation, when a free block is used as the base block,
			 *     any subsequent purgeable blocks are purged
			 *
			 * The effect of this invariant is that if the base block is a purgeable block, then
			 * purging it does not merge it with the previous block.
			 */
			
			/** try to purge this block **/
			if (baseBlock->tag > 0) {
				zoneAllocatorDisposeInternal(baseBlock);
			}
			
			/** purge subsequent blocks **/
			while (baseBlock->next->tag > 0 && baseBlock->size < size) {
				zoneAllocatorDisposeInternal(baseBlock->next);
			}
			
			/** The next block is not purgeable. Let's see if the base block is large enough. **/
			if (baseBlock->size >= size) {
				break;
			}
			
		}
		
		/** move to the next block and check for failure **/
		baseBlock = baseBlock->next;
		if (baseBlock == data->allocationStartingBlock) {
			systemFatalError("out of memory");
			return NULL;
		}
		
	}
	
	/**
	 * We have a base block that is large anough. If the remaining size of the base block is
	 * large enough, we turn it into a free block. Otherwise we just leave it at the end
	 * of the allocated block unused.
	 */
	if (baseBlock->size - size >= MINIMAL_FRAGMENT_SIZE) {
		struct ZoneAllocatorMemoryBlock *fragment = (struct ZoneAllocatorMemoryBlock *)(((unsigned char *)baseBlock) + size);
		fragment->size = baseBlock->size - size;
		fragment->pointerToUserPointer = NULL;
		fragment->tag = 0;
		fragment->corruptionGuard = CORRUPTION_GUARD_VALUE;
		fragment->previous = baseBlock;
		fragment->next = baseBlock->next;
		baseBlock->next->previous = fragment;
		baseBlock->next = fragment;
		baseBlock->size = size;
	}
	
	/**
	 * Store a pointer to the allocated memory in the user's pointer variable (if any)
	 */
	void *userArea = (void *)(baseBlock + 1);
	if (pointerToUserPointer != NULL) {
		*((void **)pointerToUserPointer) = userArea;
	}
	
	/**
	 * Fill the remaining header fields.
	 */
	baseBlock->pointerToUserPointer = pointerToUserPointer;
	baseBlock->tag = tag;
	
	/**
	 * Remember the block after our allocated base block as the starting point for
	 * the next allocation
	 */
	data->allocationStartingBlock = baseBlock->next;

	/** return a pointer to the user area **/
	return userArea;

}

/**
 * See header file for information.
 */
void zoneAllocatorDispose(void *memory) {
	zoneAllocatorDisposeInternal(getBlockHeader(memory));
}

/**
 * See header file for information.
 */
void zoneAllocatorSetCleared() {
	struct ZoneAllocatorMemoryBlock *freeBlock;
	
	/** initialize the root block **/
	data->rootBlock.size = HEADER_SIZE;
	data->rootBlock.pointerToUserPointer = NULL;
	data->rootBlock.tag = 0;
	data->rootBlock.corruptionGuard = CORRUPTION_GUARD_VALUE;
	
	/** create a free block that contains all unused memory, and build the block list **/
	freeBlock = (struct ZoneAllocatorMemoryBlock *)(data + 1);
	data->rootBlock.previous = freeBlock;
	data->rootBlock.next = freeBlock;
	freeBlock->previous = &(data->rootBlock);
	freeBlock->next = &(data->rootBlock);
	
	/** initialize the free block **/
	freeBlock->size = data->size - sizeof(struct ZoneAllocatorData);
	freeBlock->pointerToUserPointer = NULL;
	freeBlock->tag = 0;
	freeBlock->corruptionGuard = CORRUPTION_GUARD_VALUE;
	
	/** the initial allocation starting point is the big free block **/
	data->allocationStartingBlock = freeBlock;
	
}

/**
 * See header file for information.
 */
void zoneAllocatorDisposeByTag(int minTagValue, int maxTagValue) {
	struct ZoneAllocatorMemoryBlock *block;
	
	/**
	 * We must use this rather complex way of moving to the next block because
	 * freeing the current block may in the worst case merge it with the
	 * previous and next blocks.
	 */
	FOREACH_BLOCK(block, {
		if (block->tag >= minTagValue && block->tag <= maxTagValue) {
			struct ZoneAllocatorMemoryBlock *blockToFree = block;
			block = block->previous;
			zoneAllocatorDisposeInternal(blockToFree);
		}
	});
}

/**
 * See header file for information.
 */
void zoneAllocatorPurge() {
	zoneAllocatorDisposeByTag(1, 0x7fffffff);
}

/**
 * Dumps information about a single block.
 */
static void zoneAllocatorDumpBlock(struct ZoneAllocatorMemoryBlock *block) {
	systemPrintDebugMessage("Block at %p, size %d, tag %d, pointerToUserPointer: %p, previous: %p, next: %p\n", block, block->size, block->tag, block->pointerToUserPointer, block->previous, block->next);
}

/**
 * See header file for information.
 */
void zoneAllocatorDump() {
	struct ZoneAllocatorMemoryBlock *block;
	
	/** zone header **/
	systemPrintDebugMessage("Allocation zone position: %p, size %d, start: %p\n", data, data->size, data->allocationStartingBlock);
	
	/** root block header (not included in the loop below) **/
	zoneAllocatorDumpBlock(&data->rootBlock);
	
	/** other blocks **/
	FOREACH_BLOCK(block, zoneAllocatorDumpBlock(block));

}

/**
 * Verifies the consistency of a single block header.
 */
static void zoneAllocatorVerifyBlockConsistency(struct ZoneAllocatorMemoryBlock *block) {
	unsigned char *blockBytes = (unsigned char *)block;

	/** check the corruption guard **/
	if (block->corruptionGuard != CORRUPTION_GUARD_VALUE) {
		systemFatalError("heap corrupted: corruption guard damaged");
		return;
	}

	/** the next block must begin exactly where this block ends **/
	if ((struct ZoneAllocatorMemoryBlock *)(blockBytes + block->size) != block->next && block->next != &data->rootBlock) {
		systemFatalError("heap corrupted: block end does not match beginning of next block");
		return;
	}

	/** check integrity of the double links **/
	if (block->next->previous != block) {
		systemFatalError("heap corrupted: block list links damaged (BNP)");
		return;
	}

	if (block->previous->next != block) {
		systemFatalError("heap corrupted: block list links damaged (BPN)");
		return;
	}

	/** check that no two free blocks occur in a row **/
	if (block != &data->rootBlock && block->next != &data->rootBlock && block->tag == 0 && block->next->tag == 0) {
		systemFatalError("heap corrupted: two free blocks in a row");
		return;
	}

	/** check that automatically disposable blocks have a pointerToUserPointer **/
	if (block->tag > 0 && block->pointerToUserPointer == NULL) {
		systemFatalError("heap corrupted: found automatically disposable block without pointerToUserPointer");
		return;
	}

}

/**
 * See header file for information.
 */
void zoneAllocatorVerifyConsistency() {
	struct ZoneAllocatorMemoryBlock *block;
	zoneAllocatorVerifyBlockConsistency(&data->rootBlock);
	FOREACH_BLOCK(block, zoneAllocatorVerifyBlockConsistency(block));
}

/**
 * See header file for information.
 */
void zoneAllocatorChangeTag(void *memory, int newTag) {
	struct ZoneAllocatorMemoryBlock *block = getBlockHeader(memory);
	if (newTag == 0) {
		systemFatalError("trying to set memory block tag to 0");
		return;
	} else if (newTag > 0 && block->pointerToUserPointer == NULL) {
		systemFatalError("trying to make a block purgeable that has no pointerToUserPointer");
		return;
	}
	block->tag = newTag;
}
