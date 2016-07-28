`default_nettype none
`timescale 1ns / 1ps

/**
 * Testbench for the TLB value memory.
 */
module TlbValueMemoryTest ();

	/** includes **/
	`include "VUnit.inc.v"
	`include "Clock.inc.v"

	/** the module under test **/
	reg[4:0] index1;
	wire[21:0] readData1;
	reg[4:0] index2;
	wire[21:0] readData2;
	reg[21:0] writeData2;
	reg writeEnable2;
	TlbValueMemory tlbValueMemory (
		.clock(clock),
		.index1(index1),
		.readData1(readData1),
		.index2(index2),
		.readData2(readData2),
		.writeData2(writeData2),
		.writeEnable2(writeEnable2)
	);
	
	/** simulation **/
	reg[31:0] i;
	initial begin
		$dumpfile("cpu/TlbValueMemoryTest.vcd");
		$dumpvars;

		/** initialize **/
		index1 <= 5'd0;
		index2 <= 5'd0;
		writeData2 <= 22'h000000;
		writeEnable2 <= 1'b0;
		
		for (i=0; i<32; i=i+1) begin
			tlbValueMemory.valueArray[i] <= 22'h0;
		end
		tlbValueMemory.valueArray[5] <= 22'h123456;
		tlbValueMemory.valueArray[3] <= 22'h044444;
		tlbValueMemory.valueArray[7] <= 22'h344444;
		#5;
		
		/** test reading **/
		
		assertionContext <= "read 1";
		index1 <= 5'd2;
		index2 <= 5'd20;
		#20;
		assert(readData1 == 22'h000000, "value 1");
		assert(readData2 == 22'h000000, "value 2");

		assertionContext <= "read 2";
		index1 <= 5'd3;
		index2 <= 5'd20;
		#20;
		assert(readData1 == 22'h044444, "value 1");
		assert(readData2 == 22'h000000, "value 2");

		assertionContext <= "read 3";
		index1 <= 5'd5;
		index2 <= 5'd7;
		#20;
		assert(readData1 == 22'h123456, "value 1");
		assert(readData2 == 22'h344444, "value 2");
		
		/** test writing **/

		assertionContext <= "write 1";
		index1 <= 5'd7;
		index2 <= 5'd10;
		writeData2 <= 22'h171717;
		writeEnable2 <= 1'b1;
		#20;
		assert(readData1 == 22'h344444, "value 1");
		assert(readData2 == 22'h171717, "value 2");
		assert(tlbValueMemory.valueArray[10] == 22'h171717, "written array cell");

		assertionContext <= "write 2";
		index1 <= 5'd10;
		index2 <= 5'd5;
		writeData2 <= 22'h003366;
		writeEnable2 <= 1'b1;
		#20;
		assert(readData1 == 22'h171717, "value 1");
		assert(readData2 == 22'h003366, "value 2");
		assert(tlbValueMemory.valueArray[5] == 22'h003366, "written array cell");

		assertionContext <= "write 3";
		index1 <= 5'd7;
		index2 <= 5'd7;
		writeData2 <= 22'h077007;
		writeEnable2 <= 1'b1;
		#20;
		assert(readData1 == 22'h344444, "value 1");
		assert(readData2 == 22'h077007, "value 2");
		assert(tlbValueMemory.valueArray[7] == 22'h077007, "written array cell");
		
		/** no longer writing **/

		assertionContext <= "read again";
		index1 <= 5'd3;
		index2 <= 5'd3;
		writeData2 <= 22'h19876;
		writeEnable2 <= 1'b0;
		#20;
		assert(readData1 == 22'h044444, "value 1");
		assert(readData2 == 22'h044444, "value 2");
		assert(tlbValueMemory.valueArray[3] == 22'h044444, "not-written array cell");

		/** done **/
		#100 $finish;
	end
		
endmodule
