/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.swtlib.application;

import name.martingeisse.swtlib.color.Colors;
import name.martingeisse.swtlib.color.PaleColors;
import name.martingeisse.swtlib.font.Fonts;

import org.eclipse.swt.widgets.Display;

/**
 * This class manages a global display.
 */
public abstract class AbstractSingleDisplayApplication {

	/**
	 * the display
	 */
	private Display display;
	
	/**
	 * the open
	 */
	boolean open;

	/**
	 * Constructor
	 */
	public AbstractSingleDisplayApplication() {
		this.display = null;
		this.open = false;
	}

	/**
	 * @return Returns the display.
	 */
	public final Display getDisplay() {
		return display;
	}

	/**
	 * Creates the GUI components for this application.
	 */
	public void create() {
		ensureGuiNotCreated();
		display = new Display();
		Colors.initialize(display);
		PaleColors.initialize(display);
		Fonts.initialize(display);
	}
	
	/**
	 * Ensures that the GUI components have been created, and throws and exception
	 * if that is not the case.
	 */
	public void ensureGuiCreated() {
		if (display == null) {
			throw new IllegalStateException("GUI has not been created yet");
		}
	}

	/**
	 * Ensures that the GUI components have not been created yet,
	 * and throws and exception if they have been created.
	 */
	public void ensureGuiNotCreated() {
		if (display != null) {
			throw new IllegalStateException("GUI has already been created");
		}
	}

	/**
	 * Opens the GUI for this application.
	 */
	public void open() {
		ensureGuiCreated();
		ensureGuiNotOpen();
		open = true;
	}

	/**
	 * Ensures that the GUI has been opened, and throws and exception
	 * if that is not the case.
	 */
	public void ensureGuiOpen() {
		if (!open) {
			throw new IllegalStateException("GUI has not been opened yet");
		}
	}

	/**
	 * Ensures that the GUI has been opened, and throws and exception
	 * if it hasn't.
	 */
	public void ensureGuiNotOpen() {
		if (open) {
			throw new IllegalStateException("GUI has already been opened");
		}
	}


	/**
	 * Runs the main event loop. This method only returns
	 * when checkExitCondition() returns true.
	 */
	public final void mainLoop() {

		/** sanity check **/
		ensureGuiOpen();

		/** main loop **/
		while (!isExitCondition()) {
			handleAllEvents();
			idle();
		}

	}
	
	/**
	 * @return Returns true to exit the main loop, false to stay.
	 */
	protected abstract boolean isExitCondition();
	
	/**
	 * Processes all prending GUI events.
	 */
	public final void handleAllEvents() {
		while (!display.isDisposed() && display.readAndDispatch()) {}
	}

	/**
	 * This method is invoked when no GUI events are pending.
	 * The default implementation invokes sleep() to wait for
	 * GUI events.
	 */
	protected void idle() {
		sleep();
	}
	
	/**
	 * This method blocks until GUI events are available.
	 */
	public final void sleep() {
		if (!display.isDisposed()) {
			display.sleep();
		}
	}
	
	/**
	 * Disposes of this application and all GUI resources.
	 */
	public void dispose() {
		PaleColors.dispose();
		Colors.dispose();
		if (display != null) {
			display.dispose();
			display = null;
		}
		open = false;
	}
	
	/**
	 * Exits the application. This is needed to clean up OpenGL resources.
	 */
	public void exit() {
		System.exit(0);
	}

}
