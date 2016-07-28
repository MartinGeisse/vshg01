/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.core.tlb;

import name.martingeisse.ecotools.common.util.HexNumberUtil;
import name.martingeisse.ecotools.simulator.cpu.IMemoryManagementUnit;
import name.martingeisse.ecotools.simulator.ui.core.cpu.CpuUserInterface;
import name.martingeisse.swtlib.layout.CenterLayout;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * This panel visualizes the TLB.
 */
public class TlbPanel extends Composite {

	/**
	 * the positioningComposite
	 */
	private Composite positioningComposite;

	/**
	 * the memoryManagementUnit
	 */
	private IMemoryManagementUnit memoryManagementUnit;

	/**
	 * the virtual address text fields
	 */
	private StyledText[] virtualAddressTextFields;

	/**
	 * the physical address text fields
	 */
	private StyledText[] physicalAddressTextFields;

	/**
	 * the writeFlagCheckboxes
	 */
	private Button[] writeFlagCheckboxes;

	/**
	 * the validFlagCheckboxes
	 */
	private Button[] validFlagCheckboxes;
	
	/**
	 * the updating
	 */
	private boolean updating;

	/**
	 * Constructor
	 * @param parent the parent composite
	 * @param memoryManagementUnit the memory management unit that contains the TLB to show 
	 * @param cpuUserInterface the CPU user interface implementation
	 */
	public TlbPanel(Composite parent, IMemoryManagementUnit memoryManagementUnit, CpuUserInterface cpuUserInterface) {
		super(parent, 0);
		setLayout(new CenterLayout(false, false));
		cpuUserInterface.setTlbPanel(this);
		this.memoryManagementUnit = memoryManagementUnit;

		positioningComposite = new Composite(this, 0);
		positioningComposite.setLayout(new GridLayout(11, false));

		virtualAddressTextFields = new StyledText[32];
		physicalAddressTextFields = new StyledText[32];
		writeFlagCheckboxes = new Button[32];
		validFlagCheckboxes = new Button[32];
		updating = false;

		addHeaderRow();
		for (int y = 0; y < 16; y++) {
			for (int x = 0; x < 2; x++) {

				/** add a center spacer just before the second column group begins **/
				if (x == 1) {
					addSpacer();
				}

				/** add components **/
				int index = x * 16 + y;
				addIndexLabel(index);
				MyListener listener = new MyListener(index);
				virtualAddressTextFields[index] = createTextField(listener);
				physicalAddressTextFields[index] = createTextField(listener);
				writeFlagCheckboxes[index] = createCheckbox(listener);
				validFlagCheckboxes[index] = createCheckbox(listener);

			}
		}

		updateAllEntries();

	}

	private void addHeaderRow() {
		addHeadersOnce();
		addSpacer();
		addHeadersOnce();
	}

	private void addHeadersOnce() {
		addSpacer();
		addHeader("virtual");
		addHeader("physical");
		addHeader("write");
		addHeader("valid");
	}

	private void addHeader(String text) {
		Label label = new Label(positioningComposite, SWT.CENTER);
		label.setText(text);
		label.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false, 1, 1));
	}

	private void addIndexLabel(int index) {
		Label label = new Label(positioningComposite, SWT.RIGHT);
		label.setText(Integer.toString(index));
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
	}

	private void addSpacer() {
		Label label = new Label(positioningComposite, SWT.CENTER);
		label.setText("");
		label.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false, 1, 1));
	}

	private StyledText createTextField(ModifyListener listener) {
		StyledText textField = new StyledText(positioningComposite, SWT.BORDER | SWT.LEFT);
		GridData layoutData = new GridData(SWT.CENTER, SWT.TOP, false, false, 1, 1);
		layoutData.widthHint = 80;
		textField.setLayoutData(layoutData);
		textField.addModifyListener(listener);
		return textField;
	}

	private Button createCheckbox(SelectionListener listener) {
		Button checkbox = new Button(positioningComposite, SWT.CHECK);
		checkbox.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false, 1, 1));
		checkbox.addSelectionListener(listener);
		return checkbox;
	}

	/**
	 * Updates the GUI components of a single TLB entry.
	 * @param index the index of the entry to update
	 */
	public void updateEntry(int index) {
		updating = true;
		int high = memoryManagementUnit.getTlbEntryHigh(index);
		int low = memoryManagementUnit.getTlbEntryLow(index);
		virtualAddressTextFields[index].setText(HexNumberUtil.unsignedWordToString(high));
		physicalAddressTextFields[index].setText(HexNumberUtil.unsignedWordToString(low & 0xfffff000));
		writeFlagCheckboxes[index].setSelection((low & IMemoryManagementUnit.TLB_ENTRY_WRITE_BIT) != 0);
		validFlagCheckboxes[index].setSelection((low & IMemoryManagementUnit.TLB_ENTRY_VALID_BIT) != 0);
		updating = false;
	}

	/**
	 * Updates the GUI components of all TLB entries at once.
	 */
	public void updateAllEntries() {
		for (int i = 0; i < 32; i++) {
			updateEntry(i);
		}
	}

	/**
	 * This method is invoked by the CPU user interface implementation when
	 * the TLB entries are modified.
	 */
	public void onWriteTlb() {
		updateAllEntries();
	}
	
	/**
	 * Reads values from the GUI controls for the specified TLB index and
	 * writes the values to the TLB itself.
	 * @param index the index of the entry to modify
	 */
	private void modifyEntry(int index) {
		
		/** ignore modify events caused by updating the GUI components **/
		if (updating) {
			return;
		}
		
		/** determine new virtual address **/
		int virtualAddress;
		try {
			virtualAddress = (int)Long.parseLong(virtualAddressTextFields[index].getText().trim(), 16);
		} catch (NumberFormatException e) {
			virtualAddress = memoryManagementUnit.getTlbEntryHigh(index);
		}
		virtualAddress &= 0xfffff000;
		
		/** determine new physical address **/
		int physicalAddress;
		try {
			physicalAddress = (int)Long.parseLong(physicalAddressTextFields[index].getText().trim(), 16);
		} catch (NumberFormatException e) {
			physicalAddress = memoryManagementUnit.getTlbEntryLow(index);
		}
		physicalAddress &= 0xfffff000;
		
		/** determine new write/valid flags **/
		boolean write = writeFlagCheckboxes[index].getSelection();
		boolean valid = validFlagCheckboxes[index].getSelection();
		
		/** compute high and low values **/
		int high = virtualAddress;
		int low = physicalAddress | (write ? IMemoryManagementUnit.TLB_ENTRY_WRITE_BIT : 0) | (valid ? IMemoryManagementUnit.TLB_ENTRY_VALID_BIT : 0);
		
		/** store the new value **/
		memoryManagementUnit.setTlbEntry(index, high, low, false);
		
	}

	/**
	 * This listener writes modified values from the GUI controls back
	 * to the TLB.
	 */
	private class MyListener implements ModifyListener, SelectionListener {

		/**
		 * the index
		 */
		private int index;
		
		/**
		 * Constructor
		 * @param index the TLB index for which this listener is used
		 */
		public MyListener(int index) {
			this.index = index;
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		@Override
		public void widgetDefaultSelected(SelectionEvent event) {
			modifyEntry(index);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		@Override
		public void widgetSelected(SelectionEvent event) {
			modifyEntry(index);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
		 */
		@Override
		public void modifyText(ModifyEvent event) {
			modifyEntry(index);
		}

	}

}
