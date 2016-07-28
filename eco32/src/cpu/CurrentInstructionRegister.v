`default_nettype none
`timescale 1ns / 1ps

/**
 * The current instruction register. This register stores the
 * instruction after fetching and keeps it until finished.
 */
module CurrentInstructionRegister (

		/** the clock **/
		input clock,

		/** the write-enable for this register **/
		input writeEnable,

		/** this value is loaded into this register when writeEnable is 1 **/
		input[31:0] busReadData,

		/** the current value of this register **/
		output reg[31:0] currentInstruction

	);

	/** synchronous loading logic **/
	always @(posedge clock) begin
		if (writeEnable) begin
			currentInstruction <= busReadData;
		end
	end

endmodule
