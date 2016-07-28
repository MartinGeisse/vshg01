#
# Makefile for ECO32 porting project
#

VERSION = port-15

DIRS = doc binutils tools roms monitor loadserv memtest fm-synth

BUILD = `pwd`/build

.PHONY:		all compiler builddir clean dist

all:		compiler
		for i in $(DIRS) ; do \
		  $(MAKE) -C $$i install ; \
		done

compiler:	builddir
		$(MAKE) -C lcc BUILDDIR=$(BUILD)/bin \
		  HOSTFILE=etc/eco32-linux.c lcc
		$(MAKE) -C lcc BUILDDIR=$(BUILD)/bin all
		rm -f $(BUILD)/bin/*.c
		rm -f $(BUILD)/bin/*.o
		rm -f $(BUILD)/bin/*.a

builddir:
		mkdir -p $(BUILD)/bin

clean:
		for i in $(DIRS) ; do \
		  $(MAKE) -C $$i clean ; \
		done
		rm -rf $(BUILD)
		rm -f *~

dist:		clean
		(cd .. ; \
		 tar -cvf eco32-30-$(VERSION).tar eco32-30-$(VERSION)/* ; \
		 gzip -f eco32-30-$(VERSION).tar)
