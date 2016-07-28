/*
 *  WidgetTestMain.c
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
#include "Widget.h"
#include "TestMainCommon.h"

static struct NumberWidget numberWidget;

static struct ChoiceWidget choiceWidget;

static struct OnOffWidget onOffWidget;

/**
 * Main function.
 */
int main(int argc, const char *argv[]) {
	int value;
	void *cards[6];
	
	initializeAllegroStuff();
	zoneAllocatorInitialize();
	initializeWadFile(WAD_FILE_PATH);
	initializeStaticGraphicsData();
	initializeStaticWidgetData();
	systemGraphicsSetPalette((unsigned char *)getWadFileLumpContentsByName("PLAYPAL"));
	clearScreen();
	
	cards[0] = getWadFileLumpContentsByName("STKEYS0");
	cards[1] = getWadFileLumpContentsByName("STKEYS1");
	cards[2] = getWadFileLumpContentsByName("STKEYS2");
	cards[3] = getWadFileLumpContentsByName("STKEYS3");
	cards[4] = getWadFileLumpContentsByName("STKEYS4");
	cards[5] = getWadFileLumpContentsByName("STKEYS5");

	initializeNumberWidget(&numberWidget, NUMBER_WIDGET_STYLE_SMALL, 100, 10, 10, &value);
	value = 1;
	drawNumberWidget(&numberWidget);
	
	numberWidget.y = 20;
	value = 2;
	drawNumberWidget(&numberWidget);
	
	numberWidget.y = 30;
	value = 0x80000000;
	drawNumberWidget(&numberWidget);

	numberWidget.y = 40;
	value = 4;
	drawNumberWidget(&numberWidget);

	numberWidget.y = 50;
	numberWidget.style = NUMBER_WIDGET_STYLE_LARGE;
	value = 5;
	drawNumberWidget(&numberWidget);

	numberWidget.y = 70;
	value = -6;
	drawNumberWidget(&numberWidget);

	numberWidget.y = 90;
	value = 12345;
	drawNumberWidget(&numberWidget);

	numberWidget.y = 110;
	value = -67890;
	drawNumberWidget(&numberWidget);

	numberWidget.y = 130;
	numberWidget.style = NUMBER_WIDGET_STYLE_SMALL;
	value = 12345;
	drawNumberWidget(&numberWidget);
	
	numberWidget.y = 140;
	value = 67890;
	drawNumberWidget(&numberWidget);

	numberWidget.y = 150;
	numberWidget.style = NUMBER_WIDGET_STYLE_LARGE_PERCENT;
	value = 50;
	drawNumberWidget(&numberWidget);

	initializeChoiceWidget(&choiceWidget, cards, 10, 170, &value);
	value = 0;
	drawChoiceWidget(&choiceWidget);

	choiceWidget.x = 20;
	value = 1;
	drawChoiceWidget(&choiceWidget);

	choiceWidget.x = 30;
	value = 2;
	drawChoiceWidget(&choiceWidget);

	choiceWidget.x = 40;
	value = -1;
	drawChoiceWidget(&choiceWidget);

	choiceWidget.x = 50;
	value = 3;
	drawChoiceWidget(&choiceWidget);

	choiceWidget.x = 60;
	value = 4;
	drawChoiceWidget(&choiceWidget);

	choiceWidget.x = 70;
	value = 5;
	drawChoiceWidget(&choiceWidget);
	
	initializeOnOffWidget(&onOffWidget, cards[0], 10, 180, &value);
	value = 0;
	drawOnOffWidget(&onOffWidget);
	
	onOffWidget.x = 20;
	value = 1;
	drawOnOffWidget(&onOffWidget);

	numberWidget.x = 100;
	numberWidget.y = 200;
	numberWidget.style = NUMBER_WIDGET_STYLE_INTERMISSION;
	value = 12345;
	drawNumberWidget(&numberWidget);
	
	numberWidget.y = 220;
	value = -67890;
	drawNumberWidget(&numberWidget);
	
	numberWidget.y = 240;
	numberWidget.style = NUMBER_WIDGET_STYLE_INTERMISSION_PERCENT;
	value = 50;
	drawNumberWidget(&numberWidget);
	
	readkey();
	return 0;
}
END_OF_MAIN()
