/*
 *  SolidOcclusionCullingMap.c
 *  baselib
 *
 *  Created by Martin on 8/29/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

#include "Common.h"
#include "SystemDebug.h"
#include "SolidOcclusionCullingMap.h"

/**
 * This is the underlying buffer used for the main SOCM data structure.
 * it ensures that array indices from -1, inclusive, through 320, inclusive,
 * are valid.
 */
static unsigned short socmBuffer[322];

/**
 * The main SOCM data structure, which is an array with valid indices ranging from -1, inclusive,
 * through 320, inclusive. This structure indicates which screen columns are blocked by "solid"
 * (one-sided) walls. Since segments are traversed front-to-back, once a solid segment has been
 * visited, no further segments can appear at the screen columns occupied by that segment. This
 * can be used for quick and simple occlusion culling and clipping.
 *
 * This structure can only be used in a sequential way, not in a random-access way. It stores a
 * list of entries, each of which covers a range of screen columns. Consecutive entries fill up
 * the screen from left to right. Entries are solid and empty in an alternating way, with the
 * first and last entry always being solid. The entries actually cover one excess column at
 * each side, with the initial setting being a solid 1-pixel column at each screen border and 320
 * empty columns in between. Added segments may turn empty columns into solid ones, but never
 * back to empty, so the borders will stay solid.
 *
 * The simplify indexing, the left screen border is located at position -1, so normal colunm
 * indices retain their meaning in this structure. The right border is located at index 320.
 *
 * The entry list is kept by storing at the beginning column index of each entry the beginning
 * column index of the next entry. The structure can be read by reading at index -1, taking that
 * value as the next index to read, taking that value as the next index to read, and so on.
 * The last entry must point to column 321, and each entry must point forward by at
 * least 1 column. Once index 321 is read, the whole structure has been traversed,
 * including the screen borders.
 *
 * Array entries that are skipped by this traversal are ignored and do not have
 * any meaning.
 */
static unsigned short *entryToNextEntry = socmBuffer + 1;

/**
 * See header file for information.
 */
void clearSolidOcclusionCullingMap() {
	entryToNextEntry[-1] = 0;
	entryToNextEntry[0] = 320;
	entryToNextEntry[320] = 321;
}

/**
 * See header file for information.
 *
 * This function handles everything in a single loop to
 * make testing and debugging easier.
 */
void invokeSolidOcclusionCullingMap(int firstSegmentColumn, int lastSegmentColumn, SolidOcclusionMapFragmentHandler handler, int segmentIsSolid) {

	/** fast-skip position -1 so we don't have to handle the "no predecessor" case **/
	int previousPosition;
	int currentPosition;
	int currentEntryIsSolid;
	int segmentStopColumn;
	
	/** swap the endpoints if in reversed order **/
	if (lastSegmentColumn < firstSegmentColumn) {
		int temp = firstSegmentColumn;
		firstSegmentColumn = lastSegmentColumn;
		lastSegmentColumn = temp;
	}
	
	/** check if totally outside the screen **/
	if (lastSegmentColumn < 0 || firstSegmentColumn > 319) {
		return;
	}
	
	/** clip the segment against screen borders **/
	if (firstSegmentColumn < 0) {
		firstSegmentColumn = 0;
	}
	if (lastSegmentColumn > 319) {
		lastSegmentColumn = 319;
	}
	
	/** fast-skip position -1 so we don't have to handle the "no predecessor" case **/
	previousPosition = -1;
	currentPosition = entryToNextEntry[-1];
	currentEntryIsSolid = 0;
	segmentStopColumn = lastSegmentColumn + 1;
	
	/** fast-skipping the first entry means we might have to clip the segment already **/
	if (firstSegmentColumn < currentPosition) {
		firstSegmentColumn = currentPosition;
		/** that might have killed the whole segment **/
		if (firstSegmentColumn > lastSegmentColumn) {
			return;
		}
	}

	/** stop as soon as we skip the right screen border **/
	while (currentPosition < 321) {
		
		/** determine the next position **/
		int nextPosition = entryToNextEntry[currentPosition];

		/**
		 * Entries that do not contain (part of) the segment are not interesting. Note that we only
		 * check against the start of the segment since we will simply stop the main loop when
		 * its end is reached.
		 *
		 * Further note that we push the firstSegmentColumn variable forward as soon as we reach it,
		 * so it will not be less than the entry start at this point. In other words, we clip left parts
		 * off the segment to prevent it from extending to the left of the current position.
		 */
		if (firstSegmentColumn < nextPosition) {

			/** We ignore parts of the segment that are hidden behind solid entries. **/
			if (!currentEntryIsSolid) {
			
				/**
				 * Handle the current fragment. The -1 modifier in the second argument handles the fact
				 * that the segment handler expects the last covered column, but this algorithm uses
				 * the "stop" column (first non-occupied column).
				 */
				handler(firstSegmentColumn, ((segmentStopColumn < nextPosition) ? segmentStopColumn : nextPosition) - 1);
			
				/** if the segment is solid, we insert its fragment into the SOCM **/
				if (segmentIsSolid) {
				
					/**
					 * Posibly merge with next SOCM entry. The mergedSolidFragmentEnd will contain the next entry
					 * to point to for the SOCM entry that covers the current fragment -- whether that
					 * will be the previous entry (merged) or a newly created entry (not merged, free space
					 * gets split).
					 */
					int mergedSolidFragmentEnd;
					if (segmentStopColumn < nextPosition) {
						/** do not merge -- there is a gap **/
						mergedSolidFragmentEnd = segmentStopColumn;
						entryToNextEntry[segmentStopColumn] = nextPosition;
					} else {
						/** Merge with next entry. **/
						mergedSolidFragmentEnd = entryToNextEntry[nextPosition];
					}
					
					/** possibly merge with previous SOCM entry, then move to the beginning of the entry that covers the current fragment **/
					if (firstSegmentColumn > currentPosition) {
						/** do not merge -- there is a gap **/
						entryToNextEntry[currentPosition] = firstSegmentColumn;
						entryToNextEntry[firstSegmentColumn] = mergedSolidFragmentEnd;
						currentPosition = firstSegmentColumn;
					} else {
						/** merge with previous entry **/
						entryToNextEntry[previousPosition] = mergedSolidFragmentEnd;
						currentPosition = previousPosition;
					}
					currentEntryIsSolid = 1;
					nextPosition = mergedSolidFragmentEnd;
				
				}
			
			}
			
			/** clip off that part of the segment which we just handled **/
			firstSegmentColumn = nextPosition;
			
		}
		
		/** check if the end of the segment is reached **/
		if (lastSegmentColumn < nextPosition) {
			break;
		}
		
		/** go to the next position **/
		previousPosition = currentPosition;
		currentPosition = nextPosition;
		currentEntryIsSolid = !currentEntryIsSolid;
		
		/** debug check **/
		if (previousPosition >= currentPosition) {
			systemFatalError("SOCM: moving from position %d to %d", previousPosition, currentPosition);
		}
		
	}

	/** debug check **/
	if (currentPosition > 321) {
		systemFatalError("SOCM: moving beyond position 321: %d", currentPosition);
	}
	
}
