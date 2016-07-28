#
# Makefile to build different system ROMs
#

BUILD = ../build

AS = $(BUILD)/bin/as
LD = $(BUILD)/bin/ld
BIN2MCS = $(BUILD)/bin/bin2mcs

SFILES = $(wildcard *.s)
OBJFILES = $(patsubst %.s,%.o,$(SFILES))
BINFILES = $(patsubst %.s,%.bin,$(SFILES))
MCSFILES = $(patsubst %.s,%.mcs,$(SFILES))

.PHONY:		all install clean
.PRECIOUS:	$(OBJFILES) $(BINFILES)

all:		$(MCSFILES)

install:	$(MCSFILES)
		mkdir -p $(BUILD)/roms
		cp *.mcs $(BUILD)/roms

%.mcs:		%.bin
		$(BIN2MCS) 0x00000000 $< $@

%.bin:		%.o
		$(LD) -h -rc 0xE0000000 -o $@ $<

%.o:		%.s
		$(AS) -o $@ $<

clean:
		rm -f *~ *.o *.bin *.mcs
