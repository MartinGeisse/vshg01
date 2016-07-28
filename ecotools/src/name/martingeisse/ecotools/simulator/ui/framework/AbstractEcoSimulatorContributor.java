/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.framework;

/**
 * This abstract implementation of {@link IEcoSimulatorContributor} simply stores the
 * simulator framework at initialization time and makes it accessible through a getter
 * method.
 */
public abstract class AbstractEcoSimulatorContributor implements IEcoSimulatorContributor {

	/**
	 * the framework
	 */
	private EcoSimulatorFramework framework;

	/**
	 * Constructor
	 */
	public AbstractEcoSimulatorContributor() {
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.framework.IEcoSimulatorContributor#initialize(name.martingeisse.ecotools.simulator.ui.framework.EcoSimulatorFramework)
	 */
	@Override
	public void initialize(EcoSimulatorFramework framework) {
		this.framework = framework;
	}

	/**
	 * @return Returns the simulator framework.
	 */
	public EcoSimulatorFramework getFramework() {
		return framework;
	}

}
