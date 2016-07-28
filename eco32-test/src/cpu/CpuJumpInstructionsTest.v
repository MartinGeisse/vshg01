`default_nettype none
`timescale 1ns / 1ps

/**
 * Testbench for jump instructions.
 */
module CPUJumpInstructionsTest ();

	/** includes **/
	`include "VUnit.inc.v"
	`include "Clock.inc.v"
	`include "../../eco32/src/cpu/CpuConstants.inc.v"
	`include "cpu/CpuTestHelper.inc.v"

	/** simulation **/
	initial begin
		$dumpfile("cpu/CpuJumpInstructionsTest.vcd");
		$dumpvars;
		
		/** initialization **/
		busReadData <= 32'd0;
		busWait <= 1'b0;
		interrupts <= 16'b0000000000000000;
		
		/** reset **/
		#5 reset <= 1'b1;
		#20 reset <= 1'b0;
		#20; // reset -> begin instruction

		/** test J instruction **/
		assertionContext = "test J instruction";
		cpu.generalPurposeRegisters.valueArray[31][31:0] <= 32'h12345678;
		#20 waitForBusAccess();
		assert(busAddress == 32'h20000000, "instruction fetch bus address 1");
		busReadData <= buildJInstruction(6'h2a, 26'h0000200);
		#20 waitForBusAccess();
		assert(cpu.generalPurposeRegisters.valueArray[31][31:0] == 32'h12345678, "reg 31 not affected (1)");
		assert(busAddress == 32'h20000804, "instruction fetch bus address 2");
		busReadData <= buildJInstruction(6'h2a, 26'h1ffffff);
		#20 waitForBusAccess();
		assert(cpu.generalPurposeRegisters.valueArray[31][31:0] == 32'h12345678, "reg 31 not affected (2)");
		assert(busAddress == 32'h28000804, "instruction fetch bus address 3");
		busReadData <= buildJInstruction(6'h2a, 26'h2000000);
		#20 waitForBusAccess();
		assert(cpu.generalPurposeRegisters.valueArray[31][31:0] == 32'h12345678, "reg 31 not affected (3)");
		assert(busAddress == 32'h20000808, "instruction fetch bus address 4");

		/** test JAL instruction **/
		assertionContext = "test JAL instruction";
		busReadData <= buildJInstruction(6'h2c, 26'h0000200);
		#20 waitForBusAccess();
		assert(cpu.generalPurposeRegisters.valueArray[31][31:0] == 32'he000080c, "reg 31 (4)");
		assert(busAddress == 32'h2000100c, "instruction fetch bus address 5");
		busReadData <= buildJInstruction(6'h2c, 26'h1ffffff);
		#20 waitForBusAccess();
		assert(cpu.generalPurposeRegisters.valueArray[31][31:0] == 32'he0001010, "reg 31 (5)");
		assert(busAddress == 32'h2800100c, "instruction fetch bus address 6");
		busReadData <= buildJInstruction(6'h2c, 26'h2000000);
		#20 waitForBusAccess();
		assert(cpu.generalPurposeRegisters.valueArray[31][31:0] == 32'he8001010, "reg 31 (6)");
		assert(busAddress == 32'h20001010, "instruction fetch bus address 7");

		/** move PC back **/
		assertionContext = "test JR instruction";
		busReadData <= 32'h00000000;
		cpu.programCounterRegister.programCounter <= 32'hc0d0e0f0;
		waitForBeginInstruction();
		waitForBusAccess();
		assert(cpu.generalPurposeRegisters.valueArray[31][31:0] == 32'he8001010, "reg 31 (reset PC 1)");
		assert(busAddress == 32'h00d0e0f0, "instruction fetch bus address (reset PC 1)");

		/** test JR instruction **/
		busReadData <= buildRRRInstruction(6'h2b, 5'd11, 5'd1, 5'd2);
		#20 waitForBusAccess();
		assert(cpu.generalPurposeRegisters.valueArray[31][31:0] == 32'he8001010, "reg 31 not affected");
		assert(busAddress == 32'h00c0c0c0, "instruction fetch bus address");
		
		/** move PC back **/
		assertionContext = "test JALR instruction";
		busReadData <= 32'h00000000;
		cpu.programCounterRegister.programCounter <= 32'hc0d0e0f0;
		waitForBeginInstruction();
		waitForBusAccess();
		assert(cpu.generalPurposeRegisters.valueArray[31][31:0] == 32'he8001010, "reg 31 (reset PC 2)");
		assert(busAddress == 32'h00d0e0f0, "instruction fetch bus address (reset PC 2)");

		/** test JALR instruction **/
		busReadData <= buildRRRInstruction(6'h2d, 5'd11, 5'd1, 5'd2);
		#20 waitForBusAccess();
		assert(cpu.generalPurposeRegisters.valueArray[31][31:0] == 32'hc0d0e0f4, "reg 31 affected");
		assert(busAddress == 32'h00c0c0c0, "instruction fetch bus address");

		/** done **/
		#100 $finish;
	end

endmodule
