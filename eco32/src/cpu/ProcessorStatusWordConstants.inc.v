
/**
 * The number of selector bits for the PSW write data MUX.
 */
`define PSW_WRITE_DATA_SOURCE_WIDTH				2

/**
 * This value specifies that the PSW shall be loaded with an
 * explicitly provided value.
 */
`define PSW_WRITE_DATA_SOURCE_EXPLICIT			2'b00

/**
 * This value specifies that the PSW shall be transformed for
 * exception handler entry using the specified exception priority.
 */
`define PSW_WRITE_DATA_SOURCE_HANDLER_ENTRY		2'b01

/**
 * This value specifies that the PSW shall be transformed for
 * exception handler exit.
 */
`define PSW_WRITE_DATA_SOURCE_HANDLER_EXIT		2'b10

/**
 * This value specifies that the PSW write value is undefined.
 */
`define PSW_WRITE_DATA_SOURCE_UNDEFINED			2'bxx
