`default_nettype none
`timescale 1ns / 1ps

/**
 * Testbench for the synchronizer.
 */
module SynchronizerTest ();

	/** includes **/
	`include "VUnit.inc.v"
	`include "Clock.inc.v"

	/** simulation **/
	reg reset;
	initial begin
		$dumpfile("util/SynchronizerTest.vcd");
		$dumpvars;
		
		/** try setting the data to 0 **/
		#5 dataIn <= 1'b0;
		#1000 assert(~dataOut, "fail: set data to 0");
		
		/**
		 * Set dataIn to 1, and watch how long it takes to propagate.
		 * According to the stageCount of 2 and the interface
		 * specification of the Synchronizer module, it should take exactly
		 * 2 clock cycles until the new value is visible at dataOut.
		 */
		dataIn <= 1'b1;
		#1 assert(~dataOut, "fail: set data to 1, immediate");
		#19 assert(~dataOut, "fail: set data to 1, 1 clock cycle");
		#20 assert(dataOut, "fail: set data to 1, 2 clock cycles");
		
		/** set data to 0 again **/
		dataIn <= 1'b0;
		#1 assert(dataOut, "fail: set data to 0, immediate");
		#19 assert(dataOut, "fail: set data to 0, 1 clock cycle");
		#20 assert(~dataOut, "fail: set data to 0, 2 clock cycles");
		
		/** if the input is toggled, it should appear toggled at the output with a 2 cycle delay **/
		dataIn <= 1'b1;
		#1 assert(~dataOut, "fail: toggle, 0+");
		#19 assert(~dataOut, "fail: toggle, 1");
		dataIn <= 1'b0;
		#1 assert(~dataOut, "fail: toggle, 1+");
		#19 assert(dataOut, "fail: toggle, 2");
		dataIn <= 1'b1;
		#1 assert(dataOut, "fail: toggle, 2+");
		#19 assert(~dataOut, "fail: toggle, 3");
		
		#100 $finish;
	end
	
	/** the module under test **/
	reg dataIn;
	wire dataOut;
	Synchronizer #(
		.stageCount(2)
	) synchronizer (
		.clock(clock),
		.reset(reset),
		.dataIn(dataIn),
		.dataOut(dataOut)
	);

endmodule
