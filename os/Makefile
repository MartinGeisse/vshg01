
include Configuration.make-include
include Macros.make-include

TOOL_DIR := $(SVN_BASE_DIR)/eco/tools/build/bin
PROJECT_BASE_DIR := $(SVN_BASE_DIR)/os
SRC_DIR := $(PROJECT_BASE_DIR)/src
TEST_SRC_DIR := $(PROJECT_BASE_DIR)/testsrc
BUILD_DIR := $(PROJECT_BASE_DIR)/build
TEST_BUILD_DIR := $(PROJECT_BASE_DIR)/testbuild

SAFEMKDIR := $(PROJECT_BASE_DIR)/safemkdir.sh

MODULES := $(notdir $(wildcard $(SRC_DIR)/*))
TEST_MODULES := $(notdir $(wildcard $(TEST_SRC_DIR)/*))

.PHONY: all
all: $(foreach module,$(MODULES) $(TEST_MODULES),Build$(module))

$(BUILD_DIR):
	$(SAFEMKDIR) $(BUILD_DIR)

$(TEST_BUILD_DIR):
	$(SAFEMKDIR) $(TEST_BUILD_DIR)

include $(foreach module,$(MODULES),$(SRC_DIR)/$(module)/Module.make-include)
include $(foreach module,$(TEST_MODULES),$(TEST_SRC_DIR)/$(module)/Module.make-include)

.PHONY: clean
clean:
	rm -rf $(BUILD_DIR)
	rm -rf $(TEST_BUILD_DIR)
