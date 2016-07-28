/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.framework.memory;

/**
 * This interface provides methods to (un-)register main memory
 * visualization listeners.
 */
public interface IMainMemoryVisualizationListenerRegistry {

	/**
	 * Registers a listener.
	 * @param listener the listener to register.
	 */
	public abstract void registerListener(IMainMemoryVisualizationListener listener);

	/**
	 * Unregisters a listener.
	 * @param listener the listener to unregister.
	 */
	public abstract void unregisterListener(IMainMemoryVisualizationListener listener);

}
