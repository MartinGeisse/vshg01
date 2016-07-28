/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.framework;

import java.util.ArrayList;
import java.util.List;

import name.martingeisse.ecotools.simulator.bus.Bus;
import name.martingeisse.ecotools.simulator.cpu.Cpu;
import name.martingeisse.ecotools.simulator.ui.framework.memory.IMainMemoryVisualizationContributionItem;
import name.martingeisse.ecotools.simulator.ui.framework.memory.IMainMemoryVisualizationContributor;
import name.martingeisse.ecotools.simulator.ui.framework.memory.IMainMemoryVisualizationListener;
import name.martingeisse.ecotools.simulator.ui.framework.memory.IMainMemoryVisualizationListenerRegistry;
import name.martingeisse.ecotools.simulator.ui.framework.memory.MainMemoryGlobalVisualizationStorageStrategy;
import name.martingeisse.ecotools.simulator.ui.framework.memory.MainMemoryVisualizationListenerRegistry;
import name.martingeisse.swtlib.application.AbstractSingleWindowApplication;
import name.martingeisse.swtlib.layout.RubberLayout;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;

/**
 * This class encapsulates the ECO32 simulator framework. Simulation
 * contributors are registered with this framework. This object then
 * provides the GUI and queries its contributors accordingly.
 * 
 * TODO: Let the user choose a timing model. The current simple model
 * executes N instructions per tick, one instruction per step, and
 * as many steps as possible per time unit. This model can best
 * be described as "fast non-interactive". Alternative models are:
 * 
 * - realistic: Execute as many ticks and instructions as the real
 *   ECO32 would do. Requires a fast enough machine, so we cannot
 *   bet on this one.
 *   
 * - semi-realistic: Synchronize bus ticks with real time but execute
 *   as many CPU instructions as possible. This roughly simulates a
 *   virtual machine in real time whose CPU clock frequency is unknown
 *   (and usually lower than that of the real ECO32, since host machines
 *   aren't that fast).
 *   
 * --> put this on its own preference pane. Each of these modes has
 *     its use cases. They are also parameterizable.
 *   
 */
public class EcoSimulatorFramework extends AbstractSingleWindowApplication implements IMainMemoryVisualizationListenerRegistry {

	/**
	 * The number of simulation steps to perform as a bulk step before
	 * secondary work is performed (such as UI redraws). While a
	 * bulk step is being performed, UI events are queued normally
	 * but not processed. Too high values make the UI unresponsive,
	 * while too low values slow down the simulation.
	 */
	public static final int BULK_STEP_SIZE = 1000;

	/**
	 * The number of CPU instructions per bus tick.
	 */
	public static final int INSTRUCTIONS_PER_TICK = 500;

	/**
	 * the contributors
	 */
	private List<IEcoSimulatorContributor> contributors;

	/**
	 * the tabFolder
	 */
	private TabFolder tabFolder;

	/**
	 * the bus
	 */
	private Bus bus;

	/**
	 * the cpu
	 */
	private Cpu cpu;

	/**
	 * the instructionsLeftUntilTick
	 */
	private int instructionsLeftUntilTick;

	/**
	 * the fastSimulationMode
	 */
	private boolean fastSimulationMode;

	/**
	 * the breakpointSet
	 */
	private BreakpointSet breakpointSet;

	/**
	 * the globalMemoryVisualizationStorageStrategy
	 */
	private MainMemoryGlobalVisualizationStorageStrategy globalMemoryVisualizationStorageStrategy;

	/**
	 * the mainMemoryVisualizationListenerRegistry
	 */
	private MainMemoryVisualizationListenerRegistry mainMemoryVisualizationListenerRegistry;

	/**
	 * Constructor
	 */
	public EcoSimulatorFramework() {
		this.contributors = new ArrayList<IEcoSimulatorContributor>();
		this.globalMemoryVisualizationStorageStrategy = new MainMemoryGlobalVisualizationStorageStrategy();
		this.breakpointSet = new BreakpointSet();
		this.mainMemoryVisualizationListenerRegistry = new MainMemoryVisualizationListenerRegistry();
	}

	/**
	 * @return Returns the bus.
	 */
	public Bus getBus() {
		return bus;
	}

	/**
	 * @return Returns the cpu.
	 */
	public Cpu getCpu() {
		return cpu;
	}

	/**
	 * @return Returns the globalMemoryVisualizationStorageStrategy.
	 */
	public MainMemoryGlobalVisualizationStorageStrategy getGlobalMemoryVisualizationStorageStrategy() {
		return globalMemoryVisualizationStorageStrategy;
	}

	/**
	 * @return Returns the breakpointSet.
	 */
	public BreakpointSet getBreakpointSet() {
		return breakpointSet;
	}

