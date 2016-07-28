`default_nettype none
`timescale 1ns / 1ps

/**
 * Basic testbench for the CPU.
 */
module CpuBasicTest ();

	/** includes **/
	`include "VUnit.inc.v"
	`include "Clock.inc.v"
	`include "../../eco32/src/cpu/CpuConstants.inc.v"
	`include "../../eco32/src/cpu/AluConstants.inc.v"

	/** simulation **/
	reg reset;
	initial begin
		$dumpfile("cpu/CpuBasicTest.vcd");
		$dumpvars;
		
		/** initialization **/
		busReadData <= 32'd0;
		busWait <= 1'b0;
		interrupts <= 16'b0000000000000000;
		cpu.generalPurposeRegisters.valueArray[0] <= 32'h00000000;
		
		/** reset **/
		#5 reset <= 1'b1;
		#20 reset <= 1'b0;
		
		/** check reset state, then perform two all-zero (NOP, actually ADD $0,$0,$0) instructions to see the PC work **/
		assertionContext = "reset state";
		assert(cpu.state == `STATE_RESET, "state");
		assert(busEnable == 1'b0, "bus enable");
		#20;
		assertionContext = "begin instruction";
		assert(cpu.state == `STATE_BEGIN_INSTRUCTION, "state");
		assert(cpu.programCounter == 32'he0000000, "pc");
		assert(cpu.processorStatusWordValue == 32'h00000000, "psw");
		assert(cpu.mmuAffectRandomIndexCounter == 1'b0, "affect random counter flag");
		#20;
		assertionContext = "translate instruction address";
		assert(cpu.state == `STATE_TRANSLATE_INSTRUCTION_ADDRESS, "state");
		assert(cpu.exceptionReturnAddressRegister == 32'he0000000, "exception return address");
		assert(cpu.mmuAffectRandomIndexCounter == 1'b1, "affect random counter flag");
		assert(cpu.virtualAddress == 32'he0000000, "MMU virtual address");
		#20;
		assertionContext = "interpret translated instruction address";
		assert(cpu.state == `STATE_INTERPRET_TRANSLATED_INSTRUCTION_ADDRESS, "state");
		assert(cpu.exceptionReturnAddressRegister == 32'he0000000, "exception return address");
		assert(cpu.mmuAffectRandomIndexCounter == 1'b0, "affect random counter flag");
		assert(cpu.virtualAddress == 32'he0000000, "MMU virtual address");
		#20;
		assertionContext = "fetch instruction";
		assert(cpu.state == `STATE_FETCH_INSTRUCTION, "state");
		assert(cpu.busEnable == 1'b1, "busEnableRegister");
		assert(cpu.busWrite == 1'b0, "busWriteRegister");
		assert(cpu.busSize == `BUS_SIZE_WORD, "cpu.busSize");
		assert(cpu.busTimeoutCounter.counter == 8'hff, "bus timeout counter");
		assert(cpu.busEnable == 1'b1, "busEnable");
		assert(cpu.busWrite == 1'b0, "busWrite");
		assert(busAddress == 32'h20000000, "bus address");
		assert(busEnable == 1'b1, "bus enable");
		assert(busSize == `BUS_SIZE_WORD, "instruction fetch bus size");
		assert(busWrite == 1'b0, "bus write");
		busReadData <= 32'h00000000;
		busWait <= 1'b0;
		#20;
		assertionContext = "decode instruction";
		assert(cpu.state == `STATE_DECODE_INSTRUCTION, "state");
		assert(cpu.busEnable == 1'b0, "busEnableRegister");
		assert(cpu.busEnable == 1'b0, "busEnable");
		assert(busEnable == 1'b0, "bus enable");
		assert(cpu.currentInstruction == 32'h00000000, "instruction register");
		#20;
		assertionContext = "compute ALU operator";
		assert(cpu.state == `STATE_COMPUTE_ALU_OPERATOR, "state");
		/** there is not much to see at this point -- everything is all-zero-bits **/
		#20;
		assertionContext = "begin instruction 2";
		assert(cpu.state == `STATE_BEGIN_INSTRUCTION, "state");
		assert(cpu.programCounter == 32'he0000004, "pc");
		assert(cpu.processorStatusWordValue == 32'h00000000, "psw");
		assert(cpu.mmuAffectRandomIndexCounter == 1'b0, "affect random counter flag");
		#20;
		assertionContext = "translate instruction address 2";
		assert(cpu.state == `STATE_TRANSLATE_INSTRUCTION_ADDRESS, "state");
		assert(cpu.exceptionReturnAddressRegister == 32'he0000004, "exception return address");
		assert(cpu.mmuAffectRandomIndexCounter == 1'b1, "affect random counter flag");
		assert(cpu.virtualAddress == 32'he0000004, "MMU virtual address");
		#20;
		assertionContext = "interpret translated instruction address 2";
		assert(cpu.state == `STATE_INTERPRET_TRANSLATED_INSTRUCTION_ADDRESS, "state");
		assert(cpu.exceptionReturnAddressRegister == 32'he0000004, "exception return address");
		assert(cpu.mmuAffectRandomIndexCounter == 1'b0, "affect random counter flag");
		assert(cpu.virtualAddress == 32'he0000004, "MMU virtual address");
		#20;
		assertionContext = "fetch instruction 2";
		assert(cpu.state == `STATE_FETCH_INSTRUCTION, "state");
		assert(cpu.busEnable == 1'b1, "busEnableRegister");
		assert(cpu.busWrite == 1'b0, "busWriteRegister");
		assert(cpu.busSize == `BUS_SIZE_WORD, "cpu.busSize");
		assert(cpu.busTimeoutCounter.counter == 8'hff, "bus timeout counter");
		assert(cpu.busEnable == 1'b1, "busEnable");
		assert(cpu.busWrite == 1'b0, "busWrite");
		assert(busAddress == 32'h20000004, "bus address");
		assert(busEnable == 1'b1, "bus enable");
		assert(busSize == `BUS_SIZE_WORD, "instruction fetch bus size");
		assert(busWrite == 1'b0, "bus write");
		busReadData <= 32'h00000000;
		busWait <= 1'b0;
		#20;
		assertionContext = "decode instruction 2";
		assert(cpu.state == `STATE_DECODE_INSTRUCTION, "state");
		assert(cpu.busEnable == 1'b0, "busEnableRegister");
		assert(cpu.busEnable == 1'b0, "busEnable");
		assert(busEnable == 1'b0, "bus enable");
		assert(cpu.currentInstruction == 32'h00000000, "instruction register");
		#20;
		assertionContext = "compute ALU operator 2";
		assert(cpu.state == `STATE_COMPUTE_ALU_OPERATOR, "state");
		/** there is not much to see at this point -- everything is all-zero-bits **/
		#20;
		assertionContext = "begin instruction 3";
		assert(cpu.state == `STATE_BEGIN_INSTRUCTION, "state");
		assert(cpu.programCounter == 32'he0000008, "pc");
		assert(cpu.processorStatusWordValue == 32'h00000000, "psw");
		assert(cpu.mmuAffectRandomIndexCounter == 1'b0, "affect random counter flag");
		#20;
		assertionContext = "translate instruction address 3";
		assert(cpu.state == `STATE_TRANSLATE_INSTRUCTION_ADDRESS, "state");
		assert(cpu.exceptionReturnAddressRegister == 32'he0000008, "exception return address");
		assert(cpu.mmuAffectRandomIndexCounter == 1'b1, "affect random counter flag");
		assert(cpu.virtualAddress == 32'he0000008, "MMU virtual address");
		#20;
		assertionContext = "interpret translated instruction address 3";
		assert(cpu.state == `STATE_INTERPRET_TRANSLATED_INSTRUCTION_ADDRESS, "state");
		assert(cpu.exceptionReturnAddressRegister == 32'he0000008, "exception return address");
		assert(cpu.mmuAffectRandomIndexCounter == 1'b0, "affect random counter flag");
		assert(cpu.virtualAddress == 32'he0000008, "MMU virtual address");

		/** now we use ADDI $1, $0, 0x42 to get some more interesting values **/

		#20;
		assertionContext = "fetch instruction ADDI 1";
		assert(cpu.state == `STATE_FETCH_INSTRUCTION, "state");
		assert(cpu.busEnable == 1'b1, "busEnableRegister");
		assert(cpu.busWrite == 1'b0, "busWriteRegister");
		assert(cpu.busSize == `BUS_SIZE_WORD, "cpu.busSize");
		assert(cpu.busTimeoutCounter.counter == 8'hff, "bus timeout counter");
		assert(cpu.busEnable == 1'b1, "busEnable");
		assert(cpu.busWrite == 1'b0, "busWrite");
		assert(busAddress == 32'h20000008, "bus address");
		assert(busEnable == 1'b1, "bus enable");
		assert(busSize == `BUS_SIZE_WORD, "instruction fetch bus size");
		assert(busWrite == 1'b0, "bus write");
		busReadData <= 32'b000001_00000_00001_0000_0000_0100_0010;
		busWait <= 1'b0;
		#20;
		assertionContext = "decode instruction ADDI 1";
		assert(cpu.state == `STATE_DECODE_INSTRUCTION, "state");
		assert(cpu.busEnable == 1'b0, "busEnableRegister");
		assert(cpu.busEnable == 1'b0, "busEnable");
		assert(cpu.currentInstruction == 32'b000001_00000_00001_0000_0000_0100_0010, "instruction register");
		assert(busEnable == 1'b0, "bus enable");
		#20;
		assertionContext = "compute ALU operator ADDI 1";
		assert(cpu.state == `STATE_COMPUTE_ALU_OPERATOR, "state");
		assert(cpu.currentInstructionAluOperation == `ALU_OPERATION_ADD, "ALU operation");
		assert(cpu.leftOperandValue == 32'h00000000, "ALU left operand");
		assert(cpu.rightOperandValue == 32'h00000042, "ALU right operand");
		assert(cpu.aluResult == 32'h00000042, "ALU result");
		#20;
		assert(cpu.generalPurposeRegisters.valueArray[1] == 32'h00000042, "register 1");

		/** finally, we use ADD $1, $1, $2 to double the value. The instruction fetch also gets bus wait cycles. **/
		assertionContext = "double instruction with bus wait";
		assert(cpu.state == `STATE_BEGIN_INSTRUCTION, "STATE_BEGIN_INSTRUCTION");
		#20 assert(cpu.state == `STATE_TRANSLATE_INSTRUCTION_ADDRESS, "STATE_TRANSLATE_INSTRUCTION_ADDRESS");
		#20 assert(cpu.state == `STATE_INTERPRET_TRANSLATED_INSTRUCTION_ADDRESS, "STATE_INTERPRET_TRANSLATED_INSTRUCTION_ADDRESS");
		#20 assert(cpu.state == `STATE_FETCH_INSTRUCTION, "state");
		assert(cpu.busEnable == 1'b1, "busEnableRegister");
		assert(cpu.busWrite == 1'b0, "busWriteRegister");
		assert(cpu.busSize == `BUS_SIZE_WORD, "cpu.busSize");
		assert(cpu.busTimeoutCounter.counter == 8'hff, "bus timeout counter");
		assert(cpu.busEnable == 1'b1, "busEnable");
		assert(cpu.busWrite == 1'b0, "busWrite");
		assert(busAddress == 32'h2000000c, "bus address");
		assert(busEnable == 1'b1, "bus enable");
		assert(busSize == `BUS_SIZE_WORD, "instruction fetch bus size");
		assert(busWrite == 1'b0, "bus write");
		busReadData <= 32'b000000_00001_00001_00010_00000000000;
		busWait <= 1'b1;
		#20;
		assert(cpu.busEnable == 1'b1, "busEnableRegister");
		assert(cpu.busWrite == 1'b0, "busWriteRegister");
		assert(cpu.busSize == `BUS_SIZE_WORD, "cpu.busSize");
		assert(cpu.busTimeoutCounter.counter == 8'hfe, "bus timeout counter");
		assert(cpu.busEnable == 1'b1, "busEnable");
		assert(cpu.busWrite == 1'b0, "busWrite");
		assert(busAddress == 32'h2000000c, "bus address");
		assert(busEnable == 1'b1, "bus enable");
		assert(busSize == `BUS_SIZE_WORD, "instruction fetch bus size");
		assert(busWrite == 1'b0, "bus write");
		#20;
		assert(cpu.busEnable == 1'b1, "busEnableRegister");
		assert(cpu.busWrite == 1'b0, "busWriteRegister");
		assert(cpu.busSize == `BUS_SIZE_WORD, "cpu.busSize");
		assert(cpu.busTimeoutCounter.counter == 8'hfd, "bus timeout counter");
		assert(cpu.busEnable == 1'b1, "busEnable");
		assert(cpu.busWrite == 1'b0, "busWrite");
		assert(busAddress == 32'h2000000c, "bus address");
		assert(busEnable == 1'b1, "bus enable");
		assert(busSize == `BUS_SIZE_WORD, "instruction fetch bus size");
		assert(busWrite == 1'b0, "bus write");
		busWait <= 1'b0;
		#20 assert(cpu.state == `STATE_DECODE_INSTRUCTION, "STATE_DECODE_INSTRUCTION");
		#20 assert(cpu.state == `STATE_COMPUTE_ALU_OPERATOR, "STATE_COMPUTE_ALU_OPERATOR");
		#20 assert(cpu.state == `STATE_BEGIN_INSTRUCTION, "STATE_BEGIN_INSTRUCTION");
		assert(cpu.generalPurposeRegisters.valueArray[1] == 32'h00000042, "register 1");
		assert(cpu.generalPurposeRegisters.valueArray[2] == 32'h00000084, "register 2");
		
		/** done **/
		#100 $finish;
	end

	/** the module under test **/
	wire busEnable;
	wire[1:0] busSize;
	wire busWrite;
	wire[31:0] busAddress;
	wire[31:0] busWriteData;
	reg[31:0] busReadData;
	reg busWait;
	reg[15:0] interrupts;
	Cpu cpu (
		.clock(clock),
		.reset(reset),
		.busEnable(busEnable),
		.busSize(busSize),
		.busWrite(busWrite),
		.busAddress(busAddress),
		.busWriteData(busWriteData),
		.busReadData(busReadData),
		.busWait(busWait),
		.interrupts(interrupts)
	);
		
endmodule
