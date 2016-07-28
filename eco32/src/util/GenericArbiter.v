`default_nettype none
`timescale 1ns / 1ps

/**
 * This is a generic arbiter module. It receives N input signals
 * and generates N output signals. If all inputs are 0, then all
 * outputs are 0. Otherwise, all outputs are 0 except that
 * output with the same index as the lowest active input.
 *
 * In other words, this module accepts N request input signals,
 * and grants access (sets the output active) to the requestor
 * with the lowest index.
 */
module GenericArbiter (

		/** the request signals **/
		input[width-1:0] request,
		
		/** the grant signals **/
		output[width-1:0] grant
		
	);
	
	/**
	 * The number of request / grant signals.
	 */
	parameter width = 1;
	
	/**
	 * arbitration logic. Access is allowed to requester 0 at any
	 * time, and allowed to requester (i+1) if it is allowed to
	 * but not requested by requester (i).
	 */
	wire[width-1:0] allow = {allow[width-2:0] & ~request[width-2:0], 1'b1};
	
	/**
	 * grant logic: Access is granted to any requester for which it
	 * is allowed and requested.
	 */
	assign grant = request & allow;
	
endmodule
