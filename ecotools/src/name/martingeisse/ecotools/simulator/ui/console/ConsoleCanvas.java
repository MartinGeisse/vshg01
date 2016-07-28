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
import name.martingeisse.swtlib.canvas.AbstractBlockContentRetainingCanvas;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Composite;

/**
 * This component shows the contents of the character display
 */
public class ConsoleCanvas extends AbstractBlockContentRetainingCanvas implements ICharacterDisplayUserInterface, IKeyboardUserInterface {

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
	private SwtCharacterGenerator characterGenerator;

	/**
	 * Constructor
	 * @param parent the parent of this component
	 * @param keyboardMap the keyboard map
	 */
	public ConsoleCanvas(Composite parent, IKeyboardMap keyboardMap) {
		super(parent, BLOCK_WIDTH, BLOCK_HEIGHT, 80, 30);
		addKeyListener(new MyKeyListener());
		this.keyboardMap = keyboardMap;
		this.inputQueue = new LinkedList<Byte>();
		this.characterGenerator = new SwtCharacterGenerator(getDisplay());
	}
	
	/* (non-Javadoc)
	 * @see name.martingeisse.swtlib.canvas.ContentRetainingCanvas#dispose()
	 */
	@Override
	public void dispose() {
		characterGenerator.dispose();
		super.dispose();
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
	 * @see name.martingeisse.swtlib.canvas.AbstractBlockContentRetainingCanvas#drawBlock(org.eclipse.swt.graphics.ImageData, int, int)
	 */
	@Override
	protected void drawBlock(ImageData data, int x, int y) {
		int characterCode = characterDisplayUserInterfaceSocket.getCharacter(x, y);
		characterCode = characterCode & 0xff;
		int attributeCode = characterDisplayUserInterfaceSocket.getAttribute(x, y);
		int foreground = attributeCode & 15;
		int background = (attributeCode >> 4) & 15;
		characterGenerator.drawBlock(data, x * BLOCK_WIDTH, y * BLOCK_HEIGHT, characterCode, decodeColor(foreground), decodeColor(background));
	}
	
	/**
	 * the colorDecodingTable
	 */
	private static int[] colorDecodingTable = {
		0x00000000, // black
		0x00000080, // dark blue
		0x00008000, // dark green
		0x00008080, // dark cyan
		0x00800000, // dark red
		0x00800080, // dark violet
		0x00808000, // brown ("dark yellow")
		0x00c0c0c0, // light grey ("dark white")
		0x00808080, // dark grey ("light black")
		0x000000ff, // light blue
		0x0000ff00, // light green
		0x0000ffff, // light cyan
		0x00ff0000, // light red
		0x00ff00ff, // light violet
		0x00ffff00, // (light) yellow
		0x00ffffff, // white
	};
	
	/**
	 * @param colorIndex
	 * @return
	 */
	private int decodeColor(int colorIndex) {
		return colorDecodingTable[colorIndex];
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
