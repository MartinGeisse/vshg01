/*
 *  draw.h
 *  Blocks-in-a-Row
 *
 *  Created by Martin Geisse on 30.11.07.
 *  Copyright 2007 __MyCompanyName__. All rights reserved.
 *
 */

// offet of the game area in screen coordinates
#define GAME_AREA_X_ON_SCREEN	6
#define GAME_AREA_Y_ON_SCREEN	5

// offet of the (leftmost) preview box screen coordinates
#define PREVIEW_X_ON_SCREEN		20
#define PREVIEW_Y_ON_SCREEN		7

// x offset of one preview box to the next
#define PREVIEW_X_DELTA			5

// draw the screen background (i.e. anything that doesn't change during the game)
void drawBackground (void);

// draw the title screen
void drawTitleScreen (void);

/* These drawing functions draw a shape using characters. The shapeIndex selects
 * one of 19 shapes as defined in the "shapes" module. All character positions
 * which are occupied by the shape are overdrawn with the c character; all other
 * positions are left alone.
 */

/* Draw a shape using absolute screen coordinates, clipped against the
 * specified clipping rectangle.
 */
void drawClippedShapeOnScreen (int x, int y, int shapeIndex, char c, int minx, int miny, int maxx, int maxy);

/* Draw a shape using absolute screen coordinates and clipped against
 * the screen borders.
 */
void drawShapeOnScreen (int x, int y, int shapeIndex, char c);

/* Draw a shape using game-area coordinates. The shape is automatically clipped
 * against the borders of the game area.
 */
void drawShapeOnGameArea (int x, int y, int shapeIndex, char c);

/* Draw a shape in one of the preview boxes. The preview index must be one of 0, 1, 2.
 * NOTE: Only the preview shapes may be used, otherwise the shape may not fit into
 * the preview box.
 */
void drawShapeInPreview (int previewIndex, int shapeIndex, char c);

/* Draw the preview shape of the specified piece in one of the preview boxes.
 * The preview index must be one of 0, 1, 2.
 */
void drawPieceInPreview (int previewIndex, int pieceIndex, char c);

/* Fill a row in the game area with the specified character
 */
void fillGameRow (int y, char c);

/* Fill multiple rows in the game area with the specified character
 */
void fillGameRows (int count, int *rows, char c);

/* Copy character data to the game area. The data array must be of size 10*20.
 */
void drawGameArea (unsigned char *data);

/* Draw a string of characters to screen. The string may not cross screen borders;
 * this is not checked though. Note that the string is specified in the special
 * machine character set, not in ASCII characters.
 */
void drawString (int x, int y, int length, unsigned char *characters);

/* Draw an ASCII character to screen. The specified position must be inside the
 * screen borders; this is not checked though.
 * Note that only certain characters are allowed:
 *   - uppercase letters (none else so far)
 *   - space
 *   - colon, exclamation mark, hyphen/minus
 */
void drawASCIICharacter (int x, int y, char c);

/* Draw a string of ASCII characters to screen. The string may not cross screen borders;
 * this is not checked though. The string must be 0-terminated. Note that only
 * certain characters are allowed, as specified in drawASCIICharacter.
 */
void drawASCIIString (int x, int y, char *s);

/* Draw a string of ASCII characters to the center of the screen.
 * The string must not cross screen borders; this is not checked though.
 * The string must be 0-terminated. Note that only certain characters are
 * allowed, as specified in drawASCIICharacter.
 */
void drawCenteredASCIIString (int y, char *s);

/* Draw a string of ASCII characters to the center of either the left or right
 * half of the screen (specified as 0 or 1, respecitively). The string must not
 * cross screen borders; this is not checked though. The string must be
 * 0-terminated. Note that only certain characters are allowed, as specified
 * in drawASCIICharacter.
 */
void drawHalfCenteredASCIIString (int half, int y, char *s);

/* Draw a number to screen. Screen borders may not be crossed; this is not
 * checked though. The number will be left-padded with the specified
 * padding character to the specified number of digits. Digits will be
 * lost of the number has too many digits.
 */
void drawNumber (int x, int y, unsigned int value, int digits, unsigned char padding);

/* Draw the score display, using up to 8 digits.
 */
void drawScore (int score);

/* Draw the level display, using up to 2 digits.
 */
void drawLevel (int level);
