/*
 *  UnitTestSupport.h
 *  baselib
 *
 *  Created by Martin on 7/26/10.
 *  Copyright 2010 __MyCompanyName__. All rights reserved.
 *
 */

/**
 * Ensures that the condition argument is not 0, and exits with
 * the specified error messge if it is.
 */
void assert(int condition, const char *failureMessage, ...);
