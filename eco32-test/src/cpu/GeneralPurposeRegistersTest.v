`default_nettype none
`timescale 1ns / 1ps

/**
 * Testbench for the general purpose registers.
 */
module GeneralPurposeRegistersTest ();

	/** includes **/
	`include "VUnit.inc.v"
	`include "Clock.inc.v"

	/** the module under test **/
	reg[4:0] index1;
	wire[31:0] readData1;
	reg[4:0] index2;
	wire[31:0] readData2;
	reg[31:0] writeData2;
	reg writeEnable2;
	GeneralPurposeRegisters generalPurposeRegisters (
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
		$dumpfile("cpu/GeneralPurposeRegistersTest.vcd");
		$dumpvars;

		/** initialize **/
		index1 <= 5'd0;
		index2 <= 5'd0;
		writeData2 <= 32'h00000000;
		writeEnable2 <= 1'b0;
		
		for (i=0; i<32; i=i+1) begin
			generalPurposeRegisters.valueArray[i] <= 32'h00000000;
		end
		generalPurposeRegisters.valueArray[5] <= 32'h12345678;
		generalPurposeRegisters.valueArray[3] <= 32'h44444444;
		generalPurposeRegisters.valueArray[7] <= 32'h33333333;
		#5;
		
		/** test reading **/
		
		assertionContext <= "read 1";
		index1 <= 5'd2;
		index2 <= 5'd20;
		#20;
		assert(readData1 == 32'h00000000, "value 1");
		assert(readData2 == 32'h00000000, "value 2");

		assertionContext <= "read 2";
		index1 <= 5'd3;
		index2 <= 5'd20;
		#20;
		assert(readData1 == 32'h44444444, "value 1");
		assert(readData2 == 32'h00000000, "value 2");

		assertionContext <= "read 3";
		index1 <= 5'd5;
		index2 <= 5'd7;
		#20;
		assert(readData1 == 32'h12345678, "value 1");
		assert(readData2 == 32'h33333333, "value 2");
		
		/** test writing **/

		assertionContext <= "write 1";
		index1 <= 5'd7;
		index2 <= 5'd10;
		writeData2 <= 32'h17171717;
		writeEnable2 <= 1'b1;
		#20;
		assert(readData1 == 32'h33333333, "value 1");
		assert(readData2 == 32'h17171717, "value 2");
		assert(generalPurposeRegisters.valueArray[10] == 32'h17171717, "written array cell");

		assertionContext <= "write 2";
		index1 <= 5'd10;
		index2 <= 5'd5;
		writeData2 <= 32'h00336699;
		writeEnable2 <= 1'b1;
		#20;
		assert(readData1 == 32'h17171717, "value 1");
		assert(readData2 == 32'h00336699, "value 2");
		assert(generalPurposeRegisters.valueArray[5] == 32'h00336699, "written array cell");

		assertionContext <= "write 3";
		index1 <= 5'd7;
		index2 <= 5'd7;
		writeData2 <= 32'habcdabcd;
		writeEnable2 <= 1'b1;
		#20;
		assert(readData1 == 32'h33333333, "value 1");
		assert(readData2 == 32'habcdabcd, "value 2");
		assert(generalPurposeRegisters.valueArray[7] == 32'habcdabcd, "written array cell");
		
		/** no longer writing **/

		assertionContext <= "read again";
		index1 <= 5'd3;
		index2 <= 5'd3;
		writeData2 <= 32'h19876543;
		writeEnable2 <= 1'b0;
		#20;
		assert(readData1 == 32'h44444444, "value 1");
		assert(readData2 == 32'h44444444, "value 2");
		assert(generalPurposeRegisters.valueArray[3] == 32'h44444444, "not-written array cell");

		/** done **/
		#100 $finish;
	end
		
endmodule
