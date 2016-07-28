`default_nettype none
`timescale 1ns / 1ps

/**
 * Testbench for the virtual address register.
 */
module VirtualAddressRegisterTest ();

	/** includes **/
	`include "VUnit.inc.v"
	`include "Clock.inc.v"
	`include "../../eco32/src/cpu/VirtualAddressRegisterConstants.inc.v"

	/** the module under test **/
	reg writeEnable;
	reg[`VIRTUAL_ADDRESS_REGISTER_WRITE_DATA_SOURCE_WIDTH-1:0] writeDataSource;
	reg[31:0] programCounter;
	reg[31:0] addressComputationResult;
	reg[31:0] mmuSpecialRegisterReadData;
	wire[31:0] virtualAddress;
	VirtualAddressRegister virtualAddressRegister (
		.clock(clock),
		.writeEnable(writeEnable),
		.programCounter(programCounter),
		.virtualAddress(virtualAddress),
		.writeDataSource(writeDataSource),
		.addressComputationResult(addressComputationResult),
		.mmuSpecialRegisterReadData(mmuSpecialRegisterReadData)
	);
	
	/** simulation **/
	initial begin
		$dumpfile("cpu/VirtualAddressRegisterTest.vcd");
		$dumpvars;
		#5;
		
		/** test **/
		assertionContext <= "test";
		
		writeEnable <= 1'b1;
		writeDataSource <= `VIRTUAL_ADDRESS_REGISTER_WRITE_DATA_SOURCE_PC;
		programCounter <= 32'h12345678;
		addressComputationResult <= 32'hdefdefde;
		mmuSpecialRegisterReadData <= 32'hea00ea00;
		#20 assert(virtualAddress == 32'h12345678, "test load PC (1)");
		
		writeEnable <= 1'b1;
		writeDataSource <= `VIRTUAL_ADDRESS_REGISTER_WRITE_DATA_SOURCE_PC;
		programCounter <= 32'habcdabcd;
		addressComputationResult <= 32'hdefdefde;
		mmuSpecialRegisterReadData <= 32'hea00ea00;
		#20 assert(virtualAddress == 32'habcdabcd, "test load PC (2)");
		
		writeEnable <= 1'b0;
		writeDataSource <= `VIRTUAL_ADDRESS_REGISTER_WRITE_DATA_SOURCE_PC;
		programCounter <= 32'h01020304;
		addressComputationResult <= 32'hdefdefde;
		mmuSpecialRegisterReadData <= 32'hea00ea00;
		#20 assert(virtualAddress == 32'habcdabcd, "test hold value");

		writeEnable <= 1'b1;
		writeDataSource <= `VIRTUAL_ADDRESS_REGISTER_WRITE_DATA_SOURCE_ADDRESS_COMPUTATION_RESULT;
		programCounter <= 32'h01020304;
		addressComputationResult <= 32'hdefdefde;
		mmuSpecialRegisterReadData <= 32'hea00ea00;
		#20 assert(virtualAddress == 32'hdefdefde, "test load address computation result");

		writeEnable <= 1'b1;
		writeDataSource <= `VIRTUAL_ADDRESS_REGISTER_WRITE_DATA_SOURCE_MMU_SPECIAL_REGISTER_READ_DATA;
		programCounter <= 32'h01020304;
		addressComputationResult <= 32'hdefdefde;
		mmuSpecialRegisterReadData <= 32'hea00ea00;
		#20 assert(virtualAddress == 32'hea00ea00, "test load MMU special register read data");
		
		/** done **/
		#100 $finish;
	end
		
endmodule
