
/** the number of bits for the shifter operation codes **/
`define SHIFTER_OPERATION_WIDTH						2

/** shift left and fill with zero bits **/
`define SHIFTER_OPERATION_LOGICAL_SHIFT_LEFT		2'b00

/** shift right and fill with zero bits **/
`define SHIFTER_OPERATION_LOGICAL_SHIFT_RIGHT		2'b01

/** shift right and replicate the sign bit **/
`define SHIFTER_OPERATION_ARITHMETIC_SHIFT_RIGHT	2'b10
