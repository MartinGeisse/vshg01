/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.util.memory;


/**
 * This is the common super-interface for strategies used by the
 * memory visualization system.
 */
public interface IMemoryVisualizationStrategy {

	/**
	 * This method gives information about whether byte-sized memory units are supported.
	 * @return Returns true if byte-sized units are supported by this strategy, false if not.
	 */
	public boolean supportsByteUnits();

	/**
	 * This method gives information about whether halfword-sized memory units are supported.
	 * @return Returns true if halfword-sized units are supported by this strategy, false if not.
	 */
	public boolean supportsHalfwordUnits();

	/**
	 * This method gives information about whether word-sized memory units are supported.
	 * @return Returns true if word-sized units are supported by this strategy, false if not.
	 */
	public boolean supportsWordUnits();

}
