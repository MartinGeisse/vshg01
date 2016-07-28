/*
 *  common.h
 *  baselib
 *
 *  Created by Martin on 7/25/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

#define DOOM_LITTLE_ENDIAN
//define DOOM_BIG_ENDIAN

#define MIN_INT		0x80000000
#define MAX_INT		0x7fffffff

/**
 * Note: These settings can NOT be used to switch to a different screen size!
 * They are used to inform testing code about the screen size used.
 */
#define SCREEN_WIDTH 640
#define SCREEN_HEIGHT 480

/************************************************************/

/**
 * NULL pointer definition
 */
#ifndef NULL
#define NULL	((void*)0)
#endif

/**
 * Big / little endian support
 */
#ifdef DOOM_LITTLE_ENDIAN
	#ifdef DOOM_BIG_ENDIAN
		#error "both LITTLE_ENDIAN and BIG_ENDIAN defined"
	#else
		#define toLittleEndian16(value) (value)
		#define toLittleEndian16u(value) (value)
		#define toLittleEndian32(value) (value)
		#define toLittleEndian32u(value) (value)
		#define fromLittleEndian16(value) (value)
		#define fromLittleEndian16u(value) (value)
		#define fromLittleEndian32(value) (value)
		#define fromLittleEndian32u(value) (value)
		#define toBigEndian16(value) swapEndianness16(value)
		#define toBigEndian16u(value) swapEndianness16u(value)
		#define toBigEndian32(value) swapEndianness32(value)
		#define toBigEndian32u(value) swapEndianness32u(value)
		#define fromBigEndian16(value) swapEndianness16(value)
		#define fromBigEndian16u(value) swapEndianness16u(value)
		#define fromBigEndian32(value) swapEndianness32(value)
		#define fromBigEndian32u(value) swapEndianness32u(value)
	#endif
#else
	#ifdef DOOM_BIG_ENDIAN
		#define toLittleEndian16(value) swapEndianness16(value)
		#define toLittleEndian16u(value) swapEndianness16u(value)
		#define toLittleEndian32(value) swapEndianness32(value)
		#define toLittleEndian32u(value) swapEndianness32u(value)
		#define fromLittleEndian16(value) swapEndianness16(value)
		#define fromLittleEndian16u(value) swapEndianness16u(value)
		#define fromLittleEndian32(value) swapEndianness32(value)
		#define fromLittleEndian32u(value) swapEndianness32u(value)
		#define toBigEndian16(value) (value)
		#define toBigEndian16u(value) (value)
		#define toBigEndian32(value) (value)
		#define toBigEndian32u(value) (value)
		#define fromBigEndian16(value) (value)
		#define fromBigEndian16u(value) (value)
		#define fromBigEndian32(value) (value)
		#define fromBigEndian32u(value) (value)
	#else
		#error "neither LITTLE_ENDIAN nor BIG_ENDIAN defined"
	#endif
#endif
short swapEndianness16(short value);
unsigned short swapEndianness16u(unsigned short value);
int swapEndianness32(int value);
unsigned int swapEndianness32u(unsigned int value);

/**
 * Obtains a value of the specified type from the location that is computed
 * from the pointer and offset (in bytes). No other conversion, e.g. endianness
 * conversion, is done.
 */
#define DESERIALIZE(pointer, offset, type)	(*(type *)(((unsigned char *)(pointer)) + (offset)))

/**
 * This function determines the length of the specified string, inspecting at most
 * maxLength characters from the starting pointer. Some platforms provide this
 * function as strnlen(), but we cannot assume it is present.
 */
int getLimitedStringLength(const char *s, int maxLength);

/**
 * This structure is used for names up to 8 characters. The contained
 * character array is up to 8 characters long and is NOT terminated
 * with a NUL character if 8 characters long. It is filled up to 8
 * characters with NUL characters though if shorter.
 *
 * These names are used throughout the application, for example for
 * WAD file lump names or texture names.
 */
struct DoomName {
	
	/**
	 * the actual name characters
	 */
	char value[8];
	
};

/**
 * Returns 1 if the specified names are equal, 0 if not.
 */
int doomNamesEqual(const struct DoomName *name1, const struct DoomName *name2);