	/**
	 * Adds a simulator contributor. This method may only be called as long as the
	 * simulation has not been opened yet.
	 * @param contributor the contributor to add
	 */
	public void addContributor(IEcoSimulatorContributor contributor) {
		ensureGuiNotCreated();
		contributors.add(contributor);
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.swtlib.application.AbstractSingleWindowApplication#create()
	 */
	@Override
	public void create() {
		super.create();
		handleAllEvents();
		Shell shell = getShell();

		/** initialize all contributors **/
		for (IEcoSimulatorContributor contributor : contributors) {
			contributor.initialize(this);
		}

		/** create simulation model **/
		this.bus = new Bus();
		this.cpu = new Cpu();
		cpu.setBus(bus);
		for (IEcoSimulatorContributor contributor : contributors) {
			contributor.registerPeripheralDevices(bus);
		}
		bus.buildBusMap();
		this.instructionsLeftUntilTick = INSTRUCTIONS_PER_TICK;
		this.fastSimulationMode = false;
		this.globalMemoryVisualizationStorageStrategy.setCpu(cpu);
		this.globalMemoryVisualizationStorageStrategy.setBus(bus);

		// TODO: remove this
		for (int i = 0; i < 32; i++) {
			cpu.getMemoryManagementUnit().setTlbEntry(i, 0xffffffff, 0, false);
		}

		/** create GUI components **/
		shell.setLayout(new RubberLayout(800, 600));
		tabFolder = new TabFolder(shell, SWT.TOP);

		/** build memory visualization system **/
		for (IEcoSimulatorContributor contributor : contributors) {
			IMainMemoryVisualizationContributor memoryVisualizationContributor = contributor.getMemoryVisualizationContributor();
			if (memoryVisualizationContributor == null) {
				continue;
			}
			for (IMainMemoryVisualizationContributionItem memoryVisualizationContributionItem : memoryVisualizationContributor.getContributionItems()) {
				globalMemoryVisualizationStorageStrategy.addDeviceStrategy(memoryVisualizationContributionItem.getDevice(), memoryVisualizationContributionItem.getLocalStorageStrategy());
			}
		}

		/** add tab items from contributors **/
		for (IEcoSimulatorContributor contributor : contributors) {
			contributor.createTabItems(tabFolder);
		}

		/** initialize the GUI **/
		tabFolder.pack();
		shell.pack();
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.swtlib.application.AbstractSingleDisplayApplication#idle()
	 */
	@Override
	protected void idle() {
		if (fastSimulationMode) {
			if (bulkStep()) {
				setFastSimulationMode(false);
			}
		} else {
			sleep();
		}
	}

	/**
	 * Performs a bulk step. This method simply invokes step()
	 * repeatedly (BULK_STEP_SIZE times).
	 * @return Returns true if a breakpoint was reached, false
	 * if the bulk step completed normally. 
	 */
	public boolean bulkStep() {
		for (int i = 0; i < BULK_STEP_SIZE; i++) {
			step();
			if (isAtBreakPoint()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return Returns true if the current PC value is that same as an
	 * active breakpoint, false if there is no or only an inactive matching
	 * break point.
	 */
	public boolean isAtBreakPoint() {
		Boolean breakPoint = breakpointSet.get(cpu.getPc().getValue());
		return (breakPoint != null && breakPoint);
	}

	/**
	 * Performs a single simulation step. This executes one instruction (or performs
	 * an interrupt/exception entry) and - if enough instructions have been executed -
	 * sends a timer tick to all peripheral devices.
	 */
	public void step() {

		/** perform the simulation step **/
		cpu.step();
		instructionsLeftUntilTick--;
		if (instructionsLeftUntilTick == 0) {
			instructionsLeftUntilTick = INSTRUCTIONS_PER_TICK;
			bus.tick();
		}

	}

	/**
	 * Enables or disables fast simulation mode. This passes fast simulation
	 * mode enabling/disabling to all devices. Furthermore, as long as fast
	 * simulation mode is enabled, simulation steps are performed as long as
	 * the GUI is idle.
	 * @see IEcoSimulatorContributor#setEnableFastSimulationMode(boolean)
	 * @param enable whether to enable or disable fast simulation mode
	 */
	public void setFastSimulationMode(boolean enable) {

		/** enable/disable fast simulation mode **/
		this.fastSimulationMode = enable;
		for (IEcoSimulatorContributor contributor : contributors) {
			contributor.setEnableFastSimulationMode(enable);
		}

	}

	/**
	 * Returns true if fast simulation mode is currently enabled, false if not.
	 * @return Returns the fastSimulationMode.
	 */
	public boolean isFastSimulationMode() {
		return fastSimulationMode;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.framework.memory.IMainMemoryVisualizationListenerRegistry#registerListener(name.martingeisse.ecotools.simulator.ui.framework.memory.IMainMemoryVisualizationListener)
	 */
	@Override
	public void registerListener(IMainMemoryVisualizationListener listener) {
		mainMemoryVisualizationListenerRegistry.registerListener(listener);
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.framework.memory.IMainMemoryVisualizationListenerRegistry#unregisterListener(name.martingeisse.ecotools.simulator.ui.framework.memory.IMainMemoryVisualizationListener)
	 */
	@Override
	public void unregisterListener(IMainMemoryVisualizationListener listener) {
		mainMemoryVisualizationListenerRegistry.unregisterListener(listener);
	}

	/**
	 * @see name.martingeisse.ecotools.simulator.ui.framework.memory.MainMemoryVisualizationListenerRegistry#fireAfterWrite()
	 */
	public void fireAfterWriteMainMemory() {
		mainMemoryVisualizationListenerRegistry.fireAfterWrite();
	}

}
