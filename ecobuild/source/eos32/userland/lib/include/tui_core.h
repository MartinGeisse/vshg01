
#ifndef BASELIB_TUI_CORE_H
#define BASELIB_TUI_CORE_H

/*
 * Opens the TUI device.
 */
void baselib_tui_core_open(void);

/*
 * Closes the TUI device.
 */
void baselib_tui_core_close(void);

/*
 * Reads the TUI device framebuffer into the local framebuffer.
 */
void baselib_tui_core_read(void);

/*
 * Writes the local framebuffer into the TUI device framebuffer.
 */
void baselib_tui_core_write(void);

/*
 * Writes the specified character and attributes into a local framebuffer cell.
 */
void baselib_tui_core_setCell(int x, int y, int c, int a);


#endif
