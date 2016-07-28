`default_nettype none
`timescale 1ns / 1ps

/**
 * Testbench for interrupt handling.
 */
module CPUInterruptTest ();

	/** includes **/
	`include "VUnit.inc.v"
	`include "Clock.inc.v"
	`include "../../eco32/src/cpu/CpuConstants.inc.v"
	`include "cpu/CpuTestHelper.inc.v"

	/** helper tasks **/
	task testInterrupt(input[31:0] pc, input[15:0] interruptValues, input[31:0] pswValueBeforeEntry, input[31:0] expectedEntryPoint, input[3:0] expectedInterruptIndex);
		begin
			cpu.programCounterRegister.programCounter <= pc;
			cpu.processorStatusWord.processorStatusWord <= pswValueBeforeEntry;
			busReadData <= 32'h0;
			interrupts <= interruptValues;
			
			/** wait until just before the interrupt is detected **/
			#20 waitForBeginInstruction();
			assert(cpu.programCounterRegister.programCounter == pc, "PC before interrupt detected");
			
			/** wait until just after interrupt entry **/
			#20 waitForBeginInstruction();
			assert(cpu.programCounterRegister.programCounter == expectedEntryPoint, "PC");
			assert(cpu.processorStatusWord.processorStatusWord == {
				4'b0000,					// upper bits
				pswValueBeforeEntry[27],	// vector
				1'b0,						// IEN
				pswValueBeforeEntry[26],	// PIEN
				pswValueBeforeEntry[25],	// OIEN
				1'b0,						// UM
				pswValueBeforeEntry[23],	// PUM
				pswValueBeforeEntry[22],	// OUM
				1'b0,						// highest priority bit (0 = interrupt)
				expectedInterruptIndex,		// remaining priority bits
				pswValueBeforeEntry[15:0]	// interrupt mask
			}, "PSW");
			assert(cpu.generalPurposeRegisters.valueArray[30] == pc, "R30");
			
			/** check instruction fetch bus address for the first instruction of the handler **/
			#20 waitForBusAccess();
			assert(busAddress == {2'b00, expectedEntryPoint[29:0]}, "bus address");
			
		end
	endtask
	
	/** simulation **/
	initial begin
		$dumpfile("cpu/CpuInterruptTest.vcd");
		$dumpvars;
		
		/** initialization **/
		busReadData <= 32'd0;
		busWait <= 1'b0;
		interrupts <= 16'b0000000000000000;
		
		/** reset **/
		#5 reset <= 1'b1;
		#20 reset <= 1'b0;
		#20 waitForBusAccess();

		/** test (one masked interrupt skipped) **/
		assertionContext <= "test 1 masked skipped";
		testInterrupt(32'hc9283744, 16'b0000_0111_0000_0000, 32'b0000_0_011_101_10110_0001_0110_0000_0101, 32'he0000004, 4'd9);

		/** test with vector bit set **/
		assertionContext <= "test vector bit";
		testInterrupt(32'hc9283744, 16'b0000_0111_0000_0000, 32'b0000_1_011_101_10110_0001_0110_0000_0101, 32'hc0000004, 4'd9);

		/** test with interrupt enable bit cleared **/
		assertionContext <= "test IEN bit";
		cpu.programCounterRegister.programCounter <= 32'hc9283744;
		cpu.processorStatusWord.processorStatusWord <= 32'b0000_0_011_001_10110_0001_0110_0000_0101;
		busReadData <= 32'h0;
		interrupts <= 16'b0000_0111_0000_0000;
		#20 waitForBeginInstruction();
		assert(cpu.programCounterRegister.programCounter == 32'hc9283744, "PC before interrupt detected");
		#20 waitForBeginInstruction();
		assert(cpu.programCounterRegister.programCounter == 32'hc9283748, "PC after interrupt could have been detected");
		
		/** test with user mode bit set **/
		assertionContext <= "test user mode";
		waitForBusAccess();
		testInterrupt(32'h12345000, 16'b0000_0111_0000_0000, 32'b0000_1_111_101_10110_0001_0110_0000_0101, 32'hc0000004, 4'd9);

		/** done **/
		#100 $finish;
	end

endmodule
