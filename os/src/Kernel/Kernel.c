
#include <Kernel.h>
#include <Display.h>

void panic(const char *message) {
	
	/** print the message **/
	int x = 0;
	while (1) {
		char c = *message;
		if (c == 0) {
			break;
		} else {
			writeCharacterOnDisplay(x, 0, c);
			x++;
		}
	}
	
	/** lock up **/
	while (1);
	
}

void printPanicMessage(const char *format, ...) {
	panic(format);
}

void main(void) {
	panic("yeah!");
}
