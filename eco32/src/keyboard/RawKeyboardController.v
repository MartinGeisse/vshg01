`default_nettype none
`timescale 1ns / 1ps

/**
 * PS/2 keyboard controller with low-level interface. This controller
 * receives keyboard scan code bytes and makes them available through
 * the internal data interface.
 *
 * The timing of data transmission is controlled by the keyboard, and
 * data bytes are not buffered by this class. Therefore, this class
 * pushes data bytes out whenever completed.
 *
 * This module will detect errors on the PS/2 interface and ignore
 * further input in that case. Instead, it will wait for silence
 * on the interface and then recover. It will, hoever, NOT cause
 * an appropriate retransmission to happen, so errors will cause
 * scan codes to be lost.
 */
module RawKeyboardController (

		/** system clock **/
		input clock,
		
		/** system reset **/
		input reset,
		
		/**
		 * This line is asserted for one clock cycle when dataOut
		 * contains a data byte.
		 */
		output dataReady,
		
		/**
		 * When dataReady is asserted, this port contains a data
		 * byte just received. Otherwise, this port must be
		 * ignored.
		 */
		output[7:0] dataOut,
		
		/** the PS/2 clock line **/
		input ps2Clock,
		
		/** the PS/2 data line **/
		input ps2Data
		
	);

	/** synchronize and debounce the external PS/2 clock line **/
	wire debouncedPs2Clock;
	SynchronizerAndDebouncer #(
		.synchronizerStageCount(2),
		.debouncerTimerWidth(4),
		.debouncerTimerInitializationValue(4'd15)
	) ps2ClockSynchronizerAndDebouncer (
		.clock(clock),
		.reset(reset),
		.dataIn(ps2Clock),
		.dataOut(debouncedPs2Clock)
	);

	/** synchronize and debounce the external PS/2 data line **/
	wire debouncedPs2Data;
	SynchronizerAndDebouncer #(
		.synchronizerStageCount(2),
		.debouncerTimerWidth(4),
		.debouncerTimerInitializationValue(4'd15)
	) ps2DataSynchronizerAndDebouncer (
		.clock(clock),
		.reset(reset),
		.dataIn(ps2Data),
		.dataOut(debouncedPs2Data)
	);
	
	/** detect edges in the PS/2 clock **/
	wire ps2ClockRisingEdge;
	wire ps2ClockFallingEdge;
	wire ps2ClockAnyEdge;
	SynchronousEdgeDetector ps2ClockEdgeDetector (
		.clock(clock),
		.reset(reset),
		.dataIn(debouncedPs2Clock),
		.risingEdge(ps2ClockRisingEdge),
		.fallingEdge(ps2ClockFallingEdge),
		.anyEdge(ps2ClockAnyEdge)
	);
	
	/**
	 * This counter is reset at every PS/2 clock edge.
	 * If the PS/2 interface is currently in the process
	 * of transmiting data, but the PS/2 clock signal
	 * remains unchanged for too long and is not indicating
	 * the and of a transmission, then this counter detects
	 * an error. This causes the corrupted value to be
	 * discarded.
	 *
	 * The exact time interval that causes an error to be
	 * flagged is 5k clock cycles, i.e. 100us. This time is
	 * the maximal PS/2 clock period, so the counter should
	 * be reset at least twice in the meantime.
	 * 
	 * The ps2ClockWatchdogCounterExpired signal indicates that
	 * the counter is expired. This signal is asserted only for
	 * a single clock cycle, and so must be honored immediately.
	 */
	reg[12:0] ps2ClockWatchdogCounter;
	always @(posedge clock) begin
		if (reset || ps2ClockAnyEdge) begin
			ps2ClockWatchdogCounter <= 13'b0000000000000;
		end else begin
			ps2ClockWatchdogCounter <= ps2ClockWatchdogCounter + 1;
		end
	end
	wire ps2ClockWatchdogCounterExpired = (ps2ClockWatchdogCounter == 13'b1010000000000);

	/**
	 * This counter counts the number of data bits successfully received.
	 * It only has a meaning during STATE_TRANSMIT. This register contain
	 * the values 0..7 which is sufficient since upon receiving the 8th
	 * bit the main state machine switches to STATE_PARITY.
	 */
	reg[2:0] receivedBitCount;

	/** the main state machine register **/
	reg[2:0] state;
	
	/** data data shift register. Only valid in STATE_TRANSMIT and STATE_PARITY. **/
	reg[7:0] result;
	
	/** accumulated parity of the received bits. Only valid in STATE_TRANSMIT and STATE_PARITY. **/
	reg parity;

	/** state: waiting for start of transmission **/
	parameter STATE_IDLE = 3'b000;
	
	/** state: waiting for the next data bit **/
	parameter STATE_TRANSMIT = 3'b001;
	
	/** state: waiting for the parity bit **/
	parameter STATE_PARITY = 3'b010;
	
	/** state: waiting for the stop bit **/
	parameter STATE_STOP = 3'b011;
	
	/** state: error. Waiting for silence on the PS/2 interface to recover **/
	parameter STATE_ERROR = 3'b100;
	
	/** main state machine logic **/
	always @(posedge clock) begin
		if (reset) begin
			receivedBitCount <= 3'bxxx;
			state <= STATE_IDLE;
			result <= 8'bxxxxxxxx;
			parity <= 1'bx;
		end else begin
			case (state)
			
				/** idle: wait for start of transmission **/
				STATE_IDLE: begin
					if (ps2ClockFallingEdge) begin
						if (debouncedPs2Data) begin
							/** error: data must be low at start of transmission **/
							state <= STATE_ERROR;
							receivedBitCount <= 3'bxxx;
							result <= 8'bxxxxxxxx;
							parity <= 1'bx;
						end else begin
							/** start transmission **/
							state <= STATE_TRANSMIT;
							receivedBitCount <= 3'b000;
							result <= 8'bxxxxxxxx;
							parity <= 1'b0;
						end
					end
				end
				
				/** transmission in progress: wait for the next data bit **/
				STATE_TRANSMIT: begin
					if (ps2ClockFallingEdge) begin
						result <= {debouncedPs2Data, result[7:1]};
						parity <= parity ^ debouncedPs2Data;
						if (receivedBitCount == 3'b111) begin
							receivedBitCount <= 3'bxxx;
							state <= STATE_PARITY;
						end else begin
							receivedBitCount <= receivedBitCount + 1;
						end
					end else if (ps2ClockWatchdogCounterExpired) begin
						/** timeout error **/
						state <= STATE_ERROR;
						receivedBitCount <= 3'bxxx;
						result <= 8'bxxxxxxxx;
						parity <= 1'bx;
					end
				end
				
				/** parity: wait for the parity bit **/
				STATE_PARITY: begin
					if (ps2ClockFallingEdge) begin
						/** parity check **/
						state <= (debouncedPs2Data ^ parity) ? STATE_STOP : STATE_ERROR;
						receivedBitCount <= 3'bxxx;
						result <= 8'bxxxxxxxx;
						parity <= 1'bx;
					end else if (ps2ClockWatchdogCounterExpired) begin
						/** timeout error **/
						state <= STATE_ERROR;
						receivedBitCount <= 3'bxxx;
						result <= 8'bxxxxxxxx;
						parity <= 1'bx;
					end
				end
				
				/** stop: wait for the stop bit **/
				STATE_STOP: begin
					if (ps2ClockFallingEdge) begin
						/** stop bit must be 1 **/
						state <= debouncedPs2Data ? STATE_IDLE : STATE_ERROR;
						receivedBitCount <= 3'bxxx;
						result <= 8'bxxxxxxxx;
						parity <= 1'bx;
					end else if (ps2ClockWatchdogCounterExpired) begin
						/** timeout error **/
						state <= STATE_ERROR;
						receivedBitCount <= 3'bxxx;
						result <= 8'bxxxxxxxx;
						parity <= 1'bx;
					end
				end
				
				/** error: wait for watchdog expiration while clock and data are high **/
				STATE_ERROR: begin
					if (ps2ClockWatchdogCounterExpired & debouncedPs2Clock & debouncedPs2Data) begin
						state <= STATE_IDLE;
						receivedBitCount <= 3'bxxx;
						result <= 8'bxxxxxxxx;
						parity <= 1'bx;
					end
				end
				
			endcase
		end
	end
	
	/** compute output signals **/
	assign dataReady = (state == STATE_PARITY) & ps2ClockFallingEdge & (debouncedPs2Data ^ parity);
	assign dataOut = result;

endmodule
