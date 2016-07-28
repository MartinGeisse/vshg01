`default_nettype none
`timescale 1ns / 1ps

/**
 * Testbench for the raw keyboard controller.
 */
module RawKeyboardControllerTest ();

	/** includes **/
	`include "VUnit.inc.v"
	`include "Clock.inc.v"

	/** used to check the results **/
	reg expectedDataReady;
	reg[7:0] expectedDataOut;
	always @(posedge clock) begin
		if (~reset) begin
			assert(expectedDataReady == dataReady, "dataReady");
			assert(~expectedDataReady || (expectedDataOut == dataOut), "dataOut");
		end
	end
	
	/** helper tasks **/
	task sendValidStartBit;
		begin
			ps2Data <= 1'b0;
			#5000 ps2Clock <= 1'b0;
			#30000 ps2Clock <= 1'b1;
		end
	endtask

	task sendInvalidStartBit;
		begin
			ps2Data <= 1'b1;
			#5000 ps2Clock <= 1'b0;
			#30000 ps2Clock <= 1'b1;
		end
	endtask

	task sendDataBit(input value);
		begin
			#15000 ps2Data <= value;
			#15000 ps2Clock <= 1'b0;
			#30000 ps2Clock <= 1'b1;
		end
	endtask
	
	task sendValidParityBit(input value, input[7:0] expectedData);
		begin
			#15000 ps2Data <= value;
			#15000 ps2Clock <= 1'b0;
			/* 380 ns is the delay of the synchronizer and debouncer **/
			#380 expectedDataReady <= 1'b1;
			expectedDataOut <= expectedData;
			#20 expectedDataReady <= 1'b0;
			#29600 ps2Clock <= 1'b1;
		end
	endtask

	task sendInvalidParityBit(input value);
		begin
			#15000 ps2Data <= value;
			#15000 ps2Clock <= 1'b0;
			#30000 ps2Clock <= 1'b1;
		end
	endtask

	task sendValidStopBit;
		begin
			#15000 ps2Data <= 1'b1;
			#15000 ps2Clock <= 1'b0;
			#30000 ps2Clock <= 1'b1;
		end
	endtask

	task sendInvalidStopBit;
		begin
			#15000 ps2Data <= 1'b0;
			#15000 ps2Clock <= 1'b0;
			#30000 ps2Clock <= 1'b1;
		end
	endtask
	
	task pause;
		begin
			#30000;
		end
	endtask
	
	task recover;
		begin
			ps2Data <= 1'b1;
			ps2Clock <= 1'b1;
			#120000;
		end
	endtask

	/** simulation **/
	reg reset;
	reg[159:0] currentTest;
	initial begin
		$dumpfile("keyboard/RawKeyboardControllerTest.vcd");
		$dumpvars;
		
		/** initialize testing **/
		expectedDataReady <= 1'b0;
		ps2Clock <= 1'b1;
		ps2Data <= 1'b1;
		
		/** reset **/
		#5 reset <= 1'b1;
		#1000 reset <= 1'b0;
		disableSimulationTimeout <= 1'b1;
		
		/** expect no data if there is no activity on the PS/2 interface **/
		#1000;
		
		/** test a normal transmission of the byte 0x12 (LSB to MSB: 01001000) **/
		currentTest <= "normal A";
		sendValidStartBit();
		sendDataBit(1'b0);
		sendDataBit(1'b1);
		sendDataBit(1'b0);
		sendDataBit(1'b0);
		sendDataBit(1'b1);
		sendDataBit(1'b0);
		sendDataBit(1'b0);
		sendDataBit(1'b0);
		sendValidParityBit(1'b1, 8'h12);
		sendValidStopBit();
		pause();

		/** test a transmission of the byte 0x12 with wrong parity **/
		currentTest <= "parity error";
		sendValidStartBit();
		sendDataBit(1'b0);
		sendDataBit(1'b1);
		sendDataBit(1'b0);
		sendDataBit(1'b0);
		sendDataBit(1'b1);
		sendDataBit(1'b0);
		sendDataBit(1'b0);
		sendDataBit(1'b0);
		sendInvalidParityBit(1'b0);
		sendValidStopBit();
		recover();

		/** test another normal transmission of the byte 0xf4 (LSB to MSB: 00101111) to make sure error recovery works **/
		currentTest <= "normal B";
		sendValidStartBit();
		sendDataBit(1'b0);
		sendDataBit(1'b0);
		sendDataBit(1'b1);
		sendDataBit(1'b0);
		sendDataBit(1'b1);
		sendDataBit(1'b1);
		sendDataBit(1'b1);
		sendDataBit(1'b1);
		sendValidParityBit(1'b0, 8'hf4);
		sendValidStopBit();
		pause();
		
		/** test a transmission of the byte 0x12 with an invalid start bit **/
		currentTest <= "invalid start";
		sendInvalidStartBit();
		sendDataBit(1'b0);
		sendDataBit(1'b1);
		sendDataBit(1'b0);
		sendDataBit(1'b0);
		sendDataBit(1'b1);
		sendDataBit(1'b0);
		sendDataBit(1'b0);
		sendDataBit(1'b0);
		sendInvalidParityBit(1'b1);
		sendValidStopBit();
		recover();

		/** test a transmission of the byte 0x12 with an invalid stop bit. This should receive the data value! **/
		currentTest <= "invalid stop";
		sendValidStartBit();
		sendDataBit(1'b0);
		sendDataBit(1'b1);
		sendDataBit(1'b0);
		sendDataBit(1'b0);
		sendDataBit(1'b1);
		sendDataBit(1'b0);
		sendDataBit(1'b0);
		sendDataBit(1'b0);
		sendValidParityBit(1'b1, 8'h12);
		sendInvalidStopBit();
		recover();

		/** test a transmission of the byte 0x12 with an invalid start bit and invalid parity. This must not cancel each other out. **/
		currentTest <= "invalid start and parity";
		sendInvalidStartBit();
		sendDataBit(1'b0);
		sendDataBit(1'b1);
		sendDataBit(1'b0);
		sendDataBit(1'b0);
		sendDataBit(1'b1);
		sendDataBit(1'b0);
		sendDataBit(1'b0);
		sendDataBit(1'b0);
		sendInvalidParityBit(1'b0);
		sendValidStopBit();
		recover();

		/** test another normal transmission of the byte 0xf4 (LSB to MSB: 00101111) to make sure error recovery works **/
		currentTest <= "normal C";
		sendValidStartBit();
		sendDataBit(1'b0);
		sendDataBit(1'b0);
		sendDataBit(1'b1);
		sendDataBit(1'b0);
		sendDataBit(1'b1);
		sendDataBit(1'b1);
		sendDataBit(1'b1);
		sendDataBit(1'b1);
		sendValidParityBit(1'b0, 8'hf4);
		sendValidStopBit();
		pause();

		/** done **/
		#100 $finish;
	end
	
	/** the module under test **/
	reg ps2Clock;
	reg ps2Data;
	wire dataReady;
	wire[7:0] dataOut;
	RawKeyboardController rawKeyboardController (
		.clock(clock),
		.reset(reset),
		.ps2Clock(ps2Clock),
		.ps2Data(ps2Data),
		.dataReady(dataReady),
		.dataOut(dataOut)
	);
	
endmodule
