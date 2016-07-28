`default_nettype none
`timescale 1ns / 1ps

/**
 * Testbench for computation instructions.
 */
module CpuComputationInstructionsTest ();

	/** includes **/
	`include "VUnit.inc.v"
	`include "Clock.inc.v"
	`include "../../eco32/src/cpu/CpuConstants.inc.v"
	`include "cpu/CpuTestHelper.inc.v"

	/** simulation **/
	initial begin
		$dumpfile("cpu/CpuComputationInstructionsTest.vcd");
		$dumpvars;
		
		/** initialization **/
		busReadData <= 32'd0;
		busWait <= 1'b0;
		interrupts <= 16'b0000000000000000;
		
		/** reset **/
		#5 reset <= 1'b1;
		#20 reset <= 1'b0;
		#20; // reset -> begin instruction

		/** test arithmetic **/

		assertionContext <= "add";
		testComputationInstructionR(0, 5, 3, 32'd125, "ADD [5] + [3]");
		testComputationInstructionI(1, 5, 11, 32'd134, "ADDI [5] + 11");
		testComputationInstructionI(1, 5, (32'd0 - 32'd1), 32'd122, "ADDI [5] + (-1)");
		
		assertionContext <= "sub";
		testComputationInstructionR(2, 5, 3, 32'd121, "SUB [5] - [3]");
		testComputationInstructionI(3, 5, 11, 32'd112, "SUBI [5] - 11");
		testComputationInstructionI(3, 5, (32'd0 - 32'd1), 32'd124, "SUBI [5] - (-1)");
		
		assertionContext <= "mul";
		testComputationInstructionR(4, 5, 3, 32'd246, "MUL [5] * [3]");
		testComputationInstructionR(4, 5, 2, (32'd0 - 32'd123), "MUL [5] * [2]");
		testComputationInstructionI(5, 5, 3, 32'd369, "MULI [5] * 3");
		testComputationInstructionI(5, 5, (32'd0 - 32'd1), (32'd0 - 32'd123), "MULI [5] * (-1)");
		
		assertionContext <= "mulu";
		testComputationInstructionR(6, 5, 3, 32'd246, "MULU [5] * [3]");
		testComputationInstructionR(6, 5, 2, (32'd0 - 32'd123), "MULU [5] * [2]");
		testComputationInstructionI(7, 5, 3, 32'd369, "MULUI [5] * 3");
		testComputationInstructionI(7, 5, (32'd0 - 32'd1), 32'h7AFF85, "MULUI [5] * (-1)");

		assertionContext <= "div";
		testComputationInstructionR(8, 5, 3, 32'd61, "DIV [5] / [3]");
		testComputationInstructionR(8, 5, 5, 32'd1, "DIV [5] / [5]");
		testComputationInstructionR(8, 5, 2, (32'd0 - 32'd123), "DIV [5] / [2]");
		testComputationInstructionI(9, 5, 3, 32'd41, "DIVI [5] / 3");
		testComputationInstructionI(9, 5, (32'd0 - 32'd1), (32'd0 - 32'd123), "DIVI [5] / (-1)");
		testComputationInstructionI(9, 8, 32'hfff0, 32'h05edcba9, "DIVI [8] / 0xfff0");

		assertionContext <= "divu";
		testComputationInstructionR(10, 5, 3, 32'd61, "DIVU [5] / [3]");
		testComputationInstructionR(10, 5, 5, 32'd1, "DIVU [5] / [5]");
		testComputationInstructionR(10, 5, 2, 32'd0, "DIVU [5] / [2]");
		testComputationInstructionI(11, 5, 3, 32'd41, "DIVUI [5] / 3");
		testComputationInstructionI(11, 5, (32'd0 - 32'd1), 32'd0, "DIVUI [5] / (-1)");
		testComputationInstructionI(11, 8, 32'hfff0, 32'ha12d, "DIVUI [8] / 0xfff0");

		assertionContext <= "rem";
		testComputationInstructionR(12, 5, 3, 32'd1, "REM [5] % [3]");
		testComputationInstructionR(12, 5, 5, 32'd0, "REM [5] % [5]");
		testComputationInstructionR(12, 5, 4, 32'd1, "REM [5] % [4]");
		testComputationInstructionI(13, 5, 5, 32'd3, "REMI [5] % 5");
		testComputationInstructionI(13, 5, (32'd0 - 32'd12), 32'd3, "REMI [5] % (-12)");
		testComputationInstructionI(13, 8, 32'hfff0, 32'hfffffff7, "REMI [8] % 0xfff0");

		assertionContext <= "remu";
		testComputationInstructionR(14, 5, 3, 32'd1, "REMU [5] % [3]");
		testComputationInstructionR(14, 5, 5, 32'd0, "REMU [5] % [5]");
		testComputationInstructionR(14, 5, 4, 32'd123, "REMU [5] % [4]");
		testComputationInstructionI(15, 5, 5, 32'd3, "REMUI [5] % 3");
		testComputationInstructionI(15, 5, (32'd0 - 32'd1), 32'd123, "REMUI [5] % (-1)");
		testComputationInstructionI(15, 8, 32'hfff0, 32'h5837, "REMUI [8] % 0xfff0");

		/** test logic **/

		assertionContext <= "and";
		testComputationInstructionR(16, 7, 8, 32'b0010_0000_0010_0000_0100_0100_0010_0010, "AND [7] & [8]");
		testComputationInstructionI(17, 8, 16'b1111_0101_1010_0011, 32'b0000_0000_0000_0000_0100_0101_0010_0011, "ANDI [8] & 0xf5a3");
		testComputationInstructionI(17, 8, 16'hffff, 32'h00004567, "ANDI [8] & 0xffff");

		assertionContext <= "or";
		testComputationInstructionR(18, 7, 8, 32'b1010_1011_0111_0011_1111_0111_0111_0111_, "OR [7] | [8]");
		testComputationInstructionI(19, 8, 16'b1111_0101_1010_0011, 32'b1010_0001_0010_0011_1111_0101_1110_0111, "ORI [8] | 0xf5a3");
		testComputationInstructionI(19, 8, 16'hffff, 32'ha123ffff, "ORI [8] | 0xffff");

		assertionContext <= "xor";
		testComputationInstructionR(20, 7, 8, 32'b1000_1011_0101_0011_1011_0011_0101_0101, "XOR [7] ^ [8]");
		testComputationInstructionI(21, 8, 16'b1111_0101_1010_0011, 32'b1010_0001_0010_0011_1011_0000_1100_0100, "XORI [8] ^ 0xf5a3");
		testComputationInstructionI(21, 8, 16'hffff, 32'b1010_0001_0010_0011_1011_1010_1001_1000, "XORI [8] ^ 0xffff");

		assertionContext <= "xnor";
		testComputationInstructionR(22, 7, 8, 32'b0111_0100_1010_1100_0100_1100_1010_1010, "XNOR [7] ~^ [8]");
		testComputationInstructionI(23, 8, 16'b1111_0101_1010_0011, 32'b0101_1110_1101_1100_0100_1111_0011_1011, "XNORI [8] ~^ 0xf5a3");
		testComputationInstructionI(23, 8, 16'hffff, 32'b0101_1110_1101_1100_0100_0101_0110_0111, "XNORI [8] ~^ 0xffff");

		/** test shift **/

		assertionContext <= "sll";
		testComputationInstructionR(24, 7, 8, 32'b0011_1000_0111_1011_0001_1001_0000_0000, "SLL [7] << [8]");
		testComputationInstructionI(25, 8, 16'b1111_0101_1010_0011, 32'b0000_1001_0001_1010_0010_1011_0011_1000, "SLL [8] << 0xf5a3");

		assertionContext <= "slr";
		testComputationInstructionR(26, 7, 8, 32'b0000_0000_0101_0100_1110_0001_1110_1100, "SLR [7] >> [8]");
		testComputationInstructionR(26, 8, 8, 32'b0000_0001_0100_0010_0100_0110_1000_1010, "SLR [8] >> [8]");
		testComputationInstructionI(27, 7, 16'b1111_0101_1010_0011, 32'b0000_0101_0100_1110_0001_1110_1100_0110, "SLR [7] >> 0xf5a3");
		testComputationInstructionI(27, 8, 16'b1111_0101_1010_0011, 32'b0001_0100_0010_0100_0110_1000_1010_1100, "SLR [8] >> 0xf5a3");

		assertionContext <= "sar";
		testComputationInstructionR(28, 7, 8, 32'b0000_0000_0101_0100_1110_0001_1110_1100, "SAR [7] >> [8]");
		testComputationInstructionR(28, 8, 8, 32'b1111_1111_0100_0010_0100_0110_1000_1010, "SAR [8] >> [8]");
		testComputationInstructionI(29, 7, 16'b1111_0101_1010_0011, 32'b0000_0101_0100_1110_0001_1110_1100_0110, "SAR [7] >> 0xf5a3");
		testComputationInstructionI(29, 8, 16'b1111_0101_1010_0011, 32'b1111_0100_0010_0100_0110_1000_1010_1100, "SAR [8] >> 0xf5a3");

		assertionContext <= "ldhi";
		testComputationInstructionI(31, 8, 16'hf5a3, 32'hf5a30000, "LDHI [8], 0xf5a3");
		
		/** done **/
		#100 $finish;
	end

endmodule
