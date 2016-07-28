/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.cpu;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * 
 */
public class CpuPrivilegedInstructionTest extends AbstractCpuTest {

	/**
	 * 
	 */
	private void executeInstruction() {
		cpu.reset();
		for (int i=0; i<32; i++) {
			cpu.getMemoryManagementUnit().setTlbEntry(i, 0xffffffff, 0, false);
		}
		cpu.getMemoryManagementUnit().setTlbEntry(0, 0, 3, false);
		cpu.getPc().setValue(0x00000000, true);
		cpu.getSpecialRegisters().write(ISpecialRegisterFile.INDEX_PSW, ProcessorStatusWord.USER_MODE_BIT, false);
		cpu.getGeneralRegisters().write(1, 4, false);
		cpu.getGeneralRegisters().write(2, 8, false);
		cpu.step();
	}

	/**
	 * 
	 */
	private void testDisallowedUserModeInstruction() {
		executeInstruction();
		int expectedPriority = CpuException.CODE_PRIVILEGED_INSTRUCTION + 16;
		int expectedPsw = (expectedPriority << 16) | ProcessorStatusWord.PREVIOUS_USER_MODE_BIT;
		assertEquals(expectedPsw, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_PSW, false));
	}

	/**
	 * 
	 */
	private void testDisallowedUserModeInstruction(int opcode) {
		addRRI(opcode, 0, 0, 1);
		testDisallowedUserModeInstruction();
	}

	/**
	 * 
	 */
	private void testAllowedUserModeInstruction() {
		executeInstruction();
		assertEquals(ProcessorStatusWord.USER_MODE_BIT, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_PSW, false));
	}

	/**
	 * 
	 */
	private void testAllowedUserModeRRRInstruction(int opcode) {
		addRRR(opcode, 1, 2, 3);
		testAllowedUserModeInstruction();
	}

	/**
	 * 
	 */
	private void testAllowedUserModeRRI0Instruction(int opcode) {
		addRRI(opcode, 1, 3, 0);
		testAllowedUserModeInstruction();
	}

	/**
	 * 
	 */
	private void testAllowedUserModeLoadInstruction(int opcode) {
		addRRI(opcode, 1, 3, 0);
		bus.addReadValues(0);
		testAllowedUserModeInstruction();
	}

	/**
	 * 
	 */
	@Test
	public void testAllowedUserModeInstructions() {
		testAllowedUserModeRRRInstruction(Instruction.OPCODE_ADD);
		testAllowedUserModeRRRInstruction(Instruction.OPCODE_ADDI);
		testAllowedUserModeRRRInstruction(Instruction.OPCODE_SUB);
		testAllowedUserModeRRRInstruction(Instruction.OPCODE_SUBI);
		testAllowedUserModeRRRInstruction(Instruction.OPCODE_MUL);
		testAllowedUserModeRRRInstruction(Instruction.OPCODE_MULI);
		testAllowedUserModeRRRInstruction(Instruction.OPCODE_MULU);
		testAllowedUserModeRRRInstruction(Instruction.OPCODE_MULUI);
		testAllowedUserModeRRRInstruction(Instruction.OPCODE_DIV);
		testAllowedUserModeRRRInstruction(Instruction.OPCODE_DIVI);
		testAllowedUserModeRRRInstruction(Instruction.OPCODE_DIVU);
		testAllowedUserModeRRRInstruction(Instruction.OPCODE_DIVUI);
		testAllowedUserModeRRRInstruction(Instruction.OPCODE_REM);
		testAllowedUserModeRRRInstruction(Instruction.OPCODE_REMI);
		testAllowedUserModeRRRInstruction(Instruction.OPCODE_REMU);
		testAllowedUserModeRRRInstruction(Instruction.OPCODE_REMUI);
		testAllowedUserModeRRRInstruction(Instruction.OPCODE_AND);
		testAllowedUserModeRRRInstruction(Instruction.OPCODE_ANDI);
		testAllowedUserModeRRRInstruction(Instruction.OPCODE_OR);
		testAllowedUserModeRRRInstruction(Instruction.OPCODE_ORI);
		testAllowedUserModeRRRInstruction(Instruction.OPCODE_XOR);
		testAllowedUserModeRRRInstruction(Instruction.OPCODE_XORI);
		testAllowedUserModeRRRInstruction(Instruction.OPCODE_XNOR);
		testAllowedUserModeRRRInstruction(Instruction.OPCODE_XNORI);
		testAllowedUserModeRRRInstruction(Instruction.OPCODE_SLL);
		testAllowedUserModeRRRInstruction(Instruction.OPCODE_SLLI);
		testAllowedUserModeRRRInstruction(Instruction.OPCODE_SLR);
		testAllowedUserModeRRRInstruction(Instruction.OPCODE_SLRI);
		testAllowedUserModeRRRInstruction(Instruction.OPCODE_SAR);
		testAllowedUserModeRRRInstruction(Instruction.OPCODE_SARI);
		testAllowedUserModeRRRInstruction(Instruction.OPCODE_LDHI);
		testAllowedUserModeRRI0Instruction(Instruction.OPCODE_BEQ);
		testAllowedUserModeRRI0Instruction(Instruction.OPCODE_BNE);
		testAllowedUserModeRRI0Instruction(Instruction.OPCODE_BLT);
		testAllowedUserModeRRI0Instruction(Instruction.OPCODE_BLTU);
		testAllowedUserModeRRI0Instruction(Instruction.OPCODE_BLE);
		testAllowedUserModeRRI0Instruction(Instruction.OPCODE_BLEU);
		testAllowedUserModeRRI0Instruction(Instruction.OPCODE_BGT);
		testAllowedUserModeRRI0Instruction(Instruction.OPCODE_BGTU);
		testAllowedUserModeRRI0Instruction(Instruction.OPCODE_BGE);
		testAllowedUserModeRRI0Instruction(Instruction.OPCODE_BGEU);
		addJ(Instruction.OPCODE_J, 0);
		testAllowedUserModeInstruction();
		testAllowedUserModeRRRInstruction(Instruction.OPCODE_JR);
		addJ(Instruction.OPCODE_JAL, 0);
		testAllowedUserModeInstruction();
		testAllowedUserModeRRRInstruction(Instruction.OPCODE_JALR);
		testAllowedUserModeRRI0Instruction(Instruction.OPCODE_STW);
		testAllowedUserModeRRI0Instruction(Instruction.OPCODE_STH);
		testAllowedUserModeRRI0Instruction(Instruction.OPCODE_STB);
		testAllowedUserModeLoadInstruction(Instruction.OPCODE_LDW);
		testAllowedUserModeLoadInstruction(Instruction.OPCODE_LDH);
		testAllowedUserModeLoadInstruction(Instruction.OPCODE_LDHU);
		testAllowedUserModeLoadInstruction(Instruction.OPCODE_LDB);
		testAllowedUserModeLoadInstruction(Instruction.OPCODE_LDBU);
	}
	
	/**
	 * The TRAP instruction is allowed yet causes an exception,
	 * so we can just check that the exception code is not
	 * "privileged instruction" but "trap".
	 */
	@Test
	public void testTrap() {
		addRRR(Instruction.OPCODE_TRAP, 0, 0, 0);
		executeInstruction();
		int expectedPriority = CpuException.CODE_TRAP + 16;
		int expectedPsw = (expectedPriority << 16) | ProcessorStatusWord.PREVIOUS_USER_MODE_BIT;
		assertEquals(expectedPsw, cpu.getSpecialRegisters().read(ISpecialRegisterFile.INDEX_PSW, false));
	}
	
	/**
	 * 
	 */
	@Test
	public void testDisallowedUserModeInstructions() {
		testDisallowedUserModeInstruction(Instruction.OPCODE_RFX);		
		testDisallowedUserModeInstruction(Instruction.OPCODE_MVFS);		
		testDisallowedUserModeInstruction(Instruction.OPCODE_MVTS);		
		testDisallowedUserModeInstruction(Instruction.OPCODE_TBS);		
		testDisallowedUserModeInstruction(Instruction.OPCODE_TBWR);		
		testDisallowedUserModeInstruction(Instruction.OPCODE_TBWI);		
		testDisallowedUserModeInstruction(Instruction.OPCODE_TBRI);		
	}
	
}
