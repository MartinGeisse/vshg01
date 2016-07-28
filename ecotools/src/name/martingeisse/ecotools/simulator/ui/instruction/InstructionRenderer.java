/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.instruction;

import name.martingeisse.ecotools.simulator.cpu.Instruction;

/**
 * This class is able to render instructions to text.
 */
public class InstructionRenderer {

	/**
	 * This array contains the appropriate rendering strategy for
	 * each opcode.
	 */
	private static IInstructionRenderStrategy[] strategies = {
		new InstructionRenderStrategyRRR("add"),
		new InstructionRenderStrategySignedRRI("addi"),
		new InstructionRenderStrategyRRR("sub"),
		new InstructionRenderStrategySignedRRI("subi"),
		new InstructionRenderStrategyRRR("mul"),
		new InstructionRenderStrategySignedRRI("muli"),
		new InstructionRenderStrategyRRR("mulu"),
		new InstructionRenderStrategyUnsignedRRI("mului"),
		new InstructionRenderStrategyRRR("div"),
		new InstructionRenderStrategySignedRRI("divi"),
		new InstructionRenderStrategyRRR("divu"),
		new InstructionRenderStrategyUnsignedRRI("divui"),
		new InstructionRenderStrategyRRR("rem"),
		new InstructionRenderStrategySignedRRI("remi"),
		new InstructionRenderStrategyRRR("remu"),
		new InstructionRenderStrategyUnsignedRRI("remui"),
		new InstructionRenderStrategyRRR("and"),
		new InstructionRenderStrategyUnsignedRRI("andi"),
		new InstructionRenderStrategyRRR("or"),
		new InstructionRenderStrategyUnsignedRRI("ori"),
		new InstructionRenderStrategyRRR("xor"),
		new InstructionRenderStrategyUnsignedRRI("xori"),
		new InstructionRenderStrategyRRR("xnor"),
		new InstructionRenderStrategyUnsignedRRI("xnori"),
		new InstructionRenderStrategyRRR("sll"),
		new InstructionRenderStrategyUnsignedRRI("slli"),
		new InstructionRenderStrategyRRR("slr"),
		new InstructionRenderStrategyUnsignedRRI("slri"),
		new InstructionRenderStrategyRRR("sar"),
		new InstructionRenderStrategyUnsignedRRI("sari"),
		new InstructionRenderStrategyFixed("--- invalid opcode ---"),
		new InstructionRenderStrategyLdhi("ldhi"),
		new InstructionRenderStrategyBranch("beq"),
		new InstructionRenderStrategyBranch("bne"),
		new InstructionRenderStrategyBranch("ble"),
		new InstructionRenderStrategyBranch("bleu"),
		new InstructionRenderStrategyBranch("blt"),
		new InstructionRenderStrategyBranch("bltu"),
		new InstructionRenderStrategyBranch("bge"),
		new InstructionRenderStrategyBranch("bgeu"),
		new InstructionRenderStrategyBranch("bgt"),
		new InstructionRenderStrategyBranch("bgtu"),
		new InstructionRenderStrategyJ("j"),
		new InstructionRenderStrategyR("jr", 1),
		new InstructionRenderStrategyJ("jal"),
		new InstructionRenderStrategyR("jalr", 1),
		new InstructionRenderStrategyFixed("trap"),
		new InstructionRenderStrategyFixed("rfx"),
		new InstructionRenderStrategySignedRRI("ldw"),
		new InstructionRenderStrategySignedRRI("ldh"),
		new InstructionRenderStrategySignedRRI("ldhu"),
		new InstructionRenderStrategySignedRRI("ldb"),
		new InstructionRenderStrategySignedRRI("ldbu"),
		new InstructionRenderStrategySignedRRI("stw"),
		new InstructionRenderStrategySignedRRI("sth"),
		new InstructionRenderStrategySignedRRI("stb"),
		new InstructionRenderStrategyR("mvfs", 2),
		new InstructionRenderStrategyR("mvts", 2),
		new InstructionRenderStrategyFixed("tbs"),
		new InstructionRenderStrategyFixed("tbwr"),
		new InstructionRenderStrategyFixed("tbri"),
		new InstructionRenderStrategyFixed("tbwi"),
		new InstructionRenderStrategyFixed("--- invalid opcode ---"),
		new InstructionRenderStrategyFixed("--- invalid opcode ---"),
	};
	
	/**
	 * Constructor
	 */
	public InstructionRenderer() {
	}
	
	/**
	 * Renders the specified instruction to text.
	 * @param instruction the instruction to render
	 * @return Returns the rendered text for that instruction.
	 */
	public String renderInstruction(int instruction) {
		int opcode = (instruction >> Instruction.OPCODE_SHIFT) & Instruction.OPCODE_VALUE_MASK;
		return strategies[opcode].renderInstruction(instruction);
	}
	
}
