/*
 *  StatusBarTestMain.c
 *  baselib
 *
 *  Created by Martin on 8/26/10.
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
#include "StatusBar.h"
#include "Widget.h"
#include "TestMainCommon.h"

/**
 * Main function.
 */
int main(int argc, const char *argv[]) {
	
	testMainScreenWidth = 320;
	testMainScreenHeight = 200;
	initializeAllegroStuff();
	zoneAllocatorInitialize();
	initializeWadFile(WAD_FILE_PATH);
	initializeStaticGraphicsData();
	initializeStaticWidgetData();
	initializeStatusBar();
	bindStatusBarState();
	systemGraphicsSetPalette((unsigned char *)getWadFileLumpContentsByName("PLAYPAL"));
	clearScreen();
	
	drawStatusBar();
	while ((readkey() & 0x7f) != 'q') drawStatusBar();
	return 0;
}
END_OF_MAIN()
