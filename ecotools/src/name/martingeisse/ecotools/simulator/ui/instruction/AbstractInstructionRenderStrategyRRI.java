/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.instruction;

import name.martingeisse.ecotools.simulator.cpu.Instruction;

/**
 * This class can render RRI-type instructions to text. The
 * instruction is rendered as
 * 
 *   name $r2, $r1, immediate
 * 
 * to mimic the syntax of the assembler, where rendering the
 * immediate value is left to the concrete subclass.
 */
public abstract class AbstractInstructionRenderStrategyRRI implements IInstructionRenderStrategy {

	/**
	 * the name of the instruction
	 */
	private String name;

	/**
	 * Constructor
	 * @param name the name of the instruction to render
	 */
	public AbstractInstructionRenderStrategyRRI(String name) {
		this.name = name;
	}
	
	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.instruction.IInstructionRenderStrategy#renderInstruction(int)
	 */
	@Override
	public String renderInstruction(int instruction) {
		int reg1 = (instruction >> Instruction.REG1_SHIFT) & Instruction.REG1_VALUE_MASK;
		int reg2 = (instruction >> Instruction.REG2_SHIFT) & Instruction.REG2_VALUE_MASK;
		int unsignedImmediate = (instruction >> Instruction.IMMEDIATE_SHIFT) & Instruction.IMMEDIATE_VALUE_MASK;
		String renderedImmediate = renderImmediateValue(unsignedImmediate);
		return renderFinish(name, reg1, reg2, renderedImmediate);
	}
	
	/**
	 * This method must be implemented to render the immediate value.
	 * @param unsignedValue the unsigned immediate value in the range 0..65535
	 * @return Returns the rendered immediate value.
	 */
	protected abstract String renderImmediateValue(int unsignedValue);
	
	/**
	 * Renders the final instruction text from the pices of information passed as
	 * arguments. This method is implemented separately because some RRI instructions
	 * use a reversed register ordering (such as ADDI), while some do not (such as BEQ).
	 */
	protected String renderFinish(String name, int reg1, int reg2, String renderedImmediate) {
		return name + " $" + reg2 + ", $" + reg1 + ", " + renderedImmediate;
	}

}
