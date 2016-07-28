`default_nettype none
`timescale 1ns / 1ps

/**
 * This module keeps a 'previous' version of the input signal,
 * i.e. one that is one clock cycle old, and compares it with
 * the current version. It derives output signals that indicate
 * a rising edge (i.e. a change from 0 to 1), a falling edge
 * (i.e. a change from 1 to 0), or any edge (any of the two).
 */
module SynchronousEdgeDetector (

		/** system clock **/
		input clock,
		
		/** system reset **/
		input reset,
		
		/** input signal for which edges shall be detected **/
		input dataIn,
		
		/** the previous data signal **/
		output previousDataOut,
		
		/** this is 1 if the current data signal is 1 and the previous one is 0 **/
		output risingEdge,
		
		/** this is 1 if the current data signal is 0 and the previous one is 1 **/
		output fallingEdge,

		/** this is 1 if risingEdge and/or fallingEdge is **/
		output anyEdge
		
	);
	
	/** the previous data signal value **/
	reg previousData;
	always @(posedge clock) begin
		previousData <= dataIn;
	end
	
	/** generate output signals **/
	assign previousDataOut = previousData;
	assign risingEdge = dataIn & ~previousData;
	assign fallingEdge = ~dataIn & previousData;
	assign anyEdge = (dataIn != previousData);

endmodule
