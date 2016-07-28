/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.blockconsole;

import java.util.LinkedList;

import name.martingeisse.ecotools.simulator.devices.blockdisplay.IBlockDisplayUserInterface;
import name.martingeisse.ecotools.simulator.devices.blockdisplay.IBlockDisplayUserInterfaceSocket;
import name.martingeisse.ecotools.simulator.devices.keyboard.IKeyboardUserInterface;
import name.martingeisse.ecotools.simulator.devices.keyboard.IKeyboardUserInterfaceSocket;
import name.martingeisse.ecotools.simulator.ui.util.keyboard.GermanAppleToGermanWindowsKeyboardMap;
import name.martingeisse.ecotools.simulator.ui.util.keyboard.IKeyboardMap;
import name.martingeisse.swtlib.canvas.AbstractBlockContentRetainingCanvas;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Composite;

/**
 * This component shows the contents of the block display
 */
public class BlockConsoleCanvas extends AbstractBlockContentRetainingCanvas implements IBlockDisplayUserInterface, IKeyboardUserInterface {

	/**
	 * the blockGenerator
	 */
	private SwtBlockGenerator blockGenerator;

	/**
	 * the blockDisplayUserInterfaceSocket
	 */
	private IBlockDisplayUserInterfaceSocket blockDisplayUserInterfaceSocket;

	/**
	 * the keyboardMap
	 */
	private IKeyboardMap keyboardMap;

	/**
	 * the inputQueue
	 */
	private LinkedList<Byte> inputQueue;

	/**
	 * the keyboardUserInterfaceSocket
	 */
	private IKeyboardUserInterfaceSocket keyboardUserInterfaceSocket;

	/**
	 * Constructor
	 * @param parent the parent of this component
	 */
	public BlockConsoleCanvas(Composite parent) {
		super(parent, 16, 16, 40, 30);
		addKeyListener(new MyKeyListener());
		this.blockGenerator = new SwtBlockGenerator(getDisplay());
		this.keyboardMap = GermanAppleToGermanWindowsKeyboardMap.getInstance();
		this.inputQueue = new LinkedList<Byte>();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Widget#dispose()
	 */
	@Override
	public void dispose() {
		blockGenerator.dispose();
		super.dispose();
	}

	/**
	 * @return Returns the blockDisplayUserInterfaceSocket.
	 */
	public IBlockDisplayUserInterfaceSocket getBlockDisplayUserInterfaceSocket() {
		return blockDisplayUserInterfaceSocket;
	}

	/**
	 * Sets the blockDisplayUserInterfaceSocket.
	 * @param blockDisplayUserInterfaceSocket the new value to set
	 */
	public void setBlockDisplayUserInterfaceSocket(IBlockDisplayUserInterfaceSocket blockDisplayUserInterfaceSocket) {
		this.blockDisplayUserInterfaceSocket = blockDisplayUserInterfaceSocket;
	}

	/**
	 * @return Returns the keyboardUserInterfaceSocket.
	 */
	public IKeyboardUserInterfaceSocket getKeyboardUserInterfaceSocket() {
		return keyboardUserInterfaceSocket;
	}

	/**
	 * Sets the keyboardUserInterfaceSocket.
	 * @param keyboardUserInterfaceSocket the new value to set
	 */
	public void setKeyboardUserInterfaceSocket(IKeyboardUserInterfaceSocket keyboardUserInterfaceSocket) {
		this.keyboardUserInterfaceSocket = keyboardUserInterfaceSocket;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.devices.display.DisplayUserInterface#update(name.martingeisse.ecotools.simulator.devices.display.Display, int, int)
	 */
	@Override
	public void update(IBlockDisplayUserInterfaceSocket characterDisplayUserInterfaceSocket, int x, int y) {
		updateBlock(x, y);
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.swtlib.canvas.AbstractBlockContentRetainingCanvas#drawBlock(org.eclipse.swt.graphics.ImageData, int, int)
	 */
	@Override
	protected void drawBlock(ImageData data, int x, int y) {
		if (x < 0 || y < 0 || x >= 40 || y >= 30) {
			throw new IllegalArgumentException("Invalid block cell position: " + x + ", " + y);
		}
		int block = blockDisplayUserInterfaceSocket.getBlock(x, y);
		if (block < 0 || block >= 64) {
			throw new IllegalArgumentException("Invalid block type: " + block);
		}
		blockGenerator.drawBlock(data, x * 16, y * 16, block);
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.devices.keyboard.IKeyboardUserInterface#hasInput()
	 */
	@Override
	public boolean hasInput() {
		return !inputQueue.isEmpty();
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.devices.keyboard.IKeyboardUserInterface#receiveByte()
	 */
	@Override
	public byte receiveByte() throws IllegalStateException {
		return inputQueue.removeFirst();
	}

	/**
	 * Enqueues the specified scan codes in the input queue.
	 * @param scanCodes the scan codes to enqueue
	 */
	private void enqueueScanCodes(int[] scanCodes) {
		if (scanCodes != null) {
			for (int scanCode : scanCodes) {
				inputQueue.addLast((byte)scanCode);
			}
			keyboardUserInterfaceSocket.onInputAvailable();
		}
	}
	
	/**
	 * 
	 */
	private class MyKeyListener implements KeyListener {

		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.KeyListener#keyPressed(org.eclipse.swt.events.KeyEvent)
		 */
		@Override
		public void keyPressed(KeyEvent event) {
			enqueueScanCodes(keyboardMap.translate(event, true));
		}

		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.KeyListener#keyReleased(org.eclipse.swt.events.KeyEvent)
		 */
		@Override
		public void keyReleased(KeyEvent event) {
			enqueueScanCodes(keyboardMap.translate(event, false));
		}

	}

}
