/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecotools.simulator.ui.extension;

import name.martingeisse.ecotools.simulator.bus.Bus;
import name.martingeisse.ecotools.simulator.cpu.Cpu;
import name.martingeisse.ecotools.simulator.cpu.ICpuExtensionHandler;
import name.martingeisse.ecotools.simulator.cpu.ProgramCounter;
import name.martingeisse.ecotools.simulator.devices.disk.Disk;

/**
 * Our CPU extension handler.
 */
public class CpuExtensionHandler implements ICpuExtensionHandler {

	/**
	 * the cpu
	 */
	private Cpu cpu;
	
	/**
	 * the bus
	 */
	private Bus bus;
	
	/**
	 * the disk
	 */
	private Disk disk;
	
	/**
	 * Constructor.
	 * @param cpu the CPU
	 * @param bus the bus
	 * @param disk the disk
	 */
	public CpuExtensionHandler(Cpu cpu, Bus bus, Disk disk) {
		this.cpu = cpu;
		this.bus = bus;
		this.disk = disk;
	}
	
	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.cpu.ICpuExtensionHandler#handleIllegalOpcode(int)
	 */
	@Override
	public boolean handleIllegalOpcode(int instruction) {
		
		// wait for disk done, but prevent infinite loop
		if (instruction == 0xFC000000 && disk != null) {
			for (int i=0; i<100000; i++) {
				if (disk.isDone()) {
					incrementPc();
					return true;
				}
				bus.tick();
			}
			return false;
		}
		
		return false;
	}

	/**
	 * Convenience function to increment the PC by 4 (one instruction).
	 */
	private void incrementPc() {
		ProgramCounter pc = cpu.getPc();
		pc.setValue(pc.getValue() + 4, true);
	}
	
}
