/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.webeco.model;

import name.martingeisse.webeco.GuiMessageHub;

/**
 * This class keeps singleton simulator data. It is thread-safe.
 */
public class Simulator {

	/**
	 * the simulationModel
	 */
	private static SimulationModel simulationModel;

	/**
	 * the guiMessageHub
	 */
	private static GuiMessageHub guiMessageHub;

	/**
	 * 
	 */
	public static synchronized void initialize() {
		simulationModel = new SimulationModel();
		simulationModel.initializeDeviceModels();
		guiMessageHub = new GuiMessageHub();
		simulationModel.initializeDeviceUserInterfaces(guiMessageHub);
		new SimulationThread().start();
	}

	/**
	 * Getter method for the simulationModel.
	 * @return the simulationModel
	 */
	public static synchronized SimulationModel getSimulationModel() {
		return simulationModel;
	}

	/**
	 * Getter method for the guiMessageHub.
	 * @return the guiMessageHub
	 */
	public static synchronized GuiMessageHub getGuiMessageHub() {
		return guiMessageHub;
	}

}
