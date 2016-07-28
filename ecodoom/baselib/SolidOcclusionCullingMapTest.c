/*
 *  SolidOcclusionCullingMapTest.c
 *  baselib
 *
 *  Created by Martin on 9/2/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

#include <stdio.h>
#include <stdarg.h>
#include "UnitTestSupport.h"
#include "SolidOcclusionCullingMap.c"

/*************************************************************************************/
/* fragment logging */
/*************************************************************************************/

int fragmentLogLength;
int fragmentStartLog[1024];
int fragmentEndLog[1024];

static void resetFragmentLog() {
	fragmentLogLength = 0;
}

static int checkFragmentLog(int expectedLength, ...) {
	va_list args;
	int i;
	
	if (fragmentLogLength != expectedLength) {
		return 0;
	}
	
	va_start(args, expectedLength);
	for (i=0; i<expectedLength; i++) {
		if (fragmentStartLog[i] != va_arg(args, int)) {
			return 0;
		}
		if (fragmentEndLog[i] != va_arg(args, int)) {
			return 0;
		}
	}
	va_end(args);
	
	return 1;
}

static void solidOcclusionMapFragmentHandler(int start, int end) {
	fragmentStartLog[fragmentLogLength] = start;
	fragmentEndLog[fragmentLogLength] = end;
	fragmentLogLength++;
}

static void dumpFragmentLog() {
	int i;
	int first = 1;
	
	printf("fragments: [");
	for (i=0; i<fragmentLogLength; i++) {
		if (first) {
			first = 0;
		} else {
			printf(", ");
		}
		printf("(%d .. %d)", fragmentStartLog[i], fragmentEndLog[i]);
	}
	printf("]");
}

/*************************************************************************************/
/* SOCM validation */
/*************************************************************************************/

static int checkSocm(int expectedLength, ...) {
	int currentPosition = -1;
	int i;
	va_list args;
	
	va_start(args, expectedLength);
	for (i=0; i<expectedLength; i++) {
		int nextPosition = entryToNextEntry[currentPosition];
		if (nextPosition > 321) {
			return 0;
		} else if (nextPosition != va_arg(args, int)) {
			return 0;
		} else if (nextPosition <= currentPosition) {
			return 0;
		} else {
			currentPosition = nextPosition;
		}
	}
	va_end(args);
	
	return (entryToNextEntry[currentPosition] == 321);
}

static void dumpSocm() {
	int currentPosition = -1;
	int first = 1;
	
	printf("SOCM: [");
	while (1) {
		int nextPosition = entryToNextEntry[currentPosition];
		if (nextPosition > 320) {
			break;
		}
		printf(first ? "%d" : ", %d", nextPosition);
		first = 0;
		currentPosition = nextPosition;
	}
	printf("]\n");
}

/*************************************************************************************/
/* meta-tests */
/*************************************************************************************/

/**
 * Meta-test: fragment logging.
 */
void testFragmentLog() {
	
	/** empty log **/
	fragmentLogLength = 0;
	assert(checkFragmentLog(0), "fragment log [] a");
	assert(!checkFragmentLog(1, 0, 0), "fragment log [] b");
	assert(!checkFragmentLog(1, 0, 1), "fragment log [] c");

	/** 1-element log **/
	fragmentLogLength = 1;
	fragmentStartLog[0] = 0;
	fragmentEndLog[0] = 5;
	assert(!checkFragmentLog(0), "fragment log [0, 5] a");
	assert(!checkFragmentLog(1, 0, 3), "fragment log [0, 5] b");
	assert(!checkFragmentLog(1, 1, 5), "fragment log [0, 5] c");
	assert(checkFragmentLog(1, 0, 5), "fragment log [0, 5] d");

	/** 2-element log **/
	fragmentLogLength = 2;
	fragmentStartLog[1] = 7;
	fragmentEndLog[1] = 11;
	assert(!checkFragmentLog(0), "fragment log [0, 5, 7, 11] a");
	assert(!checkFragmentLog(1, 0, 5), "fragment log [0, 5, 7, 11] b");
	assert(!checkFragmentLog(1, 7, 11), "fragment log [0, 5, 7, 11] c");
	assert(!checkFragmentLog(2, 7, 11, 0, 5), "fragment log [0, 5, 7, 11] d");
	assert(checkFragmentLog(2, 0, 5, 7, 11), "fragment log [0, 5, 7, 11] e");
	
}

