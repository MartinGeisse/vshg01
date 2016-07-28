
#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>

#include <CrossTestUtils.h>

// see header file
void failTest(const char *format, ...) {
	va_list args;
	va_start(args, format);
	vprintf(format, args);
	va_end(args);
	exit(1);
}

// see header file
void failTestV(const char *format, va_list args) {
	vprintf(format, args);
	printf("\n");
	exit(1);
}

// see header file
void assertTrue(int condition, const char *format, ...) {
	if (!condition) {
		va_list args;
		printf("assertTrue() failed.\n");
		va_start(args, format);
		failTestV(format, args);
	}
}

// see header file
void assertFalse(int condition, const char *format, ...) {
	if (condition) {
		va_list args;
		printf("assertFalse() failed.\n");
		va_start(args, format);
		failTestV(format, args);
	}
}
