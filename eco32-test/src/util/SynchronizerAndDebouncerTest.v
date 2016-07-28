`default_nettype none
`timescale 1ns / 1ps

/**
 * Testbench for the synchronizer-and-debouncer bundle module.
 */
module SynchronizerAndDebouncerTest ();

	/** includes **/
	`include "VUnit.inc.v"
	`include "Clock.inc.v"

	/** simulation **/
	reg reset;
	initial begin
		$dumpfile("util/SynchronizerAndDebouncerTest.vcd");
		$dumpvars;

		/** reset the debouncer to put it into a defined state **/
		reset <= 1'b1;
		#45 reset <= 1'b0;
		
		/** try setting the data to 0 **/
		dataIn <= 1'b0;
		#1000 assert(~dataOut, "fail: set data to 0");
		
		/**
		 * Set dataIn to 1, and watch how long it takes to propagate.
		 * It should take 7 clock edges (2 synchronizer stages, 5 clock
		 * edges for a debouncer with an initialization value of 3).
		 */
		dataIn <= 1'b1;
		#1 assert(~dataOut, "fail: set data to 1, immediate");
		#19 assert(~dataOut, "fail: set data to 1, 1 clock cycle");
		#20 assert(~dataOut, "fail: set data to 1, 2 clock cycles");
		#20 assert(~dataOut, "fail: set data to 1, 3 clock cycles");
		#20 assert(~dataOut, "fail: set data to 1, 4 clock cycles");
		#20 assert(~dataOut, "fail: set data to 1, 5 clock cycles");
		#20 assert(~dataOut, "fail: set data to 1, 6 clock cycles");
		#20 assert(dataOut, "fail: set data to 1, 7 clock cycles");
		
		/** set data to 0 again **/
		dataIn <= 1'b0;
		#1 assert(dataOut, "fail: set data to 0, immediate");
		#19 assert(dataOut, "fail: set data to 0, 1 clock cycle");
		#20 assert(dataOut, "fail: set data to 0, 2 clock cycles");
		#20 assert(dataOut, "fail: set data to 0, 3 clock cycles");
		#20 assert(dataOut, "fail: set data to 0, 4 clock cycles");
		#20 assert(dataOut, "fail: set data to 0, 5 clock cycles");
		#20 assert(dataOut, "fail: set data to 0, 6 clock cycles");
		#20 assert(~dataOut, "fail: set data to 0, 7 clock cycles");
		
		/**
		 * An input pulse spanning 4 clock edges should pass the debouncer
		 * and be visible after the usual 7 clock edges. Likewise,
		 * the end of the pulse should be visible after a total of
		 * 4 + 7 = 11 clock edges.
		 */
		dataIn <= 1'b1;
		#1 assert(~dataOut, "fail: pulse width 4, immediate");
		#19 assert(~dataOut, "fail: pulse width 4, 1 clock cycle");
		#20 assert(~dataOut, "fail: pulse width 4, 2 clock cycles");
		#20 assert(~dataOut, "fail: pulse width 4, 3 clock cycles");
		#20 assert(~dataOut, "fail: pulse width 4, 4 clock cycles");
		dataIn <= 1'b0;
		#1 assert(~dataOut, "fail: pulse width 4, 4+ clock cycles");
		#19 assert(~dataOut, "fail: pulse width 4, 5 clock cycles");
		#20 assert(~dataOut, "fail: pulse width 4, 6 clock cycles");
		#20 assert(dataOut, "fail: pulse width 4, 7 clock cycles");
		#20 assert(dataOut, "fail: pulse width 4, 8 clock cycles");
		#20 assert(dataOut, "fail: pulse width 4, 9 clock cycles");
		#20 assert(dataOut, "fail: pulse width 4, 10 clock cycles");
		#20 assert(~dataOut, "fail: pulse width 4, 11 clock cycles");
		
		/**
		 * An input pulse spanning 3 clock edges should be blocked
		 * by the debouncer.
		 */
		dataIn <= 1'b1;
		#1 assert(~dataOut, "fail: pulse width 3, immediate");
		#19 assert(~dataOut, "fail: pulse width 3, 1 clock cycle");
		#20 assert(~dataOut, "fail: pulse width 3, 2 clock cycles");
		#20 assert(~dataOut, "fail: pulse width 3, 3 clock cycles");
		dataIn <= 1'b0;
		#1 assert(~dataOut, "fail: pulse width 3, 3+ clock cycles");
		#19 assert(~dataOut, "fail: pulse width 3, 4 clock cycles");
		#20 assert(~dataOut, "fail: pulse width 3, 5 clock cycles");
		#20 assert(~dataOut, "fail: pulse width 3, 6 clock cycles");
		#20 assert(~dataOut, "fail: pulse width 3, 7 clock cycles");
		#20 assert(~dataOut, "fail: pulse width 3, 8 clock cycles");
		#20 assert(~dataOut, "fail: pulse width 3, 9 clock cycles");
		#20 assert(~dataOut, "fail: pulse width 3, 10 clock cycles");
		#20 assert(~dataOut, "fail: pulse width 3, 11 clock cycles");
		
		#100 $finish;
	end
	
	/** the module under test **/
	reg dataIn;
	wire dataOut;
	SynchronizerAndDebouncer #(
		.synchronizerStageCount(2),
		.debouncerTimerWidth(2),
		.debouncerTimerInitializationValue(3)
	) synchronizerAndDebouncer (
		.clock(clock),
		.reset(reset),
		.dataIn(dataIn),
		.dataOut(dataOut)
	);

endmodule
