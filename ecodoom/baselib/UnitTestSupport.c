/*
 *  UnitTestSupport.c
 *  baselib
 *
 *  Created by Martin on 7/26/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#include "UnitTestSupport.h"

/**
 * See header file for information.
 */
void assert(int condition, const char *format, ...) {
	if (!condition) {
		va_list args;
		va_start(args, format);
		vprintf(format, args);
		va_end(args);
		puts("\n");
		exit(1);
	}
}
