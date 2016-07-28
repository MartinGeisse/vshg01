`default_nettype none
`timescale 1ns / 1ps

/**
 * PS/2 keyboard controller with ECO32 bus interface.
 */
module BusKeyboardController (
		input clock,
		input reset,
		input busEnable,
		input busWrite,
		input[2:2] busAddress,
		input[7:0] busWriteData,
		output[7:0] busReadData,
		output busWait,
		output interrupt,
		input ps2Clock,
		input ps2Data
	);

	/** the internal raw keyboard controller **/
	wire rawKeyboardDataReady;
	wire[7:0] rawKeyboardData;
	RawKeyboardController rawKeyboardController (
		.clock(clock),
		.reset(reset),
		.ps2Clock(ps2Clock),
		.ps2Data(ps2Data),
		.dataReady(rawKeyboardDataReady),
		.dataOut(rawKeyboardData)
	);
	
	/** decode bus signals **/
	wire selectControlRegister = (busEnable & (busAddress[2] == 1'b0));
	wire selectDataRegister = (busEnable & (busAddress[2] == 1'b1));
	wire writeControlRegister = selectControlRegister & busWrite;
	wire readDataRegister = selectDataRegister & ~busWrite;

	/** bus-visible registers **/
	reg readyRegister;
	reg interruptEnableRegister;
	reg[7:0] dataRegister;
	always @(posedge clock) begin
		if (reset) begin
			readyRegister <= 1'b0;
			interruptEnableRegister <= 1'b0;
		end else begin
			
			/**
			 * The data register cannot be written to from the bus. It is
			 * simply loaded when the raw keyboard controller receives
			 * a data byte.
			 */
			if (rawKeyboardDataReady) begin
				dataRegister <= rawKeyboardData;
			end
			
			/**
			 * If the control register is written, it changes its value as
			 * demanded, no matter what the raw controller is doing. This is
			 * also the only way to change the interrupt enable register.
			 * If the control register is not written and the raw controller
			 * just has a data byte available, then the ready register is set,
			 * even if the data register is currently read from the bus (in
			 * the latter case, the CPU has read a byte just in time before
			 * a new one arrives). If the raw controller does not have a byte
			 * ready, then reading the data register clears the ready register.
			 * In all other cases, the ready register is left alone.
			 */
			if (writeControlRegister) begin
				readyRegister <= busWriteData[0];
				interruptEnableRegister <= busWriteData[1];
			end else if (rawKeyboardDataReady) begin
				readyRegister <= 1'b1;
			end else if (readDataRegister) begin
				readyRegister <= 1'b0;
			end

		end
	end	
	
	/** the remaining bus interface logic **/
	assign busReadData = busAddress[2] ? dataRegister : {6'd0, interruptEnableRegister, readyRegister};
	assign busWait = 1'b0;
	assign interrupt = readyRegister & interruptEnableRegister;
	
endmodule
