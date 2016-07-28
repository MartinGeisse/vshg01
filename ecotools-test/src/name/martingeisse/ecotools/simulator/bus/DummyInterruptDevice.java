/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.bus;

/**
 * 
 */
public class DummyInterruptDevice implements IPeripheralDevice {

	/**
	 * the interruptCount
	 */
	private int interruptCount;
	
	/**
	 * the interruptLine
	 */
	private IInterruptLine interruptLine;

	/**
	 * Constructor
	 */
	public DummyInterruptDevice() {
		this(1);
	}

	/**
	 * Constructor
	 * @param interruptCount the number of interrupt lines
	 */
	public DummyInterruptDevice(int interruptCount) {
		this.interruptCount = interruptCount;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.bus.PeripheralDevice#connectInterruptLines(name.martingeisse.ecotools.simulator.bus.InterruptLine[])
	 */
	@Override
	public void connectInterruptLines(IInterruptLine[] interruptLines) {
		this.interruptLine = interruptLines[0];
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.bus.PeripheralDevice#getInterruptLineCount()
	 */
	@Override
	public int getInterruptLineCount() {
		return interruptCount;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.bus.PeripheralDevice#getLocalAddressBitCount()
	 */
	@Override
	public int getLocalAddressBitCount() {
		return 5;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.bus.PeripheralDevice#read(int, name.martingeisse.ecotools.simulator.bus.BusAccessSize)
	 */
	@Override
	public int read(int localAddress, BusAccessSize size) {
		return 0;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.bus.PeripheralDevice#write(int, name.martingeisse.ecotools.simulator.bus.BusAccessSize, int)
	 */
	@Override
	public void write(int localAddress, BusAccessSize size, int value) {
		setActive(false);
	}

	/**
	 * @return Returns the interruptLine.
	 */
	public IInterruptLine getInterruptLine() {
		return interruptLine;
	}

	/**
	 * @param active the interrupt status
	 */
	public void setActive(boolean active) {
		interruptLine.setActive(active);
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
	}

}
