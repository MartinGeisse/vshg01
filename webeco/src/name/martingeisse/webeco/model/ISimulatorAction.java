/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.webeco.model;


/**
 * This interface can be implemented by arbitrary code in the GUI to inject code
 * into the simulator that is run in the simulation thread (as opposed to one
 * of the GUI threads) as soon as the simulator can handle it.
 */
public interface ISimulatorAction {

	/**
	 * @param model the simulation model
	 */
	public void execute(SimulationModel model);
	
}
