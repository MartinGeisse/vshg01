`default_nettype none
`timescale 1ns / 1ps

/**
 * The bus timeout counter. This is simply a counter that can be cleared and fires
 * an alarm after a certain number of clock edges. The counter will fire an alarm
 * only for a single clock cycle, and will do so in regular intervals if not cleared.
 */
module BusTimeoutCounter (

		/** the clock **/
		input clock,
		
		/** this signal will synchronously clear the counter to its initial value **/
		input clear,
		
		/** this signal is set to 1 for one clock cycle when the timer expires **/
		output alarm
		
	);

	/** the internal counter **/
	reg[7:0] counter;
	
	/** counter logic **/
	always @(posedge clock) begin
		if (clear) begin
			counter <= 8'd255;
		end else begin
			counter <= counter - 1;
		end
	end
	
	/** alarm logic **/
	assign alarm = (counter == 8'd0);

endmodule
