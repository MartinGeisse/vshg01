`default_nettype none
`timescale 1ns / 1ps

/**
 * 32-bit / 5-bit synchronous shifter. This shifter supports logical shift left/right
 * and arithmetic shift right operations.
 *
 * This shifter works as follows to reduce combinatorial delay: After the operands and
 * operation have been asserted, it takes a rather long setup time and one clock edge
 * to compute an intermediate result. After that edge, it takes again a rather long
 * clock-to-out delay to compute the final result, but not a second clock edge. Thus,
 * an RTL design would treat this shifter as if it simply provided its result at the
 * first clock edge, but avoid additional combinatorial delays at the inputs and outputs.
 *
 * The operands and operation must remain unchanged in both cycles.
 */
module Shifter (
		
		/** the clock **/
		input clock,
		
		/** the operation to perform **/
		input[1:0] operation,
		
		/** the left operand **/
		input[31:0] leftOperand,
		
		/** the right operand **/
		input[4:0] rightOperand,
		
		/** the computed result **/
		output reg[31:0] result

	);

	/**
	 * Compute the intermediate result synchronously. We shift the left operand by
	 * an amount as if bits 3..0 of the right operand were zero and respect only
	 * bit 4.
	 */
	reg[31:0] temporaryResult;
	always @(posedge clock) begin
	
		if (rightOperand[4]) begin

			/** bit 4 is set, so shift by 16 positions **/
			case (operation)
			
				/** logical shift left **/
				3'b00: begin
					temporaryResult <= {leftOperand[15:0], 16'h0};
				end
				
				/** logical shift right **/
				3'b01: begin
					temporaryResult <= {16'h0, leftOperand[31:16]};
				end
				
				/** arithmetic shift right **/
				3'b10: begin
					temporaryResult <= {{16{leftOperand[31]}}, leftOperand[31:16]};
				end
				
				/** the result is undefined for any other operation code **/
				default: begin
					temporaryResult <= 32'bxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx;
				end
				
			endcase

		end else begin
		
			/** if bit 4 is zero, we have nothing to do **/
			temporaryResult <= leftOperand;
			
		end
		
	end

	/**
	 * Compute the final result asynchronously. We shift the intermediate
	 * result by an amount as if bit 4 of the right operand was zero
	 * and respect only bits 3..0.
	 */
	always @(*) begin
		case (operation)

			/** logical shift left **/
			3'b00: begin
				result <= (temporaryResult << rightOperand[3:0]);
			end

			/** logical shift right **/
			3'b01: begin
				result <= (temporaryResult >> rightOperand[3:0]);
			end

			/** arithmetic shift right **/
			3'b10: begin
				if (temporaryResult[31]) begin
					result <= ~(32'hffffffff >> rightOperand[3:0]) | (temporaryResult >> rightOperand[3:0]);
				end else begin
					result <= (temporaryResult >> rightOperand[3:0]);
				end
			end

			/** the result is undefined for any other operation code **/
			default: begin
				result <= 32'bxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx;
			end
			
		endcase
	end

endmodule
