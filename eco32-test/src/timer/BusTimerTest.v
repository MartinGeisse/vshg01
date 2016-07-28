`default_nettype none
`timescale 1ns / 1ps

/**
 * Testbench for the bus-attached timer device.
 */
module BusTimerTest ();

	/** include file configuration **/
	`define BUS_ADDRESS_HIGH 2
	`define BUS_ADDRESS_LOW 2
	`define BUS_READ_DATA_WIDTH 32
	`define BUS_WRITE_DATA_WIDTH 32
	
	/** includes **/
	`include "VUnit.inc.v"
	`include "Clock.inc.v"
	`include "BusSlaveTestHelper.inc.v"

	/** helper tasks **/
	task checkControlRegister(input expectedInterruptEnable, input expectedAlarm, input[799:0] message);
		begin
			performBusReadAndCheck(1'b0, {30'b0, expectedInterruptEnable, expectedAlarm}, message);
		end
	endtask

	task writeControlRegister(input interruptEnable, input alarm);
		begin
			performBusWrite(1'b0, {30'b0, interruptEnable, alarm});
		end
	endtask
	
	task checkDivisorRegister(input[31:0] expectedValue, input[799:0] message);
		begin
			performBusReadAndCheck(1'b1, expectedValue, message);
		end
	endtask

	task writeDivisorRegister(input[31:0] value);
		begin
			performBusWrite(1'b1, value);
		end
	endtask

	/** simulation **/
	reg reset;
	initial begin
		$dumpfile("timer/BusTimerTest.vcd");
		$dumpvars;
		
		/** reset **/
		#5 reset <= 1'b1;
		#1000 reset <= 1'b0;
		disableSimulationTimeout <= 1'b1;

		/** check initial state (the data register is initially undefined **/
		assert(~interrupt, "initial interrupt");
		checkControlRegister(1'b0, 1'b0, "initial control register value");
		checkDivisorRegister(32'hffffffff, "initial divisor register value");
		
		/** check writing to the control register **/
		writeControlRegister(1'b1, 1'b0);
		assert(~interrupt, "write control (1, 0) interrupt");
		checkControlRegister(1'b1, 1'b0, "write control (1, 0) value");

		writeControlRegister(1'b0, 1'b1);
		assert(~interrupt, "write control (0, 1) interrupt");
		checkControlRegister(1'b0, 1'b1, "write control (0, 1) value");

		writeControlRegister(1'b1, 1'b1);
		assert(interrupt, "write control (1, 1) interrupt");
		checkControlRegister(1'b1, 1'b1, "write control (1, 1) value");

		writeControlRegister(1'b0, 1'b0);
		assert(~interrupt, "write control (0, 0) interrupt");
		checkControlRegister(1'b0, 1'b0, "write control (0, 0) value");

		writeDivisorRegister(32'd1234);
		assert(~interrupt, "write divisor register - interrupts");
		checkControlRegister(1'b0, 1'b0, "write divisor register - control register value");
		checkDivisorRegister(32'd1234, "write divisor register - check modified value");

		/** test preserved bits in the control register **/
		performBusWrite(1'b0, 32'hffffffff);
		performBusReadAndCheck(1'b0, 32'h00000003, "check preserved bits in control register");
		performBusWrite(1'b0, 32'h00000000);
		
		/** ensure that writing the divisor has no effect on the control register **/
		writeControlRegister(1'b1, 1'b1);
		writeDivisorRegister(32'd1234);
		assert(interrupt, "check register interactions - interrupts");
		checkControlRegister(1'b1, 1'b1, "check register interactions - control register value");
		checkDivisorRegister(32'd1234, "check register interactions - divisor register value");
		writeControlRegister(1'b0, 1'b0);

		/**
		 * Write divisor to a defined value (also to reset counter) and wait for alarm.
		 * We set the divisor to 10 milliseconds. The alarm should not have occurred after
		 * 9 ms, and it should have after 10 ms. We can't tell any more detailed because
		 * we cannot / don't want to synchronize to the internal prescaling counter.
		 */
		writeDivisorRegister(32'd10);
		#9000000;
		checkControlRegister(1'b0, 1'b0, "wait for alarm - read A");
		#1000000;
		checkControlRegister(1'b0, 1'b1, "wait for alarm - read B");
		writeControlRegister(1'b0, 1'b0);
		
		/** after another 2 ms, the alarm should not have fired again yet **/
		#2000000;
		checkControlRegister(1'b0, 1'b0, "no other alarm yet");
		
		/**
		 * We set the divisor to 2 ms, wait 1 ms, set it to 2 ms again, wait another 1 ms,
		 * and once more. The alarm should not have fired since setting the divisor resets
		 * the microsecond counter. Note that waiting for more than 1 ms at once could experience
		 * two prescaler expirations, thus ruining the experiment, so we don't do that.
		 */
		writeDivisorRegister(32'd2);
		#1000000;
		writeDivisorRegister(32'd2);
		#1000000;
		writeDivisorRegister(32'd2);
		#1000000;
		checkControlRegister(1'b0, 1'b0, "reset millisecond counter by setting divisor");
		
		/**
		 * Another 1 ms later, the counter should have fired.
		 */
		#1000000;
		checkControlRegister(1'b0, 1'b1, "reset millisecond counter by setting divisor - finally wait for alarm");
		
		/**
		 * Set the divisor to 0, then wait a sane amount of time. The alarm should not have
		 * fired. (It does so after 2^32 milliseconds, or 2^22 seconds, which is more
		 * than 50 days.
		 *
		 * We wait for 20ms, then stop.
		 */
		writeDivisorRegister(32'd0);
		writeControlRegister(1'b0, 1'b0);
		#20000000;
		checkControlRegister(1'b0, 1'b0, "check with zero divisor");
		 
		$finish;
	end
	
	/** the module under test **/
	wire interrupt;
	BusTimer busTimer (
		.clock(clock),
		.reset(reset),
		.busEnable(busEnable),
		.busWrite(busWrite),
		.busAddress(busAddress),
		.busWriteData(busWriteData),
		.busReadData(busReadData),
		.busWait(busWait),
		.interrupt(interrupt)
	);

endmodule
