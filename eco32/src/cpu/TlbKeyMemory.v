`default_nettype none
`timescale 1ns / 1ps

/**
 * TLB key memory. This memory stores an array of 32 TLB keys (i.e. page numbers),
 * each 20 bits wide. The memory can be read asynchronously and written
 * synchronously. The memory also supports searching all entries for a key in a
 * purely combinatorial way, resulting either in a matching index or "no match".
 * If multiple indices match, the lowest one is used.
 */
module TlbKeyMemory (
	
		/** the clock **/
		input clock,
		
		/** the index to read or write **/
		input[4:0] accessIndex,
		
		/** the read value, read asynchronously from the location specified by the access index **/
		output[19:0] readValue,
		
		/** the write value, written synchronously to the location specified by the access index **/
		input[19:0] writeValue,
		
		/** whether to write an entry **/
		input writeEnable,
		
		/** the key to search for **/
		input[19:0] searchKey,
		
		/** whether the search returned a result **/
		output found,
		
		/** the matching index if the search key was found, otherwise undefined **/
		output[4:0] foundIndex
		
	);

	/**
	 * The actual memory where the keys are stored.
	 */
	reg[19:0] keyArray[0:31];
	
	/** read logic **/
	assign readValue = keyArray[accessIndex];
	
	/** write logic **/
	always @(posedge clock) begin
		if (writeEnable) begin
			keyArray[accessIndex] <= writeValue;
		end
	end
	
	/**
	 * This array contains one success flag for every key in the key array that
	 * indicates whether the search key finds that entry.
	 */
	wire[31:0] keyComparatorResults = {
		searchKey == keyArray[31],
		searchKey == keyArray[30],
		searchKey == keyArray[29],
		searchKey == keyArray[28],
		searchKey == keyArray[27],
		searchKey == keyArray[26],
		searchKey == keyArray[25],
		searchKey == keyArray[24],
		searchKey == keyArray[23],
		searchKey == keyArray[22],
		searchKey == keyArray[21],
		searchKey == keyArray[20],
		searchKey == keyArray[19],
		searchKey == keyArray[18],
		searchKey == keyArray[17],
		searchKey == keyArray[16],
		searchKey == keyArray[15],
		searchKey == keyArray[14],
		searchKey == keyArray[13],
		searchKey == keyArray[12],
		searchKey == keyArray[11],
		searchKey == keyArray[10],
		searchKey == keyArray[9],
		searchKey == keyArray[8],
		searchKey == keyArray[7],
		searchKey == keyArray[6],
		searchKey == keyArray[5],
		searchKey == keyArray[4],
		searchKey == keyArray[3],
		searchKey == keyArray[2],
		searchKey == keyArray[1],
		searchKey == keyArray[0]
	};

	/**
	 * This priority encoder selects the first matching key.
	 */
	PriorityEncoder32 keyComparatorPriorityEncoder (
		.inputSignals(keyComparatorResults),
		.anySignalActive(found),
		.activeSignalIndex(foundIndex)
	);

endmodule
