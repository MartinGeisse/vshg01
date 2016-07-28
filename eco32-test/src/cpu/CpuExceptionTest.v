`default_nettype none
`timescale 1ns / 1ps

/**
 * Testbench for exception handling.
 */
module CPUExceptionTest ();

	/** includes **/
	`include "VUnit.inc.v"
	`include "Clock.inc.v"
	`include "../../eco32/src/cpu/CpuConstants.inc.v"
	`include "cpu/CpuTestHelper.inc.v"

	/** helper tasks **/
	reg[799:0] subContext;
	
	task testGeneralExceptionEntry(input[31:0] pc, input[31:0] pcPhysical, input[31:0] pswValueBeforeEntry, input[31:0] expectedEntryPoint);
		begin
		
			/** set the PC and PSW, and execute a dummy instruction to make sure they are used **/
			cpu.programCounterRegister.programCounter <= pc;
			cpu.processorStatusWord.processorStatusWord <= pswValueBeforeEntry;
			busReadData <= 32'h0;
			#20 waitForBusAccess();
			assert(busAddress == pcPhysical, "DIV instruction fetch address");
			
			/** feed a (DIV $0, $0, $0) instruction to trigger the exception, then wait for it **/
			busReadData <= buildRRRInstruction(6'h08, 5'd0, 5'd0, 5'd0);
			waitForBeginInstruction();
			
			/** check state upon handler entry **/
			assert(cpu.programCounterRegister.programCounter == expectedEntryPoint, "PC");
			assert(cpu.processorStatusWord.processorStatusWord == {
				4'b0000,					// upper bits
				pswValueBeforeEntry[27],	// vector
				1'b0,						// IEN
				pswValueBeforeEntry[26],	// PIEN
				pswValueBeforeEntry[25],	// OIEN
				1'b0,						// UM
				pswValueBeforeEntry[23],	// PUM
				pswValueBeforeEntry[22],	// OUM
				5'h13,						// priority (0x13 means division by zero)
				pswValueBeforeEntry[15:0]	// interrupt mask
			}, "PSW");
			assert(cpu.generalPurposeRegisters.valueArray[30] == pc, "R30");
			
			/** check instruction fetch bus address for the first instruction of the handler **/
			#20 waitForBusAccess();
			assert(busAddress == {2'b00, expectedEntryPoint[29:0]}, "bus address");
			
		end
	endtask
	
	/**
	 * This task sets up the CPU to test a specific exception type. It moves the CPU to begin
	 * instruction state and sets the specified values for the PC and the user mode flag of
	 * the PSW.
	 *
	 * Following this task, the caller must set a bus read value (the instruction to test), 
	 * and other CPU-internal values as needed, then call executeSpecificExceptionExceptionTest
	 * or one of the specialized versions of that task.
	 *
	 * Note about the expected PC: This method sets the vector bit, so the expected PC must be c0000004 or c0000008.
	 */
	task prepareSpecificExceptionTest(input[31:0] pc, input userMode);
		begin

			/** enter a known state **/
			subContext <= "prepareSpecificExceptionTest: enter known state";
			busReadData <= 32'h0;
			waitForBeginInstruction();

			/** set up the desired value **/
			subContext <= "prepareSpecificExceptionTest: set values";
			cpu.programCounterRegister.programCounter <= pc;
			cpu.processorStatusWord.processorStatusWord <= {5'b0000_1, userMode, 26'b11_101_10110_0001_0110_0000_0101};
			cpu.memoryManagementUnit.tlbIndexRegister <= 5'd29;
			cpu.memoryManagementUnit.tlbInvalidIndexRegister <= 1'b1;
			cpu.memoryManagementUnit.tlbEntryHighRegister <= 20'h9af00;
			cpu.memoryManagementUnit.tlbEntryLowRegister <= 22'h180aa;
			cpu.memoryManagementUnit.tlbBadAddressRegister <= 31'h70809010;
			
			/** go to the fetch bus access state **/
			subContext <= "prepareSpecificExceptionTest: wait for bus access";
			
			subContext <= "prepareSpecificExceptionTest finishes";
		end
	endtask
	
	/**
	 * This task waits for the next begin instruction state that is at least one cycle in the future,
	 * such that this task can be used directly after prepareSpecificExceptionTest. It then makes sure
	 * that an exception handler entry occured.
	 */
	task executeSpecificExceptionExceptionTest(input[31:0] previousPc, input previousUserMode, input[4:0] expectedPriority);
		begin

			/** execute the instruction under test **/
			subContext <= "executeSpecificExceptionExceptionTest: wait for begin instruction state";
			#20 waitForBeginInstruction();
			
			/** ensure that we entered the handler **/
			subContext <= "executeSpecificExceptionExceptionTest: assertions";
			assert(cpu.programCounterRegister.programCounter == 32'hc0000004, "PC");
			assert(cpu.processorStatusWord.processorStatusWord == {6'b0000_1_0, previousUserMode, 4'b1_010, expectedPriority, 16'b0001_0110_0000_0101}, "PSW");
			assert(cpu.generalPurposeRegisters.valueArray[30] == previousPc, "R30");
			assert(cpu.memoryManagementUnit.tlbIndexRegister == 5'd29, "TLB index register");
			assert(cpu.memoryManagementUnit.tlbInvalidIndexRegister == 1'b1, "TLB invalid index register");
			assert(cpu.memoryManagementUnit.tlbEntryHighRegister == 20'h9af00, "TLB entry high register");
			assert(cpu.memoryManagementUnit.tlbEntryLowRegister == 22'h180aa, "TLB entry low register");
			assert(cpu.memoryManagementUnit.tlbBadAddressRegister == 31'h70809010, "TLB bad address register");

			subContext <= "executeSpecificExceptionExceptionTest finished";
		end
	endtask

	/**
	 * This task waits for the begin instruction state, then makes sure that a TLB miss exception handler
	 * entry occured.
	 */
	task executeTlbMissTest(input[31:0] previousPc, input[31:0] expectedPc, input previousUserMode, input[31:0] badAddress);
		begin

			/** execute the instruction under test **/
			subContext <= "executeTlbMissTest: wait for begin instruction state";
			#20 waitForBeginInstruction();
			
			/** ensure that we entered the handler **/
			subContext <= "executeTlbMissTest: assertions";
			assert(cpu.programCounterRegister.programCounter == expectedPc, "PC");
			assert(cpu.processorStatusWord.processorStatusWord == {6'b0000_1_0, previousUserMode, 4'b1_010, `EXCEPTION_CODE_TLB_MISS, 16'b0001_0110_0000_0101}, "PSW");
			assert(cpu.generalPurposeRegisters.valueArray[30] == previousPc, "R30");
			assert(cpu.memoryManagementUnit.tlbIndexRegister == 5'd29, "TLB index register");
			assert(cpu.memoryManagementUnit.tlbInvalidIndexRegister == 1'b1, "TLB invalid index register");
			assert(cpu.memoryManagementUnit.tlbEntryHighRegister == badAddress[31:12], "TLB entry high register");
			assert(cpu.memoryManagementUnit.tlbEntryLowRegister == 22'h180aa, "TLB entry low register");
			assert(cpu.memoryManagementUnit.tlbBadAddressRegister == badAddress, "TLB bad address register");

			subContext <= "executeTlbMissTest finished";
		end
	endtask

	/**
	 * This task waits for the begin instruction state, then makes sure that a TLB invalid/write exception handler
	 * entry occured.
	 */
	task executeTlbInvalidOrWriteTest(input[31:0] previousPc, input previousUserMode, input[4:0] expectedPriority,
			input[4:0] expectedTlbIndex, input[31:0] expectedBadAddress, input[21:0] expectedTlbLowValue);
		begin

			/** execute the instruction under test **/
			subContext <= "executeTlbInvalidOrWriteTest: wait for begin instruction state";
			#20 waitForBeginInstruction();
			
			/** ensure that we entered the handler **/
			subContext <= "executeTlbInvalidOrWriteTest: assertions";
			assert(cpu.programCounterRegister.programCounter == 32'hc0000004, "PC");
			assert(cpu.processorStatusWord.processorStatusWord == {6'b0000_1_0, previousUserMode, 4'b1_010, expectedPriority, 16'b0001_0110_0000_0101}, "PSW");
			assert(cpu.generalPurposeRegisters.valueArray[30] == previousPc, "R30");
			assert(cpu.memoryManagementUnit.tlbIndexRegister == expectedTlbIndex, "TLB index register");
			assert(cpu.memoryManagementUnit.tlbInvalidIndexRegister == 1'b0, "TLB invalid index register");
			assert(cpu.memoryManagementUnit.tlbEntryHighRegister == expectedBadAddress[31:12], "TLB entry high register");
			assert(cpu.memoryManagementUnit.tlbEntryLowRegister == expectedTlbLowValue, "TLB entry low register");
			assert(cpu.memoryManagementUnit.tlbBadAddressRegister == expectedBadAddress, "TLB bad address register");

			subContext <= "executeTlbInvalidOrWriteTest finished";
		end
	endtask

	/**
	 * This task waits for the begin instruction state, then makes sure that an illegal/privileged address exception handler
	 * entry occured.
	 */
	task executeIllegalOrPrivilegedAddressTest(input[31:0] previousPc, input previousUserMode, input[4:0] expectedPriority, input[31:0] expectedBadAddress);
		begin

			/** execute the instruction under test **/
			subContext <= "executeIllegalOrPrivilegedAddressTest: wait for begin instruction state";
			#20 waitForBeginInstruction();
			
			/** ensure that we entered the handler **/
			subContext <= "executeIllegalOrPrivilegedAddressTest: assertions";
			assert(cpu.programCounterRegister.programCounter == 32'hc0000004, "PC");
			assert(cpu.processorStatusWord.processorStatusWord == {6'b0000_1_0, previousUserMode, 4'b1_010, expectedPriority, 16'b0001_0110_0000_0101}, "PSW");
			assert(cpu.generalPurposeRegisters.valueArray[30] == previousPc, "R30");
			assert(cpu.memoryManagementUnit.tlbIndexRegister == 5'd29, "TLB index register");
			assert(cpu.memoryManagementUnit.tlbInvalidIndexRegister == 1'b1, "TLB invalid index register");
			assert(cpu.memoryManagementUnit.tlbEntryHighRegister == 20'h9af00, "TLB entry high register");
			assert(cpu.memoryManagementUnit.tlbEntryLowRegister == 22'h180aa, "TLB entry low register");
			assert(cpu.memoryManagementUnit.tlbBadAddressRegister == expectedBadAddress, "TLB bad address register");

			subContext <= "executeIllegalOrPrivilegedAddressTest finished";
		end
	endtask
	
	/** simulation **/
	initial begin
		$dumpfile("cpu/CpuExceptionTest.vcd");
		$dumpvars;
		
		/** initialization **/
		busReadData <= 32'd0;
		busWait <= 1'b0;
		interrupts <= 16'b0000000000000000;
		
		/** reset **/
		#5 reset <= 1'b1;
		#20 reset <= 1'b0;
		#20 waitForBusAccess();
		
		/** initialize TLB entries specifically for this test **/
		cpu.memoryManagementUnit.tlbKeyMemory.keyArray[9] <= 20'h36937;
		cpu.memoryManagementUnit.tlbValueMemory.valueArray[9] <= {20'haaaab, 2'b10};
		cpu.memoryManagementUnit.tlbKeyMemory.keyArray[10] <= 20'h36938;
		cpu.memoryManagementUnit.tlbValueMemory.valueArray[10] <= {20'haaaac, 2'b00};
		cpu.memoryManagementUnit.tlbKeyMemory.keyArray[11] <= 20'h86936;
		cpu.memoryManagementUnit.tlbValueMemory.valueArray[11] <= {20'haaaad, 2'b01};
		cpu.memoryManagementUnit.tlbKeyMemory.keyArray[12] <= 20'h86937;
		cpu.memoryManagementUnit.tlbValueMemory.valueArray[12] <= {20'haaaae, 2'b10};
		cpu.memoryManagementUnit.tlbKeyMemory.keyArray[13] <= 20'h86938;
		cpu.memoryManagementUnit.tlbValueMemory.valueArray[13] <= {20'haaaaf, 2'b00};
		cpu.memoryManagementUnit.tlbKeyMemory.keyArray[14] <= 20'h86939;
		cpu.memoryManagementUnit.tlbValueMemory.valueArray[14] <= {20'haaab0, 2'b11};
		
		/** test **/
		assertionContext = "test 1";
		testGeneralExceptionEntry(32'hc9283744, 32'h09283744, 32'b0000_0_011_101_10110_0001_0110_0000_0101, 32'he0000004);

		/** test with interrupts disabled (should still work) **/
		assertionContext = "test IEN cleared";
		testGeneralExceptionEntry(32'hc9283744, 32'h09283744, 32'b0000_0_011_001_10110_0001_0110_0000_0101, 32'he0000004);

		/** test vector bit **/
		assertionContext = "test vector bit";
		testGeneralExceptionEntry(32'hc9283744, 32'h09283744, 32'b0000_1_011_101_10110_0001_0110_0000_0101, 32'hc0000004);

		/** test with user mode bit set **/
		assertionContext = "test user mode";
		testGeneralExceptionEntry(32'h12345000, 32'h55555000, 32'b0000_0_111_101_10110_0001_0110_0000_0101, 32'he0000004);
		
		/************************************************************************************************************************/
		
		/** bus timeout **/

		assertionContext = "bus timeout (instruction fetch)";
		prepareSpecificExceptionTest(32'hffffff00, 1'b0);
		waitForBusAccess();
		busWait <= 1'b1;
		executeSpecificExceptionExceptionTest(32'hffffff00, 1'b0, 5'h10);
		busWait <= 1'b0;

		assertionContext = "bus timeout (LDW)";
		prepareSpecificExceptionTest(32'hffffff00, 1'b0);
		waitForBusAccess();
		busReadData <= buildRRIInstruction(6'h30, 5'd0, 5'd0, 16'hfff0);
		busWait <= 1'b0; // it's not the instruction fetch that should time out...
		#20 busWait <= 1'b1; // ... but the load operation itself
		executeSpecificExceptionExceptionTest(32'hffffff00, 1'b0, 5'h10);
		busWait <= 1'b0;

		/** illegal instruction **/

		assertionContext = "illegal instruction (1e)";
		prepareSpecificExceptionTest(32'hdcdcdcdc, 1'b0);
		busReadData <= buildJInstruction(6'h1e, 26'd0);
		executeSpecificExceptionExceptionTest(32'hdcdcdcdc, 1'b0, 5'h11);

		assertionContext = "illegal instruction (3e)";
		prepareSpecificExceptionTest(32'hdcdcdcdc, 1'b0);
		busReadData <= buildJInstruction(6'h3e, 26'd0);
		executeSpecificExceptionExceptionTest(32'hdcdcdcdc, 1'b0, 5'h11);

		assertionContext = "illegal instruction (3f)";
		prepareSpecificExceptionTest(32'hdcdcdcdc, 1'b0);
		busReadData <= buildJInstruction(6'h3f, 26'd0);
		executeSpecificExceptionExceptionTest(32'hdcdcdcdc, 1'b0, 5'h11);

		assertionContext = "illegal instruction (MVFS 5)";
		prepareSpecificExceptionTest(32'hdcdcdcdc, 1'b0);
		busReadData <= buildRRIInstruction(6'h38, 5'd0, 5'd0, 16'd5);
		executeSpecificExceptionExceptionTest(32'hdcdcdcdc, 1'b0, 5'h11);

		assertionContext = "illegal instruction (MVFS 8)";
		prepareSpecificExceptionTest(32'hdcdcdcdc, 1'b0);
		busReadData <= buildRRIInstruction(6'h38, 5'd0, 5'd0, 16'd8);
		executeSpecificExceptionExceptionTest(32'hdcdcdcdc, 1'b0, 5'h11);

		assertionContext = "illegal instruction (MVTS 5)";
		prepareSpecificExceptionTest(32'hdcdcdcdc, 1'b0);
		busReadData <= buildRRIInstruction(6'h39, 5'd0, 5'd0, 16'd5);
		executeSpecificExceptionExceptionTest(32'hdcdcdcdc, 1'b0, 5'h11);

		assertionContext = "illegal instruction (MVTS 8)";
		prepareSpecificExceptionTest(32'hdcdcdcdc, 1'b0);
		busReadData <= buildRRIInstruction(6'h39, 5'd0, 5'd0, 16'd8);
		executeSpecificExceptionExceptionTest(32'hdcdcdcdc, 1'b0, 5'h11);

		/** privileged instruction **/

		assertionContext = "privileged instruction (2f = RFX)";
		prepareSpecificExceptionTest(32'h12345000, 1'b1);
		busReadData <= buildJInstruction(6'h2f, 26'd0);
		executeSpecificExceptionExceptionTest(32'h12345000, 1'b1, 5'h12);

		assertionContext = "privileged instruction (38 = MVFS)";
		prepareSpecificExceptionTest(32'h12345000, 1'b1);
		busReadData <= buildRRIInstruction(6'h38, 5'd0, 5'd0, 16'd0);
		executeSpecificExceptionExceptionTest(32'h12345000, 1'b1, 5'h12);

		assertionContext = "privileged instruction (39 = MVTS)";
		prepareSpecificExceptionTest(32'h12345000, 1'b1);
		busReadData <= buildRRIInstruction(6'h39, 5'd0, 5'd0, 16'd0);
		executeSpecificExceptionExceptionTest(32'h12345000, 1'b1, 5'h12);

		assertionContext = "privileged instruction (3a = TBS)";
		prepareSpecificExceptionTest(32'h12345000, 1'b1);
		busReadData <= buildRRIInstruction(6'h3a, 5'd0, 5'd0, 16'd0);
		executeSpecificExceptionExceptionTest(32'h12345000, 1'b1, 5'h12);

		assertionContext = "privileged instruction (3b = TBWR)";
		prepareSpecificExceptionTest(32'h12345000, 1'b1);
		busReadData <= buildRRIInstruction(6'h3b, 5'd0, 5'd0, 16'd0);
		executeSpecificExceptionExceptionTest(32'h12345000, 1'b1, 5'h12);

		assertionContext = "privileged instruction (3c = TBRI)";
		prepareSpecificExceptionTest(32'h12345000, 1'b1);
		busReadData <= buildRRIInstruction(6'h3c, 5'd0, 5'd0, 16'd0);
		executeSpecificExceptionExceptionTest(32'h12345000, 1'b1, 5'h12);

		assertionContext = "privileged instruction (3d = TBWI)";
		prepareSpecificExceptionTest(32'h12345000, 1'b1);
		busReadData <= buildRRIInstruction(6'h3d, 5'd0, 5'd0, 16'd0);
		executeSpecificExceptionExceptionTest(32'h12345000, 1'b1, 5'h12);

		/** division / remainder by zero **/

		assertionContext = "division by zero (DIV)";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'h91827364;
		prepareSpecificExceptionTest(32'hdcdcdcdc, 1'b0);
		busReadData <= buildRRRInstruction(6'h08, 5'd1, 5'd0, 5'd31);
		executeSpecificExceptionExceptionTest(32'hdcdcdcdc, 1'b0, 5'h13);
		assert(cpu.generalPurposeRegisters.valueArray[31] == 32'h91827364, "destination register not affected");

		assertionContext = "division by zero (DIVI)";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'h91827364;
		prepareSpecificExceptionTest(32'hdcdcdcdc, 1'b0);
		busReadData <= buildRRIInstruction(6'h09, 5'd1, 5'd31, 16'd0);
		executeSpecificExceptionExceptionTest(32'hdcdcdcdc, 1'b0, 5'h13);
		assert(cpu.generalPurposeRegisters.valueArray[31] == 32'h91827364, "destination register not affected");

		assertionContext = "division by zero (DIVU)";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'h91827364;
		prepareSpecificExceptionTest(32'hdcdcdcdc, 1'b0);
		busReadData <= buildRRRInstruction(6'h0a, 5'd1, 5'd0, 5'd31);
		executeSpecificExceptionExceptionTest(32'hdcdcdcdc, 1'b0, 5'h13);
		assert(cpu.generalPurposeRegisters.valueArray[31] == 32'h91827364, "destination register not affected");

		assertionContext = "division by zero (DIVUI)";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'h91827364;
		prepareSpecificExceptionTest(32'hdcdcdcdc, 1'b0);
		busReadData <= buildRRIInstruction(6'h0b, 5'd1, 5'd31, 16'd0);
		executeSpecificExceptionExceptionTest(32'hdcdcdcdc, 1'b0, 5'h13);
		assert(cpu.generalPurposeRegisters.valueArray[31] == 32'h91827364, "destination register not affected");

		assertionContext = "division by zero (REM)";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'h91827364;
		prepareSpecificExceptionTest(32'hdcdcdcdc, 1'b0);
		busReadData <= buildRRRInstruction(6'h0c, 5'd1, 5'd0, 5'd31);
		executeSpecificExceptionExceptionTest(32'hdcdcdcdc, 1'b0, 5'h13);
		assert(cpu.generalPurposeRegisters.valueArray[31] == 32'h91827364, "destination register not affected");

		assertionContext = "division by zero (REMI)";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'h91827364;
		prepareSpecificExceptionTest(32'hdcdcdcdc, 1'b0);
		busReadData <= buildRRIInstruction(6'h0d, 5'd1, 5'd31, 16'd0);
		executeSpecificExceptionExceptionTest(32'hdcdcdcdc, 1'b0, 5'h13);
		assert(cpu.generalPurposeRegisters.valueArray[31] == 32'h91827364, "destination register not affected");

		assertionContext = "division by zero (REMU)";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'h91827364;
		prepareSpecificExceptionTest(32'hdcdcdcdc, 1'b0);
		busReadData <= buildRRRInstruction(6'h0e, 5'd1, 5'd0, 5'd31);
		executeSpecificExceptionExceptionTest(32'hdcdcdcdc, 1'b0, 5'h13);
		assert(cpu.generalPurposeRegisters.valueArray[31] == 32'h91827364, "destination register not affected");

		assertionContext = "division by zero (REMUI)";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'h91827364;
		prepareSpecificExceptionTest(32'hdcdcdcdc, 1'b0);
		busReadData <= buildRRIInstruction(6'h0f, 5'd1, 5'd31, 16'd0);
		executeSpecificExceptionExceptionTest(32'hdcdcdcdc, 1'b0, 5'h13);
		assert(cpu.generalPurposeRegisters.valueArray[31] == 32'h91827364, "destination register not affected");

		/** trap **/

		assertionContext = "trap (kernel mode)";
		prepareSpecificExceptionTest(32'hdcdcdcdc, 1'b0);
		busReadData <= buildJInstruction(6'h2e, 26'd0);
		executeSpecificExceptionExceptionTest(32'hdcdcdcdc, 1'b0, 5'h14);

		assertionContext = "trap (user mode)";
		prepareSpecificExceptionTest(32'h12345000, 1'b1);
		busReadData <= buildJInstruction(6'h2e, 26'd0);
		executeSpecificExceptionExceptionTest(32'h12345000, 1'b1, 5'h14);

		/** TLB miss **/

		assertionContext = "TLB miss (instruction fetch, user space, kernel mode)";
		prepareSpecificExceptionTest(32'h78978000, 1'b0);
		busReadData <= buildRRRInstruction(6'h0, 5'd0, 5'd0, 5'd0);
		executeTlbMissTest(32'h78978000, 32'hc0000008, 1'b0, 32'h78978000);

		assertionContext = "TLB miss (instruction fetch, user space, user mode)";
		prepareSpecificExceptionTest(32'h78978000, 1'b1);
		busReadData <= buildRRRInstruction(6'h0, 5'd0, 5'd0, 5'd0);
		executeTlbMissTest(32'h78978000, 32'hc0000008, 1'b1, 32'h78978000);

		assertionContext = "TLB miss (instruction fetch, kernel space, kernel mode)";
		prepareSpecificExceptionTest(32'h88978000, 1'b0);
		busReadData <= buildRRRInstruction(6'h0, 5'd0, 5'd0, 5'd0);
		executeTlbMissTest(32'h88978000, 32'hc0000004, 1'b0, 32'h88978000);



		assertionContext = "TLB miss (LDW, user space, kernel mode)";
		prepareSpecificExceptionTest(32'hdcdcdcdc, 1'b0);
		busReadData <= buildRRIInstruction(6'h30, 5'd0, 5'd0, 16'h1000);
		executeTlbMissTest(32'hdcdcdcdc, 32'hc0000008, 1'b0, 32'h00001000);

		assertionContext = "TLB miss (LDW, user space, user mode)";
		prepareSpecificExceptionTest(32'h12345000, 1'b1);
		busReadData <= buildRRIInstruction(6'h30, 5'd0, 5'd0, 16'h1000);
		executeTlbMissTest(32'h12345000, 32'hc0000008, 1'b1, 32'h00001000);

		assertionContext = "TLB miss (LDW, kernel space, kernel mode)";
		prepareSpecificExceptionTest(32'hdcdcdcdc, 1'b0);
		busReadData <= buildRRIInstruction(6'h30, 5'd9, 5'd0, 16'h0000);
		executeTlbMissTest(32'hdcdcdcdc, 32'hc0000004, 1'b0, 32'h80000000);

		/** TLB write **/

		assertionContext = "TLB write (STW, user space, kernel mode)";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'h36936000;
		prepareSpecificExceptionTest(32'hdcdcdcdc, 1'b0);
		busReadData <= buildRRIInstruction(6'h35, 5'd31, 5'd0, 16'h0000);
		executeTlbInvalidOrWriteTest(32'hdcdcdcdc, 1'b0, `EXCEPTION_CODE_TLB_WRITE, 5'd8, 32'h36936000, {20'haaaaa, 2'b01});

		assertionContext = "TLB write (STW, user space, user mode)";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'h36936000;
		prepareSpecificExceptionTest(32'h12345000, 1'b1);
		busReadData <= buildRRIInstruction(6'h35, 5'd31, 5'd0, 16'h0000);
		executeTlbInvalidOrWriteTest(32'h12345000, 1'b1, `EXCEPTION_CODE_TLB_WRITE, 5'd8, 32'h36936000, {20'haaaaa, 2'b01});

		assertionContext = "TLB write (STW, kernel space, kernel mode)";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'h86936000;
		prepareSpecificExceptionTest(32'hdcdcdcdc, 1'b0);
		busReadData <= buildRRIInstruction(6'h35, 5'd31, 5'd0, 16'h0000);
		executeTlbInvalidOrWriteTest(32'hdcdcdcdc, 1'b0, `EXCEPTION_CODE_TLB_WRITE, 5'd11, 32'h86936000, {20'haaaad, 2'b01});

		/** TLB invalid **/

		assertionContext = "TLB invalid (instruction fetch, user space, kernel mode)";
		prepareSpecificExceptionTest(32'h36937000, 1'b0);
		busReadData <= buildRRRInstruction(6'h0, 5'd0, 5'd0, 5'd0);
		executeTlbInvalidOrWriteTest(32'h36937000, 1'b0, `EXCEPTION_CODE_TLB_INVALID, 5'd9, 32'h36937000, {20'haaaab, 2'b10});

		assertionContext = "TLB invalid (instruction fetch, user space, user mode)";
		prepareSpecificExceptionTest(32'h36937000, 1'b1);
		busReadData <= buildRRRInstruction(6'h0, 5'd0, 5'd0, 5'd0);
		executeTlbInvalidOrWriteTest(32'h36937000, 1'b1, `EXCEPTION_CODE_TLB_INVALID, 5'd9, 32'h36937000, {20'haaaab, 2'b10});

		assertionContext = "TLB invalid (instruction fetch, kernel space, kernel mode)";
		prepareSpecificExceptionTest(32'h86937000, 1'b0);
		busReadData <= buildRRRInstruction(6'h0, 5'd0, 5'd0, 5'd0);
		executeTlbInvalidOrWriteTest(32'h86937000, 1'b0, `EXCEPTION_CODE_TLB_INVALID, 5'd12, 32'h86937000, {20'haaaae, 2'b10});
		
		

		assertionContext = "TLB invalid (LDW, user space, kernel mode)";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'h36937000;
		prepareSpecificExceptionTest(32'hdcdcdcdc, 1'b0);
		busReadData <= buildRRIInstruction(6'h30, 5'd31, 5'd0, 16'h0000);
		executeTlbInvalidOrWriteTest(32'hdcdcdcdc, 1'b0, `EXCEPTION_CODE_TLB_INVALID, 5'd9, 32'h36937000, {20'haaaab, 2'b10});

		assertionContext = "TLB invalid (LDW, user space, user mode)";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'h36937000;
		prepareSpecificExceptionTest(32'h12345000, 1'b1);
		busReadData <= buildRRIInstruction(6'h30, 5'd31, 5'd0, 16'h0000);
		executeTlbInvalidOrWriteTest(32'h12345000, 1'b1, `EXCEPTION_CODE_TLB_INVALID, 5'd9, 32'h36937000, {20'haaaab, 2'b10});

		assertionContext = "TLB invalid (LDW, kernel space, kernel mode)";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'h86937000;
		prepareSpecificExceptionTest(32'hdcdcdcdc, 1'b0);
		busReadData <= buildRRIInstruction(6'h30, 5'd31, 5'd0, 16'h0000);
		executeTlbInvalidOrWriteTest(32'hdcdcdcdc, 1'b0, `EXCEPTION_CODE_TLB_INVALID, 5'd12, 32'h86937000, {20'haaaae, 2'b10});
		
		

		assertionContext = "TLB invalid (STW, user space, kernel mode)";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'h36937000;
		prepareSpecificExceptionTest(32'hdcdcdcdc, 1'b0);
		busReadData <= buildRRIInstruction(6'h35, 5'd31, 5'd0, 16'h0000);
		executeTlbInvalidOrWriteTest(32'hdcdcdcdc, 1'b0, `EXCEPTION_CODE_TLB_INVALID, 5'd9, 32'h36937000, {20'haaaab, 2'b10});

		assertionContext = "TLB invalid (STW, user space, user mode)";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'h36937000;
		prepareSpecificExceptionTest(32'h12345000, 1'b1);
		busReadData <= buildRRIInstruction(6'h35, 5'd31, 5'd0, 16'h0000);
		executeTlbInvalidOrWriteTest(32'h12345000, 1'b1, `EXCEPTION_CODE_TLB_INVALID, 5'd9, 32'h36937000, {20'haaaab, 2'b10});

		assertionContext = "TLB invalid (STW, kernel space, kernel mode)";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'h86937000;
		prepareSpecificExceptionTest(32'hdcdcdcdc, 1'b0);
		busReadData <= buildRRIInstruction(6'h35, 5'd31, 5'd0, 16'h0000);
		executeTlbInvalidOrWriteTest(32'hdcdcdcdc, 1'b0, `EXCEPTION_CODE_TLB_INVALID, 5'd12, 32'h86937000, {20'haaaae, 2'b10});



		assertionContext = "TLB invalid+write (STW, user space, kernel mode)";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'h36938000;
		prepareSpecificExceptionTest(32'hdcdcdcdc, 1'b0);
		busReadData <= buildRRIInstruction(6'h35, 5'd31, 5'd0, 16'h0000);
		executeTlbInvalidOrWriteTest(32'hdcdcdcdc, 1'b0, `EXCEPTION_CODE_TLB_INVALID, 5'd10, 32'h36938000, {20'haaaac, 2'b00});

		assertionContext = "TLB invalid+write (STW, user space, user mode)";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'h36938000;
		prepareSpecificExceptionTest(32'h12345000, 1'b1);
		busReadData <= buildRRIInstruction(6'h35, 5'd31, 5'd0, 16'h0000);
		executeTlbInvalidOrWriteTest(32'h12345000, 1'b1, `EXCEPTION_CODE_TLB_INVALID, 5'd10, 32'h36938000, {20'haaaac, 2'b00});

		assertionContext = "TLB invalid+write (STW, kernel space, kernel mode)";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'h86938000;
		prepareSpecificExceptionTest(32'hdcdcdcdc, 1'b0);
		busReadData <= buildRRIInstruction(6'h35, 5'd31, 5'd0, 16'h0000);
		executeTlbInvalidOrWriteTest(32'hdcdcdcdc, 1'b0, `EXCEPTION_CODE_TLB_INVALID, 5'd13, 32'h86938000, {20'haaaaf, 2'b00});

		/** illegal address **/

		assertionContext = "illegal address (instruction fetch, user space, kernel mode)";
		prepareSpecificExceptionTest(32'h12345001, 1'b0);
		busReadData <= buildRRRInstruction(6'h0, 5'd0, 5'd0, 5'd0);
		executeIllegalOrPrivilegedAddressTest(32'h12345001, 1'b0, `EXCEPTION_CODE_ILLEGAL_ADDRESS, 32'h12345001);

		assertionContext = "illegal address (instruction fetch, user space, user mode)";
		prepareSpecificExceptionTest(32'h12345001, 1'b1);
		busReadData <= buildRRRInstruction(6'h0, 5'd0, 5'd0, 5'd0);
		executeIllegalOrPrivilegedAddressTest(32'h12345001, 1'b1, `EXCEPTION_CODE_ILLEGAL_ADDRESS, 32'h12345001);

		assertionContext = "illegal address (instruction fetch, kernel space, kernel mode)";
		prepareSpecificExceptionTest(32'h86939001, 1'b0);
		busReadData <= buildRRRInstruction(6'h0, 5'd0, 5'd0, 5'd0);
		executeIllegalOrPrivilegedAddressTest(32'h86939001, 1'b0, `EXCEPTION_CODE_ILLEGAL_ADDRESS, 32'h86939001);



		assertionContext = "illegal address + TLB miss (instruction fetch, user space, kernel mode)";
		prepareSpecificExceptionTest(32'h12346001, 1'b0);
		busReadData <= buildRRRInstruction(6'h0, 5'd0, 5'd0, 5'd0);
		executeIllegalOrPrivilegedAddressTest(32'h12346001, 1'b0, `EXCEPTION_CODE_ILLEGAL_ADDRESS, 32'h12346001);

		assertionContext = "illegal address + TLB miss (instruction fetch, user space, user mode)";
		prepareSpecificExceptionTest(32'h12346001, 1'b1);
		busReadData <= buildRRRInstruction(6'h0, 5'd0, 5'd0, 5'd0);
		executeIllegalOrPrivilegedAddressTest(32'h12346001, 1'b1, `EXCEPTION_CODE_ILLEGAL_ADDRESS, 32'h12346001);

		assertionContext = "illegal address + TLB miss (instruction fetch, kernel space, kernel mode)";
		prepareSpecificExceptionTest(32'h8693a001, 1'b0);
		busReadData <= buildRRRInstruction(6'h0, 5'd0, 5'd0, 5'd0);
		executeIllegalOrPrivilegedAddressTest(32'h8693a001, 1'b0, `EXCEPTION_CODE_ILLEGAL_ADDRESS, 32'h8693a001);



		assertionContext = "illegal address + TLB invalid (instruction fetch, user space, kernel mode)";
		prepareSpecificExceptionTest(32'h36937001, 1'b0);
		busReadData <= buildRRRInstruction(6'h0, 5'd0, 5'd0, 5'd0);
		executeIllegalOrPrivilegedAddressTest(32'h36937001, 1'b0, `EXCEPTION_CODE_ILLEGAL_ADDRESS, 32'h36937001);

		assertionContext = "illegal address + TLB invalid (instruction fetch, user space, user mode)";
		prepareSpecificExceptionTest(32'h36937001, 1'b1);
		busReadData <= buildRRRInstruction(6'h0, 5'd0, 5'd0, 5'd0);
		executeIllegalOrPrivilegedAddressTest(32'h36937001, 1'b1, `EXCEPTION_CODE_ILLEGAL_ADDRESS, 32'h36937001);

		assertionContext = "illegal address + TLB invalid (instruction fetch, kernel space, kernel mode)";
		prepareSpecificExceptionTest(32'h86937001, 1'b0);
		busReadData <= buildRRRInstruction(6'h0, 5'd0, 5'd0, 5'd0);
		executeIllegalOrPrivilegedAddressTest(32'h86937001, 1'b0, `EXCEPTION_CODE_ILLEGAL_ADDRESS, 32'h86937001);



		assertionContext = "illegal address (LDW, user space, kernel mode)";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'h12345001;
		prepareSpecificExceptionTest(32'h12345000, 1'b0);
		busReadData <= buildRRIInstruction(6'h30, 5'd31, 5'd0, 16'h0000);
		executeIllegalOrPrivilegedAddressTest(32'h12345000, 1'b0, `EXCEPTION_CODE_ILLEGAL_ADDRESS, 32'h12345001);

		assertionContext = "illegal address (LDW, user space, user mode)";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'h12345001;
		prepareSpecificExceptionTest(32'h12345000, 1'b1);
		busReadData <= buildRRIInstruction(6'h30, 5'd31, 5'd0, 16'h0000);
		executeIllegalOrPrivilegedAddressTest(32'h12345000, 1'b1, `EXCEPTION_CODE_ILLEGAL_ADDRESS, 32'h12345001);

		assertionContext = "illegal address (LDW, kernel space, kernel mode)";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'h86939001;
		prepareSpecificExceptionTest(32'h12345000, 1'b0);
		busReadData <= buildRRIInstruction(6'h30, 5'd31, 5'd0, 16'h0000);
		executeIllegalOrPrivilegedAddressTest(32'h12345000, 1'b0, `EXCEPTION_CODE_ILLEGAL_ADDRESS, 32'h86939001);



		assertionContext = "illegal address + TLB miss (LDW, user space, kernel mode)";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'h12346001;
		prepareSpecificExceptionTest(32'h12345000, 1'b0);
		busReadData <= buildRRIInstruction(6'h30, 5'd31, 5'd0, 16'h0000);
		executeIllegalOrPrivilegedAddressTest(32'h12345000, 1'b0, `EXCEPTION_CODE_ILLEGAL_ADDRESS, 32'h12346001);

		assertionContext = "illegal address + TLB miss (LDW, user space, user mode)";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'h12346001;
		prepareSpecificExceptionTest(32'h12345000, 1'b1);
		busReadData <= buildRRIInstruction(6'h30, 5'd31, 5'd0, 16'h0000);
		executeIllegalOrPrivilegedAddressTest(32'h12345000, 1'b1, `EXCEPTION_CODE_ILLEGAL_ADDRESS, 32'h12346001);

		assertionContext = "illegal address + TLB miss (LDW, kernel space, kernel mode)";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'h8693a001;
		prepareSpecificExceptionTest(32'h12345000, 1'b0);
		busReadData <= buildRRIInstruction(6'h30, 5'd31, 5'd0, 16'h0000);
		executeIllegalOrPrivilegedAddressTest(32'h12345000, 1'b0, `EXCEPTION_CODE_ILLEGAL_ADDRESS, 32'h8693a001);



		assertionContext = "illegal address + TLB invalid + TLB write (LDW, user space, kernel mode)";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'h36938001;
		prepareSpecificExceptionTest(32'h12345000, 1'b0);
		busReadData <= buildRRIInstruction(6'h30, 5'd31, 5'd0, 16'h0000);
		executeIllegalOrPrivilegedAddressTest(32'h12345000, 1'b0, `EXCEPTION_CODE_ILLEGAL_ADDRESS, 32'h36938001);

		assertionContext = "illegal address + TLB invalid + TLB write (LDW, user space, user mode)";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'h36938001;
		prepareSpecificExceptionTest(32'h12345000, 1'b1);
		busReadData <= buildRRIInstruction(6'h30, 5'd31, 5'd0, 16'h0000);
		executeIllegalOrPrivilegedAddressTest(32'h12345000, 1'b1, `EXCEPTION_CODE_ILLEGAL_ADDRESS, 32'h36938001);

		assertionContext = "illegal address + TLB invalid + TLB write (LDW, kernel space, kernel mode)";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'h86938001;
		prepareSpecificExceptionTest(32'h12345000, 1'b0);
		busReadData <= buildRRIInstruction(6'h30, 5'd31, 5'd0, 16'h0000);
		executeIllegalOrPrivilegedAddressTest(32'h12345000, 1'b0, `EXCEPTION_CODE_ILLEGAL_ADDRESS, 32'h86938001);



		assertionContext = "illegal address (LDW, aligned + 1)";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'hc0000001;
		prepareSpecificExceptionTest(32'hdcdcdcdc, 1'b0);
		busReadData <= buildRRIInstruction(6'h30, 5'd31, 5'd0, 16'h0000);
		executeIllegalOrPrivilegedAddressTest(32'hdcdcdcdc, 1'b0, `EXCEPTION_CODE_ILLEGAL_ADDRESS, 32'hc0000001);

		assertionContext = "illegal address (LDW, aligned + 2)";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'hc0000002;
		prepareSpecificExceptionTest(32'hdcdcdcdc, 1'b0);
		busReadData <= buildRRIInstruction(6'h30, 5'd31, 5'd0, 16'h0000);
		executeIllegalOrPrivilegedAddressTest(32'hdcdcdcdc, 1'b0, `EXCEPTION_CODE_ILLEGAL_ADDRESS, 32'hc0000002);

		assertionContext = "illegal address (LDW, aligned + 3)";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'hc0000003;
		prepareSpecificExceptionTest(32'hdcdcdcdc, 1'b0);
		busReadData <= buildRRIInstruction(6'h30, 5'd31, 5'd0, 16'h0000);
		executeIllegalOrPrivilegedAddressTest(32'hdcdcdcdc, 1'b0, `EXCEPTION_CODE_ILLEGAL_ADDRESS, 32'hc0000003);

		assertionContext = "illegal address (LDH, aligned + 1)";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'hc0000001;
		prepareSpecificExceptionTest(32'hdcdcdcdc, 1'b0);
		busReadData <= buildRRIInstruction(6'h31, 5'd31, 5'd0, 16'h0000);
		executeIllegalOrPrivilegedAddressTest(32'hdcdcdcdc, 1'b0, `EXCEPTION_CODE_ILLEGAL_ADDRESS, 32'hc0000001);

		assertionContext = "illegal address (LDH, aligned + 3)";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'hc0000003;
		prepareSpecificExceptionTest(32'hdcdcdcdc, 1'b0);
		busReadData <= buildRRIInstruction(6'h31, 5'd31, 5'd0, 16'h0000);
		executeIllegalOrPrivilegedAddressTest(32'hdcdcdcdc, 1'b0, `EXCEPTION_CODE_ILLEGAL_ADDRESS, 32'hc0000003);

		assertionContext = "illegal address (LDHU, aligned + 1)";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'hc0000001;
		prepareSpecificExceptionTest(32'hdcdcdcdc, 1'b0);
		busReadData <= buildRRIInstruction(6'h32, 5'd31, 5'd0, 16'h0000);
		executeIllegalOrPrivilegedAddressTest(32'hdcdcdcdc, 1'b0, `EXCEPTION_CODE_ILLEGAL_ADDRESS, 32'hc0000001);

		assertionContext = "illegal address (LDHU, aligned + 3)";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'hc0000003;
		prepareSpecificExceptionTest(32'hdcdcdcdc, 1'b0);
		busReadData <= buildRRIInstruction(6'h32, 5'd31, 5'd0, 16'h0000);
		executeIllegalOrPrivilegedAddressTest(32'hdcdcdcdc, 1'b0, `EXCEPTION_CODE_ILLEGAL_ADDRESS, 32'hc0000003);



		assertionContext = "illegal address (STW, aligned + 1)";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'hc0000001;
		prepareSpecificExceptionTest(32'hdcdcdcdc, 1'b0);
		busReadData <= buildRRIInstruction(6'h35, 5'd31, 5'd0, 16'h0000);
		executeIllegalOrPrivilegedAddressTest(32'hdcdcdcdc, 1'b0, `EXCEPTION_CODE_ILLEGAL_ADDRESS, 32'hc0000001);

		assertionContext = "illegal address (STW, aligned + 2)";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'hc0000002;
		prepareSpecificExceptionTest(32'hdcdcdcdc, 1'b0);
		busReadData <= buildRRIInstruction(6'h35, 5'd31, 5'd0, 16'h0000);
		executeIllegalOrPrivilegedAddressTest(32'hdcdcdcdc, 1'b0, `EXCEPTION_CODE_ILLEGAL_ADDRESS, 32'hc0000002);

		assertionContext = "illegal address (STW, aligned + 3)";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'hc0000003;
		prepareSpecificExceptionTest(32'hdcdcdcdc, 1'b0);
		busReadData <= buildRRIInstruction(6'h35, 5'd31, 5'd0, 16'h0000);
		executeIllegalOrPrivilegedAddressTest(32'hdcdcdcdc, 1'b0, `EXCEPTION_CODE_ILLEGAL_ADDRESS, 32'hc0000003);

		assertionContext = "illegal address (STH, aligned + 1)";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'hc0000001;
		prepareSpecificExceptionTest(32'hdcdcdcdc, 1'b0);
		busReadData <= buildRRIInstruction(6'h36, 5'd31, 5'd0, 16'h0000);
		executeIllegalOrPrivilegedAddressTest(32'hdcdcdcdc, 1'b0, `EXCEPTION_CODE_ILLEGAL_ADDRESS, 32'hc0000001);

		assertionContext = "illegal address (STH, aligned + 3)";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'hc0000003;
		prepareSpecificExceptionTest(32'hdcdcdcdc, 1'b0);
		busReadData <= buildRRIInstruction(6'h36, 5'd31, 5'd0, 16'h0000);
		executeIllegalOrPrivilegedAddressTest(32'hdcdcdcdc, 1'b0, `EXCEPTION_CODE_ILLEGAL_ADDRESS, 32'hc0000003);

		/** privileged address **/

		assertionContext = "privileged address (instruction fetch)";
		prepareSpecificExceptionTest(32'hdefdef00, 1'b1);
		busReadData <= buildRRRInstruction(6'h0, 5'd0, 5'd0, 5'd0);
		executeIllegalOrPrivilegedAddressTest(32'hdefdef00, 1'b1, `EXCEPTION_CODE_PRIVILEGED_ADDRESS, 32'hdefdef00);

		assertionContext = "privileged address + TLB miss (instruction fetch)";
		prepareSpecificExceptionTest(32'h89898900, 1'b1);
		busReadData <= buildRRRInstruction(6'h0, 5'd0, 5'd0, 5'd0);
		executeIllegalOrPrivilegedAddressTest(32'h89898900, 1'b1, `EXCEPTION_CODE_PRIVILEGED_ADDRESS, 32'h89898900);

		assertionContext = "privileged address + TLB invalid (instruction fetch)";
		prepareSpecificExceptionTest(32'h86938000, 1'b1);
		busReadData <= buildRRRInstruction(6'h0, 5'd0, 5'd0, 5'd0);
		executeIllegalOrPrivilegedAddressTest(32'h86938000, 1'b1, `EXCEPTION_CODE_PRIVILEGED_ADDRESS, 32'h86938000);

		assertionContext = "privileged address + illegal address (instruction fetch)";
		prepareSpecificExceptionTest(32'hdefdef01, 1'b1);
		busReadData <= buildRRRInstruction(6'h0, 5'd0, 5'd0, 5'd0);
		executeIllegalOrPrivilegedAddressTest(32'hdefdef01, 1'b1, `EXCEPTION_CODE_ILLEGAL_ADDRESS, 32'hdefdef01);

		assertionContext = "privileged address (LDW)";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'hc0102030;
		prepareSpecificExceptionTest(32'h12345000, 1'b1);
		busReadData <= buildRRIInstruction(6'h30, 5'd31, 5'd0, 16'h0000);
		executeIllegalOrPrivilegedAddressTest(32'h12345000, 1'b1, `EXCEPTION_CODE_PRIVILEGED_ADDRESS, 32'hc0102030);

		assertionContext = "privileged address + TLB miss (LDW)";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'h80102030;
		prepareSpecificExceptionTest(32'h12345000, 1'b1);
		busReadData <= buildRRIInstruction(6'h30, 5'd31, 5'd0, 16'h0000);
		executeIllegalOrPrivilegedAddressTest(32'h12345000, 1'b1, `EXCEPTION_CODE_PRIVILEGED_ADDRESS, 32'h80102030);

		assertionContext = "privileged address + TLB invalid (LDW)";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'h86938000;
		prepareSpecificExceptionTest(32'h12345000, 1'b1);
		busReadData <= buildRRIInstruction(6'h30, 5'd31, 5'd0, 16'h0000);
		executeIllegalOrPrivilegedAddressTest(32'h12345000, 1'b1, `EXCEPTION_CODE_PRIVILEGED_ADDRESS, 32'h86938000);

		assertionContext = "privileged address + TLB invalid + TLB write (STW)";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'h86938000;
		prepareSpecificExceptionTest(32'h12345000, 1'b1);
		busReadData <= buildRRIInstruction(6'h35, 5'd31, 5'd0, 16'h0000);
		executeIllegalOrPrivilegedAddressTest(32'h12345000, 1'b1, `EXCEPTION_CODE_PRIVILEGED_ADDRESS, 32'h86938000);

		assertionContext = "privileged address + illegal address (LDW)";
		cpu.generalPurposeRegisters.valueArray[31] <= 32'hdefdef01;
		prepareSpecificExceptionTest(32'h12345000, 1'b1);
		busReadData <= buildRRIInstruction(6'h30, 5'd31, 5'd0, 16'h0000);
		executeIllegalOrPrivilegedAddressTest(32'h12345000, 1'b1, `EXCEPTION_CODE_ILLEGAL_ADDRESS, 32'hdefdef01);

		/** done **/
		#100 $finish;
	end

endmodule
