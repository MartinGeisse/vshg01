/*
 *  SystemMemory.c
 *  baselib
 *
 *  Created by Martin on 7/25/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

#include <stdlib.h>
#include "SystemDebug.h"
#include "SystemMemory.h"

/**
 * The amount of memory to allocate.
 */
#ifndef MEMORY_SIZE
#define MEMORY_SIZE		(24 * 1024 * 1024)
#endif

/**
 * The allocated memory.
 */
static void *allocatedMemory = NULL;

/**
 * See header file for information.
 */
void systemAllocateMemory() {
	if (allocatedMemory != NULL) {
		systemFatalError("systemAllocateMemory: memory already allocated");
	}
	allocatedMemory = malloc(MEMORY_SIZE);
}

/**
 * See header file for information.
 */
void *systemGetMemory() {
	return allocatedMemory;
}

/**
 * See header file for information.
 */
int systemGetMemorySize() {
	return (allocatedMemory == NULL) ? 0 : MEMORY_SIZE;
}

/**
 * See header file for information.
 */
void systemDisposeMemory() {
	if (allocatedMemory == NULL) {
		systemFatalError("systemDisposeMemory: memory not allocated");
	}
	free(allocatedMemory);
	allocatedMemory = NULL;
}
