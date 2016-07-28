`default_nettype none
`timescale 1ns / 1ps

/**
 * General purpose registers. This is a dual-port 32x32 memory. One port has synchronous read
 * access. The second port has synchronous read/write access. This allows implementation
 * in a block RAM.
 *
 * This module does not implement the safety check for the zero register -- that is,
 * the zero register of this module behaves like any other register. To fully
 * implement the zero register of the ECO32, write-protection for that register
 * must be ensured from outside this module.
 */
module GeneralPurposeRegisters(

		/** the clock **/
		input clock,
		
		/** port 1 index **/
		input[4:0] index1,
		
		/** port 1 read data (synchronously read) **/
		output reg[31:0] readData1,
		
		/** port 2 index **/
		input[4:0] index2,
		
		/** port 2 read data (synchronously read) **/
		output reg[31:0] readData2,

		/** port 2 write data (synchronously written) **/
		input[31:0] writeData2,
		
		/** port 2 write enable **/
		input writeEnable2
		
	);

	/** the actual value array **/
	reg[31:0] valueArray[0:31];

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
