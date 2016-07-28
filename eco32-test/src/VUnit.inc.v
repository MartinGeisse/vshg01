/**
 * Include file for unit test support.
 */

/**
 * This macro can be detected by ifdef directives.
 */
`define SIMULATION

/**
 * This context is printed out with every assertion failure message to
 * show the location where the error occured.
 */
reg[799:0] assertionContext;

/**
 * Like assertionContext, this context is printed out with every assertion
 * failure message. It is intended to provide a more fine-grained location
 * description.
 */
reg[799:0] assertionSubContext;

/**
 * Ensures that the argument x is 1, and if it isn't then stops the simulation
 * with an error message containing the specified message as well as the
 * current assertionContext and assertionSubContext. Such an error does not
 * halt the simulation.
 */
task assert(input x, input[799:0] message);
	begin
	
		/** this way of expressing the check also handles x and z values correctly **/
		case (x)
		
			/** 1 is the expected value **/
			1'b1: begin
			end
			
			/** anything else triggers an error **/
			default: begin
				$display("*** ERROR: %0s: %0s at %0t ps", assertionContext, message, $time);
			end
			
		endcase
		
	end
endtask

/**
 * This piece of code stops the simulation if it takes too long. If the
 * simulation does not stop by itself, there is usually some faulty module that
 * has locked up and does not finish its work, with the simulation code waiting
 * for it to finish.
 *
 * The disableSimulationTimeout disables the automatic timeout. This is useful
 * for testing modules that simply take that long to finish.
 */
reg disableSimulationTimeout;
initial begin
	disableSimulationTimeout <= 0;
	#1000000;
	if (~disableSimulationTimeout) begin
		$display("simulation timeout");
		$finish;
	end
end
