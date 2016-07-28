/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.terminal;

import java.util.LinkedList;

import name.martingeisse.ecotools.simulator.devices.terminal.ITerminalUserInterface;
import name.martingeisse.ecotools.simulator.devices.terminal.ITerminalUserInterfaceSocket;
import name.martingeisse.swtlib.color.PaleColors;
import name.martingeisse.swtlib.font.Fonts;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * The terminal "screen" and "keyboard" widgets. This front-end is connected
 * to the simulation model via an abstract "serial line".
 */
public class TerminalPanel extends Composite implements ITerminalUserInterface {

	/**
	 * the text
	 */
	private StyledText textView;

	/**
	 * the terminalSideEchoCheckbox
	 */
	private Button terminalSideEchoCheckbox;
	
	/**
	 * the inputQueue
	 */
	private LinkedList<Byte> inputQueue;

	/**
	 * the terminalUserInterfaceSocket
	 */
	private ITerminalUserInterfaceSocket terminalUserInterfaceSocket;
	
	/**
	 * the absorbNewlineOutputCharacter. This flag is set when a CR character is
	 * sent to the output to avoid a double newline for the CR/LF newline sequence.
	 */
	private boolean absorbNewlineOutputCharacter;

	/**
	 * Constructor
	 * @param parent the parent composite
	 */
	public TerminalPanel(Composite parent) {
		super(parent, 0);
		setLayout(new GridLayout(1, false));
		
		textView = new StyledText(this, SWT.LEFT | SWT.READ_ONLY | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		textView.setFont(Fonts.getCourier12());
		textView.addKeyListener(new MyKeyListener());
		textView.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		terminalSideEchoCheckbox = new Button(this, SWT.CHECK);
		terminalSideEchoCheckbox.setText("Terminal-side echo");
		terminalSideEchoCheckbox.setSelection(true);
		terminalSideEchoCheckbox.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));

		inputQueue = new LinkedList<Byte>();
	}

	/**
	 * @return Returns the terminalUserInterfaceSocket.
	 */
	public ITerminalUserInterfaceSocket getTerminalUserInterfaceSocket() {
		return terminalUserInterfaceSocket;
	}

	/**
	 * Sets the terminalUserInterfaceSocket.
	 * @param terminalUserInterfaceSocket the new value to set
	 */
	public void setTerminalUserInterfaceSocket(ITerminalUserInterfaceSocket terminalUserInterfaceSocket) {
		this.terminalUserInterfaceSocket = terminalUserInterfaceSocket;
	}

	/**
	 * Appends a character to the view.
	 * @param c the character to append
	 * @param backgroundColor the background color for the appended character
	 */
	public void appendToView(char c, Color backgroundColor) {
		int position = textView.getCharCount();
		textView.append(Character.toString(c));
		StyleRange styleRange = new StyleRange();
		styleRange.start = position;
		styleRange.length = 1;
		styleRange.background = backgroundColor;
		textView.setStyleRange(styleRange);
		textView.setCaretOffset(textView.getCharCount());
		textView.showSelection();
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.devices.terminal.ITerminalUserInterface#hasInput()
	 */
	@Override
	public boolean hasInput() {
		return !inputQueue.isEmpty();
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.devices.terminal.ITerminalUserInterface#receiveByte()
	 */
	@Override
	public byte receiveByte() throws IllegalStateException {
		return inputQueue.removeFirst();
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.devices.terminal.ITerminalUserInterface#sendByte(byte)
	 */
	@Override
	public void sendByte(byte b) {
		char c;
		if (b < 0) {
			c = '?';
		} else if (b == 0x0d) {
			c = '\n';
			absorbNewlineOutputCharacter = true;
		} else if (b == 0x0a) {
			if (absorbNewlineOutputCharacter) {
				absorbNewlineOutputCharacter = false;
				return;
			} else {
				c = '\n';
			}
		} else {
			c = (char)b;
		}
		appendToView(c, PaleColors.getGreen());
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.devices.terminal.ITerminalUserInterface#sendCorruptedByte()
	 */
	@Override
	public void sendCorruptedByte() {
		appendToView('*', PaleColors.getBlue());
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
			byte c = (byte)event.character;
			if (c != 0) {
				if (terminalSideEchoCheckbox.getSelection()) {
					appendToView(event.character, PaleColors.getRed());
				}
				inputQueue.addLast((byte) event.character);
				terminalUserInterfaceSocket.onInputAvailable();
			}
		}

		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.KeyListener#keyReleased(org.eclipse.swt.events.KeyEvent)
		 */
		@Override
		public void keyReleased(KeyEvent event) {
		}

	}
}
