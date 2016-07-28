/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.core.cpu;

import java.util.Map;

import name.martingeisse.ecotools.common.util.ArrayUtil;
import name.martingeisse.ecotools.common.util.HexNumberUtil;
import name.martingeisse.ecotools.common.util.IntArrayTraversal;
import name.martingeisse.ecotools.common.util.IntPredicates;
import name.martingeisse.ecotools.simulator.ui.framework.BreakpointSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

/**
 * 
 */
public class BreakpointsPanel extends Composite {

	/**
	 * the breakpointSet
	 */
	private BreakpointSet breakpointSet;

	/**
	 * the addBreakpointTextField
	 */
	private Text addBreakpointTextField;

	/**
	 * the breakpointListView
	 */
	private List breakpointListView;
	
	/**
	 * the values of the currently selected breakpoints in the same order
	 * as in the list view, since we can't obtain them from the list view
	 */
	private int[] currentlyDisplayedBreakpoints;

	/**
	 * Constructor
	 * @param parent the parent composite
	 * @param breakpointSet the breakpoint set file that backs this panel
	 */
	public BreakpointsPanel(Composite parent, BreakpointSet breakpointSet) {
		super(parent, 0);
		this.breakpointSet = breakpointSet;
		setLayout(new FillLayout());
		Group group = new Group(this, 0);
		GridLayout groupLayout = new GridLayout(5, false);
		group.setLayout(groupLayout);

		Label title = new Label(group, SWT.LEFT);
		title.setText("Breakpoints:");
		title.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

		addBreakpointTextField = new Text(group, SWT.LEFT | SWT.BORDER);
		addBreakpointTextField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		addBreakpointTextField.addKeyListener(new AddBreakpointKeyListener());

		Button addBreakpointButton = new Button(group, SWT.PUSH | SWT.CENTER);
		addBreakpointButton.setText("+");
		addBreakpointButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		addBreakpointButton.addSelectionListener(new AddBreakpointButtonListener());

		Button removeBreakpointButton = new Button(group, SWT.PUSH | SWT.CENTER);
		removeBreakpointButton.setText("-");
		removeBreakpointButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		removeBreakpointButton.addSelectionListener(new RemoveBreakpointButtonListener());

		Button toggleBreakpointButton = new Button(group, SWT.PUSH | SWT.CENTER);
		toggleBreakpointButton.setText("A");
		toggleBreakpointButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		toggleBreakpointButton.addSelectionListener(new ToggleBreakpointsButtonListener());

		breakpointListView = new List(group, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		breakpointListView.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 5, 1));
		breakpointListView.addSelectionListener(new ToggleBreakpointsListViewListener());
		
