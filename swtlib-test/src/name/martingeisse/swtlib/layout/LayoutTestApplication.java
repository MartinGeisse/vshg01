/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.swtlib.layout;

import name.martingeisse.swtlib.util.test.FixedSizeSingleColorCanvas;
import name.martingeisse.swtlib.util.test.SingleColorCanvas;
import name.martingeisse.swtlib.util.test.wizard.AbstractTestWizardApplication;
import name.martingeisse.swtlib.util.test.wizard.ITestWizardItem;

import org.eclipse.swt.widgets.Composite;

/**
 * Test for the test wizard.
 */
public class LayoutTestApplication extends AbstractTestWizardApplication {

	/**
	 * Constructor
	 */
	public LayoutTestApplication() {
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.swtlib.util.test.wizard.AbstractTestWizardApplication#createItems()
	 */
	@Override
	protected ITestWizardItem[] createItems() {
		return new ITestWizardItem[] {
			new ContextSensitiveCenteredFixedSizeItem(),
			new ContextSensitiveCenteredAdaptingItem(),
			new ContextFreeCenteredFixedSizeItem(),
			new ContextFreeCenteredAdaptingItem(),
			new RubberFixedItem(),
			new RubberAdaptingItem(),
		};
	}

	/**
	 * Main method.
	 * @param args ...
	 */
	public static void main(String[] args) {
		LayoutTestApplication app = new LayoutTestApplication();
		app.create();
		app.open();
		app.mainLoop();
		app.dispose();
		app.exit();
	}

	private class ContextSensitiveCenteredFixedSizeItem implements ITestWizardItem {

		/* (non-Javadoc)
		 * @see name.martingeisse.swtlib.util.test.wizard.ITestWizardItem#createComponent(org.eclipse.swt.widgets.Composite)
		 */
		@Override
		public void createComponent(Composite parent) {
			Composite composite = new Composite(parent, 0);
			composite.setLayout(new CenterLayout(true, true));
			new FixedSizeSingleColorCanvas(composite, 0, 0, 255, 100, 100);
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.swtlib.util.test.wizard.ITestWizardItem#getDescription()
		 */
		@Override
		public String getDescription() {
			return "CONTEXT-SENSITIVE CENTERED FIXED SIZE: the background should not resize with the component but stay in the center";
		}

	}

	private class ContextSensitiveCenteredAdaptingItem implements ITestWizardItem {

		/* (non-Javadoc)
		 * @see name.martingeisse.swtlib.util.test.wizard.ITestWizardItem#createComponent(org.eclipse.swt.widgets.Composite)
		 */
		@Override
		public void createComponent(Composite parent) {
			Composite composite = new Composite(parent, 0);
			composite.setLayout(new CenterLayout(true, true));
			new SingleColorCanvas(composite, 0, 0, 255);
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.swtlib.util.test.wizard.ITestWizardItem#getDescription()
		 */
		@Override
		public String getDescription() {
			return "CONTEXT-SENSITIVE CENTERED ADAPTING: the background should fill the whole area";
		}

	}

	private class ContextFreeCenteredFixedSizeItem implements ITestWizardItem {

		/* (non-Javadoc)
		 * @see name.martingeisse.swtlib.util.test.wizard.ITestWizardItem#createComponent(org.eclipse.swt.widgets.Composite)
		 */
		@Override
		public void createComponent(Composite parent) {
			Composite composite = new Composite(parent, 0);
			composite.setLayout(new CenterLayout(false, false));
			new FixedSizeSingleColorCanvas(composite, 0, 0, 255, 100, 100);
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.swtlib.util.test.wizard.ITestWizardItem#getDescription()
		 */
		@Override
		public String getDescription() {
			return "CONTEXT-FREE CENTERED FIXED SIZE: the background should not resize with the component but stay in the center";
		}

	}

	private class ContextFreeCenteredAdaptingItem implements ITestWizardItem {

		/* (non-Javadoc)
		 * @see name.martingeisse.swtlib.util.test.wizard.ITestWizardItem#createComponent(org.eclipse.swt.widgets.Composite)
		 */
		@Override
		public void createComponent(Composite parent) {
			Composite composite = new Composite(parent, 0);
			composite.setLayout(new CenterLayout(false, false));
			new SingleColorCanvas(composite, 0, 0, 255);
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.swtlib.util.test.wizard.ITestWizardItem#getDescription()
		 */
		@Override
		public String getDescription() {
			return "CONTEXT-FREE CENTERED FIXED SIZE: the background should not resize with the component but stay in the center";
		}

	}
	
	private class RubberFixedItem implements ITestWizardItem {

		/* (non-Javadoc)
		 * @see name.martingeisse.swtlib.util.test.wizard.ITestWizardItem#createComponent(org.eclipse.swt.widgets.Composite)
		 */
		@Override
		public void createComponent(Composite parent) {
			Composite composite = new Composite(parent, 0);
			composite.setLayout(new RubberLayout(200, 100));
			new FixedSizeSingleColorCanvas(composite, 0, 0, 255, 100, 100);
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.swtlib.util.test.wizard.ITestWizardItem#getDescription()
		 */
		@Override
		public String getDescription() {
			return "RUBBER FIXED: the background should fill the whole area";
		}

	}

	private class RubberAdaptingItem implements ITestWizardItem {

		/* (non-Javadoc)
		 * @see name.martingeisse.swtlib.util.test.wizard.ITestWizardItem#createComponent(org.eclipse.swt.widgets.Composite)
		 */
		@Override
		public void createComponent(Composite parent) {
			Composite composite = new Composite(parent, 0);
			composite.setLayout(new RubberLayout(200, 100));
			new SingleColorCanvas(composite, 0, 0, 255);
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.swtlib.util.test.wizard.ITestWizardItem#getDescription()
		 */
		@Override
		public String getDescription() {
			return "RUBBER ADAPTING: the background should fill the whole area";
		}

	}

}
