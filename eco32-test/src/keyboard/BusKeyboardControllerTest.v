`default_nettype none
`timescale 1ns / 1ps

/**
 * Testbench for the bus keyboard controller.
 */
module BusKeyboardControllerTest ();

	/** include file configuration **/
	`define BUS_ADDRESS_HIGH 2
	`define BUS_ADDRESS_LOW 2
	`define BUS_READ_DATA_WIDTH 8
	`define BUS_WRITE_DATA_WIDTH 8
	
	/** includes **/
	`include "VUnit.inc.v"
	`include "Clock.inc.v"
	`include "BusSlaveTestHelper.inc.v"

	/** helper tasks **/
	task checkControlRegister(input expectedInterruptEnable, input expectedReady, input[799:0] message);
		begin
			performBusReadAndCheck(1'b0, {6'b0, expectedInterruptEnable, expectedReady}, message);
		end
	endtask

	task writeControlRegister(input interruptEnable, input ready);
		begin
			performBusWrite(1'b0, {6'b0, interruptEnable, ready});
		end
	endtask
	
	task checkDataRegister(input[7:0] expectedValue, input[799:0] message);
		begin
			performBusReadAndCheck(1'b1, expectedValue, message);
		end
	endtask

	task writeDataRegister(input[7:0] value);
		begin
			performBusWrite(1'b1, value);
		end
	endtask
	
	/** simulation **/
	reg reset;
	reg[159:0] currentTest;
	initial begin
		$dumpfile("keyboard/BusKeyboardControllerTest.vcd");
		$dumpvars;
		
		/** initialize testing **/
		ps2Clock <= 1'b0;
		ps2Data <= 1'b0;
		busKeyboardController.rawKeyboardController.dataReady <= 1'b0;
		busKeyboardController.rawKeyboardController.dataOut <= 8'h00;
		
		/** reset **/
		#5 reset <= 1'b1;
		#1000 reset <= 1'b0;
		
		/** ensure that the PS/2 signals are passed on correctly **/
		assert(~busKeyboardController.rawKeyboardController.ps2Clock, "initial PS/2 clock");
		assert(~busKeyboardController.rawKeyboardController.ps2Data, "initial PS/2 data");
		ps2Clock <= 1'b1;
		#1;
		assert(busKeyboardController.rawKeyboardController.ps2Clock, "initial PS/2 clock");
		assert(~busKeyboardController.rawKeyboardController.ps2Data, "initial PS/2 data");
		ps2Data <= 1'b1;
		#1;
		assert(busKeyboardController.rawKeyboardController.ps2Clock, "initial PS/2 clock");
		assert(busKeyboardController.rawKeyboardController.ps2Data, "initial PS/2 data");
		#18;
		
		/** check initial state (the data register is initially undefined **/
		assert(~interrupt, "initial interrupt");
		checkControlRegister(1'b0, 1'b0, "initial control register value");
		
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

		/** test preserved bits in the control register **/
		performBusWrite(1'b0, 32'hffffffff);
		performBusReadAndCheck(1'b0, 32'h00000003, "check preserved bits in control register");
		performBusWrite(1'b0, 32'h00000000);
		
		/** test receiving a byte **/
		busKeyboardController.rawKeyboardController.dataReady <= 1'b1;
		busKeyboardController.rawKeyboardController.dataOut <= 8'h42;
		#20;
		busKeyboardController.rawKeyboardController.dataReady <= 1'b0;

		assert(~interrupt, "receive byte interrupt (immediate)");
		checkControlRegister(1'b0, 1'b1, "receive byte control register value (immediate)");
		checkDataRegister(8'h42, "receive byte data register value");
		assert(~interrupt, "receive byte interrupt (after reading data)");
		checkControlRegister(1'b0, 1'b0, "receive byte control register value (after reading data)");
		
		/** test a non-receive event in which the data signals change but dataReady remains low **/
		busKeyboardController.rawKeyboardController.dataOut <= 8'h99;
		#20;
		assert(~interrupt, "non-receive interrupt");
		checkControlRegister(1'b0, 1'b0, "non-receive control register value");
		checkDataRegister(8'h42, "non-receive data register value");
		
		/** test writing to the data register (should not work) **/
		writeDataRegister(32'habcdabcd);
		assert(~interrupt, "write data register - interrupt");
		checkControlRegister(1'b0, 1'b0, "write data register - control register value");
		checkDataRegister(8'h42, "write data register - data register value");
		
		/** enable interrupts, receive another byte, and check the interrupt signal **/
		writeControlRegister(1'b1, 1'b0);
		assert(~interrupt, "receive with interrupts enabled - enable interrupts");

		busKeyboardController.rawKeyboardController.dataReady <= 1'b1;
		busKeyboardController.rawKeyboardController.dataOut <= 8'h7e;
		#20;
		busKeyboardController.rawKeyboardController.dataReady <= 1'b0;
		
		assert(interrupt, "receive with interrupts enabled - interrupt (immediate)");
		checkControlRegister(1'b1, 1'b1, "receive with interrupts enabled - control register value (immediate)");
		checkDataRegister(8'h7e, "receive with interrupts enabled - data register value");
		assert(~interrupt, "receive with interrupts enabled - interrupt (after reading data)");
		checkControlRegister(1'b1, 1'b0, "receive with interrupts enabled - control register value (after reading data)");
		
		/** receive a data byte, and when it is read receive another one. We should get both. **/
		#200;
		busKeyboardController.rawKeyboardController.dataReady <= 1'b1;
		busKeyboardController.rawKeyboardController.dataOut <= 8'hab;
		#20;
		busKeyboardController.rawKeyboardController.dataReady <= 1'b0;
		#200;
		busKeyboardController.rawKeyboardController.dataReady <= 1'b1;
		busKeyboardController.rawKeyboardController.dataOut <= 8'hcd;
		checkDataRegister(8'hab, "arrive just in time - read first data byte");
		busKeyboardController.rawKeyboardController.dataReady <= 1'b0;

		assert(interrupt, "arrive just in time - check interrupts after reading the first byte");
		checkControlRegister(1'b1, 1'b1, "arrive just in time - control register value (after reading first byte)");
		checkDataRegister(8'hcd, "arrive just in time - read second data byte");
		assert(~interrupt, "arrive just in time - check interrupts after reading the second byte");
		checkControlRegister(1'b1, 1'b0, "arrive just in time - control register value (after reading second byte)");

		/** done **/
		#100 $finish;
	end

	/** bus operations should occur immediately for the keyboard controller **/
	always @(busWait) begin
		assert(~busWait, "bus wait");
	end
		
	/** the module under test **/
	wire interrupt;
	reg ps2Clock;
	reg ps2Data;
	BusKeyboardController busKeyboardController (
		.clock(clock),
		.reset(reset),
		.busEnable(busEnable),
		.busWrite(busWrite),
		.busAddress(busAddress),
		.busWriteData(busWriteData),
		.busReadData(busReadData),
		.busWait(busWait),
		.interrupt(interrupt),
		.ps2Clock(ps2Clock),
		.ps2Data(ps2Data)
	);
	
endmodule

/**
 * This is a mock module for the raw keyboard controller.
 */
module RawKeyboardController (
		input clock,
		input reset,
		output reg dataReady,
		output reg[7:0] dataOut,
		input ps2Clock,
		input ps2Data
	);
endmodule
