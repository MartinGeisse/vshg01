/*
 *  SolidOcclusionCullingMap.h
 *  baselib
 *
 *  Created by Martin on 8/29/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

/**
 * The callback function type used by the SOCM.
 */
typedef void (*SolidOcclusionMapFragmentHandler)(int firstFragmentColumn, int lastFragmentColumn);

/**
 * Resets the SOCM to empty.
 */
void clearSolidOcclusionCullingMap();

/**
 * Handles a segment with the SOCM. This will first cull/clip
 * the segment against the SOCM. For each range of remaining
 * screen columns of the segment, the specified callback is
 * called. If the segment is solid, it is inserted into the
 * SOCM and affects subsequent segments.
 *
 * This function can handle the case that firstSegmentColumn and
 * lastSegmentColumn are in reversed order. It will simply swap them.
 * The fragment handler is always called with firstFragmentColumn being
 * less or equal to lastFragmentColumn.
 */
void invokeSolidOcclusionCullingMap(int firstSegmentColumn, int lastSegmentColumn, SolidOcclusionMapFragmentHandler handler, int segmentIsSolid);
