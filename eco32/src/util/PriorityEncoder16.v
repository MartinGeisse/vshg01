`default_nettype none
`timescale 1ns / 1ps

/**
 * This is a 16-input priority encoder.
 */
module PriorityEncoder16 (

		/** the input signals **/
		input[15:0] inputSignals,
		
		/** whether any of the input signals is active **/
		output anySignalActive,
		
		/** the index of the first active input signal. Only valid if anySignalActive is asserted **/
		output reg[3:0] activeSignalIndex
		
	);
	
	/** generate the activeSignalIndex **/
	always @(inputSignals) begin
		if (inputSignals[0]) begin
			activeSignalIndex <= 4'b0000;
		end else if (inputSignals[1]) begin
			activeSignalIndex <= 4'b0001; 
		end else if (inputSignals[2]) begin
			activeSignalIndex <= 4'b0010; 
		end else if (inputSignals[3]) begin
			activeSignalIndex <= 4'b0011; 
		end else if (inputSignals[4]) begin
			activeSignalIndex <= 4'b0100; 
		end else if (inputSignals[5]) begin
			activeSignalIndex <= 4'b0101; 
		end else if (inputSignals[6]) begin
			activeSignalIndex <= 4'b0110; 
		end else if (inputSignals[7]) begin
			activeSignalIndex <= 4'b0111; 
		end else if (inputSignals[8]) begin
			activeSignalIndex <= 4'b1000;
		end else if (inputSignals[9]) begin
			activeSignalIndex <= 4'b1001; 
		end else if (inputSignals[10]) begin
			activeSignalIndex <= 4'b1010; 
		end else if (inputSignals[11]) begin
			activeSignalIndex <= 4'b1011; 
		end else if (inputSignals[12]) begin
			activeSignalIndex <= 4'b1100; 
		end else if (inputSignals[13]) begin
			activeSignalIndex <= 4'b1101; 
		end else if (inputSignals[14]) begin
			activeSignalIndex <= 4'b1110; 
		end else if (inputSignals[15]) begin
			activeSignalIndex <= 4'b1111; 
		end else begin
			activeSignalIndex <= 4'bxxxx;
		end
	end 	

	/** generate the anySignalActive **/
	assign anySignalActive = (| inputSignals);
	
endmodule
