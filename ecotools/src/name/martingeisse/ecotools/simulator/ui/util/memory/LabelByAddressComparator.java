/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.util.memory;

import java.util.Comparator;

/**
 * This comparator type orders labels by their address. Labels with
 * the same address are ordered by name, as defined by the natural
 * ordersing of {@link String}. Labels with equal address and name
 * are considered equal with respect to the ordering defined by
 * this comparator.
 */
public class LabelByAddressComparator implements Comparator<IMemoryVisualizationLabel> {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(IMemoryVisualizationLabel o1, IMemoryVisualizationLabel o2) {
		int addressOrder = o1.getAddress() - o2.getAddress();
		if (addressOrder != 0) {
			return addressOrder;
		} else {
			return o1.getName().compareTo(o2.getName());
		}
	}

}
