/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.meltdown.engine;

/**
 * This enumeration is used to translate the x coordinate of an object that is anchored at a specified
 * point, but the point could specify the left, center, or right x position of the object. The translation
 * ensures that the resulting x coordinate is always the left x position, since that is needed for drawing.
 */
public enum HorizontalAlignment {

	/**
	 * the LEFT
	 */
	LEFT {

		/* (non-Javadoc)
		 * @see name.martingeisse.meltdown.engine.HorizontalAlignment#getTranslatedX(int, int)
		 */
		@Override
		public int getTranslatedX(int originalX, int width) {
			return originalX;
		}
		
	},
	
	/**
	 * the CENTER
	 */
	CENTER {

		/* (non-Javadoc)
		 * @see name.martingeisse.meltdown.engine.HorizontalAlignment#getTranslatedX(int, int)
		 */
		@Override
		public int getTranslatedX(int originalX, int width) {
			return originalX - (width >> 1);
		}
		
	},
	
	/**
	 * the RIGHT
	 */
	RIGHT {

		/* (non-Javadoc)
		 * @see name.martingeisse.meltdown.engine.HorizontalAlignment#getTranslatedX(int, int)
		 */
		@Override
		public int getTranslatedX(int originalX, int width) {
			return originalX - width;
		}
		
	};
	
	/**
	 * Translates an X coordinate according to this alignment such that the resulting coordinate
	 * specifies the upper left corner of the object to align.
	 * @param originalX the original X coordinate to align to
	 * @param width the width of the object to align
	 * @return the translated X coordinate
	 */
	public abstract int getTranslatedX(int originalX, int width);
	
}
