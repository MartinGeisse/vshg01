/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.terminal;

import name.martingeisse.ecotools.simulator.bus.Bus;
import name.martingeisse.ecotools.simulator.devices.terminal.Terminal;
import name.martingeisse.ecotools.simulator.ui.framework.AbstractEcoSimulatorContributor;
import name.martingeisse.ecotools.simulator.ui.framework.memory.IMainMemoryVisualizationContributor;

import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**
 * 
 */
public class TerminalContributor extends AbstractEcoSimulatorContributor {

	/**
	 * The physical base address of terminal 0.
	 */
	public static final int TERMINAL_0_BASE_ADDRESS = 0x30300000;

	/**
	 * The physical base address of terminal 1.
	 */
	public static final int TERMINAL_1_BASE_ADDRESS = 0x30300010;

	/**
	 * The terminal 0 receiver interrupt index.
	 */
	public static final int TERMINAL_0_RECEIVER_INTERRUPT = 1;

	/**
	 * The terminal 0 transmitter interrupt index.
	 */
	public static final int TERMINAL_0_TRANSMITTER_INTERRUPT = 0;

	/**
	 * The terminal 1 receiver interrupt index.
	 */
	public static final int TERMINAL_1_RECEIVER_INTERRUPT = 3;

	/**
	 * The terminal 1 transmitter interrupt index.
	 */
	public static final int TERMINAL_1_TRANSMITTER_INTERRUPT = 2;

	/**
	 * the terminalNumber
	 */
	private int terminalNumber;
	
	/**
	 * the terminalTitle
	 */
	private String terminalTitle;
	
	/**
	 * the terminal
	 */
	private Terminal terminal;

	/**
	 * Constructor for terminal number 0.
	 */
	public TerminalContributor() {
		this(0, "Terminal");
	}

	/**
	 * Constructor for a specific terminal.
	 * @param terminalNumber the terminal number added by this contributor
	 */
	public TerminalContributor(int terminalNumber) {
		this(terminalNumber, "Terminal " + terminalNumber);
	}


	/**
	 * Constructor for a specific terminal.
	 * @param terminalNumber the terminal number added by this contributor
	 * @param terminalTitle the terminal title (for UI purposes)
	 */
	public TerminalContributor(int terminalNumber, String terminalTitle) {
		if (terminalNumber < 0 || terminalNumber > 1) {
			throw new IllegalArgumentException("Invalid terminal number: " + terminalNumber);
		}
		this.terminalNumber = terminalNumber;
		this.terminalTitle = terminalTitle;
		this.terminal = new Terminal();
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.framework.IEcoSimulatorContributor#createTabItems(org.eclipse.swt.widgets.TabFolder)
	 */
	@Override
	public void createTabItems(TabFolder tabFolder) {

		/** create a tab item **/
		TabItem tabItem = new TabItem(tabFolder, 0);
		tabItem.setText(terminalTitle);

		/** create the console canvas within the tab item **/
		TerminalPanel terminalPanel = new TerminalPanel(tabFolder);
		tabItem.setControl(terminalPanel);

		/** link the terminal panel and the terminal device **/
		terminal.setUserInterface(terminalPanel);
		terminalPanel.setTerminalUserInterfaceSocket(terminal);

	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.framework.IEcoSimulatorContributor#registerPeripheralDevices(name.martingeisse.ecotools.simulator.bus.Bus)
	 */
	@Override
	public void registerPeripheralDevices(Bus bus) {
		int baseAddress = (terminalNumber == 0) ? TERMINAL_0_BASE_ADDRESS : TERMINAL_1_BASE_ADDRESS;
		int receiverInterrupt = (terminalNumber == 0) ? TERMINAL_0_RECEIVER_INTERRUPT : TERMINAL_1_RECEIVER_INTERRUPT;
		int transmitterInterrupt = (terminalNumber == 0) ? TERMINAL_0_TRANSMITTER_INTERRUPT : TERMINAL_1_TRANSMITTER_INTERRUPT;
		bus.add(baseAddress, terminal, new int[] {
				receiverInterrupt, transmitterInterrupt
		});
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.framework.IEcoSimulatorContributor#setEnableFastSimulationMode(boolean)
	 */
	@Override
	public void setEnableFastSimulationMode(boolean enable) {
		/** the terminal UI remains unchanged in fast simulation mode **/
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.framework.IEcoSimulatorContributor#getMemoryVisualizationContributor()
	 */
	@Override
	public IMainMemoryVisualizationContributor getMemoryVisualizationContributor() {
		return null;
	}

}
