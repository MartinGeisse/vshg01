/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.meltdown.engine;

/**
 * A sound chain is a sequence of sound chain elements, each representing a sound frequency
 * (to generate a sawtooth wave) and a duration measured in game frames.
 */
public class SoundChain {

	/**
	 * the elements
	 */
	private Element[] elements;
	
	/**
	 * Constructor.
	 * @param elementData an alternating sequence of frequency and duration values
	 */
	public SoundChain(int... elementData) {
		if (elementData.length % 2 != 0) {
			throw new IllegalArgumentException("elementData has an odd number of elements");
		}
		elements = new Element[elementData.length / 2];
		for (int i=0, j=0; i<elementData.length; i += 2, j++) {
			elements[j] = new Element(elementData[i], elementData[i + 1]);
		}
	}
	
	/**
	 * @return the number of elements in this chain
	 */
	public int getElementCount() {
		return elements.length;
	}
	
	/**
	 * @param index the element index
	 * @return the element with the specified index
	 */
	public Element getElement(int index) {
		return elements[index];
	}
	
	/**
	 * Represents a single element of a sound chain.
	 */
	public static final class Element {

		/**
		 * the frequency
		 */
		private final int frequency;

		/**
		 * the duration
		 */
		private final int duration;

		/**
		 * Constructor.
		 * @param frequency the frequency
		 * @param duration the duration
		 */
		public Element(final int frequency, final int duration) {
			this.frequency = frequency;
			this.duration = duration;
		}

		/**
		 * Getter method for the frequency.
		 * @return the frequency
		 */
		public int getFrequency() {
			return frequency;
		}

		/**
		 * Getter method for the duration.
		 * @return the duration
		 */
		public int getDuration() {
			return duration;
		}

	}
}