		updateFromBreakpointSet();
	}

	/**
	 * 
	 */
	public void updateFromBreakpointSet() {
		
		/** remember the selected breakpoints by value to re-select them later **/
		int[] oldSelectedBreakpoints;
		if (currentlyDisplayedBreakpoints == null) {
			oldSelectedBreakpoints = new int[0];
		} else {
			oldSelectedBreakpoints = ArrayUtil.selectByIndexArray(currentlyDisplayedBreakpoints, breakpointListView.getSelectionIndices());
		}
		
		/** clear the list view **/
		breakpointListView.removeAll();

		/**
		 * Re-build the list view, and remember the breakpoints including their order
		 * so we can interpret selection indices from the list view correctly. Also
		 * determine the new selection indices, which may be a subset of the selected
		 * breakpoints in case some were deleted. Note that selected breakpoints
		 * as well as breakpoint set entries are ordered by ascending address.
		 */
		IntArrayTraversal oldSelectedBreakpointsTraversal = new IntArrayTraversal(oldSelectedBreakpoints);
		IntArrayTraversal newSelectionIndexTraversal = new IntArrayTraversal(oldSelectedBreakpoints.length);
		IntArrayTraversal newBreakpointsTraversal = new IntArrayTraversal(breakpointSet.size());
		for (Map.Entry<Integer, Boolean> breakpointEntry : breakpointSet.entrySet()) {
			int breakpoint = breakpointEntry.getKey();
			
			/** build a list item for this breakpoint **/
			String text = HexNumberUtil.unsignedWordToString(breakpoint);
			if (!breakpointEntry.getValue()) {
				text += " (inactive)";
			}
			breakpointListView.add(text);
			
			/** skip old selected breakpoints that cannot occur anymore **/
			oldSelectedBreakpointsTraversal.skip(new IntPredicates.Less(breakpoint));
			
			/** check if this breakpoint is part of the new selection **/
			if (!oldSelectedBreakpointsTraversal.isAtEnd() && oldSelectedBreakpointsTraversal.readDontMove() == breakpoint) {
				oldSelectedBreakpointsTraversal.read();
				newSelectionIndexTraversal.write(newBreakpointsTraversal.getCursor());
			}
			
			/** remember the breakpoint, because we can't get the integer value from the list item **/
			newBreakpointsTraversal.write(breakpoint);
			
		}
		
		/** remember the new breakpoints **/
		currentlyDisplayedBreakpoints = newBreakpointsTraversal.getArray();
		
		/** set the new selection **/
		breakpointListView.setSelection(newSelectionIndexTraversal.getSubArrayBeforeCursor());
		
	}
	
	/**
	 * Adds a breakpoint as specified in the text field.
	 */
	private void addBreakpoint() {

		/** parse the contents of the address text field **/
		int address;
		try {
			address = (int)Long.parseLong(addBreakpointTextField.getText(), 16);
		} catch (NumberFormatException e) {
			MessageBox messageBox = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
			messageBox.setMessage("Invalid breakpoint address: " + addBreakpointTextField.getText());
			messageBox.open();
			return;
		}
		
		/** add the breakpoint **/
		breakpointSet.put(address, true);
		addBreakpointTextField.setText("");
		updateFromBreakpointSet();

	}
	
	/**
	 * Removes the selected breakpoints from the breakpoint set.
	 */
	private void removeBreakpoints() {
		for (int selectionIndex : breakpointListView.getSelectionIndices()) {
			int breakpoint = currentlyDisplayedBreakpoints[selectionIndex];
			breakpointSet.remove(breakpoint);
		}
		updateFromBreakpointSet();
	}
	
	/**
	 * Toggles the active-ness of the currently selected breakpoints.
	 */
	private void toggleBreakpoints() {
		for (int selectionIndex : breakpointListView.getSelectionIndices()) {
			int breakpoint = currentlyDisplayedBreakpoints[selectionIndex];
			boolean active = breakpointSet.get(breakpoint);
			breakpointSet.put(breakpoint, !active);
		}
		updateFromBreakpointSet();
	}

	private class AddBreakpointButtonListener extends SelectionAdapter {

		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		@Override
		public void widgetSelected(SelectionEvent event) {
			addBreakpoint();
		}

	}

	private class RemoveBreakpointButtonListener extends SelectionAdapter {

		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		@Override
		public void widgetSelected(SelectionEvent event) {
			removeBreakpoints();
		}

	}

	private class ToggleBreakpointsButtonListener extends SelectionAdapter {

		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		@Override
		public void widgetSelected(SelectionEvent event) {
			toggleBreakpoints();
		}

	}

	private class ToggleBreakpointsListViewListener extends SelectionAdapter {

		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.SelectionAdapter#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		@Override
		public void widgetDefaultSelected(SelectionEvent event) {
			toggleBreakpoints();
		}

	}

	private class AddBreakpointKeyListener extends KeyAdapter {

		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.KeyListener#keyPressed(org.eclipse.swt.events.KeyEvent)
		 */
		@Override
		public void keyPressed(KeyEvent event) {
			if (event.character == SWT.CR || event.character == SWT.LF) {
				addBreakpoint();
			}
		}
		
	}
	

}
