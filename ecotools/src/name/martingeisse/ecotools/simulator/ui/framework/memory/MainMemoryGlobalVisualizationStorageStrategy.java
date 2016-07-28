/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.framework.memory;

import java.util.HashMap;
import java.util.Map;

import name.martingeisse.ecotools.simulator.bus.Bus;
import name.martingeisse.ecotools.simulator.bus.BusAccessSize;
import name.martingeisse.ecotools.simulator.bus.BusMapEntry;
import name.martingeisse.ecotools.simulator.bus.IPeripheralDevice;
import name.martingeisse.ecotools.simulator.cpu.Cpu;
import name.martingeisse.ecotools.simulator.ui.util.memory.IMemoryVisualizationStorageStrategy;
import name.martingeisse.ecotools.simulator.ui.util.memory.MemoryVisualizationException;

/**
 * This storage strategy keeps a bus map to map global to local
 * addresses, and a set of device-specific local strategies to
 * which local addresses are routed.
 */
public class MainMemoryGlobalVisualizationStorageStrategy implements IMemoryVisualizationStorageStrategy {

	/**
	 * the cpu
	 */
	private Cpu cpu;

	/**
	 * the bus
	 */
	private Bus bus;

	/**
	 * the deviceStrategies
	 */
	private Map<IPeripheralDevice, IMemoryVisualizationStorageStrategy> deviceStrategies;

	/**
	 * Constructor
	 */
	public MainMemoryGlobalVisualizationStorageStrategy() {
		this.cpu = null;
		this.bus = null;
		this.deviceStrategies = new HashMap<IPeripheralDevice, IMemoryVisualizationStorageStrategy>();
	}

	/**
	 * @return Returns the cpu.
	 */
	public Cpu getCpu() {
		return cpu;
	}

	/**
	 * Sets the cpu.
	 * @param cpu the new value to set
	 */
	public void setCpu(Cpu cpu) {
		this.cpu = cpu;
	}

	/**
	 * @return Returns the bus.
	 */
	public Bus getBus() {
		return bus;
	}

	/**
	 * Sets the bus.
	 * @param bus the new value to set
	 */
	public void setBus(Bus bus) {
		this.bus = bus;
	}

	/**
	 * Adds a device-specific local strategy for the specified device. If this global strategy
	 * has already a strategy configured for the device, this global strategy is not modified,
	 * but an {@link IllegalStateException} is thrown.
	 * @param peripheralDevice the device for which the strategy shall be used
	 * @param deviceStrategy the strategy to use for the device
	 * @throws IllegalStateException if there is already a strategy configured for that device
	 */
	public void addDeviceStrategy(IPeripheralDevice peripheralDevice, IMemoryVisualizationStorageStrategy deviceStrategy) {
		if (deviceStrategies.containsKey(peripheralDevice)) {
			throw new IllegalStateException("device " + peripheralDevice + " already has a local memory visualization strategy");
		}
		deviceStrategies.put(peripheralDevice, deviceStrategy);
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.util.memory.IMemoryVisualizationStorageStrategy#read(int, name.martingeisse.ecotools.simulator.bus.BusAccessSize)
	 */
	@Override
	public int read(int virtualAddress, BusAccessSize accessSize) throws MemoryVisualizationException {

		/** address translation **/
		int physicalAddress = cpu.getMemoryManagementUnit().mapAddressForVisualization(virtualAddress);
		
		/** determine the bus map entry for this address **/
		BusMapEntry busMapEntry = bus.getBusMapEntryForAddress(physicalAddress);
		if (busMapEntry == null) {
			throw new MemoryVisualizationException("--- no device ---");
		}
		IPeripheralDevice device = busMapEntry.getDevice();

		/** get the local visualization strategy for this device **/
		IMemoryVisualizationStorageStrategy localStrategy = deviceStrategies.get(device);
		if (localStrategy == null) {
			throw new MemoryVisualizationException("--- device: " + device + " ---");
		}

		/** use the local strategy to access the device's memory **/
		return localStrategy.read(busMapEntry.extractLocalAddress(physicalAddress), accessSize);

	}

}
