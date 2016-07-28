/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.instruction;

/**
 * This strategy performs the actual rendering of an instruction
 * to text. Different strategies should be applied to different
 * instructions.
 */
public interface IInstructionRenderStrategy {

	/**
	 * Renders the specified instruction to text.
	 * @param instruction the instruction to render
	 * @return Returns the rendered instruction text.
	 */
	public String renderInstruction(int instruction);
	
}
