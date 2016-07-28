`default_nettype none
`timescale 1ns / 1ps

/**
 * This is a convenience module that applies a Synchronizer, then a
 * Debouncer to a data signal. See the documentation for those modules
 * for details.
 */
module SynchronizerAndDebouncer (

		/** system clock **/
		input clock,
		
		/** system reset **/
		input reset,
		
		/** input signal to de-bounce **/
		input dataIn,
		
		/** de-bounced output signal **/
		output dataOut
		
	);
	
	/** number of synchronization stages **/
	parameter synchronizerStageCount = 2;
	
	/** size of the debouncing timer register **/
	parameter debouncerTimerWidth = 4;
	
	/** initialization value of the debouncing timer **/
	parameter debouncerTimerInitializationValue = 4'b1111;

	/** the temporary data signal between the synchronizer and the debouncer **/
	wire synchronizedData;
	
	/** the synchronizer module **/	
	Synchronizer #(
		.stageCount(synchronizerStageCount)
	) synchronizer (
		.clock(clock),
		.reset(reset),
		.dataIn(dataIn),
		.dataOut(synchronizedData)
	);
	
	/** the debouncer module **/
	Debouncer #(
		.timerWidth(debouncerTimerWidth),
		.timerInitializationValue(debouncerTimerInitializationValue)
	) ps2ClockDebouncer (
		.clock(clock),
		.reset(reset),
		.dataIn(synchronizedData),
		.dataOut(dataOut)
	);
	

endmodule
