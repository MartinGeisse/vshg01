/*
 *  WadFile.c
 *  baselib
 *
 *  Created by Martin on 7/29/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

#include <fcntl.h>
#include <unistd.h>
#include <ctype.h>
#include <string.h>
#include "Common.h"
#include "SystemDebug.h"
#include "ZoneAllocator.h"
#include "WadFile.h"

/**
 * Provides the total number of lumps.
 */
#define LUMP_COUNT			(wadFile.lumpCount)

/**
 * Provides the lump header at index i.
 */
#define LUMP_HEADER(i)		(wadFile.lumpHeaders[i])

/**
 * Loop forward through all lumps.
 */
#define FOREACH_LUMP(i,lumpHeader,loopBody)							\
	for (i=0; i<wadFile.lumpCount; i++) {							\
		struct WadFileLump *lumpHeader = wadFile.lumpHeaders + i;	\
		loopBody;													\
	}

/**
 * Loop backward through all lumps.
 */
#define FOREACH_LUMP_BACKWARD(i,lumpHeader,loopBody)				\
	for (i=wadFile.lumpCount - 1; i >=0 ; i--) {					\
		struct WadFileLump *lumpHeader = wadFile.lumpHeaders + i;	\
		loopBody;													\
	}

/**
 * Header information for a WAD file lump, as well as an optional
 * pointer to the cached lump contents.
 */
struct WadFileLump {
	
	/**
	 * The offset of this lump inside the WAD file.
	 */
	int positionInWadFile;
	
	/**
	 * The size of this lump in bytes.
	 */
	int size;
	
	/**
	 * The name of this lump.
	 */
	struct DoomName name;
	
	/**
	 * The contents if this lump if currently cached in memory, or NULL if
	 * not currently cached.
	 */
	void *cachedContents;
	
};

/**
 * A structure describing a whole WAD file.
 */
struct WadFile {
	
	/**
	 * The WAD file tag. Currently, this *must* be IWAD.
	 */
	char tag[4];
	
	/**
	 * The number of lumps in this file.
	 */
	int lumpCount;
	
	/**
	 * A pointer to the lump header array.
	 */
	struct WadFileLump *lumpHeaders;
	
	/**
	 * An open file handle for the WAD file.
	 */
	int fileHandle;
	
};

/**
 * Currently, only a single global IWAD file is supported.
 */
static struct WadFile wadFile;

/**
 * See header file for information.
 */
void initializeWadFile(const char *filename) {
	int fileHandle;
	int i;

	/** open the WAD file **/
	fileHandle = wadFile.fileHandle = open(filename, O_RDONLY);
	if (fileHandle == -1) {
		systemFatalError("cannot open WAD file: %s", filename);
	}

	/** read the WAD file tag **/
	read(fileHandle, wadFile.tag, 4);
	if (strncmp(wadFile.tag, "IWAD", 4) != 0) {
		systemFatalError("Invalid WAD file tag (can only handle IWAD files): %4s", wadFile.tag);
	}

	/** read the number of lumps **/
	read(fileHandle, &(wadFile.lumpCount), 4);
	wadFile.lumpCount = fromLittleEndian32(wadFile.lumpCount);
	if (wadFile.lumpCount < 0 || wadFile.lumpCount > 1000000) {
		systemFatalError("Invalid lump count: %d", wadFile.lumpCount);
	}

	/** read the position of the lump header table in the file **/
	int lumpHeaderPosition;
	read(fileHandle, &lumpHeaderPosition, 4);
	lumpHeaderPosition = fromLittleEndian32(lumpHeaderPosition);

	/** read the lump headers **/
	lseek(fileHandle, lumpHeaderPosition, SEEK_SET);
	wadFile.lumpHeaders = zoneAllocatorAllocate(sizeof(struct WadFileLump) * wadFile.lumpCount, STATIC_ALLOCATION_TAG, NULL);
	FOREACH_LUMP(i, lumpHeader, {

		/** read the position in WAD file **/
		read(fileHandle, &(lumpHeader->positionInWadFile), 4);
		lumpHeader->positionInWadFile = fromLittleEndian32(lumpHeader->positionInWadFile);

		/** read the lump size **/
		read(fileHandle, &(lumpHeader->size), 4);
		lumpHeader->size = fromLittleEndian32(lumpHeader->size);

		/** read the lump name **/
		read(fileHandle, lumpHeader->name.value, 8);

		/** contents are initially not cached **/
		lumpHeader->cachedContents = NULL;

	});

}

