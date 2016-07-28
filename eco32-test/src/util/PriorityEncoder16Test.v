`default_nettype none
`timescale 1ns / 1ps

/**
 * Testbench for the 16-input priority encoder.
 */
module PriorityEncoder16Test ();

	/** includes **/
	`include "VUnit.inc.v"

	/** simulation **/
	initial begin
		$dumpfile("util/PriorityEncoder16Test.vcd");
		$dumpvars;
		
		/** test **/

		inputSignals <= 16'b0000000000000000;
		#1 assert(anySignalActive == 1'b0, "none active");

		inputSignals <= 16'b0000000000000001;
		#1 assert(anySignalActive == 1'b1, "only signal 0 active -- anySignalActive");
		#1 assert(activeSignalIndex == 4'd0, "only signal 0 active -- activeSignalIndex");

		inputSignals <= 16'b0000000000000010;
		#1 assert(anySignalActive == 1'b1, "only signal 1 active -- anySignalActive");
		#1 assert(activeSignalIndex == 4'd1, "only signal 1 active -- activeSignalIndex");

		inputSignals <= 16'b0000000000000011;
		#1 assert(anySignalActive == 1'b1, "signals 0, 1 active -- anySignalActive");
		#1 assert(activeSignalIndex == 4'd0, "signals 0, 1 active -- activeSignalIndex");

		inputSignals <= 16'b1100100111001010;
		#1 assert(anySignalActive == 1'b1, "many active -- anySignalActive");
		#1 assert(activeSignalIndex == 4'd1, "many active -- activeSignalIndex");

		inputSignals <= 16'b1100100111001100;
		#1 assert(anySignalActive == 1'b1, "find 2 -- anySignalActive");
		#1 assert(activeSignalIndex == 4'd2, "find 2 -- activeSignalIndex");

		inputSignals <= 16'b1100100111001000;
		#1 assert(anySignalActive == 1'b1, "find 3 -- anySignalActive");
		#1 assert(activeSignalIndex == 4'd3, "find 3 -- activeSignalIndex");

		inputSignals <= 16'b1100100111010000;
		#1 assert(anySignalActive == 1'b1, "find 4 -- anySignalActive");
		#1 assert(activeSignalIndex == 4'd4, "find 4 -- activeSignalIndex");

		inputSignals <= 16'b1100100111100000;
		#1 assert(anySignalActive == 1'b1, "find 5 -- anySignalActive");
		#1 assert(activeSignalIndex == 4'd5, "find 5 -- activeSignalIndex");

		inputSignals <= 16'b1100100111000000;
		#1 assert(anySignalActive == 1'b1, "find 6 -- anySignalActive");
		#1 assert(activeSignalIndex == 4'd6, "find 6 -- activeSignalIndex");

		inputSignals <= 16'b1100100110000000;
		#1 assert(anySignalActive == 1'b1, "find 7 -- anySignalActive");
		#1 assert(activeSignalIndex == 4'd7, "find 7 -- activeSignalIndex");

		inputSignals <= 16'b1100100100000000;
		#1 assert(anySignalActive == 1'b1, "find 8 -- anySignalActive");
		#1 assert(activeSignalIndex == 4'd8, "find 8 -- activeSignalIndex");

		inputSignals <= 16'b1100101000000000;
		#1 assert(anySignalActive == 1'b1, "find 9 -- anySignalActive");
		#1 assert(activeSignalIndex == 4'd9, "find 9 -- activeSignalIndex");

		inputSignals <= 16'b1100110000000000;
		#1 assert(anySignalActive == 1'b1, "find 10 -- anySignalActive");
		#1 assert(activeSignalIndex == 4'd10, "find 10 -- activeSignalIndex");

		inputSignals <= 16'b1100100000000000;
		#1 assert(anySignalActive == 1'b1, "find 11 -- anySignalActive");
		#1 assert(activeSignalIndex == 4'd11, "find 11 -- activeSignalIndex");

		inputSignals <= 16'b1101000000000000;
		#1 assert(anySignalActive == 1'b1, "find 12 -- anySignalActive");
		#1 assert(activeSignalIndex == 4'd12, "find 12 -- activeSignalIndex");

		inputSignals <= 16'b1110000000000000;
		#1 assert(anySignalActive == 1'b1, "find 13 -- anySignalActive");
		#1 assert(activeSignalIndex == 4'd13, "find 13 -- activeSignalIndex");

		inputSignals <= 16'b1100000000000000;
		#1 assert(anySignalActive == 1'b1, "find 14 -- anySignalActive");
		#1 assert(activeSignalIndex == 4'd14, "find 14 -- activeSignalIndex");

		inputSignals <= 16'b1000000000000000;
		#1 assert(anySignalActive == 1'b1, "find 15 -- anySignalActive");
		#1 assert(activeSignalIndex == 4'd15, "find 15 -- activeSignalIndex");

		inputSignals <= 16'b0000000000000000;
		#1 assert(anySignalActive == 1'b0, "none again -- anySignalActive");
		
		$finish;
	end
	
	/** the module under test **/
	reg[15:0] inputSignals;
	wire anySignalActive;
	wire[3:0] activeSignalIndex;
	PriorityEncoder16 encoder (
		.inputSignals(inputSignals),
		.anySignalActive(anySignalActive),
		.activeSignalIndex(activeSignalIndex)
	);

endmodule
