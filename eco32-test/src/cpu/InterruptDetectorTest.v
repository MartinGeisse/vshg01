`default_nettype none
`timescale 1ns / 1ps

/**
 * Testbench for the interrupt detector of the CPU.
 */
module InterruptDetectorTest ();

	/** includes **/
	`include "VUnit.inc.v"
	`include "Clock.inc.v"
	`include "../../eco32/src/cpu/CpuConstants.inc.v"

	/** the module under test **/
	reg[15:0] externalInterruptLines;
	reg[15:0] pswInterruptMask;
	reg pswInterruptEnable;
	wire interruptActive;
	wire[3:0] index;
	InterruptDetector interruptDetector (
		.clock(clock),
		.externalInterruptLines(externalInterruptLines),
		.pswInterruptMask(pswInterruptMask),
		.pswInterruptEnable(pswInterruptEnable),
		.interruptActive(interruptActive),
		.index(index)
	);
	
	/** simulation **/
	initial begin
		$dumpfile("cpu/InterruptDetectorTest.vcd");
		$dumpvars;

		/** initialize **/
		pswInterruptMask <= 16'b1111111111111111;
		pswInterruptEnable <= 1'b1;
		#5 externalInterruptLines <= 16'b0000000000000000;
		#20 assert(~interruptActive, "initial interruptActive");

		/** enable an interrupt **/
		externalInterruptLines[5] <= 1'b1;
		#1 assert(~interruptActive, "asynchronous interruptActive after enable");
		#19 assert(interruptActive, "synchronous interruptActive after enable");
		assert(index == 4'd5, "synchronous index after enable");

		/** enable another interrupt **/
		externalInterruptLines[8] <= 1'b1;
		#1 assert(interruptActive, "asynchronous interruptActive after enable second");
		assert(index == 4'd5, "asynchronous index after enable second");
		#19 assert(interruptActive, "synchronous interruptActive after enable second");
		assert(index == 4'd5, "synchronous index after enable second");
		
		/** disable the first one **/
		externalInterruptLines[5] <= 1'b0;
		#1 assert(interruptActive, "asynchronous interruptActive after disable first");
		assert(index == 4'd5, "asynchronous index after disable first");
		#19 assert(interruptActive, "synchronous interruptActive after disable first");
		assert(index == 4'd8, "synchronous index after disable first");

		/** disable the second one **/
		externalInterruptLines[8] <= 1'b0;
		#1 assert(interruptActive, "asynchronous interruptActive after disable second");
		assert(index == 4'd8, "asynchronous index after disable second");
		#19 assert(~interruptActive, "synchronous interruptActive after disable second");

		/** enable two interrupts but de-activate the interrupt enable flag, later enable again **/
		externalInterruptLines[5] <= 1'b1;
		externalInterruptLines[8] <= 1'b1;
		pswInterruptEnable <= 1'b0;
		#1 assert(~interruptActive, "asynchronous interruptActive after interrupt disable");
		#19 assert(~interruptActive, "synchronous interruptActive after interrupt disable");
		pswInterruptEnable <= 1'b1;
		#1 assert(~interruptActive, "asynchronous interruptActive after interrupt enable");
		#19 assert(interruptActive, "synchronous interruptActive after interrupt enable");
		assert(index == 4'd5, "synchronous index after interrupt enable");

		/** enable two interrupts but de-activate the first one using its mask flag, later disable the second, then enable the first **/
		externalInterruptLines[5] <= 1'b1;
		externalInterruptLines[8] <= 1'b1;
		pswInterruptEnable <= 1'b1;
		pswInterruptMask <= 16'b1111111111011111;
		#20 assert(interruptActive, "synchronous interruptActive after interrupt mask");
		assert(index == 4'd8, "synchronous index after interrupt mask");
		pswInterruptMask <= 16'b1111111011011111;
		#20 assert(~interruptActive, "synchronous interruptActive after interrupt mask second");
		pswInterruptMask <= 16'b1111111011111111;
		#20 assert(interruptActive, "synchronous interruptActive after interrupt unmask first");
		assert(index == 4'd5, "synchronous index after interrupt unmask first");

		/** done **/
		#100 $finish;
	end
		
endmodule
