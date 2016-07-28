`default_nettype none
`timescale 1ns / 1ps

/**
 * This is a 32-input priority encoder.
 */
module PriorityEncoder32 (

		/** the input signals **/
		input[31:0] inputSignals,
		
		/** whether any of the input signals is active **/
		output anySignalActive,
		
		/** the index of the first active input signal. Only valid if anySignalActive is asserted **/
		output reg[4:0] activeSignalIndex
		
	);
	
	/** generate the activeSignalIndex **/
	always @(inputSignals) begin
		if (inputSignals[0]) begin
			activeSignalIndex <= 5'b00000;
		end else if (inputSignals[1]) begin
			activeSignalIndex <= 5'b00001; 
		end else if (inputSignals[2]) begin
			activeSignalIndex <= 5'b00010; 
		end else if (inputSignals[3]) begin
			activeSignalIndex <= 5'b00011; 
		end else if (inputSignals[4]) begin
			activeSignalIndex <= 5'b00100; 
		end else if (inputSignals[5]) begin
			activeSignalIndex <= 5'b00101; 
		end else if (inputSignals[6]) begin
			activeSignalIndex <= 5'b00110; 
		end else if (inputSignals[7]) begin
			activeSignalIndex <= 5'b00111; 
		end else if (inputSignals[8]) begin
			activeSignalIndex <= 5'b01000;
		end else if (inputSignals[9]) begin
			activeSignalIndex <= 5'b01001; 
		end else if (inputSignals[10]) begin
			activeSignalIndex <= 5'b01010; 
		end else if (inputSignals[11]) begin
			activeSignalIndex <= 5'b01011; 
		end else if (inputSignals[12]) begin
			activeSignalIndex <= 5'b01100; 
		end else if (inputSignals[13]) begin
			activeSignalIndex <= 5'b01101; 
		end else if (inputSignals[14]) begin
			activeSignalIndex <= 5'b01110; 
		end else if (inputSignals[15]) begin
			activeSignalIndex <= 5'b01111; 
		end else if (inputSignals[16]) begin
			activeSignalIndex <= 5'b10000;
		end else if (inputSignals[17]) begin
			activeSignalIndex <= 5'b10001; 
		end else if (inputSignals[18]) begin
			activeSignalIndex <= 5'b10010; 
		end else if (inputSignals[19]) begin
			activeSignalIndex <= 5'b10011; 
		end else if (inputSignals[20]) begin
			activeSignalIndex <= 5'b10100; 
		end else if (inputSignals[21]) begin
			activeSignalIndex <= 5'b10101; 
		end else if (inputSignals[22]) begin
			activeSignalIndex <= 5'b10110; 
		end else if (inputSignals[23]) begin
			activeSignalIndex <= 5'b10111; 
		end else if (inputSignals[24]) begin
			activeSignalIndex <= 5'b11000;
		end else if (inputSignals[25]) begin
			activeSignalIndex <= 5'b11001; 
		end else if (inputSignals[26]) begin
			activeSignalIndex <= 5'b11010; 
		end else if (inputSignals[27]) begin
			activeSignalIndex <= 5'b11011; 
		end else if (inputSignals[28]) begin
			activeSignalIndex <= 5'b11100; 
		end else if (inputSignals[29]) begin
			activeSignalIndex <= 5'b11101; 
		end else if (inputSignals[30]) begin
			activeSignalIndex <= 5'b11110; 
		end else if (inputSignals[31]) begin
			activeSignalIndex <= 5'b11111; 
		end else begin
			activeSignalIndex <= 5'bxxxxx;
		end
	end 	

	/** generate the anySignalActive **/
	assign anySignalActive = (| inputSignals);
	
endmodule
