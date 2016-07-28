/*
 *  shapes.h
 *  Blocks-in-a-Row
 *
 *  Created by Martin Geisse on 30.11.07.
 *  Copyright 2007 __MyCompanyName__. All rights reserved.
 *
 */

/* Number of pieces: 7  (O,  I,  T,  L,  J,  S,  Z)
 * Number of shapes: 19 (1 + 2 + 4 + 4 + 4 + 2 + 2)
 *
 * Piece index:
 * 0 - O
 * 1 - I
 * 2 - T
 * 3 - L
 * 4 - J
 * 5 - S
 * 6 - Z
 *
 * Shape index:
 *  0 - O
 *  1 - lying I
 *  2 - standing I
 *  3 - regular (down-pointing) T
 *  4 - left-pointing T
 *  5 - up-pointing T
 *  6 - right-pointing T
 *  7 - regular L
 *  8 - "feet-up" L
 *  9 - upside-down L
 * 10 - "feet-down" L
 * 11 - regular J
 * 12 - "feet-up" J
 * 13 - upside-down J
 * 14 - "feet-down" J
 * 15 - regular S
 * 16 - rotated S
 * 17 - regular Z
 * 18 - rotated Z
 */
#define NUM_PIECES	7
#define NUM_SHAPES	19

/* This array maps piece indices to their "normal" shape index. This is,
 * for each piece, the shape to be displayed in the "next piece" display,
 * and also the initial shape to appear when the piece drops into the
 * game area.
 */
extern int normalShapeByPiece [7];

/* This array maps shape indices to their piece index.
 */
extern int pieceByShape [19];

// This array maps shapes to their corresponding clockwise rotated shape
extern int shapeRotatedClockwise [19];

// This array maps shapes to their corresponding counter-clockwise rotated shape
extern int shapeRotatedCounterClockwise [19];

/* This array defines which tiles are occupied by which shape. The size
 * of an occupation matrix is 4x4. The element type is char, with 0
 * meaning 'free' and 1 meaning 'occupied'.
 */
typedef unsigned char SHAPE_OCCUPATION_MATRIX[16];
extern SHAPE_OCCUPATION_MATRIX shapeOccupationMatrices [19];
