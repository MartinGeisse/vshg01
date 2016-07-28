
#ifndef __CROSSTESTUTILS_CROSSTESTUTILS_H__
#define __CROSSTESTUTILS_CROSSTESTUTILS_H__

/**
 * Prints the specified error message about a failed test and exits with error code 1
 * to indicate test failure to the make system.
 */
void failTest(const char *format, ...);

/**
 * va_list version of failTest().
 */
void failTestV(const char *format, va_list args);

/**
 * Ensures that the condition is true. Fails the test if this is not the case.
 */
void assertTrue(int condition, const char *format, ...);

/**
 * Ensures that the condition is false. Fails the test if this is not the case.
 */
void assertFalse(int condition, const char *format, ...);

#endif
