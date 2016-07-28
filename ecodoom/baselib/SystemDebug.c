/*
 *  debug.c
 *  baselib
 *
 *  Created by Martin on 7/25/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

#include <stdarg.h>
#include <stdlib.h>
#include <stdio.h>
#include "SystemDebug.h"

/**
 * See header file for information.
 */
void systemPrintDebugMessage(const char *format, ...) {
	va_list args;
	va_start(args, format);
	vprintf(format, args);
	va_end(args);
}

/**
 * See header file for information.
 */
void systemFatalError(const char *format, ...) {
	va_list args;
	va_start(args, format);
	printf("FATAL ERROR: ");
	vprintf(format, args);
	va_end(args);
	exit(1);
}
