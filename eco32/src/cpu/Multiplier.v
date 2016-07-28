`default_nettype none
`timescale 1ns / 1ps

/**
 * 32-bit purely combinatorial multiplier. This module is expressed in a way to support
 * hardware multiplier inference.
 */
module Multiplier (
		
		/** the left operand **/
		input[31:0] leftOperand,
		
		/** the right operand **/
		input[31:0] rightOperand,
		
		/** the computed result (computed in a purely combinatorial way) **/
		output[31:0] result

	);

	/** this supports hardware multiplier inference **/
	assign result = leftOperand * rightOperand;

endmodule
