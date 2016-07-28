
/**
 * The number of selector bits for the PC register data MUX.
 */
`define PC_SOURCE_WIDTH							3

/**
 * This value specifies that the PC shall be incremented by 4.
 */
`define PC_SOURCE_INCREMENT						3'b000

/**
 * This value specifies that the PC shall be incremented by the contents of the extended immediate operand.
 */
`define PC_SOURCE_ADD_IMMEDIATE					3'b001

/**
 * This value specifies that the PC shall be incremented by the contents of the extended jump offset.
 */
`define PC_SOURCE_ADD_OFFSET					3'b010

/**
 * This value specifies that the PC shall be set to an explicitly supplied value.
 */
`define PC_SOURCE_EXPLICIT						3'b011

/**
 * This value specifies that the PC shall be set to the general exception handler entry point
 */
`define PC_SOURCE_EXCEPTION						3'b100

/**
 * This value specifies that the PC shall be set to the user TLB miss handler entry point
 */
`define PC_SOURCE_USER_TLB_MISS					3'b101

/**
 * This value specifies that the PC write value is undefined.
 */
`define PC_SOURCE_UNDEFINED						3'bxxx
