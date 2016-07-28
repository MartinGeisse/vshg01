/**
 * Include file for testing bus slave modules. This module defines bus
 * signals to connect to a bus slave and provides helper tasks to set
 * these signals in a meaningful way.
 */

/** define the bus signals **/
reg busEnable;
reg busWrite;
reg[`BUS_ADDRESS_HIGH:`BUS_ADDRESS_LOW] busAddress;
reg[`BUS_WRITE_DATA_WIDTH-1:0] busWriteData;
wire[`BUS_READ_DATA_WIDTH-1:0] busReadData;
wire busWait;

/**
 * This register stores the bus read value. This is needed
 * because the performBusRead() task completes a read cycle including
 * the terminating clock edge, so the busReadData wire is not
 * guaranteed to contain the read value afterwards.
 */
reg[`BUS_READ_DATA_WIDTH-1:0] busReadDataRegister;

/**
 * Initialize the bus signals.
 */
initial begin
	busEnable <= 1'b0;
	busWrite <= 1'b0;
	busAddress <= 0;
	busWriteData <= 0;
	busReadDataRegister <= 0;
end

/**
 * Sends a bus read signal to the specified address and waits for one
 * clock cycle to complete the read operation. This function currently
 * does not honor the bus wait signal. The read value is stored in the
 * busReadDataRegister.
 */
task performBusRead(input[`BUS_ADDRESS_HIGH:`BUS_ADDRESS_LOW] readAddress);
	begin
		busEnable <= 1'b1;
		busWrite <= 1'b0;
		busAddress <= readAddress;
		#1
		busReadDataRegister <= busReadData;
		#18;
		busEnable <= 1'b0;
		#1;
	end
endtask

/**
 * Sends a bus write signal to the specified address and waits for one
 * clock cycle to complete the write operation. This function currently
 * does not honor the bus wait signal.
 */
task performBusWrite(input[`BUS_ADDRESS_HIGH:`BUS_ADDRESS_LOW] writeAddress, input[`BUS_WRITE_DATA_WIDTH-1:0] writeData);
	begin
		busEnable <= 1'b1;
		busWrite <= 1'b1;
		busAddress <= writeAddress;
		busWriteData <= writeData;
		#19;
		busEnable <= 1'b0;
		#1;
	end
endtask

/**
 * Calls performBusRead(readAddress) to read from the specified address, then
 * compares the read value against an expected value and raises an
 * error on mismatch.
 */
task performBusReadAndCheck(input[`BUS_ADDRESS_HIGH:`BUS_ADDRESS_LOW] readAddress, input[`BUS_READ_DATA_WIDTH-1:0] expectedValue, input[799:0] message);
	begin
		performBusRead(readAddress);
		assert(busReadDataRegister == expectedValue, message);
	end
endtask
