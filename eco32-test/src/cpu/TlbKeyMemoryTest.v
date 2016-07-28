`default_nettype none
`timescale 1ns / 1ps

/**
 * Testbench for the TLB key memory.
 */
module TlbKeyMemoryTest ();

	/** includes **/
	`include "VUnit.inc.v"
	`include "Clock.inc.v"

	/** the module under test **/
	reg[4:0] accessIndex;
	wire[19:0] readValue;
	reg[19:0] writeValue;
	reg writeEnable;
	reg[19:0] searchKey;
	wire found;
	wire[4:0] foundIndex;
	TlbKeyMemory tlbKeyMemory (
		.clock(clock),
		.accessIndex(accessIndex),
		.readValue(readValue),
		.writeValue(writeValue),
		.writeEnable(writeEnable),
		.searchKey(searchKey),
		.found(found),
		.foundIndex(foundIndex)
	);
	
	/** simulation **/
	reg[31:0] i;
	initial begin
		$dumpfile("cpu/TlbKeyMemoryTest.vcd");
		$dumpvars;

		/** initialize **/
		accessIndex <= 5'd0;
		writeValue <= 20'h00000;
		writeEnable <= 1'b0;
		searchKey <= 20'h00000;
		for (i=0; i<32; i=i+1) begin
			tlbKeyMemory.keyArray[i] <= 20'h0;
		end
		tlbKeyMemory.keyArray[5] <= 20'h12345;
		tlbKeyMemory.keyArray[3] <= 20'h44444;
		tlbKeyMemory.keyArray[7] <= 20'h44444;
		#5;
		
		/** test searching **/
		searchKey <= 20'h12121;
		#1 assert(~found, "not found");

		searchKey <= 20'h12345;
		#1
		assert(found, "found 1");
		assert(foundIndex == 5'd5, "found index 1");

		searchKey <= 20'h44444;
		#1
		assert(found, "found 2");
		assert(foundIndex == 5'd3, "found index 2");

		searchKey <= 20'h22222;
		#1 assert(~found, "not found");
		
		/** test reading **/
		accessIndex <= 5'd2;
		#1 assert(readValue == 20'h00000, "read 1");
		accessIndex <= 5'd3;
		#1 assert(readValue == 20'h44444, "read 2");
		accessIndex <= 5'd4;
		#1 assert(readValue == 20'h00000, "read 3");
		accessIndex <= 5'd5;
		#1 assert(readValue == 20'h12345, "read 4");
		accessIndex <= 5'd7;
		#1 assert(readValue == 20'h44444, "read 5");
		
		/** test writing **/
		accessIndex <= 5'd6;
		writeValue <= 20'h91919;
		writeEnable <= 1'b1;
		#1 assert(tlbKeyMemory.keyArray[6] == 20'h00000, "write, before edge");
		#20 assert(tlbKeyMemory.keyArray[6] == 20'h91919, "write, after edge");

		/** done **/
		#100 $finish;
	end
		
endmodule
