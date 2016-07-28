`default_nettype none
`timescale 1ns / 1ps

/**
 * Testbench for the 32-input priority encoder.
 */
module PriorityEncoder32Test ();

	/** includes **/
	`include "VUnit.inc.v"

	/** simulation **/
	initial begin
		$dumpfile("util/PriorityEncoder32Test.vcd");
		$dumpvars;
		
		/** test **/

		inputSignals <= 32'b00000000000000000000000000000000;
		#1 assert(anySignalActive == 1'b0, "none active");

		inputSignals <= 32'b00000000000000000000000000000001;
		#1 assert(anySignalActive == 1'b1, "only signal 0 active -- anySignalActive");
		#1 assert(activeSignalIndex == 5'd0, "only signal 0 active -- activeSignalIndex");

		inputSignals <= 32'b00000000000000000000000000000010;
		#1 assert(anySignalActive == 1'b1, "only signal 1 active -- anySignalActive");
		#1 assert(activeSignalIndex == 5'd1, "only signal 1 active -- activeSignalIndex");

		inputSignals <= 32'b00000000000000000000000000000011;
		#1 assert(anySignalActive == 1'b1, "signals 0, 1 active -- anySignalActive");
		#1 assert(activeSignalIndex == 5'd0, "signals 0, 1 active -- activeSignalIndex");

		inputSignals <= 32'b01011001001110001100100111001011;
		#1 assert(anySignalActive == 1'b1, "find 0 -- anySignalActive");
		#1 assert(activeSignalIndex == 5'd0, "find 0 -- activeSignalIndex");

		inputSignals <= 32'b01011001001110001100100111001010;
		#1 assert(anySignalActive == 1'b1, "find 1 -- anySignalActive");
		#1 assert(activeSignalIndex == 5'd1, "find 1 -- activeSignalIndex");

		inputSignals <= 32'b01011001001110001100100111001100;
		#1 assert(anySignalActive == 1'b1, "find 2 -- anySignalActive");
		#1 assert(activeSignalIndex == 5'd2, "find 2 -- activeSignalIndex");

		inputSignals <= 32'b01011001001110001100100111001000;
		#1 assert(anySignalActive == 1'b1, "find 3 -- anySignalActive");
		#1 assert(activeSignalIndex == 5'd3, "find 3 -- activeSignalIndex");

		inputSignals <= 32'b01011001001110001100100111010000;
		#1 assert(anySignalActive == 1'b1, "find 4 -- anySignalActive");
		#1 assert(activeSignalIndex == 5'd4, "find 4 -- activeSignalIndex");

		inputSignals <= 32'b01011001001110001100100111100000;
		#1 assert(anySignalActive == 1'b1, "find 5 -- anySignalActive");
		#1 assert(activeSignalIndex == 5'd5, "find 5 -- activeSignalIndex");

		inputSignals <= 32'b01011001001110001100100111000000;
		#1 assert(anySignalActive == 1'b1, "find 6 -- anySignalActive");
		#1 assert(activeSignalIndex == 5'd6, "find 6 -- activeSignalIndex");

		inputSignals <= 32'b01011001001110001100100110000000;
		#1 assert(anySignalActive == 1'b1, "find 7 -- anySignalActive");
		#1 assert(activeSignalIndex == 5'd7, "find 7 -- activeSignalIndex");

		inputSignals <= 32'b01011001001110001100100100000000;
		#1 assert(anySignalActive == 1'b1, "find 8 -- anySignalActive");
		#1 assert(activeSignalIndex == 5'd8, "find 8 -- activeSignalIndex");

		inputSignals <= 32'b01011001001110001100101000000000;
		#1 assert(anySignalActive == 1'b1, "find 9 -- anySignalActive");
		#1 assert(activeSignalIndex == 5'd9, "find 9 -- activeSignalIndex");

		inputSignals <= 32'b01011001001110001100110000000000;
		#1 assert(anySignalActive == 1'b1, "find 10 -- anySignalActive");
		#1 assert(activeSignalIndex == 5'd10, "find 10 -- activeSignalIndex");

		inputSignals <= 32'b01011001001110001100100000000000;
		#1 assert(anySignalActive == 1'b1, "find 11 -- anySignalActive");
		#1 assert(activeSignalIndex == 5'd11, "find 11 -- activeSignalIndex");

		inputSignals <= 32'b01011001001110001101000000000000;
		#1 assert(anySignalActive == 1'b1, "find 12 -- anySignalActive");
		#1 assert(activeSignalIndex == 5'd12, "find 12 -- activeSignalIndex");

		inputSignals <= 32'b01011001001110001110000000000000;
		#1 assert(anySignalActive == 1'b1, "find 13 -- anySignalActive");
		#1 assert(activeSignalIndex == 5'd13, "find 13 -- activeSignalIndex");

		inputSignals <= 32'b01011001001110001100000000000000;
		#1 assert(anySignalActive == 1'b1, "find 14 -- anySignalActive");
		#1 assert(activeSignalIndex == 5'd14, "find 14 -- activeSignalIndex");

		inputSignals <= 32'b01011001001110001000000000000000;
		#1 assert(anySignalActive == 1'b1, "find 15 -- anySignalActive");
		#1 assert(activeSignalIndex == 5'd15, "find 15 -- activeSignalIndex");

		inputSignals <= 32'b01011001001110010000000000000000;
		#1 assert(anySignalActive == 1'b1, "find 16 -- anySignalActive");
		#1 assert(activeSignalIndex == 5'd16, "find 16 -- activeSignalIndex");

		inputSignals <= 32'b01011001001110100000000000000000;
		#1 assert(anySignalActive == 1'b1, "find 17 -- anySignalActive");
		#1 assert(activeSignalIndex == 5'd17, "find 17 -- activeSignalIndex");

		inputSignals <= 32'b01011001001111000000000000000000;
		#1 assert(anySignalActive == 1'b1, "find 18 -- anySignalActive");
		#1 assert(activeSignalIndex == 5'd18, "find 18 -- activeSignalIndex");

		inputSignals <= 32'b01011001001110000000000000000000;
		#1 assert(anySignalActive == 1'b1, "find 19 -- anySignalActive");
		#1 assert(activeSignalIndex == 5'd19, "find 19 -- activeSignalIndex");

		inputSignals <= 32'b01011001001100000000000000000000;
		#1 assert(anySignalActive == 1'b1, "find 20 -- anySignalActive");
		#1 assert(activeSignalIndex == 5'd20, "find 20 -- activeSignalIndex");

		inputSignals <= 32'b01011001001000000000000000000000;
		#1 assert(anySignalActive == 1'b1, "find 21 -- anySignalActive");
		#1 assert(activeSignalIndex == 5'd21, "find 21 -- activeSignalIndex");

		inputSignals <= 32'b01011001010000000000000000000000;
		#1 assert(anySignalActive == 1'b1, "find 22 -- anySignalActive");
		#1 assert(activeSignalIndex == 5'd22, "find 22 -- activeSignalIndex");

		inputSignals <= 32'b01011001100000000000000000000000;
		#1 assert(anySignalActive == 1'b1, "find 23 -- anySignalActive");
		#1 assert(activeSignalIndex == 5'd23, "find 23 -- activeSignalIndex");

		inputSignals <= 32'b01011001000000000000000000000000;
		#1 assert(anySignalActive == 1'b1, "find 24 -- anySignalActive");
		#1 assert(activeSignalIndex == 5'd24, "find 24 -- activeSignalIndex");

		inputSignals <= 32'b01011010000000000000000000000000;
		#1 assert(anySignalActive == 1'b1, "find 25 -- anySignalActive");
		#1 assert(activeSignalIndex == 5'd25, "find 25 -- activeSignalIndex");

		inputSignals <= 32'b01011100000000000000000000000000;
		#1 assert(anySignalActive == 1'b1, "find 26 -- anySignalActive");
		#1 assert(activeSignalIndex == 5'd26, "find 26 -- activeSignalIndex");

		inputSignals <= 32'b01011000000000000000000000000000;
		#1 assert(anySignalActive == 1'b1, "find 27 -- anySignalActive");
		#1 assert(activeSignalIndex == 5'd27, "find 27 -- activeSignalIndex");

		inputSignals <= 32'b01010000000000000000000000000000;
		#1 assert(anySignalActive == 1'b1, "find 28 -- anySignalActive");
		#1 assert(activeSignalIndex == 5'd28, "find 28 -- activeSignalIndex");

		inputSignals <= 32'b01100000000000000000000000000000;
		#1 assert(anySignalActive == 1'b1, "find 29 -- anySignalActive");
		#1 assert(activeSignalIndex == 5'd29, "find 29 -- activeSignalIndex");

		inputSignals <= 32'b01000000000000000000000000000000;
		#1 assert(anySignalActive == 1'b1, "find 30 -- anySignalActive");
		#1 assert(activeSignalIndex == 5'd30, "find 30 -- activeSignalIndex");

		inputSignals <= 32'b10000000000000000000000000000000;
		#1 assert(anySignalActive == 1'b1, "find 31 -- anySignalActive");
		#1 assert(activeSignalIndex == 5'd31, "find 31 -- activeSignalIndex");

		inputSignals <= 32'b00000000000000000000000000000000;
		#1 assert(anySignalActive == 1'b0, "none again -- anySignalActive");
		
		$finish;
	end
	
	/** the module under test **/
	reg[31:0] inputSignals;
	wire anySignalActive;
	wire[4:0] activeSignalIndex;
	PriorityEncoder32 encoder (
		.inputSignals(inputSignals),
		.anySignalActive(anySignalActive),
		.activeSignalIndex(activeSignalIndex)
	);

endmodule
