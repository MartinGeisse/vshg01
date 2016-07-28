/**
 * Include file for a single test 50MHz clock named "clock".
 */

reg clock;
initial begin
	clock <= 1'b0;
end
always @(*) begin
	clock <= #10 ~clock;
end
