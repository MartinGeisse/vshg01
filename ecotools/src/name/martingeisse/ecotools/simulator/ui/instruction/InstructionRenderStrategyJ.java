/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.instruction;

import name.martingeisse.ecotools.simulator.cpu.Instruction;

/**
 * This class can render J-type instructions to text. The
 * instruction is rendered as
 * 
 *   j +100
 *   
 * or
 * 
 *   jal -24
 * 
 */
public class InstructionRenderStrategyJ implements IInstructionRenderStrategy {

	/**
	 * the name of the instruction
	 */
	private String name;

	/**
	 * Constructor
	 * @param name the name of the instruction to render
	 */
	public InstructionRenderStrategyJ(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.instruction.IInstructionRenderStrategy#renderInstruction(int)
	 */
	@Override
	public String renderInstruction(int instruction) {
		int unsignedOffset = (instruction >> Instruction.OFFSET_SHIFT) & Instruction.OFFSET_VALUE_MASK;
		int signedOffset = (unsignedOffset << 6) >> 4;
		return name + (signedOffset < 0 ? " " : " +") + Integer.toString(signedOffset);
	}

}