/**
 * Meta-test: SOCM state checking
 */
void testSocmCheck() {
	
	/** freshly initialized **/
	clearSolidOcclusionCullingMap();
	assert(checkSocm(2, 0, 320), "check SOCM fresh a");
	assert(!checkSocm(1, 320), "check SOCM fresh b");
	assert(!checkSocm(1, 0), "check SOCM fresh c");
	assert(!checkSocm(3, 0, 5, 320), "check SOCM fresh d");
	assert(!checkSocm(3, 0, 320, 350), "check SOCM fresh e");

	/** this entry should be overlooked **/
	entryToNextEntry[5] = 320;
	assert(checkSocm(2, 0, 320), "check SOCM overlooked a");
	assert(!checkSocm(3, 0, 5, 320), "check SOCM overlooked b");

	/** with some more entries **/
	clearSolidOcclusionCullingMap();
	entryToNextEntry[0] = 10;
	entryToNextEntry[10] = 20;
	entryToNextEntry[20] = 320;
	assert(checkSocm(4, 0, 10, 20, 320), "check SOCM with wall a");
	
	/** with a self-referring entry (not allowed) **/
	clearSolidOcclusionCullingMap();
	entryToNextEntry[0] = 0;
	assert(!checkSocm(2, 0, 320), "check SOCM with self-referring a");
	
	/** with a backwards-referring entry (not allowed) **/
	clearSolidOcclusionCullingMap();
	entryToNextEntry[0] = 20;
	entryToNextEntry[20] = 10;
	entryToNextEntry[10] = 320;
	assert(!checkSocm(4, 0, 10, 20, 320), "check SOCM with backwards reference a");
	assert(!checkSocm(4, 0, 20, 10, 320), "check SOCM with backwards reference b");
	
}

/*************************************************************************************/
/* actual tests */
/*************************************************************************************/

static const char *assertionMessageFormat;

static void testEmptyAtBorder(int segmentStart, int segmentEnd, int clippedStart, int clippedEnd, int emptyStart, int emptyEnd) {

	clearSolidOcclusionCullingMap();
	resetFragmentLog();
	invokeSolidOcclusionCullingMap(segmentStart, segmentEnd, &solidOcclusionMapFragmentHandler, 0);
	assert(checkFragmentLog(1, clippedStart, clippedEnd), assertionMessageFormat, "(non-solid, fragment log)");
	assert(checkSocm(2, 0, 320), assertionMessageFormat, "(non-solid, SOCM)");
	
	clearSolidOcclusionCullingMap();
	resetFragmentLog();
	invokeSolidOcclusionCullingMap(segmentStart, segmentEnd, &solidOcclusionMapFragmentHandler, 1);
	assert(checkFragmentLog(1, clippedStart, clippedEnd), assertionMessageFormat, "(solid, fragment log)");
	if (emptyStart == -1) {
		assert(checkSocm(0), assertionMessageFormat, "(solid, SOCM)");
	} else {
		assert(checkSocm(2, emptyStart, emptyEnd), assertionMessageFormat, "(solid, SOCM)");
	}
	
}

