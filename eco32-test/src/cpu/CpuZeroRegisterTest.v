`default_nettype none
`timescale 1ns / 1ps

/**
 * Testbench for the zero register.
 */
module CPUZeroRegisterTest ();

	/** includes **/
	`include "VUnit.inc.v"
	`include "Clock.inc.v"
	`include "../../eco32/src/cpu/CpuConstants.inc.v"
	`include "cpu/CpuTestHelper.inc.v"

	/** simulation **/
	initial begin
		$dumpfile("cpu/CpuZeroRegisterTest.vcd");
		$dumpvars;
		
		/** initialization **/
		busReadData <= 32'd0;
		busWait <= 1'b0;
		interrupts <= 16'b0000000000000000;
		
		/** reset **/
		#5 reset <= 1'b1;
		#20 reset <= 1'b0;
		#20 waitForBusAccess();

		/** assert initial state **/
		assert(cpu.generalPurposeRegisters.valueArray[0] == 32'h00000000, "test initial zero register value");
		
		/*****************************************************************************************************/
		
		/** test ADD instruction **/
		assertionContext = "test ADD";
		busReadData <= buildRRRInstruction(6'h00, 5'd7, 5'd1, 5'd0);
		#20 waitForBusAccess();
		assert(cpu.generalPurposeRegisters.valueArray[0][31:0] == 32'h00000000, "reg 0 not affected");

		/** test MUL instruction **/
		assertionContext = "test MUL";
		busReadData <= buildRRRInstruction(6'h04, 5'd7, 5'd1, 5'd0);
		#20 waitForBusAccess();
		assert(cpu.generalPurposeRegisters.valueArray[0][31:0] == 32'h00000000, "reg 0 not affected");

		/** test DIV instruction **/
		assertionContext = "test DIV";
		busReadData <= buildRRRInstruction(6'h08, 5'd7, 5'd1, 5'd0);
		#20 waitForBusAccess();
		assert(cpu.generalPurposeRegisters.valueArray[0][31:0] == 32'h00000000, "reg 0 not affected");

		/** test SLL instruction **/
		assertionContext = "test SLL";
		busReadData <= buildRRRInstruction(6'h18, 5'd7, 5'd1, 5'd0);
		#20 waitForBusAccess();
		assert(cpu.generalPurposeRegisters.valueArray[0][31:0] == 32'h00000000, "reg 0 not affected");

		/** test LDW instruction **/
		assertionContext = "test LDW";
		busReadData <= buildRRRInstruction(6'h30, 5'd0, 5'd0, 16'hfff0);
		#20 waitForBusAccess();
		busReadData <= 32'h12345678;
		#20 waitForBusAccess();
		assert(cpu.generalPurposeRegisters.valueArray[0][31:0] == 32'h00000000, "reg 0 not affected");

		/** test MVFS instruction **/
		assertionContext = "test MVFS";
		cpu.processorStatusWord.processorStatusWord <= 32'h00001234;
		busReadData <= buildRRIInstruction(6'h38, 5'd0, 5'd0, 16'd0);
		#20 waitForBusAccess();
		assert(cpu.generalPurposeRegisters.valueArray[0][31:0] == 32'h00000000, "reg 0 not affected");

		/** done **/
		#100 $finish;
	end

endmodule
