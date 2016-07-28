`default_nettype none
`timescale 1ns / 1ps

/**
 * Testbench for the current instruction register.
 */
module CurrentInstructionRegisterTest ();

	/** includes **/
	`include "VUnit.inc.v"
	`include "Clock.inc.v"

	/** the module under test **/
	reg writeEnable;
	reg[31:0] busReadData;
	wire[31:0] currentInstruction;
	CurrentInstructionRegister currentInstructionRegister (
		.clock(clock),
		.writeEnable(writeEnable),
		.busReadData(busReadData),
		.currentInstruction(currentInstruction)
	);
	
	/** simulation **/
	initial begin
		$dumpfile("cpu/CurrentInstructionRegisterTest.vcd");
		$dumpvars;
		#5;
		
		/** test **/
		assertionContext <= "test";
		writeEnable <= 1'b1;
		busReadData <= 32'h12345678;
		#20 assert(currentInstruction == 32'h12345678, "load 1");
		busReadData <= 32'habcdabcd;
		#20 assert(currentInstruction == 32'habcdabcd, "load 2");
		writeEnable <= 1'b0;
		busReadData <= 32'h01010101;
		#20 assert(currentInstruction == 32'habcdabcd, "hold");
		
		/** done **/
		#100 $finish;
	end
		
endmodule
