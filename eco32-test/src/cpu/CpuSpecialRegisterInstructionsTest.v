`default_nettype none
`timescale 1ns / 1ps

/**
 * Testbench for special register handling instructions.
 */
module CPUSpecialRegisterInstructionsTest ();

	/** includes **/
	`include "VUnit.inc.v"
	`include "Clock.inc.v"
	`include "../../eco32/src/cpu/CpuConstants.inc.v"
	`include "cpu/CpuTestHelper.inc.v"

	/** simulation **/
	initial begin
		$dumpfile("cpu/CpuSpecialRegisterInstructionsTest.vcd");
		$dumpvars;
		
		/** initialization **/
		busReadData <= 32'd0;
		busWait <= 1'b0;
		interrupts <= 16'b0000000000000000;
		
		/** reset **/
		#5 reset <= 1'b1;
		#20 reset <= 1'b0;
		#20 waitForBusAccess();

		/** assert initial state **/
		assert(cpu.processorStatusWord.processorStatusWord == 32'h00000000, "test initial PSW value");
		
		/*****************************************************************************************************/
		
		/** test MVTS(0) instruction **/
		assertionContext = "test MVTS(0): pattern";
		busReadData <= buildRRIInstruction(6'h39, 5'd0, 5'd7, 16'd0);
		#20 waitForBusAccess();
		assert(cpu.generalPurposeRegisters.valueArray[0][31:0] == 32'h00000000, "reg 0 not affected");
		assert(cpu.generalPurposeRegisters.valueArray[7][31:0] == 32'h2a70f632, "reg 7 not affected");
		assert(cpu.processorStatusWord.processorStatusWord == 32'h2a70f632, "new PSW value");

		/** test MVFS(0) instruction **/
		assertionContext = "test MVFS(0): pattern";
		busReadData <= buildRRIInstruction(6'h38, 5'd3, 5'd15, 16'd0);
		#20 waitForBusAccess();
		assert(cpu.generalPurposeRegisters.valueArray[3][31:0] == 32'h00000002, "reg 3 not affected");
		assert(cpu.generalPurposeRegisters.valueArray[15][31:0] == 32'h2a70f632, "reg 15 value");
		
		/** clear state **/
		cpu.processorStatusWord.processorStatusWord <= 32'h00000000;
		cpu.generalPurposeRegisters.valueArray[15][31:0] <= 32'h00000000;

		/** test MVTS(0) instruction **/
		assertionContext = "test MVTS(0): all-1";
		busReadData <= buildRRIInstruction(6'h39, 5'd0, 5'd2, 16'd0);
		#20 waitForBeginInstruction();
		assert(cpu.generalPurposeRegisters.valueArray[0][31:0] == 32'h00000000, "reg 0 not affected");
		assert(cpu.generalPurposeRegisters.valueArray[2][31:0] == 32'hffffffff, "reg 2 not affected");
		assert(cpu.processorStatusWord.processorStatusWord == 32'hffffffff, "new PSW value");

		/** test MVFS(0) instruction (note: the UM bit would block the use of the MVFS instruction) **/
		assertionContext = "test MVFS(0): all-1 except UM bit";
		cpu.processorStatusWord.processorStatusWord <= 32'hfbffffff;
		busReadData <= buildRRIInstruction(6'h38, 5'd3, 5'd15, 16'd0);
		#20 waitForBeginInstruction();
		assert(cpu.generalPurposeRegisters.valueArray[3][31:0] == 32'h00000002, "reg 3 not affected");
		assert(cpu.generalPurposeRegisters.valueArray[15][31:0] == 32'hfbffffff, "reg 15 value");
		
		/** clear state **/
		cpu.processorStatusWord.processorStatusWord <= 32'h00000000;
		cpu.generalPurposeRegisters.valueArray[15][31:0] <= 32'h00000000;
		waitForBusAccess();

		/*****************************************************************************************************/

		/** test MVTS(1) instruction **/
		assertionContext = "test MVTS(1): pattern, existing MSbit cleared";
		busReadData <= buildRRIInstruction(6'h39, 5'd0, 5'd7, 16'd1);
		#20 waitForBusAccess();
		assert(cpu.generalPurposeRegisters.valueArray[0][31:0] == 32'h00000000, "reg 0 not affected");
		assert(cpu.generalPurposeRegisters.valueArray[7][31:0] == 32'h2a70f632, "reg 7 not affected");
		assert(cpu.memoryManagementUnit.tlbIndexRegister == 5'h12, "new TLB index register value");
		assert(cpu.memoryManagementUnit.tlbInvalidIndexRegister == 1'b0, "new TLB index invalid register value");

		/** test MVFS(1) instruction **/
		assertionContext = "test MVFS(1): pattern, existing MSbit cleared";
		busReadData <= buildRRIInstruction(6'h38, 5'd3, 5'd15, 16'd1);
		#20 waitForBusAccess();
		assert(cpu.generalPurposeRegisters.valueArray[15][31:0] == 32'h00000012, "reg 15 value");
		
		/** clear state **/
		cpu.memoryManagementUnit.tlbIndexRegister <= 5'h00;
		cpu.generalPurposeRegisters.valueArray[15][31:0] <= 32'h00000000;

		/** test MVTS(1) instruction **/
		assertionContext = "test MVTS(1): all-1, existing MSbit cleared";
		busReadData <= buildRRIInstruction(6'h39, 5'd0, 5'd2, 16'd1);
		#20 waitForBusAccess();
		assert(cpu.generalPurposeRegisters.valueArray[0][31:0] == 32'h00000000, "reg 0 not affected");
		assert(cpu.generalPurposeRegisters.valueArray[1][31:0] == 32'h00000001, "reg 1 not affected");
		assert(cpu.generalPurposeRegisters.valueArray[2][31:0] == 32'hffffffff, "reg 2 not affected");
		assert(cpu.memoryManagementUnit.tlbIndexRegister == 5'h1f, "new TLB index register value");
		assert(cpu.memoryManagementUnit.tlbInvalidIndexRegister == 1'b0, "new TLB index invalid register value");

		/** test MVFS(1) instruction **/
		assertionContext = "test MVFS(1): all-1, existing MSbit cleared";
		busReadData <= buildRRIInstruction(6'h38, 5'd3, 5'd15, 16'd1);
		#20 waitForBusAccess();
		assert(cpu.generalPurposeRegisters.valueArray[15][31:0] == 32'h0000001f, "reg 15 value");
		
		/** clear state **/
		cpu.memoryManagementUnit.tlbIndexRegister <= 5'h00;
		cpu.generalPurposeRegisters.valueArray[15][31:0] <= 32'h00000000;

		/** test MVTS(1) instruction **/
		cpu.memoryManagementUnit.tlbInvalidIndexRegister <= 1'b1;
		assertionContext = "test MVTS(1): pattern, existing MSbit set";
		busReadData <= buildRRIInstruction(6'h39, 5'd0, 5'd7, 16'd1);
		#20 waitForBusAccess();
		assert(cpu.generalPurposeRegisters.valueArray[0][31:0] == 32'h00000000, "reg 0 not affected");
		assert(cpu.generalPurposeRegisters.valueArray[7][31:0] == 32'h2a70f632, "reg 7 not affected");
		assert(cpu.memoryManagementUnit.tlbIndexRegister == 5'h12, "new TLB index register value");
		assert(cpu.memoryManagementUnit.tlbInvalidIndexRegister == 1'b0, "new TLB index invalid register value");

		/** test MVFS(1) instruction **/
		assertionContext = "test MVFS(1): pattern, existing MSbit set";
		busReadData <= buildRRIInstruction(6'h38, 5'd3, 5'd15, 16'd1);
		#20 waitForBusAccess();
		assert(cpu.generalPurposeRegisters.valueArray[15][31:0] == 32'h00000012, "reg 15 value");
		
		/** clear state **/
		cpu.memoryManagementUnit.tlbIndexRegister <= 5'h00;
		cpu.generalPurposeRegisters.valueArray[15][31:0] <= 32'h00000000;

		/** test MVTS(1) instruction **/
		cpu.memoryManagementUnit.tlbInvalidIndexRegister <= 1'b1;
		assertionContext = "test MVTS(1): all-1, existing MSbit set";
		busReadData <= buildRRIInstruction(6'h39, 5'd0, 5'd2, 16'd1);
		#20 waitForBusAccess();
		assert(cpu.generalPurposeRegisters.valueArray[0][31:0] == 32'h00000000, "reg 0 not affected");
		assert(cpu.generalPurposeRegisters.valueArray[1][31:0] == 32'h00000001, "reg 1 not affected");
		assert(cpu.generalPurposeRegisters.valueArray[2][31:0] == 32'hffffffff, "reg 2 not affected");
		assert(cpu.memoryManagementUnit.tlbIndexRegister == 5'h1f, "new TLB index register value");
		assert(cpu.memoryManagementUnit.tlbInvalidIndexRegister == 1'b0, "new TLB index invalid register value");

		/** test MVFS(1) instruction **/
		assertionContext = "test MVFS(1): all-1, existing MSbit set";
		busReadData <= buildRRIInstruction(6'h38, 5'd3, 5'd15, 16'd1);
		#20 waitForBusAccess();
		assert(cpu.generalPurposeRegisters.valueArray[15][31:0] == 32'h0000001f, "reg 15 value");
		
		/** configure state with MSbit set **/
		cpu.memoryManagementUnit.tlbIndexRegister <= 5'h1a;
		cpu.memoryManagementUnit.tlbInvalidIndexRegister <= 1'b1;
		cpu.generalPurposeRegisters.valueArray[15][31:0] <= 32'h00000000;

		/** test MVFS(1) instruction **/
		assertionContext = "test MVFS(1): MSbit artificially set";
		busReadData <= buildRRIInstruction(6'h38, 5'd3, 5'd15, 16'd1);
		#20 waitForBusAccess();
		assert(cpu.generalPurposeRegisters.valueArray[15][31:0] == 32'h8000001a, "reg 15 value");

		/** clear state **/
		cpu.memoryManagementUnit.tlbIndexRegister <= 5'h00;
		cpu.memoryManagementUnit.tlbInvalidIndexRegister <= 1'b0;
		cpu.generalPurposeRegisters.valueArray[15][31:0] <= 32'h00000000;

		/*****************************************************************************************************/
		
		#1000 waitForBusAccess();
		
		/** test MVTS(2) instruction **/
		assertionContext = "test MVTS(2): pattern";
		busReadData <= buildRRIInstruction(6'h39, 5'd0, 5'd7, 16'd2);
		#20 waitForBusAccess();
		assert(cpu.generalPurposeRegisters.valueArray[0][31:0] == 32'h00000000, "reg 0 not affected");
		assert(cpu.generalPurposeRegisters.valueArray[7][31:0] == 32'h2a70f632, "reg 7 not affected");
		assert(cpu.memoryManagementUnit.tlbEntryHighRegister == 20'h2a70f, "new PSW value");

		/** test MVFS(2) instruction **/
		assertionContext = "test MVFS(2): pattern";
		busReadData <= buildRRIInstruction(6'h38, 5'd3, 5'd15, 16'd2);
		#20 waitForBusAccess();
		assert(cpu.generalPurposeRegisters.valueArray[3][31:0] == 32'h00000002, "reg 3 not affected");
		assert(cpu.generalPurposeRegisters.valueArray[15][31:0] == 32'h2a70f000, "reg 15 value");
		
		/** clear state **/
		cpu.memoryManagementUnit.tlbEntryHighRegister <= 20'h0;
		cpu.generalPurposeRegisters.valueArray[15][31:0] <= 32'h00000000;

		/** test MVTS(2) instruction **/
		assertionContext = "test MVTS(2): all-1";
		busReadData <= buildRRIInstruction(6'h39, 5'd0, 5'd2, 16'd2);
		#20 waitForBusAccess();
		assert(cpu.generalPurposeRegisters.valueArray[0][31:0] == 32'h00000000, "reg 0 not affected");
		assert(cpu.generalPurposeRegisters.valueArray[2][31:0] == 32'hffffffff, "reg 2 not affected");
		assert(cpu.memoryManagementUnit.tlbEntryHighRegister == 20'hfffff, "new PSW value");

		/** test MVFS(2) instruction **/
		assertionContext = "test MVFS(2): all-1";
		busReadData <= buildRRIInstruction(6'h38, 5'd3, 5'd15, 16'd2);
		#20 waitForBusAccess();
		assert(cpu.generalPurposeRegisters.valueArray[3][31:0] == 32'h00000002, "reg 3 not affected");
		assert(cpu.generalPurposeRegisters.valueArray[15][31:0] == 32'hfffff000, "reg 15 value");
		
		/** clear state **/
		cpu.memoryManagementUnit.tlbEntryHighRegister <= 20'h0;
		cpu.generalPurposeRegisters.valueArray[15][31:0] <= 32'h00000000;

		/*****************************************************************************************************/
		
		/** test MVTS(3) instruction **/
		assertionContext = "test MVTS(3): pattern";
		busReadData <= buildRRIInstruction(6'h39, 5'd0, 5'd7, 16'd3);
		#20 waitForBusAccess();
		assert(cpu.generalPurposeRegisters.valueArray[0][31:0] == 32'h00000000, "reg 0 not affected");
		assert(cpu.generalPurposeRegisters.valueArray[7][31:0] == 32'h2a70f632, "reg 7 not affected");
		assert(cpu.memoryManagementUnit.tlbEntryLowRegister == {20'h2a70f, 2'b10}, "new PSW value");

		/** test MVFS(3) instruction **/
		assertionContext = "test MVFS(3): pattern";
		busReadData <= buildRRIInstruction(6'h38, 5'd3, 5'd15, 16'd3);
		#20 waitForBusAccess();
		assert(cpu.generalPurposeRegisters.valueArray[3][31:0] == 32'h00000002, "reg 3 not affected");
		assert(cpu.generalPurposeRegisters.valueArray[15][31:0] == 32'h2a70f002, "reg 15 value");
		
		/** clear state **/
		cpu.memoryManagementUnit.tlbEntryLowRegister <= 22'h0;
		cpu.generalPurposeRegisters.valueArray[15][31:0] <= 32'h00000000;

		/** test MVTS(3) instruction **/
		assertionContext = "test MVTS(3): all-1";
		busReadData <= buildRRIInstruction(6'h39, 5'd0, 5'd2, 16'd3);
		#20 waitForBusAccess();
		assert(cpu.generalPurposeRegisters.valueArray[0][31:0] == 32'h00000000, "reg 0 not affected");
		assert(cpu.generalPurposeRegisters.valueArray[2][31:0] == 32'hffffffff, "reg 2 not affected");
		assert(cpu.memoryManagementUnit.tlbEntryLowRegister == {20'hfffff, 2'b11}, "new PSW value");

		/** test MVFS(3) instruction **/
		assertionContext = "test MVFS(3): all-1";
		busReadData <= buildRRIInstruction(6'h38, 5'd3, 5'd15, 16'd3);
		#20 waitForBusAccess();
		assert(cpu.generalPurposeRegisters.valueArray[3][31:0] == 32'h00000002, "reg 3 not affected");
		assert(cpu.generalPurposeRegisters.valueArray[15][31:0] == 32'hfffff003, "reg 15 value");
		
		/** clear state **/
		cpu.memoryManagementUnit.tlbEntryLowRegister <= 22'h0;
		cpu.generalPurposeRegisters.valueArray[15][31:0] <= 32'h00000000;

		/*****************************************************************************************************/
		
		/** test MVTS(4) instruction **/
		assertionContext = "test MVTS(4): pattern";
		busReadData <= buildRRIInstruction(6'h39, 5'd0, 5'd7, 16'd4);
		#20 waitForBusAccess();
		assert(cpu.generalPurposeRegisters.valueArray[0][31:0] == 32'h00000000, "reg 0 not affected");
		assert(cpu.generalPurposeRegisters.valueArray[7][31:0] == 32'h2a70f632, "reg 7 not affected");
		assert(cpu.memoryManagementUnit.tlbBadAddressRegister == 32'h2a70f632, "new PSW value");

		/** test MVFS(4) instruction **/
		assertionContext = "test MVFS(4): pattern";
		busReadData <= buildRRIInstruction(6'h38, 5'd3, 5'd15, 16'd4);
		#20 waitForBusAccess();
		assert(cpu.generalPurposeRegisters.valueArray[3][31:0] == 32'h00000002, "reg 3 not affected");
		assert(cpu.generalPurposeRegisters.valueArray[15][31:0] == 32'h2a70f632, "reg 15 value");
		
		/** clear state **/
		cpu.memoryManagementUnit.tlbBadAddressRegister <= 32'h00000000;
		cpu.generalPurposeRegisters.valueArray[15][31:0] <= 32'h00000000;

		/** test MVTS(4) instruction **/
		assertionContext = "test MVTS(4): all-1";
		busReadData <= buildRRIInstruction(6'h39, 5'd0, 5'd2, 16'd4);
		#20 waitForBusAccess();
		assert(cpu.generalPurposeRegisters.valueArray[0][31:0] == 32'h00000000, "reg 0 not affected");
		assert(cpu.generalPurposeRegisters.valueArray[2][31:0] == 32'hffffffff, "reg 2 not affected");
		assert(cpu.memoryManagementUnit.tlbBadAddressRegister == 32'hffffffff, "new PSW value");

		/** test MVFS(4) instruction **/
		assertionContext = "test MVFS(4): all-1";
		busReadData <= buildRRIInstruction(6'h38, 5'd3, 5'd15, 16'd4);
		#20 waitForBusAccess();
		assert(cpu.generalPurposeRegisters.valueArray[3][31:0] == 32'h00000002, "reg 3 not affected");
		assert(cpu.generalPurposeRegisters.valueArray[15][31:0] == 32'hffffffff, "reg 15 value");
		
		/** clear state **/
		cpu.memoryManagementUnit.tlbBadAddressRegister <= 32'h00000000;
		cpu.generalPurposeRegisters.valueArray[15][31:0] <= 32'h00000000;

		/** done **/
		#100 $finish;
	end

endmodule
