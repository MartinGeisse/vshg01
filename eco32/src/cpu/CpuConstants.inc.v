
/**
 * This value is specified by the SoC bus and selects a byte-sized bus access.
 */
`define BUS_SIZE_BYTE		2'b00

/**
 * This value is specified by the SoC bus and selects a halfword-sized bus access.
 */
`define BUS_SIZE_HALFWORD	2'b01

/**
 * This value is specified by the SoC bus and selects a word-sized bus access.
 */
`define BUS_SIZE_WORD		2'b10

/**
 * This value leaves the bus access size undefined.
 */
`define BUS_SIZE_UNDEFINED	2'bxx



/**
 * This value is used internally to denote zero-extension of a value to a wider vector size.
 */
`define EXTEND_MODE_ZERO		1'b0

/**
 * This value is used internally to denote sign-extension of a value to a wider vector size.
 */
`define EXTEND_MODE_SIGN		1'b1
/**
 * This value leaves the extension mode undefined
 */
`define EXTEND_MODE_UNDEFINED	1'bx



/**
 * The width of the CPU state register.
 */
`define STATEREG_WIDTH		5

/**
 * This CPU state is entered on reset. It is left immediately and
 * entered again only on the next reset. Its purpose is to
 * initialize the CPU state, so only the most important state
 * variables need to be affected by the reset signal directly
 * in order to avoid unwanted external effects on reset.
 */
`define STATE_RESET		5'd0

/**
 * This CPU state is entered directly after reset, after entering
 * a system event handler (interrupt, exception, or user TLB miss),
 * or when the previous instruction has been completed. Its purpose
 * is to provide a central entry point to begin a new instruction
 * cycle. Any state can make a transition to this one and not care
 * about temporary (intra-instruction) registers -- all of those are
 * initialized by this state.
 *
 * Although this state performs no real work and could be optimized away,
 * doing so would clutter the code, since the check for interrupts would
 * have to be placed into each "final" state that otherwise just leads to
 * this state. It would also mean going a step towards pipelining,
 * which should rather be implemented properly and in a planned way.
 *
 * This state is the only state that is able to sense interrupts and
 * react to them. If no interrupt is raised, the next action is to translate
 * the PC to a physical address for instruction fetching. Note that it makes
 * sense performance-wise not to put too much CPU logic into this state since
 * the next transition already depends on finding active interrupts and
 * choosing one of them.
 */
`define STATE_BEGIN_INSTRUCTION		5'd1

/**
 * This state is active while the MMU is translating the PC to a physical
 * address for instruction fetching. Since the MMU loads its results into
 * registers, they are not directly interpreted before the clock edge that
 * leaves this state, but afterwards.
 */
`define STATE_TRANSLATE_INSTRUCTION_ADDRESS		5'd2

/**
 * This state is active after the MMU has translated the PC to a physical
 * address. It can result in a valid physical address to fetch an instruction
 * from, an illegal address exception, privileged address exception,
 * TLB miss exception, or TLB invalid exception.
 */
`define STATE_INTERPRET_TRANSLATED_INSTRUCTION_ADDRESS		5'd3
 
/**
 * This CPU state is active while the bus is fetching the next instruction.
 * As soon as the bus is able to provide the instruction -- i.e. when
 * the busWait signal is cleared -- the next clock edge causes the CPU
 * to store the instruction and leave this state. If the bus is unable to
 * provide a value in time -- as determined by the bus timeout counter --
 * then a bus timeout exception occurs.
 */
`define STATE_FETCH_INSTRUCTION		5'd4

/**
 * This CPU state is active while the CPU is busy decoding a fetched instruction.
 * Decoding takes only a single clock cycle. During this clock cycle, CPU logic
 * analyzes the instruction and decides about the next state to enter and
 * about values to write to temporary (intra-instruction) registers. It also
 * uses the register indices encoded into the instruction to load operand values
 * from the general-purpose register file.
 *
 * As a special case of decoding, this instruction may result in an
 * illegal instruction exception or privileged instruction exception.
 */
`define STATE_DECODE_INSTRUCTION	5'd5

/**
 * This CPU state is active when the CPU computes the result of an ALU operator
 * instruction and waits for the ALU to complete its work. The clock edge that
 * finishes this state writes the ALU result to the destination register and
 * finishes the instruction cycle.
 */
`define STATE_COMPUTE_ALU_OPERATOR		5'd6

/**
 * This CPU state is active in the first cycle of computing the result of a shift
 * operator.
 */
`define STATE_COMPUTE_SHIFT_OPERATOR	5'd7

