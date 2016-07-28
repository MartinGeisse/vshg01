`default_nettype none
`timescale 1ns / 1ps

/**
 * Testbench for TLB handling instructions.
 */
module CPUTlbInstructionsTest ();

	/** includes **/
	`include "VUnit.inc.v"
	`include "Clock.inc.v"
	`include "../../eco32/src/cpu/CpuConstants.inc.v"
	`include "cpu/CpuTestHelper.inc.v"

	/** simulation **/
	initial begin
		$dumpfile("cpu/CpuTlbInstructionsTest.vcd");
		$dumpvars;
		
		/** initialization **/
		busReadData <= 32'd0;
		busWait <= 1'b0;
		interrupts <= 16'b0000000000000000;
		
		/** reset **/
		#5 reset <= 1'b1;
		#20 reset <= 1'b0;
		#20 waitForBusAccess();

		/*****************************************************************************************************/

		/** check initial MMU state after reset **/
		assertionContext = "initial state";
		assert(cpu.memoryManagementUnit.randomIndexCounter == 5'd4, "random counter");
		
		/** test TBS with one match **/
		assertionContext = "test TBS (one)";
		busReadData <= buildJInstruction(6'h3a, 26'd0);
		cpu.memoryManagementUnit.tlbEntryHighRegister <= 20'h12345;
		cpu.memoryManagementUnit.tlbEntryLowRegister <= {20'hefefe, 2'b11};
		cpu.memoryManagementUnit.tlbIndexRegister <= 5'd13;
		cpu.memoryManagementUnit.tlbInvalidIndexRegister <= 1'b1;
		#20 waitForBusAccess();
		assert(cpu.memoryManagementUnit.tlbEntryHighRegister == 20'h12345, "cpu.memoryManagementUnit.tlbEntryHighRegister");
		assert(cpu.memoryManagementUnit.tlbEntryLowRegister == {20'hefefe, 2'b11}, "cpu.memoryManagementUnit.tlbEntryLowRegister");
		assert(cpu.memoryManagementUnit.tlbIndexRegister == 5'd5, "cpu.memoryManagementUnit.tlbIndexRegister");
		assert(cpu.memoryManagementUnit.tlbInvalidIndexRegister == 1'b0, "cpu.memoryManagementUnit.tlbIndexInvalidRegister");
		assert(cpu.memoryManagementUnit.randomIndexCounter == 5'd4, "random counter");

		/** test TBS with two matches **/
		assertionContext = "test TBS (two)";
		busReadData <= buildJInstruction(6'h3a, 26'd0);
		cpu.memoryManagementUnit.tlbEntryHighRegister <= 20'h2468a;
		cpu.memoryManagementUnit.tlbEntryLowRegister <= {20'hefefe, 2'b11};
		cpu.memoryManagementUnit.tlbIndexRegister <= 5'd13;
		cpu.memoryManagementUnit.tlbInvalidIndexRegister <= 1'b1;
		#20 waitForBusAccess();
		assert(cpu.memoryManagementUnit.tlbEntryHighRegister == 20'h2468a, "cpu.memoryManagementUnit.tlbEntryHighRegister");
		assert(cpu.memoryManagementUnit.tlbEntryLowRegister == {20'hefefe, 2'b11}, "cpu.memoryManagementUnit.tlbEntryLowRegister");
		assert(cpu.memoryManagementUnit.tlbIndexRegister == 5'd6, "cpu.memoryManagementUnit.tlbIndexRegister");
		assert(cpu.memoryManagementUnit.tlbInvalidIndexRegister == 1'b0, "cpu.memoryManagementUnit.tlbIndexInvalidRegister");
		assert(cpu.memoryManagementUnit.randomIndexCounter == 5'd4, "random counter");

		/** test TBS with no matches **/
		assertionContext = "test TBS (zero)";
		busReadData <= buildJInstruction(6'h3a, 26'd0);
		cpu.memoryManagementUnit.tlbEntryHighRegister <= 20'h2468b;
		cpu.memoryManagementUnit.tlbEntryLowRegister <= {20'hefefe, 2'b11};
		cpu.memoryManagementUnit.tlbIndexRegister <= 5'd13;
		cpu.memoryManagementUnit.tlbInvalidIndexRegister <= 1'b0;
		#20 waitForBusAccess();
		assert(cpu.memoryManagementUnit.tlbEntryHighRegister == 20'h2468b, "cpu.memoryManagementUnit.tlbEntryHighRegister");
		assert(cpu.memoryManagementUnit.tlbEntryLowRegister == {20'hefefe, 2'b11}, "cpu.memoryManagementUnit.tlbEntryLowRegister");
		assert(cpu.memoryManagementUnit.tlbIndexRegister == 5'd0, "cpu.memoryManagementUnit.tlbIndexRegister");
		assert(cpu.memoryManagementUnit.tlbInvalidIndexRegister == 1'b1, "cpu.memoryManagementUnit.tlbIndexInvalidRegister");
		assert(cpu.memoryManagementUnit.randomIndexCounter == 5'd4, "random counter");

		/*****************************************************************************************************/

		/** test TBWR **/
		assertionContext = "test TBWR";
		busReadData <= buildJInstruction(6'h3b, 26'd0);
		cpu.memoryManagementUnit.randomIndexCounter <= 5'd17;
		cpu.memoryManagementUnit.tlbEntryHighRegister <= 20'h2468b;
		cpu.memoryManagementUnit.tlbEntryLowRegister <= {20'hefefe, 2'b11};
		cpu.memoryManagementUnit.tlbIndexRegister <= 5'd13;
		cpu.memoryManagementUnit.tlbInvalidIndexRegister <= 1'b1;
		#20 waitForBusAccess();
		assert(cpu.memoryManagementUnit.tlbEntryHighRegister == 20'h2468b, "cpu.memoryManagementUnit.tlbEntryHighRegister");
		assert(cpu.memoryManagementUnit.tlbEntryLowRegister == {20'hefefe, 2'b11}, "cpu.memoryManagementUnit.tlbEntryLowRegister");
		assert(cpu.memoryManagementUnit.tlbIndexRegister == 5'd13, "cpu.memoryManagementUnit.tlbIndexRegister");
		assert(cpu.memoryManagementUnit.tlbInvalidIndexRegister == 1'b1, "cpu.memoryManagementUnit.tlbIndexInvalidRegister");
		assert(cpu.memoryManagementUnit.tlbKeyMemory.keyArray[13] == 20'h00000, "check key at index");
		assert(cpu.memoryManagementUnit.tlbValueMemory.valueArray[13] == {20'h00000, 2'b00}, "check value at index");
		assert(cpu.memoryManagementUnit.tlbKeyMemory.keyArray[17] == 20'h2468b, "check written key");
		assert(cpu.memoryManagementUnit.tlbValueMemory.valueArray[17] == {20'hefefe, 2'b11}, "check written value");
		assert(cpu.memoryManagementUnit.randomIndexCounter == 5'd17, "random counter");

		/** test TBRI **/
		assertionContext = "test TBRI";
		busReadData <= buildJInstruction(6'h3c, 26'd0);
		cpu.memoryManagementUnit.randomIndexCounter <= 5'd17;
		cpu.memoryManagementUnit.tlbEntryHighRegister <= 20'h2468b;
		cpu.memoryManagementUnit.tlbEntryLowRegister <= {20'hefefe, 2'b11};
		cpu.memoryManagementUnit.tlbIndexRegister <= 5'd5;
		cpu.memoryManagementUnit.tlbInvalidIndexRegister <= 1'b1;
		#20 waitForBusAccess();
		assert(cpu.memoryManagementUnit.tlbEntryHighRegister == 20'h12345, "cpu.memoryManagementUnit.tlbEntryHighRegister");
		assert(cpu.memoryManagementUnit.tlbEntryLowRegister == {20'h55555, 2'b11}, "cpu.memoryManagementUnit.tlbEntryLowRegister");
		assert(cpu.memoryManagementUnit.tlbIndexRegister == 5'd5, "cpu.memoryManagementUnit.tlbIndexRegister");
		assert(cpu.memoryManagementUnit.tlbInvalidIndexRegister == 1'b1, "cpu.memoryManagementUnit.tlbIndexInvalidRegister");
		assert(cpu.memoryManagementUnit.tlbKeyMemory.keyArray[5] == 20'h12345, "check key at index");
		assert(cpu.memoryManagementUnit.tlbValueMemory.valueArray[5] == {20'h55555, 2'b11}, "check value at index");
		assert(cpu.memoryManagementUnit.randomIndexCounter == 5'd17, "random counter");

		/** test TBWI **/
		assertionContext = "test TBWI";
		busReadData <= buildJInstruction(6'h3d, 26'd0);
		cpu.memoryManagementUnit.randomIndexCounter <= 5'd17;
		cpu.memoryManagementUnit.tlbEntryHighRegister <= 20'h91919;
		cpu.memoryManagementUnit.tlbEntryLowRegister <= {20'h82828, 2'b01};
		cpu.memoryManagementUnit.tlbIndexRegister <= 5'd13;
		cpu.memoryManagementUnit.tlbInvalidIndexRegister <= 1'b1;
		#20 waitForBusAccess();
		assert(cpu.memoryManagementUnit.tlbEntryHighRegister == 20'h91919, "cpu.memoryManagementUnit.tlbEntryHighRegister");
		assert(cpu.memoryManagementUnit.tlbEntryLowRegister == {20'h82828, 2'b01}, "cpu.memoryManagementUnit.tlbEntryLowRegister");
		assert(cpu.memoryManagementUnit.tlbIndexRegister == 5'd13, "cpu.memoryManagementUnit.tlbIndexRegister");
		assert(cpu.memoryManagementUnit.tlbInvalidIndexRegister == 1'b1, "cpu.memoryManagementUnit.tlbIndexInvalidRegister");
		assert(cpu.memoryManagementUnit.tlbKeyMemory.keyArray[13] == 20'h91919, "check key at index");
		assert(cpu.memoryManagementUnit.tlbValueMemory.valueArray[13] == {20'h82828, 2'b01}, "check value at index");
		assert(cpu.memoryManagementUnit.tlbKeyMemory.keyArray[17] == 20'h2468b, "check written key");
		assert(cpu.memoryManagementUnit.tlbValueMemory.valueArray[17] == {20'hefefe, 2'b11}, "check written value");
		assert(cpu.memoryManagementUnit.randomIndexCounter == 5'd17, "random counter");

		/** done **/
		#100 $finish;
	end

endmodule
