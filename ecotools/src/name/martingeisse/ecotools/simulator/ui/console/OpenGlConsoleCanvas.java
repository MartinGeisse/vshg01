/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.console;

import java.util.LinkedList;

import name.martingeisse.ecotools.simulator.devices.chardisplay.ICharacterDisplayUserInterface;
import name.martingeisse.ecotools.simulator.devices.chardisplay.ICharacterDisplayUserInterfaceSocket;
import name.martingeisse.ecotools.simulator.devices.keyboard.IKeyboardUserInterface;
import name.martingeisse.ecotools.simulator.devices.keyboard.IKeyboardUserInterfaceSocket;
import name.martingeisse.ecotools.simulator.ui.util.keyboard.IKeyboardMap;
import name.martingeisse.swtlib.canvas.AbstractOpenGlBlockCanvas;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;

/**
 * This component shows the contents of the character display
 */
public class OpenGlConsoleCanvas extends AbstractOpenGlBlockCanvas implements ICharacterDisplayUserInterface, IKeyboardUserInterface {

	/**
	 * the BLOCK_WIDTH
	 */
	public static int BLOCK_WIDTH = 8;
	
	/**
	 * the BLOCK_HEIGHT
	 */
	public static int BLOCK_HEIGHT = 16;
	
	/**
	 * the characterDisplayUserInterfaceSocket
	 */
	private ICharacterDisplayUserInterfaceSocket characterDisplayUserInterfaceSocket;

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
	 * the characterGenerator
	 */
	private OpenGlCharacterGenerator characterGenerator;

	/**
	 * Constructor
	 * @param parent the parent of this component
	 * @param keyboardMap the keyboard map
	 */
	public OpenGlConsoleCanvas(Composite parent, IKeyboardMap keyboardMap) {
		super(parent, BLOCK_WIDTH, BLOCK_HEIGHT, 80, 30);
		addKeyListener(new MyKeyListener());
		this.keyboardMap = keyboardMap;
		this.inputQueue = new LinkedList<Byte>();
		this.characterGenerator = new OpenGlCharacterGenerator();
	}

	/**
	 * @return Returns the characterDisplayUserInterfaceSocket.
	 */
	public ICharacterDisplayUserInterfaceSocket getCharacterDisplayUserInterfaceSocket() {
		return characterDisplayUserInterfaceSocket;
	}

	/**
	 * Sets the characterDisplayUserInterfaceSocket.
	 * @param characterDisplayUserInterfaceSocket the new value to set
	 */
	public void setCharacterDisplayUserInterfaceSocket(ICharacterDisplayUserInterfaceSocket characterDisplayUserInterfaceSocket) {
		this.characterDisplayUserInterfaceSocket = characterDisplayUserInterfaceSocket;
		if (characterDisplayUserInterfaceSocket != null) {
			updateAllBlocks();
		}
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
	public void update(ICharacterDisplayUserInterfaceSocket characterDisplayUserInterfaceSocket, int x, int y) {
		updateBlock(x, y);
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.swtlib.canvas.AbstractOpenGlBlockCanvas#drawBlock(int, int)
	 */
	@Override
	protected void drawBlock(int x, int y) {
		int characterCode = characterDisplayUserInterfaceSocket.getCharacter(x, y);
		characterCode = characterCode & 0xff;
		int attributeCode = characterDisplayUserInterfaceSocket.getAttribute(x, y);
		attributeCode = attributeCode & 0xff;
		characterGenerator.drawBlock(x, y + 1, characterCode, attributeCode & 15, attributeCode >> 4);
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
	public void enqueueScanCodes(int[] scanCodes) {
		if (scanCodes != null) {
			for (int scanCode : scanCodes) {
				inputQueue.addLast((byte) scanCode);
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
