/*
 *  SystemDebugMock.c
 *  baselib
 *
 *  Created by Martin on 7/26/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

#include <stdarg.h>
#include <stdlib.h>
#include <stdio.h>
#include <setjmp.h>
#include "Common.h"
#include "SystemDebug.h"

int systemPrintDebugMessageCalled = 0;
int systemFatalErrorCalled = 0;

int systemFatalErrorJumpBufferActive = 0;
jmp_buf systemFatalErrorJumpBuffer;

/**
 * See header file for information.
 */
void systemPrintDebugMessage(const char *format, ...) {
	va_list args;
	va_start(args, format);
	vprintf(format, args);
	va_end(args);
	systemPrintDebugMessageCalled++;
}

/**
 * See header file for information.
 */
void systemFatalError(const char *format, ...) {
	systemFatalErrorCalled++;
	if (systemFatalErrorJumpBufferActive) {
		longjmp(systemFatalErrorJumpBuffer, 0);
	}
}
