
/**
 * The number of selector bits for the TLB index register data source MUX.
 */
`define TLB_INDEX_DATA_SOURCE_WIDTH			1

/**
 * This value selects the explicit special register write value
 * as the data source for the TLB index register. It is used to
 * implement the MVTS(1) instruction.
 */
`define TLB_INDEX_DATA_SOURCE_EXPLICIT		1'b0

/**
 * This value selects the index determined by MMU address mapping
 * as the data source for the TLB index register. It is used to
 * implement the TBS instruction.
 *
 * Note that this selector value does not itself implement full TBS.
 * It uses the virtual address supplied by the main state machine
 * as the address to search, while TBS uses the value of the TLB
 * entry high register. Therefore, to implement TBS, the main state
 * machine must first load the TLB entry high value into its virtual
 * address register before selecting this data source.
 */
`define TLB_INDEX_DATA_SOURCE_SEARCH		1'b1

/**
 * The number of selector bits for the TLB entry high register data source MUX.
 */
`define TLB_ENTRY_HIGH_DATA_SOURCE_WIDTH				2

/**
 * This value selects the explicit special register write value
 * as the data source for the TLB entry high register. It is used to
 * implement the MVTS(2) instruction.
 */
`define TLB_ENTRY_HIGH_DATA_SOURCE_EXPLICIT				2'b00

/**
 * This value selects the 20 highest-order bits of the input virtual
 * address of the MMU as the data source for the TLB entry high register.
 * It is used to implement the TLB miss, TLB invalid, and TLB write exceptions,
 * assuming that the faulting virtual address is asserted.
 */
`define TLB_ENTRY_HIGH_DATA_SOURCE_VIRTUAL_ADDRESS		2'b01

/**
 * This value selects the entry read from the TLB key memory as the
 * data source for the TLB entry high register. It is used to implement
 * the TBRI instruction.
 */
`define TLB_ENTRY_HIGH_DATA_SOURCE_READ_ENTRY			2'b10

/**
 * The number of selector bits for the TLB entry low register data source MUX.
 */
`define TLB_ENTRY_LOW_DATA_SOURCE_WIDTH					2

/**
 * This value selects the explicit special register write value
 * as the data source for the TLB entry low register. It is used to
 * implement the MVTS(3) instruction.
 */
`define TLB_ENTRY_LOW_DATA_SOURCE_EXPLICIT				2'b00

/**
 * This value selects the TLB lookup result value as the data source for the
 * TLB entry low register. It is used to implement the TLB invalid and TLB
 * write exceptions, assuming that the faulting virtual address has been
 * asserted in the previous clock cycle such that the correct TLB lookup
 * result value is available.
 */
`define TLB_ENTRY_LOW_DATA_SOURCE_TLB_VALUE				2'b01

/**
 * This value selects the entry read from the TLB value memory as the
 * data source for the TLB entry low register. It is used to implement
 * the TBRI instruction.
 */
`define TLB_ENTRY_LOW_DATA_SOURCE_READ_ENTRY			2'b10

/**
 * The number of selector bits for the TLB bad address register data source MUX.
 */
`define TLB_BAD_ADDRESS_DATA_SOURCE_WIDTH					1

/**
 * This value selects the explicit special register write value
 * as the data source for the TLB bad address register. It is used to
 * implement the MVTS(4) instruction.
 */
`define TLB_BAD_ADDRESS_DATA_SOURCE_EXPLICIT				1'b0

/**
 * This value selects the input virtual address of the MMU as the data source
 * for the TLB bad address register. It is used to implement the TLB miss,
 * TLB invalid, TLB write, illegal address, and privileged address exceptions,
 * assuming that the faulting virtual address is asserted.
 */
`define TLB_BAD_ADDRESS_DATA_SOURCE_VIRTUAL_ADDRESS			1'b1
