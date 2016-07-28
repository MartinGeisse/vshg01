/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.swtlib.util.test.wizard;

import org.eclipse.swt.widgets.Composite;

/**
 * This item must be implemented by wizard-guided test applications
 * to perform the actual tests.
 */
public interface ITestWizardItem {

	/**
	 * @return Returns the description for this step. This description
	 * should explain to the user how the control should behave.
	 */
	public String getDescription();
	
	/**
	 * Creates the component under test.
	 * @param parent the parent composite.
	 */
	public void createComponent(Composite parent);
	
}
