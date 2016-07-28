/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.framework.memory;

import java.util.HashSet;
import java.util.Set;

/**
 * This registry stores a set of main memory visualization listeners
 * and provides utility methods to send an event to all of them.
 */
public class MainMemoryVisualizationListenerRegistry implements IMainMemoryVisualizationListenerRegistry {

	/**
	 * the listeners
	 */
	private Set<IMainMemoryVisualizationListener> listeners;
	
	/**
	 * Constructor
	 */
	public MainMemoryVisualizationListenerRegistry() {
		this.listeners = new HashSet<IMainMemoryVisualizationListener>();
	}
	
	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.framework.memory.IMainMemoryVisualizationListenerRegistry#registerListener(name.martingeisse.ecotools.simulator.ui.framework.memory.IMainMemoryVisualizationListener)
	 */
	@Override
	public void registerListener(IMainMemoryVisualizationListener listener) {
		listeners.add(listener);
	}
	
	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.framework.memory.IMainMemoryVisualizationListenerRegistry#unregisterListener(name.martingeisse.ecotools.simulator.ui.framework.memory.IMainMemoryVisualizationListener)
	 */
	@Override
	public void unregisterListener(IMainMemoryVisualizationListener listener) {
		listeners.remove(listener);
	}
	
	/**
	 * Fires the "after write" event to all listeners.
	 */
	public void fireAfterWrite() {
		for (IMainMemoryVisualizationListener listener : listeners) {
			listener.onAfterWrite();
		}
	}
	
}
