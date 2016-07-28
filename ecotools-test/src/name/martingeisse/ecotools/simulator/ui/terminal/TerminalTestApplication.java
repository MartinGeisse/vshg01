/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.terminal;

import static org.junit.Assert.assertFalse;
import name.martingeisse.ecotools.simulator.devices.terminal.Terminal;
import name.martingeisse.swtlib.util.test.wizard.AbstractTestWizardApplication;
import name.martingeisse.swtlib.util.test.wizard.ITestWizardItem;

import org.eclipse.swt.widgets.Composite;

/**
 * Test for the console contributor.
 */
public class TerminalTestApplication extends AbstractTestWizardApplication {

	/**
	 * Constructor
	 */
	public TerminalTestApplication() {
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
		TerminalTestApplication app = new TerminalTestApplication();
		app.create();
		app.open();
		app.mainLoop();
		app.dispose();
		app.exit();
	}

	/**
	 * 
	 */
	private class TestItem implements ITestWizardItem {

		/**
		 * the panel
		 */
		private TerminalPanel panel;
		
		/**
		 * the terminal
		 */
		private Terminal terminal;
		
		/**
		 * @param s the string to send
		 */
		private void sendString(String s) {
			for (int i=0; i<s.length(); i++) {
				panel.sendByte((byte)(s.charAt(i)));
			}
		}
		
		/* (non-Javadoc)
		 * @see name.martingeisse.swtlib.util.test.wizard.ITestWizardItem#createComponent(org.eclipse.swt.widgets.Composite)
		 */
		@Override
		public void createComponent(Composite parent) {
			
			terminal = new Terminal() {

				/* (non-Javadoc)
				 * @see name.martingeisse.ecotools.simulator.devices.terminal.Terminal#onInputAvailable()
				 */
				@Override
				public void onInputAvailable() {
					/**
					 * We don't call super.onInputAvailable() here, as we don't want the
					 * terminal to consume any input.
					 */
					while (getUserInterface().hasInput()) {
						sendString("You typed: '");
						panel.sendByte(getUserInterface().receiveByte());
						sendString("'.\n");
					}
				}
				
			};
			
			panel = new TerminalPanel(parent);
			
			terminal.setUserInterface(panel);
			panel.setTerminalUserInterfaceSocket(terminal);
			
			assertFalse(panel.hasInput());
			sendString("Hello World!\n");
			sendString("This is the second line, and it is very very very very very very very very very very very very very very very very very very very very very very very very very very very very very very long!\n");
			sendString("This line contains '");
			panel.sendCorruptedByte();
			sendString("', which indicates a corrupted byte.\n");
			sendString("This line is terminated with CR/LF, but you should not see an empty line since the terminal transforms it to a single LF.\r\n");
			sendString("Now type something.\n");
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.swtlib.util.test.wizard.ITestWizardItem#getDescription()
		 */
		@Override
		public String getDescription() {
			return "You should see a message in the panel, and additional messages for input characters.";
		}

	}

}
