`default_nettype none
`timescale 1ns / 1ps

/**
 * Testbench for the debouncer.
 */
module DebouncerTest ();

	/** includes **/
	`include "VUnit.inc.v"
	`include "Clock.inc.v"

	/** simulation **/
	reg reset;
	initial begin
		$dumpfile("util/DebouncerTest.vcd");
		$dumpvars;
		
		/** reset the debouncer to put it into a defined state **/
		reset <= 1'b1;
		#45 reset <= 1'b0;
		
		/** try setting the data to 0 **/
		dataIn <= 1'b0;
		#1000 assert(~dataOut, "fail: set data to 0");
		
		/**
		 * Set dataIn to 1, and watch how long it takes to propagate.
		 * According to the timerInitializationValue of 3 and the interface
		 * specification of the Debouncer module, it should take exactly
		 * 2 more clock cycles (i.e. a total of 5) until the new value is
		 * visible at dataOut.
		 */
		dataIn <= 1'b1;
		#1 assert(~dataOut, "fail: set data to 1, immediate");
		#19 assert(~dataOut, "fail: set data to 1, 1 clock cycle");
		#20 assert(~dataOut, "fail: set data to 1, 2 clock cycles");
		#20 assert(~dataOut, "fail: set data to 1, 3 clock cycles");
		#20 assert(~dataOut, "fail: set data to 1, 4 clock cycles");
		#20 assert(dataOut, "fail: set data to 1, 5 clock cycles");
		
		/** set data to 0 again **/
		dataIn <= 1'b0;
		#1000 assert(~dataOut, "fail: set data to 0");
		
		/**
		 * Set dataIn to 1, and reset to 0 after 4 clock cycles. This
		 * must not prevent dataOut from being asserted at the 5th clock
		 * cycle. On the other hand, it should take the usual 5 clock
		 * cycles for the new 0 value to propagate.
		 */
		dataIn <= 1'b1;
		#1 assert(~dataOut, "fail: set data to 1, immediate");
		#19 assert(~dataOut, "fail: set data to 1, 1 clock cycle");
		#20 assert(~dataOut, "fail: set data to 1, 2 clock cycles");
		#20 assert(~dataOut, "fail: set data to 1, 3 clock cycles");
		#20 assert(~dataOut, "fail: set data to 1, 4 clock cycles");
		dataIn <= 1'b0;
		#1 assert(~dataOut, "reset dataIn, immediate");
		#19 assert(dataOut, "fail: de-asserting dataIn at the last clock cycle affects dataOut. Reset dataIn, 1 clock cycle");
		#20 assert(dataOut, "Reset dataIn to 0 after 4 cycles, 2 clock cycles");
		#20 assert(dataOut, "Reset dataIn to 0 after 4 cycles, 3 clock cycles");
		#20 assert(dataOut, "Reset dataIn to 0 after 4 cycles, 4 clock cycles");
		#20 assert(~dataOut, "Reset dataIn to 0 after 4 cycles, 5 clock cycles");
		#1000 assert(~dataOut, "Reset dataIn to 0 after 4 cycles, long time");
		
		/**
		 * Set dataIn to 1, and reset to 0 after 3 clock cycles. This should cause
		 * dataOut to remain 0.
		 */
		dataIn <= 1'b1;
		#1 assert(~dataOut, "fail: set data to 1, immediate");
		#19 assert(~dataOut, "fail: set data to 1, 1 clock cycle");
		#20 assert(~dataOut, "fail: set data to 1, 2 clock cycles");
		#20 assert(~dataOut, "fail: set data to 1, 3 clock cycles");
		dataIn <= 1'b0;
		#1 assert(~dataOut, "Reset dataIn to 0 after 3 cycles, immediate");
		#20 assert(~dataOut, "Reset dataIn to 0 after 3 cycles, 1 clock cycles");
		#20 assert(~dataOut, "Reset dataIn to 0 after 3 cycles, 2 clock cycles");
		#20 assert(~dataOut, "Reset dataIn to 0 after 3 cycles, 3 clock cycles");
		#20 assert(~dataOut, "Reset dataIn to 0 after 3 cycles, 4 clock cycles");
		#20 assert(~dataOut, "Reset dataIn to 0 after 3 cycles, 5 clock cycles");
		
		/**
		 * Set dataIn to 1, and reset to 0 after 2 clock cycles. dataOut should still
		 * remain 0.
		 */
		dataIn <= 1'b1;
		#1 assert(~dataOut, "fail: set data to 1, immediate");
		#19 assert(~dataOut, "fail: set data to 1, 1 clock cycle");
		#20 assert(~dataOut, "fail: set data to 1, 2 clock cycles");
		dataIn <= 1'b0;
		#1 assert(~dataOut, "Reset dataIn to 0 after 2 cycles, immediate");
		#20 assert(~dataOut, "Reset dataIn to 0 after 2 cycles, 1 clock cycles");
		#20 assert(~dataOut, "Reset dataIn to 0 after 2 cycles, 2 clock cycles");
		#20 assert(~dataOut, "Reset dataIn to 0 after 2 cycles, 3 clock cycles");
		#20 assert(~dataOut, "Reset dataIn to 0 after 2 cycles, 4 clock cycles");
		#20 assert(~dataOut, "Reset dataIn to 0 after 2 cycles, 5 clock cycles");

		$finish;
	end
	
	/** the module under test **/
	reg dataIn;
	wire dataOut;
	Debouncer #(
		.timerWidth(2),
		.timerInitializationValue(3)
	) debouncer (
		.clock(clock),
		.reset(reset),
		.dataIn(dataIn),
		.dataOut(dataOut)
	);

endmodule
