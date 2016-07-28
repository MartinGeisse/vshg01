
function[31:0] buildRRRInstruction(input[5:0] opcode, input[4:0] leftOperandIndex, input[4:0] rightOperandIndex, input[4:0] destinationIndex);
	begin
		buildRRRInstruction = {opcode, leftOperandIndex, rightOperandIndex, destinationIndex, 11'd0};
	end
endfunction

function[31:0] buildRRIInstruction(input[5:0] opcode, input[4:0] leftOperandIndex, input[4:0] destinationIndex, input[15:0] immediateValue);
	begin
		buildRRIInstruction = {opcode, leftOperandIndex, destinationIndex, immediateValue};
	end
endfunction

function[31:0] buildJInstruction(input[5:0] opcode, input[25:0] wordOffset);
	begin
		buildJInstruction = {opcode, wordOffset};
	end
endfunction

task waitForBusAccess;
	begin
		while (~busEnable) begin
			#20;
		end
	end
endtask

task waitForBeginInstruction;
	begin
		while (cpu.state != `STATE_BEGIN_INSTRUCTION) begin
			#20;
		end
	end
endtask

task testComputationInstructionR(input[5:0] opcode, input[4:0] operandIndex1, input[4:0] operandIndex2, input[31:0] expectedResultValue, input[799:0] message);
	begin
		waitForBusAccess();
		busReadData <= buildRRRInstruction(opcode, operandIndex1, operandIndex2, 25);
		busWait <= 1'b0;
		waitForBeginInstruction();
		assert(cpu.generalPurposeRegisters.valueArray[25] == expectedResultValue, message);
	end
endtask

task testComputationInstructionI(input[5:0] opcode, input[4:0] operandIndex1, input[15:0] immediateOperandValue, input[31:0] expectedResultValue, input[799:0] message);
	begin
		waitForBusAccess();
		busReadData <= buildRRIInstruction(opcode, operandIndex1, 25, immediateOperandValue);
		busWait <= 1'b0;
		waitForBeginInstruction();
		assert(cpu.generalPurposeRegisters.valueArray[25] == expectedResultValue, message);
	end
endtask

task testBranchInstruction(input[5:0] opcode, input[4:0] operandIndex1, input[4:0] operandIndex2, input expectBranch, input[799:0] message);
	begin
		busReadData <= 32'h00000000;
		waitForBeginInstruction();
		cpu.programCounterRegister.programCounter <= 32'hf0000000;
		waitForBusAccess();
		assert(busAddress == 32'h30000000, "instruction fetch bus address");
		busReadData <= buildRRIInstruction(opcode, operandIndex1, operandIndex2, 32'h100);
		busWait <= 1'b0;
		waitForBeginInstruction();
		waitForBusAccess();
		if (expectBranch) begin
			assert(busAddress == 32'h30000404, message);
		end else begin
			assert(busAddress == 32'h30000004, message);
		end
	end
endtask

reg[31:0] cpuTestHelperIterationVariable;
initial begin
	cpu.generalPurposeRegisters.valueArray[0] <= 32'h00000000;
	cpu.generalPurposeRegisters.valueArray[1] <= 32'h00000001;
	cpu.generalPurposeRegisters.valueArray[2] <= 32'hffffffff;
	cpu.generalPurposeRegisters.valueArray[3] <= 32'h00000002;
	cpu.generalPurposeRegisters.valueArray[4] <= 32'hfffffffe;
	cpu.generalPurposeRegisters.valueArray[5] <= 32'd123;
	cpu.generalPurposeRegisters.valueArray[6] <= (32'd0 - 32'd456);
	cpu.generalPurposeRegisters.valueArray[7] <= 32'h2a70f632;
	cpu.generalPurposeRegisters.valueArray[8] <= 32'ha1234567;
	cpu.generalPurposeRegisters.valueArray[9] <= 32'h80000000;
	cpu.generalPurposeRegisters.valueArray[10] <= 32'h2a70f632;
	cpu.generalPurposeRegisters.valueArray[11] <= 32'hc0c0c0c0;

	for (cpuTestHelperIterationVariable = 0; cpuTestHelperIterationVariable < 32; cpuTestHelperIterationVariable = cpuTestHelperIterationVariable + 1) begin
		cpu.memoryManagementUnit.tlbKeyMemory.keyArray[cpuTestHelperIterationVariable] <= 20'h0;
		cpu.memoryManagementUnit.tlbValueMemory.valueArray[cpuTestHelperIterationVariable] <= 22'h0;
	end
	
	cpu.memoryManagementUnit.tlbKeyMemory.keyArray[5] <= 20'h12345;
	cpu.memoryManagementUnit.tlbValueMemory.valueArray[5] <= {20'h55555, 2'b11};
	cpu.memoryManagementUnit.tlbKeyMemory.keyArray[6] <= 20'h2468a;
	cpu.memoryManagementUnit.tlbValueMemory.valueArray[6] <= {20'h77777, 2'b11};
	cpu.memoryManagementUnit.tlbKeyMemory.keyArray[7] <= 20'h2468a;
	cpu.memoryManagementUnit.tlbValueMemory.valueArray[7] <= {20'h99999, 2'b11};
	cpu.memoryManagementUnit.tlbKeyMemory.keyArray[8] <= 20'h36936;
	cpu.memoryManagementUnit.tlbValueMemory.valueArray[8] <= {20'haaaaa, 2'b01};
end

/** the module under test **/
reg reset;
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
