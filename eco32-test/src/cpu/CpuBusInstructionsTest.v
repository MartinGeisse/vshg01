`default_nettype none
`timescale 1ns / 1ps

/**
 * Testbench for bus access instructions.
 */
module CPUBusInstructionsTest ();

	/** includes **/
	`include "VUnit.inc.v"
	`include "Clock.inc.v"
	`include "../../eco32/src/cpu/CpuConstants.inc.v"
	`include "cpu/CpuTestHelper.inc.v"

	/** simulation **/
	initial begin
		$dumpfile("cpu/CpuBusInstructionsTest.vcd");
		$dumpvars;
		
		/** initialization **/
		busReadData <= 32'd0;
		busWait <= 1'b0;
		interrupts <= 16'b0000000000000000;
		
		/** reset **/
		#5 reset <= 1'b1;
		#20 reset <= 1'b0;
		#20; // reset -> begin instruction
		#20 waitForBusAccess();
		assert(busAddress == 32'h20000000, "instruction fetch bus address");
		assert(busSize == `BUS_SIZE_WORD, "instruction fetch bus size");

		/** test LDW instruction **/
		assertionContext = "test LDW instruction";
		busReadData <= buildRRIInstruction(6'h30, 5'd0, 5'd15, 16'hfff0);
		#20 waitForBusAccess();
		assert(busAddress == 32'h3ffffff0, "LDW bus address");
		assert(busSize == `BUS_SIZE_WORD, "LDW bus size");
		assert(busWrite == 1'b0, "LDW bus write");
		busReadData <= 32'h18273645;
		#20 waitForBusAccess();
		assert(busAddress == 32'h20000004, "instruction fetch bus address");
		assert(busSize == `BUS_SIZE_WORD, "instruction fetch bus size");
		assert(cpu.generalPurposeRegisters.valueArray[15] == 32'h18273645, "LDW data register");

		/** test address computation **/
		assertionContext = "test address computation";
		busReadData <= buildRRIInstruction(6'h30, 5'd11, 5'd15, 16'h0010);
		#20 waitForBusAccess();
		assert(busAddress == 32'h00c0c0d0, "LDW bus address");
		assert(busSize == `BUS_SIZE_WORD, "LDW bus size");
		assert(busWrite == 1'b0, "LDW bus write");
		busReadData <= 32'h18273646;
		#20 waitForBusAccess();
		assert(busAddress == 32'h20000008, "instruction fetch bus address");
		assert(busSize == `BUS_SIZE_WORD, "instruction fetch bus size");
		assert(cpu.generalPurposeRegisters.valueArray[15] == 32'h18273646, "LDW data register");

		/** test LDH instruction **/
		assertionContext = "test LDH instruction";
		busReadData <= buildRRIInstruction(6'h31, 5'd0, 5'd15, 16'hfff2);
		#20 waitForBusAccess();
		assert(busAddress == 32'h3ffffff2, "LDH bus address");
		assert(busSize == `BUS_SIZE_HALFWORD, "LDH bus size");
		assert(busWrite == 1'b0, "LDH bus write");
		busReadData <= 32'h18273645;
		#20 waitForBusAccess();
		assert(busAddress == 32'h2000000c, "instruction fetch bus address");
		assert(busSize == `BUS_SIZE_WORD, "instruction fetch bus size");
		assert(cpu.generalPurposeRegisters.valueArray[15] == 32'h00003645, "LDH data register");

		/** test LDH instruction with negative result **/
		assertionContext = "test LDH instruction (negative)";
		busReadData <= buildRRIInstruction(6'h31, 5'd0, 5'd15, 16'hfff2);
		#20 waitForBusAccess();
		assert(busAddress == 32'h3ffffff2, "LDH bus address");
		assert(busSize == `BUS_SIZE_HALFWORD, "LDH bus size");
		assert(busWrite == 1'b0, "LDH bus write");
		busReadData <= 32'h88888888;
		#20 waitForBusAccess();
		assert(busAddress == 32'h20000010, "instruction fetch bus address");
		assert(busSize == `BUS_SIZE_WORD, "instruction fetch bus size");
		assert(cpu.generalPurposeRegisters.valueArray[15] == 32'hffff8888, "LDH data register");

		/** test LDHU instruction **/
		assertionContext = "test LDHU instruction";
		busReadData <= buildRRIInstruction(6'h32, 5'd0, 5'd15, 16'hfff2);
		#20 waitForBusAccess();
		assert(busAddress == 32'h3ffffff2, "LDHU bus address");
		assert(busSize == `BUS_SIZE_HALFWORD, "LDHU bus size");
		assert(busWrite == 1'b0, "LDHU bus write");
		busReadData <= 32'h18273645;
		#20 waitForBusAccess();
		assert(busAddress == 32'h20000014, "instruction fetch bus address");
		assert(busSize == `BUS_SIZE_WORD, "instruction fetch bus size");
		assert(cpu.generalPurposeRegisters.valueArray[15] == 32'h00003645, "LDHU data register");

		/** test LDHU instruction with large result **/
		assertionContext = "test LDHU instruction (large)";
		busReadData <= buildRRIInstruction(6'h32, 5'd0, 5'd15, 16'hfff2);
		#20 waitForBusAccess();
		assert(busAddress == 32'h3ffffff2, "LDHU bus address");
		assert(busSize == `BUS_SIZE_HALFWORD, "LDHU bus size");
		assert(busWrite == 1'b0, "LDHU bus write");
		busReadData <= 32'h88888888;
		#20 waitForBusAccess();
		assert(busAddress == 32'h20000018, "instruction fetch bus address");
		assert(busSize == `BUS_SIZE_WORD, "instruction fetch bus size");
		assert(cpu.generalPurposeRegisters.valueArray[15] == 32'h00008888, "LDHU data register");

		/** test LDB instruction **/
		assertionContext = "test LDB instruction";
		busReadData <= buildRRIInstruction(6'h33, 5'd0, 5'd15, 16'hfff3);
		#20 waitForBusAccess();
		assert(busAddress == 32'h3ffffff3, "LDB bus address");
		assert(busSize == `BUS_SIZE_BYTE, "LDB bus size");
		assert(busWrite == 1'b0, "LDB bus write");
		busReadData <= 32'h18273645;
		#20 waitForBusAccess();
		assert(busAddress == 32'h2000001c, "instruction fetch bus address");
		assert(busSize == `BUS_SIZE_WORD, "instruction fetch bus size");
		assert(cpu.generalPurposeRegisters.valueArray[15] == 32'h00000045, "LDB data register");

		/** test LDB instruction with negative result **/
		assertionContext = "test LDB instruction (negative)";
		busReadData <= buildRRIInstruction(6'h33, 5'd0, 5'd15, 16'hfff3);
		#20 waitForBusAccess();
		assert(busAddress == 32'h3ffffff3, "LDB bus address");
		assert(busSize == `BUS_SIZE_BYTE, "LDB bus size");
		assert(busWrite == 1'b0, "LDB bus write");
		busReadData <= 32'h88888888;
		#20 waitForBusAccess();
		assert(busAddress == 32'h20000020, "instruction fetch bus address");
		assert(busSize == `BUS_SIZE_WORD, "instruction fetch bus size");
		assert(cpu.generalPurposeRegisters.valueArray[15] == 32'hffffff88, "LDB data register");

		/** test LDBU instruction **/
		assertionContext = "test LDBU instruction";
		busReadData <= buildRRIInstruction(6'h34, 5'd0, 5'd15, 16'hfff3);
		#20 waitForBusAccess();
		assert(busAddress == 32'h3ffffff3, "LDBU bus address");
		assert(busSize == `BUS_SIZE_BYTE, "LDBU bus size");
		assert(busWrite == 1'b0, "LDBU bus write");
		busReadData <= 32'h18273645;
		#20 waitForBusAccess();
		assert(busAddress == 32'h20000024, "instruction fetch bus address");
		assert(busSize == `BUS_SIZE_WORD, "instruction fetch bus size");
		assert(cpu.generalPurposeRegisters.valueArray[15] == 32'h00000045, "LDBU data register");

		/** test LDBU instruction with large result **/
		assertionContext = "test LDBU instruction (large)";
		busReadData <= buildRRIInstruction(6'h34, 5'd0, 5'd15, 16'hfff3);
		#20 waitForBusAccess();
		assert(busAddress == 32'h3ffffff3, "LDBU bus address");
		assert(busSize == `BUS_SIZE_BYTE, "LDBU bus size");
		assert(busWrite == 1'b0, "LDBU bus write");
		busReadData <= 32'h88888888;
		#20 waitForBusAccess();
		assert(busAddress == 32'h20000028, "instruction fetch bus address");
		assert(busSize == `BUS_SIZE_WORD, "instruction fetch bus size");
		assert(cpu.generalPurposeRegisters.valueArray[15] == 32'h00000088, "LDBU data register");

		/** test STW instruction **/
		assertionContext = "test STW instruction";
		busReadData <= buildRRIInstruction(6'h35, 5'd0, 5'd7, 16'hfff0);
		#20 waitForBusAccess();
		assert(busAddress == 32'h3ffffff0, "STW bus address");
		assert(busSize == `BUS_SIZE_WORD, "LDW bus size");
		assert(busWrite == 1'b1, "STW bus write");
		assert(busWriteData[31:0] == 32'h2a70f632, "STW bus write data");
		#20 waitForBusAccess();
		assert(busAddress == 32'h2000002c, "instruction fetch bus address");
		assert(busSize == `BUS_SIZE_WORD, "instruction fetch bus size");

		/** test STH instruction **/
		assertionContext = "test STH instruction";
		busReadData <= buildRRIInstruction(6'h36, 5'd0, 5'd7, 16'hfff2);
		#20 waitForBusAccess();
		assert(busAddress == 32'h3ffffff2, "STH bus address");
		assert(busSize == `BUS_SIZE_HALFWORD, "LDH bus size");
		assert(busWrite == 1'b1, "STH bus write");
		assert(busWriteData[15:0] == 32'hf632, "STH bus write data");
		#20 waitForBusAccess();
		assert(busAddress == 32'h20000030, "instruction fetch bus address");
		assert(busSize == `BUS_SIZE_WORD, "instruction fetch bus size");

		/** test STH instruction **/
		assertionContext = "test STB instruction";
		busReadData <= buildRRIInstruction(6'h37, 5'd0, 5'd7, 16'hfff3);
		#20 waitForBusAccess();
		assert(busAddress == 32'h3ffffff3, "STB bus address");
		assert(busSize == `BUS_SIZE_BYTE, "STB bus size");
		assert(busWrite == 1'b1, "STB bus write");
		assert(busWriteData[15:0] == 32'hf632, "STB bus write data");
		#20 waitForBusAccess();
		assert(busAddress == 32'h20000034, "instruction fetch bus address");
		assert(busSize == `BUS_SIZE_WORD, "instruction fetch bus size");

		/** test LDW with bus wait **/
		assertionContext = "test LDW instruction with bus wait";
		busReadData <= buildRRIInstruction(6'h30, 5'd0, 5'd15, 16'hfff0);
		#20 waitForBusAccess();
		assert(busAddress == 32'h3ffffff0, "LDW bus address");
		assert(busSize == `BUS_SIZE_WORD, "LDW bus size");
		assert(busWrite == 1'b0, "LDW bus write");
		busWait <= 1'b1;
		#100;
		assert(busEnable == 1'b1, "bus enable after wait");
		assert(busAddress == 32'h3ffffff0, "LDW bus address after wait");
		assert(busSize == `BUS_SIZE_WORD, "LDW bus size after wait");
		assert(busWrite == 1'b0, "LDW bus write after wait");
		busReadData <= 32'h18273645;
		busWait <= 1'b0;
		#20;
		assert(busEnable == 1'b0, "bus enable after wait and clock edge");
		waitForBusAccess();
		assert(busAddress == 32'h20000038, "instruction fetch bus address");
		assert(busSize == `BUS_SIZE_WORD, "instruction fetch bus size");
		assert(cpu.generalPurposeRegisters.valueArray[15] == 32'h18273645, "LDW data register");

		/** test STW with bus wait **/
		assertionContext = "test STW instruction with bus wait";
		busReadData <= buildRRIInstruction(6'h35, 5'd0, 5'd7, 16'hfff0);
		#20 waitForBusAccess();
		assert(busAddress == 32'h3ffffff0, "STW bus address");
		assert(busSize == `BUS_SIZE_WORD, "LDW bus size");
		assert(busWrite == 1'b1, "STW bus write");
		assert(busWriteData[31:0] == 32'h2a70f632, "STW bus write data");
		busWait <= 1'b1;
		#100;
		assert(busEnable == 1'b1, "bus enable after wait");
		assert(busAddress == 32'h3ffffff0, "STW bus address after wait");
		assert(busSize == `BUS_SIZE_WORD, "LDW bus size after wait");
		assert(busWrite == 1'b1, "STW bus write after wait");
		assert(busWriteData[31:0] == 32'h2a70f632, "STW bus write data after wait");
		busWait <= 1'b0;
		#20;
		assert(busEnable == 1'b0, "bus enable after wait and clock edge");
		waitForBusAccess();
		assert(busAddress == 32'h2000003c, "instruction fetch bus address");
		assert(busSize == `BUS_SIZE_WORD, "instruction fetch bus size");

		/** done **/
		#100 $finish;
	end

endmodule
