/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.core.memory;

import java.util.Iterator;
import java.util.TreeSet;

import name.martingeisse.ecotools.simulator.bus.BusAccessSize;
import name.martingeisse.ecotools.simulator.ui.framework.memory.IMainMemoryVisualizationListenerRegistry;
import name.martingeisse.ecotools.simulator.ui.framework.memory.UpdateMemoryVisualizationPanelOnStoreListener;
import name.martingeisse.ecotools.simulator.ui.util.memory.IMemoryVisualizationLabel;
import name.martingeisse.ecotools.simulator.ui.util.memory.IMemoryVisualizationLabelProvider;
import name.martingeisse.ecotools.simulator.ui.util.memory.IMemoryVisualizationStorageStrategy;
import name.martingeisse.ecotools.simulator.ui.util.memory.LabelByAddressComparator;
import name.martingeisse.ecotools.simulator.ui.util.memory.MemoryVisualizationLabel;
import name.martingeisse.ecotools.simulator.ui.util.memory.MemoryVisualizationPanel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * This panel is used as the main control of a tab inside
 * the memory panel.
 */
public class MemoryTabPanel extends Composite {

	/**
	 * The base number of rows per block. A block is a unit of rows
	 * added or removed with the address range adjustment buttons.
	 * The actual number of rows per block is determined from a
	 * multiplier from the display mode.
	 */
	private static final int BASE_ROWS_PER_BLOCK = 20;

	/**
	 * The number of bytes displayed per row for display modes
	 * for which the row count multiplier is 1.
	 */
	private static final int BASE_BYTES_PER_ROW = 16;

	/**
	 * The number of bytes per block, which is intentionally the
	 * same for all display modes.
	 */
	private static final int BYTES_PER_BLOCK = BASE_ROWS_PER_BLOCK * BASE_BYTES_PER_ROW;

	/**
	 * the memoryVisualizationPanel
	 */
	private MemoryVisualizationPanel memoryVisualizationPanel;

	/**
	 * the blockCount
	 */
	private int blockCount;

	/**
	 * the display mode currently used by this panel
	 */
	private MemoryTabPanelDisplayMode displayMode;

