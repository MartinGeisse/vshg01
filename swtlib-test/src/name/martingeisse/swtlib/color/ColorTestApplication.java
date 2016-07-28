/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.swtlib.color;

import name.martingeisse.swtlib.util.test.SingleColorCanvas;
import name.martingeisse.swtlib.util.test.wizard.AbstractTestWizardApplication;
import name.martingeisse.swtlib.util.test.wizard.ITestWizardItem;

import org.eclipse.swt.widgets.Composite;

/**
 * Test for the test wizard.
 */
public class ColorTestApplication extends AbstractTestWizardApplication {

	/**
	 * Constructor
	 */
	public ColorTestApplication() {
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.swtlib.util.test.wizard.AbstractTestWizardApplication#createItems()
	 */
	@Override
	protected ITestWizardItem[] createItems() {
		return new ITestWizardItem[] {
			new ColorTest(0, "black, dark blue, dark green, dark cyan"),
			new ColorTest(4, "dark red, dark purple, dark yellow, light gray"),
			new ColorTest(8, "dark gray, light blue, light green, light cyan"),
			new ColorTest(12, "light red, light purple, light yellow, white"),
		};
	}

	/**
	 * Main method.
	 * @param args ...
	 */
	public static void main(String[] args) {
		ColorTestApplication app = new ColorTestApplication();
		app.create();
		app.open();
		app.mainLoop();
		app.dispose();
		app.exit();
	}

	private class ColorTest implements ITestWizardItem {

		/**
		 * the start
		 */
		private int start;
		
		/**
		 * the colorDescription
		 */
		private String colorDescription;
		
		/**
		 * Constructor
		 * @param start the starting color
		 * @param colorDescription a description of the colors that should appear
		 */
		public ColorTest(int start, String colorDescription) {
			this.start = start;
			this.colorDescription = colorDescription;
		}
		
		/* (non-Javadoc)
		 * @see name.martingeisse.swtlib.util.test.wizard.ITestWizardItem#createComponent(org.eclipse.swt.widgets.Composite)
		 */
		@Override
		public void createComponent(Composite parent) {
			new SingleColorCanvas(parent, Colors.getColor(start + 0));
			new SingleColorCanvas(parent, Colors.getColor(start + 1));
			new SingleColorCanvas(parent, Colors.getColor(start + 2));
			new SingleColorCanvas(parent, Colors.getColor(start + 3));
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.swtlib.util.test.wizard.ITestWizardItem#getDescription()
		 */
		@Override
		public String getDescription() {
			return "COLORS: " + colorDescription;
		}

	}

}
