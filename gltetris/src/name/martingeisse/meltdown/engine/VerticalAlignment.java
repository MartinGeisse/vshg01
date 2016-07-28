/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.meltdown.engine;

/**
 * This enumeration is used to translate the y coordinate of an object that is anchored at a specified
 * point, but the point could specify the top, center, or bottom y position of the object. The translation
 * ensures that the resulting y coordinate is always the top y position, since that is needed for drawing.
 */
public enum VerticalAlignment {

	/**
	 * the TOP
	 */
	TOP {

		/* (non-Javadoc)
		 * @see name.martingeisse.meltdown.engine.HorizontalAlignment#getTranslatedY(int, int)
		 */
		@Override
		public int getTranslatedY(int originalY, int height) {
			return originalY;
		}
		
	},
	
	/**
	 * the CENTER
	 */
	CENTER {

		/* (non-Javadoc)
		 * @see name.martingeisse.meltdown.engine.HorizontalAlignment#getTranslatedY(int, int)
		 */
		@Override
		public int getTranslatedY(int originalY, int height) {
			return originalY - (height >> 1);
		}
		
	},
	
	/**
	 * the BOTTOM
	 */
	BOTTOM {

		/* (non-Javadoc)
		 * @see name.martingeisse.meltdown.engine.HorizontalAlignment#getTranslatedY(int, int)
		 */
		@Override
		public int getTranslatedY(int originalY, int height) {
			return originalY - height;
		}
		
	};
	
	/**
	 * Translates an Y coordinate according to this alignment such that the resulting coordinate
	 * specifies the upper left corner of the object to align.
	 * @param originalY the original Y coordinate to align to
	 * @param height the height of the object to align
	 * @return the translated Y coordinate
	 */
	public abstract int getTranslatedY(int originalY, int height);
	
}
