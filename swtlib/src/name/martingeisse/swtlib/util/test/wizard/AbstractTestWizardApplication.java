/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.swtlib.util.test.wizard;

import name.martingeisse.swtlib.application.AbstractSingleWindowApplication;
import name.martingeisse.swtlib.util.ExchangeComposite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;

/**
 * This class implements an application type that shows a series
 * of GUI component tests to the user, each with a description
 * of how it should behave. The user can then interact with the
 * GUI component and see if it works correctly.
 * 
 * The wizard has no side effects. Users should take notes about
 * malfunctioning components. 
 */
public abstract class AbstractTestWizardApplication extends AbstractSingleWindowApplication {

	/**
	 * the items
	 */
	private ITestWizardItem[] items;

	/**
	 * the position
	 */
	private int position;

	/**
	 * the exchangeComposite
	 */
	private ExchangeComposite exchangeComposite;

	/**
	 * the descriptionLabel
	 */
	private Label descriptionLabel;

	/**
	 * Constructor
	 */
	public AbstractTestWizardApplication() {
	}

	/**
	 * @return Returns the wizard items.
	 */
	protected abstract ITestWizardItem[] createItems();

	/* (non-Javadoc)
	 * @see name.martingeisse.swtlib.application.AbstractSingleWindowApplication#create()
	 */
	@Override
	public void create() {
		super.create();

		getShell().setLayout(new GridLayout(2, true));
		exchangeComposite = new ExchangeComposite(getShell());
		exchangeComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		descriptionLabel = new Label(getShell(), SWT.LEFT);
		descriptionLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
		Button backButton = new Button(getShell(), SWT.PUSH | SWT.CENTER);
		backButton.setText("<< Back");
		backButton.addSelectionListener(new MyButtonListener(-1));
		backButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		Button nextButton = new Button(getShell(), SWT.PUSH | SWT.CENTER);
		nextButton.setText("Next >>");
		nextButton.addSelectionListener(new MyButtonListener(+1));
		nextButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		this.items = createItems();
		this.position = 0;
		showCurrent();

	}

	/**
	 * Shows the current item as determined by the position field.
	 */
	private void showCurrent() {
		exchangeComposite.clear();
		if (position < 0 || position >= items.length) {
			exchangeComposite.addPlaceholder();
			descriptionLabel.setText("Invalid item index: " + position + ", item count: " + items.length);
		} else {
			String description = items[position].getDescription();
			if (description == null) {
				description = "<no description for this item>";
			}
			items[position].createComponent(exchangeComposite);
			descriptionLabel.setText(description);
		}
		exchangeComposite.layout();
		getShell().layout();
	}

	/**
	 * Listener implementation for the "back" and "next" buttons
	 */
	private class MyButtonListener extends SelectionAdapter {

		/**
		 * the positionModifier
		 */
		private int positionModifier;

		/**
		 * Constructor
		 * @param positionModifier the modifier that is applied to the position by this button
		 */
		private MyButtonListener(int positionModifier) {
			this.positionModifier = positionModifier;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		@Override
		public void widgetSelected(SelectionEvent event) {
			position += positionModifier;
			showCurrent();
		}

	}

}
