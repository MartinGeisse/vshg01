/*
 *  CommonTest.c
 *  baselib
 *
 *  Created by Martin on 7/28/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

#include "Common.c"
#include "UnitTestSupport.h"

unsigned char bytes[4] = {0x11, 0x22, 0x33, 0x44};

void testTypeSizes() {
	assert(sizeof(char) == 1, "sizeof char");
	assert(sizeof(short) == 2, "sizeof short");
	assert(sizeof(int) == 4, "sizeof int");
	assert(sizeof(long) == 4, "sizeof long");
	assert(sizeof(long long) == 8, "sizeof long long");
}

void testSwap() {
	short x = 0x1234;
	short xs = swapEndianness16(x);
	
	unsigned short ux = 0x1234;
	unsigned short uxs = swapEndianness16(ux);
	
	int y = 0x12345678;
	int ys = swapEndianness32(y);
	
	unsigned int uy = 0x12345678;
	unsigned int uys = swapEndianness32(uy);
	
	assert(xs == 0x3412, "swap short");
	assert(uxs == 0x3412, "swap unsigned short");
	assert(ys == 0x78563412, "swap int");
	assert(uys == 0x78563412, "swap unsigned int");
}

void testToType() {
	assert(toLittleEndian16(*((short *)bytes)) == 0x2211, "toLittleEndian16");
	assert(toLittleEndian16u(*((short *)bytes)) == 0x2211, "toLittleEndian16u");
	assert(toLittleEndian32(*((int *)bytes)) == 0x44332211, "toLittleEndian32");
	assert(toLittleEndian32u(*((int *)bytes)) == 0x44332211, "toLittleEndian32u");
	assert(toBigEndian16(*((short *)bytes)) == 0x1122, "toBigEndian16");
	assert(toBigEndian16u(*((short *)bytes)) == 0x1122, "toBigEndian16u");
	assert(toBigEndian32(*((int *)bytes)) == 0x11223344, "toBigEndian32");
	assert(toBigEndian32u(*((int *)bytes)) == 0x11223344, "toBigEndian32u");
}

void testGetLimitedStringLength() {
	assert(getLimitedStringLength("foobar", 20) == 6, "getLimitedStringLength(), less than max length");
	assert(getLimitedStringLength("foobar", 7) == 6, "getLimitedStringLength(), just less than max length");
	assert(getLimitedStringLength("foobar", 6) == 6, "getLimitedStringLength(), equal to max length");
	assert(getLimitedStringLength("foobar", 5) == 5, "getLimitedStringLength(), longer than max length");
}

int main() {
	testTypeSizes();
	testSwap();
	testToType();
	testGetLimitedStringLength();
	return 0;
}

