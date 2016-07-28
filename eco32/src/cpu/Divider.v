`default_nettype none
`timescale 1ns / 1ps

/**
 * 32-bit sequential divider.
 *
 * To use this divider, assert the input values, and assert the activate signal for one
 * clock edge. The divider then starts working.
 *
 * When finished, the divider asserts the done signal. The consumer of the result may
 * synchronously react to that signal. For example, the consumer may treat the done
 * signal as a clock-enable for its own registers that store the values of the quotient,
 * remainder and divisionByZero signals. 
 *
 * The inputs may change their values after the divider has started working without
 * affecting the result.
 */
module Divider (
		
		/** the clock **/
		input clock,
		
		/** the left operand (dividend) **/
		input[31:0] leftOperand,
		
		/** the right operand (divisor) **/
		input[31:0] rightOperand,
		
		/** set to 1 to perform a signed division, 0 for unsigned **/
		input isSigned,
		
		/** set to 1 for one clock edge to activate the divider **/
		input activate,
		
		/**
		 * This output is 1 when the result is ready. It can be used as a clock enable
		 * of a register that consumes the result.
		 */
		output reg done,
		
		/** the resulting quotient **/
		output[31:0] quotient,
		
		/** the resulting remainder **/
		output[31:0] remainder,
		
		/** this result flag is 1 if the divisor was zero **/
		output reg divisionByZero

	);

	/** determine the final sign of the quotient, and save it during activation **/
	reg quotientSignRegister;
	always @(posedge clock) begin
		if (activate) begin
			quotientSignRegister <= isSigned & (leftOperand[31] ^ rightOperand[31]);
		end
	end

	/** determine the final sign of the remainder, and save it during activation **/
	reg remainderSignRegister;
	always @(posedge clock) begin
		if (activate) begin
			remainderSignRegister <= isSigned & leftOperand[31];
		end
	end

	/**
	 * This wire provides the magnitude of the dividend input signal.
	 * Since it is computed from the input signal in a purely combinatorial way,
	 * this signal is only valid while the divider is being activated.
	 *
	 * In the special case that the dividend is 0x80000000, this value is
	 * 0x80000000. This is intentional.
	 */
	wire[31:0] dividendInputMagnitude = (isSigned & leftOperand[31]) ? -leftOperand : leftOperand;
	
	/**
	 * This register contains the divisor magnitude. It is loaded during
	 * activation and is unchanged afterwards.
	 * 
	 * In the special case that the divisor is 0x80000000, this value is
	 * 0x80000000. This is intentional.
	 */
	reg[31:0] divisorMagnitudeRegister;
	always @(posedge clock) begin
		if (activate) begin
			divisorMagnitudeRegister <= (isSigned & rightOperand[31]) ? -rightOperand : rightOperand;
		end
	end
	
	/**
	 * We determine a division by zero from the divisorMagnitudeRegister to reduce
	 * load on the rightOperand input signal. Note that division-by-zero detection
	 * need not be fast.
	 */
	always @(posedge clock) begin
		if (~activate & ~done) begin
			divisionByZero <= (divisorMagnitudeRegister == 32'd0);
		end
	end
	
	/**
	 * This counter controls the operation of the divider. It ensures that
	 * the divider runs for exactly 32 cycles after activation, then
	 * asserts the done signal to stop the divider and to signal
	 * availability of the result to the consumer.
	 */
	reg[4:0] completedCycleCounter;
	always @(posedge clock) begin
		if (activate) begin
			done <= 1'b0;
			completedCycleCounter <= 5'd0;
		end else if (~done) begin
			if (completedCycleCounter == 5'd31) begin
				done <= 1'b1;
				completedCycleCounter <= 5'bxxxxx;
			end else begin
				completedCycleCounter <= completedCycleCounter + 1;
			end
		end
	end

	/*************************************************************************/

	/**
	 * This register has several purposes. The left half serves as
	 * a "current remainder" that is tested for possible subtraction
	 * of the divisor. The right half initially contains the dividend
	 * and is used to shift bits into the left half. As bits become
	 * free in the right half, they are re-used for bits of the quotient.
	 */
	reg[63:0] workingRegister;

	/**
	 * Compute the division results. These signals will show a lot of intermediate
	 * values while the division is in progress, but will show the final result
	 * when the divider has finished.
	 */
	assign quotient = quotientSignRegister ? -workingRegister[31:0] : workingRegister[31:0];
	assign remainder = remainderSignRegister ? -workingRegister[63:32] : workingRegister[63:32];

	/**
	 * Helper signals for the actual division. Bit 32 is used to store
	 * the carry bit of the subtraction; the actual subtraction result
	 * is in bits 31:0.
	 */
	wire[32:0] extendedCurrentRemainder = {1'b0, workingRegister[62:31]};
	wire[32:0] subtrahend = {1'b0, divisorMagnitudeRegister};
	wire[32:0] subtractionResult = extendedCurrentRemainder - subtrahend;
	wire doSubtract = ~subtractionResult[32];

	/**
	 * The actual divider logic.
	 */
	always @(posedge clock) begin
	
		if (activate) begin
		
			/**
			 * During activation, we just load the working register but don't do anything interesting.
			 */
			workingRegister <= {32'd0, dividendInputMagnitude};
			
		end else if (~done) begin
			
			/** division logic **/
			if (doSubtract) begin
			
				/**
				 * Shift 1 and subtract. The subtraction result takes working register bit 31 into account,
				 * so we combine it with bits 30:0.
				 */
				workingRegister <= {subtractionResult[31:0], workingRegister[30:0], 1'b1};
				
			end else begin
			
				/**
				 * Shift 0.
				 */
				workingRegister <= {workingRegister[62:0], 1'b0};
				
			end
			
		end
		
	end

endmodule
