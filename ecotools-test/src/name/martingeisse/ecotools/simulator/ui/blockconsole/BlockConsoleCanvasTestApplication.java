/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.blockconsole;

import name.martingeisse.ecotools.simulator.bus.DefaultInterruptLine;
import name.martingeisse.ecotools.simulator.bus.IInterruptLine;
import name.martingeisse.ecotools.simulator.devices.blockdisplay.BlockDisplay;
import name.martingeisse.ecotools.simulator.devices.keyboard.Keyboard;
import name.martingeisse.swtlib.util.test.wizard.AbstractTestWizardApplication;
import name.martingeisse.swtlib.util.test.wizard.ITestWizardItem;

import org.eclipse.swt.widgets.Composite;

/**
 * Test for the console contributor.
 */
public class BlockConsoleCanvasTestApplication extends AbstractTestWizardApplication {

	/**
	 * Constructor
	 */
	public BlockConsoleCanvasTestApplication() {
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.swtlib.util.test.wizard.AbstractTestWizardApplication#createItems()
	 */
	@Override
	protected ITestWizardItem[] createItems() {
		return new ITestWizardItem[] {
			new TestCornersItem(),
			new TestAttributesItem(),
			new TestKeyboardItem(),
		};
	}

	/**
	 * Main method.
	 * @param args ...
	 */
	public static void main(String[] args) {
		BlockConsoleCanvasTestApplication app = new BlockConsoleCanvasTestApplication();
		app.create();
		app.open();
		app.mainLoop();
		app.dispose();
		app.exit();
	}

	/**
	 * 
	 */
	private abstract class AbstractItem implements ITestWizardItem {

		/**
		 * the keyboard
		 */
		protected Keyboard keyboard;

		/**
		 * the display
		 */
		protected BlockDisplay display;
		
		/**
		 * the canvas
		 */
		protected BlockConsoleCanvas canvas;

		/**
		 * @param parent the parent composite
		 */
		public void createComponentHelper(Composite parent) {
			keyboard = new Keyboard() {

				/* (non-Javadoc)
				 * @see name.martingeisse.ecotools.simulator.devices.keyboard.Keyboard#onInputAvailable()
				 */
				@Override
				public void onInputAvailable() {
					while (getUserInterface().hasInput()) {
						onKeyPress(getUserInterface().receiveByte());
					}
				}
				
			};
			keyboard.connectInterruptLines(new IInterruptLine[] {new DefaultInterruptLine()});
			display = new BlockDisplay();
			
			canvas = new BlockConsoleCanvas(parent);
			canvas.setKeyboardUserInterfaceSocket(keyboard);
			keyboard.setUserInterface(canvas);
			canvas.setBlockDisplayUserInterfaceSocket(display);
			display.setUserInterface(canvas);
			canvas.updateAllBlocks();
		}
		
		/**
		 * @param scanCode the scan code
		 */
		protected void onKeyPress(int scanCode) {
			
		}
		
	}
	
	/**
	 * 
	 */
	private class TestCornersItem extends AbstractItem {

		/* (non-Javadoc)
		 * @see name.martingeisse.swtlib.util.test.wizard.ITestWizardItem#createComponent(org.eclipse.swt.widgets.Composite)
		 */
		@Override
		public void createComponent(Composite parent) {
			createComponentHelper(parent);
			display.setBlock(0, 0, 17);
			display.setBlock(39, 0, 18);
			display.setBlock(0, 29, 19);
			display.setBlock(39, 29, 20);
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.swtlib.util.test.wizard.ITestWizardItem#getDescription()
		 */
		@Override
		public String getDescription() {
			return "You should see the letters ABCD in the corners of the canvas in that order.";
		}

	}

	/**
	 * 
	 */
	private class TestAttributesItem extends AbstractItem {

		/* (non-Javadoc)
		 * @see name.martingeisse.swtlib.util.test.wizard.ITestWizardItem#createComponent(org.eclipse.swt.widgets.Composite)
		 */
		@Override
		public void createComponent(Composite parent) {
			createComponentHelper(parent);
			for (int i=0; i<8; i++) {
				for (int j=0; j<8; j++) {
					display.setBlock(i, j, 8*j+i);
				}
			}
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.swtlib.util.test.wizard.ITestWizardItem#getDescription()
		 */
		@Override
		public String getDescription() {
			return "You should see all possible blocks.";
		}

	}

	/**
	 * 
	 */
	private class TestKeyboardItem extends AbstractItem {

		/* (non-Javadoc)
		 * @see name.martingeisse.swtlib.util.test.wizard.ITestWizardItem#createComponent(org.eclipse.swt.widgets.Composite)
		 */
		@Override
		public void createComponent(Composite parent) {
			createComponentHelper(parent);
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.swtlib.util.test.wizard.ITestWizardItem#getDescription()
		 */
		@Override
		public String getDescription() {
			return "Focus the canvas and press the enter key. You should see a block in the upper left corner.";
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.ecotools.simulator.ui.console.ConsoleCanvasTestApplication.AbstractItem#onKeyPress(int)
		 */
		@Override
		protected void onKeyPress(int scanCode) {
			if (scanCode == 90) {
				display.setBlock(0, 0, 1);
			}
		}
		
	}

}
