/*
 *  ZoneAllocator.h
 *  baselib
 *
 *  Created by Martin on 7/25/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 *  Zone memory allocator. This module uses system memory allocation
 *  internally to allocate one big memory block, which is then provided
 *  to the application using a custom allocation algorithm. Do not
 *  access the system memory allocation module directly when using this
 *  zone allocator!
 */

/**
 * This tag value is commonly used for statically allocated data.
 */
#define STATIC_ALLOCATION_TAG -1

/**
 * This tag is used for "current level" data and is not purgeable.
 */
#define LEVEL_STATIC_ALLOCATION_TAG -2

/**
 * This tag value is commonly used for cached data.
 */
#define CACHED_ALLOCATION_TAG 1

/**
 * Initializes the zone allocator.
 */
void zoneAllocatorInitialize();

/**
 * Shuts down the zone allocator.
 */
void zoneAllocatorShutdown();

/**
 * Allocates memory from the zone allocator and returns a pointer
 * to the allocated memory.
 *
 * The tag indicates the intended use of the memory. The zone allocator
 * does not interpret the actual value of the tag, only its sign:
 * Negative tag values indicate statically allocated memory that is only
 * disposed of with the dispose functions. Positive tags
 * indicate volatile allocation blocks that can be disposed of by
 * the zone allocator itself when running out of memory.
 *
 * The zero tag value is reserved for internal use and must not be used
 * for user-allocated memory.
 *
 * If a pointerToUserPointer is passed (i.e. is not NULL), then it should
 * point to the client's variable where the allocated memory pointer (the
 * return value of this function) is stored. This function will set
 * the variable behind the pointer to the return value. Furthermore,
 * de-allocation of the memory (whether automatic or manual) will cause
 * the variable to be set to NULL again. This can be used by clients
 * to detect de-allocation, and *must* be used for automatically disposable
 * blocks (tag < 0) because there is no other way to detect automatic disposal.
 */
void *zoneAllocatorAllocate(int size, int tag, void *pointerToUserPointer);

/**
 * Disposes of memory that was previously allocated from the zone
 * allocator. This function must not be called for memory that has already
 * been disposed of, not even for memory that was de-allocated automatically.
 * Clients must use the pointerToUserPointer parameter of zoneAllocatorAllocate()
 * to detect such cases.
 */
void zoneAllocatorDispose(void *memory);

/**
 * Turns the whole memory block managed by the zone allocator to
 * "not allocated" state, as if zoneAllocatorDispose() was invoked
 * for all allocated memory blocks, but without resetting user pointers
 * through the "pointers to user pointers".
 */
void zoneAllocatorSetCleared();

/**
 * Disposes of all memory blocks that use a tag in the specified range, as if
 * zoneAllocatorDispose() had been invoked for all such blocks. Both range
 * end values are inclusive.
 */
void zoneAllocatorDisposeByTag(int minTagValue, int maxTagValue);

/**
 * Disposes of all memory blocks with a positive tag.
 */
void zoneAllocatorPurge();

/**
 * Prints debugging information about the zone allocator state.
 */
void zoneAllocatorDump();

/**
 * Verifies the consistency of the zone allocator state.
 */
void zoneAllocatorVerifyConsistency();

/**
 * Changes the tag of an allocated memory block. This can be used, for example,
 * to change the tag sign to allow or disallow automatic de-allocation. Changing
 * a block's tag to a positive value (automatically disposable) requires that a
 * pointerToUserPointer has been set for that block.
 */
void zoneAllocatorChangeTag(void *memory, int newTag);
