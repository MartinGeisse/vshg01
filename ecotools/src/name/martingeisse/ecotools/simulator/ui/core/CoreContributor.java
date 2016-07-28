/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.core;

import java.io.File;
import java.io.IOException;

import name.martingeisse.ecotools.simulator.bus.Bus;
import name.martingeisse.ecotools.simulator.devices.memory.Ram;
import name.martingeisse.ecotools.simulator.devices.memory.Rom;
import name.martingeisse.ecotools.simulator.ui.core.cpu.CpuPanel;
import name.martingeisse.ecotools.simulator.ui.core.cpu.CpuUserInterface;
import name.martingeisse.ecotools.simulator.ui.core.memory.MemoryPanel;
import name.martingeisse.ecotools.simulator.ui.core.memory.MemoryVisualizationContributionItem;
import name.martingeisse.ecotools.simulator.ui.core.tlb.TlbPanel;
import name.martingeisse.ecotools.simulator.ui.framework.AbstractEcoSimulatorContributor;
import name.martingeisse.ecotools.simulator.ui.framework.EcoSimulatorFramework;
import name.martingeisse.ecotools.simulator.ui.framework.memory.IMainMemoryVisualizationContributionItem;
import name.martingeisse.ecotools.simulator.ui.framework.memory.IMainMemoryVisualizationContributor;
import name.martingeisse.ecotools.simulator.ui.util.memory.DefaultMemoryVisualizationStorageStrategy;

import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**
 * This contributor provides a RAM and ROM as well as convenient
 * user interface components to handle CPU, RAM and ROM at the
 * same time.
 * 
 * The core components are initialized as 32 MB RAM, 2 MB ROM,
 * with ROM contents optionally initialized from a file.
 */
public class CoreContributor extends AbstractEcoSimulatorContributor {

	/**
	 * The physical base address of the RAM.
	 */
	public static final int RAM_BASE_ADDRESS = 0x00000000;

	/**
	 * The number of local address bits for the RAM. This is 25 to
	 * indicate that the size of the RAM is 2^25 Bytes = 32 MB.
	 */
	public static final int LOCAL_RAM_ADDRESS_BITS = 25;

	/**
	 * The physical base address of the ROM.
	 */
	public static final int ROM_BASE_ADDRESS = 0x20000000;

	/**
	 * The number of local address bits for the ROM. This is 21 to
	 * indicate that the size of the ROM is 2^21 Bytes = 2 MB.
	 */
	public static final int LOCAL_ROM_ADDRESS_BITS = 21;

	/**
	 * the ram
	 */
	private Ram ram;

	/**
	 * the rom
	 */
	private Rom rom;

	/**
	 * the cpuUserInterface
	 */
	private CpuUserInterface cpuUserInterface;

	/**
	 * the cpuPanel
	 */
	private CpuPanel cpuPanel;

	/**
	 * the tlbPanel
	 */
	private TlbPanel tlbPanel;

	/**
	 * the memoryPanel
	 */
	private MemoryPanel memoryPanel;

	/**
	 * Constructor for an empty ROM.
	 */
	public CoreContributor() {
		this.ram = new Ram(LOCAL_RAM_ADDRESS_BITS);
		this.rom = new Rom(LOCAL_ROM_ADDRESS_BITS);
	}

	/**
	 * Constructor that initializes the ROM from the specified file.
	 * @param romFile the file to load ROM contents from
	 * @throws IOException on problems while loading the ROM content file
	 */
	public CoreContributor(File romFile) throws IOException {
		this.ram = new Ram(LOCAL_RAM_ADDRESS_BITS);
		this.rom = new Rom(LOCAL_ROM_ADDRESS_BITS);
		rom.readContentsFromFile(romFile);
	}

	/**
	 * @return Returns the ram.
	 */
	public Ram getRam() {
		return ram;
	}

	/**
	 * @return Returns the rom.
	 */
	public Rom getRom() {
		return rom;
	}

	/**
	 * @return Returns the cpuUserInterface.
	 */
	public CpuUserInterface getCpuUserInterface() {
		return cpuUserInterface;
	}

	/**
	 * @return Returns the cpuPanel.
	 */
	public CpuPanel getCpuPanel() {
		return cpuPanel;
	}

	/**
	 * @return Returns the memoryPanel.
	 */
	public MemoryPanel getMemoryPanel() {
		return memoryPanel;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.framework.IEcoSimulatorContributor#createTabItems(org.eclipse.swt.widgets.TabFolder)
	 */
	@Override
	public void createTabItems(TabFolder tabFolder) {
		EcoSimulatorFramework ecoSimulatorFramework = getFramework();

		/** create an ICpuUserInterface implementation and link it to the CPU **/
		cpuUserInterface = new CpuUserInterface();
		ecoSimulatorFramework.getCpu().setUserInterface(cpuUserInterface);
		cpuUserInterface.setFramework(ecoSimulatorFramework);

		/** create a tab item for the CPU **/
		TabItem cpuTabItem = new TabItem(tabFolder, 0);
		cpuTabItem.setText("CPU");

		/** create the CPU panel within the tab item **/
		cpuPanel = new CpuPanel(tabFolder, ecoSimulatorFramework.getCpu(), getFramework().getGlobalMemoryVisualizationStorageStrategy(), cpuUserInterface, ecoSimulatorFramework);
		cpuTabItem.setControl(cpuPanel);

		/** create a tab item for the TLB **/
		TabItem tlbTabItem = new TabItem(tabFolder, 0);
		tlbTabItem.setText("TLB");

		/** create the TLB panel within the tab item **/
		tlbPanel = new TlbPanel(tabFolder, ecoSimulatorFramework.getCpu().getMemoryManagementUnit(), cpuUserInterface);
		tlbTabItem.setControl(tlbPanel);

		/** create a tab item for the memory **/
		TabItem memoryTabItem = new TabItem(tabFolder, 0);
		memoryTabItem.setText("Memory");

		/** create the memory panel within the tab item **/
		memoryPanel = new MemoryPanel(tabFolder, getFramework().getGlobalMemoryVisualizationStorageStrategy(), getFramework());
		memoryTabItem.setControl(memoryPanel);

	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.framework.IEcoSimulatorContributor#registerPeripheralDevices(name.martingeisse.ecotools.simulator.bus.Bus)
	 */
	@Override
	public void registerPeripheralDevices(Bus bus) {
		bus.add(RAM_BASE_ADDRESS, ram, new int[] {});
		bus.add(ROM_BASE_ADDRESS, rom, new int[] {});
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.framework.IEcoSimulatorContributor#setEnableFastSimulationMode(boolean)
	 */
	@Override
	public void setEnableFastSimulationMode(boolean enable) {
		cpuUserInterface.setFastSimulationMode(enable);
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.framework.IEcoSimulatorContributor#getMemoryVisualizationContributor()
	 */
	@Override
	public IMainMemoryVisualizationContributor getMemoryVisualizationContributor() {
		return new IMainMemoryVisualizationContributor() {
			@Override
			public IMainMemoryVisualizationContributionItem[] getContributionItems() {
				return new IMainMemoryVisualizationContributionItem[] {
					new MemoryVisualizationContributionItem(ram, new DefaultMemoryVisualizationStorageStrategy(ram)),
					new MemoryVisualizationContributionItem(rom, new DefaultMemoryVisualizationStorageStrategy(rom)),
				};
			}
		};
	}

}
