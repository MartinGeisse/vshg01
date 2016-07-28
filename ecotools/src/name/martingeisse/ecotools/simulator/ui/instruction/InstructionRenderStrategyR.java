/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.instruction;

import name.martingeisse.ecotools.simulator.cpu.Instruction;

/**
 * This class can render single-R-type instructions to text. The
 * instruction is rendered as
 * 
 *   name $reg
 * 
 * to mimic the syntax of the assembler. The constructor accepts
 * a register position argument to indicate the position of the
 * register index in the instruction.
 */
public class InstructionRenderStrategyR implements IInstructionRenderStrategy {

	/**
	 * the name of the instruction
	 */
	private String name;
	
	/**
	 * the shift
	 */
	private int shift;
	
	/**
	 * the valueMask
	 */
	private int valueMask;

	/**
	 * Constructor
	 * @param name the name of the instruction to render
	 * @param registerPosition the position of the register index in the instruction.
	 * Can be one of 1, 2, or 3 to indicate the corresponding position.
	 */
	public InstructionRenderStrategyR(String name, int registerPosition) {
		this.name = name;
		
		switch (registerPosition) {
		
		case 1:
			this.shift = Instruction.REG1_SHIFT;
			this.valueMask = Instruction.REG1_VALUE_MASK;
			break;
			
		case 2:
			this.shift = Instruction.REG2_SHIFT;
			this.valueMask = Instruction.REG2_VALUE_MASK;
			break;
			
		case 3:
			this.shift = Instruction.REG3_SHIFT;
			this.valueMask = Instruction.REG3_VALUE_MASK;
			break;
			
		default:
			throw new IllegalArgumentException("invalid register position: " + registerPosition);
			
		}
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.instruction.IInstructionRenderStrategy#renderInstruction(int)
	 */
	@Override
	public String renderInstruction(int instruction) {
		int reg = (instruction >> shift) & valueMask;
		return name + " $" + reg;
	}

}
