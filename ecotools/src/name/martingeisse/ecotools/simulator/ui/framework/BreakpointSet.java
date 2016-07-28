/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.framework;

import java.util.TreeMap;

/**
 * This class keeps the set of CPU breakpoints. Breakpoints come to
 * effect in fast simulation mody only. As soon as the CPU's PC
 * register reaches a value stored in this set -- before executing
 * the instruction from that address -- fast simulation mode is
 * stopped.
 * 
 * For each breakpoint, a boolean value is stored that tells whether
 * the breakpoint is active.
 */
public class BreakpointSet extends TreeMap<Integer, Boolean> {

	/**
	 * the serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructor
	 */
	public BreakpointSet() {
	}

}
