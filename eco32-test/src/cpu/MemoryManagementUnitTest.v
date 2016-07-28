`default_nettype none
`timescale 1ns / 1ps

/**
 * Testbench for the MMU.
 */
module MemoryManagementUnitTest ();

	/** includes **/
	`include "VUnit.inc.v"
	`include "Clock.inc.v"
	`include "../../eco32/src/cpu/MemoryManagementUnitConstants.inc.v"

	/** the module under test **/
	reg reset;
	reg[31:0] virtualAddress;
	reg affectRandomIndexCounter;
	wire tlbMiss;
	wire[31:0] physicalAddress;
	wire physicalAddressWriteAllowed;
	wire physicalAddressValid;
	reg randomizeEntryIndex;
	reg writeEntryEnable;
	reg[2:0] specialRegisterReadIndex;
	wire[31:0] specialRegisterReadValue;
	reg[31:0] specialRegisterExplicitWriteValue;
	reg tlbIndexWriteEnable;
	reg[`TLB_INDEX_DATA_SOURCE_WIDTH-1:0] tlbIndexDataSource;
	reg tlbEntryHighWriteEnable;
	reg[`TLB_ENTRY_HIGH_DATA_SOURCE_WIDTH-1:0] tlbEntryHighDataSource;
	reg tlbEntryLowWriteEnable;
	reg[`TLB_ENTRY_LOW_DATA_SOURCE_WIDTH-1:0] tlbEntryLowDataSource;
	reg tlbBadAddressWriteEnable;
	reg[`TLB_BAD_ADDRESS_DATA_SOURCE_WIDTH-1:0] tlbBadAddressDataSource;
	MemoryManagementUnit memoryManagementUnit(
		.clock(clock),
		.reset(reset),
		.virtualAddress(virtualAddress),
		.affectRandomIndexCounter(affectRandomIndexCounter),
		.tlbMiss(tlbMiss),
		.physicalAddress(physicalAddress),
		.physicalAddressWriteAllowed(physicalAddressWriteAllowed),
		.physicalAddressValid(physicalAddressValid),
		.randomizeEntryIndex(randomizeEntryIndex),
		.writeEntryEnable(writeEntryEnable),
		.specialRegisterReadIndex(specialRegisterReadIndex),
		.specialRegisterReadValue(specialRegisterReadValue),
		.specialRegisterExplicitWriteValue(specialRegisterExplicitWriteValue),
		.tlbIndexWriteEnable(tlbIndexWriteEnable),
		.tlbIndexDataSource(tlbIndexDataSource),
		.tlbEntryHighWriteEnable(tlbEntryHighWriteEnable),
		.tlbEntryHighDataSource(tlbEntryHighDataSource),
		.tlbEntryLowWriteEnable(tlbEntryLowWriteEnable),
		.tlbEntryLowDataSource(tlbEntryLowDataSource),
		.tlbBadAddressWriteEnable(tlbBadAddressWriteEnable),
		.tlbBadAddressDataSource(tlbBadAddressDataSource)
	);
	
	/** simulation **/
	reg[31:0] i;
	initial begin
		$dumpfile("cpu/MemoryManagementUnitTest.vcd");
		$dumpvars;

		/** initialize **/
		affectRandomIndexCounter <= 1'b0;
		writeEntryEnable <= 1'b0;
		tlbIndexWriteEnable <= 1'b0;
		tlbEntryHighWriteEnable <= 1'b0;
		tlbEntryLowWriteEnable <= 1'b0;
		tlbBadAddressWriteEnable <= 1'b0;
		for (i=0; i<32; i=i+1) begin
			memoryManagementUnit.tlbKeyMemory.keyArray[i] <= 20'h0;
			memoryManagementUnit.tlbValueMemory.valueArray[i] <= 20'h0;
		end

		/** reset **/
		#5 reset <= 1'b1;
		#20 reset <= 1'b0;

		/** ensure we have a valid reset state **/
		assert(memoryManagementUnit.randomIndexCounter == 5'd4, "initial random counter");

		/** try setting all special registers to 0 **/
		assertionContext <= "load registers to 0";
		specialRegisterExplicitWriteValue <= 32'h00000000;
		tlbIndexWriteEnable <= 1'b1;
		tlbIndexDataSource <= `TLB_INDEX_DATA_SOURCE_EXPLICIT;
		tlbEntryHighWriteEnable <= 1'b1;
		tlbEntryHighDataSource <= `TLB_ENTRY_HIGH_DATA_SOURCE_EXPLICIT;
		tlbEntryLowWriteEnable <= 1'b1;
		tlbEntryLowDataSource <= `TLB_ENTRY_LOW_DATA_SOURCE_EXPLICIT;
		tlbBadAddressWriteEnable <= 1'b1;
		tlbBadAddressDataSource <= `TLB_BAD_ADDRESS_DATA_SOURCE_EXPLICIT;
		#20;
		tlbIndexWriteEnable <= 1'b0;
		tlbEntryHighWriteEnable <= 1'b0;
		tlbEntryLowWriteEnable <= 1'b0;
		tlbBadAddressWriteEnable <= 1'b0;
		specialRegisterReadIndex <= 3'd1;
		#1 assert(specialRegisterReadValue == 32'h00000000, "index 1");
		specialRegisterReadIndex <= 3'd2;
		#1 assert(specialRegisterReadValue == 32'h00000000, "index 2");
		specialRegisterReadIndex <= 3'd3;
		#1 assert(specialRegisterReadValue == 32'h00000000, "index 3");
		specialRegisterReadIndex <= 3'd4;
		#1 assert(specialRegisterReadValue == 32'h00000000, "index 4");
		#16;
		
		/** try setting all special registers to all-1 **/
		assertionContext <= "load registers to all-1";
		specialRegisterExplicitWriteValue <= 32'hffffffff;
		tlbIndexWriteEnable <= 1'b1;
		tlbIndexDataSource <= `TLB_INDEX_DATA_SOURCE_EXPLICIT;
		tlbEntryHighWriteEnable <= 1'b1;
		tlbEntryHighDataSource <= `TLB_ENTRY_HIGH_DATA_SOURCE_EXPLICIT;
		tlbEntryLowWriteEnable <= 1'b1;
		tlbEntryLowDataSource <= `TLB_ENTRY_LOW_DATA_SOURCE_EXPLICIT;
		tlbBadAddressWriteEnable <= 1'b1;
		tlbBadAddressDataSource <= `TLB_BAD_ADDRESS_DATA_SOURCE_EXPLICIT;
		#20;
		tlbIndexWriteEnable <= 1'b0;
		tlbEntryHighWriteEnable <= 1'b0;
		tlbEntryLowWriteEnable <= 1'b0;
		tlbBadAddressWriteEnable <= 1'b0;
		specialRegisterReadIndex <= 3'd1;
		#1 assert(specialRegisterReadValue == 32'h0000001f, "index 1");
		specialRegisterReadIndex <= 3'd2;
		#1 assert(specialRegisterReadValue == 32'hfffff000, "index 2");
		specialRegisterReadIndex <= 3'd3;
		#1 assert(specialRegisterReadValue == 32'hfffff003, "index 3");
		specialRegisterReadIndex <= 3'd4;
		#1 assert(specialRegisterReadValue == 32'hffffffff, "index 4");
		#16;
		
		/** try a direct-mapped address, and check the results and new state **/
		assertionContext <= "simple direct-mapped";
		virtualAddress <= 32'hc123f456;
		affectRandomIndexCounter <= 1'b0;
		#20;
		assert(virtualAddress == 32'hc123f456, "virtualAddress");
		assert(affectRandomIndexCounter == 1'b0, "affectRandomIndexCounter");
		assert(memoryManagementUnit.virtualAddressInputIsDirectMapped, "memoryManagementUnit.virtualAddressInputIsDirectMapped");
		assert(~tlbMiss, "tlbMiss");
		assert(physicalAddress == 32'h0123f456, "physicalAddress");
		assert(physicalAddressWriteAllowed, "physicalAddressWriteAllowed");
		assert(physicalAddressValid, "physicalAddressValid");
		assert(memoryManagementUnit.randomIndexCounter == 5'd4, "memoryManagementUnit.randomIndexCounter");
		assert(memoryManagementUnit.tlbKeyMemory.keyComparatorResults == 32'b00000000000000000000000000000000, "memoryManagementUnit.tlbKeyMemory.keyComparatorResults");
		assert(~memoryManagementUnit.found, "memoryManagementUnit.found");

		/**
		 * Try a direct-mapped address, this time with the random counter affect register
		 * enabled. This should not have any effect for a direct-mapped address.
		 */
		assertionContext <= "another direct-mapped with random counter";
		virtualAddress <= 32'hef01abcd;
		affectRandomIndexCounter <= 1'b1;
		#20;
		assert(virtualAddress == 32'hef01abcd, "virtualAddress");
		assert(affectRandomIndexCounter == 1'b1, "affectRandomIndexCounter");
		assert(memoryManagementUnit.virtualAddressInputIsDirectMapped, "memoryManagementUnit.virtualAddressInputIsDirectMapped");
		assert(~tlbMiss, "tlbMiss");
		assert(physicalAddress == 32'h2f01abcd, "physicalAddress");
		assert(physicalAddressWriteAllowed, "physicalAddressWriteAllowed");
		assert(physicalAddressValid, "physicalAddressValid");
		assert(memoryManagementUnit.randomIndexCounter == 5'd4, "memoryManagementUnit.randomIndexCounter");
		assert(memoryManagementUnit.tlbKeyMemory.keyComparatorResults == 32'b00000000000000000000000000000000, "memoryManagementUnit.tlbKeyMemory.keyComparatorResults");
		assert(~memoryManagementUnit.found, "memoryManagementUnit.found");

		/**
		 * Write a TLB entry that looks like it could map a direct address. However,
		 * mapping of direct addresses should not find such an entry. Since the address
		 * is not page-mapped, the random counter is not affected.
		 */
		assertionContext <= "direct-mapped with random counter and matching TLB entry";
		memoryManagementUnit.tlbKeyMemory.keyArray[5] <= 20'hddeef;
		memoryManagementUnit.tlbValueMemory.valueArray[5] <= 22'b00000000000000001000_00;
		virtualAddress <= 32'hddeef012;
		affectRandomIndexCounter <= 1'b1;
		#20;
		assert(virtualAddress == 32'hddeef012, "virtualAddress");
		assert(affectRandomIndexCounter == 1'b1, "affectRandomIndexCounter");
		assert(memoryManagementUnit.virtualAddressInputIsDirectMapped, "memoryManagementUnit.virtualAddressInputIsDirectMapped");
		assert(~tlbMiss, "tlbMiss");
		assert(physicalAddress == 32'h1deef012, "physicalAddress");
		assert(physicalAddressWriteAllowed, "physicalAddressWriteAllowed");
		assert(physicalAddressValid, "physicalAddressValid");
		assert(memoryManagementUnit.randomIndexCounter == 5'd4, "memoryManagementUnit.randomIndexCounter");
		assert(memoryManagementUnit.tlbKeyMemory.keyComparatorResults == 32'b00000000000000000000000000100000, "memoryManagementUnit.tlbKeyMemory.keyComparatorResults");
		assert(memoryManagementUnit.found, "memoryManagementUnit.found");
		assert(memoryManagementUnit.foundIndex == 5'd5, "memoryManagementUnit.found");

		/**
		 * Now try an access that uses a mapped address that can be found.
		 */
		assertionContext <= "first TLB-mapped address (don't affect random counter)";
		memoryManagementUnit.tlbKeyMemory.keyArray[7] <= 20'h01234;
		memoryManagementUnit.tlbValueMemory.valueArray[7] <= {20'h55555, 2'b11};
		virtualAddress <= 32'h01234987;
		affectRandomIndexCounter <= 1'b0;
		#20;
		assert(virtualAddress == 32'h01234987, "virtualAddress");
		assert(affectRandomIndexCounter == 1'b0, "affectRandomIndexCounter");
		assert(~memoryManagementUnit.virtualAddressInputIsDirectMapped, "memoryManagementUnit.virtualAddressInputIsDirectMapped");
		assert(~tlbMiss, "tlbMiss");
		assert(physicalAddress == 32'h55555987, "physicalAddress");
		assert(physicalAddressWriteAllowed, "physicalAddressWriteAllowed");
		assert(physicalAddressValid, "physicalAddressValid");
		assert(memoryManagementUnit.randomIndexCounter == 5'd4, "memoryManagementUnit.randomIndexCounter");
		assert(memoryManagementUnit.tlbKeyMemory.keyComparatorResults == 32'b00000000000000000000000010000000, "memoryManagementUnit.tlbKeyMemory.keyComparatorResults");
		assert(memoryManagementUnit.found, "memoryManagementUnit.found");
		assert(memoryManagementUnit.foundIndex == 5'd7, "memoryManagementUnit.found");

		/**
		 * check the valid flag of the TLB entry
		 */
		assertionContext <= "first TLB-mapped address (don't affect random counter, invalid)";
		memoryManagementUnit.tlbKeyMemory.keyArray[7] <= 20'h01234;
		memoryManagementUnit.tlbValueMemory.valueArray[7] <= {20'h55555, 2'b10};
		virtualAddress <= 32'h01234987;
		affectRandomIndexCounter <= 1'b0;
		#20;
		assert(virtualAddress == 32'h01234987, "virtualAddress");
		assert(affectRandomIndexCounter == 1'b0, "affectRandomIndexCounter");
		assert(~memoryManagementUnit.virtualAddressInputIsDirectMapped, "memoryManagementUnit.virtualAddressInputIsDirectMapped");
		assert(~tlbMiss, "tlbMiss");
		assert(physicalAddress == 32'h55555987, "physicalAddress");
		assert(physicalAddressWriteAllowed, "physicalAddressWriteAllowed");
		assert(~physicalAddressValid, "physicalAddressValid");
		assert(memoryManagementUnit.randomIndexCounter == 5'd4, "memoryManagementUnit.randomIndexCounter");
		assert(memoryManagementUnit.tlbKeyMemory.keyComparatorResults == 32'b00000000000000000000000010000000, "memoryManagementUnit.tlbKeyMemory.keyComparatorResults");
		assert(memoryManagementUnit.found, "memoryManagementUnit.found");
		assert(memoryManagementUnit.foundIndex == 5'd7, "memoryManagementUnit.found");
		 
		/**
		 * check the write flag of the TLB entry
		 */
		assertionContext <= "first TLB-mapped address (don't affect random counter, no-write)";
		memoryManagementUnit.tlbKeyMemory.keyArray[7] <= 20'h01234;
		memoryManagementUnit.tlbValueMemory.valueArray[7] <= {20'h55555, 2'b01};
		virtualAddress <= 32'h01234987;
		affectRandomIndexCounter <= 1'b0;
		#20;
		assert(virtualAddress == 32'h01234987, "virtualAddress");
		assert(affectRandomIndexCounter == 1'b0, "affectRandomIndexCounter");
		assert(~memoryManagementUnit.virtualAddressInputIsDirectMapped, "memoryManagementUnit.virtualAddressInputIsDirectMapped");
		assert(~tlbMiss, "tlbMiss");
		assert(physicalAddress == 32'h55555987, "physicalAddress");
		assert(~physicalAddressWriteAllowed, "physicalAddressWriteAllowed");
		assert(physicalAddressValid, "physicalAddressValid");
		assert(memoryManagementUnit.randomIndexCounter == 5'd4, "memoryManagementUnit.randomIndexCounter");
		assert(memoryManagementUnit.tlbKeyMemory.keyComparatorResults == 32'b00000000000000000000000010000000, "memoryManagementUnit.tlbKeyMemory.keyComparatorResults");
		assert(memoryManagementUnit.found, "memoryManagementUnit.found");
		assert(memoryManagementUnit.foundIndex == 5'd7, "memoryManagementUnit.found");
		 
		/**
		 * check the valid and write flags of the TLB entry
		 */
		assertionContext <= "first TLB-mapped address (don't affect random counter, no-write, invalid)";
		memoryManagementUnit.tlbKeyMemory.keyArray[7] <= 20'h01234;
		memoryManagementUnit.tlbValueMemory.valueArray[7] <= {20'h55555, 2'b00};
		virtualAddress <= 32'h01234987;
		affectRandomIndexCounter <= 1'b0;
		#20;
		assert(virtualAddress == 32'h01234987, "virtualAddress");
		assert(affectRandomIndexCounter == 1'b0, "affectRandomIndexCounter");
		assert(~memoryManagementUnit.virtualAddressInputIsDirectMapped, "memoryManagementUnit.virtualAddressInputIsDirectMapped");
		assert(~tlbMiss, "tlbMiss");
		assert(physicalAddress == 32'h55555987, "physicalAddress");
		assert(~physicalAddressWriteAllowed, "physicalAddressWriteAllowed");
		assert(~physicalAddressValid, "physicalAddressValid");
		assert(memoryManagementUnit.randomIndexCounter == 5'd4, "memoryManagementUnit.randomIndexCounter");
		assert(memoryManagementUnit.tlbKeyMemory.keyComparatorResults == 32'b00000000000000000000000010000000, "memoryManagementUnit.tlbKeyMemory.keyComparatorResults");
		assert(memoryManagementUnit.found, "memoryManagementUnit.found");
		assert(memoryManagementUnit.foundIndex == 5'd7, "memoryManagementUnit.found");
		 
		/**
		 * check the effect on the random counter
		 */
		assertionContext <= "first TLB-mapped address (affect random counter, no-write, invalid)";
		memoryManagementUnit.tlbKeyMemory.keyArray[7] <= 20'h01234;
		memoryManagementUnit.tlbValueMemory.valueArray[7] <= {20'h55555, 2'b00};
		virtualAddress <= 32'h01234987;
		affectRandomIndexCounter <= 1'b1;
		#20;
		assert(virtualAddress == 32'h01234987, "virtualAddress");
		assert(affectRandomIndexCounter == 1'b1, "affectRandomIndexCounter");
		assert(~memoryManagementUnit.virtualAddressInputIsDirectMapped, "memoryManagementUnit.virtualAddressInputIsDirectMapped");
		assert(~tlbMiss, "tlbMiss");
		assert(physicalAddress == 32'h55555987, "physicalAddress");
		assert(~physicalAddressWriteAllowed, "physicalAddressWriteAllowed");
		assert(~physicalAddressValid, "physicalAddressValid");
		assert(memoryManagementUnit.randomIndexCounter == 5'd5, "memoryManagementUnit.randomIndexCounter");
		assert(memoryManagementUnit.tlbKeyMemory.keyComparatorResults == 32'b00000000000000000000000010000000, "memoryManagementUnit.tlbKeyMemory.keyComparatorResults");
		assert(memoryManagementUnit.found, "memoryManagementUnit.found");
		assert(memoryManagementUnit.foundIndex == 5'd7, "memoryManagementUnit.found");

		/**
		 * try an address that causes a TLB miss (this should increase the random counter nevertheless)
		 */
		assertionContext <= "TLB miss";
		memoryManagementUnit.tlbKeyMemory.keyArray[7] <= 20'h01234;
		memoryManagementUnit.tlbValueMemory.valueArray[7] <= {20'h55555, 2'b00};
		virtualAddress <= 32'h56565656;
		affectRandomIndexCounter <= 1'b1;
		#20;
		assert(virtualAddress == 32'h56565656, "virtualAddress");
		assert(affectRandomIndexCounter == 1'b1, "affectRandomIndexCounter");
		assert(~memoryManagementUnit.virtualAddressInputIsDirectMapped, "memoryManagementUnit.virtualAddressInputIsDirectMapped");
		assert(tlbMiss, "tlbMiss");
		assert(memoryManagementUnit.randomIndexCounter == 5'd6, "memoryManagementUnit.randomIndexCounter");
		assert(memoryManagementUnit.tlbKeyMemory.keyComparatorResults == 32'b00000000000000000000000000000000, "memoryManagementUnit.tlbKeyMemory.keyComparatorResults");
		assert(~memoryManagementUnit.found, "memoryManagementUnit.found");

		/**
		 * try an address that matches two entries. The first one should take precedence.
		 */
		assertionContext <= "ambiguous mapping";
		memoryManagementUnit.tlbKeyMemory.keyArray[7] <= 20'h01234;
		memoryManagementUnit.tlbValueMemory.valueArray[7] <= {20'h77777, 2'b00};
		memoryManagementUnit.tlbKeyMemory.keyArray[8] <= 20'h01234;
		memoryManagementUnit.tlbValueMemory.valueArray[8] <= {20'h44444, 2'b11};
		virtualAddress <= 32'h01234987;
		affectRandomIndexCounter <= 1'b0;
		#20;
		assert(virtualAddress == 32'h01234987, "virtualAddress");
		assert(affectRandomIndexCounter == 1'b0, "affectRandomIndexCounter");
		assert(~memoryManagementUnit.virtualAddressInputIsDirectMapped, "memoryManagementUnit.virtualAddressInputIsDirectMapped");
		assert(~tlbMiss, "tlbMiss");
		assert(physicalAddress == 32'h77777987, "physicalAddress");
		assert(~physicalAddressWriteAllowed, "physicalAddressWriteAllowed");
		assert(~physicalAddressValid, "physicalAddressValid");
		assert(memoryManagementUnit.randomIndexCounter == 5'd6, "memoryManagementUnit.randomIndexCounter");
		assert(memoryManagementUnit.tlbKeyMemory.keyComparatorResults == 32'b00000000000000000000000110000000, "memoryManagementUnit.tlbKeyMemory.keyComparatorResults");
		assert(memoryManagementUnit.found, "memoryManagementUnit.found");
		assert(memoryManagementUnit.foundIndex == 5'd7, "memoryManagementUnit.found");
		
		/**
		 * Test write index randomization.
		 */
		assertionContext <= "write index randomization";
		memoryManagementUnit.tlbIndexRegister <= 5'd15;
		randomizeEntryIndex <= 1'b0;
		#1 assert(memoryManagementUnit.effectiveEntryAccessIndex == 5'd15, "not randomized");
		randomizeEntryIndex <= 1'b1;
		#1 assert(memoryManagementUnit.effectiveEntryAccessIndex == 5'd6, "randomized");
		randomizeEntryIndex <= 1'b0;
		#1 assert(memoryManagementUnit.effectiveEntryAccessIndex == 5'd15, "non-randomized again");
		
		// TODO: finish unit tests. These are currently not that important because the CPU will be
		// tested as a whole anyway, and the MMU is not used in another context. It would be
		// nice nevertheless, and could be done as part of a test case documentation overhaul.
		 
		/** done **/
		#100 $finish;
		
	end
		
endmodule
