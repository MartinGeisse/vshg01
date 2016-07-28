
TOOLDIR = ../../eco32-0.20/build/bin

SOURCES = start.s $(REGULAR_SOURCES) end.s
REGULAR_SOURCES = $(UTIL_SOURCES) $(LOWLEVEL_SOURCES) $(GAME_SOURCES)
UTIL_SOURCES = util/cpu.s util/kernel.s util/memory.c util/random.c
LOWLEVEL_SOURCES = lowlevel/timer.c lowlevel/sound.c lowlevel/display.c lowlevel/keyboard.c
GAME_SOURCES = game/main.c game/draw.c game/engine.c game/gamestate.c game/shapes.c game/soundchain.c

all:		romimage.exo

romimage.exo: romimage.bin
		$(TOOLDIR)/bin2exo 0 romimage.bin romimage.exo

romimage.bin: $(SOURCES)
		$(TOOLDIR)/lcc -I. -A -Wo-kernel -Wl-m -Wlromimage.map -Wl-h -Wl-rc -Wl0xC0000000 -o romimage.bin $(SOURCES)

clean:
		rm -f *~ romimage.bin romimage.map romimage.exo
