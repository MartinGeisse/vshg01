`default_nettype none
`timescale 1ns / 1ps

/**
 * The PSW register. This module basically acts as a register with some special
 * support for computing its next value.
 */
module ProcessorStatusWord (
	
		/** the clock **/
		input clock,
		
		/** the reset signal **/
		input reset,
		
		/** the current value, obtained asynchronously **/
		output[31:0] readValue,
		
		/** the write-enable for this register **/
		input writeEnable,
		
		/** the data MUX for the value to write **/
		input[`PSW_WRITE_DATA_SOURCE_WIDTH-1:0] writeDataSource,
		
		/** the explicit value to load if writeDataSource is PSW_WRITE_DATA_SOURCE_EXPLICIT **/
		input[31:0] explicitWriteValue,
		
		/** the priority value to use for the transformed PSW value if writeDataSource is PSW_WRITE_DATA_SOURCE_HANDLER_ENTRY **/
		input[4:0] priorityWriteValue

	);

	/** the actual register **/
	reg[31:0] processorStatusWord;
	assign readValue = processorStatusWord;
	
	/**
	 * Decode the current PSW value to simplify the computation of the
	 * transformed value for handler entry and exit.
	 */
	wire pswVectorBit = processorStatusWord[27];
	wire pswUserModeBit = processorStatusWord[26];
	wire pswPreviousUserModeBit = processorStatusWord[25];
	wire pswOldUserModeBit = processorStatusWord[24];
	wire pswInterruptEnableBit = processorStatusWord[23];
	wire pswPreviousInterruptEnableBit = processorStatusWord[22];
	wire pswOldInterruptEnableBit = processorStatusWord[21];
	wire[4:0] pswPriority = processorStatusWord[20:16];
	wire[15:0] pswInterruptMask = processorStatusWord[15:0];

	/** synchronous loading logic **/
	always @(posedge clock) begin
		if (reset) begin
			processorStatusWord <= 32'h00000000;
		end else if (writeEnable) begin
			case (writeDataSource)
		
				/** load an explicitly provided value **/
				`PSW_WRITE_DATA_SOURCE_EXPLICIT: begin
					processorStatusWord <= explicitWriteValue;
				end
				
				/** transform the PSW for exception handler entry **/
				`PSW_WRITE_DATA_SOURCE_HANDLER_ENTRY: begin
					processorStatusWord <= {
						4'b0000,
						pswVectorBit,
						1'b0, pswUserModeBit, pswPreviousUserModeBit,
						1'b0, pswInterruptEnableBit, pswPreviousInterruptEnableBit,
						priorityWriteValue,
						pswInterruptMask
					};
				end
				
				/** transform the PSW for exception handler exit **/
				`PSW_WRITE_DATA_SOURCE_HANDLER_EXIT: begin
					processorStatusWord <= {
						4'b0000,
						pswVectorBit,
						pswPreviousUserModeBit, pswOldUserModeBit, pswOldUserModeBit,
						pswPreviousInterruptEnableBit, pswOldInterruptEnableBit, pswOldInterruptEnableBit,
						pswPriority,
						pswInterruptMask
					};
				end
				
				/** for any other value of writeDataSource, the new value is undefined **/
				default: begin
					processorStatusWord <= 32'bxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx;
				end
	
			endcase
		end
	end

endmodule
