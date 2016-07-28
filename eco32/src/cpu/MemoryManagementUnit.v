`default_nettype none
`timescale 1ns / 1ps

/**
 * Memory management unit.
 */
module MemoryManagementUnit (
		
		/** the clock **/
		input clock,
		
		/** synchronous reset signal **/
		input reset,

		/**
		 * This signal is used as the input for MMU address translation. It is used
		 * for regular address translation as well as for the TBS instruction.
		 *
		 * For regular address translation, assert this signal, as well as
		 * affectRandomIndexCounter, and wait until the next
		 * clock cycle, then consume the values of the tlbMiss, physicalAddress,
		 * physicalAddressWriteAllowed and physicalAddressValid result signals.
		 * This input signal must be unchanged in that second cycle -- the
		 * results depend on the same value being present in both cycles. 
		 */
		input[31:0] virtualAddress,

		/**
		 * At every clock edge where this signal is asserted and the virtualAddress
		 * input signal is TLB-mapped, the internal index randomization counter
		 * is incremented. This signal should therefore be asserted for exactly
		 * one clock edge for every regular address translation, and otherwise
		 * stay de-asserted.
		 */
		input affectRandomIndexCounter,

		/**
		 * After address translation (i.e. after the clock edge following the
		 * assertion of the virtualAddress), this signal contains a 1 if the address
		 * was NOT found in the TLB, i.e. causes a TLB miss, 0 if it was found. This
		 * flag is always 0 for direct-mapped addresses, i.e. treats such addresses as
		 * "never-miss" addresses. 
		 */
		output tlbMiss,
		
		/**
		 * After address translation (i.e. after the clock edge following the
		 * assertion of the virtualAddress), this signal contains
		 * the physical address. The value of this signal is only valid if
		 * tlbMiss is valid and 0.
		 */
		output[31:0] physicalAddress,
		
		/**
		 * After address translation (i.e. after the clock edge following the
		 * assertion of the virtualAddress), this signal contains
		 * the write flag of the TLB entry used for mapping. If this flag is
		 * 0 and the access is a write access, a TLB write exception should
		 * be triggered. This flag is always 1 for direct-mapped addresses.
		 * The value of this flag is only valid if tlbMiss is valid and 0.
		 */
		output physicalAddressWriteAllowed,
		
		/**
		 * After address translation (i.e. after the clock edge following the
		 * assertion of the virtualAddress), this signal contains
		 * the valid flag of the TLB entry used for mapping. If this flag is
		 * 0, a TLB invalid exception should be triggered. This flag is always
		 * 1 for direct-mapped addresses. The value of this flag is only valid
		 * if tlbMiss is valid and 0.
		 */
		output physicalAddressValid,

		/**
		 * If this signal is set to 1, then the index for TLB entry write operations
		 * is taken from the random index counter (i.e. we have a TBWR), otherwise
		 * it is taken from the TLB index register (i.e. we have a TBWI). The key and
		 * value to write are always taken from the TLB high and low registers,
		 * respectively. This signal MUST be low for TBRI.
		 */
		input randomizeEntryIndex,
		
		/**
		 * This signal enables entry writing for TBWR and TBWI. The distinction between those
		 * two instructions is made by the randomizeEntryIndex input signal.
		 */
		input writeEntryEnable,
		
		/**
		 * This signal determines the special register value being read by the CPU.
		 * It uses the same encoding as the 3 lowest-order bits of the MVFS and
		 * MVTS instructions: "001" for the TLB index register, "010" for the TLB entry
		 * high register, "011" for the TLB entry low register, and "100" for the TLB
		 * bad address register.
		 */
		input[2:0] specialRegisterReadIndex,
		
		/**
		 * This signal provides the value of the special register denoted by
		 * specialRegisterReadIndex in a purely combinatorial way.
		 */
		output reg[31:0] specialRegisterReadValue,
		
		/**
		 * This signal must be asserted to the value to write into a special register
		 * in the case that the write value shall be specified explicitly by the main
		 * state machine. The special register to write is not denoted by an index, but
		 * rather by appropriate write-enable and data MUX signals (see below).
		 */
		input[31:0] specialRegisterExplicitWriteValue,
		
		/**
		 * This signal causes the TLB index register to be loaded on the next clock edge.
		 * See tlbIndexDataSource for controlling the data MUX.
		 */
		input tlbIndexWriteEnable,

		/**
		 * This signal selects the value to write into the TLB index register when
		 * tlbIndexWriteEnable is asserted. See MemoryManagementUnitConstants.v for allowed
		 * values.
		 */
		input[`TLB_INDEX_DATA_SOURCE_WIDTH-1:0] tlbIndexDataSource,

		/**
		 * This signal causes the TLB entry high register to be loaded on the next clock edge.
		 * See tlbEntryHighDataSource for controlling the data MUX.
		 */
		input tlbEntryHighWriteEnable,

		/**
		 * This signal selects the value to write into the TLB entry high register when
		 * tlbEntryHighWriteEnable is asserted. See MemoryManagementUnitConstants.v for allowed
		 * values.
		 */
		input[`TLB_ENTRY_HIGH_DATA_SOURCE_WIDTH-1:0] tlbEntryHighDataSource,

		/**
		 * This signal causes the TLB entry low register to be loaded on the next clock edge.
		 * See tlbEntryLowDataSource for controlling the data MUX.
		 */
		input tlbEntryLowWriteEnable,

		/**
		 * This signal selects the value to write into the TLB entry low register when
		 * tlbEntryLowWriteEnable is asserted. See MemoryManagementUnitConstants.v for allowed
		 * values.
		 */
		input[`TLB_ENTRY_LOW_DATA_SOURCE_WIDTH-1:0] tlbEntryLowDataSource,

		/**
		 * This signal causes the TLB bad adress register to be loaded on the next clock edge.
		 * See tlbBadAddressDataSource for controlling the data MUX.
		 */
		input tlbBadAddressWriteEnable,

		/**
		 * This signal selects the value to write into the TLB bad address register when
		 * tlbBadAddressWriteEnable is asserted. See MemoryManagementUnitConstants.v for allowed
		 * values.
		 */
		input[`TLB_BAD_ADDRESS_DATA_SOURCE_WIDTH-1:0] tlbBadAddressDataSource

	);

	/*********************************************************************************/
	/** TLB declarations **/
	/*********************************************************************************/

	/**
	 * This value provides the index for indexed access to the key and value memories.
	 * Depending on randomizeEntryIndex, it will either use the value of the random index
	 * counter or the value of the TLB index register.
	 */
	wire[4:0] effectiveEntryAccessIndex;
	
	/**
	 * The key read asynchronously from the key memory location specified by the
	 * effective entry access index.
	 */
	wire[19:0] tlbKeyMemoryReadValue;
	
	/**
	 * This flag is true if and only if the search key was found.
	 */
	wire found;
	
	/**
	 * This flag contains the index where the search key was found, and is undefined
	 * if the search key was not found
	 */
	wire[4:0] foundIndex;

	/**
	 * This signal provides the contents of the value memory for the entry that was
	 * found for the search key. It is undefined if no entry was found for the search key.
	 */
	wire[21:0] addressMappingResult;

	/**
	 * The value read synchronously from the value memory location specified by the
	 * effective entry access index.
	 */
	wire[21:0] tlbValueMemoryReadValue;
	
	/*********************************************************************************/
	/** address translation declarations **/
	/*********************************************************************************/

	/**
	 * This value is true if the virtualAddress contains a direct-mapped
	 * address, false if the address is TLB-mapped. It is computed in a purely
	 * combinatorial way.
	 */
	wire virtualAddressInputIsDirectMapped;
	
	/**
	 * This register stores synchronously whether a TLB miss occurred. Its output
	 * value is thus valid at the same time the physical address and write/valid flags are.
	 */
	reg tlbMissRegister;

	/** generate mapping output signals **/
	wire[19:0] pageFrameNumber;

	/**
	 * This counter is increased on every clock cycle at which affectRandomIndexCounter is 1
	 * and the virtualAddress is a TLB-mapped address. It is used to generate random TLB index
	 * numbers for the TBWR instruction. This counter runs from 4 through 31 repeatedly.
	 * Entries 0..3 cannot be accessed by TBWR.
	 */
	reg[4:0] randomIndexCounter;

	/*********************************************************************************/
	/** program-visible special registers **/
	/*********************************************************************************/
	
	/**
	 * This register contains the TLB index register. It provides the index used by the
	 * TBRI and TBWI instructions, and is loaded by the MVTS and TBS instructions as well
	 * as some exceptions.
	 */
	reg[4:0] tlbIndexRegister;

	/**
	 * This register contains bit 31 of the TLB index register.
	 * This bit is set by the TBS instruction if no entry could be found.
	 * The MVTS instruction always resets this bit.
	 */
	reg tlbInvalidIndexRegister;

	/**
	 * This register is used to transfer the key part (i.e.
	 * the page number) of a TLB entry to or from the TLB.
	 */
	reg[19:0] tlbEntryHighRegister;

	/**
	 * This register is used to transfer the value part (i.e.
	 * the page frame number, the write bit, and the valid bit,
	 * in this order from MSbit to LSbit) of a TLB entry to or
	 * from the TLB.
	 */
	reg[21:0] tlbEntryLowRegister;
	
	/**
	 * This register is used to store a faulting address so
	 * the exception handler can analyze it.
	 */
	reg[31:0] tlbBadAddressRegister;
	
	/*********************************************************************************/
	/** TLB implementation **/
	/*********************************************************************************/

	/** assign signals **/
	assign effectiveEntryAccessIndex = randomizeEntryIndex ? randomIndexCounter : tlbIndexRegister;
	
	/** the key memory **/
	TlbKeyMemory tlbKeyMemory (
		.clock(clock),
		.accessIndex(effectiveEntryAccessIndex),
		.readValue(tlbKeyMemoryReadValue),
		.writeValue(tlbEntryHighRegister),
		.writeEnable(writeEntryEnable),
		.searchKey(virtualAddress[31:12]),
		.found(found),
		.foundIndex(foundIndex)
	);

	/** the value memory **/
	TlbValueMemory tlbValueMemory (
		.clock(clock),
		.index1(foundIndex),
		.readData1(addressMappingResult),
		.index2(effectiveEntryAccessIndex),
		.readData2(tlbValueMemoryReadValue),
		.writeData2(tlbEntryLowRegister),
		.writeEnable2(writeEntryEnable)
	);

	/*********************************************************************************/
	/** address translation implementation **/
	/*********************************************************************************/

	/** assign internal signals **/
	assign virtualAddressInputIsDirectMapped = (virtualAddress[31:30] == 2'b11);
	always @(posedge clock) begin
		tlbMissRegister <= ~virtualAddressInputIsDirectMapped & ~found;
	end
	assign tlbMiss = tlbMissRegister;

	/** generate mapping output signals **/
	assign pageFrameNumber = virtualAddressInputIsDirectMapped ? {2'b00, virtualAddress[29:12]} : addressMappingResult[21:2];
	assign physicalAddress = {pageFrameNumber, virtualAddress[11:0]};
	assign physicalAddressWriteAllowed = virtualAddressInputIsDirectMapped | addressMappingResult[1];
	assign physicalAddressValid = virtualAddressInputIsDirectMapped | addressMappingResult[0];

	/**
	 * Random counter handling
	 */
	always @(posedge clock) begin
		if (reset) begin
			randomIndexCounter <= 5'd4;
		end else if (affectRandomIndexCounter & ~virtualAddressInputIsDirectMapped) begin
			randomIndexCounter <= (randomIndexCounter == 5'd31) ? 5'd4 : (randomIndexCounter + 1);
		end
	end

	/*********************************************************************************/
	/** implementation of the program-visible special registers **/
	/*********************************************************************************/
	
	/** register loading logic for the TLB index register **/
	always @(posedge clock) begin
		if (tlbIndexWriteEnable) begin
			case (tlbIndexDataSource)
			
				/** write an explicitly specified value to the TLB index register **/
				`TLB_INDEX_DATA_SOURCE_EXPLICIT: begin
					tlbIndexRegister <= specialRegisterExplicitWriteValue[4:0];
					tlbInvalidIndexRegister <= 1'b0;
				end
				
				/**
				 * Write the TBS result to the TLB index register. This result is the index of the
				 * entry found for the specified virtual address, or a special invalid value
				 * if no entry was found.
				 */
				`TLB_INDEX_DATA_SOURCE_SEARCH: begin
					tlbIndexRegister <= found ? foundIndex : 5'b00000;
					tlbInvalidIndexRegister <= ~found;
				end
				
				/** any other value for tlbIndexDataSource causes an undefined value to be loaded **/
				default: begin
					tlbIndexRegister <= 5'bxxxxx;
					tlbInvalidIndexRegister <= 1'bx;
				end
				
			endcase
		end
	end
	
	/** register loading logic for the TLB entry high register **/
	always @(posedge clock) begin
		if (tlbEntryHighWriteEnable) begin
			case (tlbEntryHighDataSource)
			
				/** write an explicitly specified value to the TLB entry high register **/
				`TLB_ENTRY_HIGH_DATA_SOURCE_EXPLICIT: begin
					tlbEntryHighRegister <= specialRegisterExplicitWriteValue[31:12];
				end
				
				/**
				 * write the highest-order 20 bits of the specified virtual address (i.e. the page number)
				 * to the TLB entry high register
				 */
				`TLB_ENTRY_HIGH_DATA_SOURCE_VIRTUAL_ADDRESS: begin
					tlbEntryHighRegister <= virtualAddress[31:12];
				end

				/**
				 * Write the key memory entry at the specified index to the TLB entry high register.
				 */
				`TLB_ENTRY_HIGH_DATA_SOURCE_READ_ENTRY: begin
					tlbEntryHighRegister <= tlbKeyMemoryReadValue;
				end
				
				/** any other value for tlbEntryHighDataSource causes an undefined value to be loaded **/
				default: begin
					tlbEntryHighRegister <= 20'bxxxxxxxxxxxxxxxxxxxx;
				end
				
			endcase
		end
	end
	
	/** register loading logic for the TLB entry low register **/
	always @(posedge clock) begin
		if (tlbEntryLowWriteEnable) begin
			case (tlbEntryLowDataSource)
			
				/** write an explicitly specified value to the TLB entry low register **/
				`TLB_ENTRY_LOW_DATA_SOURCE_EXPLICIT: begin
					tlbEntryLowRegister <= {specialRegisterExplicitWriteValue[31:12], specialRegisterExplicitWriteValue[1:0]};
				end
				
				/**
				 * Write the address mapping result to the TLB entry low register. This result is
				 * the contents of the value memory entry whose corresponding key memory entry
				 * is the lowest matching key for the specified virtual address.
				 */
				`TLB_ENTRY_LOW_DATA_SOURCE_TLB_VALUE: begin
					tlbEntryLowRegister <= addressMappingResult;
				end

				/**
				 * Write the value memory entry at the specified index to the TLB entry high register.
				 */
				`TLB_ENTRY_LOW_DATA_SOURCE_READ_ENTRY: begin
					tlbEntryLowRegister <= tlbValueMemoryReadValue;
				end
				
				/** any other value for tlbEntryLowDataSource causes an undefined value to be loaded **/
				default: begin
					tlbEntryLowRegister <= 22'bxxxxxxxxxxxxxxxxxxxxxx;
				end
				
			endcase
		end
	end
	
	/** register loading logic for the TLB bad address register **/
	always @(posedge clock) begin
		if (tlbBadAddressWriteEnable) begin
			case (tlbBadAddressDataSource)
			
				/** write an explicitly specified value to the TLB bad address register **/
				`TLB_BAD_ADDRESS_DATA_SOURCE_EXPLICIT: begin
					tlbBadAddressRegister <= specialRegisterExplicitWriteValue;
				end
				
				/** write the specified virtual address to the TLB bad address register **/
				`TLB_BAD_ADDRESS_DATA_SOURCE_VIRTUAL_ADDRESS: begin
					tlbBadAddressRegister <= virtualAddress;
				end
				
				/** any other value for tlbBadAddressDataSource causes an undefined value to be loaded **/
				default: begin
					tlbBadAddressRegister <= 32'bxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx;
				end
				
			endcase
		end
	end
	
	/** special register reading logic **/
	always @(*) begin
		case (specialRegisterReadIndex)
		
			/** TLB index **/
			3'd1: begin
				specialRegisterReadValue <= {tlbInvalidIndexRegister, 26'h0, tlbIndexRegister};
			end

			/** TLB entry high **/
			3'd2: begin
				specialRegisterReadValue <= {tlbEntryHighRegister, 12'h0};
			end

			/** TLB entry low **/
			3'd3: begin
				specialRegisterReadValue <= {tlbEntryLowRegister[21:2], 10'h0, tlbEntryLowRegister[1:0]};
			end

			/** TLB bad address **/
			3'd4: begin
				specialRegisterReadValue <= tlbBadAddressRegister;
			end
			
			/** undefined **/
			default: begin
				specialRegisterReadValue <= 32'bxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx;
			end
		
		endcase
	end

endmodule
