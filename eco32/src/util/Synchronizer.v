`default_nettype none
`timescale 1ns / 1ps

/**
 * This module feeds the input signal through a shift register
 * to synchronize it. The number of shift register stages can
 * be controlled by a parameter and is 2 by default.
 */
module Synchronizer (

		/** system clock **/
		input clock,
		
		/** system reset **/
		input reset,
		
		/** input signal to synchronize **/
		input dataIn,
		
		/** synchronized output signal **/
		output dataOut
		
	);
	
	/** number of synchronization stages **/
	parameter stageCount = 2;
	
	/** the synchronization stages themselves. The data signal flows from left (high index) to right (low index) **/
	reg[stageCount-1:0] stages;
	
	/** synchronization logic **/
	always @(posedge clock) begin
		stages <= {dataIn, stages[stageCount-1:1]};
	end
	assign dataOut = stages[0];

endmodule