/**
 * This CPU state is active in the second cycle of computing the result of a shift
 * operator. The clock edge that finishes this state writes the shift result to the
 * destination register and finishes the instruction cycle.
 */
`define STATE_COMPUTE_SHIFT_OPERATOR_CONTINUED		5'd8

/**
 * This CPU state is used during the computation of a multiplication operator. 
 * This state lasts a single cycle, after which the result of the operator is written
 * to the destination register. That edge also finishes the instruction cycle.
 */
`define STATE_COMPUTE_MULTIPLICATION_OPERATOR		5'd9

/**
 * This CPU state is used for the first clock cycle of the computation of a division
 * or remainder operator. This state is left at the first clock edge. 
 * During this state, the divider input signals are valid to activate a new division
 * operation. The dividerDone signal is undefined in this state.
 */
`define STATE_INITIALIZE_DIVISION_REMAINDER_OPERATOR		5'd10

/**
 * This CPU state is used during the computation of a division
 * or remainder operator. This state is left at the first clock edge at which the
 * divide unit is finished. At this point, the CPU checks for a division by zero
 * and possibly results in an appropriate exception. Otherwise, the result of the
 * operator is written to the destination register and the instruction cycle is finished.
 */
`define STATE_CONTINUE_DIVISION_REMAINDER_OPERATOR			5'd11

/**
 * This CPU state is active when the CPU computes the result of a branch instruction
 * condition and waits for the ALU to complete its work. At the same time, the immediate
 * sign-extended branch offset is added to the PC to obtain the branch target. At the
 * first clock edge, the branch target is possibly written to the PC (depending on the
 * outcome of the condition logic) and the instruction cycle is finished.
 */
`define STATE_COMPUTE_BRANCH		5'd12

/**
 * This CPU state is active when the CPU computes the target address of a load or store
 * instruction and waits for the ALU to complete its work. The clock edge that finishes
 * this state signals that the virtual address to use is ready, and can be translated
 * to a physcial address.
 */
`define STATE_COMPUTE_LOAD_STORE_ADDRESS		5'd13

/**
 * This state is active while the MMU is translating the load/store address
 * to a physical address. Since the MMU loads its results into registers, they are
 * not directly interpreted before the clock edge that leaves this state,
 * but afterwards.
 */
`define STATE_TRANSLATE_LOAD_STORE_ADDRESS		5'd14

/**
 * This state is active after the MMU has translated the load/store address
 * to a physical address. It can result in a valid physical address to load from
 * or store to, an illegal address exception, privileged address exception,
 * TLB miss exception TLB invalid exception, or TLB write exception.
 */
`define STATE_INTERPRET_TRANSLATED_LOAD_STORE_ADDRESS		5'd15

/**
 * This state is active while the bus is busy with a load or store operation.
 * When the bus is finished -- i.e. at the first clock edge where busWait is 0 --
 * the CPU finishes the store instruction cycle (for store instructions) or saves
 * the busReadValue to the rawDataInRegister and prepares to extend it to 32 bits
 * (for load instructions). If the bus is unable to perform the operation in time --
 * as determined by the bus timeout counter -- then a bus timeout exception occurs.
 */
`define STATE_PERFORM_LOAD_STORE		5'd16

/**
 * This state is active while the CPU extends the bus read value to 32 bits.
 * This state ends at the first clock edge by storing the extended value in the
 * destination register. That edge also finishes the instruction cycle.
 */
`define STATE_FINISH_LOAD		5'd17

/**
 * This state is active while the CPU performs a J or JAL instruction. During
 * this state, the ALU computes the jump target address. At the first clock edge,
 * the PC is loaded with that address. For the JAL instruction, the old PC is also
 * written to general purpose register 31. That edge also finishes the instruction
 * cycle.
 */
`define STATE_PERFORM_J_JAL		5'd18

/**
 * This state is active while the CPU performs a JR or JALR instruction. There is
 * no computation going on in this state. At the first clock edge, the PC is loaded
 * with the jump target address. For the JALR instruction, the old PC is also written
 * to general purpose register 31. That edge also finishes the instruction cycle.
 *
 * Note that synchronously writing the new PC and register 31 ensures that "JALR 31"
 * jumps to the old contents of register 31 and also stores the old PC in that
 * register.
 */
`define STATE_PERFORM_JR_JALR		5'd19

/**
 * This state is active while the CPU loads the exception return address from
 * register 30. This is needed since the general purpose register memory only
 * supports synchronous reading.
 */
