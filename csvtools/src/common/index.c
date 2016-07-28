
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "index.h"

// index handling modes
#define MODE_UNDEFINED 0
#define MODE_INPUT 1
#define MODE_OUTPUT 2

// index file formats
#define FORMAT_LONG 0
#define FORMAT_DECIMAL 2

/**
 * The index file pointer
 */
static FILE *fp = NULL;

/**
 * The index handling mode
 */
static int mode = MODE_UNDEFINED;

/**
 * The index file format
 */
static int format = FORMAT_LONG;

/**
 * Ensures that index handling has not been initialized yet.
 */
static void checkNotInitialized() {
	if (mode != MODE_UNDEFINED) {
		fprintf(stderr, "index handling already initialized");
		exit(1);
	}
}

/**
 * Ensures that the index file has not been closed yet.
 */
static void checkIndexFileNotYetClosed() {
	if (fp == NULL) {
		fprintf(stderr, "index file already closed");
		exit(1);
	}
}

/**
 * Ensures that the specified mode is in use.
 */
static void checkMode(int expectedMode) {
	if (mode != expectedMode) {
		fprintf(stderr, "invalid operation for the current index file mode");
		exit(1);
	}
}

/**
 * Public function -- see index.h
 */
int consumeIndexCommandLineOption(const char *arg) {
	if (strcmp(arg, "-d") == 0) {
		format = FORMAT_DECIMAL;
		return 1;
	}
	return 0;
}

/**
 * Public function -- see index.h
 */
void initializeIndexInput(const char *filename) {
	checkNotInitialized();
	if (format != FORMAT_LONG) {
		fprintf(stderr, "cannot handle index input formats other than 'long int' yet");
		exit(1);
	}
	mode = MODE_INPUT;
	fp = fopen(filename, "rb");
}

/**
 * Public function -- see index.h
 */
void initializeIndexOutput(const char *filename) {
	checkNotInitialized();
	mode = MODE_OUTPUT;
	fp = fopen(filename, "wb");
}

/**
 * Public function -- see index.h
 */
void writeIndexEntry(long recordOffset) {
	checkIndexFileNotYetClosed();
	checkMode(MODE_OUTPUT);
	fwrite(&recordOffset, sizeof(long), 1, fp);
}

/**
 * Public function -- see index.h
 */
long readIndexEntry(long recordIndex) {
	long result;
	checkIndexFileNotYetClosed();
	checkMode(MODE_INPUT);
	fseek(fp, recordIndex * sizeof(long), SEEK_SET);
	fread(&result, sizeof(long), 1, fp);
	return result;
}

/**
 * Public function -- see index.h
 */
void finalizeIndexHandling() {
	checkIndexFileNotYetClosed();
	fclose(fp);
	fp = NULL;
}

