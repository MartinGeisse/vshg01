/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.framework.memory;

/**
 * This interface is implemented by GUI components that visualize the
 * main memory and need to listen to memory access events.
 */
public interface IMainMemoryVisualizationListener {

	/**
	 * This method is invoked after a value has been written by
	 * the CPU or by a GUI component.
	 */
	public void onAfterWrite();
	
}
