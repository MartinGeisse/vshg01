#
# Makefile for 'lua' command
#

BUILD = ../../../../build

CORE_C = lapi.c lcode.c lctype.c ldebug.c ldo.c ldump.c lfunc.c lgc.c llex.c \
	lmem.c lobject.c lopcodes.c lparser.c lstate.c lstring.c ltable.c \
	ltm.c lundump.c lvm.c lzio.c
LIB_C = lauxlib.c lbaselib.c lbitlib.c lcorolib.c ldblib.c liolib.c \
	lmathlib.c loslib.c lstrlib.c ltablib.c loadlib.c linit.c
LUA_C = lua.c
LUA_ALL_C = $(CORE_C) $(LIB_C) $(LUA_C)


all:		lua

install:	lua
		mkdir -p $(BUILD)/cmds
		cp lua $(BUILD)/cmds

lua:	$(LUA_ALL_C)
		$(BUILD)/bin/lcc -A -o lua $(LUA_ALL_C)

clean:
		rm -f *~ lua
