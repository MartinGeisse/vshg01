`default_nettype none
`timescale 1ns / 1ps

/**
 * Timer device module. This device first prescales
 * the clock to millisecond units. It then sets the
 * alarm bit (bit 0 of the control register) every D
 * milliseconds, where D is the value of the divisor
 * register. Setting D to 0 is interpreted as setting
 * a divisor of 2^32.
 */
module BusTimer (
		input clock,
		input reset,
		input busEnable,
		input busWrite,
		input[2:2] busAddress,
		input[31:0] busWriteData,
		output[31:0] busReadData,
		output busWait,
		output interrupt
	);

	/** prescaling counter that runs from 50000 down to 1 to generate millisecond events **/
	parameter prescalingCounterInitializationValue = 16'd50000;
	reg[15:0] prescalingCounter;
	wire prescalingCounterExpired = (prescalingCounter == 16'd1);
	
	/** the millisecond counter **/
	reg[31:0] millisecondCounter;
	wire millisecondCounterExpired = (millisecondCounter == 32'd1);
	
	/** decode bus signals **/
	wire controlRegisterSelected = (busEnable & ~busAddress[2]);
	wire divisorRegisterSelected = (busEnable & busAddress[2]);
	
	/** bus-visible registers **/
	reg[31:0] divisorRegister;
	reg interruptEnableRegister;
	reg alarmRegister;
	
	/** logic **/
	always @(posedge clock) begin
		if (reset) begin
			prescalingCounter <= prescalingCounterInitializationValue;
			millisecondCounter <= 32'hffffffff;
			divisorRegister <= 32'hffffffff;
			interruptEnableRegister <= 1'b0;
			alarmRegister <= 1'b0;
		end else begin
			
			/** handle prescaling counter **/
			if (prescalingCounterExpired) begin
				prescalingCounter <= prescalingCounterInitializationValue;
			end else begin
				prescalingCounter <= prescalingCounter - 1;
			end
			
			/** handle millisecond counter and divisor register **/
			if (divisorRegisterSelected & busWrite) begin
				millisecondCounter <= busWriteData;
				divisorRegister <= busWriteData;
			end else if (prescalingCounterExpired) begin
				if (millisecondCounterExpired) begin
					millisecondCounter <= divisorRegister;
				end else begin
					millisecondCounter <= millisecondCounter - 1;
				end
			end

			/** handle alarm and interrupt enable register **/
			if (controlRegisterSelected & busWrite) begin
				interruptEnableRegister <= busWriteData[1];
				alarmRegister <= busWriteData[0];
			end else if (prescalingCounterExpired & millisecondCounterExpired) begin
				alarmRegister <= 1'b1;
			end

		end
	end
	
	/** the remaining bus interface **/
	assign busReadData = busAddress[2] ? divisorRegister : {30'd0, interruptEnableRegister, alarmRegister};
	assign busWait = 1'b0;
	assign interrupt = interruptEnableRegister & alarmRegister;
	
endmodule
