/*
 * tui.c -- text-mode user interface device driver
 */


#include "../include/param.h"
#include "../include/dev.h"
#include "../include/blk.h"
#include "../include/off.h"
#include "../include/tim.h"
#include "../include/ino.h"
#include "../include/systm.h"
#include "../include/tty.h"
#include "../include/dir.h"
#include "../include/user.h"

#include "start.h"
#include "machdep.h"
#include "clock.h"
#include "slp.h"
#include "prim.h"
#include "trm.h"
#include "tt.h"
#include "tui.h"
#include "subr.h"

void tuiOpen(dev_t dev, int flag) {
}

void tuiClose(dev_t dev, int flag) {
}

void tuiIoctl(dev_t dev, int cmd, caddr_t addr, int flag) {
}

void tuiRead(dev_t dev) {
	int c;
	do {
		// 80x30 -- 128x32 -- 2^7x2^5 -- 2^12 == 4096 cells
		int cellIndex = ((u.u_offset >> 1) & 4095);
		int address = (0xF0100000 + (cellIndex << 2));
		unsigned int cellValue = *(unsigned int*)address;
		if (u.u_offset & 1) {
			c = (cellValue >> 8) & 0xff;
		} else {
			c = cellValue & 0xff;
			if (c < 32) {
				c = 32;
			}
		}
	} while (u.u_error == 0 && passc(c) >= 0);
}

void tuiWrite(dev_t dev) {
	int c;
	do {

		// 80x30 -- 128x32 -- 2^7x2^5 -- 2^12 == 4096 cells
		int cellIndex = ((u.u_offset >> 1) & 4095);
		int address = (0xF0100000 + (cellIndex << 2));
		unsigned int previousCellValue = *(unsigned int*)address;
		int subcell = (u.u_offset & 1);
		unsigned int nextCellValue;
		
		// fetch character -- do this AFTER the address has been computed, it updates u.u_offset!
		if ((c = cpass()) < 0 || u.u_error != 0) {
			break;
		}
		c = c & 0xff;
		
		// update either the glyph or the attributes
		if (subcell) {
			nextCellValue = (previousCellValue & 0xff) + (c << 8);
		} else {
			if (c < 32) {
				c = 32;
			}
			nextCellValue = (previousCellValue & 0xff00) + c;
		}
		
		// write back
		*(unsigned int*)address = nextCellValue;
		
	} while (1);
}