	/**
	 * Constructor
	 * @param parent the parent composite
	 * @param storageStrategy the visualization storage strategy
	 * @param mainMemoryVisualizationListenerRegistry memory panels will register themselves with this
	 * listener to receive update notification when the main memory contents change.
	 */
	public MemoryTabPanel(Composite parent, IMemoryVisualizationStorageStrategy storageStrategy, IMainMemoryVisualizationListenerRegistry mainMemoryVisualizationListenerRegistry) {
		super(parent, 0);
		this.blockCount = 1;
		this.displayMode = MemoryTabPanelDisplayMode.BYTE;

		GridLayout layout = new GridLayout(9, false);
		setLayout(layout);

		memoryVisualizationPanel = new MemoryVisualizationPanel(this);
		memoryVisualizationPanel.setStorageStrategy(storageStrategy);
		memoryVisualizationPanel.setStartAddress(0x20000000);
		memoryVisualizationPanel.setAccessSize(BusAccessSize.HALFWORD);
		memoryVisualizationPanel.setUnitsPerRow(4);
		memoryVisualizationPanel.setRowCount(100);
		memoryVisualizationPanel.updateContents();
		memoryVisualizationPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 9, 1));
		
		/** TODO: test **/
		final TreeSet<IMemoryVisualizationLabel> labels = new TreeSet<IMemoryVisualizationLabel>(new LabelByAddressComparator());
		labels.add(new MemoryVisualizationLabel(0xe0000000, "start"));
		labels.add(new MemoryVisualizationLabel(0xe0000004, "exceptionHandler"));
		labels.add(new MemoryVisualizationLabel(0xe0000004, "interruptHandler"));
		labels.add(new MemoryVisualizationLabel(0xe0000008, "userTlbMissHandler"));
		labels.add(new MemoryVisualizationLabel(0xe0000020, "foo"));
		labels.add(new MemoryVisualizationLabel(0xe0000021, "bar"));
		labels.add(new MemoryVisualizationLabel(0xe0000022, "flubb"));
		labels.add(new MemoryVisualizationLabel(0xe0000023, "barr"));
		labels.add(new MemoryVisualizationLabel(0xe0000024, "fuppi"));
		memoryVisualizationPanel.setLabelProvider(new IMemoryVisualizationLabelProvider() {
			
			@Override
			public Iterator<IMemoryVisualizationLabel> getLabels(int startAddress, int endAddress) {
				IMemoryVisualizationLabel from = new MemoryVisualizationLabel(startAddress, "");
				IMemoryVisualizationLabel to = new MemoryVisualizationLabel(endAddress, "");
				return labels.subSet(from, to).iterator();
			}
			
		});
		/** TODO: test **/

		createButton(SWT.PUSH | SWT.CENTER, "< +", new AddressRangeAdjustmentListener(-1, +1), new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		createButton(SWT.PUSH | SWT.CENTER, "- >", new AddressRangeAdjustmentListener(+1, -1), new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		createDisplayModeButton("W", MemoryTabPanelDisplayMode.WORD);
		createDisplayModeButton("H", MemoryTabPanelDisplayMode.HALFWORD);
		Button defaultModeButton = createDisplayModeButton("B", MemoryTabPanelDisplayMode.BYTE);
		createDisplayModeButton("I", MemoryTabPanelDisplayMode.INSTRUCTION);
		createDisplayModeButton("A", MemoryTabPanelDisplayMode.ASCII);
		createButton(SWT.PUSH | SWT.CENTER, "< -", new AddressRangeAdjustmentListener(0, -1), new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		createButton(SWT.PUSH | SWT.CENTER, "+ >", new AddressRangeAdjustmentListener(0, +1), new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

		defaultModeButton.setSelection(true);
		makeDisplayModeActive();
		
		mainMemoryVisualizationListenerRegistry.registerListener(new UpdateMemoryVisualizationPanelOnStoreListener(memoryVisualizationPanel));
	}

	/**
	 * Helper method to create a button in the bottom button bar.
	 * @param text the button text
	 * @param listener the listener that reacts to the button
	 * @param layoutData the layout data for the button
	 * @return Returns the created button.
	 */
	private Button createDisplayModeButton(String text, MemoryTabPanelDisplayMode displayMode) {
		SelectionListener listener = new DisplayModeButtonListener(displayMode);
		GridData layoutData = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		return createButton(SWT.RADIO, text, listener, layoutData);
	}

	/**
	 * Helper method to create a button in the bottom button bar.
	 * @param text the button text
	 * @param listener the listener that reacts to the button
	 * @param layoutData the layout data for the button
	 * @return Returns the created button.
	 */
	private Button createButton(int style, String text, SelectionListener listener, GridData layoutData) {
		Button button = new Button(this, style);
		button.setText(text);
		button.addSelectionListener(listener);
		button.setLayoutData(layoutData);
		return button;
	}

	/**
	 * @return Returns the memoryVisualizationPanel.
	 */
	public MemoryVisualizationPanel getMemoryVisualizationPanel() {
		return memoryVisualizationPanel;
	}

	/**
	 * Enters the properties of the current display mode into the visualization panel.
	 */
	private void makeDisplayModeActive() {
		memoryVisualizationPanel.setAccessSize(displayMode.getBusAccessSize());
		memoryVisualizationPanel.setUnitsPerRow(displayMode.getUnitsPerRow());
		memoryVisualizationPanel.setRowCount(blockCount * BASE_ROWS_PER_BLOCK * displayMode.getRowCountMultiplier());
		memoryVisualizationPanel.setTextConversionStrategy(displayMode.getTextConversionStrategy());
		memoryVisualizationPanel.updateContents();
	}

	/**
	 * This listener grows or shrinks the displayed address range at the beginning or
	 * at the end. The effect of the button is expressed in two numbers: The start
	 * modifier specifies the number of blocks by which the start address is moved.
	 * The length modifier specifies the number of blocks to add or subtract to
	 * the displayed block count.
	 */
	private class AddressRangeAdjustmentListener extends SelectionAdapter {

		/**
		 * the startModifier
		 */
		private int startModifier;

		/**
		 * the lengthModifier
		 */
		private int lengthModifier;

		/**
		 * Constructor
		 * @param startModifier
		 * @param lengthModifier
		 */
		private AddressRangeAdjustmentListener(int startModifier, int lengthModifier) {
			super();
			this.startModifier = startModifier;
			this.lengthModifier = lengthModifier;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		@Override
		public void widgetSelected(SelectionEvent event) {
			if (blockCount + lengthModifier > 0) {
				memoryVisualizationPanel.setStartAddress(memoryVisualizationPanel.getStartAddress() + startModifier * BYTES_PER_BLOCK);
				blockCount += lengthModifier;
			}
			makeDisplayModeActive();
		}

	}

	/**
	 * This listener is used for the unit selection buttons (W, H, B, I, A)
	 * at the bottom button bar.
	 */
	private class DisplayModeButtonListener extends SelectionAdapter {

		/**
		 * the displayModeToSet
		 */
		private MemoryTabPanelDisplayMode displayModeToSet;

		/**
		 * Constructor
		 * @param displayModeToSet
		 */
		private DisplayModeButtonListener(MemoryTabPanelDisplayMode displayModeToSet) {
			this.displayModeToSet = displayModeToSet;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		@Override
		public void widgetSelected(SelectionEvent event) {
			displayMode = displayModeToSet;
			makeDisplayModeActive();
		}

	}

}
