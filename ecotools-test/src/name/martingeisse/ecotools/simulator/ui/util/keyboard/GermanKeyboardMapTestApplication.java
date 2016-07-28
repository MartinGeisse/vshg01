/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.util.keyboard;

import name.martingeisse.swtlib.util.test.wizard.AbstractTestWizardApplication;
import name.martingeisse.swtlib.util.test.wizard.ITestWizardItem;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * Test for the console contributor.
 */
public class GermanKeyboardMapTestApplication extends AbstractTestWizardApplication {

	/**
	 * Constructor
	 */
	public GermanKeyboardMapTestApplication() {
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.swtlib.util.test.wizard.AbstractTestWizardApplication#createItems()
	 */
	@Override
	protected ITestWizardItem[] createItems() {
		return new ITestWizardItem[] {
			new TestGermanKeyboardMapItem(),
		};
	}

	/**
	 * Main method.
	 * @param args ...
	 */
	public static void main(String[] args) {
		GermanKeyboardMapTestApplication app = new GermanKeyboardMapTestApplication();
		app.create();
		app.open();
		app.mainLoop();
		app.dispose();
		app.exit();
	}

	/**
	 * 
	 */
	private class TestGermanKeyboardMapItem implements ITestWizardItem {

		/* (non-Javadoc)
		 * @see name.martingeisse.swtlib.util.test.wizard.ITestWizardItem#createComponent(org.eclipse.swt.widgets.Composite)
		 */
		@Override
		public void createComponent(Composite parent) {
			final Text text = new Text(parent, SWT.LEFT | SWT.MULTI | SWT.WRAP | SWT.READ_ONLY);

			text.addTraverseListener(new TraverseListener() {
				@Override
				public void keyTraversed(TraverseEvent e) {
					switch (e.detail) {
					case SWT.TRAVERSE_TAB_NEXT:
					case SWT.TRAVERSE_TAB_PREVIOUS: {
						e.doit = false;
					}
					}
				}
			});

			text.addKeyListener(new KeyListener() {

				@Override
				public void keyReleased(KeyEvent event) {
					int[] scanCodes = GermanAppleToGermanWindowsKeyboardMap.getInstance().translate(event, false);
					if (scanCodes != null) {
						StringBuilder builder = new StringBuilder(text.getText());
						for (int scanCode : scanCodes) {
							builder.append(' ').append(Integer.toHexString(scanCode));
						}
						text.setText(builder.toString());
					}
				}

				@Override
				public void keyPressed(KeyEvent event) {
					int[] scanCodes = GermanAppleToGermanWindowsKeyboardMap.getInstance().translate(event, true);
					if (scanCodes != null) {
						StringBuilder builder = new StringBuilder(text.getText());
						for (int scanCode : scanCodes) {
							builder.append(' ').append(Integer.toHexString(scanCode));
						}
						text.setText(builder.toString());
					}
				}

			});
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.swtlib.util.test.wizard.ITestWizardItem#getDescription()
		 */
		@Override
		public String getDescription() {
			return "Press some keys. The scan codes are written to the system console.";
		}

	}

}
