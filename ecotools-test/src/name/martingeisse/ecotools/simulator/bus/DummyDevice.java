/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.bus;

/**
 * 
 */
public class DummyDevice implements IPeripheralDevice {

	/**
	 * the localAddressBitCount
	 */
	private int localAddressBitCount;

	/**
	 * the readValueModifier
	 */
	private int readValueModifier;

	/**
	 * the tickCount
	 */
	private int tickCount;

	/**
	 * Constructor
	 * @param localAddressBitCount the local address bit count
	 */
	public DummyDevice(int localAddressBitCount) {
		this(localAddressBitCount, 42);
	}

	/**
	 * Constructor
	 * @param localAddressBitCount the local address bit count
	 * @param readValueModifier the modifier that is added to the address to obtain read values
	 */
	public DummyDevice(int localAddressBitCount, int readValueModifier) {
		this.localAddressBitCount = localAddressBitCount;
		this.readValueModifier = readValueModifier;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.bus.PeripheralDevice#connectInterruptLines(name.martingeisse.ecotools.simulator.bus.InterruptLine[])
	 */
	@Override
	public void connectInterruptLines(IInterruptLine[] interruptLines) {
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.bus.PeripheralDevice#getInterruptLineCount()
	 */
	@Override
	public int getInterruptLineCount() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.bus.PeripheralDevice#getLocalAddressBitCount()
	 */
	@Override
	public int getLocalAddressBitCount() {
		return localAddressBitCount;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.bus.PeripheralDevice#read(int, name.martingeisse.ecotools.simulator.bus.BusAccessSize)
	 */
	@Override
	public int read(int localAddress, BusAccessSize size) {
		return localAddress + readValueModifier;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.bus.PeripheralDevice#write(int, name.martingeisse.ecotools.simulator.bus.BusAccessSize, int)
	 */
	@Override
	public void write(int localAddress, BusAccessSize size, int value) {
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.bus.IPeripheralDevice#dispose()
	 */
	@Override
	public void dispose() {
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.timer.ITickable#tick()
	 */
	@Override
	public void tick() {
		tickCount++;
	}

	/**
	 * @return Returns the tickCount.
	 */
	public int getTickCount() {
		return tickCount;
	}

}
