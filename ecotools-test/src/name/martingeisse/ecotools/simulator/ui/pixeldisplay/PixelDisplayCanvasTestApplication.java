/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.pixeldisplay;

import name.martingeisse.ecotools.simulator.devices.pixeldisplay.PixelDisplay;
import name.martingeisse.swtlib.util.test.wizard.AbstractTestWizardApplication;
import name.martingeisse.swtlib.util.test.wizard.ITestWizardItem;

import org.eclipse.swt.widgets.Composite;

/**
 * Test for the console contributor.
 */
public class PixelDisplayCanvasTestApplication extends AbstractTestWizardApplication {

	/**
	 * Constructor
	 */
	public PixelDisplayCanvasTestApplication() {
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.swtlib.util.test.wizard.AbstractTestWizardApplication#createItems()
	 */
	@Override
	protected ITestWizardItem[] createItems() {
		return new ITestWizardItem[] {
			new TestItem(),
		};
	}

	/**
	 * Main method.
	 * @param args ...
	 */
	public static void main(String[] args) {
		System.setProperty("java.library.path", "/Users/martin/Desktop/Schaffe/workspace/swtlib/lib");
		PixelDisplayCanvasTestApplication app = new PixelDisplayCanvasTestApplication();
		app.create();
		app.open();
		app.mainLoop();
		app.dispose();
		app.exit();
	}

	private class TestItem implements ITestWizardItem {

		/* (non-Javadoc)
		 * @see name.martingeisse.swtlib.util.test.wizard.ITestWizardItem#createComponent(org.eclipse.swt.widgets.Composite)
		 */
		@Override
		public void createComponent(Composite parent) {
			
			PixelDisplay display = new PixelDisplay();
			PixelDisplayCanvas canvas = new PixelDisplayCanvas(parent);
			display.setUserInterface(canvas);
			canvas.setUserInterfaceSocket(display);
			
			canvas.setDelayUpdates(true);
			for (int i=200; i<440; i++) {
				for (int j=200; j<280; j++) {
					display.setPixel(i, j, 0x00800000);
				}
			}
			for (int i=200; i<440; i++) {
				display.setPixel(i, 200, 0x00ff0000);
				display.setPixel(i, 280, 0x00400000);
			}
			for (int j=200; j<280; j++) {
				display.setPixel(200, j, 0x00ff0000);
				display.setPixel(440, j, 0x00400000);
			}
			canvas.setDelayUpdates(false);
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.swtlib.util.test.wizard.ITestWizardItem#getDescription()
		 */
		@Override
		public String getDescription() {
			return "You should see a 'raised-style' rectangular red box.";
		}
		
	}

}
