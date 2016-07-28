
TOOLDIR := ~/Desktop/Schaffe/workspace/original-eco-tools
SOURCE_FILES := $(notdir $(wildcard *.s))
SOURCE_FILE_BASE_NAMES := $(basename $(SOURCE_FILES))
BINARY_FILES := $(foreach sourceFileBaseName,$(SOURCE_FILE_BASE_NAMES),$(sourceFileBaseName).bin)

.PHONY: all
all: $(BINARY_FILES)

%.bin: %.s
	$(TOOLDIR)/lcc -I. -A -Wo-kernel -Wl-m -Wl$*.map -Wl-h -Wl-rc -Wl0xE0000000 -Wl-rd -Wl0xE0000000 -o $@ $<

clean:
		rm -f *~ *.bin *.map *.exo
