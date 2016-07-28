
#include <eos32sys.h>
#include <tui_core.h>

//
static int tuiFileDescriptor = -1;

//
static unsigned char framebuffer[32 * 128 * 2];

//
void baselib_tui_core_open(void) {
	if (tuiFileDescriptor < 0) {
		tuiFileDescriptor = open("/dev/tui", O_RDWR);
	}
}

//
void baselib_tui_core_close(void) {
	if (tuiFileDescriptor >= 0) {
		close(tuiFileDescriptor);
		tuiFileDescriptor = -1;
	}
}

//
void baselib_tui_core_read(void) {
	lseek(tuiFileDescriptor, 0, 0);
	read(tuiFileDescriptor, framebuffer, sizeof(framebuffer));
}

//
void baselib_tui_core_write(void) {
	lseek(tuiFileDescriptor, 0, 0);
	write(tuiFileDescriptor, framebuffer, sizeof(framebuffer));
}

//
void baselib_tui_core_setCell(int x, int y, int c, int a) {
	framebuffer[y * 128 * 2 + x * 2 + 0] = (unsigned char)c;
	framebuffer[y * 128 * 2 + x * 2 + 1] = (unsigned char)a;
}
