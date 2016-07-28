`default_nettype none
`timescale 1ns / 1ps

/**
 * Testbench for the instruction decoder of the CPU.
 */
module InstructionDecoderTest ();

	/** includes **/
	`include "VUnit.inc.v"
	`include "Clock.inc.v"

	/** include constant definitions **/
	`include "../../eco32/src/cpu/CpuConstants.inc.v"
	`include "../../eco32/src/cpu/AluConstants.inc.v"
	
	/** the module under test **/
	reg[31:0] currentInstructionRegister;
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
	InstructionDecoder instructionDecoder (
		.currentInstruction(currentInstructionRegister),
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
	wire[4:0] currentInstructionRegisterIndex1 = instructionDecoder.currentInstructionRegisterIndex1;
	wire[4:0] currentInstructionRegisterIndex2 = instructionDecoder.currentInstructionRegisterIndex2;
	wire[4:0] currentInstructionRegisterIndex3 = instructionDecoder.currentInstructionRegisterIndex3;
	
	/** simulation **/
	initial begin
		$dumpfile("cpu/InstructionDecoderTest.vcd");
		$dumpvars;

		/** test ADD **/
		assertionContext <= "test ADD";
		currentInstructionRegister <= 32'b000000_00001_00010_00011_00000000000;
		#1;
		assert(currentInstructionOpcode == 0, "opcode");
		assert(currentInstructionRegisterIndex1 == 1, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 2, "operand index 2");
		assert(currentInstructionRegisterIndex3 == 3, "operand index 3");
		assert(~currentInstructionRightOperandIsImmediate, "is immediate");
		assert(instructionDecoder.currentInstructionDestinationRegisterIndexIsRegisterIndex3, "dest is 3");
		assert(currentInstructionLeftOperandRegisterIndex == 1, "left operand index");
		assert(currentInstructionRightOperandRegisterIndex == 2, "right operand index");
		assert(currentInstructionDestinationRegisterIndex == 3, "destination operand index");
		assert(currentInstructionAluOperation == `ALU_OPERATION_ADD, "ALU operation");

		/** test ADDI **/
		assertionContext <= "test ADDI";
		currentInstructionRegister <= 32'b000001_00001_00010_0000000000000000;
		#1;
		assert(currentInstructionOpcode == 1, "opcode");
		assert(currentInstructionRegisterIndex1 == 1, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 2, "operand index 2");
		assert(instructionDecoder.currentInstructionRawImmediateValue == 0, "immediate operand");
		assert(currentInstructionRightOperandIsImmediate, "is immediate");
		assert(instructionDecoder.currentInstructionImmediateValueIsSigned, "immediate is signed");
		assert(~instructionDecoder.currentInstructionDestinationRegisterIndexIsRegisterIndex3, "dest is 3");
		assert(currentInstructionLeftOperandRegisterIndex == 1, "left operand index");
		assert(currentInstructionDestinationRegisterIndex == 2, "destination operand index");
		assert(currentInstructionExtendedImmediateValue == 0, "extended immediate value");
		assert(currentInstructionAluOperation == `ALU_OPERATION_ADD, "ALU operation");
		
		/** test SUB with more interesting register numbers **/
		assertionContext <= "test SUB";
		currentInstructionRegister <= 32'b000010_10101_01010_00111_00000000000;
		#1;
		assert(currentInstructionOpcode == 2, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(currentInstructionRegisterIndex3 == 7, "operand index 3");
		assert(~currentInstructionRightOperandIsImmediate, "is immediate");
		assert(instructionDecoder.currentInstructionDestinationRegisterIndexIsRegisterIndex3, "dest is 3");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionRightOperandRegisterIndex == 10, "right operand index");
		assert(currentInstructionDestinationRegisterIndex == 7, "destination operand index");
		assert(currentInstructionAluOperation == `ALU_OPERATION_SUB, "ALU operation");

		/** test SUBI **/
		assertionContext <= "test SUBI";
		currentInstructionRegister <= 32'b000011_10101_01010_0000000000001111;
		#1;
		assert(currentInstructionOpcode == 3, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(instructionDecoder.currentInstructionRawImmediateValue == 15, "immediate operand");
		assert(currentInstructionRightOperandIsImmediate, "is immediate");
		assert(instructionDecoder.currentInstructionImmediateValueIsSigned, "immediate is signed");
		assert(~instructionDecoder.currentInstructionDestinationRegisterIndexIsRegisterIndex3, "dest is 3");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionDestinationRegisterIndex == 10, "destination operand index");
		assert(currentInstructionExtendedImmediateValue == 15, "extended immediate value");
		assert(currentInstructionAluOperation == `ALU_OPERATION_SUB, "ALU operation");

		/** test SUBI with negative immediate operand **/
		assertionContext <= "test SUBI (negative)";
		currentInstructionRegister <= 32'b000011_10101_01010_1111111111110000;
		#1;
		assert(currentInstructionOpcode == 3, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(instructionDecoder.currentInstructionRawImmediateValue == -16, "immediate operand");
		assert(currentInstructionRightOperandIsImmediate, "is immediate");
		assert(instructionDecoder.currentInstructionImmediateValueIsSigned, "immediate is signed");
		assert(~instructionDecoder.currentInstructionDestinationRegisterIndexIsRegisterIndex3, "dest is 3");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionDestinationRegisterIndex == 10, "destination operand index");
		assert(currentInstructionExtendedImmediateValue == -16, "extended immediate value");
		assert(currentInstructionAluOperation == `ALU_OPERATION_SUB, "ALU operation");

		/** test MUL **/
		assertionContext <= "test MUL";
		currentInstructionRegister <= 32'b000100_10101_01010_00111_00000000000;
		#1;
		assert(currentInstructionOpcode == 4, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(currentInstructionRegisterIndex3 == 7, "operand index 3");
		assert(~currentInstructionRightOperandIsImmediate, "is immediate");
		assert(instructionDecoder.currentInstructionDestinationRegisterIndexIsRegisterIndex3, "dest is 3");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionRightOperandRegisterIndex == 10, "right operand index");
		assert(currentInstructionDestinationRegisterIndex == 7, "destination operand index");

		/** test MULI **/
		assertionContext <= "test MULI";
		currentInstructionRegister <= 32'b000101_10101_01010_0000000000001111;
		#1;
		assert(currentInstructionOpcode == 5, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(instructionDecoder.currentInstructionRawImmediateValue == 15, "immediate operand");
		assert(currentInstructionRightOperandIsImmediate, "is immediate");
		assert(instructionDecoder.currentInstructionImmediateValueIsSigned, "immediate is signed");
		assert(~instructionDecoder.currentInstructionDestinationRegisterIndexIsRegisterIndex3, "dest is 3");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionDestinationRegisterIndex == 10, "destination operand index");
		assert(currentInstructionExtendedImmediateValue == 15, "extended immediate value");

		/** test MULU **/
		assertionContext <= "test MULU";
		currentInstructionRegister <= 32'b000110_10101_01010_00111_00000000000;
		#1;
		assert(currentInstructionOpcode == 6, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(currentInstructionRegisterIndex3 == 7, "operand index 3");
		assert(~currentInstructionRightOperandIsImmediate, "is immediate");
		assert(instructionDecoder.currentInstructionDestinationRegisterIndexIsRegisterIndex3, "dest is 3");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionRightOperandRegisterIndex == 10, "right operand index");
		assert(currentInstructionDestinationRegisterIndex == 7, "destination operand index");

		/** test MULUI **/
		assertionContext <= "test MULUI";
		currentInstructionRegister <= 32'b000111_10101_01010_0000000000001111;
		#1;
		assert(currentInstructionOpcode == 7, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(instructionDecoder.currentInstructionRawImmediateValue == 15, "immediate operand");
		assert(currentInstructionRightOperandIsImmediate, "is immediate");
		assert(~instructionDecoder.currentInstructionImmediateValueIsSigned, "immediate is signed");
		assert(~instructionDecoder.currentInstructionDestinationRegisterIndexIsRegisterIndex3, "dest is 3");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionDestinationRegisterIndex == 10, "destination operand index");
		assert(currentInstructionExtendedImmediateValue == 15, "extended immediate value");

		/** test MULUI with a large operand (must be interpreted as unsigned!) **/
		assertionContext <= "test MULUI (large)";
		currentInstructionRegister <= 32'b000111_10101_01010_1111111111110000;
		#1;
		assert(currentInstructionOpcode == 7, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(instructionDecoder.currentInstructionRawImmediateValue == 65520, "immediate operand");
		assert(currentInstructionRightOperandIsImmediate, "is immediate");
		assert(~instructionDecoder.currentInstructionImmediateValueIsSigned, "immediate is signed");
		assert(~instructionDecoder.currentInstructionDestinationRegisterIndexIsRegisterIndex3, "dest is 3");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionDestinationRegisterIndex == 10, "destination operand index");
		assert(currentInstructionExtendedImmediateValue == 65520, "extended immediate value");

		/** test DIV **/
		assertionContext <= "test DIV";
		currentInstructionRegister <= 32'b001000_10101_01010_00111_00000000000;
		#1;
		assert(currentInstructionOpcode == 8, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(currentInstructionRegisterIndex3 == 7, "operand index 3");
		assert(~currentInstructionRightOperandIsImmediate, "is immediate");
		assert(instructionDecoder.currentInstructionDestinationRegisterIndexIsRegisterIndex3, "dest is 3");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionRightOperandRegisterIndex == 10, "right operand index");
		assert(currentInstructionDestinationRegisterIndex == 7, "destination operand index");

		/** test DIVI **/
		assertionContext <= "test DIVI";
		currentInstructionRegister <= 32'b001001_10101_01010_0000000000001111;
		#1;
		assert(currentInstructionOpcode == 9, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(instructionDecoder.currentInstructionRawImmediateValue == 15, "immediate operand");
		assert(currentInstructionRightOperandIsImmediate, "is immediate");
		assert(instructionDecoder.currentInstructionImmediateValueIsSigned, "immediate is signed");
		assert(~instructionDecoder.currentInstructionDestinationRegisterIndexIsRegisterIndex3, "dest is 3");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionDestinationRegisterIndex == 10, "destination operand index");
		assert(currentInstructionExtendedImmediateValue == 15, "extended immediate value");

		/** test DIVU **/
		assertionContext <= "test DIVU";
		currentInstructionRegister <= 32'b001010_10101_01010_00111_00000000000;
		#1;
		assert(currentInstructionOpcode == 10, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(currentInstructionRegisterIndex3 == 7, "operand index 3");
		assert(~currentInstructionRightOperandIsImmediate, "is immediate");
		assert(instructionDecoder.currentInstructionDestinationRegisterIndexIsRegisterIndex3, "dest is 3");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionRightOperandRegisterIndex == 10, "right operand index");
		assert(currentInstructionDestinationRegisterIndex == 7, "destination operand index");

		/** test DIVUI **/
		assertionContext <= "test DIVUI";
		currentInstructionRegister <= 32'b001011_10101_01010_0000000000001111;
		#1;
		assert(currentInstructionOpcode == 11, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(instructionDecoder.currentInstructionRawImmediateValue == 15, "immediate operand");
		assert(currentInstructionRightOperandIsImmediate, "is immediate");
		assert(~instructionDecoder.currentInstructionImmediateValueIsSigned, "immediate is signed");
		assert(~instructionDecoder.currentInstructionDestinationRegisterIndexIsRegisterIndex3, "dest is 3");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionDestinationRegisterIndex == 10, "destination operand index");
		assert(currentInstructionExtendedImmediateValue == 15, "extended immediate value");

		/** test REM **/
		assertionContext <= "test REM";
		currentInstructionRegister <= 32'b001100_10101_01010_00111_00000000000;
		#1;
		assert(currentInstructionOpcode == 12, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(currentInstructionRegisterIndex3 == 7, "operand index 3");
		assert(~currentInstructionRightOperandIsImmediate, "is immediate");
		assert(instructionDecoder.currentInstructionDestinationRegisterIndexIsRegisterIndex3, "dest is 3");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionRightOperandRegisterIndex == 10, "right operand index");
		assert(currentInstructionDestinationRegisterIndex == 7, "destination operand index");

		/** test REMI **/
		assertionContext <= "test REMI";
		currentInstructionRegister <= 32'b001101_10101_01010_0000000000001111;
		#1;
		assert(currentInstructionOpcode == 13, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(instructionDecoder.currentInstructionRawImmediateValue == 15, "immediate operand");
		assert(currentInstructionRightOperandIsImmediate, "is immediate");
		assert(instructionDecoder.currentInstructionImmediateValueIsSigned, "immediate is signed");
		assert(~instructionDecoder.currentInstructionDestinationRegisterIndexIsRegisterIndex3, "dest is 3");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionDestinationRegisterIndex == 10, "destination operand index");
		assert(currentInstructionExtendedImmediateValue == 15, "extended immediate value");

		/** test REMU **/
		assertionContext <= "test REMU";
		currentInstructionRegister <= 32'b001110_10101_01010_00111_00000000000;
		#1;
		assert(currentInstructionOpcode == 14, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(currentInstructionRegisterIndex3 == 7, "operand index 3");
		assert(~currentInstructionRightOperandIsImmediate, "is immediate");
		assert(instructionDecoder.currentInstructionDestinationRegisterIndexIsRegisterIndex3, "dest is 3");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionRightOperandRegisterIndex == 10, "right operand index");
		assert(currentInstructionDestinationRegisterIndex == 7, "destination operand index");

		/** test REMUI **/
		assertionContext <= "test REMUI";
		currentInstructionRegister <= 32'b001111_10101_01010_0000000000001111;
		#1;
		assert(currentInstructionOpcode == 15, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(instructionDecoder.currentInstructionRawImmediateValue == 15, "immediate operand");
		assert(currentInstructionRightOperandIsImmediate, "is immediate");
		assert(~instructionDecoder.currentInstructionImmediateValueIsSigned, "immediate is signed");
		assert(~instructionDecoder.currentInstructionDestinationRegisterIndexIsRegisterIndex3, "dest is 3");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionDestinationRegisterIndex == 10, "destination operand index");
		assert(currentInstructionExtendedImmediateValue == 15, "extended immediate value");

		/** test AND **/
		assertionContext <= "test AND";
		currentInstructionRegister <= 32'b010000_10101_01010_00111_00000000000;
		#1;
		assert(currentInstructionOpcode == 16, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(currentInstructionRegisterIndex3 == 7, "operand index 3");
		assert(~currentInstructionRightOperandIsImmediate, "is immediate");
		assert(instructionDecoder.currentInstructionDestinationRegisterIndexIsRegisterIndex3, "dest is 3");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionRightOperandRegisterIndex == 10, "right operand index");
		assert(currentInstructionDestinationRegisterIndex == 7, "destination operand index");
		assert(currentInstructionAluOperation == `ALU_OPERATION_AND, "ALU operation");

		/** test ANDI **/
		assertionContext <= "test ANDI";
		currentInstructionRegister <= 32'b010001_10101_01010_0000000000001111;
		#1;
		assert(currentInstructionOpcode == 17, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(instructionDecoder.currentInstructionRawImmediateValue == 15, "immediate operand");
		assert(currentInstructionRightOperandIsImmediate, "is immediate");
		assert(~instructionDecoder.currentInstructionImmediateValueIsSigned, "immediate is signed");
		assert(~instructionDecoder.currentInstructionDestinationRegisterIndexIsRegisterIndex3, "dest is 3");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionDestinationRegisterIndex == 10, "destination operand index");
		assert(currentInstructionExtendedImmediateValue == 15, "extended immediate value");
		assert(currentInstructionAluOperation == `ALU_OPERATION_AND, "ALU operation");

		/** test ANDI (all bits set) **/
		assertionContext <= "test ANDI (all bits set)";
		currentInstructionRegister <= 32'b010001_10101_01010_1111111111111111;
		#1;
		assert(currentInstructionOpcode == 17, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(instructionDecoder.currentInstructionRawImmediateValue == 65535, "immediate operand");
		assert(currentInstructionRightOperandIsImmediate, "is immediate");
		assert(~instructionDecoder.currentInstructionImmediateValueIsSigned, "immediate is signed");
		assert(~instructionDecoder.currentInstructionDestinationRegisterIndexIsRegisterIndex3, "dest is 3");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionDestinationRegisterIndex == 10, "destination operand index");
		assert(currentInstructionExtendedImmediateValue == 65535, "extended immediate value");
		assert(currentInstructionAluOperation == `ALU_OPERATION_AND, "ALU operation");

		/** test OR **/
		assertionContext <= "test OR";
		currentInstructionRegister <= 32'b010010_10101_01010_00111_00000000000;
		#1;
		assert(currentInstructionOpcode == 18, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(currentInstructionRegisterIndex3 == 7, "operand index 3");
		assert(~currentInstructionRightOperandIsImmediate, "is immediate");
		assert(instructionDecoder.currentInstructionDestinationRegisterIndexIsRegisterIndex3, "dest is 3");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionRightOperandRegisterIndex == 10, "right operand index");
		assert(currentInstructionDestinationRegisterIndex == 7, "destination operand index");
		assert(currentInstructionAluOperation == `ALU_OPERATION_OR, "ALU operation");

		/** test ORI **/
		assertionContext <= "test ORI";
		currentInstructionRegister <= 32'b010011_10101_01010_0000000000001111;
		#1;
		assert(currentInstructionOpcode == 19, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(instructionDecoder.currentInstructionRawImmediateValue == 15, "immediate operand");
		assert(currentInstructionRightOperandIsImmediate, "is immediate");
		assert(~instructionDecoder.currentInstructionImmediateValueIsSigned, "immediate is signed");
		assert(~instructionDecoder.currentInstructionDestinationRegisterIndexIsRegisterIndex3, "dest is 3");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionDestinationRegisterIndex == 10, "destination operand index");
		assert(currentInstructionExtendedImmediateValue == 15, "extended immediate value");
		assert(currentInstructionAluOperation == `ALU_OPERATION_OR, "ALU operation");

		/** test XOR **/
		assertionContext <= "test XOR";
		currentInstructionRegister <= 32'b010100_10101_01010_00111_00000000000;
		#1;
		assert(currentInstructionOpcode == 20, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(currentInstructionRegisterIndex3 == 7, "operand index 3");
		assert(~currentInstructionRightOperandIsImmediate, "is immediate");
		assert(instructionDecoder.currentInstructionDestinationRegisterIndexIsRegisterIndex3, "dest is 3");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionRightOperandRegisterIndex == 10, "right operand index");
		assert(currentInstructionDestinationRegisterIndex == 7, "destination operand index");
		assert(currentInstructionAluOperation == `ALU_OPERATION_XOR, "ALU operation");

		/** test XORI **/
		assertionContext <= "test XORI";
		currentInstructionRegister <= 32'b010101_10101_01010_0000000000001111;
		#1;
		assert(currentInstructionOpcode == 21, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(instructionDecoder.currentInstructionRawImmediateValue == 15, "immediate operand");
		assert(currentInstructionRightOperandIsImmediate, "is immediate");
		assert(~instructionDecoder.currentInstructionImmediateValueIsSigned, "immediate is signed");
		assert(~instructionDecoder.currentInstructionDestinationRegisterIndexIsRegisterIndex3, "dest is 3");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionDestinationRegisterIndex == 10, "destination operand index");
		assert(currentInstructionExtendedImmediateValue == 15, "extended immediate value");
		assert(currentInstructionAluOperation == `ALU_OPERATION_XOR, "ALU operation");

		/** test XNOR **/
		assertionContext <= "test XNOR";
		currentInstructionRegister <= 32'b010110_10101_01010_00111_00000000000;
		#1;
		assert(currentInstructionOpcode == 22, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(currentInstructionRegisterIndex3 == 7, "operand index 3");
		assert(~currentInstructionRightOperandIsImmediate, "is immediate");
		assert(instructionDecoder.currentInstructionDestinationRegisterIndexIsRegisterIndex3, "dest is 3");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionRightOperandRegisterIndex == 10, "right operand index");
		assert(currentInstructionDestinationRegisterIndex == 7, "destination operand index");
		assert(currentInstructionAluOperation == `ALU_OPERATION_XNOR, "ALU operation");

		/** test XNORI **/
		assertionContext <= "test XNORI";
		currentInstructionRegister <= 32'b010111_10101_01010_0000000000001111;
		#1;
		assert(currentInstructionOpcode == 23, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(instructionDecoder.currentInstructionRawImmediateValue == 15, "immediate operand");
		assert(currentInstructionRightOperandIsImmediate, "is immediate");
		assert(~instructionDecoder.currentInstructionImmediateValueIsSigned, "immediate is signed");
		assert(~instructionDecoder.currentInstructionDestinationRegisterIndexIsRegisterIndex3, "dest is 3");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionDestinationRegisterIndex == 10, "destination operand index");
		assert(currentInstructionExtendedImmediateValue == 15, "extended immediate value");
		assert(currentInstructionAluOperation == `ALU_OPERATION_XNOR, "ALU operation");

		/** test SLL **/
		assertionContext <= "test SLL";
		currentInstructionRegister <= 32'b011000_10101_01010_00111_00000000000;
		#1;
		assert(currentInstructionOpcode == 24, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(currentInstructionRegisterIndex3 == 7, "operand index 3");
		assert(~currentInstructionRightOperandIsImmediate, "is immediate");
		assert(instructionDecoder.currentInstructionDestinationRegisterIndexIsRegisterIndex3, "dest is 3");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionRightOperandRegisterIndex == 10, "right operand index");
		assert(currentInstructionDestinationRegisterIndex == 7, "destination operand index");

		/** test SLLI **/
		assertionContext <= "test SLLI";
		currentInstructionRegister <= 32'b011001_10101_01010_0000000000001111;
		#1;
		assert(currentInstructionOpcode == 25, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(instructionDecoder.currentInstructionRawImmediateValue == 15, "immediate operand");
		assert(currentInstructionRightOperandIsImmediate, "is immediate");
		assert(~instructionDecoder.currentInstructionDestinationRegisterIndexIsRegisterIndex3, "dest is 3");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionDestinationRegisterIndex == 10, "destination operand index");

		/** test SLR **/
		assertionContext <= "test SLR";
		currentInstructionRegister <= 32'b011010_10101_01010_00111_00000000000;
		#1;
		assert(currentInstructionOpcode == 26, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(currentInstructionRegisterIndex3 == 7, "operand index 3");
		assert(~currentInstructionRightOperandIsImmediate, "is immediate");
		assert(instructionDecoder.currentInstructionDestinationRegisterIndexIsRegisterIndex3, "dest is 3");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionRightOperandRegisterIndex == 10, "right operand index");
		assert(currentInstructionDestinationRegisterIndex == 7, "destination operand index");

		/** test SLRI **/
		assertionContext <= "test SLRI";
		currentInstructionRegister <= 32'b011011_10101_01010_0000000000001111;
		#1;
		assert(currentInstructionOpcode == 27, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(instructionDecoder.currentInstructionRawImmediateValue == 15, "immediate operand");
		assert(currentInstructionRightOperandIsImmediate, "is immediate");
		assert(~instructionDecoder.currentInstructionDestinationRegisterIndexIsRegisterIndex3, "dest is 3");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionDestinationRegisterIndex == 10, "destination operand index");

		/** test SAR **/
		assertionContext <= "test SAR";
		currentInstructionRegister <= 32'b011100_10101_01010_00111_00000000000;
		#1;
		assert(currentInstructionOpcode == 28, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(currentInstructionRegisterIndex3 == 7, "operand index 3");
		assert(~currentInstructionRightOperandIsImmediate, "is immediate");
		assert(instructionDecoder.currentInstructionDestinationRegisterIndexIsRegisterIndex3, "dest is 3");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionRightOperandRegisterIndex == 10, "right operand index");
		assert(currentInstructionDestinationRegisterIndex == 7, "destination operand index");

		/** test SARI **/
		assertionContext <= "test SARI";
		currentInstructionRegister <= 32'b011101_10101_01010_0000000000001111;
		#1;
		assert(currentInstructionOpcode == 29, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(instructionDecoder.currentInstructionRawImmediateValue == 15, "immediate operand");
		assert(currentInstructionRightOperandIsImmediate, "is immediate");
		assert(~instructionDecoder.currentInstructionDestinationRegisterIndexIsRegisterIndex3, "dest is 3");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionDestinationRegisterIndex == 10, "destination operand index");

		/** test LDHI **/
		assertionContext <= "test LDHI";
		currentInstructionRegister <= 32'b011111_10101_01010_0000000000001111;
		#1;
		assert(currentInstructionOpcode == 31, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(instructionDecoder.currentInstructionRawImmediateValue == 15, "immediate operand");
		assert(currentInstructionRightOperandIsImmediate, "is immediate");
		assert(~instructionDecoder.currentInstructionDestinationRegisterIndexIsRegisterIndex3, "dest is 3");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionDestinationRegisterIndex == 10, "destination operand index");
		assert(currentInstructionAluOperation == `ALU_OPERATION_HIGH, "ALU operation");

		/** test BEQ **/
		assertionContext <= "test BEQ";
		currentInstructionRegister <= 32'b100000_10101_01010_0000000000001111;
		#1;
		assert(currentInstructionOpcode == 32, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(instructionDecoder.currentInstructionRawImmediateValue == 15, "immediate operand");
		assert(~currentInstructionRightOperandIsImmediate, "is immediate");
		assert(instructionDecoder.currentInstructionImmediateValueIsSigned, "immediate is signed");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionRightOperandRegisterIndex == 10, "right operand index");
		assert(currentInstructionExtendedImmediateValue == 15, "extended immediate value");
		assert(currentInstructionAluOperation == `ALU_OPERATION_SUB, "ALU operation");
		assert(currentInstructionComparisonOperation <= `ALU_COMPARISON_EQUAL, "comparison operation");

		/** test BEQ with negative offset **/
		assertionContext <= "test BEQ (negative offset) ";
		currentInstructionRegister <= 32'b100000_10101_01010_1111111111110000;
		#1;
		assert(currentInstructionOpcode == 32, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(instructionDecoder.currentInstructionRawImmediateValue == -16, "immediate operand");
		assert(~currentInstructionRightOperandIsImmediate, "is immediate");
		assert(instructionDecoder.currentInstructionImmediateValueIsSigned, "immediate is signed");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionRightOperandRegisterIndex == 10, "right operand index");
		assert(currentInstructionExtendedImmediateValue == -16, "extended immediate value");
		assert(currentInstructionAluOperation == `ALU_OPERATION_SUB, "ALU operation");
		assert(currentInstructionComparisonOperation <= `ALU_COMPARISON_EQUAL, "comparison operation");

		/** test BNE **/
		assertionContext <= "test BNE";
		currentInstructionRegister <= 32'b100001_10101_01010_0000000000001111;
		#1;
		assert(currentInstructionOpcode == 33, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(instructionDecoder.currentInstructionRawImmediateValue == 15, "immediate operand");
		assert(~currentInstructionRightOperandIsImmediate, "is immediate");
		assert(instructionDecoder.currentInstructionImmediateValueIsSigned, "immediate is signed");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionRightOperandRegisterIndex == 10, "right operand index");
		assert(currentInstructionExtendedImmediateValue == 15, "extended immediate value");
		assert(currentInstructionAluOperation == `ALU_OPERATION_SUB, "ALU operation");
		assert(currentInstructionComparisonOperation <= `ALU_COMPARISON_NOT_EQUAL, "comparison operation");

		/** test BLE **/
		assertionContext <= "test BLE";
		currentInstructionRegister <= 32'b100010_10101_01010_0000000000001111;
		#1;
		assert(currentInstructionOpcode == 34, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(instructionDecoder.currentInstructionRawImmediateValue == 15, "immediate operand");
		assert(~currentInstructionRightOperandIsImmediate, "is immediate");
		assert(instructionDecoder.currentInstructionImmediateValueIsSigned, "immediate is signed");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionRightOperandRegisterIndex == 10, "right operand index");
		assert(currentInstructionExtendedImmediateValue == 15, "extended immediate value");
		assert(currentInstructionAluOperation == `ALU_OPERATION_SUB, "ALU operation");
		assert(currentInstructionSignedComparison == 1'b1, "comparison signed-ness");
		assert(currentInstructionComparisonOperation <= `ALU_COMPARISON_LESS_EQUAL, "comparison operation");

		/** test BLEU **/
		assertionContext <= "test BLEU";
		currentInstructionRegister <= 32'b100011_10101_01010_0000000000001111;
		#1;
		assert(currentInstructionOpcode == 35, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(instructionDecoder.currentInstructionRawImmediateValue == 15, "immediate operand");
		assert(~currentInstructionRightOperandIsImmediate, "is immediate");
		assert(instructionDecoder.currentInstructionImmediateValueIsSigned, "immediate is signed");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionRightOperandRegisterIndex == 10, "right operand index");
		assert(currentInstructionExtendedImmediateValue == 15, "extended immediate value");
		assert(currentInstructionAluOperation == `ALU_OPERATION_SUB, "ALU operation");
		assert(currentInstructionSignedComparison == 1'b0, "comparison signed-ness");
		assert(currentInstructionComparisonOperation <= `ALU_COMPARISON_LESS_EQUAL, "comparison operation");

		/** test BLT **/
		assertionContext <= "test BLT";
		currentInstructionRegister <= 32'b100100_10101_01010_0000000000001111;
		#1;
		assert(currentInstructionOpcode == 36, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(instructionDecoder.currentInstructionRawImmediateValue == 15, "immediate operand");
		assert(~currentInstructionRightOperandIsImmediate, "is immediate");
		assert(instructionDecoder.currentInstructionImmediateValueIsSigned, "immediate is signed");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionRightOperandRegisterIndex == 10, "right operand index");
		assert(currentInstructionExtendedImmediateValue == 15, "extended immediate value");
		assert(currentInstructionAluOperation == `ALU_OPERATION_SUB, "ALU operation");
		assert(currentInstructionSignedComparison == 1'b1, "comparison signed-ness");
		assert(currentInstructionComparisonOperation <= `ALU_COMPARISON_LESS_THAN, "comparison operation");

		/** test BLTU **/
		assertionContext <= "test BLTU";
		currentInstructionRegister <= 32'b100101_10101_01010_0000000000001111;
		#1;
		assert(currentInstructionOpcode == 37, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(instructionDecoder.currentInstructionRawImmediateValue == 15, "immediate operand");
		assert(~currentInstructionRightOperandIsImmediate, "is immediate");
		assert(instructionDecoder.currentInstructionImmediateValueIsSigned, "immediate is signed");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionRightOperandRegisterIndex == 10, "right operand index");
		assert(currentInstructionExtendedImmediateValue == 15, "extended immediate value");
		assert(currentInstructionAluOperation == `ALU_OPERATION_SUB, "ALU operation");
		assert(currentInstructionSignedComparison == 1'b0, "comparison signed-ness");
		assert(currentInstructionComparisonOperation <= `ALU_COMPARISON_LESS_THAN, "comparison operation");

		/** test BGE **/
		assertionContext <= "test BGE";
		currentInstructionRegister <= 32'b100110_10101_01010_0000000000001111;
		#1;
		assert(currentInstructionOpcode == 38, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(instructionDecoder.currentInstructionRawImmediateValue == 15, "immediate operand");
		assert(~currentInstructionRightOperandIsImmediate, "is immediate");
		assert(instructionDecoder.currentInstructionImmediateValueIsSigned, "immediate is signed");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionRightOperandRegisterIndex == 10, "right operand index");
		assert(currentInstructionExtendedImmediateValue == 15, "extended immediate value");
		assert(currentInstructionAluOperation == `ALU_OPERATION_SUB, "ALU operation");
		assert(currentInstructionSignedComparison == 1'b1, "comparison signed-ness");
		assert(currentInstructionComparisonOperation <= `ALU_COMPARISON_GREATER_EQUAL, "comparison operation");

		/** test BGEU **/
		assertionContext <= "test BGEU";
		currentInstructionRegister <= 32'b100111_10101_01010_0000000000001111;
		#1;
		assert(currentInstructionOpcode == 39, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(instructionDecoder.currentInstructionRawImmediateValue == 15, "immediate operand");
		assert(~currentInstructionRightOperandIsImmediate, "is immediate");
		assert(instructionDecoder.currentInstructionImmediateValueIsSigned, "immediate is signed");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionRightOperandRegisterIndex == 10, "right operand index");
		assert(currentInstructionExtendedImmediateValue == 15, "extended immediate value");
		assert(currentInstructionAluOperation == `ALU_OPERATION_SUB, "ALU operation");
		assert(currentInstructionSignedComparison == 1'b0, "comparison signed-ness");
		assert(currentInstructionComparisonOperation <= `ALU_COMPARISON_GREATER_EQUAL, "comparison operation");

		/** test BGT **/
		assertionContext <= "test BGT";
		currentInstructionRegister <= 32'b101000_10101_01010_0000000000001111;
		#1;
		assert(currentInstructionOpcode == 40, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(instructionDecoder.currentInstructionRawImmediateValue == 15, "immediate operand");
		assert(~currentInstructionRightOperandIsImmediate, "is immediate");
		assert(instructionDecoder.currentInstructionImmediateValueIsSigned, "immediate is signed");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionRightOperandRegisterIndex == 10, "right operand index");
		assert(currentInstructionExtendedImmediateValue == 15, "extended immediate value");
		assert(currentInstructionAluOperation == `ALU_OPERATION_SUB, "ALU operation");
		assert(currentInstructionSignedComparison == 1'b1, "comparison signed-ness");
		assert(currentInstructionComparisonOperation <= `ALU_COMPARISON_GREATER_THAN, "comparison operation");

		/** test BGTU **/
		assertionContext <= "test BGTU";
		currentInstructionRegister <= 32'b101001_10101_01010_0000000000001111;
		#1;
		assert(currentInstructionOpcode == 41, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(instructionDecoder.currentInstructionRawImmediateValue == 15, "immediate operand");
		assert(~currentInstructionRightOperandIsImmediate, "is immediate");
		assert(instructionDecoder.currentInstructionImmediateValueIsSigned, "immediate is signed");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionRightOperandRegisterIndex == 10, "right operand index");
		assert(currentInstructionExtendedImmediateValue == 15, "extended immediate value");
		assert(currentInstructionAluOperation == `ALU_OPERATION_SUB, "ALU operation");
		assert(currentInstructionSignedComparison == 1'b0, "comparison signed-ness");
		assert(currentInstructionComparisonOperation <= `ALU_COMPARISON_GREATER_THAN, "comparison operation");

		/** test J **/
		assertionContext <= "test J";
		currentInstructionRegister <= 32'b101010_00000000000000000000001111;
		#1;
		assert(currentInstructionOpcode == 42, "opcode");
		assert(instructionDecoder.currentInstructionRawJumpOffset == 15, "raw offset");
		assert(currentInstructionExtendedJumpOffset == 15, "extended offset");

		/** test J with negative offset **/
		assertionContext <= "test J (negative offset) ";
		currentInstructionRegister <= 32'b101010_11111111111111111111110000;
		#1;
		assert(currentInstructionOpcode == 42, "opcode");
		assert(instructionDecoder.currentInstructionRawJumpOffset == -16, "raw offset");
		assert(currentInstructionExtendedJumpOffset == -16, "extended offset");

		/** test JR **/
		assertionContext <= "test JR";
		currentInstructionRegister <= 32'b101011_10101_01010_00111_00000000000;
		#1;
		assert(currentInstructionOpcode == 43, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(currentInstructionRegisterIndex3 == 7, "operand index 2");

		/** test JAL **/
		assertionContext <= "test JAL";
		currentInstructionRegister <= 32'b101100_00000000000000000000001111;
		#1;
		assert(currentInstructionOpcode == 44, "opcode");
		assert(instructionDecoder.currentInstructionRawJumpOffset == 15, "raw offset");
		assert(currentInstructionExtendedJumpOffset == 15, "extended offset");

		/** test JAL with negative offset **/
		assertionContext <= "test JAL (negative offset) ";
		currentInstructionRegister <= 32'b101100_11111111111111111111110000;
		#1;
		assert(currentInstructionOpcode == 44, "opcode");
		assert(instructionDecoder.currentInstructionRawJumpOffset == -16, "raw offset");
		assert(currentInstructionExtendedJumpOffset == -16, "extended offset");

		/** test JALR **/
		assertionContext <= "test JALR";
		currentInstructionRegister <= 32'b101101_10101_01010_00111_00000000000;
		#1;
		assert(currentInstructionOpcode == 45, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(currentInstructionRegisterIndex3 == 7, "operand index 2");

		/** test TRAP **/
		assertionContext <= "test TRAP";
		currentInstructionRegister <= 32'b101110_10101_01010_00111_00000000000;
		#1;
		assert(currentInstructionOpcode == 46, "opcode");

		/** test RFX **/
		assertionContext <= "test RFX";
		currentInstructionRegister <= 32'b101111_10101_01010_00111_00000000000;
		#1;
		assert(currentInstructionOpcode == 47, "opcode");

		/** test LDW **/
		assertionContext <= "test LDW";
		currentInstructionRegister <= 32'b110000_10101_01010_0000000000001111;
		#1;
		assert(currentInstructionOpcode == 48, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(instructionDecoder.currentInstructionRawImmediateValue == 15, "immediate operand");
		assert(currentInstructionRightOperandIsImmediate, "is immediate");
		assert(instructionDecoder.currentInstructionImmediateValueIsSigned, "immediate is signed");
		assert(~instructionDecoder.currentInstructionDestinationRegisterIndexIsRegisterIndex3, "dest is 3");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionDestinationRegisterIndex == 10, "destination operand index");
		assert(currentInstructionExtendedImmediateValue == 15, "extended immediate value");
		assert(currentInstructionAluOperation == `ALU_OPERATION_ADD, "ALU operation");
		assert(~currentInstructionLoadStoreIsStore, "load/store is store");
		assert(currentInstructionBusSize == `BUS_SIZE_WORD, "bus size");

		/** test LDW (negative address offset) **/
		assertionContext <= "test LDW (negative address offset)";
		currentInstructionRegister <= 32'b110000_10101_01010_1111111111110000;
		#1;
		assert(currentInstructionOpcode == 48, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(instructionDecoder.currentInstructionRawImmediateValue == -16, "immediate operand");
		assert(currentInstructionRightOperandIsImmediate, "is immediate");
		assert(instructionDecoder.currentInstructionImmediateValueIsSigned, "immediate is signed");
		assert(~instructionDecoder.currentInstructionDestinationRegisterIndexIsRegisterIndex3, "dest is 3");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionDestinationRegisterIndex == 10, "destination operand index");
		assert(currentInstructionExtendedImmediateValue == -16, "extended immediate value");
		assert(currentInstructionAluOperation == `ALU_OPERATION_ADD, "ALU operation");
		assert(~currentInstructionLoadStoreIsStore, "load/store is store");
		assert(currentInstructionBusSize == `BUS_SIZE_WORD, "bus size");

		/** test LDH **/
		assertionContext <= "test LDH";
		currentInstructionRegister <= 32'b110001_10101_01010_0000000000001111;
		#1;
		assert(currentInstructionOpcode == 49, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(instructionDecoder.currentInstructionRawImmediateValue == 15, "immediate operand");
		assert(currentInstructionRightOperandIsImmediate, "is immediate");
		assert(instructionDecoder.currentInstructionImmediateValueIsSigned, "immediate is signed");
		assert(~instructionDecoder.currentInstructionDestinationRegisterIndexIsRegisterIndex3, "dest is 3");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionDestinationRegisterIndex == 10, "destination operand index");
		assert(currentInstructionExtendedImmediateValue == 15, "extended immediate value");
		assert(currentInstructionAluOperation == `ALU_OPERATION_ADD, "ALU operation");
		assert(~currentInstructionLoadStoreIsStore, "load/store is store");
		assert(currentInstructionBusSize == `BUS_SIZE_HALFWORD, "bus size");
		assert(currentInstructionExtendMode == `EXTEND_MODE_SIGN, "extend mode");

		/** test LDHU **/
		assertionContext <= "test LDHU";
		currentInstructionRegister <= 32'b110010_10101_01010_0000000000001111;
		#1;
		assert(currentInstructionOpcode == 50, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(instructionDecoder.currentInstructionRawImmediateValue == 15, "immediate operand");
		assert(currentInstructionRightOperandIsImmediate, "is immediate");
		assert(instructionDecoder.currentInstructionImmediateValueIsSigned, "immediate is signed");
		assert(~instructionDecoder.currentInstructionDestinationRegisterIndexIsRegisterIndex3, "dest is 3");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionDestinationRegisterIndex == 10, "destination operand index");
		assert(currentInstructionExtendedImmediateValue == 15, "extended immediate value");
		assert(currentInstructionAluOperation == `ALU_OPERATION_ADD, "ALU operation");
		assert(~currentInstructionLoadStoreIsStore, "load/store is store");
		assert(currentInstructionBusSize == `BUS_SIZE_HALFWORD, "bus size");
		assert(currentInstructionExtendMode == `EXTEND_MODE_ZERO, "extend mode");

		/** test LDB **/
		assertionContext <= "test LDB";
		currentInstructionRegister <= 32'b110011_10101_01010_0000000000001111;
		#1;
		assert(currentInstructionOpcode == 51, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(instructionDecoder.currentInstructionRawImmediateValue == 15, "immediate operand");
		assert(currentInstructionRightOperandIsImmediate, "is immediate");
		assert(instructionDecoder.currentInstructionImmediateValueIsSigned, "immediate is signed");
		assert(~instructionDecoder.currentInstructionDestinationRegisterIndexIsRegisterIndex3, "dest is 3");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionDestinationRegisterIndex == 10, "destination operand index");
		assert(currentInstructionExtendedImmediateValue == 15, "extended immediate value");
		assert(currentInstructionAluOperation == `ALU_OPERATION_ADD, "ALU operation");
		assert(~currentInstructionLoadStoreIsStore, "load/store is store");
		assert(currentInstructionBusSize == `BUS_SIZE_BYTE, "bus size");
		assert(currentInstructionExtendMode == `EXTEND_MODE_SIGN, "extend mode");

		/** test LDBU **/
		assertionContext <= "test LDBU";
		currentInstructionRegister <= 32'b110100_10101_01010_0000000000001111;
		#1;
		assert(currentInstructionOpcode == 52, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(instructionDecoder.currentInstructionRawImmediateValue == 15, "immediate operand");
		assert(currentInstructionRightOperandIsImmediate, "is immediate");
		assert(instructionDecoder.currentInstructionImmediateValueIsSigned, "immediate is signed");
		assert(~instructionDecoder.currentInstructionDestinationRegisterIndexIsRegisterIndex3, "dest is 3");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionDestinationRegisterIndex == 10, "destination operand index");
		assert(currentInstructionExtendedImmediateValue == 15, "extended immediate value");
		assert(currentInstructionAluOperation == `ALU_OPERATION_ADD, "ALU operation");
		assert(~currentInstructionLoadStoreIsStore, "load/store is store");
		assert(currentInstructionBusSize == `BUS_SIZE_BYTE, "bus size");
		assert(currentInstructionExtendMode == `EXTEND_MODE_ZERO, "extend mode");

		/** test STW **/
		assertionContext <= "test STW";
		currentInstructionRegister <= 32'b110101_10101_01010_0000000000001111;
		#1;
		assert(currentInstructionOpcode == 53, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(instructionDecoder.currentInstructionRawImmediateValue == 15, "immediate operand");
		assert(currentInstructionRightOperandIsImmediate, "is immediate");
		assert(instructionDecoder.currentInstructionImmediateValueIsSigned, "immediate is signed");
		assert(~instructionDecoder.currentInstructionDestinationRegisterIndexIsRegisterIndex3, "dest is 3");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionDestinationRegisterIndex == 10, "destination operand index");
		assert(currentInstructionExtendedImmediateValue == 15, "extended immediate value");
		assert(currentInstructionAluOperation == `ALU_OPERATION_ADD, "ALU operation");
		assert(currentInstructionLoadStoreIsStore, "load/store is store");
		assert(currentInstructionBusSize == `BUS_SIZE_WORD, "bus size");

		/** test STH **/
		assertionContext <= "test STH";
		currentInstructionRegister <= 32'b110110_10101_01010_0000000000001111;
		#1;
		assert(currentInstructionOpcode == 54, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(instructionDecoder.currentInstructionRawImmediateValue == 15, "immediate operand");
		assert(currentInstructionRightOperandIsImmediate, "is immediate");
		assert(instructionDecoder.currentInstructionImmediateValueIsSigned, "immediate is signed");
		assert(~instructionDecoder.currentInstructionDestinationRegisterIndexIsRegisterIndex3, "dest is 3");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionDestinationRegisterIndex == 10, "destination operand index");
		assert(currentInstructionExtendedImmediateValue == 15, "extended immediate value");
		assert(currentInstructionAluOperation == `ALU_OPERATION_ADD, "ALU operation");
		assert(currentInstructionLoadStoreIsStore, "load/store is store");
		assert(currentInstructionBusSize == `BUS_SIZE_HALFWORD, "bus size");

		/** test STB **/
		assertionContext <= "test STB";
		currentInstructionRegister <= 32'b110111_10101_01010_0000000000001111;
		#1;
		assert(currentInstructionOpcode == 55, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(instructionDecoder.currentInstructionRawImmediateValue == 15, "immediate operand");
		assert(currentInstructionRightOperandIsImmediate, "is immediate");
		assert(instructionDecoder.currentInstructionImmediateValueIsSigned, "immediate is signed");
		assert(~instructionDecoder.currentInstructionDestinationRegisterIndexIsRegisterIndex3, "dest is 3");
		assert(currentInstructionLeftOperandRegisterIndex == 21, "left operand index");
		assert(currentInstructionDestinationRegisterIndex == 10, "destination operand index");
		assert(currentInstructionExtendedImmediateValue == 15, "extended immediate value");
		assert(currentInstructionAluOperation == `ALU_OPERATION_ADD, "ALU operation");
		assert(currentInstructionLoadStoreIsStore, "load/store is store");
		assert(currentInstructionBusSize == `BUS_SIZE_BYTE, "bus size");

		/** test MVFS **/
		assertionContext <= "test MVFS";
		currentInstructionRegister <= 32'b111000_10101_01010_0000000000001111;
		#1;
		assert(currentInstructionOpcode == 56, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(instructionDecoder.currentInstructionRawImmediateValue == 15, "immediate operand");
		assert(~instructionDecoder.currentInstructionDestinationRegisterIndexIsRegisterIndex3, "dest is 3");
		assert(currentInstructionDestinationRegisterIndex == 10, "destination operand index");

		/** test MVTS **/
		assertionContext <= "test MVTS";
		currentInstructionRegister <= 32'b111001_10101_01010_0000000000001111;
		#1;
		assert(currentInstructionOpcode == 57, "opcode");
		assert(currentInstructionRegisterIndex1 == 21, "operand index 1");
		assert(currentInstructionRegisterIndex2 == 10, "operand index 2");
		assert(instructionDecoder.currentInstructionRawImmediateValue == 15, "immediate operand");
		assert(~instructionDecoder.currentInstructionDestinationRegisterIndexIsRegisterIndex3, "dest is 3");
		assert(currentInstructionDestinationRegisterIndex == 10, "destination operand index");

		/** test TBS **/
		assertionContext <= "test TBS";
		currentInstructionRegister <= 32'b111010_10101_01010_0000000000001111;
		#1;
		assert(currentInstructionOpcode == 58, "opcode");

		/** test TBWR **/
		assertionContext <= "test TBWR";
		currentInstructionRegister <= 32'b111011_10101_01010_0000000000001111;
		#1;
		assert(currentInstructionOpcode == 59, "opcode");

		/** test TBRI **/
		assertionContext <= "test TBRI";
		currentInstructionRegister <= 32'b111100_10101_01010_0000000000001111;
		#1;
		assert(currentInstructionOpcode == 60, "opcode");

		/** test TBWI **/
		assertionContext <= "test TBWI";
		currentInstructionRegister <= 32'b111101_10101_01010_0000000000001111;
		#1;
		assert(currentInstructionOpcode == 61, "opcode");

		/** done **/
		#100 $finish;
	end
		
endmodule
