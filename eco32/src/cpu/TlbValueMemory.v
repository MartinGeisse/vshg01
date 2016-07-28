`default_nettype none
`timescale 1ns / 1ps

/**
 * TLB value memory. This is a dual-port 32x22 memory, used to store the 20-bit physical address
 * as well as the write and valid bits of each of the 32 TLB entries. One port has synchronous read
 * access. The second port has synchronous read/write access. This allows implementation
 * in a block RAM.
 */
module TlbValueMemory(

		/** the clock **/
		input clock,
		
		/** port 1 index **/
		input[4:0] index1,
		
		/** port 1 read data (synchronously read) **/
		output reg[21:0] readData1,
		
		/** port 2 index **/
		input[4:0] index2,
		
		/** port 2 read data (synchronously read) **/
		output reg[21:0] readData2,

		/** port 2 write data (synchronously written) **/
		input[21:0] writeData2,
		
		/** port 2 write enable **/
		input writeEnable2
		
	);

	/** the actual value array **/
	reg[21:0] valueArray[0:31];

	/** read/write logic **/
	always @(posedge clock) begin
		readData1 <= valueArray[index1];
		if (writeEnable2 == 0) begin
			readData2 <= valueArray[index2];
		end else begin
			readData2 <= writeData2;
			valueArray[index2] <= writeData2;
		end
	end

endmodule
