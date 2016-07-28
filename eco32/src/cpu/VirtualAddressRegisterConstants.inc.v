
/**
 * The number of selector bits for the virtual address register data MUX
 */
`define VIRTUAL_ADDRESS_REGISTER_WRITE_DATA_SOURCE_WIDTH							2

/**
 * This value selects the PC as the data source for the virtual address register.
 */
`define VIRTUAL_ADDRESS_REGISTER_WRITE_DATA_SOURCE_PC							2'b00

/**
 * This value selects the address computation result as the data source for the
 * virtual address register. This result is obtained from the ALU result.
 */
`define VIRTUAL_ADDRESS_REGISTER_WRITE_DATA_SOURCE_ADDRESS_COMPUTATION_RESULT	2'b01

/**
 * This value selects the special register read data from the MMU as the data
 * source for the virtual address register. This mode is required to implement
 * the TBS instruction.
 */
`define VIRTUAL_ADDRESS_REGISTER_WRITE_DATA_SOURCE_MMU_SPECIAL_REGISTER_READ_DATA	2'b10

/**
 * This value specifies that the virtual address data source is undefined.
 */
`define VIRTUAL_ADDRESS_REGISTER_WRITE_DATA_SOURCE_UNDEFINED						2'bxx

