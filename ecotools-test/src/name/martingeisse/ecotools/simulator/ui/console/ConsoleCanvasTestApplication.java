/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.console;

import name.martingeisse.ecotools.simulator.bus.DefaultInterruptLine;
import name.martingeisse.ecotools.simulator.bus.IInterruptLine;
import name.martingeisse.ecotools.simulator.devices.chardisplay.CharacterDisplay;
import name.martingeisse.ecotools.simulator.devices.keyboard.Keyboard;
import name.martingeisse.ecotools.simulator.ui.util.keyboard.GermanAppleToGermanWindowsKeyboardMap;
import name.martingeisse.ecotools.simulator.ui.util.keyboard.IKeyboardMap;
import name.martingeisse.swtlib.util.test.wizard.AbstractTestWizardApplication;
import name.martingeisse.swtlib.util.test.wizard.ITestWizardItem;

import org.eclipse.swt.widgets.Composite;

/**
 * Test for the console contributor.
 */
public class ConsoleCanvasTestApplication extends AbstractTestWizardApplication {

	/**
	 * Constructor
	 */
	public ConsoleCanvasTestApplication() {
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.swtlib.util.test.wizard.AbstractTestWizardApplication#createItems()
	 */
	@Override
	protected ITestWizardItem[] createItems() {
		return new ITestWizardItem[] {
			new TestCornersItem(), new TestAttributesItem(), new TestKeyboardItem(), new TestAllCharactersItem(), new TestMissedKeysItem(),
		};
	}

	/**
	 * Main method.
	 * @param args ...
	 */
	public static void main(String[] args) {
		System.setProperty("java.library.path", "/Users/martin/Desktop/Schaffe/workspace/swtlib/lib");
		ConsoleCanvasTestApplication app = new ConsoleCanvasTestApplication();
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
		protected CharacterDisplay display;

		/**
		 * the canvas
		 */
		protected ConsoleCanvas canvas;

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
			keyboard.connectInterruptLines(new IInterruptLine[] {
				new DefaultInterruptLine()
			});
			display = new CharacterDisplay();

			canvas = createConsoleCanvas(parent, GermanAppleToGermanWindowsKeyboardMap.getInstance());
			canvas.setKeyboardUserInterfaceSocket(keyboard);
			keyboard.setUserInterface(canvas);
			canvas.setCharacterDisplayUserInterfaceSocket(display);
			display.setUserInterface(canvas);
		}

		/**
		 * @param scanCode the scan code
		 */
		protected void onKeyPress(int scanCode) {

		}

		/**
		 * @param parent the parent composite
		 * @param keyboardMap the keyboard map
		 * @return Returns the console canvas
		 */
		protected ConsoleCanvas createConsoleCanvas(Composite parent, IKeyboardMap keyboardMap) {
			return new ConsoleCanvas(parent, keyboardMap);
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
			display.setAttribute(0, 0, 15);
			display.setCharacter(0, 0, 'A');
			display.setAttribute(79, 0, 15);
			display.setCharacter(79, 0, 'B');
			display.setAttribute(0, 29, 15);
			display.setCharacter(0, 29, 'C');
			display.setAttribute(79, 29, 15);
			display.setCharacter(79, 29, 'D');
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
			canvas.setDelayUpdates(true);
			for (int i = 0; i < 16; i++) {
				for (int j = 0; j < 16; j++) {
					display.setAttribute(i, j, i + 16 * j);
					display.setCharacter(i, j, 'A');
				}
			}
			canvas.setDelayUpdates(false);
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.swtlib.util.test.wizard.ITestWizardItem#getDescription()
		 */
		@Override
		public String getDescription() {
			return "You should see the letter A in all possible attribute combinations.";
		}

	}

	/**
	 * 
	 */
	private class TestKeyboardItem extends AbstractItem {

		/**
		 * the x
		 */
		private int x = 0;

		/**
		 * the y
		 */
		private int y = 0;

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
			return "Focus the canvas and press letter keys. You should see the scan codes appear in the canvas.";
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.ecotools.simulator.ui.console.ConsoleCanvasTestApplication.AbstractItem#onKeyPress(int)
		 */
		@Override
		protected void onKeyPress(int scanCode) {
			int digit1 = (scanCode >> 4) & 15;
			printCharacter(Integer.toHexString(digit1).charAt(0));
			int digit2 = scanCode & 15;
			printCharacter(Integer.toHexString(digit2).charAt(0));
			printCharacter(' ');
		}

		/**
		 * @param c the character to print
		 */
		private void printCharacter(int c) {
			display.setCharacter(x, y, c);
			display.setAttribute(x, y, 15);
			x++;
			if (x == 78) {
				x = 0;
				y++;
			}
		}

	}

	/**
	 * 
	 */
	private class TestAllCharactersItem extends AbstractItem {

		/* (non-Javadoc)
		 * @see name.martingeisse.swtlib.util.test.wizard.ITestWizardItem#createComponent(org.eclipse.swt.widgets.Composite)
		 */
		@Override
		public void createComponent(Composite parent) {
			createComponentHelper(parent);
			canvas.setDelayUpdates(true);
			for (int x = 0; x < 80; x++) {
				for (int y = 0; y < 30; y++) {
					int c = (x < 16 && y < 16) ? (y * 16 + x) : ((y % 10) + '0');
					display.setCharacter(x, y, c);
					display.setAttribute(x, y, 15);
				}
			}
			canvas.setDelayUpdates(false);
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.swtlib.util.test.wizard.ITestWizardItem#getDescription()
		 */
		@Override
		public String getDescription() {
			return "You should see all characters.";
		}

	}

	/**
	 * 
	 */
	private class TestMissedKeysItem extends TestKeyboardItem {

		/* (non-Javadoc)
		 * @see name.martingeisse.ecotools.simulator.ui.console.ConsoleCanvasTestApplication.AbstractItem#createConsoleCanvas(org.eclipse.swt.widgets.Composite, name.martingeisse.ecotools.simulator.ui.util.keyboard.IKeyboardMap)
		 */
		@Override
		protected ConsoleCanvas createConsoleCanvas(Composite parent, IKeyboardMap keyboardMap) {
			ConsolePanel consolePanel = new ConsolePanel(parent, keyboardMap);
			return consolePanel.getConsoleCanvas();
		}

	}

}
