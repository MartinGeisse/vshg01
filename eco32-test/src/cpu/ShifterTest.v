`default_nettype none
`timescale 1ns / 1ps

/**
 * Testbench for the shifter.
 */
module ShifterTest ();

	/** includes **/
	`include "VUnit.inc.v"
	`include "Clock.inc.v"

	/** include constant definitions **/
	`include "../../eco32/src/cpu/CpuConstants.inc.v"
	`include "../../eco32/src/cpu/ShifterConstants.inc.v"
	
	/** the module under test **/
	reg[1:0] shifterOperation;
	reg[31:0] shifterLeftOperand;
	reg[4:0] shifterRightOperand;
	wire[31:0] shifterResult;
	Shifter shifter (
		.clock(clock),
		.operation(shifterOperation),
		.leftOperand(shifterLeftOperand),
		.rightOperand(shifterRightOperand),
		.result(shifterResult)
	);
	
	/** simulation **/
	initial begin
		$dumpfile("cpu/ShifterTest.vcd");
		$dumpvars;
		#5;

		/** test SLL **/
		
		shifterOperation <= `SHIFTER_OPERATION_LOGICAL_SHIFT_LEFT;
		shifterLeftOperand <= 32'h0472b8af;
		shifterRightOperand <= 5'h11;
		#20 assert(shifterResult == 32'h715e0000, "sll 1");

		shifterOperation <= `SHIFTER_OPERATION_LOGICAL_SHIFT_LEFT;
		shifterLeftOperand <= 32'h01234567;
		shifterRightOperand <= 5'h01;
		#20 assert(shifterResult == 32'h02468ace, "sll 2");
		
		/** test SLR **/
		
		shifterOperation <= `SHIFTER_OPERATION_LOGICAL_SHIFT_RIGHT;
		shifterLeftOperand <= 32'h0472b8af;
		shifterRightOperand <= 5'h11;
		#20 assert(shifterResult == 32'h00000239, "slr 1");

		shifterOperation <= `SHIFTER_OPERATION_LOGICAL_SHIFT_RIGHT;
		shifterLeftOperand <= 32'h22222222;
		shifterRightOperand <= 5'h01;
		#20 assert(shifterResult == 32'h11111111, "slr 2");

		shifterOperation <= `SHIFTER_OPERATION_LOGICAL_SHIFT_RIGHT;
		shifterLeftOperand <= 32'h80000000;
		shifterRightOperand <= 5'h01;
		#20 assert(shifterResult == 32'h40000000, "slr 3");
		
		/** test SAR **/
		
		shifterOperation <= `SHIFTER_OPERATION_ARITHMETIC_SHIFT_RIGHT;
		shifterLeftOperand <= 32'h0472b8af;
		shifterRightOperand <= 5'h11;
		#20 assert(shifterResult == 32'h00000239, "sar 1");

		shifterOperation <= `SHIFTER_OPERATION_ARITHMETIC_SHIFT_RIGHT;
		shifterLeftOperand <= 32'h22222222;
		shifterRightOperand <= 5'h01;
		#20 assert(shifterResult == 32'h11111111, "sar 2");

		shifterOperation <= `SHIFTER_OPERATION_ARITHMETIC_SHIFT_RIGHT;
		shifterLeftOperand <= 32'h80000000;
		shifterRightOperand <= 5'h01;
		#20 assert(shifterResult == 32'hc0000000, "sar 3");
		
		/** done **/
		#100 $finish;
	end
		
endmodule