`define STATE_RFX_LOAD_RETURN_ADDRESS	5'd20

/**
 * This state is active while the CPU performs an RFX instruction. There is no
 * computation going on in this state. At the clock edge that finishes this state,
 * the PC is loaded with the contents of register 30, and the PSW is loaded
 * with an appropriate transformed value. That edge also finishes the instruction
 * cycle.
 */
`define STATE_PERFORM_RFX		5'd21

/**
 * This state is active while the CPU executes a MVFS instruction. No computation
 * occurs in this state. At the first clock edge, the value of the corresponding
 * special register is stored in the destination register and the instruction
 * cycle is finished.
 */
`define STATE_PERFORM_MVFS		5'd22

/**
 * This state is active while the CPU executes a MVTS instruction. No computation
 * occurs in this state. At the first clock edge, the value of the corresponding
 * general-purpose register is stored in the destination register and the instruction
 * cycle is finished.
 */
`define STATE_PERFORM_MVTS		5'd23

/**
 * This state is active while the CPU loads the virtual address for a TBS instruction
 * from the TLB entry high register into the virtual address register. This state lasts
 * a single clock cycle.
 */
`define STATE_TBS_LOAD_VIRTUAL_ADDRESS		5'd24

/**
 * This state is active while the CPU translates the virtual address for a TBS instruction.
 * At the first clock edge, the translation result is stored in the TLB index register
 * and the instruction cycle is finished.
 */
`define STATE_TBS_TRANSLATE_AND_STORE		5'd25

/**
 * This state is active while the CPU executes a TBWR or TBWI instruction. No computation
 * occurs in this state. At the first clock edge, the TLB performs the write operation
 * and the instruction cycle is finished. The tlbEntryRandomizeIndex control signal
 * determines the source of the index of the entry to write, i.e. whether a TBWR or
 * TBWI occurs.
 */
`define STATE_PERFORM_TLB_WRITE 		5'd26

/**
 * This state is active while the CPU reads a TLB entry in an indexed way to perform a
 * TBRI instruction. At the first clock edge, the TLB loads the contents of the value
 * array (it is stored in synchronous memory). The contents of the key array are available
 * both asynchronously and synchronously (since the TLB index register is left unchanged
 * in this state), and the synchronous variant is used.
 */
`define STATE_TBRI_READ_ENTRY			5'd27

/**
 * This state is active while the CPU stores the result of a TBRI instruction in the
 * TLB entry high/low registers.
 */
`define STATE_TBRI_STORE_RESULT			5'd28

/**
 * This state is active when the CPU has detected an interrupt or exception. At the
 * first clock edge, the CPU stores the exception return address in register 30,
 * the handler start address in the PC, and transforms the PSW according to the
 * exception priority. That edge also finishes the instruction cycle.
 *
 * The handler start address is determined by the value of the vector bit in the PSW
 * register as well as by whether a user-space TLB miss or any other exception has occurred.
 *
 * The exception priority field of the PSW is loaded with the contents of the
 * detectedExceptionPriorityRegister (which in turn is loaded with the value of the
 * detectedExceptionPriority on every clock edge). The main state machine must therefore
 * provide an appropriate value for detectedExceptionPriority at the clock edge that
 * enters this state.
 *
 * The register at index 30 is loaded with the value of the exceptionReturnAddress,
 * which in turn is loaded from the PC at the beginning of every instruction.
 * This ensures that an interrupt between two instructions returns to the address
 * immediately following it, and an exception returns to the instruction that caused it.
 * (Note: the TRAP instruction causes an exception that is usually intended to return to
 * the *next* instruction, not to the TRAP instruction itself. Such behavior must be
 * implemented in software.)
 */
`define STATE_EXCEPTION		5'd29

/**
 * This state simply leads to the begin instruction state, and can be used to delay
 * the transition to that state by one clock cycle. This is important for states that
 * finish the current instruction, but have affected the PSW. The extra cycle is needed
 * for the new PSW value to take effect on the interrupt detection logic.
 */
`define STATE_DELAY_NEXT_INSTRUCTION	5'd30

/**
 * This value leaves the state undefined.
 */
`define STATE_UNDEFINED		5'bxxxxx



/**
 * This exception priority code indicates a bus timeout. This usually means that the
 * CPU tried to access a physical address to which no device is attached.
 */
`define EXCEPTION_CODE_BUS_TIMEOUT				5'h10

/**
 * This exception priority code indicates that the opcode does not denote any instruction,
 * but is an unused opcode.
 */
`define EXCEPTION_CODE_ILLEGAL_INSTRUCTION		5'h11

/**
 * This exception priority code indicates that the current instruction can only be executed
 * in kernel mode, but the CPU is in user mode.
 */
