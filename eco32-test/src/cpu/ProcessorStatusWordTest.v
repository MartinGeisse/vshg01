`default_nettype none
`timescale 1ns / 1ps

/**
 * Testbench for the PSW.
 */
module ProcessorStatusWordTest ();

	/** includes **/
	`include "VUnit.inc.v"
	`include "Clock.inc.v"
	`include "../../eco32/src/cpu/ProcessorStatusWordConstants.inc.v"
	
	/** the module under test **/
	reg reset;
	wire[31:0] readValue;
	reg writeEnable;
	reg[`PSW_WRITE_DATA_SOURCE_WIDTH-1:0] writeDataSource;
	reg[31:0] explicitWriteValue;
	reg[4:0] priorityWriteValue;
	ProcessorStatusWord processorStatusWord (
		.clock(clock),
		.reset(reset),
		.readValue(readValue),
		.writeEnable(writeEnable),
		.writeDataSource(writeDataSource),
		.explicitWriteValue(explicitWriteValue),
		.priorityWriteValue(priorityWriteValue)
	);

	/** meaning of the bits **/
	wire pswVectorBit = readValue[27];
	wire pswUserModeBit = readValue[26];
	wire pswPreviousUserModeBit = readValue[25];
	wire pswOldUserModeBit = readValue[24];
	wire pswInterruptEnableBit = readValue[23];
	wire pswPreviousInterruptEnableBit = readValue[22];
	wire pswOldInterruptEnableBit = readValue[21];
	wire[4:0] pswPriority = readValue[20:16];
	wire[15:0] pswInterruptMask = readValue[15:0];

	/** simulation **/
	initial begin
		$dumpfile("cpu/ProcessorStatusWordTest.vcd");
		$dumpvars;

		/** initialize **/
		writeEnable <= 1'b0;
		
		/** reset **/
		#5 reset <= 1'b1;
		#20 reset <= 1'b0;
		
		/** write an explicit value **/
		assertionContext <= "write explicit value";
		writeEnable <= 1'b1;
		writeDataSource <= `PSW_WRITE_DATA_SOURCE_EXPLICIT;
		explicitWriteValue <= 32'habcd1234;
		#20;
		assert(readValue == 32'habcd1234, "value");
		
		/** retain the current value **/
		assertionContext <= "retain value";
		writeEnable <= 1'b0;
		#20;
		assert(readValue == 32'habcd1234, "value");

		/** some simple tests **/
		
		assertionContext <= "test1";
		processorStatusWord.processorStatusWord <= 32'b0000_0_000_000_00000_0000000000000000;
		priorityWriteValue <= 5'b00000;
		#1;
		assert(pswVectorBit == 1'b0, "vector");
		assert(pswUserModeBit == 1'b0, "user mode");
		assert(pswPreviousUserModeBit == 1'b0, "previous user mode");
		assert(pswOldUserModeBit == 1'b0, "old user mode");
		assert(pswInterruptEnableBit == 1'b0, "interrupt enable");
		assert(pswPreviousInterruptEnableBit == 1'b0, "previous interrupt enable");
		assert(pswOldInterruptEnableBit == 1'b0, "old interrupt enable");
		assert(pswPriority == 5'b00000, "priority");
		assert(pswInterruptMask == 16'b0000000000000000, "interrupt mask");
		writeEnable <= 1'b1;
		writeDataSource <= `PSW_WRITE_DATA_SOURCE_HANDLER_ENTRY;
		#19;
		assert(readValue == 32'b0000_0_000_000_00000_0000000000000000, "transformed on entry");
		writeEnable <= 1'b1;
		writeDataSource <= `PSW_WRITE_DATA_SOURCE_HANDLER_EXIT;
		#20;
		assert(readValue == 32'b0000_0_000_000_00000_0000000000000000, "transformed on exit");

		assertionContext <= "test2";
		processorStatusWord.processorStatusWord <= 32'b0000_0_000_000_00000_0000000000000000;
		priorityWriteValue <= 5'b10101;
		#1;
		assert(pswVectorBit == 1'b0, "vector");
		assert(pswUserModeBit == 1'b0, "user mode");
		assert(pswPreviousUserModeBit == 1'b0, "previous user mode");
		assert(pswOldUserModeBit == 1'b0, "old user mode");
		assert(pswInterruptEnableBit == 1'b0, "interrupt enable");
		assert(pswPreviousInterruptEnableBit == 1'b0, "previous interrupt enable");
		assert(pswOldInterruptEnableBit == 1'b0, "old interrupt enable");
		assert(pswPriority == 5'b00000, "priority");
		assert(pswInterruptMask == 16'b0000000000000000, "interrupt mask");
		writeEnable <= 1'b1;
		writeDataSource <= `PSW_WRITE_DATA_SOURCE_HANDLER_ENTRY;
		#19;
		assert(readValue == 32'b0000_0_000_000_10101_0000000000000000, "transformed on entry");
		processorStatusWord.processorStatusWord <= 32'b0000_0_000_000_00000_0000000000000000;
		writeEnable <= 1'b1;
		writeDataSource <= `PSW_WRITE_DATA_SOURCE_HANDLER_EXIT;
		#20;
		assert(readValue == 32'b0000_0_000_000_00000_0000000000000000, "transformed on exit");

		assertionContext <= "test3";
		processorStatusWord.processorStatusWord <= 32'b0000_1_000_000_00000_0000000000000000;
		priorityWriteValue <= 5'b10101;
		#1;
		assert(pswVectorBit == 1'b1, "vector");
		assert(pswUserModeBit == 1'b0, "user mode");
		assert(pswPreviousUserModeBit == 1'b0, "previous user mode");
		assert(pswOldUserModeBit == 1'b0, "old user mode");
		assert(pswInterruptEnableBit == 1'b0, "interrupt enable");
		assert(pswPreviousInterruptEnableBit == 1'b0, "previous interrupt enable");
		assert(pswOldInterruptEnableBit == 1'b0, "old interrupt enable");
		assert(pswPriority == 5'b00000, "priority");
		assert(pswInterruptMask == 16'b0000000000000000, "interrupt mask");
		writeEnable <= 1'b1;
		writeDataSource <= `PSW_WRITE_DATA_SOURCE_HANDLER_ENTRY;
		#19;
		assert(readValue == 32'b0000_1_000_000_10101_0000000000000000, "transformed on entry");
		processorStatusWord.processorStatusWord <= 32'b0000_1_000_000_00000_0000000000000000;
		writeEnable <= 1'b1;
		writeDataSource <= `PSW_WRITE_DATA_SOURCE_HANDLER_EXIT;
		#20;
		assert(readValue == 32'b0000_1_000_000_00000_0000000000000000, "transformed on exit");

		assertionContext <= "test4";
		processorStatusWord.processorStatusWord <= 32'b0000_0_100_000_00000_0000000000000000;
		priorityWriteValue <= 5'b10101;
		#1;
		assert(pswVectorBit == 1'b0, "vector");
		assert(pswUserModeBit == 1'b1, "user mode");
		assert(pswPreviousUserModeBit == 1'b0, "previous user mode");
		assert(pswOldUserModeBit == 1'b0, "old user mode");
		assert(pswInterruptEnableBit == 1'b0, "interrupt enable");
		assert(pswPreviousInterruptEnableBit == 1'b0, "previous interrupt enable");
		assert(pswOldInterruptEnableBit == 1'b0, "old interrupt enable");
		assert(pswPriority == 5'b00000, "priority");
		assert(pswInterruptMask == 16'b0000000000000000, "interrupt mask");
		writeEnable <= 1'b1;
		writeDataSource <= `PSW_WRITE_DATA_SOURCE_HANDLER_ENTRY;
		#19;
		assert(readValue == 32'b0000_0_010_000_10101_0000000000000000, "transformed on entry");
		processorStatusWord.processorStatusWord <= 32'b0000_0_100_000_00000_0000000000000000;
		writeEnable <= 1'b1;
		writeDataSource <= `PSW_WRITE_DATA_SOURCE_HANDLER_EXIT;
		#20;
		assert(readValue == 32'b0000_0_000_000_00000_0000000000000000, "transformed on exit");

		assertionContext <= "test5";
		processorStatusWord.processorStatusWord <= 32'b0000_0_010_000_00000_0000000000000000;
		priorityWriteValue <= 5'b10101;
		#1;
		assert(pswVectorBit == 1'b0, "vector");
		assert(pswUserModeBit == 1'b0, "user mode");
		assert(pswPreviousUserModeBit == 1'b1, "previous user mode");
		assert(pswOldUserModeBit == 1'b0, "old user mode");
		assert(pswInterruptEnableBit == 1'b0, "interrupt enable");
		assert(pswPreviousInterruptEnableBit == 1'b0, "previous interrupt enable");
		assert(pswOldInterruptEnableBit == 1'b0, "old interrupt enable");
		assert(pswPriority == 5'b00000, "priority");
		assert(pswInterruptMask == 16'b0000000000000000, "interrupt mask");
		writeEnable <= 1'b1;
		writeDataSource <= `PSW_WRITE_DATA_SOURCE_HANDLER_ENTRY;
		#19;
		assert(readValue == 32'b0000_0_001_000_10101_0000000000000000, "transformed on entry");
		processorStatusWord.processorStatusWord <= 32'b0000_0_010_000_00000_0000000000000000;
		writeEnable <= 1'b1;
		writeDataSource <= `PSW_WRITE_DATA_SOURCE_HANDLER_EXIT;
		#20;
		assert(readValue == 32'b0000_0_100_000_00000_0000000000000000, "transformed on exit");

		assertionContext <= "test6";
		processorStatusWord.processorStatusWord <= 32'b0000_0_001_000_00000_0000000000000000;
		priorityWriteValue <= 5'b10101;
		#1;
		assert(pswVectorBit == 1'b0, "vector");
		assert(pswUserModeBit == 1'b0, "user mode");
		assert(pswPreviousUserModeBit == 1'b0, "previous user mode");
		assert(pswOldUserModeBit == 1'b1, "old user mode");
		assert(pswInterruptEnableBit == 1'b0, "interrupt enable");
		assert(pswPreviousInterruptEnableBit == 1'b0, "previous interrupt enable");
		assert(pswOldInterruptEnableBit == 1'b0, "old interrupt enable");
		assert(pswPriority == 5'b00000, "priority");
		assert(pswInterruptMask == 16'b0000000000000000, "interrupt mask");
		writeEnable <= 1'b1;
		writeDataSource <= `PSW_WRITE_DATA_SOURCE_HANDLER_ENTRY;
		#19;
		assert(readValue == 32'b0000_0_000_000_10101_0000000000000000, "transformed on entry");
		processorStatusWord.processorStatusWord <= 32'b0000_0_001_000_00000_0000000000000000;
		writeEnable <= 1'b1;
		writeDataSource <= `PSW_WRITE_DATA_SOURCE_HANDLER_EXIT;
		#20;
		assert(readValue == 32'b0000_0_011_000_00000_0000000000000000, "transformed on exit");

		assertionContext <= "test7";
		processorStatusWord.processorStatusWord <= 32'b0000_0_000_100_00000_0000000000000000;
		priorityWriteValue <= 5'b10101;
		#1;
		assert(pswVectorBit == 1'b0, "vector");
		assert(pswUserModeBit == 1'b0, "user mode");
		assert(pswPreviousUserModeBit == 1'b0, "previous user mode");
		assert(pswOldUserModeBit == 1'b0, "old user mode");
		assert(pswInterruptEnableBit == 1'b1, "interrupt enable");
		assert(pswPreviousInterruptEnableBit == 1'b0, "previous interrupt enable");
		assert(pswOldInterruptEnableBit == 1'b0, "old interrupt enable");
		assert(pswPriority == 5'b00000, "priority");
		assert(pswInterruptMask == 16'b0000000000000000, "interrupt mask");
		writeEnable <= 1'b1;
		writeDataSource <= `PSW_WRITE_DATA_SOURCE_HANDLER_ENTRY;
		#19;
		assert(readValue == 32'b0000_0_000_010_10101_0000000000000000, "transformed on entry");
		processorStatusWord.processorStatusWord <= 32'b0000_0_000_100_00000_0000000000000000;
		writeEnable <= 1'b1;
		writeDataSource <= `PSW_WRITE_DATA_SOURCE_HANDLER_EXIT;
		#20;
		assert(readValue == 32'b0000_0_000_000_00000_0000000000000000, "transformed on exit");

		assertionContext <= "test8";
		processorStatusWord.processorStatusWord <= 32'b0000_0_000_010_00000_0000000000000000;
		priorityWriteValue <= 5'b10101;
		#1;
		assert(pswVectorBit == 1'b0, "vector");
		assert(pswUserModeBit == 1'b0, "user mode");
		assert(pswPreviousUserModeBit == 1'b0, "previous user mode");
		assert(pswOldUserModeBit == 1'b0, "old user mode");
		assert(pswInterruptEnableBit == 1'b0, "interrupt enable");
		assert(pswPreviousInterruptEnableBit == 1'b1, "previous interrupt enable");
		assert(pswOldInterruptEnableBit == 1'b0, "old interrupt enable");
		assert(pswPriority == 5'b00000, "priority");
		assert(pswInterruptMask == 16'b0000000000000000, "interrupt mask");
		writeEnable <= 1'b1;
		writeDataSource <= `PSW_WRITE_DATA_SOURCE_HANDLER_ENTRY;
		#19;
		assert(readValue == 32'b0000_0_000_001_10101_0000000000000000, "transformed on entry");
		processorStatusWord.processorStatusWord <= 32'b0000_0_000_010_00000_0000000000000000;
		writeEnable <= 1'b1;
		writeDataSource <= `PSW_WRITE_DATA_SOURCE_HANDLER_EXIT;
		#20;
		assert(readValue == 32'b0000_0_000_100_00000_0000000000000000, "transformed on exit");

		assertionContext <= "test9";
		processorStatusWord.processorStatusWord <= 32'b0000_0_000_001_00000_0000000000000000;
		priorityWriteValue <= 5'b10101;
		#1;
		assert(pswVectorBit == 1'b0, "vector");
		assert(pswUserModeBit == 1'b0, "user mode");
		assert(pswPreviousUserModeBit == 1'b0, "previous user mode");
		assert(pswOldUserModeBit == 1'b0, "old user mode");
		assert(pswInterruptEnableBit == 1'b0, "interrupt enable");
		assert(pswPreviousInterruptEnableBit == 1'b0, "previous interrupt enable");
		assert(pswOldInterruptEnableBit == 1'b1, "old interrupt enable");
		assert(pswPriority == 5'b00000, "priority");
		assert(pswInterruptMask == 16'b0000000000000000, "interrupt mask");
		writeEnable <= 1'b1;
		writeDataSource <= `PSW_WRITE_DATA_SOURCE_HANDLER_ENTRY;
		#19;
		assert(readValue == 32'b0000_0_000_000_10101_0000000000000000, "transformed on entry");
		processorStatusWord.processorStatusWord <= 32'b0000_0_000_001_00000_0000000000000000;
		writeEnable <= 1'b1;
		writeDataSource <= `PSW_WRITE_DATA_SOURCE_HANDLER_EXIT;
		#20;
		assert(readValue == 32'b0000_0_000_011_00000_0000000000000000, "transformed on exit");

		assertionContext <= "test10";
		processorStatusWord.processorStatusWord <= 32'b0000_0_000_000_11100_0000000000000000;
		priorityWriteValue <= 5'b10101;
		#1;
		assert(pswVectorBit == 1'b0, "vector");
		assert(pswUserModeBit == 1'b0, "user mode");
		assert(pswPreviousUserModeBit == 1'b0, "previous user mode");
		assert(pswOldUserModeBit == 1'b0, "old user mode");
		assert(pswInterruptEnableBit == 1'b0, "interrupt enable");
		assert(pswPreviousInterruptEnableBit == 1'b0, "previous interrupt enable");
		assert(pswOldInterruptEnableBit == 1'b0, "old interrupt enable");
		assert(pswPriority == 5'b11100, "priority");
		assert(pswInterruptMask == 16'b0000000000000000, "interrupt mask");
		writeEnable <= 1'b1;
		writeDataSource <= `PSW_WRITE_DATA_SOURCE_HANDLER_ENTRY;
		#19;
		assert(readValue == 32'b0000_0_000_000_10101_0000000000000000, "transformed on entry");
		processorStatusWord.processorStatusWord <= 32'b0000_0_000_000_11100_0000000000000000;
		writeEnable <= 1'b1;
		writeDataSource <= `PSW_WRITE_DATA_SOURCE_HANDLER_EXIT;
		#20;
		assert(readValue == 32'b0000_0_000_000_11100_0000000000000000, "transformed on exit");

		assertionContext <= "test11";
		processorStatusWord.processorStatusWord <= 32'b0000_0_000_000_00000_1100010011101010;
		priorityWriteValue <= 5'b10101;
		#1;
		assert(pswVectorBit == 1'b0, "vector");
		assert(pswUserModeBit == 1'b0, "user mode");
		assert(pswPreviousUserModeBit == 1'b0, "previous user mode");
		assert(pswOldUserModeBit == 1'b0, "old user mode");
		assert(pswInterruptEnableBit == 1'b0, "interrupt enable");
		assert(pswPreviousInterruptEnableBit == 1'b0, "previous interrupt enable");
		assert(pswOldInterruptEnableBit == 1'b0, "old interrupt enable");
		assert(pswPriority == 5'b00000, "priority");
		assert(pswInterruptMask == 16'b1100010011101010, "interrupt mask");
		writeEnable <= 1'b1;
		writeDataSource <= `PSW_WRITE_DATA_SOURCE_HANDLER_ENTRY;
		#19;
		assert(readValue == 32'b0000_0_000_000_10101_1100010011101010, "transformed on entry");
		processorStatusWord.processorStatusWord <= 32'b0000_0_000_000_00000_1100010011101010;
		writeEnable <= 1'b1;
		writeDataSource <= `PSW_WRITE_DATA_SOURCE_HANDLER_EXIT;
		#20;
		assert(readValue == 32'b0000_0_000_000_00000_1100010011101010, "transformed on exit");

		/** a real-word test **/
		
		assertionContext <= "test X";
		processorStatusWord.processorStatusWord <= 32'b0000_1_101_110_01100_1100010011101010;
		priorityWriteValue <= 5'b10101;
		#1;
		assert(pswVectorBit == 1'b1, "vector");
		assert(pswUserModeBit == 1'b1, "user mode");
		assert(pswPreviousUserModeBit == 1'b0, "previous user mode");
		assert(pswOldUserModeBit == 1'b1, "old user mode");
		assert(pswInterruptEnableBit == 1'b1, "interrupt enable");
		assert(pswPreviousInterruptEnableBit == 1'b1, "previous interrupt enable");
		assert(pswOldInterruptEnableBit == 1'b0, "old interrupt enable");
		assert(pswPriority == 5'b01100, "priority");
		assert(pswInterruptMask == 16'b1100010011101010, "interrupt mask");
		writeEnable <= 1'b1;
		writeDataSource <= `PSW_WRITE_DATA_SOURCE_HANDLER_ENTRY;
		#19;
		assert(readValue == 32'b0000_1_010_011_10101_1100010011101010, "transformed on entry");
		processorStatusWord.processorStatusWord <= 32'b0000_1_101_110_01100_1100010011101010;
		writeEnable <= 1'b1;
		writeDataSource <= `PSW_WRITE_DATA_SOURCE_HANDLER_EXIT;
		#20;
		assert(readValue == 32'b0000_1_011_100_01100_1100010011101010, "transformed on exit");

		/** done **/
		#100 $finish;
	end
		
endmodule
