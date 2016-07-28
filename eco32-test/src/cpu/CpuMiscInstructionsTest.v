`default_nettype none
`timescale 1ns / 1ps

/**
 * Testbench for misc instructions.
 */
module CPUMiscInstructionsTest ();

	/** includes **/
	`include "VUnit.inc.v"
	`include "Clock.inc.v"
	`include "../../eco32/src/cpu/CpuConstants.inc.v"
	`include "cpu/CpuTestHelper.inc.v"

	/** simulation **/
	initial begin
		$dumpfile("cpu/CpuMiscInstructionsTest.vcd");
		$dumpvars;
		
		/** initialization **/
		busReadData <= 32'd0;
		busWait <= 1'b0;
		interrupts <= 16'b0000000000000000;
		
		/** reset **/
		#5 reset <= 1'b1;
		#20 reset <= 1'b0;
		#20; // reset -> begin instruction

		/** test RFX instruction **/
		assertionContext = "test RFX instruction";
		cpu.generalPurposeRegisters.valueArray[30][31:0] <= 32'hc2345678;
		cpu.generalPurposeRegisters.valueArray[31][31:0] <= 32'h99999999;
		cpu.processorStatusWord.processorStatusWord <= 32'b0000_1_001_110_01100_1100010011101010;
		#20 waitForBusAccess();
		assert(busAddress == 32'h20000000, "instruction fetch bus address 1");
		busReadData <= buildJInstruction(6'h2f, 26'h0);
		
		#20 waitForBusAccess();
		assert(cpu.generalPurposeRegisters.valueArray[30][31:0] == 32'hc2345678, "reg 30 not affected (1)");
		assert(cpu.generalPurposeRegisters.valueArray[31][31:0] == 32'h99999999, "reg 31 not affected (1)");
		assert(busAddress == 32'h02345678, "instruction fetch bus address 2");
		assert(cpu.processorStatusWord.processorStatusWord == 32'b0000_1_011_100_01100_1100010011101010, "transformed PSW");

		/** done **/
		#100 $finish;
	end

endmodule
