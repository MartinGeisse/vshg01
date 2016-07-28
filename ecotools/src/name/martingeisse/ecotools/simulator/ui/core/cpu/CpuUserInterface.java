/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.core.cpu;

import name.martingeisse.ecotools.simulator.cpu.ICpuUserInterface;
import name.martingeisse.ecotools.simulator.ui.core.tlb.TlbPanel;
import name.martingeisse.ecotools.simulator.ui.framework.EcoSimulatorFramework;
import name.martingeisse.ecotools.simulator.ui.util.memory.MemoryVisualizationPanel;

import org.eclipse.swt.widgets.Button;

/**
 * This class connects the CPU's notifications to the GUI.
 */
public class CpuUserInterface implements ICpuUserInterface {

	/**
	 * the fastSimulationMode
	 */
	private boolean fastSimulationMode;

	/**
	 * the generalRegistersPanel
	 */
	private GeneralRegistersPanel generalRegistersPanel;

	/**
	 * the specialRegistersPanel
	 */
	private SpecialRegistersPanel specialRegistersPanel;

	/**
	 * the instructionPanel
	 */
	private MemoryVisualizationPanel instructionPanel;

	/**
	 * the fastModeButton
	 */
	private Button fastModeButton;

	/**
	 * the framework
	 */
	private EcoSimulatorFramework framework;

	/**
	 * the tlbPanel
	 */
	private TlbPanel tlbPanel;

	/**
	 * Constructor
	 */
	public CpuUserInterface() {
	}

	/**
	 * @return Returns the fastSimulationMode.
	 */
	public boolean isFastSimulationMode() {
		return fastSimulationMode;
	}

	/**
	 * Sets the fastSimulationMode.
	 * @param fastSimulationMode the new value to set
	 */
	public void setFastSimulationMode(boolean fastSimulationMode) {
		this.fastSimulationMode = fastSimulationMode;

		/** as soon as fast simulation mode is disabled, we need to update the whole GUI once **/
		if (!fastSimulationMode) {
			generalRegistersPanel.updateAllRegisters();
			specialRegistersPanel.updateAllRegisters();
			instructionPanel.updateContents();
			framework.fireAfterWriteMainMemory();
			tlbPanel.onWriteTlb();
		}
		checkUpdateFastModeButton();
	}

	/**
	 * @return Returns the generalRegistersPanel.
	 */
	public GeneralRegistersPanel getGeneralRegistersPanel() {
		return generalRegistersPanel;
	}

	/**
	 * Sets the generalRegistersPanel.
	 * @param generalRegistersPanel the new value to set
	 */
	public void setGeneralRegistersPanel(GeneralRegistersPanel generalRegistersPanel) {
		this.generalRegistersPanel = generalRegistersPanel;
	}

	/**
	 * @return Returns the specialRegistersPanel.
	 */
	public SpecialRegistersPanel getSpecialRegistersPanel() {
		return specialRegistersPanel;
	}

	/**
	 * Sets the specialRegistersPanel.
	 * @param specialRegistersPanel the new value to set
	 */
	public void setSpecialRegistersPanel(SpecialRegistersPanel specialRegistersPanel) {
		this.specialRegistersPanel = specialRegistersPanel;
	}

	/**
	 * @return Returns the instructionPanel.
	 */
	public MemoryVisualizationPanel getInstructionPanel() {
		return instructionPanel;
	}

	/**
	 * Sets the instructionPanel.
	 * @param instructionPanel the new value to set
	 */
	public void setInstructionPanel(MemoryVisualizationPanel instructionPanel) {
		this.instructionPanel = instructionPanel;
	}

	/**
	 * @return Returns the fastModeButton.
	 */
	public Button getFastModeButton() {
		return fastModeButton;
	}

	/**
	 * Sets the fastModeButton.
	 * @param fastModeButton the new value to set
	 */
	public void setFastModeButton(Button fastModeButton) {
		this.fastModeButton = fastModeButton;
		checkUpdateFastModeButton();
	}

	/**
	 * @return Returns the framework.
	 */
	public EcoSimulatorFramework getFramework() {
		return framework;
	}

	/**
	 * Sets the framework.
	 * @param framework the new value to set
	 */
	public void setFramework(EcoSimulatorFramework framework) {
		this.framework = framework;
	}

	/**
	 * @return Returns the tlbPanel.
	 */
	public TlbPanel getTlbPanel() {
		return tlbPanel;
	}

	/**
	 * Sets the tlbPanel.
	 * @param tlbPanel the new value to set
	 */
	public void setTlbPanel(TlbPanel tlbPanel) {
		this.tlbPanel = tlbPanel;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.cpu.ICpuUserInterface#onReadGeneralRegister(int)
	 */
	@Override
	public void onReadGeneralRegister(int index) {
		if (!fastSimulationMode) {
			generalRegistersPanel.notifyReadRegister(index);
		}
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.cpu.ICpuUserInterface#onReadSpecialRegister(int)
	 */
	@Override
	public void onReadSpecialRegister(int index) {
		if (!fastSimulationMode) {
			specialRegistersPanel.notifyReadRegister(index);
		}
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.cpu.ICpuUserInterface#onWriteGeneralRegister(int)
	 */
	@Override
	public void onWriteGeneralRegister(int index) {
		if (!fastSimulationMode) {
			generalRegistersPanel.notifyWriteRegister(index);
		}
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.cpu.ICpuUserInterface#onWritePc()
	 */
	@Override
	public void onWritePc() {
		if (!fastSimulationMode) {
			specialRegistersPanel.notifyWritePcRegister();
			instructionPanel.updateContents();
		}
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.cpu.ICpuUserInterface#onWriteSpecialRegister(int)
	 */
	@Override
	public void onWriteSpecialRegister(int index) {
		if (!fastSimulationMode) {
			specialRegistersPanel.notifyWriteRegister(index);
		}
	}

	/**
	 * 
	 */
	private void checkUpdateFastModeButton() {
		if (fastModeButton != null) {
			fastModeButton.setText(fastSimulationMode ? "Stop" : "Run");
		}
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.cpu.ICpuUserInterface#onStore()
	 */
	@Override
	public void onStore() {
		if (!fastSimulationMode) {
			framework.fireAfterWriteMainMemory();
		}
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.cpu.ICpuUserInterface#onWriteTlb()
	 */
	@Override
	public void onWriteTlb() {
		if (!fastSimulationMode) {
			tlbPanel.onWriteTlb();
		}
	}

}
