/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.webeco.model;

import java.io.File;

import name.martingeisse.ecotools.simulator.bus.Bus;
import name.martingeisse.ecotools.simulator.cpu.Cpu;
import name.martingeisse.ecotools.simulator.devices.chardisplay.CharacterDisplay;
import name.martingeisse.ecotools.simulator.devices.disk.Disk;
import name.martingeisse.ecotools.simulator.devices.keyboard.Keyboard;
import name.martingeisse.ecotools.simulator.devices.memory.Ram;
import name.martingeisse.ecotools.simulator.devices.memory.Rom;
import name.martingeisse.ecotools.simulator.devices.output.OutputDevice;
import name.martingeisse.ecotools.simulator.devices.terminal.Terminal;
import name.martingeisse.ecotools.simulator.devices.timer.Timer;
import name.martingeisse.ecotools.simulator.ui.extension.CpuExtensionHandler;
import name.martingeisse.webeco.GuiMessageHub;

/**
 * This class acts as a container for the actual device models
 * and performs initialization as well as simulation steps.
 */
public class SimulationModel {

	/**
	 * The number of CPU instructions per bus tick.
	 */
	public static final int INSTRUCTIONS_PER_TICK = 500;

	/**
	 * the instructionsLeftUntilTick
	 */
	private int instructionsLeftUntilTick;

	/**
	 * the bus
	 */
	private Bus bus;

	/**
	 * the cpu
	 */
	private Cpu cpu;

	/**
	 * the ram
	 */
	private Ram ram;

	/**
	 * the rom
	 */
	private Rom rom;

	/**
	 * the disk
	 */
	private Disk disk;

	/**
	 * the terminal
	 */
	private Terminal terminal;

	/**
	 * the terminalUserInterface
	 */
	private TerminalUserInterface terminalUserInterface;

	/**
	 * the characterDisplay
	 */
	private CharacterDisplay characterDisplay;

	/**
	 * the characterDisplayUserInterface
	 */
	private CharacterDisplayUserInterface characterDisplayUserInterface;

	/**
	 * the keyboard
	 */
	private Keyboard keyboard;

	/**
	 * the keyboardUserInterface
	 */
	private KeyboardUserInterface keyboardUserInterface;

	/**
	 * the output
	 */
	private OutputDevice output;

	/**
	 * the timer
	 */
	private Timer timer;

	/**
	 * Constructor.
	 */
	public SimulationModel() {
	}

	/**
	 * Getter method for the bus.
	 * @return the bus
	 */
	public Bus getBus() {
		return bus;
	}

	/**
	 * Getter method for the cpu.
	 * @return the cpu
	 */
	public Cpu getCpu() {
		return cpu;
	}

	/**
	 * Getter method for the ram.
	 * @return the ram
	 */
	public Ram getRam() {
		return ram;
	}

	/**
	 * Getter method for the rom.
	 * @return the rom
	 */
	public Rom getRom() {
		return rom;
	}

	/**
	 * Getter method for the disk.
	 * @return the disk
	 */
	public Disk getDisk() {
		return disk;
	}

	/**
	 * Getter method for the terminal.
	 * @return the terminal
	 */
	public Terminal getTerminal() {
		return terminal;
	}

	/**
	 * Getter method for the terminalUserInterface.
	 * @return the terminalUserInterface
	 */
	public TerminalUserInterface getTerminalUserInterface() {
		return terminalUserInterface;
	}

	/**
	 * Getter method for the characterDisplay.
	 * @return the characterDisplay
	 */
	public CharacterDisplay getCharacterDisplay() {
		return characterDisplay;
	}

	/**
	 * Getter method for the characterDisplayUserInterface.
	 * @return the characterDisplayUserInterface
	 */
	public CharacterDisplayUserInterface getCharacterDisplayUserInterface() {
		return characterDisplayUserInterface;
	}

