/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.util.memory;

import java.util.Iterator;

/**
 * This interface allows to obtain labels within an address range.
 */
public interface IMemoryVisualizationLabelProvider {

	/**
	 * Returns an iterator that iterates over the labels whose addresses are
	 * in the interval from startAddress, inclusive, through endAddress, exclusive.
	 * The returned iterator returns labels in order of increasing addresses. Labels
	 * with the same address are returned in order of increasing name, as defined by
	 * the natural ordering of {@link String}. Labels with equal address and name
	 * are returned in an undefined order, and this order may change between different
	 * invocations of this method. This is the same order as defined by {@link LabelByAddressComparator}.
	 * 
	 * @param startAddress the minimal address (inclusive) for returned labels
	 * @param endAddress the maximal address (exclusive) for returned labels.
	 * @return Returns an iterator that returns the labels in the specified range in
	 * the order defined above.
	 */
	public Iterator<IMemoryVisualizationLabel> getLabels(int startAddress, int endAddress);
	
}
