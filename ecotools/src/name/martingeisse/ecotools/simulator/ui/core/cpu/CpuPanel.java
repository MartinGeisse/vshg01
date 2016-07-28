/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.core.cpu;

import name.martingeisse.ecotools.simulator.bus.BusAccessSize;
import name.martingeisse.ecotools.simulator.cpu.Cpu;
import name.martingeisse.ecotools.simulator.ui.framework.EcoSimulatorFramework;
import name.martingeisse.ecotools.simulator.ui.framework.memory.UpdateMemoryVisualizationPanelOnStoreListener;
import name.martingeisse.ecotools.simulator.ui.util.memory.HighlightSingleAddressMemoryVisualizationStrategy;
import name.martingeisse.ecotools.simulator.ui.util.memory.IMemoryVisualizationStorageStrategy;
import name.martingeisse.ecotools.simulator.ui.util.memory.InstructionMemoryVisualizationTextConversionStrategy;
import name.martingeisse.ecotools.simulator.ui.util.memory.MemoryVisualizationPanel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * 
 */
public class CpuPanel extends Composite {

	/**
	 * Constructor
	 * @param parent the parent widget
	 * @param cpu the CPU that backs this panel
	 * @param memoryVisualizationStorageStrategy the memory visualization strategy used to display the executed instructions
	 * @param cpuUserInterface the CPU user interface implementation
	 * @param ecoSimulatorFramework the simulator framework
	 */
	public CpuPanel(Composite parent, final Cpu cpu, final IMemoryVisualizationStorageStrategy memoryVisualizationStorageStrategy, final CpuUserInterface cpuUserInterface, final EcoSimulatorFramework ecoSimulatorFramework) {
		super(parent, 0);

		GridLayout layout = new GridLayout(2, false);
		setLayout(layout);
		
		GridData generalRegistersLayoutData = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		final GeneralRegistersPanel generalRegisters = new GeneralRegistersPanel(this, cpu.getGeneralRegisters());
		generalRegisters.setLayoutData(generalRegistersLayoutData);
		cpuUserInterface.setGeneralRegistersPanel(generalRegisters);

		final HighlightSingleAddressMemoryVisualizationStrategy highlightPcColoringStrategy = new HighlightSingleAddressMemoryVisualizationStrategy(0xe0000000);
		GridData instructionsLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 4);
		final MemoryVisualizationPanel instructions = new MemoryVisualizationPanel(this) {

			/* (non-Javadoc)
			 * @see name.martingeisse.ecotools.simulator.ui.util.memory.MemoryVisualizationPanel#updateContents()
			 */
			@Override
			public void updateContents() {
				int pc = cpu.getPc().getValue();
				highlightPcColoringStrategy.setHighlightedAddress(pc);
				setStartAddress(pc - 14 * 4);
				super.updateContents();
			}
			
		};
		cpuUserInterface.setInstructionPanel(instructions);
		instructions.setLayoutData(instructionsLayoutData);
		instructions.setStorageStrategy(memoryVisualizationStorageStrategy);
		instructions.setStartAddress(0xe0000000);
		instructions.setAccessSize(BusAccessSize.WORD);
		instructions.setUnitsPerRow(1);
		instructions.setRowCount(30);
		instructions.setTextConversionStrategy(InstructionMemoryVisualizationTextConversionStrategy.getInstance());
		instructions.setColoringStrategy(highlightPcColoringStrategy);
		instructions.updateContents();
		ecoSimulatorFramework.registerListener(new UpdateMemoryVisualizationPanelOnStoreListener(instructions));

		GridData specialRegistersLayoutData = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		final SpecialRegistersPanel specialRegisters = new SpecialRegistersPanel(this, cpu.getSpecialRegisters(), cpu.getPc());
		specialRegisters.setLayoutData(specialRegistersLayoutData);
		cpuUserInterface.setSpecialRegistersPanel(specialRegisters);

		GridData breakpointsLayoutData = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
		final BreakpointsPanel breakpointsPanel = new BreakpointsPanel(this, ecoSimulatorFramework.getBreakpointSet());
		breakpointsPanel.setLayoutData(breakpointsLayoutData);

		GridData buttonBarLayoutData = new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1);
		Composite buttonBar = new Composite(this, 0);
		buttonBar.setLayoutData(buttonBarLayoutData);
		buttonBar.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Button stepButton = new Button(buttonBar, SWT.PUSH | SWT.CENTER);
		stepButton.setText("Step");
		stepButton.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent event) {
				generalRegisters.clearColors();
				specialRegisters.clearColors();
				ecoSimulatorFramework.step();
			}
			
		});

		final Button runButton = new Button(buttonBar, SWT.PUSH | SWT.CENTER);
		runButton.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (ecoSimulatorFramework.isFastSimulationMode()) {
					ecoSimulatorFramework.setFastSimulationMode(false);
				} else {
					ecoSimulatorFramework.setFastSimulationMode(true);
				}
			}
			
		});
		cpuUserInterface.setFastModeButton(runButton);

	}
	
}
