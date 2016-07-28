/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.instruction;

/**
 * This strategy renders the immediate value as a 16-bit unsigned hexidecimal integer.
 */
public class InstructionRenderStrategyUnsignedRRI extends AbstractInstructionRenderStrategyRRI {

	/**
	 * Constructor
	 * @param name the name of the instruction
	 */
	public InstructionRenderStrategyUnsignedRRI(String name) {
		super(name);
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.instruction.AbstractInstructionRenderStrategyRRI#renderImmediateValue(int)
	 */
	@Override
	protected String renderImmediateValue(int unsignedValue) {
		return "0x" + Integer.toHexString(unsignedValue);
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.instruction.AbstractInstructionRenderStrategyRRI#renderFinish(java.lang.String, int, int, java.lang.String)
	 */
	@Override
	protected String renderFinish(String name, int reg1, int reg2, String renderedImmediate) {
		return name + " $" + reg2 + ", $" + reg1 + ", " + renderedImmediate;
	}

}
