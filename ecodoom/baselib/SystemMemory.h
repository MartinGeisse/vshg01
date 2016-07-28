/*
 *  SystemMemory.h
 *  baselib
 *
 *  Created by Martin on 7/25/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

/**
 * Allocates memory from the underlying system. The amount of
 * memory is fixed internally to reflect the fixed memory size
 * on the ECO32. 
 */
void systemAllocateMemory();

/**
 * Returns a pointer to the allocated system memory, or NULL if
 * no memory has been allocated.
 */
void *systemGetMemory();

/**
 * Returns the size of the allocated memory, or 0 if no memory
 * has been allocated.
 */
int systemGetMemorySize();

/**
 * Disposes of system memory.
 */
void systemDisposeMemory();
