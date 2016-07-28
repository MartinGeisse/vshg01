/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecogui.prototype.region;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * This class collects regions and precomputes the values needed to draw them.
 */
public class RegionContainer {

	/**
	 * the regions
	 */
	private List<RegionPrototypeRegion> regions;

	/**
	 * the horizontalMap
	 */
	private Entry[] horizontalMap;

	/**
	 * the verticalMap
	 */
	private Entry[] verticalMap;
	
	/**
	 * the horizontalIndex
	 */
	private int horizontalIndex;
	
	/**
	 * the remainingHorizontalDistance
	 */
	private int remainingHorizontalDistance;
	
	/**
	 * the verticalIndex
	 */
	private int verticalIndex;
	
	/**
	 * the remainingVerticalDistance
	 */
	private int remainingVerticalDistance;

	/**
	 * Constructor.
	 */
	public RegionContainer() {
	}

	/**
	 * Getter method for the regions.
	 * @return the regions
	 */
	public List<RegionPrototypeRegion> getRegions() {
		return regions;
	}

	/**
	 * Setter method for the regions.
	 * @param regions the regions to set
	 */
	public void setRegions(final List<RegionPrototypeRegion> regions) {
		this.regions = regions;
	}
	
	/**
	 * Precomputes the values needed to draw regions. This method must be called once before
	 * the usual sequence of startFrame(), startLine(), startPixel(), getCurrentMask() works.
	 */
	public void prepare() {
		
		// not a fundamental restriction, but our mask type is (32-bit) int
		if (regions.size() > 32) {
			throw new IllegalStateException();
		}
		
		// prepare vertical / horinzontal maps individually
		horizontalMap = prepareDimension(true);
		verticalMap = prepareDimension(false);
		
		System.out.println("horizontal:");
		for (Entry e : horizontalMap) {
			System.out.println("- " + e.distance + ", " + e.mask);
		}
		System.out.println("vertical:");
		for (Entry e : verticalMap) {
			System.out.println("- " + e.distance + ", " + e.mask);
		}
	}
	
	/**
	 * This function is used twice, once for the horizontal map and once for the vertical map.
	 */
	private Entry[] prepareDimension(boolean horizontal) {
		int screenSize = (horizontal ? 200 : 150);
		Entry[] entries = createEntryArray(horizontal);
		Arrays.sort(entries, new EntryDistanceComparator());
		prepareEntryFields(entries, screenSize);
		return purgeZeroDistanceEntries(entries);
	}
	
	/**
	 * Creates initial entries for one dimension. The result contains one entry for the start
	 * of the container area, and two entries for each region (denoting start and end). Each
	 * entry has its position on the container area in the distance field. The start entry has
	 * a mask of 0, and each start/end entry has a mask with only the big for that region set.
	 */
	private Entry[] createEntryArray(boolean horizontal) {
		
		Entry[] result = new Entry[2 * regions.size() + 1];
		
		// screen start
		result[0] = new Entry();
		result[0].distance = 0;
		result[0].mask = 0;
			
		int i = 0;
		for (RegionPrototypeRegion region : regions) {
			result[2*i+1] = new Entry();
			result[2*i+1].distance = (horizontal ? region.getX() : region.getY());
			result[2*i+1].mask = (1 << i);
			result[2*i+2] = new Entry();
			result[2*i+2].distance = result[2*i+1].distance + (horizontal ? region.getWidth() : region.getHeight());
			result[2*i+2].mask = (1 << i);
			i++;
		}
		
		return result;
	}
	
	/**
	 * Loops through the specified entries. Each distance field is replaced by the difference
	 * to the next distance field (or to the specified screen size for the last entry), and
	 * each mask is replaced by the XOR-accumulation of masks.
	 */
	private void prepareEntryFields(Entry[] entries, int screenSize) {
		int mask = 0;
		for (int i=0; i<entries.length; i++) {
			entries[i].distance = ((i == entries.length - 1) ? screenSize : entries[i + 1].distance) - entries[i].distance - 1;
			entries[i].mask = mask = (mask ^ entries[i].mask);
		}
	}
	
	/**
	 * Returns an array with the entries from the argument, with all entries removed that
	 * have a distance of less than zero. This corresponds to keeping only those entries
	 * with a positive position difference since the stored distance value is one less
	 * than the position difference (to exploit the carry chain).
	 */
	private Entry[] purgeZeroDistanceEntries(Entry[] entries) {
		int outputCount = 0;
		for (Entry e : entries) {
			if (e.distance >= 0) {
				outputCount++;
			}
		}
		Entry[] result = new Entry[outputCount];
		int i = 0;
		for (Entry e : entries) {
			if (e.distance >= 0) {
				result[i] = e;
				i++;
			}
		}
		return result;
	}
	
	/**
	 * This method begins the next frame.
	 */
	public void startFrame() {
		verticalIndex = -1;
		remainingVerticalDistance = 0;
	}
	
	/**
	 * This method begins the next line.
	 */
	public void startLine() {
		remainingVerticalDistance--;
		if (remainingVerticalDistance < 0) {
			verticalIndex++;
			remainingVerticalDistance = verticalMap[verticalIndex].distance;
		}
		horizontalIndex = -1;
		remainingHorizontalDistance = 0;
	}
	
	/**
	 * This method begins the next pixel.
	 */
	public void startPixel() {
		remainingHorizontalDistance--;
		if (remainingHorizontalDistance < 0) {
			horizontalIndex++;
			remainingHorizontalDistance = horizontalMap[horizontalIndex].distance;
		}
	}
	
	/**
	 * @return the mask for the current pixel
	 */
	public int getCurrentMask() {
		return (verticalMap[verticalIndex].mask & horizontalMap[horizontalIndex].mask);
	}
	
	/**
	 * @return the index of the currently visible region
	 */
	public int getCurrentVisibleRegionIndex() {
		int mask = getCurrentMask();
		for (int i=0; i<32; i++) {
			if ((mask & (1 << i)) != 0) {
				return i;
			}
		}
		return -1;
	}

	/**
	 *
	 */
	private static class Entry {
		private int distance;
		private int mask;
	}
	
	/**
	 * Compares entries by distance field.
	 */
	private static class EntryDistanceComparator implements Comparator<Entry> {
		@Override
		public int compare(Entry e1, Entry e2) {
			return (e1.distance - e2.distance);
		}
	}

}
