/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.instruction;

/**
 * This renderer strategy simply returns a fixed text. This is useful both
 * for parameter-less instructions and to indicate invalid opcodes.
 */
public class InstructionRenderStrategyFixed implements IInstructionRenderStrategy {

	/**
	 * the text
	 */
	private String text;

	/**
	 * Constructor
	 * @param text the fixed text to return
	 */
	public InstructionRenderStrategyFixed(String text) {
		this.text = text;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.instruction.IInstructionRenderStrategy#renderInstruction(int)
	 */
	@Override
	public String renderInstruction(int instruction) {
		return text;
	}

}
