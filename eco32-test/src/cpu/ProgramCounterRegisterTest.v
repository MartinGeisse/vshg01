`default_nettype none
`timescale 1ns / 1ps

/**
 * Testbench for the virtual address register.
 */
module ProgramCounterRegisterTest ();

	/** includes **/
	`include "VUnit.inc.v"
	`include "Clock.inc.v"
	`include "../../eco32/src/cpu/ProgramCounterConstants.inc.v"

	/** the module under test **/
	reg reset;
	reg writeEnable;
	reg[`PC_SOURCE_WIDTH-1:0] writeDataSource;
	reg[29:0] extendedImmediateValue;
	reg[29:0] extendedJumpOffset;
	reg[31:0] explicitValue;
	reg vectorBit;
	wire[31:0] programCounter;
	ProgramCounterRegister programCounterRegister(
		.clock(clock),
		.reset(reset),
		.writeEnable(writeEnable),
		.writeDataSource(writeDataSource),
		.extendedImmediateValue(extendedImmediateValue),
		.extendedJumpOffset(extendedJumpOffset),
		.explicitValue(explicitValue),
		.vectorBit(vectorBit),
		.programCounter(programCounter)
	);

	/** simulation **/
	initial begin
		$dumpfile("cpu/ProgramCounterRegisterTest.vcd");
		$dumpvars;
		
		/** initialize input values **/
		extendedImmediateValue <= 30'h100;
		extendedJumpOffset <= 30'h200;
		explicitValue <= 32'h12345678;
		
		/** reset **/
		#5 reset <= 1'b1;
		#20 reset <= 1'b0;
		
		/** check initial state **/
		assertionContext = "initial state";
		assert(programCounter == 32'he0000000, "test 1");
		
		/** increment **/
		assertionContext = "test";
		programCounterRegister.programCounter <= 32'h9abc9abc;
		writeEnable <= 1'b1;
		writeDataSource <= `PC_SOURCE_INCREMENT;
		#20 assert(programCounter == 32'h9abc9ac0, "test 2");

		/** hold **/
		programCounterRegister.programCounter <= 32'h9abc9abc;
		writeEnable <= 1'b0;
		writeDataSource <= `PC_SOURCE_INCREMENT;
		#20 assert(programCounter == 32'h9abc9abc, "test 3");

		/** increment **/
		programCounterRegister.programCounter <= 32'h9abc9abc;
		writeEnable <= 1'b1;
		writeDataSource <= `PC_SOURCE_INCREMENT;
		#20 assert(programCounter == 32'h9abc9ac0, "test 4");

		/** add immediate **/
		programCounterRegister.programCounter <= 32'h9abc9abc;
		writeEnable <= 1'b1;
		writeDataSource <= `PC_SOURCE_ADD_IMMEDIATE;
		#20 assert(programCounter == 32'h9abc9ebc, "test 5");

		/** add offset **/
		programCounterRegister.programCounter <= 32'h9abc9abc;
		writeEnable <= 1'b1;
		writeDataSource <= `PC_SOURCE_ADD_OFFSET;
		#20 assert(programCounter == 32'h9abca2bc, "test 6");

		/** load explicit value **/
		programCounterRegister.programCounter <= 32'h9abc9abc;
		writeEnable <= 1'b1;
		writeDataSource <= `PC_SOURCE_EXPLICIT;
		#20 assert(programCounter == 32'h12345678, "test 7");

		/** load exception handler entry point (vector = 0) **/
		programCounterRegister.programCounter <= 32'h9abc9abc;
		writeEnable <= 1'b1;
		writeDataSource <= `PC_SOURCE_EXCEPTION;
		vectorBit <= 1'b0;
		#20 assert(programCounter == 32'he0000004, "test 8");

		/** load exception handler entry point (vector = 1) **/
		programCounterRegister.programCounter <= 32'h9abc9abc;
		writeEnable <= 1'b1;
		writeDataSource <= `PC_SOURCE_EXCEPTION;
		vectorBit <= 1'b1;
		#20 assert(programCounter == 32'hc0000004, "test 9");

		/** load user-space TLB miss handler entry point (vector = 0) **/
		programCounterRegister.programCounter <= 32'h9abc9abc;
		writeEnable <= 1'b1;
		writeDataSource <= `PC_SOURCE_USER_TLB_MISS;
		vectorBit <= 1'b0;
		#20 assert(programCounter == 32'he0000008, "test 10");

		/** load user-space TLB miss handler entry point (vector = 1) **/
		programCounterRegister.programCounter <= 32'h9abc9abc;
		writeEnable <= 1'b1;
		writeDataSource <= `PC_SOURCE_USER_TLB_MISS;
		vectorBit <= 1'b1;
		#20 assert(programCounter == 32'hc0000008, "test 11");
		
		/** done **/
		#100 $finish;
	end
		
endmodule
