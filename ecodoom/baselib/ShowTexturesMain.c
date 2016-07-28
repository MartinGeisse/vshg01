/*
 *  ShowFlatsMain.c
 *  baselib
 *
 *  Created by Martin on 8/12/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

#include <allegro.h>
#include "Common.h"
#include "SystemGraphics.h"
#include "ZoneAllocator.h"
#include "WadFile.h"
#include "FixedPointNumber.h"
#include "StaticGraphicsData.h"
#include "TestMainCommon.h"

void drawTexture(int textureIndex) {
	int x, y;
	char buf[256];
	struct TextureDescriptor *texture = getTexture(textureIndex);
	for (x = 0; x < texture->width; x++) {
		unsigned char *column = getTextureColumnTexels(texture, x);
		for (y = 0; y < texture->height; y++) {
			systemGraphicsDrawPixel(x, y, column[y]);
		}
	}
	
	sprintf(buf, "%8s", texture->name.value);
	printDescription(buf);
}

void drawTextureTransparent(int textureIndex) {
	int x, y;
	struct TextureDescriptor *texture = getTexture(textureIndex);
	printf("drawing transparent texture %8s\n", texture->name.value);
	for (x = 0; x < texture->width; x++) {
		unsigned char *post = getTextureColumnTexels(texture, x) - 3;
		int antiMedusaCounter;
		
		for (antiMedusaCounter = 0; antiMedusaCounter < 100; antiMedusaCounter++) {
			int offset = post[0];
			int length = post[1];
			unsigned char *data = post + 3;
			if (offset == 255) {
				break;
			}
			
			for (y=0; y<length; y++) {
				systemGraphicsDrawPixel(x + 256, y + offset, data[y]);
			}
			post = post + length + 4;
		}
	}
}

/**
 * Main function.
 */
int main(int argc, const char *argv[]) {
	int i;

	initializeAllegroStuff();
	zoneAllocatorInitialize();
	initializeWadFile(WAD_FILE_PATH);
	initializeStaticGraphicsData();
	systemGraphicsSetPalette((unsigned char *)getWadFileLumpContentsByName("PLAYPAL"));

	for (i=0; i<getTextureCount(); i++) {
		clearScreen();
		drawTexture(i);
		drawTextureTransparent(i);
		if ((readkey() & 0xff) == 'q') {
			break;
		}
	}

	return 0;
}
END_OF_MAIN()
