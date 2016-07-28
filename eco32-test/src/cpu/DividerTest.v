`default_nettype none
`timescale 1ns / 1ps

/**
 * Testbench for the divider.
 */
module DividerTest ();

	/** includes **/
	`include "VUnit.inc.v"
	`include "Clock.inc.v"

	/** the module under test **/
	reg[31:0] divDividendInputRegister;
	reg[31:0] divDivisorInputRegister;
	reg divSignedRegister;
	reg divActivateDivisionRegister;
	wire divDone;
	wire[31:0] divQuotientResultRegister;
	wire[31:0] divRemainderResultRegister;
	wire divDivisionByZeroResultRegister;
	Divider divider (
		.clock(clock),
		.leftOperand(divDividendInputRegister),
		.rightOperand(divDivisorInputRegister),
		.isSigned(divSignedRegister),
		.activate(divActivateDivisionRegister),
		.done(divDone),
		.quotient(divQuotientResultRegister),
		.remainder(divRemainderResultRegister),
		.divisionByZero(divDivisionByZeroResultRegister)
	);
	
	/** simulation **/
	reg reset;
	initial begin
		$dumpfile("cpu/DividerTest.vcd");
		$dumpvars;
		
		/** reset **/
		#5 reset <= 1'b1;
		#20 reset <= 1'b0;

		/** test some simple divisions **/
		
		assertionContext <= "test 1";
		divDividendInputRegister <= 32'd25;
		divDivisorInputRegister <= 32'd4;
		divSignedRegister <= 1'b0;
		divActivateDivisionRegister <= 1'b1;
		#20 divActivateDivisionRegister <= 1'b0;
		while (~divDone) begin
			#20;
		end
		assert(divQuotientResultRegister == 32'd6, "quotient");
		assert(divRemainderResultRegister == 32'd1, "remainder");
		assert(divDivisionByZeroResultRegister == 1'b0, "div/0");
		
		assertionContext <= "test 2";
		divDividendInputRegister <= 32'd100;
		divDivisorInputRegister <= 32'd10;
		divSignedRegister <= 1'b0;
		divActivateDivisionRegister <= 1'b1;
		#20 divActivateDivisionRegister <= 1'b0;
		while (~divDone) begin
			#20;
		end
		assert(divQuotientResultRegister == 32'd10, "quotient");
		assert(divRemainderResultRegister == 32'd0, "remainder");
		assert(divDivisionByZeroResultRegister == 1'b0, "div/0");

		assertionContext <= "test 3";
		divDividendInputRegister <= 32'd5;
		divDivisorInputRegister <= 32'd10;
		divSignedRegister <= 1'b0;
		divActivateDivisionRegister <= 1'b1;
		#20 divActivateDivisionRegister <= 1'b0;
		while (~divDone) begin
			#20;
		end
		assert(divQuotientResultRegister == 32'd0, "quotient");
		assert(divRemainderResultRegister == 32'd5, "remainder");
		assert(divDivisionByZeroResultRegister == 1'b0, "div/0");

		/** test two large numbers in an unsigned division **/

		assertionContext <= "test 4";
		divDividendInputRegister <= 32'h9999999a;
		divDivisorInputRegister <= 32'h33333333;
		divSignedRegister <= 1'b0;
		divActivateDivisionRegister <= 1'b1;
		#20 divActivateDivisionRegister <= 1'b0;
		while (~divDone) begin
			#20;
		end
		assert(divQuotientResultRegister == 32'd3, "quotient");
		assert(divRemainderResultRegister == 32'd1, "remainder");
		assert(divDivisionByZeroResultRegister == 1'b0, "div/0");

		assertionContext <= "test 5";
		divDividendInputRegister <= 32'h99999999;
		divDivisorInputRegister <= 32'h88888888;
		divSignedRegister <= 1'b0;
		divActivateDivisionRegister <= 1'b1;
		#20 divActivateDivisionRegister <= 1'b0;
		while (~divDone) begin
			#20;
		end
		assert(divQuotientResultRegister == 32'd1, "quotient");
		assert(divRemainderResultRegister == 32'h11111111, "remainder");
		assert(divDivisionByZeroResultRegister == 1'b0, "div/0");

		/** test a large and a small number in an unsigned division **/
		
		assertionContext <= "test 6";
		divDividendInputRegister <= 32'h9999999a;
		divDivisorInputRegister <= 32'h3;
		divSignedRegister <= 1'b0;
		divActivateDivisionRegister <= 1'b1;
		#20 divActivateDivisionRegister <= 1'b0;
		while (~divDone) begin
			#20;
		end
		assert(divQuotientResultRegister == 32'h33333333, "quotient");
		assert(divRemainderResultRegister == 32'h1, "remainder");
		assert(divDivisionByZeroResultRegister == 1'b0, "div/0");

		/** test a signed division with remainder 0 **/

		assertionContext <= "test 7";
		divDividendInputRegister <= 32'h9999999a;
		divDivisorInputRegister <= 32'h33333333;
		divSignedRegister <= 1'b1;
		divActivateDivisionRegister <= 1'b1;
		#20 divActivateDivisionRegister <= 1'b0;
		while (~divDone) begin
			#20;
		end
		assert(divQuotientResultRegister == 32'hfffffffe, "quotient");
		assert(divRemainderResultRegister == 32'h0, "remainder");
		assert(divDivisionByZeroResultRegister == 1'b0, "div/0");

		/** test negative / negative with remainder: -7 / -3 yields 2 rem -1 **/
		assertionContext <= "test 8";
		divDividendInputRegister <= 32'hfffffff9;
		divDivisorInputRegister <= 32'hfffffffd;
		divSignedRegister <= 1'b1;
		divActivateDivisionRegister <= 1'b1;
		#20 divActivateDivisionRegister <= 1'b0;
		while (~divDone) begin
			#20;
		end
		assert(divQuotientResultRegister == 32'h2, "quotient");
		assert(divRemainderResultRegister == 32'hffffffff, "remainder");
		assert(divDivisionByZeroResultRegister == 1'b0, "div/0");

		/** test positive / negative with remainder: 7 / -3 yields -2 rem 1 **/
		assertionContext <= "test 8";
		divDividendInputRegister <= 32'h7;
		divDivisorInputRegister <= 32'hfffffffd;
		divSignedRegister <= 1'b1;
		divActivateDivisionRegister <= 1'b1;
		#20 divActivateDivisionRegister <= 1'b0;
		while (~divDone) begin
			#20;
		end
		assert(divQuotientResultRegister == 32'hfffffffe, "quotient");
		assert(divRemainderResultRegister == 32'h1, "remainder");
		assert(divDivisionByZeroResultRegister == 1'b0, "div/0");

		/** test negative / positive with remainder: -7 / 3 yields -2 rem -1 **/
		assertionContext <= "test 8";
		divDividendInputRegister <= 32'hfffffff9;
		divDivisorInputRegister <= 32'h3;
		divSignedRegister <= 1'b1;
		divActivateDivisionRegister <= 1'b1;
		#20 divActivateDivisionRegister <= 1'b0;
		while (~divDone) begin
			#20;
		end
		assert(divQuotientResultRegister == 32'hfffffffe, "quotient");
		assert(divRemainderResultRegister == 32'hffffffff, "remainder");
		assert(divDivisionByZeroResultRegister == 1'b0, "div/0");

		/** test MIN / positive with remainder: -0x80000000 / 3 yields -0x2AAAAAAA = 0xD5555556 rem -2 **/
		assertionContext <= "test 9";
		divDividendInputRegister <= 32'h80000000;
		divDivisorInputRegister <= 32'h3;
		divSignedRegister <= 1'b1;
		divActivateDivisionRegister <= 1'b1;
		#20 divActivateDivisionRegister <= 1'b0;
		while (~divDone) begin
			#20;
		end
		assert(divQuotientResultRegister == 32'hd5555556, "quotient");
		assert(divRemainderResultRegister == 32'hfffffffe, "remainder");
		assert(divDivisionByZeroResultRegister == 1'b0, "div/0");

		/** test MIN / negative with remainder: -0x80000000 / -3 yields 0x2aaaaaaa rem -2 **/
		assertionContext <= "test 10";
		divDividendInputRegister <= 32'h80000000;
		divDivisorInputRegister <= 32'hfffffffd;
		divSignedRegister <= 1'b1;
		divActivateDivisionRegister <= 1'b1;
		#20 divActivateDivisionRegister <= 1'b0;
		while (~divDone) begin
			#20;
		end
		assert(divQuotientResultRegister == 32'h2aaaaaaa, "quotient");
		assert(divRemainderResultRegister == 32'hfffffffe, "remainder");
		assert(divDivisionByZeroResultRegister == 1'b0, "div/0");

		/** test MIN / MIN **/
		assertionContext <= "test 11";
		divDividendInputRegister <= 32'h80000000;
		divDivisorInputRegister <= 32'h80000000;
		divSignedRegister <= 1'b1;
		divActivateDivisionRegister <= 1'b1;
		#20 divActivateDivisionRegister <= 1'b0;
		while (~divDone) begin
			#20;
		end
		assert(divQuotientResultRegister == 32'h1, "quotient");
		assert(divRemainderResultRegister == 32'h0, "remainder");
		assert(divDivisionByZeroResultRegister == 1'b0, "div/0");

		/** test MIN / 1 **/
		assertionContext <= "test 12";
		divDividendInputRegister <= 32'h80000000;
		divDivisorInputRegister <= 32'h1;
		divSignedRegister <= 1'b1;
		divActivateDivisionRegister <= 1'b1;
		#20 divActivateDivisionRegister <= 1'b0;
		while (~divDone) begin
			#20;
		end
		assert(divQuotientResultRegister == 32'h80000000, "quotient");
		assert(divRemainderResultRegister == 32'h0, "remainder");
		assert(divDivisionByZeroResultRegister == 1'b0, "div/0");

		/** test MIN / -1 (very special case! this is the only division that produces overflow) **/
		assertionContext <= "test 12";
		divDividendInputRegister <= 32'h80000000;
		divDivisorInputRegister <= 32'hffffffff;
		divSignedRegister <= 1'b1;
		divActivateDivisionRegister <= 1'b1;
		#20 divActivateDivisionRegister <= 1'b0;
		while (~divDone) begin
			#20;
		end
		assert(divQuotientResultRegister == 32'h80000000, "quotient");
		assert(divRemainderResultRegister == 32'h0, "remainder");
		assert(divDivisionByZeroResultRegister == 1'b0, "div/0");

		/** test 1 / 0 signed **/
		assertionContext <= "test 13";
		divDividendInputRegister <= 32'h00000001;
		divDivisorInputRegister <= 32'h00000000;
		divSignedRegister <= 1'b1;
		divActivateDivisionRegister <= 1'b1;
		#20 divActivateDivisionRegister <= 1'b0;
		while (~divDone) begin
			#20;
		end
		assert(divDivisionByZeroResultRegister == 1'b1, "div/0");

		/** test 1 / 0 unsigned **/
		assertionContext <= "test 14";
		divDividendInputRegister <= 32'h00000001;
		divDivisorInputRegister <= 32'h00000000;
		divSignedRegister <= 1'b0;
		divActivateDivisionRegister <= 1'b1;
		#20 divActivateDivisionRegister <= 1'b0;
		while (~divDone) begin
			#20;
		end
		assert(divDivisionByZeroResultRegister == 1'b1, "div/0");

		/** test 1 / 1, div/0 result must be reset **/
		assertionContext <= "test 15";
		divDividendInputRegister <= 32'h00000001;
		divDivisorInputRegister <= 32'h00000001;
		divSignedRegister <= 1'b1;
		divActivateDivisionRegister <= 1'b1;
		#20 divActivateDivisionRegister <= 1'b0;
		while (~divDone) begin
			#20;
		end
		assert(divDivisionByZeroResultRegister == 1'b0, "div/0");
		
		
		/** done **/
		#100 $finish;
	end
		
endmodule
