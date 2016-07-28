/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.console;

import name.martingeisse.ecotools.simulator.ui.util.keyboard.IKeyboardMap;
import name.martingeisse.ecotools.simulator.ui.util.keyboard.IKeyboardMapMissedKey;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * This class implements a panel that contains a console canvas
 * as well as buttons for missed keys.
 */
public class ConsolePanel extends Composite {

	/**
	 * the consoleCanvas
	 */
	private ConsoleCanvas consoleCanvas;
//	private OpenGlConsoleCanvas consoleCanvas;

	/**
	 * Constructor
	 * @param parent the parent composite
	 * @param keyboardMap the keyboard map
	 */
	public ConsolePanel(Composite parent, IKeyboardMap keyboardMap) {
		super(parent, 0);
		setLayout(new GridLayout(2, false));

		IKeyboardMapMissedKey[] missedKeys = keyboardMap.getMissedKeys();
//		consoleCanvas = new OpenGlConsoleCanvas(this, keyboardMap);
		consoleCanvas = new ConsoleCanvas(this, keyboardMap);
		consoleCanvas.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, missedKeys.length));

		for (IKeyboardMapMissedKey missedKey : missedKeys) {
			Button button = new Button(this, SWT.TOGGLE | SWT.CENTER);
			button.setText(missedKey.getLabel());
			button.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
			button.addSelectionListener(new MissedKeyButtonListener(missedKey));
		}

	}

	/**
	 * @return Returns the consoleCanvas.
	 */
	public ConsoleCanvas getConsoleCanvas() {
		return consoleCanvas;
	}
	
	/**
	 * This button selection listener implements a missed key.
	 */
	private class MissedKeyButtonListener extends SelectionAdapter {
		
		/**
		 * the missedKey
		 */
		private IKeyboardMapMissedKey missedKey;
		
		/**
		 * Constructor
		 * @param missedKey the missed key
		 */
		public MissedKeyButtonListener(IKeyboardMapMissedKey missedKey) {
			this.missedKey = missedKey;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		@Override
		public void widgetSelected(SelectionEvent event) {
			Button button = (Button)event.widget;
			boolean selection = button.getSelection();
			int[] scanCodes = selection ? (missedKey.getMakeCodeSequence()) : (missedKey.getBreakCodeSequence()); 
			consoleCanvas.enqueueScanCodes(scanCodes);
		}

	}

}
