/*
 *  ViewRasterizer.h
 *  baselib
 *
 *  Created by Martin on 9/4/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

/**
 * Draws a fragment of the current segment, as indicated by the render state.
 * The firstColumn and lastColumn specify the fragment to draw in screen columns.
 */
void drawSegmentFragment(int firstColumn, int lastColumn);
