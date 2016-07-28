`default_nettype none
`timescale 1ns / 1ps

/**
 * This module transforms a 1-bit input to a 1-bit output.
 * It filters rapid signal changes out: The value of the
 * input signal is transferred to the output only when it 
 * has remained unchanged for a user-specified number of
 * clock cycles, otherwise the previous output value is
 * retained.
 *
 * The exact timing behavior is as follows. Assume the
 * debouncer has seen a stable input signal for a long
 * enough time to settle for it. Then, the input signal
 * changes. At the following clock edge -- call it clock
 * edge 0 -- the debouncer notices the difference and
 * sets its internal timer to the specified
 * timerInitializationValue. The timer is first decreased
 * at clock edge 1, assuming the signal remains at its
 * new value.
 *
 * The timer reaches zero at clock edge (timerInitializationValue).
 * Finally, the dataOut port is set to the new value at clock
 * edge (timerInitializationValue + 1), with the new value
 * being visible to the outside in the clock period following
 * that edge.
 * 
 * If the dataIn port has a different value at any clock edge in
 * the range 0..timerInitializationValue, then the timer is
 * reset to the initialization value, and the value of dataOut
 * remains unchanged. If dataIn has a different value at clock
 * edge (timerInitializationValue + 1), then it is too late:
 * The internal counter will be set to the initialization value,
 * but since it has been zero at a clock edge, the new dataIn value
 * is transferred to dataOut.
 *
 * To summarize: It takes (timerInitializationValue + 2) clock
 * edges between a change in dataIn and the corresponding change
 * in dataOut -- here labeled edges 0..(timerInitializationValue + 1) --
 * but dataIn must change back before the second last of those clock
 * edges to keep dataOut from changing.
 *
 * Rationale: It is intentional that the debouncer does not notice
 * changes before clock edge (timerInitializationValue + 2) in time,
 * for implementation reasons. Doing so would require another signal
 * to flow into the computation of dataOut. If the dataIn changes much
 * slower than the reaction time of this debouncer, this should not
 * be a problem. Similarly, if the bouncing interval is shorter than
 * the reaction time, the latter can be reduced. If, however,
 * bouncing interval and intended dataIn change interval are very
 * close together, a specialized debouncer should be used instead of
 * this module.
 *
 * Parameter: timerWidth. This parameter specifies the bit width of the
 * timer, and should be large enough to hold the timerInitializationValue.
 * 
 * Parameter: timerInitializationValue. This parameter is used to initialize
 * the timer value on a change in dataIn.
 */
module Debouncer (

		/** system clock **/
		input clock,
		
		/** system reset **/
		input reset,
		
		/** input signal to de-bounce **/
		input dataIn,
		
		/** de-bounced output signal **/
		output reg dataOut
		
	);
	
	/** size of the timer register **/
	parameter timerWidth = 4;
	
	/** initialization value of the timer **/
	parameter timerInitializationValue = 4'b1111;
	
	/** this register simply stores the dataIn value from the previous clock signal **/
	reg previousDataIn;
	
	/**
	 * This counter contains the number of clock cycles left until the input signal
	 * is considered stable.
	 */
	reg[timerWidth-1:0] clocksUntilStable;
	
	/** main clocked process **/
	always @(posedge clock) begin
	
		/** store the dataIn value as the previous value **/
		previousDataIn <= dataIn;
		
		/** transfer data to dataOut when the counter is zero **/
		if (clocksUntilStable == 0) begin
			dataOut <= previousDataIn;
		end
		
		/** determine counter behavior **/
		if (reset | (dataIn != previousDataIn)) begin
			clocksUntilStable <= timerInitializationValue;
		end else begin
			clocksUntilStable <= clocksUntilStable - 1;
		end
		
	end

endmodule
