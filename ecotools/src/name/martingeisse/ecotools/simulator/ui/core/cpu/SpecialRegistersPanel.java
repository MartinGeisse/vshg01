/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.core.cpu;

import name.martingeisse.ecotools.simulator.cpu.ISpecialRegisterFile;
import name.martingeisse.ecotools.simulator.cpu.ProgramCounter;
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
public class SpecialRegistersPanel extends Composite {

	/**
	 * The label texts, i.e. the names of the registers.
	 */
	private static final String[] LABELS = {
		"PSW",
		"TLB Index",
		"TLB Entry High",
		"TLB Entry Low",
		"TLB Bad Address"
	};
	
	/**
	 * the registerFile
	 */
	private ISpecialRegisterFile registerFile;
	
	/**
	 * the programCounter
	 */
	private ProgramCounter programCounter;
	
	/**
	 * the textFields. Implementation note: Setting the background color works
	 * with normal Text widgets too, but at least on the Mac looks ugly.
	 */
	private StyledText[] textFields;

	/**
	 * the text field for the program counter. This is handled specially
	 * because the PC is not stored in the special register file.
	 */
	private StyledText pcTextField;
	
	/**
	 * the updating
	 */
	private boolean updating;

	/**
	 * Constructor
	 * @param parent the parent composite
	 * @param registerFile the register file that backs this panel
	 * @param programCounter the program counter register that backs this panel
	 */
	public SpecialRegistersPanel(Composite parent, final ISpecialRegisterFile registerFile, ProgramCounter programCounter) {
		super(parent, 0);
		this.registerFile = registerFile;
		this.programCounter = programCounter;
		this.textFields = new StyledText[5];
		setLayout(new FillLayout());
		Group group = new Group(this, 0);
		GridLayout groupLayout = new GridLayout(4, false);
		group.setLayout(groupLayout);
		updating = false;
		
		for (int i=0; i<5; i++) {
			final int index = i;
			final StyledText textField = createRegisterComponents(group, LABELS[i] + " (" + i + ")");
			textFields[i] = textField;

			textField.addModifyListener(new ModifyListener() {
				
				@Override
				public void modifyText(ModifyEvent event) {

					/** ignore events caused by updating the GUI components **/
					if (updating) {
						return;
					}
					
					/** write the modified register value back into the register file **/
					try {
						registerFile.write(index, (int)Long.parseLong(textField.getText(), 16), false);
					} catch (NumberFormatException e) {
					}
					
				}
				
			});

		}
		
		pcTextField = createRegisterComponents(group, "PC");
		pcTextField.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent event) {
				try {
					SpecialRegistersPanel.this.programCounter.setValue((int)Long.parseLong(pcTextField.getText(), 16), false);
				} catch (NumberFormatException e) {
				}
			}
			
		});

		updateAllRegisters();
	}
	
	private StyledText createRegisterComponents(Composite parent, String labelText) {
		
		Label label = new Label(parent, SWT.RIGHT);
		label.setText(labelText);
		GridData labelLayoutData = new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1);
//		labelLayoutData.widthHint = 100;
		label.setLayoutData(labelLayoutData);
		
		StyledText text = new StyledText(parent, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		text.setText("-");
		GridData textLayoutData = new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1);
		textLayoutData.widthHint = 80;
		text.setLayoutData(textLayoutData);
		return text;
		
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
	 * Updates the PC register with its current value from the CPU.
	 */
	public void updatePcRegister() {
		updating = true;
		pcTextField.setText(Integer.toHexString(programCounter.getValue()));
		updating = false;
	}

	/**
	 * Updates all registers with their current values from the CPU.
	 */
	public void updateAllRegisters() {
		for (int i=0; i<5; i++) {
			updateRegister(i);
		}
		updatePcRegister();
	}
	
	/**
	 * Notifies this panel about a register that was read by the CPU.
	 * @param index the index of the register that was read
	 */
	public void notifyReadRegister(int index) {
		textFields[index].setBackground(PaleColors.getGreen());
	}

	/**
	 * Notifies this panel about the PC register being read by the CPU.
	 */
	public void notifyReadPcRegister() {
		pcTextField.setBackground(PaleColors.getGreen());
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
	 * Notifies this panel about the PC register being written by the CPU.
	 */
	public void notifyWritePcRegister() {
		pcTextField.setBackground(PaleColors.getRed());
		updatePcRegister();
	}

	/**
	 * Resets all register background colors to white.
	 */
	public void clearColors() {
		for (StyledText textField : textFields) {
			textField.setBackground(Colors.getWhite());
		}
		pcTextField.setBackground(Colors.getWhite());
	}

}
