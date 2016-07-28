#
# Makefile for 'filehello' command
#

BUILD = ../../../../build

all:		filehello

install:	filehello
		mkdir -p $(BUILD)/cmds
		cp filehello $(BUILD)/cmds

filehello:		filehello.c
		$(BUILD)/bin/lcc -A -o filehello filehello.c

clean:
		rm -f *~ filehello
