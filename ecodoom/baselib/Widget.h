/*
 *  Widget.h
 *  baselib
 *
 *  Created by Martin on 8/26/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

/**
 * Shared data structure for common widget graphics.
 */
struct StaticWidgetData {
	
	/** patch data for small digit patches **/
	void *smallDigits[10];
	
	/** patch data for large digit patches **/
	void *largeDigits[10];
	
	/** patch data for large digit minus sign **/
	void *largeMinus;
	
	/** patch data for large digit percent sign **/
	void *largePercent;

	/** patch data for intermission digit patches **/
	void *intermissionDigits[10];

	/** patch data for intermission digit minus sign **/
	void *intermissionMinus;
	
	/** patch data for intermission digit percent sign **/
	void *intermissionPercent;
	
};

/**
 * The static widget data. This data structure is controlled by
 * initializeStaticWidgetData() and disposeStaticWidgetData().
 */
extern struct StaticWidgetData staticWidgetData;

/**
 * The widget style (patch set) to use for a number widget.
 */
enum NumberWidgetStyle {
	
	/** use small numbers **/
	NUMBER_WIDGET_STYLE_SMALL = 0,
	
	/** use large numbers **/
	NUMBER_WIDGET_STYLE_LARGE = 1,

	/** use large numbers and a percent sign **/
	NUMBER_WIDGET_STYLE_LARGE_PERCENT = 2,

	/** use intermission screen numbers **/
	NUMBER_WIDGET_STYLE_INTERMISSION = 3,

	/** use intermission screen numbers and a percent sign **/
	NUMBER_WIDGET_STYLE_INTERMISSION_PERCENT = 4,

};

/**
 * This structure can be used by a client to implement a number widget.
 * Normally, the widget will display the client value numerically. However,
 * the special value 0x80000000 can be used to hide the widget.
 */
struct NumberWidget {
	
	/** the widget style **/
	enum NumberWidgetStyle style;
	
	/** the x position of the widget on screen **/
	int x;
	
	/** the y position of the widget on screen **/
	int y;
	
	/** the maximum number of digits to display **/
	int maxDigitCount;
	
	/** pointer to the current client value **/
	int *clientValuePointer;
	
};

/**
 * Displays a single patch from an array, using the client value as the
 * index. This can be used to visualize enumerated values. Any negative
 * value will hide the widget.
 */
struct ChoiceWidget {

	/** the patch set to use **/
	void **patches;
	
	/** the x position of the widget on screen **/
	int x;
	
	/** the y position of the widget on screen **/
	int y;
	
	/** pointer to the current client value **/
	int *clientValuePointer;
	
};

/**
 * Displays a single fixed patch if the client value is nonzero, and
 * hides it if the client value is zero.
 */
struct OnOffWidget {

	/** the patch to use **/
	void *patch;
	
	/** the x position of the widget on screen **/
	int x;
	
	/** the y position of the widget on screen **/
	int y;
	
	/** pointer to the current client value **/
	int *clientValuePointer;

};

/**
 * Loads static widget data from the WAD file.
 */
void initializeStaticWidgetData();

/**
 * Disposes of static widget data.
 */
void disposeStaticWidgetData();

/**
 * Initializes a number widget.
 */
void initializeNumberWidget(struct NumberWidget *widget, enum NumberWidgetStyle style, int x, int y, int maxDigitCount, int *clientValuePointer);

/**
 * Draws a number widget.
 */
void drawNumberWidget(struct NumberWidget *widget);

/**
 * Initializes a choice widget.
 */
void initializeChoiceWidget(struct ChoiceWidget *widget, void **patches, int x, int y, int *clientValuePointer);

/**
 * Draws a choice widget.
 */
void drawChoiceWidget(struct ChoiceWidget *widget);

/**
 * Initializes an on/off widget.
 */
void initializeOnOffWidget(struct OnOffWidget *widget, void *patch, int x, int y, int *clientValuePointer);

/**
 * Draws an on/off widget.
 */
void drawOnOffWidget(struct OnOffWidget *widget);

