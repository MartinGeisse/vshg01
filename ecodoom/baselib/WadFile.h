/*
 *  WadFile.h
 *  baselib
 *
 *  Created by Martin on 7/29/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

/**
 * Initializes the WAD file handler.
 */
void initializeWadFile(const char *filename);

/**
 * Closes the WAD file and releases all resources.
 */
void shutdownWadFile();

/**
 * Dumps information about the initialized WAD file.
 */
void dumpWadFile();

/**
 * Finds a WAD file lump with the specified name. If no such lump exists,
 * a fatal error is signalled. If multiple lumps with the specified
 * name exist, the first one is found. Returns the lump index of the
 * lump.
 *
 * The argument should be at most eight characters long (the name length
 * limitation for lumps). If the argument is longer than 8 characters,
 * then any additional characters are ignored as if the length was 8.
 */
int findWadFileLump(const char *name);

/**
 * Like findWadFileLump(name), but does not signal a fatal error if the lump
 * cannot be found. Instead, this function returns -1 if not found.
 */
int findWadFileLumpSafe(const char *name);

/**
 * Returns the size of the lump at the specified index.
 */
int getWadFileLumpSize(int lumpIndex);

/**
 * Returns a pointer to the name of the lump at the specified index.
 */
struct DoomName *getWadFileLumpName(int lumpIndex);

/**
 * Returns the lump at the specified index from the WAD file. The returned pointer
 * points into a buffer allocated for the whole WAD file at startup and must
 * therefore be treated read-only.
 */
void *getWadFileLumpContentsByIndex(int lumpIndex);

/**
 * This function acts like getWadFileLumpContentsByIndex(), but finds the lump by name.
 * An error occurs if the lump cannot be found.
 */
void *getWadFileLumpContentsByName(const char *name);
