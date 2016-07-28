#
# Makefile for ECO32 memory test
#

BUILD = ../build

.PHONY:		all install clean

all:		memtest.exo

install:	memtest.exo
		mkdir -p $(BUILD)/load
		cp memtest.exo $(BUILD)/load

memtest.exo:	memtest.bin
		$(BUILD)/bin/bin2exo -S3 0x00010000 memtest.bin memtest.exo

memtest.bin:	start.s memtest.c
		$(BUILD)/bin/lcc -Wall -S memtest.c
		$(BUILD)/bin/as -o memtest.o memtest.s
		$(BUILD)/bin/as -o start.o start.s
		$(BUILD)/bin/as -o end.o end.s
		$(BUILD)/bin/ld -h -rc 0xC0010000 \
		  -o memtest.bin -m memtest.map start.o memtest.o end.o

clean:
		rm -f *~ memtest.s start.o end.o memtest.o
		rm -f memtest.bin memtest.map memtest.exo
