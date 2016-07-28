`default_nettype none
`timescale 1ns / 1ps

/**
 * The program counter register. This module basically acts as a register with some special
 * support for computing its next value.
 */
module ProgramCounterRegister (

		/** the clock **/
		input clock,

		/** the reset signal **/
		input reset, 

		/** the write-enable for this register **/
		input writeEnable,

		/** this value selects the new value to load into this register when writeEnable is 1 **/
		input[`PC_SOURCE_WIDTH-1:0] writeDataSource,

		/** the immediate value to add if writeDataSource is PC_SOURCE_ADD_IMMEDIATE **/
		input[29:0] extendedImmediateValue,

		/** the jump offset to add if writeDataSource is PC_SOURCE_ADD_OFFSET **/
		input[29:0] extendedJumpOffset,

		/** the explicit value to load if writeDataSource is PC_SOURCE_EXPLICIT **/
		input[31:0] explicitValue,

		/**
		 * the vector bit of the PSW, used to obtain the exception handler entry point
		 * if writeDataSource is PC_SOURCE_EXCEPTION or PC_SOURCE_USER_TLB_MISS
		 */
		input vectorBit,

		/** the current value of this register **/
		output reg[31:0] programCounter

	);

	/** include constant definitions **/
	`include "ProgramCounterConstants.inc.v"
	
	/** synchronous loading logic **/
	always @(posedge clock) begin
		if (reset) begin
			programCounter <= 32'he0000000;
		end else if (writeEnable) begin
			case (writeDataSource)

				/** increment the PC by 4 **/
				`PC_SOURCE_INCREMENT: begin
					programCounter <= programCounter + 4;
				end
				
				/** add the immediate value to the PC **/
				`PC_SOURCE_ADD_IMMEDIATE: begin
					programCounter <= programCounter + {extendedImmediateValue, 2'b00};
				end
				
				/** add the jump offset to the PC **/
				`PC_SOURCE_ADD_OFFSET: begin
					programCounter <= programCounter + {extendedJumpOffset, 2'b00};
				end
				
				/** set the PC to an explicitly provided value **/
				`PC_SOURCE_EXPLICIT: begin
					programCounter <= explicitValue;
				end

				/** set the PC to the exception handler entry point (0xe0000004 or 0xc0000004, depending on the vector bit) **/
				`PC_SOURCE_EXCEPTION: begin
					programCounter <= {2'b11, ~vectorBit, 29'h00000004};
				end

				/** set the PC to the user-space TLB miss handler entry point (0xe0000008 or 0xc0000008, depending on the vector bit) **/
				`PC_SOURCE_USER_TLB_MISS: begin
					programCounter <= {2'b11, ~vectorBit, 29'h00000008};
				end
				
				/** for any other value of writeDataSource, the new value is undefined **/
				default: begin
					programCounter <= 32'bxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx;
				end

			endcase
		end
	end

endmodule
