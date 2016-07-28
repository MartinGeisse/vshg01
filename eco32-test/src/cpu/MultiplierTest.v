`default_nettype none
`timescale 1ns / 1ps

/**
 * Testbench for the multiplier.
 */
module MultiplierTest ();

	/** includes **/
	`include "VUnit.inc.v"
	`include "Clock.inc.v"

	/** the module under test **/
	reg[31:0] mulLeftOperandRegister;
	reg[31:0] mulRightOperandRegister;
	wire[31:0] mulResult;
	Multiplier multiplier (
		.leftOperand(mulLeftOperandRegister),
		.rightOperand(mulRightOperandRegister),
		.result(mulResult)
	);
	
	/** simulation **/
	reg reset;
	initial begin
		$dumpfile("cpu/MultiplierTest.vcd");
		$dumpvars;
		
		/** test some simple multiplications **/
		mulLeftOperandRegister <= 32'd1000;
		mulRightOperandRegister <= 32'd5;
		#1 assert(mulResult == 32'd5000, "test 1");

		mulLeftOperandRegister <= 32'hfffffffe;
		mulRightOperandRegister <= 32'h2;
		#1 assert(mulResult == 32'hfffffffc, "test 1");

		mulLeftOperandRegister <= 32'hfffffffe;
		mulRightOperandRegister <= 32'hfffffffe;
		#1 assert(mulResult == 32'h4, "test 1");

		mulLeftOperandRegister <= 32'hc0000000;
		mulRightOperandRegister <= 32'h2;
		#1 assert(mulResult == 32'h80000000, "test 1");
		
		/** done **/
		#100 $finish;
	end
		
endmodule
