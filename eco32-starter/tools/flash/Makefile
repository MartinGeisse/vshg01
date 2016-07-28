#
# Makefile for ROM flashing tools
#

BUILD = ../../build

.PHONY:		all install clean

all:

install:
		mkdir -p $(BUILD)/flash
		cp flash-500.bit $(BUILD)/flash
		cp flash-1600.bit $(BUILD)/flash
		cp HOWTO $(BUILD)/flash

clean:
		rm -f *~
