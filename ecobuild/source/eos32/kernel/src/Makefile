#
# Makefile for EOS32 kernel
#

BUILD = ../../../build

STARTINC = start.h
STARTSRC = start.s
KERNINC = acc.h alloc.h bio.h c.h clock.h fio.h iget.h \
          klib.h machdep.h malloc.h mem.h nami.h \
          pipe.h prim.h rdwri.h idedsk.h serdsk.h ramdsk.h \
          trm.h sig.h slp.h subr.h sys.h sys1.h sys2.h sys3.h sys4.h \
          sysent.h tt.h txt.h trap.h ureg.h xtest.h tui.h
KERNSRC = acc.c alloc.c bio.c c.c clock.c fio.c iget.c \
          klib.c machdep.c main.c malloc.c mem.c nami.c \
          pipe.c prim.c rdwri.c idedsk.c serdsk.c ramdsk.c \
          trm.c sig.c slp.c subr.c sys.c sys1.c sys2.c sys3.c sys4.c \
          sysent.c tt.c txt.c trap.c ureg.c tui.c
LOGINC = logfile.h
LOGSRC = logfile.c
CONINC = console.h display.h keyboard.h
CONSRC = console.c display.s keyboard.s kbdtbls.s
ENDINC =
ENDSRC = end.s

INC = $(STARTINC) $(KERNINC) $(LOGINC) $(CONINC) $(ENDINC)
SRC = $(STARTSRC) $(KERNSRC) $(LOGSRC) $(CONSRC) $(ENDSRC)
BIN = eos32.bin
MAP = eos32.map


$(BIN):		$(INC) $(SRC) icode/icode.dump
		$(BUILD)/bin/lcc -A -Wo-kernel \
		  -Wl-m -Wl$(MAP) -o $(BIN) $(SRC)

