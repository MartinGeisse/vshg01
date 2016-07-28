/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.webeco.model;


/**
 * This thread runs the ECO32 simulator. Only one instance should be created.
 */
public class SimulationThread extends Thread {

	/**
	 * Constructor.
	 */
	public SimulationThread() {
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		SimulationModel model = Simulator.getSimulationModel();
		while (true) {
			for (int i=0; i<100; i++) {
				model.step();
			}
			model.executePendingActions();
		}
	}
	
}