`define EXCEPTION_CODE_PRIVILEGED_INSTRUCTION	5'h12

/**
 * This exception priority code indicates a division by zero.
 */
`define EXCEPTION_CODE_DIVISION					5'h13

/**
 * This exception priority code indicates that a trap (syscall) instruction has been executed.
 */
`define EXCEPTION_CODE_TRAP						5'h14

/**
 * This exception priority code indicates a TLB miss, i.e. that the CPU tried to access a
 * TLB-mapped virtual address for which no TLB entry exists.
 */
`define EXCEPTION_CODE_TLB_MISS					5'h15

/**
 * This exception priority code indicates that a TLB entry was found and is valid,
 * but is write-protected and the CPU wants to perform a write operation.
 */
`define EXCEPTION_CODE_TLB_WRITE				5'h16

/**
 * This exception priority code indicates that a TLB entry was found but is marked invalid.
 */
`define EXCEPTION_CODE_TLB_INVALID				5'h17

/**
 * This exception priority code indicates that the CPU tried to perform a read or write
 * operation with an address that is not aligned to the access size.
 */
`define EXCEPTION_CODE_ILLEGAL_ADDRESS			5'h18

/**
 * This exception priority code indicates that the CPU tried to perform a read or write
 * operation with an address that can only be accessed in kernel mode, but the CPU is
 * in user mode.
 */
`define EXCEPTION_CODE_PRIVILEGED_ADDRESS		5'h19 

/**
 * This value leaves the exception code undefined
 */
`define EXCEPTION_CODE_UNDEFINED				5'bxxxxx


/**
 * The number of selector bits for the general purpose register port 2 index MUX.
 */
`define GENERAL_PURPOSE_REGISTERS_INDEX_2_SOURCE_WIDTH						2

/**
 * This value specifies that the index used for general purpose register port 2 is taken
 * from the right operand location of the current instruction.
 */
`define GENERAL_PURPOSE_REGISTERS_INDEX_2_SOURCE_RIGHT_OPERAND			2'b00

/**
 * This value specifies that the index used for general purpose register port 2 is taken
 * from the destination location of the current instruction.
 */
`define GENERAL_PURPOSE_REGISTERS_INDEX_2_SOURCE_DESTINATION			2'b01

/**
 * This value specifies that the index used for general purpose register port 2 is
 * the literal value 30.
 */
`define GENERAL_PURPOSE_REGISTERS_INDEX_2_SOURCE_30						2'b10

/**
 * This value specifies that the index used for general purpose register port 2 is
 * the literal value 31.
 */
`define GENERAL_PURPOSE_REGISTERS_INDEX_2_SOURCE_31						2'b11

/**
 * This value specifies that the index used for general purpose register port
 * 2 is undefined. This implies that neither reading nor writing through that port
 * are supported, i.e. the write-enable must be 0 and the read value is undefined.
 */
`define GENERAL_PURPOSE_REGISTERS_INDEX_2_SOURCE_UNDEFINED				2'bxx



/**
 * The number of selector bits for the general purpose register port 2 data MUX.
 */
`define GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_WIDTH							4

/**
 * This value specifies that the ALU result is loaded through general purpose
 * register port 2.
 */
`define GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_ALU								4'b0000

/**
 * This value specifies that the multiplier result is loaded through general
 * purpose register port 2.
 */
`define GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_MULTIPLIER						4'b0001

/**
 * This value specifies that the divider quotient result is loaded through
 * general purpose register port 2.
 */
`define GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_DIVIDER_QUOTIENT				4'b0010

/**
 * This value specifies that the divider remainder result is loaded through
 * general purpose register port 2.
 */
`define GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_DIVIDER_REMAINDER				4'b0011

/**
 * This value specifies that the shifter result is loaded through general
 * purpose register port 2.
 */
`define GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_SHIFTER							4'b0100

/**
 * This value specifies that the PC is loaded through general purpose
 * register port 2.
 */
`define GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_PC								4'b0101

/**
 * This value specifies that the extended bus data is loaded through
 * general purpose register port 2.
 */
`define GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_EXTENDED_BUS_READ_DATA			4'b0110

/**
 * This value specifies that the MVFS data is loaded through general
 * purpose register port 2.
 */
`define GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_MVFS							4'b0111

/**
 * This value specifies that the exception return address is loaded
 * through general purpose register port 2.
 */
`define GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_EXCEPTION_RETURN_ADDRESS		4'b1000

/**
 * This value specifies that the general purpose register port 2 data
 * is undefined. This implies that the write-enable must be 0.
 */
`define GENERAL_PURPOSE_REGISTERS_DATA_2_SOURCE_UNDEFINED						4'bxxxx
