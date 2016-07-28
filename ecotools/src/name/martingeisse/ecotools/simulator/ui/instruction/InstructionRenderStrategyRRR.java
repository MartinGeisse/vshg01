/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.instruction;

import name.martingeisse.ecotools.simulator.cpu.Instruction;

/**
 * This class can render RRR-type instructions to text. The
 * instruction is rendered as
 * 
 *   name $r3, $r1, $r2
 * 
 * to mimic the syntax of the assembler.
 */
public class InstructionRenderStrategyRRR implements IInstructionRenderStrategy {

	/**
	 * the name of the instruction
	 */
	private String name;

	/**
	 * Constructor
	 * @param name the name of the instruction to render
	 */
	public InstructionRenderStrategyRRR(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.instruction.IInstructionRenderStrategy#renderInstruction(int)
	 */
	@Override
	public String renderInstruction(int instruction) {
		int reg1 = (instruction >> Instruction.REG1_SHIFT) & Instruction.REG1_VALUE_MASK;
		int reg2 = (instruction >> Instruction.REG2_SHIFT) & Instruction.REG2_VALUE_MASK;
		int reg3 = (instruction >> Instruction.REG3_SHIFT) & Instruction.REG3_VALUE_MASK;
		return name + " $" + reg3 + ", $" + reg1 + ", $" + reg2;
	}

}
