/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.util.memory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import name.martingeisse.ecotools.common.util.HexNumberUtil;
import name.martingeisse.ecotools.simulator.bus.BusAccessSize;
import name.martingeisse.swtlib.font.Fonts;
import name.martingeisse.swtlib.layout.RubberLayout;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;

/**
 * This component provides a configurable base implementation of a panel
 * that displays memory contents. This implementation can be configured
 * in the following ways:
 * 
 * - storage strategy: This strategy implements a way to load and store
 *   units of varying size from and to the actual memory implementation.
 *   The size specification uses a {@link BusAccessSize} value.
 *   
 * - text conversion strategy: This strategy implements a way to convert
 *   memory units to and from a textual representation.
 *   
 * - coloring strategy: This strategy determines the color of the textual
 *   representation based on the actual value.
 *   
 * - label provider: This strategy is able to provide textual labels for the
 *   visualized addresses.
 *   
 * Each strategy can give information about its supported value unit sizes.
 * The visualization panel can use a specific size only if all strategies
 * support it.
 * 
 */
public class MemoryVisualizationPanel extends Composite {

	/**
	 * the storageStrategy
	 */
	private IMemoryVisualizationStorageStrategy storageStrategy;

	/**
	 * the textConversionStrategy
	 */
	private IMemoryVisualizationTextConversionStrategy textConversionStrategy;

	/**
	 * the coloringStrategy
	 */
	private IMemoryVisualizationColoringStrategy coloringStrategy;

	/**
	 * the labelProvider
	 */
	private IMemoryVisualizationLabelProvider labelProvider;

	/**
	 * the textView
	 */
	private StyledText textView;

	/**
	 * the startAddress
	 */
	private int startAddress;

	/**
	 * the unitsPerRow
	 */
	private int unitsPerRow;

	/**
	 * the rowCount
	 */
	private int rowCount;

	/**
	 * the accessSize
	 */
	private BusAccessSize accessSize;