/**
 * See header file for information.
 */
void shutdownWadFile() {
	int i;
	FOREACH_LUMP(i,lumpHeader, {
		if (lumpHeader->cachedContents != NULL) {
			zoneAllocatorDispose(lumpHeader->cachedContents);
		}
	});
	zoneAllocatorDispose(wadFile.lumpHeaders);
	wadFile.lumpCount = 0;
	wadFile.lumpHeaders = NULL;
	close(wadFile.fileHandle);
}

/**
 * See header file for information.
 */
void dumpWadFile() {
	int i;
	systemPrintDebugMessage("--- WAD file ---\n");
	systemPrintDebugMessage("tag: %4s\n", wadFile.tag);
	systemPrintDebugMessage("lump count: %d\n", wadFile.lumpCount);
	systemPrintDebugMessage("file handle: %d\n", wadFile.fileHandle);
	systemPrintDebugMessage("lumps:\n");
	FOREACH_LUMP(i,lumpHeader, {
		systemPrintDebugMessage("\t%8s: ", lumpHeader->name.value);
		systemPrintDebugMessage("pos: %d, size: %d, cached: %d\n", lumpHeader->positionInWadFile, lumpHeader->size, (lumpHeader->cachedContents != NULL));
	});
}

/**
 * See header file for information.
 */
int findWadFileLump(const char *name) {
	int index = findWadFileLumpSafe(name);
	if (index == -1) {
		systemFatalError("WAD file lump not found: %s", name);
	}
	return index;
}

/**
 * See header file for information.
 */
int findWadFileLumpSafe(const char *name) {
	int nameLength, i;
	struct DoomName internalName;
	
	nameLength = getLimitedStringLength(name, 8);
	memset(internalName.value, 0, 8);
	memcpy(internalName.value, name, nameLength);
	
	FOREACH_LUMP_BACKWARD(i,lumpHeader, {
		if (doomNamesEqual(&lumpHeader->name, &internalName)) {
			return i;
		}
	});
	return -1;
}

/**
 * See header file for information.
 */
int getWadFileLumpSize(int lumpIndex) {
	return LUMP_HEADER(lumpIndex).size;
}

/**
 * See header file for information.
 */
struct DoomName *getWadFileLumpName(int lumpIndex) {
	return &(LUMP_HEADER(lumpIndex).name);
}

/**
 * TODO: clean up and read whole WAD file at once
 */
static void readWadFileLump(int index, void *destination) {
	struct WadFileLump *header;
	int result;
	
	/** obtain the lump header **/
	if (index < 0 || index >= LUMP_COUNT) {
		systemFatalError("invalid wad file lump index: %d", index);
	}
	header = &(LUMP_HEADER(index));
	
	/** read the lump **/
	lseek(wadFile.fileHandle, header->positionInWadFile, SEEK_SET);
	result = read(wadFile.fileHandle, destination, header->size);
	
	/** check for errors **/
	if (result != header->size) {
		systemFatalError("error while reading WAD file lump %d", index);
	}

}

/**
 * See header file for information.
 */
void *getWadFileLumpContentsByIndex(int lumpIndex) {
	struct WadFileLump *header;
	
	/** obtain the lump header **/
	if (lumpIndex < 0 || lumpIndex >= LUMP_COUNT) {
		systemFatalError("invalid wad file lump index: %d", index);
	}
	header = &(LUMP_HEADER(lumpIndex));

	/** load the lump **/
	if (header->cachedContents == NULL) {
		zoneAllocatorAllocate(header->size, STATIC_ALLOCATION_TAG, &(header->cachedContents));
		readWadFileLump(lumpIndex, header->cachedContents);
	}

	/** return the cached lump contents **/
	return header->cachedContents;
	
}

/**
 * See header file for information.
 */
void *getWadFileLumpContentsByName(const char *name) {
	return getWadFileLumpContentsByIndex(findWadFileLump(name));
}
