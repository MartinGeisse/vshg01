
/**
 * The number of bits for ALU operation codes
 */
`define ALU_OPERATION_WIDTH		3

/**
 * ALU operation: add
 */
`define ALU_OPERATION_ADD		3'b000

/**
 * ALU operation: subtract
 */
`define ALU_OPERATION_SUB		3'b001

/**
 * ALU operation: take the low 16 bits of the right operand as the high 16 bits
 * of the result. The low 16 bits of the result are 0.
 */
`define ALU_OPERATION_HIGH		3'b011

/**
 * ALU operation: XOR
 */
`define ALU_OPERATION_XOR		3'b100

/**
 * ALU operation: XNOR
 */
`define ALU_OPERATION_XNOR		3'b101

/**
 * ALU operation: AND
 */
`define ALU_OPERATION_AND		3'b110

/**
 * ALU operation: OR
 */
`define ALU_OPERATION_OR		3'b111

/**
 * ALU operation is undefined
 */
`define ALU_OPERATION_UNDEFINED	3'bxxx

/**
 * The number of bits for ALU comparison codes
 */
`define ALU_COMPARISON_WIDTH			3

/**
 * ALU comparison mode: EQUAL
 */
`define ALU_COMPARISON_EQUAL			3'b000

/**
 * ALU comparison mode: NOT EQUAL
 */
`define ALU_COMPARISON_NOT_EQUAL		3'b100

/**
 * ALU comparison mode: LESS THAN
 */
`define ALU_COMPARISON_LESS_THAN		3'b001

/**
 * ALU comparison mode: LESS THAN OR EQUAL
 */
`define ALU_COMPARISON_LESS_EQUAL		3'b101

/**
 * ALU comparison mode: GREATER THAN
 */
`define ALU_COMPARISON_GREATER_THAN		3'b010

/**
 * ALU comparison mode: GREATER THAN OR EQUAL
 */
`define ALU_COMPARISON_GREATER_EQUAL	3'b110

/**
 * ALU comparison mode is undefined
 */
`define ALU_COMPARISON_UNDEFINED		3'bxxx
