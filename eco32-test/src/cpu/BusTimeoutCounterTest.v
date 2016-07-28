`default_nettype none
`timescale 1ns / 1ps

/**
 * Testbench for the bus timeout counter.
 */
module BusTimeoutCounterTest ();

	/** includes **/
	`include "VUnit.inc.v"
	`include "Clock.inc.v"

	/** the module under test **/
	reg clear;
	wire alarm;
	BusTimeoutCounter busTimeoutCounter (
		.clock(clock),
		.clear(clear),
		.alarm(alarm)
	);
	
	/** simulation **/
	reg[31:0] i;
	
	task countClocksUntilAlarm;
		begin
			i = 0;
			while(~alarm) begin
				#20 i = i + 1;
			end
		end
	endtask
	
	initial begin
		$dumpfile("cpu/VirtualAddressRegisterTest.vcd");
		$dumpvars;
		#5;

		/** test normal operation **/
		clear <= 1'b1;
		#20;
		clear <= 1'b0;
		countClocksUntilAlarm();
		assert(i > 250 && i < 260, "number of clocks until alarm");
		
		/** test repeated alarm without clearing **/
		#20 countClocksUntilAlarm();
		assert(i > 250 && i < 260, "number of clocks until repeated alarm");

		/** test clearing the counter halfway **/
		clear <= 1'b1;
		#20;
		clear <= 1'b0;
		#2000;
		clear <= 1'b1;
		#20;
		clear <= 1'b0;
		countClocksUntilAlarm();
		assert(i > 250 && i < 260, "number of clocks until alarm");

		/** done **/
		#100 $finish;
	end
		
endmodule