	/**
	 * Constructor. This constructor configures the text conversion strategy to be
	 * {@link DefaultMemoryVisualizationTextConversionStrategy} (hexdecimal
	 * numbers) and the coloring strategy to be {@link BlackTextMemoryVisualizationColoringStrategy}
	 * (all-black text). The storage strategy is left unconfigured and must
	 * be set before this panel can be used. The start address is set to 0, the
	 * number of units per row to 1, and the number of rows to 20 units. Again,
	 * the unit size must be set before use.
	 * @param parent the parent composite
	 */
	public MemoryVisualizationPanel(Composite parent) {
		super(parent, 0);
		this.storageStrategy = null;
		this.textConversionStrategy = DefaultMemoryVisualizationTextConversionStrategy.getInstance();
		this.coloringStrategy = BlackTextMemoryVisualizationColoringStrategy.getInstance();
		this.textView = new StyledText(this, SWT.LEFT | SWT.READ_ONLY | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		this.textView.setFont(Fonts.getCourier12());
		this.startAddress = 0;
		this.unitsPerRow = 1;
		this.rowCount = 20;
		this.accessSize = null;
		setLayout(new RubberLayout());
	}

	/**
	 * @return Returns the storageStrategy.
	 */
	public IMemoryVisualizationStorageStrategy getStorageStrategy() {
		return storageStrategy;
	}

	/**
	 * Sets the storageStrategy.
	 * @param storageStrategy the new value to set
	 */
	public void setStorageStrategy(IMemoryVisualizationStorageStrategy storageStrategy) {
		this.storageStrategy = storageStrategy;
	}

	/**
	 * @return Returns the textConversionStrategy.
	 */
	public IMemoryVisualizationTextConversionStrategy getTextConversionStrategy() {
		return textConversionStrategy;
	}

	/**
	 * Sets the textConversionStrategy.
	 * @param textConversionStrategy the new value to set
	 */
	public void setTextConversionStrategy(IMemoryVisualizationTextConversionStrategy textConversionStrategy) {
		this.textConversionStrategy = textConversionStrategy;
	}

	/**
	 * @return Returns the coloringStrategy.
	 */
	public IMemoryVisualizationColoringStrategy getColoringStrategy() {
		return coloringStrategy;
	}

	/**
	 * Sets the coloringStrategy.
	 * @param coloringStrategy the new value to set
	 */
	public void setColoringStrategy(IMemoryVisualizationColoringStrategy coloringStrategy) {
		this.coloringStrategy = coloringStrategy;
	}

	/**
	 * @return Returns the labelProvider.
	 */
	public IMemoryVisualizationLabelProvider getLabelProvider() {
		return labelProvider;
	}

	/**
	 * Sets the labelProvider.
	 * @param labelProvider the new value to set
	 */
	public void setLabelProvider(IMemoryVisualizationLabelProvider labelProvider) {
		this.labelProvider = labelProvider;
	}

	/**
	 * @return Returns the startAddress.
	 */
	public int getStartAddress() {
		return startAddress;
	}

	/**
	 * Sets the startAddress.
	 * @param startAddress the new value to set
	 */
	public void setStartAddress(int startAddress) {
		this.startAddress = startAddress;
	}

	/**
	 * @return Returns the unitsPerRow.
	 */
	public int getUnitsPerRow() {
		return unitsPerRow;
	}

	/**
	 * Sets the unitsPerRow.
	 * @param unitsPerRow the new value to set
	 */
	public void setUnitsPerRow(int unitsPerRow) {
		this.unitsPerRow = unitsPerRow;
	}

	/**
	 * @return Returns the rowCount.
	 */
	public int getRowCount() {
		return rowCount;
	}

	/**
	 * Sets the rowCount.
	 * @param rowCount the new value to set
	 */
	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	/**
	 * @return Returns the accessSize.
	 */
	public BusAccessSize getAccessSize() {
		return accessSize;
	}

	/**
	 * Sets the accessSize.
	 * @param accessSize the new value to set
	 */
	public void setAccessSize(BusAccessSize accessSize) {
		this.accessSize = accessSize;
	}

	/**
	 * Refreshes the contents of this panel from the memory using the strategies
	 * currently set.
	 */
	public void updateContents() {

		/** we must collect style ranges in a list and set them at the end, because we only set the text at the end of this method **/
		List<StyleRange> styleRanges = new ArrayList<StyleRange>();
		
		/** obtain labels for the visualized address range **/
		int byteCount = rowCount * unitsPerRow * accessSize.getByteCount();
		Iterator<IMemoryVisualizationLabel> labels;
		IMemoryVisualizationLabel nextLabel;
		if (labelProvider == null) {
			labels = null;
			nextLabel = null;
		} else {
			labels = labelProvider.getLabels(startAddress, startAddress + byteCount);
			if (labels.hasNext()) {
				nextLabel = labels.next();
				if (nextLabel.getAddress() < startAddress) {
					throw new RuntimeException("Label provider returned labels before the specified start address");
				}
			} else {
				nextLabel = null;
			}
		}

		/** generate the text **/
		int address = startAddress;
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < rowCount; i++) {

			/** print the labels (if any) for the current row **/
			int rowEndAddress = address + unitsPerRow * accessSize.getByteCount();
			while (nextLabel != null && nextLabel.getAddress() < rowEndAddress) {
				
				/** determine the address offset, and print it if nonzero **/
				int addressOffset = nextLabel.getAddress() - address;
				if (addressOffset != 0) {
					builder.append('+').append(addressOffset).append(' ');
				}
				
				/** actually print the label **/
				builder.append(nextLabel.getName()).append(':').append('\n');
					
				/** fetch the next one to see if it also applies to the current address, and check address ordering **/
				IMemoryVisualizationLabel newNextLabel = labels.hasNext() ? labels.next() : null;
				if (newNextLabel != null && newNextLabel.getAddress() < nextLabel.getAddress()) {
					throw new RuntimeException("Label provider returned labels in wrong order");
				}
				nextLabel = newNextLabel;
					
			}
			
			builder.append("  ");
			builder.append(HexNumberUtil.unsignedWordToString(address));
			builder.append("   ");

			for (int j = 0; j < unitsPerRow; j++) {

				builder.append(' ');

				StyleRange styleRange = new StyleRange();
				styleRange.start = builder.length();

				String valueText;
				try {
					int value = storageStrategy.read(address, accessSize);
					valueText = textConversionStrategy.valueToText(value, accessSize);
					coloringStrategy.configureStyleRange(address, value, accessSize, styleRange);
				} catch (MemoryVisualizationException e) {
					valueText = "(" + e.getMessage() + ")";
					styleRange = null;
				}

				builder.append(valueText);

				if (styleRange != null) {
					styleRange.length = builder.length() - styleRange.start;
					styleRanges.add(styleRange);
				}

				address += accessSize.getByteCount();
			}

			builder.append('\n');

		}

		/** store it in the view **/
		textView.setText(builder.toString());

		/** store style ranges **/
		textView.setStyleRanges(styleRanges.toArray(new StyleRange[styleRanges.size()]));

	}

}
