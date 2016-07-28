`default_nettype none
`timescale 1ns / 1ps

/**
 * Testbench for the generic arbiter.
 */
module GenericArbiterTest ();

	/** includes **/
	`include "VUnit.inc.v"

	/** simulation **/
	initial begin
		$dumpfile("util/GenericArbiterTest.vcd");
		$dumpvars;
		
		/** test **/

		request <= 8'b00000000;
		#1 assert(grant == 8'b00000000, "none");

		request <= 8'b00000001;
		#1 assert(grant == 8'b00000001, "zero only");

		request <= 8'b00000010;
		#1 assert(grant == 8'b00000010, "one only");

		request <= 8'b00000011;
		#1 assert(grant == 8'b00000001, "zero / one");

		request <= 8'b00110100;
		#1 assert(grant == 8'b00000100, "complex 1");

		request <= 8'b11100000;
		#1 assert(grant == 8'b00100000, "complex 2");

		/** done **/
		#100 $finish;

	end
	
	/** the module under test **/
	reg[7:0] request;
	wire[7:0] grant;
	GenericArbiter #(
		.width(8)
	) genericArbiter (
		.request(request),
		.grant(grant)
	);

endmodule
