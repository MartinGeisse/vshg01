`default_nettype none
`timescale 1ns / 1ps

/**
 * Interface between the Eco32 bus and the flash ROM.
 */
module BusTimer (
		input clock,
		input reset,
		input busEnable,
		input busWrite,
		input[1:0] busAccessSize,
		input[20:0] busAddress,
		input[31:0] busWriteData,
		output[31:0] busReadData,
		output busWait,
		
		
		output romChipEnableInverted;
		output romOutputEnableInverted;
		output romWriteEnableInverted;
		output romResetInverted;
		output romByteInverted;
		output[19:0] romAddress;
		input[15:0] romData;
	);

***
	
endmodule
