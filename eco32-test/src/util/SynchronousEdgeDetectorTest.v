`default_nettype none
`timescale 1ns / 1ps

/**
 * Testbench for the synchronous edge detector.
 */
module SynchronousEdgeDetectorTest ();

	/** includes **/
	`include "VUnit.inc.v"
	`include "Clock.inc.v"

	/** simulation **/
	reg reset;
	initial begin
		$dumpfile("util/SynchronousEdgeDetectorTest.vcd");
		$dumpvars;
		
		/** try setting the data to 0 **/
		#5 dataIn <= 1'b0;
		#1000 assert(~previousDataOut, "fail: set data to 0");
		assert(~risingEdge, "fail: set data to 0");
		assert(~fallingEdge, "fail: set data to 0");
		assert(~anyEdge, "fail: set data to 0");
		
		/** test a rising edge **/
		dataIn <= 1'b1;
		#1 assert(~previousDataOut, "fail: previousDataOut on rising edge, immediate");
		assert(risingEdge, "fail: risingEdge on rising edge, immediate");
		assert(~fallingEdge, "fail: fallingEdge on rising edge, immediate");
		assert(anyEdge, "fail: anyEdge on rising edge, immediate");
		#19 assert(previousDataOut, "fail: previousDataOut on rising edge, after clock edge");
		assert(~risingEdge, "fail: risingEdge on rising edge, after clock edge");
		assert(~fallingEdge, "fail: fallingEdge on rising edge, after clock edge");
		assert(~anyEdge, "fail: anyEdge on rising edge, after clock edge");
		#20 assert(previousDataOut, "fail: previousDataOut on rising edge, after second clock edge");
		assert(~risingEdge, "fail: risingEdge on rising edge, after second clock edge");
		assert(~fallingEdge, "fail: fallingEdge on rising edge, after second clock edge");
		assert(~anyEdge, "fail: anyEdge on rising edge, after second clock edge");

		/** test a falling edge **/
		dataIn <= 1'b0;
		#1 assert(previousDataOut, "fail: previousDataOut on falling edge, immediate");
		assert(~risingEdge, "fail: risingEdge on falling edge, immediate");
		assert(fallingEdge, "fail: fallingEdge on falling edge, immediate");
		assert(anyEdge, "fail: anyEdge on falling edge, immediate");
		#19 assert(~previousDataOut, "fail: previousDataOut on falling edge, after clock edge");
		assert(~risingEdge, "fail: risingEdge on falling edge, after clock edge");
		assert(~fallingEdge, "fail: fallingEdge on falling edge, after clock edge");
		assert(~anyEdge, "fail: anyEdge on falling edge, after clock edge");
		#20 assert(~previousDataOut, "fail: previousDataOut on falling edge, after second clock edge");
		assert(~risingEdge, "fail: risingEdge on falling edge, after second clock edge");
		assert(~fallingEdge, "fail: fallingEdge on falling edge, after second clock edge");
		assert(~anyEdge, "fail: anyEdge on falling edge, after second clock edge");
		
		/** test toggling **/
		dataIn <= 1'b1;
		#1 assert(~previousDataOut, "fail: previousDataOut on toggling, immediate");
		assert(risingEdge, "fail: risingEdge on toggling, immediate");
		assert(~fallingEdge, "fail: fallingEdge on toggling, immediate");
		assert(anyEdge, "fail: anyEdge on toggling, immediate");
		
		#19 assert(previousDataOut, "fail: previousDataOut on toggling, after clock edge");
		assert(~risingEdge, "fail: risingEdge on toggling, after clock edge");
		assert(~fallingEdge, "fail: fallingEdge on toggling, after clock edge");
		assert(~anyEdge, "fail: anyEdge on toggling, after clock edge");
		
		dataIn <= 1'b0;
		#1 assert(previousDataOut, "fail: previousDataOut on toggling, after clock edge and second assignment");
		assert(~risingEdge, "fail: risingEdge on toggling, after clock edge and second assignment");
		assert(fallingEdge, "fail: fallingEdge on toggling, after clock edge and second assignment");
		assert(anyEdge, "fail: anyEdge on toggling, after clock edge and second assignment");
		
		#20 assert(~previousDataOut, "fail: previousDataOut on toggling, after second clock edge");
		assert(~risingEdge, "fail: risingEdge on toggling, after second clock edge");
		assert(~fallingEdge, "fail: fallingEdge on toggling, after second clock edge");
		assert(~anyEdge, "fail: anyEdge on toggling, after second clock edge");
		
		#100 $finish;
	end
	
	/** the module under test **/
	reg dataIn;
	wire previousDataOut;
	wire risingEdge;
	wire fallingEdge;
	wire anyEdge;
	SynchronousEdgeDetector synchronousEdgeDetector (
		.clock(clock),
		.reset(reset),
		.dataIn(dataIn),
		.previousDataOut(previousDataOut),
		.risingEdge(risingEdge),
		.fallingEdge(fallingEdge),
		.anyEdge(anyEdge)
	);

endmodule
