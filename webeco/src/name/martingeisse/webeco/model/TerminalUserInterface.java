/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.webeco.model;

import name.martingeisse.ecotools.simulator.devices.terminal.ITerminalUserInterface;
import name.martingeisse.webeco.GuiMessageHub;

/**
 * An {@link ITerminalUserInterface} implementation that connects to
 * the terminal queues of a {@link GuiMessageHub}.
 */
public class TerminalUserInterface implements ITerminalUserInterface {

	/**
	 * the guiMessageHub
	 */
	private final GuiMessageHub guiMessageHub;

	/**
	 * the currentInputChunk
	 */
	private String currentInputChunk;
	
	/**
	 * Constructor.
	 * @param guiMessageHub the GUI message hub
	 */
	public TerminalUserInterface(GuiMessageHub guiMessageHub) {
		this.guiMessageHub = guiMessageHub;
		this.currentInputChunk = null;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.devices.terminal.ITerminalUserInterface#sendByte(byte)
	 */
	@Override
	public void sendByte(byte b) {
		guiMessageHub.getTerminalOutputQueue().add(Character.toString((char)(b & 0xff)));
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.devices.terminal.ITerminalUserInterface#sendCorruptedByte()
	 */
	@Override
	public void sendCorruptedByte() {
		guiMessageHub.getTerminalOutputQueue().add("?");
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.devices.terminal.ITerminalUserInterface#hasInput()
	 */
	@Override
	public boolean hasInput() {
		fetchInputChunkIfNecessary();
		return (currentInputChunk != null);
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.devices.terminal.ITerminalUserInterface#receiveByte()
	 */
	@Override
	public byte receiveByte() throws IllegalStateException {
		fetchInputChunkIfNecessary();
		if (currentInputChunk == null) {
			return 0;
		}
		
		char c = currentInputChunk.charAt(0);
		currentInputChunk = currentInputChunk.substring(1);
		return (byte)c;
	}

	/**
	 * @return true if input is actually available, whether fetched just now or previously
	 */
	public boolean fetchInputChunkIfNecessary() {
		if (currentInputChunk == null || currentInputChunk.length() == 0) {
			do {
				currentInputChunk = guiMessageHub.getTerminalInputQueue().poll();
			} while (currentInputChunk != null && currentInputChunk.length() == 0);
		}
		return (currentInputChunk != null);
	}
}