void testEmpty() {

	/** totally off to the left **/

	clearSolidOcclusionCullingMap();
	resetFragmentLog();
	invokeSolidOcclusionCullingMap(-50, -40, &solidOcclusionMapFragmentHandler, 0);
	assert(checkFragmentLog(0), "totally off to the left, non-solid, a");
	assert(checkSocm(2, 0, 320), "totally off to the left, non-solid, b");
	
	clearSolidOcclusionCullingMap();
	resetFragmentLog();
	invokeSolidOcclusionCullingMap(-50, -40, &solidOcclusionMapFragmentHandler, 1);
	assert(checkFragmentLog(0), "totally off to the left, solid, a");
	assert(checkSocm(2, 0, 320), "totally off to the left, solid, b");
	
	/** totally off to the right **/
	
	clearSolidOcclusionCullingMap();
	resetFragmentLog();
	invokeSolidOcclusionCullingMap(350, 380, &solidOcclusionMapFragmentHandler, 0);
	assert(checkFragmentLog(0), "totally off to the right, non-solid, a");
	assert(checkSocm(2, 0, 320), "totally off to the right, non-solid, b");
	
	clearSolidOcclusionCullingMap();
	resetFragmentLog();
	invokeSolidOcclusionCullingMap(350, 380, &solidOcclusionMapFragmentHandler, 1);
	assert(checkFragmentLog(0), "totally off to the right, solid, a");
	assert(checkSocm(2, 0, 320), "totally off to the right, solid, b");
	
	/** middle of empty **/
	
	clearSolidOcclusionCullingMap();
	resetFragmentLog();
	invokeSolidOcclusionCullingMap(100, 199, &solidOcclusionMapFragmentHandler, 0);
	assert(checkFragmentLog(1, 100, 199), "test non-solid middle of empty, a");
	assert(checkSocm(2, 0, 320), "test non-solid middle of empty, b");
	
	clearSolidOcclusionCullingMap();
	resetFragmentLog();
	invokeSolidOcclusionCullingMap(100, 199, &solidOcclusionMapFragmentHandler, 1);
	assert(checkFragmentLog(1, 100, 199), "test solid middle of empty, a");
	assert(checkSocm(4, 0, 100, 200, 320), "test solid middle of empty, b");

	/** middle of empty with reversed endpoints **/
	
	clearSolidOcclusionCullingMap();
	resetFragmentLog();
	invokeSolidOcclusionCullingMap(199, 100, &solidOcclusionMapFragmentHandler, 0);
	assert(checkFragmentLog(1, 100, 199), "test non-solid middle of empty with reversed endpoints, a");
	assert(checkSocm(2, 0, 320), "test non-solid middle of empty with reversed endpoints, b");
	
	clearSolidOcclusionCullingMap();
	resetFragmentLog();
	invokeSolidOcclusionCullingMap(199, 100, &solidOcclusionMapFragmentHandler, 1);
	assert(checkFragmentLog(1, 100, 199), "test solid middle of empty with reversed endpoints, a");
	assert(checkSocm(4, 0, 100, 200, 320), "test solid middle of empty with reversed endpoints, b");
	
	/** aligned with one screen border **/

	assertionMessageFormat = "test empty, aligned with left screen border, %s";
	testEmptyAtBorder(0, 99, 0, 99, 100, 320);

	assertionMessageFormat = "test empty, aligned with right screen border, %s";
	testEmptyAtBorder(100, 319, 100, 319, 0, 100);

	/** overlapping one screen border **/

	assertionMessageFormat = "test empty, overlapping left screen border, %s";
	testEmptyAtBorder(-10, 99, 0, 99, 100, 320);
	
	assertionMessageFormat = "test empty, overlapping right screen border, %s";
	testEmptyAtBorder(100, 350, 100, 319, 0, 100);

	/** both screen borders affected **/

	assertionMessageFormat = "test empty, aligned with both screen borders, %s";
	testEmptyAtBorder(0, 319, 0, 319, -1, -1);

	assertionMessageFormat = "test empty, overlapping both screen borders, %s";
	testEmptyAtBorder(-10, 350, 0, 319, -1, -1);

	assertionMessageFormat = "test empty, aligned with left screen border, overlapping right screen border, %s";
	testEmptyAtBorder(0, 350, 0, 319, -1, -1);

	assertionMessageFormat = "test empty, aligned with right screen border, overlapping left screen border, %s";
	testEmptyAtBorder(-10, 319, 0, 319, -1, -1);

}

