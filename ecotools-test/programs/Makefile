
TOOLDIR = ../../original-eco-tools

.PHONY: all
all: test.bin ChattyTerminalEcho.bin IncrementMemoryCell.bin ScanCodeEcho.bin

test.bin: test.s
		$(TOOLDIR)/lcc -I. -A -Wo-kernel -Wl-m -Wltest.map -Wl-h -Wl-rc -Wl0xE0000000 -Wl-rd -Wl0xE0000000 -o test.bin test.s

ChattyTerminalEcho.bin: ChattyTerminalEcho.s
		$(TOOLDIR)/lcc -I. -A -Wo-kernel -Wl-m -WlChattyTerminalEcho.map -Wl-h -Wl-rc -Wl0xE0000000 -Wl-rd -Wl0xE0000000 -o ChattyTerminalEcho.bin ChattyTerminalEcho.s

IncrementMemoryCell.bin: IncrementMemoryCell.s
		$(TOOLDIR)/lcc -I. -A -Wo-kernel -Wl-m -WlIncrementMemoryCell.map -Wl-h -Wl-rc -Wl0xE0000000 -Wl-rd -Wl0xE0000000 -o IncrementMemoryCell.bin IncrementMemoryCell.s

ScanCodeEcho.bin: ScanCodeEcho.s
		$(TOOLDIR)/lcc -I. -A -Wo-kernel -Wl-m -WlScanCodeEcho.map -Wl-h -Wl-rc -Wl0xE0000000 -Wl-rd -Wl0xE0000000 -o ScanCodeEcho.bin ScanCodeEcho.s

clean:
		rm -f *~ *.bin *.map *.exo
