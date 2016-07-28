/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.config;

/**
 * This exception type is thrown when the simulator configuration specified
 * by the user is inconsistent.
 */
public class InconsistentConfigurationException extends Exception {

	/**
	 * the serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 * @param message the exception message
	 */
	public InconsistentConfigurationException(String message) {
		super(message);
	}
	
}
