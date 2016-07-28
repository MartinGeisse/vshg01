/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.core.cpu;

import name.martingeisse.ecotools.simulator.cpu.IGeneralRegisterFile;
import name.martingeisse.swtlib.color.Colors;
import name.martingeisse.swtlib.color.PaleColors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

/**
 * 
 */
public class GeneralRegistersPanel extends Composite {

	/**
	 * the registerFile
	 */
	private IGeneralRegisterFile registerFile;
	
	/**
	 * the textFields. Implementation note: Setting the background color works
	 * with normal Text widgets too, but at least on the Mac looks ugly.
	 */
	private StyledText[] textFields;
	
	/**
	 * the updating
	 */
	private boolean updating;
	
	/**
	 * Constructor
	 * @param parent the parent composite
	 * @param registerFile the register file that backs this panel
	 */
	public GeneralRegistersPanel(Composite parent, final IGeneralRegisterFile registerFile) {
		super(parent, 0);
		this.registerFile = registerFile;
		this.textFields = new StyledText[32];
		setLayout(new FillLayout());
		Group group = new Group(this, 0);
		GridLayout groupLayout = new GridLayout(8, false);
		group.setLayout(groupLayout);
		updating = false;
		
		for (int y = 0; y < 8; y++) {
			for (int x = 0; x < 4; x++) {
				final int index = x*8 + y;
				
				Label label = new Label(group, SWT.LEFT);
				label.setText(Integer.toString(index));
				
				GridData labelLayoutData = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
				labelLayoutData.widthHint = 20;
				label.setLayoutData(labelLayoutData);
				
				final StyledText text = new StyledText(group, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
				text.setText("-");
				GridData textLayoutData = new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1);
				textLayoutData.widthHint = 80;
				text.setLayoutData(textLayoutData);
				textFields[index] = text;
				
				text.addModifyListener(new ModifyListener() {
					
					@Override
					public void modifyText(ModifyEvent event) {
						
						/** ignore events caused by updating the GUI components **/
						if (updating) {
							return;
						}
						
						/** write the modified register value back into the register file **/
						try {
							registerFile.write(index, (int)Long.parseLong(text.getText(), 16), false);
						} catch (NumberFormatException e) {
						}
					}
					
				});
				
			}
		}
		
		updateAllRegisters();
	}
	
	/**
	 * Updates a single register with its current value from the CPU.
	 * @param index the index of the register to update.
	 */
	public void updateRegister(int index) {
		updating = true;
		textFields[index].setText(Integer.toHexString(registerFile.read(index, false)));
		updating = false;
	}
	
	/**
	 * Updates all registers with their current values from the CPU.
	 */
	public void updateAllRegisters() {
		for (int i=0; i<32; i++) {
			updateRegister(i);
		}
	}
	
	/**
	 * Notifies this panel about a register that was read by the CPU.
	 * @param index the index of the register that was read
	 */
	public void notifyReadRegister(int index) {
		textFields[index].setBackground(PaleColors.getGreen());
	}

	/**
	 * Notifies this panel about a register that was written by the CPU.
	 * @param index the index of the register that was written
	 */
	public void notifyWriteRegister(int index) {
		textFields[index].setBackground(PaleColors.getRed());
		updateRegister(index);
	}
	
	/**
	 * Resets all register background colors to white.
	 */
	public void clearColors() {
		for (StyledText textField : textFields) {
			textField.setBackground(Colors.getWhite());
		}
	}

}
