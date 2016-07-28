/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.swtlib.basictest;

import name.martingeisse.swtlib.application.AbstractSingleWindowApplication;
import name.martingeisse.swtlib.util.ExchangeComposite;
import name.martingeisse.swtlib.util.test.SingleColorCanvas;

import org.eclipse.swt.layout.FillLayout;

/**
 * 
 */
public class ExchangedSingleColorApplication extends AbstractSingleWindowApplication {

	/**
	 * Constructor
	 */
	public ExchangedSingleColorApplication() {
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.swtlib.application.AbstractSingleWindowApplication#create()
	 */
	@Override
	public void create() {
		super.create();
		getShell().setSize(200, 200);
		getShell().setLayout(new FillLayout());
		ExchangeComposite exchangeComposite = new ExchangeComposite(getShell());
		new SingleColorCanvas(exchangeComposite, 255, 0, 0);
		exchangeComposite.clear();
		new SingleColorCanvas(exchangeComposite, 0, 255, 0);
	}

	/**
	 * Main method.
	 * @param args ...
	 */
	public static void main(String[] args) {
		ExchangedSingleColorApplication app = new ExchangedSingleColorApplication();
		app.create();
		app.open();
		app.mainLoop();
		app.dispose();
		app.exit();
	}

}
