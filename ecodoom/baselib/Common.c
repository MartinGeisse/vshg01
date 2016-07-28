/*
 *  Common.c
 *  baselib
 *
 *  Created by Martin on 7/28/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

#include <strings.h>
#include "Common.h"

/**
 * Helper function.
 */
static void swapEndianness16internal(unsigned char *pointer) {
	unsigned char temp;
	
	temp = pointer[0];
	pointer[0] = pointer[1];
	pointer[1] = temp;
}

/**
 * Helper function.
 */
static void swapEndianness32internal(unsigned char *pointer) {
	unsigned char temp;
	
	temp = pointer[0];
	pointer[0] = pointer[3];
	pointer[3] = temp;
	
	temp = pointer[1];
	pointer[1] = pointer[2];
	pointer[2] = temp;
}

/**
 * See header file for information.
 */
short swapEndianness16(short value) {
	swapEndianness16internal((unsigned char *)(&value));
	return value;
}

/**
 * See header file for information.
 */
unsigned short swapEndianness16u(unsigned short value) {
	swapEndianness16internal((unsigned char *)(&value));
	return value;
}

/**
 * See header file for information.
 */
int swapEndianness32(int value) {
	swapEndianness32internal((unsigned char *)(&value));
	return value;
}

/**
 * See header file for information.
 */
unsigned int swapEndianness32u(unsigned int value) {
	swapEndianness32internal((unsigned char *)(&value));
	return value;
}

/**
 * See header file for information.
 */
int getLimitedStringLength(const char *s, int maxLength) {
	int length = 0;
	while (length < maxLength && s[length] != 0) {
		length++;
	}
	return length;
}

/**
 * See header file for information.
 */
int doomNamesEqual(const struct DoomName *name1, const struct DoomName *name2) {
	return (strncasecmp(name1->value, name2->value, 8) == 0);
}
