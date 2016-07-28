`default_nettype none
`timescale 1ns / 1ps

/**
 * Testbench for the MMU.
 */
module CpuMmuTest ();

	/** includes **/
	`include "VUnit.inc.v"
	`include "Clock.inc.v"
	`include "../../eco32/src/cpu/CpuConstants.inc.v"
	`include "cpu/CpuTestHelper.inc.v"

	/** simulation **/
	reg[31:0] i;
	initial begin
		$dumpfile("cpu/CpuMmuTest.vcd");
		$dumpvars;
		
		/** initialization **/
		busReadData <= 32'd0;
		busWait <= 1'b0;
		interrupts <= 16'b0000000000000000;
		
		/** reset **/
		#5 reset <= 1'b1;
		#20 reset <= 1'b0;
		#20 waitForBusAccess();
		cpu.memoryManagementUnit.tlbIndexRegister <= 5'h0;
		cpu.memoryManagementUnit.tlbInvalidIndexRegister <= 1'b1;
		cpu.memoryManagementUnit.tlbEntryHighRegister <= 20'h0;
		cpu.memoryManagementUnit.tlbEntryLowRegister <= 22'h0;
		cpu.memoryManagementUnit.tlbBadAddressRegister <= 32'h0;
		
		/** check initial MMU state after reset **/
		assertionContext = "initial state";
		assert(cpu.memoryManagementUnit.randomIndexCounter == 5'd4, "random counter");

		/** test direct-mapped LDB **/
		assertionContext = "test direct-mapped LDB";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'hc1234567;
		busReadData <= buildRRIInstruction(6'h33, 5'd31, 5'd0, 16'h0000);
		#20 waitForBusAccess();
		assert(busAddress == 32'h01234567, "LDB bus address");
		assert(cpu.memoryManagementUnit.tlbIndexRegister == 5'h0, "cpu.memoryManagementUnit.tlbIndexRegister");
		assert(cpu.memoryManagementUnit.tlbInvalidIndexRegister == 1'b1, "cpu.memoryManagementUnit.tlbInvalidIndexRegister");
		assert(cpu.memoryManagementUnit.tlbEntryHighRegister == 20'h0, "cpu.memoryManagementUnit.tlbEntryHighRegister");
		assert(cpu.memoryManagementUnit.tlbEntryLowRegister == 22'h0, "cpu.memoryManagementUnit.tlbEntryLowRegister");
		assert(cpu.memoryManagementUnit.tlbBadAddressRegister == 32'h0, "cpu.memoryManagementUnit.tlbBadAddressRegister");
		assert(cpu.memoryManagementUnit.randomIndexCounter == 5'd4, "random counter");
		#20 waitForBusAccess();

		/** test page-mapped LDB **/
		assertionContext = "test page-mapped LDB";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'h12345abc;
		busReadData <= buildRRIInstruction(6'h33, 5'd31, 5'd0, 16'h0000);
		#20 waitForBusAccess();
		assert(busAddress == 32'h55555abc, "LDB bus address");
		assert(cpu.memoryManagementUnit.tlbIndexRegister == 5'h0, "cpu.memoryManagementUnit.tlbIndexRegister");
		assert(cpu.memoryManagementUnit.tlbInvalidIndexRegister == 1'b1, "cpu.memoryManagementUnit.tlbInvalidIndexRegister");
		assert(cpu.memoryManagementUnit.tlbEntryHighRegister == 20'h0, "cpu.memoryManagementUnit.tlbEntryHighRegister");
		assert(cpu.memoryManagementUnit.tlbEntryLowRegister == 22'h0, "cpu.memoryManagementUnit.tlbEntryLowRegister");
		assert(cpu.memoryManagementUnit.tlbBadAddressRegister == 32'h0, "cpu.memoryManagementUnit.tlbBadAddressRegister");
		assert(cpu.memoryManagementUnit.randomIndexCounter == 5'd5, "random counter");
		#20 waitForBusAccess();

		/** test page-mapped LDB with multiple matches **/
		assertionContext = "test page-mapped LDB with multiple matches";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'h2468adef;
		busReadData <= buildRRIInstruction(6'h33, 5'd31, 5'd0, 16'h0000);
		#20 waitForBusAccess();
		assert(busAddress == 32'h77777def, "LDB bus address");
		assert(cpu.memoryManagementUnit.randomIndexCounter == 5'd6, "random counter");
		#20 waitForBusAccess();

		/** test direct-mapped STB **/
		assertionContext = "test direct-mapped STB";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'hc1234567;
		busReadData <= buildRRIInstruction(6'h37, 5'd31, 5'd0, 16'h0000);
		#20 waitForBusAccess();
		assert(busAddress == 32'h01234567, "STB bus address");
		assert(cpu.memoryManagementUnit.tlbIndexRegister == 5'h0, "cpu.memoryManagementUnit.tlbIndexRegister");
		assert(cpu.memoryManagementUnit.tlbInvalidIndexRegister == 1'b1, "cpu.memoryManagementUnit.tlbInvalidIndexRegister");
		assert(cpu.memoryManagementUnit.tlbEntryHighRegister == 20'h0, "cpu.memoryManagementUnit.tlbEntryHighRegister");
		assert(cpu.memoryManagementUnit.tlbEntryLowRegister == 22'h0, "cpu.memoryManagementUnit.tlbEntryLowRegister");
		assert(cpu.memoryManagementUnit.tlbBadAddressRegister == 32'h0, "cpu.memoryManagementUnit.tlbBadAddressRegister");
		assert(cpu.memoryManagementUnit.randomIndexCounter == 5'd6, "random counter");
		#20 waitForBusAccess();

		/** test page-mapped STB **/
		assertionContext = "test page-mapped STB";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'h12345abc;
		busReadData <= buildRRIInstruction(6'h37, 5'd31, 5'd0, 16'h0000);
		#20 waitForBusAccess();
		assert(busAddress == 32'h55555abc, "STB bus address");
		assert(cpu.memoryManagementUnit.randomIndexCounter == 5'd7, "random counter");
		#20 waitForBusAccess();

		/** test page-mapped STB with multiple matches **/
		assertionContext = "test page-mapped STB with multiple matches";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'h2468adef;
		busReadData <= buildRRIInstruction(6'h37, 5'd31, 5'd0, 16'h0000);
		#20 waitForBusAccess();
		assert(busAddress == 32'h77777def, "STB bus address");
		assert(cpu.memoryManagementUnit.tlbIndexRegister == 5'h0, "cpu.memoryManagementUnit.tlbIndexRegister");
		assert(cpu.memoryManagementUnit.tlbInvalidIndexRegister == 1'b1, "cpu.memoryManagementUnit.tlbInvalidIndexRegister");
		assert(cpu.memoryManagementUnit.tlbEntryHighRegister == 20'h0, "cpu.memoryManagementUnit.tlbEntryHighRegister");
		assert(cpu.memoryManagementUnit.tlbEntryLowRegister == 22'h0, "cpu.memoryManagementUnit.tlbEntryLowRegister");
		assert(cpu.memoryManagementUnit.tlbBadAddressRegister == 32'h0, "cpu.memoryManagementUnit.tlbBadAddressRegister");
		assert(cpu.memoryManagementUnit.randomIndexCounter == 5'd8, "random counter");
		#20 waitForBusAccess();
		
		/** now change the PC to a page-mapped address and see if it works **/
		assertionContext = "test page-mapped instruction fetch";
		busReadData <= 32'h00000000;
		cpu.programCounterRegister.programCounter <= 32'h12345024;
		#20 waitForBusAccess();
		assert(cpu.state == `STATE_FETCH_INSTRUCTION, "instruction fetch state");
		assert(busAddress == 32'h55555024, "instruction fetch address");
		assert(cpu.memoryManagementUnit.tlbIndexRegister == 5'h0, "cpu.memoryManagementUnit.tlbIndexRegister");
		assert(cpu.memoryManagementUnit.tlbInvalidIndexRegister == 1'b1, "cpu.memoryManagementUnit.tlbInvalidIndexRegister");
		assert(cpu.memoryManagementUnit.tlbEntryHighRegister == 20'h0, "cpu.memoryManagementUnit.tlbEntryHighRegister");
		assert(cpu.memoryManagementUnit.tlbEntryLowRegister == 22'h0, "cpu.memoryManagementUnit.tlbEntryLowRegister");
		assert(cpu.memoryManagementUnit.tlbBadAddressRegister == 32'h0, "cpu.memoryManagementUnit.tlbBadAddressRegister");
		assert(cpu.memoryManagementUnit.randomIndexCounter == 5'd9, "random counter");

		/** now change the PC to a page-mapped address with two matches and see if it works **/
		assertionContext = "test page-mapped instruction fetch (two matches)";
		busReadData <= 32'h00000000;
		cpu.programCounterRegister.programCounter <= 32'h2468a440;
		#20 waitForBusAccess();
		assert(cpu.state == `STATE_FETCH_INSTRUCTION, "instruction fetch state");
		assert(busAddress == 32'h77777440, "instruction fetch address");
		assert(cpu.memoryManagementUnit.tlbIndexRegister == 5'h0, "cpu.memoryManagementUnit.tlbIndexRegister");
		assert(cpu.memoryManagementUnit.tlbInvalidIndexRegister == 1'b1, "cpu.memoryManagementUnit.tlbInvalidIndexRegister");
		assert(cpu.memoryManagementUnit.tlbEntryHighRegister == 20'h0, "cpu.memoryManagementUnit.tlbEntryHighRegister");
		assert(cpu.memoryManagementUnit.tlbEntryLowRegister == 22'h0, "cpu.memoryManagementUnit.tlbEntryLowRegister");
		assert(cpu.memoryManagementUnit.tlbBadAddressRegister == 32'h0, "cpu.memoryManagementUnit.tlbBadAddressRegister");
		assert(cpu.memoryManagementUnit.randomIndexCounter == 5'd10, "random counter");
		
		/** test random counter wrap-around **/
		assertionContext = "test random counter wrap-around";
		busReadData <= 32'h00000000;
		for (i = 5'd10; i < 5'd31; i = i + 1) begin
			#20 waitForBusAccess();
		end
		assert(cpu.memoryManagementUnit.randomIndexCounter == 5'd31, "random counter 31");
		#20 waitForBusAccess();
		assert(cpu.memoryManagementUnit.randomIndexCounter == 5'd4, "random counter 4");

		/**
		 * test page-mapped LDB from a page-mapped PC location. Note that the random counter increment from
		 * the instruction fetch has already occured above.
		 */
		assertionContext = "test page-mapped LDB from a page-mapped PC location";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'h12345abc;
		busReadData <= buildRRIInstruction(6'h33, 5'd31, 5'd0, 16'h0000);
		#20 waitForBusAccess();
		assert(busAddress == 32'h55555abc, "LDB bus address");
		assert(cpu.memoryManagementUnit.tlbIndexRegister == 5'h0, "cpu.memoryManagementUnit.tlbIndexRegister");
		assert(cpu.memoryManagementUnit.tlbInvalidIndexRegister == 1'b1, "cpu.memoryManagementUnit.tlbInvalidIndexRegister");
		assert(cpu.memoryManagementUnit.tlbEntryHighRegister == 20'h0, "cpu.memoryManagementUnit.tlbEntryHighRegister");
		assert(cpu.memoryManagementUnit.tlbEntryLowRegister == 22'h0, "cpu.memoryManagementUnit.tlbEntryLowRegister");
		assert(cpu.memoryManagementUnit.tlbBadAddressRegister == 32'h0, "cpu.memoryManagementUnit.tlbBadAddressRegister");
		assert(cpu.memoryManagementUnit.randomIndexCounter == 5'd5, "random counter");
		cpu.programCounterRegister.programCounter <= 32'hdcdcdcdc;
		#20 waitForBusAccess();

		/** test page-mapped LDB with write bit cleared **/
		assertionContext = "test page-mapped LDB with write bit cleared";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'h36936000;
		busReadData <= buildRRIInstruction(6'h33, 5'd31, 5'd0, 16'h0000);
		#20 waitForBusAccess();
		assert(busAddress == 32'haaaaa000, "LDB bus address");
		assert(cpu.memoryManagementUnit.tlbIndexRegister == 5'h0, "cpu.memoryManagementUnit.tlbIndexRegister");
		assert(cpu.memoryManagementUnit.tlbInvalidIndexRegister == 1'b1, "cpu.memoryManagementUnit.tlbInvalidIndexRegister");
		assert(cpu.memoryManagementUnit.tlbEntryHighRegister == 20'h0, "cpu.memoryManagementUnit.tlbEntryHighRegister");
		assert(cpu.memoryManagementUnit.tlbEntryLowRegister == 22'h0, "cpu.memoryManagementUnit.tlbEntryLowRegister");
		assert(cpu.memoryManagementUnit.tlbBadAddressRegister == 32'h0, "cpu.memoryManagementUnit.tlbBadAddressRegister");
		assert(cpu.memoryManagementUnit.randomIndexCounter == 5'd6, "random counter");
		#20 waitForBusAccess();
		
		/** done **/
		#100 $finish;
	end

endmodule
