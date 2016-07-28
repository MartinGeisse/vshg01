/*
 * list file or directory
 */

#include <tui_core.h>

//
int main(int argc, char *argv[]) {
	baselib_tui_core_open();
	baselib_tui_core_setCell(0, 0, 'T', 12);
	baselib_tui_core_setCell(1, 0, 'U', 12);
	baselib_tui_core_setCell(2, 0, 'I', 12);
	baselib_tui_core_setCell(3, 0, ' ', 12);
	baselib_tui_core_setCell(4, 0, 'w', 12);
	baselib_tui_core_setCell(5, 0, 'o', 12);
	baselib_tui_core_setCell(6, 0, 'r', 12);
	baselib_tui_core_setCell(7, 0, 'k', 12);
	baselib_tui_core_setCell(8, 0, 's', 12);
	baselib_tui_core_setCell(9, 0, '!', 12);
	baselib_tui_core_write();
	baselib_tui_core_close();
	return 0;
}
