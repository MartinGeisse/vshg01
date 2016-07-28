/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.meltdown.engine;

/**
 * Abstract superclass for all objects hosted by the engine.
 */
public class EngineBoundObject {

	/**
	 * the engine
	 */
	private final Engine engine;
	
	/**
	 * Constructor.
	 * @param engine the engine that created this object
	 */
	EngineBoundObject(Engine engine) {
		this.engine = engine;
	}

	/**
	 * Getter method for the engine.
	 * @return the engine
	 */
	public Engine getEngine() {
		return engine;
	}

}
