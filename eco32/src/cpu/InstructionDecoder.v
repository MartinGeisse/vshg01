`default_nettype none
`timescale 1ns / 1ps

/**
 * Instruction decoder. This module takes the current instruction and derives
 * input signals for the main state machine and other submodules from it in
 * a purely combinatorial wqy.
 */
module InstructionDecoder (

		/** the current instruction to decode **/
		input[31:0] currentInstruction,

		/** The opcode that is encoded into the current instruction. **/
		output[5:0] currentInstructionOpcode,
		
		/** Determines if the right operand is a register (0) or immediate (1) value **/
		output currentInstructionRightOperandIsImmediate,

		/** The register index of the left operand. **/
		output[4:0] currentInstructionLeftOperandRegisterIndex,
		
		/**
		 * The register index of the right operand. This field is only valid if
		 * the right operand is loaded from a register at all
		 * (i.e. currentInstructionRightOperandIsImmediate is 0).
		 */
		output[4:0] currentInstructionRightOperandRegisterIndex,

		/**
		 * The destination register index. This field is only valid under the
		 * assumption that the instruction has a destination register, i.e. generates
		 * a result that is stored in a general-purpose register.
		 */
		output[4:0] currentInstructionDestinationRegisterIndex,

		/** The immediate value of the current instruction, sign-/zero-extended to 32 bits. **/
		output[31:0] currentInstructionExtendedImmediateValue,
		
		/**
		 * The jump offset of the current instruction, sign-extended to 30 bits. Note that
		 * the actual value added to the PC is this value shifted left by two bit positions.
		 */
		output[29:0] currentInstructionExtendedJumpOffset,

		/** The ALU operation to perform for the current instruction. **/
		output reg[2:0] currentInstructionAluOperation,

		/** The ALU comparison signed-ness to assume for the current instruction. **/
		output reg currentInstructionSignedComparison,
		
		/** The ALU comparison operation to assume for the current instruction. **/
		output reg[2:0] currentInstructionComparisonOperation,

		/**
		 * This signal is valid for all load and store instructions. It is 1 if the instruction
		 * is a store instruction, 0 if it is a load instruction.
		 */
		output reg currentInstructionLoadStoreIsStore,

		/** This signal is valid for all load/store instructions, and specifies the bus access size to use. **/
		output reg[1:0] currentInstructionBusSize,

		/** This signal is valid for all load instructions, and specifies the extension mode to use (signed or unsigned). **/
		output currentInstructionExtendMode

	);

	/** assign some outputs directly **/
	assign currentInstructionOpcode = currentInstruction[31:26];
	wire[4:0] currentInstructionRegisterIndex1 = currentInstruction[25:21];
	wire[4:0] currentInstructionRegisterIndex2 = currentInstruction[20:16];
	wire[4:0] currentInstructionRegisterIndex3 = currentInstruction[15:11];

	/** The raw (non-extended) immediate value of this instruction. **/
	wire[15:0] currentInstructionRawImmediateValue = currentInstruction[15:0];
	
	/** The raw (non-extended) jump offset of this instruction. **/
	wire[25:0] currentInstructionRawJumpOffset = currentInstruction[25:0];

	/**
	 * This signal indicates whether the right operand is an immediate
	 * value (1) or a register value (0).
	 */
	assign currentInstructionRightOperandIsImmediate = currentInstructionOpcode[5] ? currentInstructionOpcode[4] : currentInstructionOpcode[0];
	
	/**
	 * This signal indicates whether the immediate value of this instruction
	 * is signed (1) or unsigned (0) and causes the corresponding extension
	 * to occur. The value of this signal is undefined for instructions that do
	 * not have an immediate value. It is, however, valid for instructions
	 * that have a non-operand immediate value, such as branch or load/store
	 * instructions. As a special rule, this signal is invalid for instructions
	 * that have an immediate value of which only the lower 16 (or less) bits
	 * are relevant.
	 */
	reg currentInstructionImmediateValueIsSigned;
	always @(*) begin
		casez (currentInstructionOpcode)
		
			/** signed arithmetic operators **/
			6'h01, 6'h03, 6'h05, 6'h09, 6'h0d:
				currentInstructionImmediateValueIsSigned <= 1'b1;
			
			/** unsigned arithmetic operators **/
			6'h07, 6'h0b, 6'h0f:
				currentInstructionImmediateValueIsSigned <= 1'b0;
				
			/** logical operators **/
			6'b01???1:
				currentInstructionImmediateValueIsSigned <= 1'b0;
			
			/** branch instructions **/
			6'b100???, 6'h28, 6'h29:
				currentInstructionImmediateValueIsSigned <= 1'b1;
				
			/** load/store instructions **/
			6'b110???:
				currentInstructionImmediateValueIsSigned <= 1'b1;
				
			/** for all other instructions, the immediate operand is either not extended or not used at all **/
			default:
				currentInstructionImmediateValueIsSigned <= 1'bx;

		endcase
	end
	
	/**
	 * This signal indicates whether the destination register for the
	 * current instruction is specified at position 3 (value is 1) or
	 * position 2 (value is 0) in the instruction. Only valid for
	 * instructions that have a destination register.
	 */
	wire currentInstructionDestinationRegisterIndexIsRegisterIndex3 = (~currentInstructionOpcode[5] & ~currentInstructionOpcode[0]);

	/** some more output signals **/
	assign currentInstructionLeftOperandRegisterIndex = currentInstructionRegisterIndex1;
	assign currentInstructionRightOperandRegisterIndex = currentInstructionRegisterIndex2;
	assign currentInstructionDestinationRegisterIndex = currentInstructionDestinationRegisterIndexIsRegisterIndex3 ? currentInstructionRegisterIndex3 : currentInstructionRegisterIndex2;

	/** extension of the immediate operand **/
	wire currentInstructionImmediateValueExtensionBit = (currentInstructionImmediateValueIsSigned & currentInstructionRawImmediateValue[15]);
	wire[15:0] currentInstructionImmediateValueExtensionBits = (currentInstructionImmediateValueExtensionBit ? 16'hFFFF : 16'h0000);
	assign currentInstructionExtendedImmediateValue = {currentInstructionImmediateValueExtensionBits, currentInstructionRawImmediateValue};

	/** extension of the jump offset **/
	wire[3:0] currentInstructionJumpOffsetExtensionBits = (currentInstructionRawJumpOffset[25] ? 6'b111111 : 6'b000000);
	assign currentInstructionExtendedJumpOffset = {currentInstructionJumpOffsetExtensionBits, currentInstructionRawJumpOffset};

	/** determine ALU operation **/
	always @(*) begin
		casez (currentInstructionOpcode)
			
			/** load/store instructions use ADD to compute the target address **/
			6'b110zzz:
				currentInstructionAluOperation <= `ALU_OPERATION_ADD;
			
			/** branch instructions use SUB to compute the branch condition **/
			6'b100zzz, 6'b10100z:
				currentInstructionAluOperation <= `ALU_OPERATION_SUB;
	
			/** AND, ANDI instructions **/
			6'b01000z:
				currentInstructionAluOperation <= `ALU_OPERATION_AND;
	
			/** OR, ORI instructions **/
			6'b01001z:
				currentInstructionAluOperation <= `ALU_OPERATION_OR;
	
			/** XOR, XORI instructions **/
			6'b01010z:
				currentInstructionAluOperation <= `ALU_OPERATION_XOR;
	
			/** XNOR, XNORI instructions **/
			6'b01011z:
				currentInstructionAluOperation <= `ALU_OPERATION_XNOR;
	
			/** LDHI instruction **/
			6'b011111:
				currentInstructionAluOperation <= `ALU_OPERATION_HIGH;
				
			/** ADD instruction **/
			6'b00000z:	
				currentInstructionAluOperation <= `ALU_OPERATION_ADD;
	
			/** SUB instruction **/
			6'b00001z:	
				currentInstructionAluOperation <= `ALU_OPERATION_SUB;
	
			/** the other instructions don't need the ALU **/
			default:
				currentInstructionAluOperation <= 3'bxxx;
	
		endcase
	end
	
	/** determine ALU comparison operation and signed-ness **/
	always @(*) begin
		casez (currentInstructionOpcode[3:0])
	
			/** BEQ **/
			4'b0000: begin
				currentInstructionSignedComparison <= 1'bx;
				currentInstructionComparisonOperation <= `ALU_COMPARISON_EQUAL;
			end
	
			/** BNE **/
			4'b0001: begin
				currentInstructionSignedComparison <= 1'bx;
				currentInstructionComparisonOperation <= `ALU_COMPARISON_NOT_EQUAL;
			end
			
			/** BLE **/
			4'b0010: begin
				currentInstructionSignedComparison <= 1'b1;
				currentInstructionComparisonOperation <= `ALU_COMPARISON_LESS_EQUAL;
			end
	
			/** BLEU **/
			4'b0011: begin
				currentInstructionSignedComparison <= 1'b0;
				currentInstructionComparisonOperation <= `ALU_COMPARISON_LESS_EQUAL;
			end
			
			/** BLT **/
			4'b0100: begin
				currentInstructionSignedComparison <= 1'b1;
				currentInstructionComparisonOperation <= `ALU_COMPARISON_LESS_THAN;
			end
	
			/** BLTU **/
			4'b0101: begin
				currentInstructionSignedComparison <= 1'b0;
				currentInstructionComparisonOperation <= `ALU_COMPARISON_LESS_THAN;
			end
			
			/** BGE **/
			4'b0110: begin
				currentInstructionSignedComparison <= 1'b1;
				currentInstructionComparisonOperation <= `ALU_COMPARISON_GREATER_EQUAL;
			end
	
			/** BGEU **/
			4'b0111: begin
				currentInstructionSignedComparison <= 1'b0;
				currentInstructionComparisonOperation <= `ALU_COMPARISON_GREATER_EQUAL;
			end
		
			/** BGT **/
			4'b1000: begin
				currentInstructionSignedComparison <= 1'b1;
				currentInstructionComparisonOperation <= `ALU_COMPARISON_GREATER_THAN;
			end
	
			/** BGTU **/
			4'b1001: begin
				currentInstructionSignedComparison <= 1'b0;
				currentInstructionComparisonOperation <= `ALU_COMPARISON_GREATER_THAN;
			end
			
			default: begin
				currentInstructionSignedComparison <= 1'bx;
				currentInstructionComparisonOperation <= 3'bxxx;
			end
			
		endcase
	end

	/** determine whether the current bus operation is a load or store **/
	always @(*) begin
		casez (currentInstructionOpcode)
	
			6'b1100zz, 6'b110100: begin
				currentInstructionLoadStoreIsStore <= 1'b0;
			end
			
			6'b110101, 6'b11011?: begin
				currentInstructionLoadStoreIsStore <= 1'b1;
			end
			
			default: begin
				currentInstructionLoadStoreIsStore <= 1'bx;
			end
	
		endcase
	end

	/** determine the bus size of the current bus operation **/
	always @(*) begin
		casez (currentInstructionOpcode)
	
			6'h30, 6'h35:
				currentInstructionBusSize <= `BUS_SIZE_WORD;
			
			6'h31, 6'h32, 6'h36:
				currentInstructionBusSize <= `BUS_SIZE_HALFWORD;
			
			6'h33, 6'h34, 6'h37:
				currentInstructionBusSize <= `BUS_SIZE_BYTE;
	
			default: begin
				currentInstructionBusSize <= 2'bxx;
			end
	
		endcase
	end

	/** determine the extension mode for the current bus operation **/
	assign currentInstructionExtendMode = currentInstructionOpcode[0] ? `EXTEND_MODE_SIGN : `EXTEND_MODE_ZERO;

endmodule
