/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.swtlib.application;

import org.eclipse.swt.widgets.Shell;

/**
 * This class further specializes {@link AbstractSingleDisplayApplication}
 * to handle only a single window (shell) for the application.
 */
public class AbstractSingleWindowApplication extends AbstractSingleDisplayApplication {

	/**
	 * the shell
	 */
	private Shell shell;

	/**
	 * Constructor
	 */
	public AbstractSingleWindowApplication() {
	}

	/**
	 * @return Returns the shell.
	 */
	public Shell getShell() {
		return shell;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.swtlib.application.AbstractSingleDisplayApplication#create()
	 */
	@Override
	public void create() {
		super.create();
		shell = new Shell(getDisplay());
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.swtlib.application.AbstractSingleDisplayApplication#open()
	 */
	@Override
	public void open() {
		super.open();
		shell.open();
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.swtlib.application.AbstractSingleDisplayApplication#isExitCondition()
	 */
	@Override
	protected boolean isExitCondition() {
		return shell.isDisposed();
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.swtlib.application.AbstractSingleDisplayApplication#dispose()
	 */
	@Override
	public void dispose() {
		shell.dispose();
		super.dispose();
	}

}
