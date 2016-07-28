/*
 *  debug.h
 *  baselib
 *
 *  Created by Martin on 7/25/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

/**
 * Prints a debug message in a platform-dependent way. This function
 * can be used analogously to printf().
 */
void systemPrintDebugMessage(const char *format, ...);

/**
 * Prints a fatal error message in a platform-dependent way, then exits
 * the program. This function can be used analogously to printf(), except
 * that it does not return.
 */
void systemFatalError(const char *format, ...);