	/**
	 * Getter method for the keyboard.
	 * @return the keyboard
	 */
	public Keyboard getKeyboard() {
		return keyboard;
	}

	/**
	 * Getter method for the keyboardUserInterface.
	 * @return the keyboardUserInterface
	 */
	public KeyboardUserInterface getKeyboardUserInterface() {
		return keyboardUserInterface;
	}

	/**
	 * Getter method for the output.
	 * @return the output
	 */
	public OutputDevice getOutput() {
		return output;
	}

	/**
	 * Getter method for the timer.
	 * @return the timer
	 */
	public Timer getTimer() {
		return timer;
	}

	/**
	 * 
	 */
	public void initializeDeviceModels() {
		this.instructionsLeftUntilTick = INSTRUCTIONS_PER_TICK;
		this.bus = new Bus();
		this.cpu = new Cpu();
		cpu.setBus(bus);
		createDevices();
		bus.buildBusMap();
		cpu.setExtensionHandler(new CpuExtensionHandler(cpu, bus, disk));

	}

	/**
	 * 
	 */
	private void createDevices() {
		try {
			
			// String buildPath = "/Users/martin/workspace/ecobuild/build";
			String buildPath = "../ecobuild/build";

			ram = new Ram(25);
			bus.add(0x00000000, ram, new int[] {});

			rom = new Rom(21);
			rom.readContentsFromFile(new File(buildPath + "/monitor/monitor.bin"));
			bus.add(0x20000000, rom, new int[] {});

			disk = new Disk(new File(buildPath + "/run/disk.img"));
			bus.add(0x30400000, disk, new int[] {
				8
			});

			terminal = new Terminal();
			bus.add(0x30300000, terminal, new int[] {
				1, 0
			});

			characterDisplay = new CharacterDisplay();
			bus.add(0x30100000, characterDisplay, new int[] {});

			keyboard = new Keyboard();
			bus.add(0x30200000, keyboard, new int[] {
				4
			});

			output = new OutputDevice(new File("testout.bin"));
			bus.add(0x3F000000, output, new int[] {});

			timer = new Timer();
			bus.add(0x30000000, new Timer(), new int[] {
				14
			});

		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param guiMessageHub the GUI message hub
	 */
	public void initializeDeviceUserInterfaces(final GuiMessageHub guiMessageHub) {

		terminalUserInterface = new TerminalUserInterface(guiMessageHub);
		terminal.setUserInterface(terminalUserInterface);

		characterDisplayUserInterface = new CharacterDisplayUserInterface(guiMessageHub);
		characterDisplay.setUserInterface(characterDisplayUserInterface);

		keyboardUserInterface = new KeyboardUserInterface();
		keyboard.setUserInterface(keyboardUserInterface);

	}

	/**
	 * 
	 */
	public void notifyTerminalInputAvailable() {
		if (terminalUserInterface.fetchInputChunkIfNecessary()) {
			terminal.onInputAvailable();
		}
	}

	/**
	 * Performs a single simulation step. This executes one instruction (or performs
	 * an interrupt/exception entry) and - if enough instructions have been executed -
	 * sends a timer tick to all peripheral devices.
	 */
	public void step() {
		cpu.step();
		instructionsLeftUntilTick--;
		if (instructionsLeftUntilTick == 0) {
			instructionsLeftUntilTick = INSTRUCTIONS_PER_TICK;
			bus.tick();
		}
	}

	/**
	 * Executes all pending actions from the GUI message hub.
	 */
	public void executePendingActions() {
		executePendingActions(Simulator.getGuiMessageHub());
	}
	
	/**
	 * Executes all pending actions from the GUI message hub.
	 * @param guiMessageHub the GUI message hub to fetch actions from
	 */
	public void executePendingActions(GuiMessageHub guiMessageHub) {
		while (true) {
			ISimulatorAction action = guiMessageHub.getActions().poll();
			if (action == null) {
				break;
			}
			action.execute(this);
		}
	}
	
}
