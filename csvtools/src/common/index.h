/**
 * Index.h -- index file handling.
 */

#ifndef __INDEX_H__
#define __INDEX_H__

/**
 * Tries to consume the specified command-line option.
 * Returns 1 if the option was understood by index handling,
 * 0 if not understood.
 */
int consumeIndexCommandLineOption(const char *arg);

/**
 * Initializes index input from the specified filename.
 */
void initializeIndexInput(const char *filename);

/**
 * Initializes index output to the specified filename.
 */
void initializeIndexOutput(const char *filename);

/**
 * Writes an entry to the index output for the specified record offset.
 */
void writeIndexEntry(long recordOffset);

/**
 * Reads the index entry for the specified record and returns the
 * offset of the record.
 */
long readIndexEntry(long recordIndex);

/**
 * Finalizes the index handling subsystem.
 */
void finalizeIndexHandling();

#endif
