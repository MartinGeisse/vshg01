/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.operations;

import name.martingeisse.ecotools.simulator.bus.Bus;
import name.martingeisse.ecotools.simulator.bus.BusAccessSize;
import name.martingeisse.ecotools.simulator.bus.BusErrorException;
import name.martingeisse.ecotools.simulator.ui.framework.EcoSimulatorFramework;
import name.martingeisse.ecotools.simulator.ui.util.memory.MemoryVisualizationException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

/**
 * 
 */
public class OperationsPanel extends Composite {

	/**
	 * the framework
	 */
	private EcoSimulatorFramework framework;
	
	/**
	 * Constructor
	 * @param parent the parent composite
	 * @param framework the simulator framework
	 */
	public OperationsPanel(Composite parent, final EcoSimulatorFramework framework) {
		super(parent, 0);
		GridLayout layout = new GridLayout(1, false);
		setLayout(layout);
		this.framework = framework;

		/** create read group **/
		Group readGroup = new Group(this, 0);
		readGroup.setLayout(new GridLayout(7, false));
		readGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

		/** "read from address" label **/
		addLabel(readGroup, "Read from address:", 130);

		/** read address text field **/
		Text readAddressTextField = addTextField(readGroup, false);

		/** read buttons **/
		Button ldwButton = addButton(readGroup, "LDW");
		Button ldhuButton = addButton(readGroup, "LDHU");
		Button ldbuButton = addButton(readGroup, "LDBU");

		/** "value" label **/
		addLabel(readGroup, "value:", SWT.DEFAULT);

		/** read value text field **/
		Text readValueTextField = addTextField(readGroup, true);

		/** create write group **/
		Group writeGroup = new Group(this, 0);
		writeGroup.setLayout(new GridLayout(7, false));
		writeGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));

		/** "write to address" label **/
		addLabel(writeGroup, "Write to address:", 130);

		/** write address text field **/
		Text writeAddressTextField = addTextField(writeGroup, false);

		/** "value" label **/
		addLabel(writeGroup, "value:", SWT.DEFAULT);

		/** write value text field **/
		Text writeValueTextField = addTextField(writeGroup, false);

		/** "Go" write button **/
		Button stwButton = addButton(writeGroup, "STW");
		Button sthButton = addButton(writeGroup, "STH");
		Button stbButton = addButton(writeGroup, "STB");

		/** add read functionality **/
		ldwButton.addSelectionListener(new BusAccessButtonListener(readAddressTextField, null, readValueTextField, BusAccessSize.WORD));
		ldhuButton.addSelectionListener(new BusAccessButtonListener(readAddressTextField, null, readValueTextField, BusAccessSize.HALFWORD));
		ldbuButton.addSelectionListener(new BusAccessButtonListener(readAddressTextField, null, readValueTextField, BusAccessSize.BYTE));
		
		/** add write functionality **/
		stwButton.addSelectionListener(new BusAccessButtonListener(writeAddressTextField, writeValueTextField, null, BusAccessSize.WORD));
		sthButton.addSelectionListener(new BusAccessButtonListener(writeAddressTextField, writeValueTextField, null, BusAccessSize.HALFWORD));
		stbButton.addSelectionListener(new BusAccessButtonListener(writeAddressTextField, writeValueTextField, null, BusAccessSize.BYTE));

	}

	/**
	 * @param parent
	 * @param text
	 * @param width
	 */
	private void addLabel(Composite parent, String text, int width) {
		Label label = new Label(parent, SWT.LEFT);
		label.setText(text);
		GridData layoutData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		layoutData.widthHint = width;
		label.setLayoutData(layoutData);
	}

	/**
	 * @param parent
	 * @return
	 */
	private Text addTextField(Composite parent, boolean readOnly) {
		int style = SWT.LEFT | SWT.BORDER;
		style = readOnly ? (style | SWT.READ_ONLY) : style;
		Text textField = new Text(parent, style);
		GridData layoutData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		layoutData.widthHint = 150;
		textField.setLayoutData(layoutData);
		return textField;
	}

	/**
	 * @param parent
	 * @param text
	 */
	private Button addButton(Composite parent, String text) {
		Button button = new Button(parent, SWT.PUSH | SWT.CENTER);
		button.setText(text);
		button.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		return button;
	}

	/**
	 * This class implements the common functionality for the "read" and "write" buttons.
	 */
	private class BusAccessButtonListener extends SelectionAdapter {

		/**
		 * the addressTextField
		 */
		private Text addressTextField;

		/**
		 * the writeValueTextField
		 */
		private Text writeValueTextField;

		/**
		 * the readValueTextField
		 */
		private Text readValueTextField;
		
		/**
		 * the accessSize
		 */
		private BusAccessSize accessSize;

		/**
		 * Constructor
		 * @param addressTextField
		 * @param writeValueTextField
		 * @param readValueTextField
		 * @param accessSize
		 */
		private BusAccessButtonListener(Text addressTextField, Text writeValueTextField, Text readValueTextField, BusAccessSize accessSize) {
			this.addressTextField = addressTextField;
			this.writeValueTextField = writeValueTextField;
			this.readValueTextField = readValueTextField;
			this.accessSize = accessSize;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		@Override
		public void widgetSelected(SelectionEvent event) {
			
			/** parse virtual address **/
			int virtualAddress;
			try {
				virtualAddress = (int)Long.parseLong(addressTextField.getText(), 16);
			} catch (NumberFormatException e) {
				error("Invalid address: " + addressTextField.getText());
				return;
			}

			/** parse write value (if write operation) **/
			int writeValue;
			if (writeValueTextField == null) {
				writeValue = 0;
			} else {
				try {
					writeValue = (int)Long.parseLong(writeValueTextField.getText(), 16);
				} catch (NumberFormatException e) {
					error("Invalid write value: " + writeValueTextField.getText());
					return;
				}
			}
			
			/** transform to physical address **/
			// TODO: that exception type is not nice here
			int physicalAddress;
			try {
				physicalAddress = framework.getCpu().getMemoryManagementUnit().mapAddressForVisualization(virtualAddress);
			} catch (MemoryVisualizationException e) {
				error("TLB miss");
				return;
			}
			
			/** perform the actual bus operation **/
			Bus bus = framework.getBus();
			try {
				if (writeValueTextField == null) {
					int readValue = bus.read(physicalAddress, accessSize) & accessSize.getValidBitMask();
					readValueTextField.setText(Integer.toHexString(readValue));
				} else {
					bus.write(physicalAddress, accessSize, writeValue);
					framework.getCpu().getUserInterface().onStore();
				}
			} catch (BusErrorException e) {
				error(e.getMessage());
				return;
			}
			
		}
		
		/**
		 * @param message
		 */
		private void error(String message) {
			MessageBox messageBox = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
			messageBox.setMessage(message);
			messageBox.open();
		}
		
	}

}
