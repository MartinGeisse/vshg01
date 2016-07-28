/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.swtlib.panel;

import name.martingeisse.swtlib.layout.CenterLayout;
import name.martingeisse.swtlib.util.test.wizard.AbstractTestWizardApplication;
import name.martingeisse.swtlib.util.test.wizard.ITestWizardItem;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * Test for the test wizard.
 */
public class TabbedBrowsingTestApplication extends AbstractTestWizardApplication {

	/**
	 * Constructor
	 */
	public TabbedBrowsingTestApplication() {
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.swtlib.util.test.wizard.AbstractTestWizardApplication#createItems()
	 */
	@Override
	protected ITestWizardItem[] createItems() {
		return new ITestWizardItem[] {
			new TabbedBrowsingTest()
		};
	}

	/**
	 * Main method.
	 * @param args ...
	 */
	public static void main(String[] args) {
		TabbedBrowsingTestApplication app = new TabbedBrowsingTestApplication();
		app.create();
		app.open();
		app.mainLoop();
		app.dispose();
		app.exit();
	}

	private class TabbedBrowsingTest implements ITestWizardItem {

		/* (non-Javadoc)
		 * @see name.martingeisse.swtlib.util.test.wizard.ITestWizardItem#createComponent(org.eclipse.swt.widgets.Composite)
		 */
		@Override
		public void createComponent(Composite parent) {
			new AbstractTabbedBrowsingPanel<MyTabControl>(parent, MyTabControl.class) {
				
				/* (non-Javadoc)
				 * @see name.martingeisse.swtlib.panel.AbstractTabbedBrowsingPanel#makeAddressActive(org.eclipse.swt.widgets.Control, java.lang.String)
				 */
				@Override
				protected void makeAddressActive(MyTabControl tabControl, String address) {
					tabControl.setText(address);
				}
				
				/* (non-Javadoc)
				 * @see name.martingeisse.swtlib.panel.AbstractTabbedBrowsingPanel#createNewTabControl(org.eclipse.swt.widgets.Composite)
				 */
				@Override
				protected MyTabControl createNewTabControl(Composite parent) {
					return new MyTabControl(parent);
				}
			};
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.swtlib.util.test.wizard.ITestWizardItem#getDescription()
		 */
		@Override
		public String getDescription() {
			return "The component should implement the 'tabbed browsing' metaphor and display the address (centered) as the content.";
		}

	}
	
	/**
	 * This control simply shows a centered label text.
	 */
	private class MyTabControl extends Composite {
		
		/**
		 * the label
		 */
		private Label label;
		
		/**
		 * Constructor
		 * @param parent the parent composite
		 */
		public MyTabControl(Composite parent) {
			super(parent, 0);
			setLayout(new CenterLayout(false, false));
			label = new Label(this, SWT.LEFT);
		}
		
		/**
		 * @param text the text to show
		 */
		public void setText(String text) {
			label.setText(text);
			layout();
		}
		
	}

}
