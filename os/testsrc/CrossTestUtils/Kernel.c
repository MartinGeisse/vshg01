
#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>

#include <Kernel.h>

void printPanicMessage(const char *format, ...) {
	va_list args;
	va_start(args, format);
	vprintf(format, args);
	va_end(args);
	exit(1);
}
