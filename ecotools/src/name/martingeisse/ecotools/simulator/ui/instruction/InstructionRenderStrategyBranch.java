/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.instruction;

/**
 * This strategy renders the immediate value as a 16-bit unsigned hexidecimal integer.
 */
public class InstructionRenderStrategyBranch extends AbstractInstructionRenderStrategyRRI {

	/**
	 * Constructor
	 * @param name the name of the instruction
	 */
	public InstructionRenderStrategyBranch(String name) {
		super(name);
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.instruction.AbstractInstructionRenderStrategyRRI#renderImmediateValue(int)
	 */
	@Override
	protected String renderImmediateValue(int unsignedValue) {
		int byteOffset = (unsignedValue << 16) >> 14;
		String byteOffsetAsString = Integer.toString(byteOffset);
		return (byteOffset < 0) ? (byteOffsetAsString) : ("+" + byteOffsetAsString);
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.instruction.AbstractInstructionRenderStrategyRRI#renderFinish(java.lang.String, int, int, java.lang.String)
	 */
	@Override
	protected String renderFinish(String name, int reg1, int reg2, String renderedImmediate) {
		return name + " $" + reg1 + ", $" + reg2 + ", " + renderedImmediate;
	}

}
