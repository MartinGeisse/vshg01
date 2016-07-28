/*
 *  Widget.c
 *  baselib
 *
 *  Created by Martin on 8/26/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

#include "Common.h"
#include "ZoneAllocator.h"
#include "WadFile.h"
#include "SystemDebug.h"
#include "FixedPointNumber.h"
#include "LowlevelGraphics.h"
#include "Widget.h"

/**
 * See header file for information.
 */
struct StaticWidgetData staticWidgetData;

/**
 * See header file for information.
 */
void initializeStaticWidgetData() {
	int i;
	char largeDigitNameBuffer[8] = {'S', 'T', 'T', 'N', 'U', 'M', 0, 0};
	char smallDigitNameBuffer[8] = {'S', 'T', 'Y', 'S', 'N', 'U', 'M', 0};
	char intermissionDigitNameBuffer[8] = {'W', 'I', 'N', 'U', 'M', 0, 0, 0};
	
	for (i=0; i<10; i++) {
		largeDigitNameBuffer[6] = '0' + i;
		staticWidgetData.largeDigits[i] = getWadFileLumpContentsByName(largeDigitNameBuffer);
		smallDigitNameBuffer[7] = '0' + i;
		staticWidgetData.smallDigits[i] = getWadFileLumpContentsByName(smallDigitNameBuffer);
		intermissionDigitNameBuffer[5] = '0' + i;
		staticWidgetData.intermissionDigits[i] = getWadFileLumpContentsByName(intermissionDigitNameBuffer);
	}

	staticWidgetData.largeMinus = getWadFileLumpContentsByName("STTMINUS");
	staticWidgetData.largePercent = getWadFileLumpContentsByName("STTPRCNT");
	staticWidgetData.intermissionMinus = getWadFileLumpContentsByName("WIMINUS");
	staticWidgetData.intermissionPercent = getWadFileLumpContentsByName("WIPCNT");
}

/**
 * See header file for information.
 */
void disposeStaticWidgetData() {
	int i;
	for (i=0; i<10; i++) {
		zoneAllocatorDispose(staticWidgetData.largeDigits[i]);
		staticWidgetData.largeDigits[i] = NULL;
		zoneAllocatorDispose(staticWidgetData.smallDigits[i]);
		staticWidgetData.smallDigits[i] = NULL;
		zoneAllocatorDispose(staticWidgetData.intermissionDigits[i]);
		staticWidgetData.intermissionDigits[i] = NULL;
	}
	zoneAllocatorDispose(staticWidgetData.largeMinus);
	staticWidgetData.largeMinus = NULL;
	zoneAllocatorDispose(staticWidgetData.largePercent);
	staticWidgetData.largePercent = NULL;
	zoneAllocatorDispose(staticWidgetData.intermissionMinus);
	staticWidgetData.intermissionMinus = NULL;
	zoneAllocatorDispose(staticWidgetData.intermissionPercent);
	staticWidgetData.intermissionPercent = NULL;
}

/**
 * See header file for information.
 */
void initializeNumberWidget(struct NumberWidget *widget, enum NumberWidgetStyle style, int x, int y, int maxDigitCount, int *clientValuePointer) {
	widget->style = style;
	widget->x = x;
	widget->y = y;
	widget->maxDigitCount = maxDigitCount;
	widget->clientValuePointer = clientValuePointer;
}

/**
 * See header file for information.
 */
void drawNumberWidget(struct NumberWidget *widget) {
	void **patches;
	void *signPatch;
	int patchWidth;
	int value;
	int negative;
	int x;
	int remainingDigits;
	
	/** allow disabling with a special client value **/
	if ((*widget->clientValuePointer) == 0x80000000) {
		return;
	}

	/** draw percent if necessary **/
	if (widget->style == NUMBER_WIDGET_STYLE_LARGE_PERCENT) {
		drawPatchOnScreen(widget->x, widget->y, staticWidgetData.largePercent);
	} else if (widget->style == NUMBER_WIDGET_STYLE_INTERMISSION_PERCENT) {
		drawPatchOnScreen(widget->x, widget->y, staticWidgetData.intermissionPercent);
	}
	
	/** obtain the patch set to use **/
	switch (widget->style) {
			
		case NUMBER_WIDGET_STYLE_SMALL:
			patches = staticWidgetData.smallDigits;
			signPatch = staticWidgetData.largeMinus; // we don't have a small minus sign
			break;
			
		case NUMBER_WIDGET_STYLE_LARGE:
		case NUMBER_WIDGET_STYLE_LARGE_PERCENT:
			patches = staticWidgetData.largeDigits;
			signPatch = staticWidgetData.largeMinus;
			break;
			
		case NUMBER_WIDGET_STYLE_INTERMISSION:
		case NUMBER_WIDGET_STYLE_INTERMISSION_PERCENT:
			patches = staticWidgetData.intermissionDigits;
			signPatch = staticWidgetData.intermissionMinus;
			break;
			
		default:
			systemFatalError("unknown number widget style: %d", widget->style);
			
	}
			
	/** obtain patch width for digit positioning **/
	patchWidth = *(unsigned short *)(patches[0]);
	
	/** obtain client value **/
	value = *widget->clientValuePointer;
	
	/** start at the widget's x position **/
	x = widget->x;
	
	/** compute absolute value and sign, and handle zero case **/
	if (value < 0) {
		negative = 1;
		value = -value;
	} else if (value > 0) {
		negative = 0;
	} else {
		drawPatchOnScreen(x - patchWidth, widget->y, patches[0]);
		return;
	}
	
	/** draw digits **/
	remainingDigits = widget->maxDigitCount;
	while (value > 0 && remainingDigits > 0) {
		x -= patchWidth;
		drawPatchOnScreen(x, widget->y, patches[value % 10]);
		remainingDigits--;
		value /= 10;
	}
	
	/** draw sign if necessary **/
	if (negative) {
		drawPatchOnScreen(x - *(unsigned short *)signPatch, widget->y, signPatch);
	}

}

/**
 * See header file for information.
 */
void initializeChoiceWidget(struct ChoiceWidget *widget, void **patches, int x, int y, int *clientValuePointer) {
	widget->patches = patches;
	widget->x = x;
	widget->y = y;
	widget->clientValuePointer = clientValuePointer;
}

/**
 * See header file for information.
 */
void drawChoiceWidget(struct ChoiceWidget *widget) {
	int value = *widget->clientValuePointer;
	if (value >= 0) {
		drawPatchOnScreen(widget->x, widget->y, widget->patches[value]);
	}
}

/**
 * See header file for information.
 */
void initializeOnOffWidget(struct OnOffWidget *widget, void *patch, int x, int y, int *clientValuePointer) {
	widget->patch = patch;
	widget->x = x;
	widget->y = y;
	widget->clientValuePointer = clientValuePointer;
}

/**
 * See header file for information.
 */
void drawOnOffWidget(struct OnOffWidget *widget) {
	if (*widget->clientValuePointer) {
		drawPatchOnScreen(widget->x, widget->y, widget->patch);
	}
}
