`default_nettype none
`timescale 1ns / 1ps

/**
 * Interrupt detector. This module takes the external interrupt lines
 * and the interrupt mask and enable values from the PSW. It then
 * selects the lowest interrupt index that is not masked out (if any),
 * and provides an internal interrupt enable bit that is active if any
 * non-masked interrupt is active and the IEN bit of the PSW is set.
 * The output signals of this module are loaded synchronously at every
 * clock edge.
 */
module InterruptDetector (

		/** the clock **/
		input clock,
	
		/** the external interrupt lines **/
		input[15:0] externalInterruptLines,
		
		/** the interrupt mask field from the PSW **/
		input[15:0] pswInterruptMask,
		
		/** the interrupt enable bit from the PSW **/
		input pswInterruptEnable,
		
		/**
		 * Whether any interrupt is visible to the CPU. This signal is determined
		 * from the inputs at every clock edge.
		 */
		output reg interruptActive,
		
		/**
		 * The index of the lowest active non-masked interrupt (undefined if interruptActive
		 * is 0). This signal is determined from the inputs at every clock edge.
		 */
		output reg[3:0] index
			
	);

	/**
	 * This encoder finds the active interrupt with the lowest index (if any).
	 */
	wire anyActiveNextValue;
	wire[3:0] indexNextValue;
	PriorityEncoder16 interruptPriorityEncoder (
		.inputSignals(externalInterruptLines & pswInterruptMask),
		.anySignalActive(anyActiveNextValue),
		.activeSignalIndex(indexNextValue)
	);

	/**
	 * Synchronous loading of the output registers.
	 */
	always @(posedge clock) begin
		interruptActive <= anyActiveNextValue & pswInterruptEnable;
		index <= indexNextValue;
	end

endmodule