void prepareTestFragmented() {
	clearSolidOcclusionCullingMap();
	invokeSolidOcclusionCullingMap(100, 149, solidOcclusionMapFragmentHandler, 1);
	invokeSolidOcclusionCullingMap(200, 249, solidOcclusionMapFragmentHandler, 1);
	resetFragmentLog();
}

void preparetTestHidden() {
	clearSolidOcclusionCullingMap();
	invokeSolidOcclusionCullingMap(-50, 99, solidOcclusionMapFragmentHandler, 1);
	invokeSolidOcclusionCullingMap(200, 299, solidOcclusionMapFragmentHandler, 1);
	resetFragmentLog();
}

void testFragmented() {
	
	/** test a segment whose right half gets clipped off by a solid wall (not the screen border) **/

	prepareTestFragmented();
	invokeSolidOcclusionCullingMap(50, 110, &solidOcclusionMapFragmentHandler, 0);
	assert(checkFragmentLog(1, 50, 99), "test fragmented right half off, non-solid, fragment log");
	assert(checkSocm(6, 0, 100, 150, 200, 250, 320), "test fragmented right half off, non-solid, SOCM");

	prepareTestFragmented();
	invokeSolidOcclusionCullingMap(50, 110, &solidOcclusionMapFragmentHandler, 1);
	assert(checkFragmentLog(1, 50, 99), "test fragmented right half off, solid, fragment log");
	assert(checkSocm(6, 0, 50, 150, 200, 250, 320), "test fragmented right half off, solid, SOCM");

	/** test a segment whose left half gets clipped off by a solid wall (not the screen border) **/
	
	prepareTestFragmented();
	invokeSolidOcclusionCullingMap(120, 169, &solidOcclusionMapFragmentHandler, 0);
	assert(checkFragmentLog(1, 150, 169), "test fragmented left half off, non-solid, fragment log");
	assert(checkSocm(6, 0, 100, 150, 200, 250, 320), "test fragmented left half off, non-solid, SOCM");

	prepareTestFragmented();
	invokeSolidOcclusionCullingMap(120, 169, &solidOcclusionMapFragmentHandler, 1);
	assert(checkFragmentLog(1, 150, 169), "test fragmented left half off, solid, fragment log");
	assert(checkSocm(6, 0, 100, 170, 200, 250, 320), "test fragmented left half off, solid, SOCM");

	/** test a segment whose ends get clipped off by solid walls (not the screen border) **/

	prepareTestFragmented();
	invokeSolidOcclusionCullingMap(120, 219, &solidOcclusionMapFragmentHandler, 0);
	assert(checkFragmentLog(1, 150, 199), "test fragmented ends off, non-solid, fragment log");
	assert(checkSocm(6, 0, 100, 150, 200, 250, 320), "test fragmented ends off, non-solid, SOCM");
	
	prepareTestFragmented();
	invokeSolidOcclusionCullingMap(120, 219, &solidOcclusionMapFragmentHandler, 1);
	assert(checkFragmentLog(1, 150, 199), "test fragmented ends off, solid, fragment log");
	assert(checkSocm(4, 0, 100, 250, 320), "test fragmented ends off, solid, SOCM");

	/** test a segment whose middle part gets clipped off by a solid wall **/
	
	prepareTestFragmented();
	invokeSolidOcclusionCullingMap(70, 169, &solidOcclusionMapFragmentHandler, 0);
	assert(checkFragmentLog(2, 70, 99, 150, 169), "test fragmented middle part off, non-solid, fragment log");
	assert(checkSocm(6, 0, 100, 150, 200, 250, 320), "test fragmented middle part off, non-solid, SOCM");

	prepareTestFragmented();
	invokeSolidOcclusionCullingMap(70, 169, &solidOcclusionMapFragmentHandler, 1);
	assert(checkFragmentLog(2, 70, 99, 150, 169), "test fragmented middle part off, solid, fragment log");
	assert(checkSocm(6, 0, 70, 170, 200, 250, 320), "test fragmented middle part off, solid, SOCM");

	/** test a segment whose left end and some middle area are visible **/

	prepareTestFragmented();
	invokeSolidOcclusionCullingMap(70, 219, &solidOcclusionMapFragmentHandler, 0);
	assert(checkFragmentLog(2, 70, 99, 150, 199), "test fragmented right/middle part off, non-solid, fragment log");
	assert(checkSocm(6, 0, 100, 150, 200, 250, 320), "test fragmented right/middle part off, non-solid, SOCM");
	
	prepareTestFragmented();
	invokeSolidOcclusionCullingMap(70, 219, &solidOcclusionMapFragmentHandler, 1);
	assert(checkFragmentLog(2, 70, 99, 150, 199), "test fragmented right/middle part off, solid, fragment log");
	assert(checkSocm(4, 0, 70, 250, 320), "test fragmented right/middle part off, solid, SOCM");
	
	/** test complete filling of all gaps by one large segment **/

	prepareTestFragmented();
	invokeSolidOcclusionCullingMap(-50, 350, &solidOcclusionMapFragmentHandler, 0);
	assert(checkFragmentLog(3, 0, 99, 150, 199, 250, 319), "test fill gaps, non-solid, fragment log");
	assert(checkSocm(6, 0, 100, 150, 200, 250, 320), "test fill gaps, non-solid, SOCM");
	
	prepareTestFragmented();
	invokeSolidOcclusionCullingMap(-50, 350, &solidOcclusionMapFragmentHandler, 1);
	assert(checkFragmentLog(3, 0, 99, 150, 199, 250, 319), "test fill gaps, solid, fragment log");
	assert(checkSocm(0), "test fill gaps, solid, SOCM");

	/************************************************************************************************/

	/** test a fragment that is totally hidden by the first entry **/

	preparetTestHidden();
	invokeSolidOcclusionCullingMap(30, 59, &solidOcclusionMapFragmentHandler, 0);
	assert(checkFragmentLog(0), "test hidden by first entry, non-solid, fragment log");
	assert(checkSocm(4, 100, 200, 300, 320), "test hidden by first entry, non-solid, SOCM");
	
	preparetTestHidden();
	invokeSolidOcclusionCullingMap(30, 59, &solidOcclusionMapFragmentHandler, 1);
	assert(checkFragmentLog(0), "test hidden by first entry, solid, fragment log");
	assert(checkSocm(4, 100, 200, 300, 320), "test hidden by first entry, solid, SOCM");

	/** test a fragment that is totally hidden by the first entry and reaches beyond the left screen border **/
	
	preparetTestHidden();
	invokeSolidOcclusionCullingMap(-30, 59, &solidOcclusionMapFragmentHandler, 0);
	assert(checkFragmentLog(0), "test hidden by first entry and beyond left border, non-solid, fragment log");
	assert(checkSocm(4, 100, 200, 300, 320), "test hidden by first entry, non-solid, SOCM");
	
	preparetTestHidden();
	invokeSolidOcclusionCullingMap(-30, 59, &solidOcclusionMapFragmentHandler, 1);
	assert(checkFragmentLog(0), "test hidden by first entry and beyond left border, solid, fragment log");
	assert(checkSocm(4, 100, 200, 300, 320), "test hidden by first entry, solid, SOCM");
	
	/** test a fragment that is totally hidden by the second entry **/
	
	preparetTestHidden();
	invokeSolidOcclusionCullingMap(230, 259, &solidOcclusionMapFragmentHandler, 0);
	assert(checkFragmentLog(0), "test hidden by second entry, non-solid, fragment log");
	assert(checkSocm(4, 100, 200, 300, 320), "test hidden by second entry, non-solid, SOCM");
	
	preparetTestHidden();
	invokeSolidOcclusionCullingMap(230, 259, &solidOcclusionMapFragmentHandler, 1);
	assert(checkFragmentLog(0), "test hidden by second entry, solid, fragment log");
	assert(checkSocm(4, 100, 200, 300, 320), "test hidden by second entry, solid, SOCM");
	
}

