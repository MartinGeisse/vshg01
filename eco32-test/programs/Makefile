
TOOLDIR = ../../eco32-0.19/build/bin

.PHONY: all
all: bin/test1.exo bin/test2.exo

bin/test1.exo: bin/test1.bin
	$(TOOLDIR)/bin2exo 0 bin/test1.bin bin/test1.exo
	unix2dos -o bin/test1.exo

bin/test1.bin: src/test1.s
	$(TOOLDIR)/lcc -I. -A -Wo-kernel -Wl-m -Wlbin/test1.map -Wl-h -Wl-rc -Wl0xE0000000 -Wl-rd -Wl0xE0000000 -o bin/test1.bin src/test1.s

bin/test2.exo: bin/test2.bin
	$(TOOLDIR)/bin2exo 0 bin/test2.bin bin/test2.exo
	unix2dos -o bin/test2.exo

bin/test2.bin: src/test2.s
	$(TOOLDIR)/lcc -I. -A -Wo-kernel -Wl-m -Wlbin/test2.map -Wl-h -Wl-rc -Wl0xE0000000 -Wl-rd -Wl0xE0000000 -o bin/test2.bin src/test2.s


clean:
		rm -f bin/*
