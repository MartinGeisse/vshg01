/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.instruction;

import name.martingeisse.ecotools.simulator.cpu.Instruction;

/**
 * This strategy renders the LDHI instruction.
 */
public class InstructionRenderStrategyLdhi implements IInstructionRenderStrategy {

	/**
	 * the name of the instruction
	 */
	private String name;

	/**
	 * Constructor
	 * @param name the name of the instruction to render
	 */
	public InstructionRenderStrategyLdhi(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.instruction.IInstructionRenderStrategy#renderInstruction(int)
	 */
	@Override
	public String renderInstruction(int instruction) {
		int reg2 = (instruction >> Instruction.REG2_SHIFT) & Instruction.REG2_VALUE_MASK;
		int unsignedImmediate = (instruction >> Instruction.IMMEDIATE_SHIFT) & Instruction.IMMEDIATE_VALUE_MASK;
		String renderedImmediate = "0x" + Integer.toHexString(unsignedImmediate << 16);
		return name + " $" + reg2 + ", " + renderedImmediate;
	}

}