void testAlignedFragmentBorders() {
	
	/** test right border aligned with left border of solid entry **/
	
	prepareTestFragmented();
	invokeSolidOcclusionCullingMap(70, 99, &solidOcclusionMapFragmentHandler, 0);
	assert(checkFragmentLog(1, 70, 99), "test right-to-left aligned, non-solid, fragment log");
	assert(checkSocm(6, 0, 100, 150, 200, 250, 320), "test right-to-left aligned, non-solid, SOCM");
	
	prepareTestFragmented();
	invokeSolidOcclusionCullingMap(70, 99, &solidOcclusionMapFragmentHandler, 1);
	assert(checkFragmentLog(1, 70, 99), "test right-to-left aligned, solid, fragment log");
	assert(checkSocm(6, 0, 70, 150, 200, 250, 320), "test right-to-left aligned, solid, SOCM");

	/** test right border misaligned (by 1 pixel) with left border of solid entry **/
	
	prepareTestFragmented();
	invokeSolidOcclusionCullingMap(70, 99, &solidOcclusionMapFragmentHandler, 0);
	assert(checkFragmentLog(1, 70, 99), "test right-to-left misaligned, non-solid, fragment log");
	assert(checkSocm(6, 0, 100, 150, 200, 250, 320), "test right-to-left misaligned, non-solid, SOCM");
	
	prepareTestFragmented();
	invokeSolidOcclusionCullingMap(70, 98, &solidOcclusionMapFragmentHandler, 1);
	assert(checkFragmentLog(1, 70, 98), "test right-to-left misaligned, solid, fragment log");
	assert(checkSocm(8, 0, 70, 99, 100, 150, 200, 250, 320), "test right-to-left misaligned, solid, SOCM");

	/** test right border aligned with right border of solid entry **/
	
	prepareTestFragmented();
	invokeSolidOcclusionCullingMap(70, 149, &solidOcclusionMapFragmentHandler, 0);
	assert(checkFragmentLog(1, 70, 99), "test right-to-right aligned, non-solid, fragment log");
	assert(checkSocm(6, 0, 100, 150, 200, 250, 320), "test right-to-right aligned, non-solid, SOCM");
	
	prepareTestFragmented();
	invokeSolidOcclusionCullingMap(70, 149, &solidOcclusionMapFragmentHandler, 1);
	assert(checkFragmentLog(1, 70, 99), "test right-to-right aligned, solid, fragment log");
	assert(checkSocm(6, 0, 70, 150, 200, 250, 320), "test right-to-right aligned, solid, SOCM");
	
	/** test right border misaligned (by 1 pixel) with right border of solid entry **/
	
	prepareTestFragmented();
	invokeSolidOcclusionCullingMap(70, 150, &solidOcclusionMapFragmentHandler, 0);
	assert(checkFragmentLog(2, 70, 99, 150, 150), "test right-to-right misaligned, non-solid, fragment log");
	assert(checkSocm(6, 0, 100, 150, 200, 250, 320), "test right-to-right misaligned, non-solid, SOCM");
	
	prepareTestFragmented();
	invokeSolidOcclusionCullingMap(70, 150, &solidOcclusionMapFragmentHandler, 1);
	assert(checkFragmentLog(2, 70, 99, 150, 150), "test right-to-right misaligned, solid, fragment log");
	assert(checkSocm(6, 0, 70, 151, 200, 250, 320), "test right-to-right misaligned, solid, SOCM");

	/************************************************************************************************/
	
	/** test left border aligned with left border of solid entry **/
	
	prepareTestFragmented();
	invokeSolidOcclusionCullingMap(100, 169, &solidOcclusionMapFragmentHandler, 0);
	assert(checkFragmentLog(1, 150, 169), "test left-to-left aligned, non-solid, fragment log");
	assert(checkSocm(6, 0, 100, 150, 200, 250, 320), "test left-to-left aligned, non-solid, SOCM");
	
	prepareTestFragmented();
	invokeSolidOcclusionCullingMap(100, 169, &solidOcclusionMapFragmentHandler, 1);
	assert(checkFragmentLog(1, 150, 169), "test left-to-left aligned, solid, fragment log");
	assert(checkSocm(6, 0, 100, 170, 200, 250, 320), "test left-to-left aligned, solid, SOCM");
	
	/** test left border misaligned (by 1 pixel) with left border of solid entry **/
	
	prepareTestFragmented();
	invokeSolidOcclusionCullingMap(99, 169, &solidOcclusionMapFragmentHandler, 0);
	assert(checkFragmentLog(2, 99, 99, 150, 169), "test left-to-left misaligned, non-solid, fragment log");
	assert(checkSocm(6, 0, 100, 150, 200, 250, 320), "test left-to-left aligned, non-solid, SOCM");
	
	prepareTestFragmented();
	invokeSolidOcclusionCullingMap(99, 169, &solidOcclusionMapFragmentHandler, 1);
	assert(checkFragmentLog(2, 99, 99, 150, 169), "test left-to-left misaligned, solid, fragment log");
	assert(checkSocm(6, 0, 99, 170, 200, 250, 320), "test left-to-left misaligned, solid, SOCM");
	
	/** test left border aligned with right border of solid entry **/
	
	prepareTestFragmented();
	invokeSolidOcclusionCullingMap(150, 169, &solidOcclusionMapFragmentHandler, 0);
	assert(checkFragmentLog(1, 150, 169), "test left-to-right aligned, non-solid, fragment log");
	assert(checkSocm(6, 0, 100, 150, 200, 250, 320), "test left-to-left aligned, non-solid, SOCM");
	
	prepareTestFragmented();
	invokeSolidOcclusionCullingMap(150, 169, &solidOcclusionMapFragmentHandler, 1);
	assert(checkFragmentLog(1, 150, 169), "test left-to-right aligned, solid, fragment log");
	assert(checkSocm(6, 0, 100, 170, 200, 250, 320), "test left-to-right aligned, solid, SOCM");
	
	/** test left border misaligned (by 1 pixel) with right border of solid entry **/
	
	prepareTestFragmented();
	invokeSolidOcclusionCullingMap(151, 169, &solidOcclusionMapFragmentHandler, 0);
	assert(checkFragmentLog(1, 151, 169), "test left-to-right misaligned, non-solid, fragment log");
	assert(checkSocm(6, 0, 100, 150, 200, 250, 320), "test left-to-left aligned, non-solid, SOCM");
	
	prepareTestFragmented();
	invokeSolidOcclusionCullingMap(151, 169, &solidOcclusionMapFragmentHandler, 1);
	assert(checkFragmentLog(1, 151, 169), "test left-to-right misaligned, solid, fragment log");
	assert(checkSocm(8, 0, 100, 150, 151, 170, 200, 250, 320), "test left-to-right misaligned, solid, SOCM");

}

/*************************************************************************************/
/* main */
/*************************************************************************************/

static int dummy;

int main() {
	
	/** prevent warnings about unused functions -- we will need them when the tests fail to find the problem **/
	dummy = (int)&dumpFragmentLog;
	dummy = (int)&dumpSocm;
	
	/** meta-tests **/
	testFragmentLog();
	testSocmCheck();
	
	/** tests that start with an empty SOCM **/
	testEmpty();
	
	/** tests that start with a fragmented SOCM **/
	testFragmented();
	
	/** test that test border-aligned and similar cases **/
	testAlignedFragmentBorders();
	
	return 0;
}

