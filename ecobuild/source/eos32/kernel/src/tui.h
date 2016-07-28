/*
 * tui.h -- text-mode user interface device driver
 */

#ifndef _TUI_H_
#define _TUI_H_

void tuiOpen(dev_t dev, int flag);
void tuiClose(dev_t dev, int flag);
void tuiRead(dev_t dev);
void tuiWrite(dev_t dev);
void tuiIoctl(dev_t dev, int cmd, caddr_t addr, int flag);

#endif
