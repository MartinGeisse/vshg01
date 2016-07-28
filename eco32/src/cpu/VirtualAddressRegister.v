`default_nettype none
`timescale 1ns / 1ps

/**
 * The virtual address register. This module basically acts as a register with some special
 * support for computing its next value. The value of this register is used as
 * the input for address translation. 
 */
module VirtualAddressRegister (

		/** the clock **/
		input clock,
		
		/** the write-enable for this register **/
		input writeEnable,
		
		/** this value selects the new value to load into this register when writeEnable is 1 **/
		input[`VIRTUAL_ADDRESS_REGISTER_WRITE_DATA_SOURCE_WIDTH-1:0] writeDataSource,
		
		/**
		 * The program counter value to load when the writeDataSource
		 * is VIRTUAL_ADDRESS_REGISTER_WRITE_DATA_SOURCE_PC.
		 */
		input[31:0] programCounter,
		
		/**
		 * The address computation result to load when the writeDataSource
		 * is VIRTUAL_ADDRESS_REGISTER_WRITE_DATA_SOURCE_ADDRESS_COMPUTATION_RESULT.
		 */
		input[31:0] addressComputationResult,
		
		/**
		 * The value to load when the writeDataSource is
		 * VIRTUAL_ADDRESS_REGISTER_WRITE_DATA_SOURCE_MMU_SPECIAL_REGISTER_READ_DATA.
		 */
		input[31:0] mmuSpecialRegisterReadData,
		
		/** the current value of this register **/
		output reg[31:0] virtualAddress

	);

	/** include constant definitions **/
	`include "VirtualAddressRegisterConstants.inc.v"
	
	/** synchronous loading logic **/
	always @(posedge clock) begin
		if (writeEnable) begin
			case (writeDataSource)
			
				/** load the PC **/
				`VIRTUAL_ADDRESS_REGISTER_WRITE_DATA_SOURCE_PC: begin
					virtualAddress <= programCounter;
				end

				/** load the address computation result **/
				`VIRTUAL_ADDRESS_REGISTER_WRITE_DATA_SOURCE_ADDRESS_COMPUTATION_RESULT: begin
					virtualAddress <= addressComputationResult;
				end
				
				/** load the MMU special register read data **/
				`VIRTUAL_ADDRESS_REGISTER_WRITE_DATA_SOURCE_MMU_SPECIAL_REGISTER_READ_DATA: begin
					virtualAddress <= mmuSpecialRegisterReadData;
				end
				
				/** for other values of writeDataSource, the new value is undefined **/
				default: begin
					virtualAddress <= 32'bxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx;
				end
			
			endcase
		end
	end

endmodule
