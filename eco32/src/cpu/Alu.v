`default_nettype none
`timescale 1ns / 1ps

/**
 * 32-bit purely combinatorial ALU. This module supports the operations and comparisons
 * defined in AluConstants.inc.v.
 *
 * Operation, left and right operators are needed for any operation. For non-comparison
 * operations, the result is computed from these values in a purely combinatorial way.
 *
 * For comparison operations, set the regular operation mode to ALU_OPERATION_SUB and
 * assert the comparison operation and signed-ness. The resulting comparison truth
 * value is computed in a purely combinatorial way. The comparison results are
 * undefined if another operation than ALU_OPERATION_SUB is used.
 */
module Alu (
		
		/** the operation to perform (ALU_OPERATION_SUB for comparison) **/
		input[2:0] operation,
		
		/** the left operand **/
		input[31:0] leftOperand,
		
		/** the right operand **/
		input[31:0] rightOperand,
		
		/** whether comparison works in signed mode (1) or unsigned mode (0). Has no effect on non-comparison operations. **/
		input signedComparison,
		
		/** the comparison operation to perform **/
		input[2:0] comparisonOperation,
		
		/** the computed result (computed in a purely combinatorial way) **/
		output[31:0] result,
		
		/** the comparison result (1 for true, 0 for false; computed in a purely combinatorial way) **/
		output reg comparisonResult

	);

	/****************************************************************************/
	/* helper signals */
	/****************************************************************************/

	/**
	 * We extend the operands to 33 bits to detect overflow as well as
	 * the sign of the result. The extension bit (bit 32) is used only
	 * for comparison operations.
	 */

	/** extend the left operand to 33 bits **/
	reg leftOperandExtensionBit;
	always @(*) begin
		if (signedComparison) begin
			leftOperandExtensionBit <= leftOperand[31];
		end else begin
			leftOperandExtensionBit <= 1'b0;
		end
	end
	wire[32:0] extendedLeftOperand = {leftOperandExtensionBit, leftOperand};
	
	/** extend the right operand to 33 bits **/
	reg rightOperandExtensionBit;
	always @(*) begin
		if (signedComparison) begin
			rightOperandExtensionBit <= rightOperand[31];
		end else begin
			rightOperandExtensionBit <= 1'b0;
		end
	end
	wire[32:0] extendedRightOperand = {rightOperandExtensionBit, rightOperand};

	/****************************************************************************/
	/* computation */
	/****************************************************************************/

	/**
	 * Compute the 33-bit operation result.
	 */
	reg[32:0] extendedResult;
	always @(*) begin
		case (operation)
			`ALU_OPERATION_ADD:
				extendedResult <= extendedLeftOperand + extendedRightOperand;
			`ALU_OPERATION_SUB:
				extendedResult <= extendedLeftOperand - extendedRightOperand;
			`ALU_OPERATION_HIGH:
				extendedResult <= {1'bx, extendedRightOperand[15:0], 16'h0000};
			`ALU_OPERATION_XOR:
				extendedResult <= extendedLeftOperand ^ extendedRightOperand;
			`ALU_OPERATION_XNOR:
				extendedResult <= extendedLeftOperand ~^ extendedRightOperand;
			`ALU_OPERATION_AND:
				extendedResult <= extendedLeftOperand & extendedRightOperand;
			`ALU_OPERATION_OR:
				extendedResult <= extendedLeftOperand | extendedRightOperand;
			default:
				extendedResult <=33'bxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx;
		endcase
	end
	
	/**
	 * Normal computation requires only bits 31..0. Bit 32 is used for
	 * comparison only.
	 */
	assign result = extendedResult[31:0];

	/****************************************************************************/
	/* comparison */
	/****************************************************************************/

	/** check if the operands are equal **/
	wire operandsEqual = (leftOperand == rightOperand);

	/** compute the comparison result **/
	always @(*) begin
		case (comparisonOperation)
			`ALU_COMPARISON_EQUAL:
				comparisonResult <= operandsEqual;
			`ALU_COMPARISON_NOT_EQUAL:
				comparisonResult <= ~operandsEqual;
			`ALU_COMPARISON_LESS_THAN:
				comparisonResult <= extendedResult[32];
			`ALU_COMPARISON_LESS_EQUAL:
				comparisonResult <= operandsEqual | extendedResult[32];
			`ALU_COMPARISON_GREATER_THAN:
				comparisonResult <= ~operandsEqual & ~extendedResult[32];
			`ALU_COMPARISON_GREATER_EQUAL:
				comparisonResult <= ~extendedResult[32];
			default:
				comparisonResult <= 1'bx;
		endcase
	end

endmodule
