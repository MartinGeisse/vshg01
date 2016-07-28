`default_nettype none
`timescale 1ns / 1ps

/**
 * Testbench for branch instructions.
 */
module CpuBranchInstructionsTest ();

	/** includes **/
	`include "VUnit.inc.v"
	`include "Clock.inc.v"
	`include "../../eco32/src/cpu/CpuConstants.inc.v"
	`include "../../eco32/src/cpu/ProgramCounterConstants.inc.v"
	`include "cpu/CpuTestHelper.inc.v"

	/** simulation **/
	initial begin
		$dumpfile("cpu/CpuBranchInstructionsTest.vcd");
		$dumpvars;
		
		/** initialization **/
		busReadData <= 32'd0;
		busWait <= 1'b0;
		interrupts <= 16'b0000000000000000;
		
		/** reset **/
		#5 reset <= 1'b1;
		#20 reset <= 1'b0;
		#20; // reset -> begin instruction

		/** test basic branch functionality **/

		assertionContext = "test 1";
		#20 waitForBusAccess();
		assert(busAddress == 32'h20000000, "instruction fetch bus address 1");
		busReadData <= buildRRIInstruction(6'h20, 5'd1, 5'd1, 16'h200);
		#20 waitForBusAccess();
		assert(busAddress == 32'h20000804, "instruction fetch bus address 2");
		busReadData <= buildRRIInstruction(6'h20, 5'd1, 5'd1, 16'h100);
		#20 waitForBusAccess();
		assert(busAddress == 32'h20000c08, "instruction fetch bus address 3");
		busReadData <= buildRRIInstruction(6'h21, 5'd1, 5'd1, 16'h100);
		#20 waitForBusAccess();
		assert(busAddress == 32'h20000c0c, "instruction fetch bus address 4");
		assert(cpu.generalPurposeRegisters.valueArray[1][31:0] == 32'd1, "register not affected");
		busReadData <= buildRRIInstruction(6'h20, 5'd1, 5'd1, 16'h7fff);
		#20 waitForBusAccess();
		assert(busAddress == 32'h20020c0c, "instruction fetch bus address 4");
		busReadData <= buildRRIInstruction(6'h20, 5'd1, 5'd1, 16'h8000);
		#20 waitForBusAccess();
		assert(busAddress == 32'h20000c10, "instruction fetch bus address 4");
		
		/** test branch instructions **/

		assertionContext = "test branch instructions";
		
		testBranchInstruction(6'h20, 5'd7, 5'd10, 1'b1, "equal true");
		testBranchInstruction(6'h20, 5'd7, 5'd8, 1'b0, "equal false");

		testBranchInstruction(6'h21, 5'd7, 5'd10, 1'b0, "not equal false");
		testBranchInstruction(6'h21, 5'd7, 5'd8, 1'b1, "not equal true");

		testBranchInstruction(6'h22, 5'd1, 5'd5, 1'b1, "signed less/equal true 1");
		testBranchInstruction(6'h22, 5'd1, 5'd8, 1'b0, "signed less/equal false 1");
		testBranchInstruction(6'h22, 5'd1, 5'd1, 1'b1, "signed less/equal true 2");
		testBranchInstruction(6'h22, 5'd1, 5'd0, 1'b0, "signed less/equal false 2");
		testBranchInstruction(6'h22, 5'd8, 5'd1, 1'b1, "signed less/equal true 3");
		testBranchInstruction(6'h22, 5'd2, 5'd1, 1'b1, "signed less/equal true 4");

		testBranchInstruction(6'h23, 5'd1, 5'd5, 1'b1, "unsigned less/equal true 1");
		testBranchInstruction(6'h23, 5'd1, 5'd8, 1'b1, "unsigned less/equal true 2");
		testBranchInstruction(6'h23, 5'd1, 5'd1, 1'b1, "unsigned less/equal true 3");
		testBranchInstruction(6'h23, 5'd1, 5'd0, 1'b0, "unsigned less/equal false 1");
		testBranchInstruction(6'h23, 5'd8, 5'd1, 1'b0, "unsigned less/equal false 2");
		testBranchInstruction(6'h23, 5'd2, 5'd1, 1'b0, "unsigned less/equal false 3");

		testBranchInstruction(6'h24, 5'd1, 5'd5, 1'b1, "signed less than true 1");
		testBranchInstruction(6'h24, 5'd1, 5'd8, 1'b0, "signed less than false 1");
		testBranchInstruction(6'h24, 5'd1, 5'd1, 1'b0, "signed less than false *2");
		testBranchInstruction(6'h24, 5'd1, 5'd0, 1'b0, "signed less than false 2");
		testBranchInstruction(6'h24, 5'd8, 5'd1, 1'b1, "signed less than true 3");
		testBranchInstruction(6'h24, 5'd2, 5'd1, 1'b1, "signed less than true 4");

		testBranchInstruction(6'h25, 5'd1, 5'd5, 1'b1, "unsigned less than true 1");
		testBranchInstruction(6'h25, 5'd1, 5'd8, 1'b1, "unsigned less than true 2");
		testBranchInstruction(6'h25, 5'd1, 5'd1, 1'b0, "unsigned less than false *3");
		testBranchInstruction(6'h25, 5'd1, 5'd0, 1'b0, "unsigned less than false 1");
		testBranchInstruction(6'h25, 5'd8, 5'd1, 1'b0, "unsigned less than false 2");
		testBranchInstruction(6'h25, 5'd2, 5'd1, 1'b0, "unsigned less than false 3");

		testBranchInstruction(6'h26, 5'd1, 5'd5, 1'b0, "signed greater/equal false 1");
		testBranchInstruction(6'h26, 5'd1, 5'd8, 1'b1, "signed greater/equal true 1");
		testBranchInstruction(6'h26, 5'd1, 5'd1, 1'b1, "signed greater/equal (equal)");
		testBranchInstruction(6'h26, 5'd1, 5'd0, 1'b1, "signed greater/equal true 2");
		testBranchInstruction(6'h26, 5'd8, 5'd1, 1'b0, "signed greater/equal false 3");
		testBranchInstruction(6'h26, 5'd2, 5'd1, 1'b0, "signed greater/equal false 4");

		testBranchInstruction(6'h27, 5'd1, 5'd5, 1'b0, "unsigned greater/equal false 1");
		testBranchInstruction(6'h27, 5'd1, 5'd8, 1'b0, "unsigned greater/equal false 2");
		testBranchInstruction(6'h27, 5'd1, 5'd1, 1'b1, "unsigned greater/equal (equal)");
		testBranchInstruction(6'h27, 5'd1, 5'd0, 1'b1, "unsigned greater/equal true 1");
		testBranchInstruction(6'h27, 5'd8, 5'd1, 1'b1, "unsigned greater/equal true 2");
		testBranchInstruction(6'h27, 5'd2, 5'd1, 1'b1, "unsigned greater/equal true 3");

		testBranchInstruction(6'h28, 5'd1, 5'd5, 1'b0, "signed greater than false 1");
		testBranchInstruction(6'h28, 5'd1, 5'd8, 1'b1, "signed greater than true 1");
		testBranchInstruction(6'h28, 5'd1, 5'd1, 1'b0, "signed greater than (equal)");
		testBranchInstruction(6'h28, 5'd1, 5'd0, 1'b1, "signed greater than true 2");
		testBranchInstruction(6'h28, 5'd8, 5'd1, 1'b0, "signed greater than false 3");
		testBranchInstruction(6'h28, 5'd2, 5'd1, 1'b0, "signed greater than false 4");

		testBranchInstruction(6'h29, 5'd1, 5'd5, 1'b0, "unsigned greater than false 1");
		testBranchInstruction(6'h29, 5'd1, 5'd8, 1'b0, "unsigned greater than false 2");
		testBranchInstruction(6'h29, 5'd1, 5'd1, 1'b0, "unsigned greater than (equal)");
		testBranchInstruction(6'h29, 5'd1, 5'd0, 1'b1, "unsigned greater than true 1");
		testBranchInstruction(6'h29, 5'd8, 5'd1, 1'b1, "unsigned greater than true 2");
		testBranchInstruction(6'h29, 5'd2, 5'd1, 1'b1, "unsigned greater than true 3");

		/** done **/
		#100 $finish;
	end

endmodule
