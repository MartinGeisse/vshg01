`default_nettype none
`timescale 1ns / 1ps

/**
 * The CPU.
 */
module Cpu (

		/** the clock **/
		input clock,

		/** the reset signal **/
		input reset,

		/** bus signal: access enable **/
		output reg busEnable,

		/** bus signal: write enable **/
		output reg busWrite,

		/** bus signal: address **/
		output[31:0] busAddress,

		/** bus signal: data to write **/
		output[31:0] busWriteData,

		/** bus signal: access size **/
		output reg[1:0] busSize,

		/** bus signal: data read from a device **/
		input[31:0] busReadData,

		/** bus signal: delay end of access **/
		input busWait,

		/** interrupt lines from peripheral devices **/
		input[15:0] interrupts

	);

	/**
	 * Include CPU constant definition files.
	 */
	`include "CpuConstants.inc.v"
	`include "AluConstants.inc.v"
	`include "MemoryManagementUnitConstants.inc.v"
	`include "ProgramCounterConstants.inc.v"
	`include "ProcessorStatusWordConstants.inc.v"
	`include "ShifterConstants.inc.v"
	`include "VirtualAddressRegisterConstants.inc.v"

	/**
	 * gtkwave does not visualize arrays, so we have to resort to a bunch of
	 * signals showing the array contents. We disable these signals in
	 * synthesis mode to avoid warnings about unused signals.
	 */
	`ifdef SIMULATION
		wire[31:0] reg0 = generalPurposeRegisters.valueArray[0];
		wire[31:0] reg1 = generalPurposeRegisters.valueArray[1];
		wire[31:0] reg2 = generalPurposeRegisters.valueArray[2];
		wire[31:0] reg3 = generalPurposeRegisters.valueArray[3];
		wire[31:0] reg4 = generalPurposeRegisters.valueArray[4];
		wire[31:0] reg5 = generalPurposeRegisters.valueArray[5];
		wire[31:0] reg6 = generalPurposeRegisters.valueArray[6];
		wire[31:0] reg7 = generalPurposeRegisters.valueArray[7];
		wire[31:0] reg8 = generalPurposeRegisters.valueArray[8];
		wire[31:0] reg9 = generalPurposeRegisters.valueArray[9];
		wire[31:0] reg10 = generalPurposeRegisters.valueArray[10];
		wire[31:0] reg11 = generalPurposeRegisters.valueArray[11];
		wire[31:0] reg12 = generalPurposeRegisters.valueArray[12];
		wire[31:0] reg13 = generalPurposeRegisters.valueArray[13];
		wire[31:0] reg14 = generalPurposeRegisters.valueArray[14];
		wire[31:0] reg15 = generalPurposeRegisters.valueArray[15];
		wire[31:0] reg16 = generalPurposeRegisters.valueArray[16];
		wire[31:0] reg17 = generalPurposeRegisters.valueArray[17];
		wire[31:0] reg18 = generalPurposeRegisters.valueArray[18];
		wire[31:0] reg19 = generalPurposeRegisters.valueArray[19];
		wire[31:0] reg20 = generalPurposeRegisters.valueArray[20];
		wire[31:0] reg21 = generalPurposeRegisters.valueArray[21];
		wire[31:0] reg22 = generalPurposeRegisters.valueArray[22];
		wire[31:0] reg23 = generalPurposeRegisters.valueArray[23];
		wire[31:0] reg24 = generalPurposeRegisters.valueArray[24];
		wire[31:0] reg25 = generalPurposeRegisters.valueArray[25];
		wire[31:0] reg26 = generalPurposeRegisters.valueArray[26];
		wire[31:0] reg27 = generalPurposeRegisters.valueArray[27];
		wire[31:0] reg28 = generalPurposeRegisters.valueArray[28];
		wire[31:0] reg29 = generalPurposeRegisters.valueArray[29];
		wire[31:0] reg30 = generalPurposeRegisters.valueArray[30];
		wire[31:0] reg31 = generalPurposeRegisters.valueArray[31];
	`endif

	/************************************************************************************
	 * Signal definitions
	 ************************************************************************************/

	/**
	 * Signals used by the main state machine.
	 */
	reg[`STATEREG_WIDTH-1:0] state;
	reg[`STATEREG_WIDTH-1:0] nextState;

	/**
	 * Signals used by the PC register.
	 */
	wire[31:0] programCounter;
	reg programCounterWriteEnable;
	reg[`PC_SOURCE_WIDTH-1:0] programCounterWriteDataSource;

	/**
	 * Signals used by the exception return address register. This register is loaded with
	 * the PC value at the beginning of each instruction, so it is not affected by changes
	 * to the PC.
	 */
	reg[31:0] exceptionReturnAddressRegister;
	reg exceptionReturnAddressRegisterWriteEnable;

	/**
	 * Signals used by the virtual address register.
	 */
	reg virtualAddressRegisterWriteEnable;
	reg[`VIRTUAL_ADDRESS_REGISTER_WRITE_DATA_SOURCE_WIDTH-1:0] virtualAddressRegisterWriteDataSource;
	wire[31:0] virtualAddress;

	/**
	 * Signals used by the current instruction register.
	 */
	reg currentInstructionRegisterWriteEnable;
	wire[31:0] currentInstruction;

	/**
	 * Signals used by the instruction decoder.
	 */
	wire[5:0] currentInstructionOpcode;
	wire currentInstructionRightOperandIsImmediate;
	wire[4:0] currentInstructionLeftOperandRegisterIndex;
	wire[4:0] currentInstructionRightOperandRegisterIndex;
	wire[4:0] currentInstructionDestinationRegisterIndex;
	wire[31:0] currentInstructionExtendedImmediateValue;
	wire[29:0] currentInstructionExtendedJumpOffset;
	wire[2:0] currentInstructionAluOperation;
	wire currentInstructionSignedComparison;
	wire[2:0] currentInstructionComparisonOperation;
	wire currentInstructionLoadStoreIsStore;
	wire[1:0] currentInstructionBusSize;
	wire currentInstructionExtendMode;

	/**
	 * Signals used by the general-purpose registers.
	 */
	reg generalPurposeRegistersWriteEnable2;
	reg[`GENERAL_PURPOSE_REGISTERS_INDEX_2_SOURCE_WIDTH-1:0] generalPurposeRegistersIndex2Source;
	reg[`GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_WIDTH-1:0] generalPurposeRegistersData2Source;
	reg[4:0] generalPurposeRegisterIndex2;
	wire generalPurposeRegistersWriteEnable2ZeroRespecting = generalPurposeRegistersWriteEnable2 & (generalPurposeRegisterIndex2 != 5'd0);
	reg[31:0] generalPurposeRegisterWriteData2;
	wire[31:0] leftOperandRegisterValue;
	wire[31:0] rightOperandRegisterValue;

	/**
	 * Signals used for the actual operand values, which may come from immediate operands instead of the register values.
	 */
	wire[31:0] leftOperandValue;
	wire[31:0] rightOperandValue;

	/**
	 * Signals used by the ALU.
	 */
	wire[31:0] aluResult;
	wire aluComparisonResult;

	/**
	 * Signals used by the multiplier.
	 */
	wire[31:0] multiplierResult;

	/**
	 * Signals used by the divider.
	 */
	reg dividerSigned;
	reg dividerActivate;
	wire dividerDone;
	wire[31:0] dividerQuotient;
	wire[31:0] dividerRemainder;
	wire divisionByZero;

	/**
	 * Signals used by the shifter.
	 */
	wire[31:0] shifterResult;

	/**
	 * Signals used by the bus access logic
	 */
	reg clearBusTimeoutCounter;
	wire busTimeout;

	/**
	 * The raw data input register. This register is used to store
	 * the busReadData at arrival. The value is not ready to use since
	 * it must be extended to 32 bits according to the bus access size.
	 */
	reg[31:0] rawBusReadDataInputRegister;

	/**
	 * This signal asynchronously computes the extended data-in value.
	 * Note that this is a "reg" in Verilog terms, but not an actual register.
	 */
	reg[31:0] extendedBusReadDataInput;

	/**
	 * Signals used by the PSW register.
	 */
	reg processorStatusWordWriteEnable;
	reg[`PSW_WRITE_DATA_SOURCE_WIDTH-1:0] pswWriteDataSource;
	wire[31:0] processorStatusWordValue;
	wire userMode;

	/**
	 * Signals used by the MMU.
	 */
	reg mmuAffectRandomIndexCounter;
	wire mmuTlbMiss;
	wire[31:0] mmuPhysicalAddress;
	wire mmuPhysicalAddressWriteAllowed;
	wire mmuPhysicalAddressValid;
	wire[31:0] mmuSpecialRegisterReadValue;

	/**
	 * TLB special register control signals
	 */
	reg[2:0] mmuSpecialRegisterReadIndex;
	reg tlbIndexWriteEnable;
	reg tlbIndexDataSource;
	reg tlbEntryHighWriteEnable;
	reg[1:0] tlbEntryHighDataSource;
	reg tlbEntryLowWriteEnable;
	reg[1:0] tlbEntryLowDataSource;
	reg tlbBadAddressWriteEnable;
	reg tlbBadAddressDataSource;

	/**
	 * TLB entry access control signals
	 */
	reg tlbEntryRandomizeIndex;
	reg tlbEntryWriteEnable;

	/**
	 * Result signal from the MVFS data MUX
	 */
	wire[31:0] mvfsValue;

	/**
	 * The exception priority to load in case an exception occurs. This value is only respected if at
	 * the same time a transition to the exception state occurs, since the exception priority
	 * register is otherwise overwritten again in the next clock cycle.
	 */
	reg[4:0] detectedExceptionPriority;
	
	/**
	 * This is a registered version of detectedExceptionPriority that is loaded on every clock cycle
	 * (hence the requirement that detectedExceptionPriority must be set at the same time the
	 * transition to the exception state occurs).
	 */
	reg[4:0] detectedExceptionPriorityRegister;

	/**
	 * Signals used by the Interrupt detector
	 */
	wire interruptDetectorActive;
	wire[3:0] interruptDetectorIndex;
	
	/************************************************************************************
	 * Sub-modules
	 ************************************************************************************/
	
	/**
	 * The PC register.
	 */
	ProgramCounterRegister programCounterRegister (
		.clock(clock),
		.reset(reset),
		.writeEnable(programCounterWriteEnable),
		.writeDataSource(programCounterWriteDataSource),
		.extendedImmediateValue(currentInstructionExtendedImmediateValue[29:0]),
		.extendedJumpOffset(currentInstructionExtendedJumpOffset),
		.explicitValue(state == `STATE_PERFORM_RFX ? rightOperandRegisterValue : leftOperandValue),
		.vectorBit(processorStatusWordValue[27]),
		.programCounter(programCounter)
	);
	
	/**
	 * Loading logic for the exception return address register.
	 */
	always @(posedge clock) begin
		if (exceptionReturnAddressRegisterWriteEnable) begin
			exceptionReturnAddressRegister <= programCounter;
		end
	end

	/**
	 * The virtual address register
	 */
	VirtualAddressRegister virtualAddressRegister (
		.clock(clock),
		.writeEnable(virtualAddressRegisterWriteEnable),
		.writeDataSource(virtualAddressRegisterWriteDataSource),
		.programCounter(programCounter),
		.addressComputationResult(aluResult),
		.mmuSpecialRegisterReadData(mmuSpecialRegisterReadValue),
		.virtualAddress(virtualAddress)
	);

	/**
	 * The current instruction register.
	 */
	CurrentInstructionRegister currentInstructionRegister (
		.clock(clock),
		.writeEnable(currentInstructionRegisterWriteEnable),
		.busReadData(busReadData),
		.currentInstruction(currentInstruction)
	);

	/**
	 * The instruction decoder.
	 */
	InstructionDecoder instructionDecoder (
		.currentInstruction(currentInstruction),
		.currentInstructionOpcode(currentInstructionOpcode),
		.currentInstructionRightOperandIsImmediate(currentInstructionRightOperandIsImmediate),
		.currentInstructionLeftOperandRegisterIndex(currentInstructionLeftOperandRegisterIndex),
		.currentInstructionRightOperandRegisterIndex(currentInstructionRightOperandRegisterIndex),
		.currentInstructionDestinationRegisterIndex(currentInstructionDestinationRegisterIndex),
		.currentInstructionExtendedImmediateValue(currentInstructionExtendedImmediateValue),
		.currentInstructionExtendedJumpOffset(currentInstructionExtendedJumpOffset),
		.currentInstructionAluOperation(currentInstructionAluOperation),
		.currentInstructionSignedComparison(currentInstructionSignedComparison),
		.currentInstructionComparisonOperation(currentInstructionComparisonOperation),
		.currentInstructionLoadStoreIsStore(currentInstructionLoadStoreIsStore),
		.currentInstructionBusSize(currentInstructionBusSize),
		.currentInstructionExtendMode(currentInstructionExtendMode)
	);

	/**
	 * Index source MUX for port 2 of the general purpose registers.
	 */
	always @(*) begin
		case (generalPurposeRegistersIndex2Source)

			`GENERAL_PURPOSE_REGISTERS_INDEX_2_SOURCE_RIGHT_OPERAND: begin
				generalPurposeRegisterIndex2 <= currentInstructionRightOperandRegisterIndex;
			end
			
			`GENERAL_PURPOSE_REGISTERS_INDEX_2_SOURCE_DESTINATION: begin
				generalPurposeRegisterIndex2 <= currentInstructionDestinationRegisterIndex;
			end

			`GENERAL_PURPOSE_REGISTERS_INDEX_2_SOURCE_30: begin
				generalPurposeRegisterIndex2 <= 5'd30;
			end

			`GENERAL_PURPOSE_REGISTERS_INDEX_2_SOURCE_31: begin
				generalPurposeRegisterIndex2 <= 5'd31;
			end

			default: begin
				generalPurposeRegisterIndex2 <= 5'bxxxxx;
			end
			
		endcase
	end

	/**
	 * Data source MUX for port 2 of the general purpose registers.
	 */
	always @(*) begin
		case (generalPurposeRegistersData2Source)
		
			`GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_ALU: begin
				generalPurposeRegisterWriteData2 <= aluResult;
			end
			
			`GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_MULTIPLIER: begin
				generalPurposeRegisterWriteData2 <= multiplierResult;
			end

			`GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_DIVIDER_QUOTIENT: begin
				generalPurposeRegisterWriteData2 <= dividerQuotient;
			end

			`GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_DIVIDER_REMAINDER: begin
				generalPurposeRegisterWriteData2 <= dividerRemainder;
			end

			`GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_SHIFTER: begin
				generalPurposeRegisterWriteData2 <= shifterResult;
			end
			
			`GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_PC: begin
				generalPurposeRegisterWriteData2 <= programCounter;
			end
			
			`GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_EXTENDED_BUS_READ_DATA: begin
				generalPurposeRegisterWriteData2 <= extendedBusReadDataInput;
			end

			`GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_MVFS: begin
				generalPurposeRegisterWriteData2 <= mvfsValue;
			end
			
			`GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_EXCEPTION_RETURN_ADDRESS: begin
				generalPurposeRegisterWriteData2 <= exceptionReturnAddressRegister;
			end
			
			default: begin
				generalPurposeRegisterWriteData2 <= 32'bxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx;
			end
			
		endcase
	end
	assign busAddress = mmuPhysicalAddress;
		
	/**
	 * The general-purpose registers.
	 */
	GeneralPurposeRegisters generalPurposeRegisters (
		.clock(clock),
		.index1(currentInstructionLeftOperandRegisterIndex),
		.readData1(leftOperandRegisterValue),
		.index2(generalPurposeRegisterIndex2),
		.readData2(rightOperandRegisterValue),
		.writeData2(generalPurposeRegisterWriteData2),
		.writeEnable2(generalPurposeRegistersWriteEnable2ZeroRespecting)
	);
		
	/**
	 * Determine the actual operand values, which may come from immediate operands instead
	 * of the register values.
	 */
	assign leftOperandValue = leftOperandRegisterValue;
	assign rightOperandValue = currentInstructionRightOperandIsImmediate ? currentInstructionExtendedImmediateValue : rightOperandRegisterValue;
	
	/**
	 * The ALU.
	 */
	Alu alu (
		.operation(currentInstructionAluOperation),
		.leftOperand(leftOperandValue),
		.rightOperand(rightOperandValue),
		.signedComparison(currentInstructionSignedComparison),
		.comparisonOperation(currentInstructionComparisonOperation),
		.result(aluResult),
		.comparisonResult(aluComparisonResult)
	);
	
	/**
	 * The multiplier.
	 */
	Multiplier multiplier (
		.leftOperand(leftOperandValue),
		.rightOperand(rightOperandValue),
		.result(multiplierResult)
	);
	
	/**
	 * The divider.
	 */
	Divider divider (
		.clock(clock),
		.leftOperand(leftOperandValue),
		.rightOperand(rightOperandValue),
		.isSigned(dividerSigned),
		.activate(dividerActivate),
		.done(dividerDone),
		.quotient(dividerQuotient),
		.remainder(dividerRemainder),
		.divisionByZero(divisionByZero)
	);

	/**
	 * The shifter.
	 */
	Shifter shifter (
		.clock(clock),
		.operation(currentInstructionOpcode[2:1]),
		.leftOperand(leftOperandValue),
		.rightOperand(rightOperandValue[4:0]),
		.result(shifterResult)
	);

	/**
	 * Read the bus data synchronously.
	 */
	always @(posedge clock) begin
		rawBusReadDataInputRegister <= busReadData;
	end
	
	/**
	 * Extend the bus read data to 32 bits.
	 */
	always @(*) begin
		case (currentInstructionBusSize)
		
			2'b00: begin
				extendedBusReadDataInput = {((currentInstructionExtendMode == `EXTEND_MODE_SIGN) & rawBusReadDataInputRegister[7]) ? 24'hffffff : 24'h000000, rawBusReadDataInputRegister[7:0]};
			end
			
			2'b01: begin
				extendedBusReadDataInput = {((currentInstructionExtendMode == `EXTEND_MODE_SIGN) & rawBusReadDataInputRegister[15]) ? 16'hffff : 16'h0000, rawBusReadDataInputRegister[15:0]};
			end
			
			default: begin
				extendedBusReadDataInput = rawBusReadDataInputRegister;
			end
			
		endcase
	end
	
	/**
	 * The right operand for a store instruction is the immediate address offset, used for
	 * address computation. However, the right operand _register_ is the register at the index
	 * taken from location 2, which is the data register to store.
	 */
	assign busWriteData = rightOperandRegisterValue;

	/**
	 * The bus timeout counter
	 */
	BusTimeoutCounter busTimeoutCounter(
		.clock(clock),
		.clear(clearBusTimeoutCounter),
		.alarm(busTimeout)
	);
	
	/**
	 * The PSW register.
	 */
	ProcessorStatusWord processorStatusWord (
		.clock(clock),
		.reset(reset),
		.readValue(processorStatusWordValue),
		.writeEnable(processorStatusWordWriteEnable),
		.writeDataSource(pswWriteDataSource),
		.explicitWriteValue(rightOperandRegisterValue),
		.priorityWriteValue(detectedExceptionPriorityRegister)
	);
	assign userMode = processorStatusWordValue[26];

	/**
	 * The MMU.
	 */
	MemoryManagementUnit memoryManagementUnit (
		.clock(clock),
		.reset(reset),
		.virtualAddress(virtualAddress),
		.affectRandomIndexCounter(mmuAffectRandomIndexCounter),
		.tlbMiss(mmuTlbMiss),
		.physicalAddress(mmuPhysicalAddress),
		.physicalAddressWriteAllowed(mmuPhysicalAddressWriteAllowed),
		.physicalAddressValid(mmuPhysicalAddressValid),
		.randomizeEntryIndex(tlbEntryRandomizeIndex),
		.writeEntryEnable(tlbEntryWriteEnable),
		.specialRegisterReadIndex(mmuSpecialRegisterReadIndex),
		.specialRegisterReadValue(mmuSpecialRegisterReadValue),
		.specialRegisterExplicitWriteValue(rightOperandRegisterValue),
		.tlbIndexWriteEnable(tlbIndexWriteEnable),
		.tlbIndexDataSource(tlbIndexDataSource),
		.tlbEntryHighWriteEnable(tlbEntryHighWriteEnable),
		.tlbEntryHighDataSource(tlbEntryHighDataSource),
		.tlbEntryLowWriteEnable(tlbEntryLowWriteEnable),
		.tlbEntryLowDataSource(tlbEntryLowDataSource),
		.tlbBadAddressWriteEnable(tlbBadAddressWriteEnable),
		.tlbBadAddressDataSource(tlbBadAddressDataSource)
	);
	
	/**
	 * MVFS data MUX
	 */
	assign mvfsValue = ((currentInstructionExtendedImmediateValue[2:0] == 3'b000) ? processorStatusWordValue : mmuSpecialRegisterReadValue);


	/**
	 * Load the detectedExceptionPriorityRegister.
	 */
	always @(posedge clock) begin
		detectedExceptionPriorityRegister <= detectedExceptionPriority;
	end

	/**
	 * Interrupt detector
	 */
	InterruptDetector interruptDetector (
		.clock(clock),
		.externalInterruptLines(interrupts),
		.pswInterruptMask(processorStatusWordValue[15:0]),
		.pswInterruptEnable(processorStatusWordValue[23]),
		.interruptActive(interruptDetectorActive),
		.index(interruptDetectorIndex)
	);

	/************************************************************************************
	 * Combinatorial signals derived from the current state and from sub-module signals
	 ************************************************************************************/

	/**
	 * PC loading
	 */
	always @(*) begin
		case (state)

			/** at the beginning of each instruction, increment the PC by 4 **/
			`STATE_BEGIN_INSTRUCTION: begin
				programCounterWriteEnable <= 1'b1;
				programCounterWriteDataSource <= `PC_SOURCE_INCREMENT;
			end

			/** for a branch instruction, we addthe immediate value if the branch condition is true **/
			`STATE_COMPUTE_BRANCH: begin
				programCounterWriteEnable <= aluComparisonResult;
				programCounterWriteDataSource <= `PC_SOURCE_ADD_IMMEDIATE;
			end

			/** for a J or JAL instruction, we add the jump offset **/
			`STATE_PERFORM_J_JAL: begin
				programCounterWriteEnable <= 1'b1;
				programCounterWriteDataSource <= `PC_SOURCE_ADD_OFFSET;
			end

			/** for a JR or JALR instruction, we load the source register value **/
			`STATE_PERFORM_JR_JALR: begin
				programCounterWriteEnable <= 1'b1;
				programCounterWriteDataSource <= `PC_SOURCE_EXPLICIT;
			end

			/** for RFX, we load the value from register 30 **/
			`STATE_PERFORM_RFX: begin
				programCounterWriteEnable <= 1'b1;
				programCounterWriteDataSource <= `PC_SOURCE_EXPLICIT;
			end

			/** for exception handler entry, we load the handler base address **/
			`STATE_EXCEPTION: begin
				programCounterWriteEnable <= 1'b1;
				if ((detectedExceptionPriorityRegister == `EXCEPTION_CODE_TLB_MISS) & ~virtualAddress[31]) begin
					programCounterWriteDataSource <= `PC_SOURCE_USER_TLB_MISS;
				end else begin
					programCounterWriteDataSource <= `PC_SOURCE_EXCEPTION;
				end
			end
			
			/** in all other states we want the PC to keep its value **/
			default: begin
				programCounterWriteEnable <= 1'b0;
				programCounterWriteDataSource <= `PC_SOURCE_UNDEFINED;
			end

		endcase
	end
	
	/**
	 * The exception return address is loaded only in STATE_BEGIN_INSTRUCTION.
	 */
	always @(*) begin
		exceptionReturnAddressRegisterWriteEnable <= (state == `STATE_BEGIN_INSTRUCTION);
	end
	
	/**
	 * Virtual address laoding.
	 */
	always @(*) begin
		case (state)

			/** use the PC as the virtual address to fetch the next instruction **/
			`STATE_BEGIN_INSTRUCTION: begin
				virtualAddressRegisterWriteEnable <= 1'b1;
				virtualAddressRegisterWriteDataSource <= `VIRTUAL_ADDRESS_REGISTER_WRITE_DATA_SOURCE_PC;
			end

			/** use the address computation result as the virtual address to perform a load or store instruction **/
			`STATE_COMPUTE_LOAD_STORE_ADDRESS: begin
				virtualAddressRegisterWriteEnable <= 1'b1;
				virtualAddressRegisterWriteDataSource <= `VIRTUAL_ADDRESS_REGISTER_WRITE_DATA_SOURCE_ADDRESS_COMPUTATION_RESULT;
			end

			/** load the TLB entry high register into the virtual address register to perform a TBS instruction **/
			`STATE_TBS_LOAD_VIRTUAL_ADDRESS: begin
				virtualAddressRegisterWriteEnable <= 1'b1;
				virtualAddressRegisterWriteDataSource <= `VIRTUAL_ADDRESS_REGISTER_WRITE_DATA_SOURCE_MMU_SPECIAL_REGISTER_READ_DATA;
			end

			/**
			 * In all other states we want the virtual address register to keep its value, or just don't care about it.
			 * We might as well set it to 0 explicitly in a one-hot encoded state machine.
			 */
			default: begin
				virtualAddressRegisterWriteEnable <= 1'b0;
				virtualAddressRegisterWriteDataSource <= `VIRTUAL_ADDRESS_REGISTER_WRITE_DATA_SOURCE_UNDEFINED;
			end
			
		endcase
	end
	
	/**
	 * Instruction register loading
	 */
	always @(*) begin
		currentInstructionRegisterWriteEnable <= (state == `STATE_FETCH_INSTRUCTION);
	end
	
	/**
	 * Divider control
	 */
	always @(*) begin
		case (state)

			/** initialize the divider **/
			`STATE_INITIALIZE_DIVISION_REMAINDER_OPERATOR: begin
				dividerSigned <= ~currentInstructionOpcode[1];
				dividerActivate <= 1'b1;
			end

			/** keep going, but the dividerSigned signal is ignored after activation **/
			`STATE_CONTINUE_DIVISION_REMAINDER_OPERATOR: begin
				dividerSigned <= 1'bx;
				dividerActivate <= 1'b0;
			end
			
			/** in the other states we don't care about the divider **/
			default: begin
				dividerActivate <= 1'bx;
				dividerSigned <= 1'bx;
			end
			
		endcase
	end
	
	/**
	 * Bus access logic.
	 */
	always @(*) begin
		case (state)

			/** instruction fetching is always an LDW-type access **/
			`STATE_FETCH_INSTRUCTION: begin
				busEnable <= 1'b1;
				busWrite <= 1'b0;
				busSize <= `BUS_SIZE_WORD;
			end

			/** explicit load/store instructions may do anything **/
			`STATE_PERFORM_LOAD_STORE: begin
				busEnable <= 1'b1;
				busWrite <= currentInstructionLoadStoreIsStore;
				busSize <= currentInstructionBusSize;
			end
			
			/** by default, we leave the bus alone **/
			default: begin
				busEnable <= 1'b0;
				busWrite <= 1'bx;
				busSize <= 2'bxx;
			end

		endcase

	end
	
	/**
	 * Bus timeout counter control.
	 */
	always @(*) begin
		case (state)
		
			/** clear the bus timeout counter prior to fetching an instruction **/
			`STATE_INTERPRET_TRANSLATED_INSTRUCTION_ADDRESS: begin
				clearBusTimeoutCounter <= 1'b1;
			end
			
			/** let the counter count while fetching an instruction **/
			`STATE_FETCH_INSTRUCTION: begin
				clearBusTimeoutCounter <= 1'b0;
			end

			/** clear the bus timeout counter prior to performing a bus access instruction **/
			`STATE_INTERPRET_TRANSLATED_LOAD_STORE_ADDRESS: begin
				clearBusTimeoutCounter <= 1'b1;
			end

			/** let the counter count while performing a bus access instruction **/
			`STATE_PERFORM_LOAD_STORE: begin
				clearBusTimeoutCounter <= 1'b0;
			end
			
			/** in the other states we don't care about the counter **/
			default: begin
				clearBusTimeoutCounter <= 1'bx;
			end

		endcase
	end
	
	/** 
	 * Loading logic for the PSW.
	 */
	always @(*) begin
		case (state)
		
			`STATE_PERFORM_MVTS: begin
				processorStatusWordWriteEnable <= (currentInstructionExtendedImmediateValue[2:0] == 3'b000);
				pswWriteDataSource <= `PSW_WRITE_DATA_SOURCE_EXPLICIT;
			end

			`STATE_EXCEPTION: begin
				processorStatusWordWriteEnable <= 1'b1;
				pswWriteDataSource <= `PSW_WRITE_DATA_SOURCE_HANDLER_ENTRY;
			end
			
			`STATE_PERFORM_RFX: begin
				processorStatusWordWriteEnable <= 1'b1;
				pswWriteDataSource <= `PSW_WRITE_DATA_SOURCE_HANDLER_EXIT;
			end
			
			default: begin
				processorStatusWordWriteEnable <= 1'b0;
				pswWriteDataSource <= `PSW_WRITE_DATA_SOURCE_UNDEFINED;
			end
		
		endcase
	end

	/**
	 * MMU random counter control
	 */
	always @(*) begin
		case (state)

			/** advance the counter once for instruction fetching **/
			`STATE_TRANSLATE_INSTRUCTION_ADDRESS: begin
				mmuAffectRandomIndexCounter <= 1'b1;
			end
			
			/** advance the counter again for bus access instructions **/
			`STATE_TRANSLATE_LOAD_STORE_ADDRESS: begin
				mmuAffectRandomIndexCounter <= 1'b1;
			end

			/** in the other states we want the random counter to keep its value **/
			default: begin
				mmuAffectRandomIndexCounter <= 1'b0;
			end
			
		endcase
	end

	/**
	 * TLB special register control logic.
	 */
	always @(*) begin
		case (state)
		
			`STATE_PERFORM_MVTS: begin
			
				mmuSpecialRegisterReadIndex <= currentInstructionExtendedImmediateValue[2:0];
				
				tlbIndexWriteEnable <= (currentInstructionExtendedImmediateValue[2:0] == 3'b001);
				tlbEntryHighWriteEnable <= (currentInstructionExtendedImmediateValue[2:0] == 3'b010);
				tlbEntryLowWriteEnable <= (currentInstructionExtendedImmediateValue[2:0] == 3'b011);
				tlbBadAddressWriteEnable <= (currentInstructionExtendedImmediateValue[2:0] == 3'b100);

				tlbIndexDataSource <= `TLB_INDEX_DATA_SOURCE_EXPLICIT;
				tlbEntryHighDataSource <= `TLB_ENTRY_HIGH_DATA_SOURCE_EXPLICIT;
				tlbEntryLowDataSource <= `TLB_ENTRY_LOW_DATA_SOURCE_EXPLICIT;
				tlbBadAddressDataSource <= `TLB_BAD_ADDRESS_DATA_SOURCE_EXPLICIT;

			end

			`STATE_PERFORM_MVFS: begin
			
				mmuSpecialRegisterReadIndex <= currentInstructionExtendedImmediateValue[2:0];
				
				tlbIndexWriteEnable <= 1'b0;
				tlbEntryHighWriteEnable <= 1'b0;
				tlbEntryLowWriteEnable <= 1'b0;
				tlbBadAddressWriteEnable <= 1'b0;

				tlbIndexDataSource <= 1'bx;
				tlbEntryHighDataSource <= 2'bxx;
				tlbEntryLowDataSource <= 2'bxx;
				tlbBadAddressDataSource <= 1'bx;

			end
			
			`STATE_TBS_LOAD_VIRTUAL_ADDRESS: begin

				mmuSpecialRegisterReadIndex <= 3'd2;
				
				tlbIndexWriteEnable <= 1'b0;
				tlbEntryHighWriteEnable <= 1'b0;
				tlbEntryLowWriteEnable <= 1'b0;
				tlbBadAddressWriteEnable <= 1'b0;

				tlbIndexDataSource <= 1'bx;
				tlbEntryHighDataSource <= 2'bxx;
				tlbEntryLowDataSource <= 2'bxx;
				tlbBadAddressDataSource <= 1'bx;

			end
			
			`STATE_TBS_TRANSLATE_AND_STORE: begin

				mmuSpecialRegisterReadIndex <= 3'bxxx;
				
				tlbIndexWriteEnable <= 1'b1;
				tlbEntryHighWriteEnable <= 1'b0;
				tlbEntryLowWriteEnable <= 1'b0;
				tlbBadAddressWriteEnable <= 1'b0;

				tlbIndexDataSource <= `TLB_INDEX_DATA_SOURCE_SEARCH;
				tlbEntryHighDataSource <= 2'bxx;
				tlbEntryLowDataSource <= 2'bxx;
				tlbBadAddressDataSource <= 1'bx;

			end

			`STATE_TBRI_STORE_RESULT: begin

				mmuSpecialRegisterReadIndex <= 3'bxxx;
				
				tlbIndexWriteEnable <= 1'b0;
				tlbEntryHighWriteEnable <= 1'b1;
				tlbEntryLowWriteEnable <= 1'b1;
				tlbBadAddressWriteEnable <= 1'b0;

				tlbIndexDataSource <= 1'bx;
				tlbEntryHighDataSource <= `TLB_ENTRY_HIGH_DATA_SOURCE_READ_ENTRY;
				tlbEntryLowDataSource <= `TLB_ENTRY_LOW_DATA_SOURCE_READ_ENTRY;
				tlbBadAddressDataSource <= 1'bx;

			end
			
			`STATE_INTERPRET_TRANSLATED_INSTRUCTION_ADDRESS: begin

				mmuSpecialRegisterReadIndex <= 3'bxxx;
				
				tlbIndexWriteEnable <= ((nextState == `STATE_EXCEPTION) & (detectedExceptionPriority == `EXCEPTION_CODE_TLB_INVALID));
				tlbEntryHighWriteEnable <= ((nextState == `STATE_EXCEPTION) &
					((detectedExceptionPriority == `EXCEPTION_CODE_TLB_MISS) | (detectedExceptionPriority == `EXCEPTION_CODE_TLB_INVALID))
				);
				tlbEntryLowWriteEnable <= ((nextState == `STATE_EXCEPTION) & (detectedExceptionPriority == `EXCEPTION_CODE_TLB_INVALID));
				tlbBadAddressWriteEnable <= (nextState == `STATE_EXCEPTION);

				tlbIndexDataSource <= `TLB_INDEX_DATA_SOURCE_SEARCH;
				tlbEntryHighDataSource <= `TLB_ENTRY_HIGH_DATA_SOURCE_VIRTUAL_ADDRESS;
				tlbEntryLowDataSource <= `TLB_ENTRY_LOW_DATA_SOURCE_TLB_VALUE;
				tlbBadAddressDataSource <= `TLB_BAD_ADDRESS_DATA_SOURCE_VIRTUAL_ADDRESS;

			end
			
			`STATE_INTERPRET_TRANSLATED_LOAD_STORE_ADDRESS: begin

				mmuSpecialRegisterReadIndex <= 3'bxxx;
				
				tlbIndexWriteEnable <= ((nextState == `STATE_EXCEPTION) &
					((detectedExceptionPriority == `EXCEPTION_CODE_TLB_INVALID) | (detectedExceptionPriority == `EXCEPTION_CODE_TLB_WRITE))
				);
				tlbEntryHighWriteEnable <= ((nextState == `STATE_EXCEPTION) &
					((detectedExceptionPriority == `EXCEPTION_CODE_TLB_MISS) | (detectedExceptionPriority == `EXCEPTION_CODE_TLB_INVALID) | (detectedExceptionPriority == `EXCEPTION_CODE_TLB_WRITE))
				);
				tlbEntryLowWriteEnable <= ((nextState == `STATE_EXCEPTION) &
					((detectedExceptionPriority == `EXCEPTION_CODE_TLB_INVALID) | (detectedExceptionPriority == `EXCEPTION_CODE_TLB_WRITE))
				);
				tlbBadAddressWriteEnable <= (nextState == `STATE_EXCEPTION);

				tlbIndexDataSource <= `TLB_INDEX_DATA_SOURCE_SEARCH;
				tlbEntryHighDataSource <= `TLB_ENTRY_HIGH_DATA_SOURCE_VIRTUAL_ADDRESS;
				tlbEntryLowDataSource <= `TLB_ENTRY_LOW_DATA_SOURCE_TLB_VALUE;
				tlbBadAddressDataSource <= `TLB_BAD_ADDRESS_DATA_SOURCE_VIRTUAL_ADDRESS;

			end
			
			default: begin

				mmuSpecialRegisterReadIndex <= 3'bxxx;
				
				tlbIndexWriteEnable <= 1'b0;
				tlbEntryHighWriteEnable <= 1'b0;
				tlbEntryLowWriteEnable <= 1'b0;
				tlbBadAddressWriteEnable <= 1'b0;

				tlbIndexDataSource <= 1'bx;
				tlbEntryHighDataSource <= 2'bxx;
				tlbEntryLowDataSource <= 2'bxx;
				tlbBadAddressDataSource <= 1'bx;

			end
		
		endcase
	end
	
	/**
	 * TLB entry access logic 
	 */
	always @(*) begin
		case (state)
		
			`STATE_PERFORM_TLB_WRITE: begin
				tlbEntryWriteEnable <= 1'b1;
				tlbEntryRandomizeIndex <= currentInstructionOpcode[1];
			end
			
			`STATE_TBRI_READ_ENTRY: begin
				tlbEntryWriteEnable <= 1'b0;
				tlbEntryRandomizeIndex <= 1'b0;
			end

			`STATE_TBRI_STORE_RESULT: begin
				tlbEntryWriteEnable <= 1'b0;
				tlbEntryRandomizeIndex <= 1'b0;
			end
			
			default: begin
				tlbEntryWriteEnable <= 1'b0;
				tlbEntryRandomizeIndex <= 1'bx;
			end
			
		endcase
	end

	/**
	 * Next-state and output logic of the main state machine.
	 */
	always @(*) begin
		case (state)

			`STATE_RESET: begin
				generalPurposeRegistersWriteEnable2 <= 1'b0;
				generalPurposeRegistersIndex2Source <= `GENERAL_PURPOSE_REGISTERS_INDEX_2_SOURCE_UNDEFINED;
				generalPurposeRegistersData2Source <= `GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_UNDEFINED;
				nextState <= `STATE_BEGIN_INSTRUCTION;
				detectedExceptionPriority <= 5'bxxxxx;
			end

			`STATE_BEGIN_INSTRUCTION: begin
				generalPurposeRegistersWriteEnable2 <= 1'b0;
				generalPurposeRegistersIndex2Source <= `GENERAL_PURPOSE_REGISTERS_INDEX_2_SOURCE_UNDEFINED;
				generalPurposeRegistersData2Source <= `GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_UNDEFINED;
				nextState <= interruptDetectorActive ? `STATE_EXCEPTION : `STATE_TRANSLATE_INSTRUCTION_ADDRESS;
				detectedExceptionPriority <= {1'b0, interruptDetectorIndex};
			end

			`STATE_TRANSLATE_INSTRUCTION_ADDRESS: begin
				generalPurposeRegistersWriteEnable2 <= 1'b0;
				generalPurposeRegistersIndex2Source <= `GENERAL_PURPOSE_REGISTERS_INDEX_2_SOURCE_UNDEFINED;
				generalPurposeRegistersData2Source <= `GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_UNDEFINED;
				nextState <= `STATE_INTERPRET_TRANSLATED_INSTRUCTION_ADDRESS;
				detectedExceptionPriority <= 5'bxxxxx;
			end

			`STATE_INTERPRET_TRANSLATED_INSTRUCTION_ADDRESS: begin
				generalPurposeRegistersWriteEnable2 <= 1'b0;
				generalPurposeRegistersIndex2Source <= `GENERAL_PURPOSE_REGISTERS_INDEX_2_SOURCE_UNDEFINED;
				generalPurposeRegistersData2Source <= `GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_UNDEFINED;
				
				if (virtualAddress[0] | virtualAddress[1]) begin
					nextState <= `STATE_EXCEPTION;
					detectedExceptionPriority <= `EXCEPTION_CODE_ILLEGAL_ADDRESS;
				end else if (virtualAddress[31] & userMode) begin
					nextState <= `STATE_EXCEPTION;
					detectedExceptionPriority <= `EXCEPTION_CODE_PRIVILEGED_ADDRESS;
				end else if (mmuTlbMiss) begin
					nextState <= `STATE_EXCEPTION;
					detectedExceptionPriority <= `EXCEPTION_CODE_TLB_MISS;
				end else if (~mmuPhysicalAddressValid) begin
					nextState <= `STATE_EXCEPTION;
					detectedExceptionPriority <= `EXCEPTION_CODE_TLB_INVALID;
				end else begin
					nextState <= `STATE_FETCH_INSTRUCTION;
					detectedExceptionPriority <= 5'bxxxxx;
				end
			end

			`STATE_FETCH_INSTRUCTION: begin
				generalPurposeRegistersWriteEnable2 <= 1'b0;
				generalPurposeRegistersIndex2Source <= `GENERAL_PURPOSE_REGISTERS_INDEX_2_SOURCE_UNDEFINED;
				generalPurposeRegistersData2Source <= `GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_UNDEFINED;
				nextState <= busWait ? (busTimeout ? `STATE_EXCEPTION : `STATE_FETCH_INSTRUCTION) : `STATE_DECODE_INSTRUCTION;
				detectedExceptionPriority <= `EXCEPTION_CODE_BUS_TIMEOUT;
			end

			`STATE_DECODE_INSTRUCTION: begin
				generalPurposeRegistersWriteEnable2 <= 1'b0;
				generalPurposeRegistersIndex2Source <= `GENERAL_PURPOSE_REGISTERS_INDEX_2_SOURCE_RIGHT_OPERAND;
				generalPurposeRegistersData2Source <= `GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_UNDEFINED;

				/**
				 * At the same clock edge at which these statements are executed,
				 * the leftOperandValue and rightOperandValue signals are made
				 * valid. The fast computation instructions therefore just need
				 * to branch to a state that stores the correct result at the
				 * next clock edge.
				 */
				casez (currentInstructionOpcode)

					/** unused opcodes **/
					6'h1e, 6'h3e, 6'h3f: begin
						detectedExceptionPriority <= `EXCEPTION_CODE_ILLEGAL_INSTRUCTION;
						nextState <= `STATE_EXCEPTION;
					end

					/** add, sub; and, or, xor, xnor; ldhi **/
					6'b0000zz, 6'b010zzz, 6'b011111: begin
						detectedExceptionPriority <= 5'bxxxxx;
						nextState <= `STATE_COMPUTE_ALU_OPERATOR;
					end

					/** mul, mulu **/
					6'b0001zz: begin
						detectedExceptionPriority <= 5'bxxxxx;
						nextState <= `STATE_COMPUTE_MULTIPLICATION_OPERATOR;
					end

					/** div, divu, rem, remu **/
					6'b001zzz: begin
						detectedExceptionPriority <= 5'bxxxxx;
						nextState <= `STATE_INITIALIZE_DIVISION_REMAINDER_OPERATOR;
					end

					/** sll, slr; sar **/
					6'b0110zz, 6'b01110z: begin
						detectedExceptionPriority <= 5'bxxxxx;
						nextState <= `STATE_COMPUTE_SHIFT_OPERATOR;
					end

					6'b100zzz, 6'b10100z: begin
						detectedExceptionPriority <= 5'bxxxxx;
						nextState <= `STATE_COMPUTE_BRANCH;
					end
					
					6'b101010, 6'b101100: begin
						detectedExceptionPriority <= 5'bxxxxx;
						nextState <= `STATE_PERFORM_J_JAL;
					end
					
					6'b101011, 6'b101101: begin
						detectedExceptionPriority <= 5'bxxxxx;
						nextState <= `STATE_PERFORM_JR_JALR;
					end
					
					6'b101110: begin
						detectedExceptionPriority <= `EXCEPTION_CODE_TRAP;
						nextState <= `STATE_EXCEPTION;
					end
					
					6'b101111: begin
						if (userMode) begin
							detectedExceptionPriority <= `EXCEPTION_CODE_PRIVILEGED_INSTRUCTION;
							nextState <= `STATE_EXCEPTION;
						end else begin
							detectedExceptionPriority <= 5'bxxxxx;
							nextState <= `STATE_RFX_LOAD_RETURN_ADDRESS;
						end
					end

					6'b110zzz: begin
						detectedExceptionPriority <= 5'bxxxxx;
						nextState <= `STATE_COMPUTE_LOAD_STORE_ADDRESS;
					end

					6'b111000: begin
						if (userMode) begin
							detectedExceptionPriority <= `EXCEPTION_CODE_PRIVILEGED_INSTRUCTION;
							nextState <= `STATE_EXCEPTION;
						end else if (currentInstructionExtendedImmediateValue[15:3] != 13'd0) begin
							detectedExceptionPriority <= `EXCEPTION_CODE_ILLEGAL_INSTRUCTION;
							nextState <= `STATE_EXCEPTION;
						end else if (currentInstructionExtendedImmediateValue[2] & (currentInstructionExtendedImmediateValue[1] | currentInstructionExtendedImmediateValue[0])) begin
							detectedExceptionPriority <= `EXCEPTION_CODE_ILLEGAL_INSTRUCTION;
							nextState <= `STATE_EXCEPTION;
						end else begin
							detectedExceptionPriority <= 5'bxxxxx;
							nextState <= `STATE_PERFORM_MVFS;
						end
					end

					6'b111001: begin
						if (userMode) begin
							detectedExceptionPriority <= `EXCEPTION_CODE_PRIVILEGED_INSTRUCTION;
							nextState <= `STATE_EXCEPTION;
						end else if (currentInstructionExtendedImmediateValue[15:3] != 13'd0) begin
							detectedExceptionPriority <= `EXCEPTION_CODE_ILLEGAL_INSTRUCTION;
							nextState <= `STATE_EXCEPTION;
						end else if (currentInstructionExtendedImmediateValue[2] & (currentInstructionExtendedImmediateValue[1] | currentInstructionExtendedImmediateValue[0])) begin
							detectedExceptionPriority <= `EXCEPTION_CODE_ILLEGAL_INSTRUCTION;
							nextState <= `STATE_EXCEPTION;
						end else begin
							detectedExceptionPriority <= 5'bxxxxx;
							nextState <= `STATE_PERFORM_MVTS;
						end
					end

					6'b111010: begin
						if (userMode) begin
							detectedExceptionPriority <= `EXCEPTION_CODE_PRIVILEGED_INSTRUCTION;
							nextState <= `STATE_EXCEPTION;
						end else begin
							detectedExceptionPriority <= 5'bxxxxx;
							nextState <= `STATE_TBS_LOAD_VIRTUAL_ADDRESS;
						end
					end

					6'b111011: begin
						if (userMode) begin
							detectedExceptionPriority <= `EXCEPTION_CODE_PRIVILEGED_INSTRUCTION;
							nextState <= `STATE_EXCEPTION;
						end else begin
							detectedExceptionPriority <= 5'bxxxxx;
							nextState <= `STATE_PERFORM_TLB_WRITE;
						end
					end

					6'b111100: begin
						if (userMode) begin
							detectedExceptionPriority <= `EXCEPTION_CODE_PRIVILEGED_INSTRUCTION;
							nextState <= `STATE_EXCEPTION;
						end else begin
							detectedExceptionPriority <= 5'bxxxxx;
							nextState <= `STATE_TBRI_READ_ENTRY;
						end
					end

					6'b111101: begin
						if (userMode) begin
							detectedExceptionPriority <= `EXCEPTION_CODE_PRIVILEGED_INSTRUCTION;
							nextState <= `STATE_EXCEPTION;
						end else begin
							detectedExceptionPriority <= 5'bxxxxx;
							nextState <= `STATE_PERFORM_TLB_WRITE;
						end
					end

					default: begin
						detectedExceptionPriority <= 5'bxxxxx;
						nextState <= `STATE_EXCEPTION;
					end
					
				endcase
			end

			`STATE_COMPUTE_ALU_OPERATOR: begin
				generalPurposeRegistersWriteEnable2 <= 1'b1;
				generalPurposeRegistersIndex2Source <= `GENERAL_PURPOSE_REGISTERS_INDEX_2_SOURCE_DESTINATION;
				generalPurposeRegistersData2Source <= `GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_ALU;
				nextState <= `STATE_BEGIN_INSTRUCTION;
				detectedExceptionPriority <= 5'bxxxxx;
			end

			`STATE_COMPUTE_SHIFT_OPERATOR: begin
				generalPurposeRegistersWriteEnable2 <= 1'b0;
				generalPurposeRegistersIndex2Source <= `GENERAL_PURPOSE_REGISTERS_INDEX_2_SOURCE_RIGHT_OPERAND;
				generalPurposeRegistersData2Source <= `GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_UNDEFINED;
				nextState <= `STATE_COMPUTE_SHIFT_OPERATOR_CONTINUED;
				detectedExceptionPriority <= 5'bxxxxx;
			end

			`STATE_COMPUTE_SHIFT_OPERATOR_CONTINUED: begin
				generalPurposeRegistersWriteEnable2 <= 1'b1;
				generalPurposeRegistersIndex2Source <= `GENERAL_PURPOSE_REGISTERS_INDEX_2_SOURCE_DESTINATION;
				generalPurposeRegistersData2Source <= `GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_SHIFTER;
				nextState <= `STATE_BEGIN_INSTRUCTION;
				detectedExceptionPriority <= 5'bxxxxx;
			end

			`STATE_COMPUTE_MULTIPLICATION_OPERATOR: begin
				generalPurposeRegistersWriteEnable2 <= 1'b1;
				generalPurposeRegistersIndex2Source <= `GENERAL_PURPOSE_REGISTERS_INDEX_2_SOURCE_DESTINATION;
				generalPurposeRegistersData2Source <= `GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_MULTIPLIER;
				nextState <= `STATE_BEGIN_INSTRUCTION;
				detectedExceptionPriority <= 5'bxxxxx;
			end

			`STATE_INITIALIZE_DIVISION_REMAINDER_OPERATOR: begin
				generalPurposeRegistersWriteEnable2 <= 1'b0;
				generalPurposeRegistersIndex2Source <= `GENERAL_PURPOSE_REGISTERS_INDEX_2_SOURCE_RIGHT_OPERAND;
				generalPurposeRegistersData2Source <= `GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_UNDEFINED;
				nextState <= `STATE_CONTINUE_DIVISION_REMAINDER_OPERATOR;
				detectedExceptionPriority <= 5'bxxxxx;
			end

			`STATE_CONTINUE_DIVISION_REMAINDER_OPERATOR: begin
				/**
				 * We may only write the result to the destination register if we don't have a division
				 * by zero.
				 */
				generalPurposeRegistersWriteEnable2 <= dividerDone & ~divisionByZero;
				generalPurposeRegistersIndex2Source <= `GENERAL_PURPOSE_REGISTERS_INDEX_2_SOURCE_DESTINATION;
				generalPurposeRegistersData2Source <= currentInstructionOpcode[2] ? `GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_DIVIDER_REMAINDER : `GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_DIVIDER_QUOTIENT;
				nextState <= dividerDone ? (divisionByZero ? `STATE_EXCEPTION : `STATE_BEGIN_INSTRUCTION) : `STATE_CONTINUE_DIVISION_REMAINDER_OPERATOR;
				detectedExceptionPriority <= `EXCEPTION_CODE_DIVISION;
			end

			`STATE_COMPUTE_BRANCH: begin
				generalPurposeRegistersWriteEnable2 <= 1'b0;
				generalPurposeRegistersIndex2Source <= `GENERAL_PURPOSE_REGISTERS_INDEX_2_SOURCE_RIGHT_OPERAND;
				generalPurposeRegistersData2Source <= `GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_UNDEFINED;
				nextState <= `STATE_BEGIN_INSTRUCTION;
				detectedExceptionPriority <= 5'bxxxxx;
			end

			`STATE_COMPUTE_LOAD_STORE_ADDRESS: begin
				generalPurposeRegistersWriteEnable2 <= 1'b0;
				generalPurposeRegistersIndex2Source <= `GENERAL_PURPOSE_REGISTERS_INDEX_2_SOURCE_RIGHT_OPERAND;
				generalPurposeRegistersData2Source <= `GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_UNDEFINED;
				nextState <= `STATE_TRANSLATE_LOAD_STORE_ADDRESS;
				detectedExceptionPriority <= 5'bxxxxx;
			end

			`STATE_TRANSLATE_LOAD_STORE_ADDRESS: begin
				generalPurposeRegistersWriteEnable2 <= 1'b0;
				generalPurposeRegistersIndex2Source <= `GENERAL_PURPOSE_REGISTERS_INDEX_2_SOURCE_RIGHT_OPERAND;
				generalPurposeRegistersData2Source <= `GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_UNDEFINED;
				nextState <= `STATE_INTERPRET_TRANSLATED_LOAD_STORE_ADDRESS;
				detectedExceptionPriority <= 5'bxxxxx;
			end

			`STATE_INTERPRET_TRANSLATED_LOAD_STORE_ADDRESS: begin
				generalPurposeRegistersWriteEnable2 <= 1'b0;
				generalPurposeRegistersIndex2Source <= `GENERAL_PURPOSE_REGISTERS_INDEX_2_SOURCE_RIGHT_OPERAND;
				generalPurposeRegistersData2Source <= `GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_UNDEFINED;
				
				if ((virtualAddress[0] & (currentInstructionBusSize != `BUS_SIZE_BYTE)) | (virtualAddress[1] & (currentInstructionBusSize == `BUS_SIZE_WORD))) begin
					detectedExceptionPriority <= `EXCEPTION_CODE_ILLEGAL_ADDRESS;
					nextState <= `STATE_EXCEPTION;
				end else if (virtualAddress[31] & userMode) begin
					detectedExceptionPriority <= `EXCEPTION_CODE_PRIVILEGED_ADDRESS;
					nextState <= `STATE_EXCEPTION;
				end else if (mmuTlbMiss) begin
					detectedExceptionPriority <= `EXCEPTION_CODE_TLB_MISS;
					nextState <= `STATE_EXCEPTION;
				end else if (~mmuPhysicalAddressValid) begin
					detectedExceptionPriority <= `EXCEPTION_CODE_TLB_INVALID;
					nextState <= `STATE_EXCEPTION;
				end else if (~mmuPhysicalAddressWriteAllowed & currentInstructionLoadStoreIsStore) begin
					detectedExceptionPriority <= `EXCEPTION_CODE_TLB_WRITE;
					nextState <= `STATE_EXCEPTION;
				end else begin
					nextState <= `STATE_PERFORM_LOAD_STORE;
					detectedExceptionPriority <= 5'bxxxxx;
				end
			end

			`STATE_PERFORM_LOAD_STORE: begin

				generalPurposeRegistersWriteEnable2 <= 1'b0;
				generalPurposeRegistersIndex2Source <= `GENERAL_PURPOSE_REGISTERS_INDEX_2_SOURCE_RIGHT_OPERAND;
				generalPurposeRegistersData2Source <= `GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_UNDEFINED;
				detectedExceptionPriority <= `EXCEPTION_CODE_BUS_TIMEOUT;

				if (busWait) begin
					nextState <= (busTimeout ? `STATE_EXCEPTION : `STATE_PERFORM_LOAD_STORE);
				end else if (currentInstructionLoadStoreIsStore) begin
					nextState <= `STATE_BEGIN_INSTRUCTION;
				end else begin
					nextState <= `STATE_FINISH_LOAD;
				end

			end

			`STATE_FINISH_LOAD: begin
				generalPurposeRegistersWriteEnable2 <= 1'b1;
				generalPurposeRegistersIndex2Source <= `GENERAL_PURPOSE_REGISTERS_INDEX_2_SOURCE_DESTINATION;
				generalPurposeRegistersData2Source <= `GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_EXTENDED_BUS_READ_DATA;
				nextState <= `STATE_BEGIN_INSTRUCTION;
				detectedExceptionPriority <= 5'bxxxxx;
			end

			`STATE_PERFORM_J_JAL: begin
				generalPurposeRegistersWriteEnable2 <= (~currentInstructionOpcode[1]);
				generalPurposeRegistersIndex2Source <= `GENERAL_PURPOSE_REGISTERS_INDEX_2_SOURCE_31;
				generalPurposeRegistersData2Source <= `GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_PC;
				nextState <= `STATE_BEGIN_INSTRUCTION;
				detectedExceptionPriority <= 5'bxxxxx;
			end

			`STATE_PERFORM_JR_JALR: begin
				generalPurposeRegistersWriteEnable2 <= (~currentInstructionOpcode[1]);
				generalPurposeRegistersIndex2Source <= `GENERAL_PURPOSE_REGISTERS_INDEX_2_SOURCE_31;
				generalPurposeRegistersData2Source <= `GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_PC;
				nextState <= `STATE_BEGIN_INSTRUCTION;
				detectedExceptionPriority <= 5'bxxxxx;
			end

			`STATE_RFX_LOAD_RETURN_ADDRESS: begin
				generalPurposeRegistersWriteEnable2 <= 1'b0;
				generalPurposeRegistersIndex2Source <= `GENERAL_PURPOSE_REGISTERS_INDEX_2_SOURCE_30;
				generalPurposeRegistersData2Source <= `GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_UNDEFINED;
				nextState <= `STATE_PERFORM_RFX;
				detectedExceptionPriority <= 5'bxxxxx;
			end
			
			`STATE_PERFORM_RFX: begin
				generalPurposeRegistersWriteEnable2 <= 1'b0;
				generalPurposeRegistersIndex2Source <= `GENERAL_PURPOSE_REGISTERS_INDEX_2_SOURCE_RIGHT_OPERAND;
				generalPurposeRegistersData2Source <= `GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_UNDEFINED;
				nextState <= `STATE_DELAY_NEXT_INSTRUCTION;
				detectedExceptionPriority <= 5'bxxxxx;
			end

			`STATE_PERFORM_MVFS: begin
				generalPurposeRegistersWriteEnable2 <= 1'b1;
				generalPurposeRegistersIndex2Source <= `GENERAL_PURPOSE_REGISTERS_INDEX_2_SOURCE_DESTINATION;
				generalPurposeRegistersData2Source <= `GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_MVFS;
				nextState <= `STATE_BEGIN_INSTRUCTION;
				detectedExceptionPriority <= 5'bxxxxx;
			end

			`STATE_PERFORM_MVTS: begin
				generalPurposeRegistersWriteEnable2 <= 1'b0;
				generalPurposeRegistersIndex2Source <= `GENERAL_PURPOSE_REGISTERS_INDEX_2_SOURCE_UNDEFINED;
				generalPurposeRegistersData2Source <= `GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_UNDEFINED;
				nextState <= `STATE_DELAY_NEXT_INSTRUCTION;
				detectedExceptionPriority <= 5'bxxxxx;
			end

			`STATE_TBS_LOAD_VIRTUAL_ADDRESS: begin
				generalPurposeRegistersWriteEnable2 <= 1'b0;
				generalPurposeRegistersIndex2Source <= `GENERAL_PURPOSE_REGISTERS_INDEX_2_SOURCE_UNDEFINED;
				generalPurposeRegistersData2Source <= `GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_UNDEFINED;
				nextState <= `STATE_TBS_TRANSLATE_AND_STORE;
				detectedExceptionPriority <= 5'bxxxxx;
			end

			`STATE_TBS_TRANSLATE_AND_STORE: begin
				generalPurposeRegistersWriteEnable2 <= 1'b0;
				generalPurposeRegistersIndex2Source <= `GENERAL_PURPOSE_REGISTERS_INDEX_2_SOURCE_UNDEFINED;
				generalPurposeRegistersData2Source <= `GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_UNDEFINED;
				nextState <= `STATE_BEGIN_INSTRUCTION;
				detectedExceptionPriority <= 5'bxxxxx;
			end

			`STATE_PERFORM_TLB_WRITE : begin
				generalPurposeRegistersWriteEnable2 <= 1'b0;
				generalPurposeRegistersIndex2Source <= `GENERAL_PURPOSE_REGISTERS_INDEX_2_SOURCE_UNDEFINED;
				generalPurposeRegistersData2Source <= `GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_UNDEFINED;
				nextState <= `STATE_BEGIN_INSTRUCTION;
				detectedExceptionPriority <= 5'bxxxxx;
			end

			`STATE_TBRI_READ_ENTRY: begin
				generalPurposeRegistersWriteEnable2 <= 1'b0;
				generalPurposeRegistersIndex2Source <= `GENERAL_PURPOSE_REGISTERS_INDEX_2_SOURCE_UNDEFINED;
				generalPurposeRegistersData2Source <= `GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_UNDEFINED;
				nextState <= `STATE_TBRI_STORE_RESULT;
				detectedExceptionPriority <= 5'bxxxxx;
			end

			`STATE_TBRI_STORE_RESULT: begin
				generalPurposeRegistersWriteEnable2 <= 1'b0;
				generalPurposeRegistersIndex2Source <= `GENERAL_PURPOSE_REGISTERS_INDEX_2_SOURCE_UNDEFINED;
				generalPurposeRegistersData2Source <= `GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_UNDEFINED;
				nextState <= `STATE_BEGIN_INSTRUCTION;
				detectedExceptionPriority <= 5'bxxxxx;
			end

			`STATE_EXCEPTION: begin
				/**
				 * Handler entry: we store the return address in register 30,
				 * the handler entry point in the PC, and transform the PSW.
				 */
				generalPurposeRegistersWriteEnable2 <= 1'b1;
				generalPurposeRegistersIndex2Source <= `GENERAL_PURPOSE_REGISTERS_INDEX_2_SOURCE_30;
				generalPurposeRegistersData2Source <= `GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_EXCEPTION_RETURN_ADDRESS;
				nextState <= `STATE_DELAY_NEXT_INSTRUCTION;
				detectedExceptionPriority <= 5'bxxxxx;
			end
			
			`STATE_DELAY_NEXT_INSTRUCTION: begin
				generalPurposeRegistersWriteEnable2 <= 1'b0;
				generalPurposeRegistersIndex2Source <= `GENERAL_PURPOSE_REGISTERS_INDEX_2_SOURCE_UNDEFINED;
				generalPurposeRegistersData2Source <= `GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_UNDEFINED;
				nextState <= `STATE_BEGIN_INSTRUCTION;
				detectedExceptionPriority <= 5'bxxxxx;
			end
			
			default: begin
				generalPurposeRegistersWriteEnable2 <= 1'bx;
				generalPurposeRegistersIndex2Source <= `GENERAL_PURPOSE_REGISTERS_INDEX_2_SOURCE_UNDEFINED;
				generalPurposeRegistersData2Source <= `GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_UNDEFINED;
				nextState <= `STATE_UNDEFINED;
				detectedExceptionPriority <= 5'bxxxxx;
			end

		endcase

	end

	/**
	 * The main state machine.
	 */
	always @(posedge clock) begin
		if (reset) begin
			state <= `STATE_RESET;
		end else begin
			state <= nextState;
		end
	end
	
endmodule
