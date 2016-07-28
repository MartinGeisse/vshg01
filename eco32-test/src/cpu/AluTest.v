`default_nettype none
`timescale 1ns / 1ps

/**
 * Testbench for the ALU.
 */
module AluTest ();

	/** includes **/
	`include "VUnit.inc.v"
	`include "Clock.inc.v"

	/** include constant definitions **/
	`include "../../eco32/src/cpu/AluConstants.inc.v"

	/** the module under test **/
	reg[2:0] aluOperationRegister;
	reg[31:0] aluLeftOperand;
	reg[31:0] aluRightOperand;
	wire[31:0] aluResult;
	reg aluSignedComparison;
	reg[2:0] aluComparisonOperation;
	wire aluComparisonResult;
	Alu alu (
		.operation(aluOperationRegister),
		.leftOperand(aluLeftOperand),
		.rightOperand(aluRightOperand),
		.signedComparison(aluSignedComparison),
		.comparisonOperation(aluComparisonOperation),
		.result(aluResult),
		.comparisonResult(aluComparisonResult)
	);
	
	/** simulation **/
	initial begin
		$dumpfile("cpu/AluTest.vcd");
		$dumpvars;
		
		/** initalization **/
		aluSignedComparison <= 1'b1;

		/** test add **/
		aluOperationRegister <= `ALU_OPERATION_ADD;
		aluLeftOperand <= 32'h0472b8af;
		aluRightOperand <= 32'h39a01111;
		#1 assert(aluResult == 32'h3e12c9c0, "add 1");

		aluOperationRegister <= `ALU_OPERATION_ADD;
		aluLeftOperand <= 32'hc0000000;
		aluRightOperand <= 32'h50000000;
		#1 assert(aluResult == 32'h10000000, "add 2");
		
		/** test sub **/
		aluOperationRegister <= `ALU_OPERATION_SUB;
		aluLeftOperand <= 32'h39a01111;
		aluRightOperand <= 32'h0472b8af;
		#1 assert(aluResult == 32'h352d5862, "sub 1");

		aluOperationRegister <= `ALU_OPERATION_SUB;
		aluLeftOperand <= 32'h10000000;
		aluRightOperand <= 32'h20000000;
		#1 assert(aluResult == 32'hf0000000, "sub 2");

		/** test high **/
		aluOperationRegister <= `ALU_OPERATION_HIGH;
		aluLeftOperand <= 32'h39a01111;
		aluRightOperand <= 32'h0472b8af;
		#1 assert(aluResult == 32'hb8af0000, "high");

		/** test xor **/
		aluOperationRegister <= `ALU_OPERATION_XOR;
		aluLeftOperand <= 32'h39a01111;
		aluRightOperand <= 32'h0472b8af;
		#1 assert(aluResult == 32'h3dd2a9be, "xor");
		
		/** test xnor **/
		aluOperationRegister <= `ALU_OPERATION_XNOR;
		aluLeftOperand <= 32'h39a01111;
		aluRightOperand <= 32'h0472b8af;
		#1 assert(aluResult == 32'hc22d5641, "xnor");
		
		/** test and **/
		aluOperationRegister <= `ALU_OPERATION_AND;
		aluLeftOperand <= 32'h39a01111;
		aluRightOperand <= 32'h0472b8af;
		#1 assert(aluResult == 32'h00201001, "and");

		/** test or **/
		aluOperationRegister <= `ALU_OPERATION_OR;
		aluLeftOperand <= 32'h39a01111;
		aluRightOperand <= 32'h0472b8af;
		#1 assert(aluResult == 32'h3df2b9bf, "or");

		/** test equality comparison **/
		aluOperationRegister <= `ALU_OPERATION_SUB;
		aluComparisonOperation <= `ALU_COMPARISON_EQUAL;
		aluLeftOperand <= 32'h39a01111;
		aluRightOperand <= 32'h0472b8af;
		aluSignedComparison <= 1'b0;
		#1 assert(~aluComparisonResult, "unsigned equal no");
		aluSignedComparison <= 1'b1;
		#1 assert(~aluComparisonResult, "signed equal no");
		aluRightOperand <= 32'h39a01111;
		aluSignedComparison <= 1'b0;
		#1 assert(aluComparisonResult, "unsigned equal yes");
		aluSignedComparison <= 1'b1;
		#1 assert(aluComparisonResult, "signed equal yes");

		/** test inequality comparison **/
		aluOperationRegister <= `ALU_OPERATION_SUB;
		aluComparisonOperation <= `ALU_COMPARISON_NOT_EQUAL;
		aluLeftOperand <= 32'h39a01111;
		aluRightOperand <= 32'h0472b8af;
		aluSignedComparison <= 1'b0;
		#1 assert(aluComparisonResult, "unsigned not equal no");
		aluSignedComparison <= 1'b1;
		#1 assert(aluComparisonResult, "signed not equal no");
		aluRightOperand <= 32'h39a01111;
		aluSignedComparison <= 1'b0;
		#1 assert(~aluComparisonResult, "unsigned not equal yes");
		aluSignedComparison <= 1'b1;
		#1 assert(~aluComparisonResult, "signed not equal yes");

		/** test less-than comparison **/
		aluOperationRegister <= `ALU_OPERATION_SUB;
		aluComparisonOperation <= `ALU_COMPARISON_LESS_THAN;
		
		aluLeftOperand <= 32'h39a01111;
		aluRightOperand <= 32'h0472b8af;
		aluSignedComparison <= 1'b0;
		#1 assert(~aluComparisonResult, "unsigned less than (1)");
		aluSignedComparison <= 1'b1;
		#1 assert(~aluComparisonResult, "signed less than (1)");

		aluLeftOperand <= 32'h0472b8af;
		aluRightOperand <= 32'h39a01111;
		aluSignedComparison <= 1'b0;
		#1 assert(aluComparisonResult, "unsigned less than (2)");
		aluSignedComparison <= 1'b1;
		#1 assert(aluComparisonResult, "signed less than (2)");

		aluLeftOperand <= 32'h0472b8af;
		aluRightOperand <= 32'hc9a01111;
		aluSignedComparison <= 1'b0;
		#1 assert(aluComparisonResult, "unsigned less than (3)");
		aluSignedComparison <= 1'b1;
		#1 assert(~aluComparisonResult, "signed less than (3)");

		aluLeftOperand <= 32'hb472b8af;
		aluRightOperand <= 32'hc9a01111;
		aluSignedComparison <= 1'b0;
		#1 assert(aluComparisonResult, "unsigned less than (4)");
		aluSignedComparison <= 1'b1;
		#1 assert(aluComparisonResult, "signed less than (4)");

		aluLeftOperand <= 32'hc0000000;
		aluRightOperand <= 32'hc0000000;
		aluSignedComparison <= 1'b0;
		#1 assert(~aluComparisonResult, "unsigned less than (4)");
		aluSignedComparison <= 1'b1;
		#1 assert(~aluComparisonResult, "signed less than (4)");

		/** test less-equal comparison **/
		aluOperationRegister <= `ALU_OPERATION_SUB;
		aluComparisonOperation <= `ALU_COMPARISON_LESS_EQUAL;
		
		aluLeftOperand <= 32'h39a01111;
		aluRightOperand <= 32'h0472b8af;
		aluSignedComparison <= 1'b0;
		#1 assert(~aluComparisonResult, "unsigned less or equal (1)");
		aluSignedComparison <= 1'b1;
		#1 assert(~aluComparisonResult, "signed less or equal (1)");

		aluLeftOperand <= 32'h0472b8af;
		aluRightOperand <= 32'h39a01111;
		aluSignedComparison <= 1'b0;
		#1 assert(aluComparisonResult, "unsigned less or equal (2)");
		aluSignedComparison <= 1'b1;
		#1 assert(aluComparisonResult, "signed less or equal (2)");

		aluLeftOperand <= 32'h0472b8af;
		aluRightOperand <= 32'hc9a01111;
		aluSignedComparison <= 1'b0;
		#1 assert(aluComparisonResult, "unsigned less or equal (3)");
		aluSignedComparison <= 1'b1;
		#1 assert(~aluComparisonResult, "signed less or equal (3)");

		aluLeftOperand <= 32'hb472b8af;
		aluRightOperand <= 32'hc9a01111;
		aluSignedComparison <= 1'b0;
		#1 assert(aluComparisonResult, "unsigned less or equal (4)");
		aluSignedComparison <= 1'b1;
		#1 assert(aluComparisonResult, "signed less or equal (4)");

		aluLeftOperand <= 32'hc0000000;
		aluRightOperand <= 32'hc0000000;
		aluSignedComparison <= 1'b0;
		#1 assert(aluComparisonResult, "unsigned less or equal (4)");
		aluSignedComparison <= 1'b1;
		#1 assert(aluComparisonResult, "signed less or equal (4)");

		/** test greater-than comparison **/
		aluOperationRegister <= `ALU_OPERATION_SUB;
		aluComparisonOperation <= `ALU_COMPARISON_GREATER_THAN;
		
		aluLeftOperand <= 32'h39a01111;
		aluRightOperand <= 32'h0472b8af;
		aluSignedComparison <= 1'b0;
		#1 assert(aluComparisonResult, "unsigned greater than (1)");
		aluSignedComparison <= 1'b1;
		#1 assert(aluComparisonResult, "signed greater than (1)");

		aluLeftOperand <= 32'h0472b8af;
		aluRightOperand <= 32'h39a01111;
		aluSignedComparison <= 1'b0;
		#1 assert(~aluComparisonResult, "unsigned greater than (2)");
		aluSignedComparison <= 1'b1;
		#1 assert(~aluComparisonResult, "signed greater than (2)");

		aluLeftOperand <= 32'h0472b8af;
		aluRightOperand <= 32'hc9a01111;
		aluSignedComparison <= 1'b0;
		#1 assert(~aluComparisonResult, "unsigned greater than (3)");
		aluSignedComparison <= 1'b1;
		#1 assert(aluComparisonResult, "signed greater than (3)");

		aluLeftOperand <= 32'hb472b8af;
		aluRightOperand <= 32'hc9a01111;
		aluSignedComparison <= 1'b0;
		#1 assert(~aluComparisonResult, "unsigned greater than (4)");
		aluSignedComparison <= 1'b1;
		#1 assert(~aluComparisonResult, "signed greater than (4)");

		aluLeftOperand <= 32'hc0000000;
		aluRightOperand <= 32'hc0000000;
		aluSignedComparison <= 1'b0;
		#1 assert(~aluComparisonResult, "unsigned greater than (4)");
		aluSignedComparison <= 1'b1;
		#1 assert(~aluComparisonResult, "signed greater than (4)");

		/** test greater-equal comparison **/
		aluOperationRegister <= `ALU_OPERATION_SUB;
		aluComparisonOperation <= `ALU_COMPARISON_GREATER_EQUAL;
		
		aluLeftOperand <= 32'h39a01111;
		aluRightOperand <= 32'h0472b8af;
		aluSignedComparison <= 1'b0;
		#1 assert(aluComparisonResult, "unsigned greater or equal (1)");
		aluSignedComparison <= 1'b1;
		#1 assert(aluComparisonResult, "signed greater or equal (1)");

		aluLeftOperand <= 32'h0472b8af;
		aluRightOperand <= 32'h39a01111;
		aluSignedComparison <= 1'b0;
		#1 assert(~aluComparisonResult, "unsigned greater or equal (2)");
		aluSignedComparison <= 1'b1;
		#1 assert(~aluComparisonResult, "signed greater or equal (2)");

		aluLeftOperand <= 32'h0472b8af;
		aluRightOperand <= 32'hc9a01111;
		aluSignedComparison <= 1'b0;
		#1 assert(~aluComparisonResult, "unsigned greater or equal (3)");
		aluSignedComparison <= 1'b1;
		#1 assert(aluComparisonResult, "signed greater or equal (3)");

		aluLeftOperand <= 32'hb472b8af;
		aluRightOperand <= 32'hc9a01111;
		aluSignedComparison <= 1'b0;
		#1 assert(~aluComparisonResult, "unsigned greater or equal (4)");
		aluSignedComparison <= 1'b1;
		#1 assert(~aluComparisonResult, "signed greater or equal (4)");

		aluLeftOperand <= 32'hc0000000;
		aluRightOperand <= 32'hc0000000;
		aluSignedComparison <= 1'b0;
		#1 assert(aluComparisonResult, "unsigned greater or equal (4)");
		aluSignedComparison <= 1'b1;
		#1 assert(aluComparisonResult, "signed greater or equal (4)");
		
		
		/** done **/
		#100 $finish;
	end
		
endmodule
