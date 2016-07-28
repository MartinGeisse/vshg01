/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.swtlib.color;

import org.lwjgl.opengl.GL11;

/**
 * This class wraps OpenGL color constants. The constants are
 * only exposed as a useColor() method.
 */
public class OpenGlColors {

	/**
	 * Uses the color with the specified index as the current OpenGL
	 * color via glColor3*(). 
	 * @param index the index of the color to use
	 */
	public static void useColor(int index) {
		index = index & 15;
		if (index == 7) {
			GL11.glColor3f(0.75f, 0.75f, 0.75f);
		} else if (index == 8) {
			GL11.glColor3f(0.5f, 0.5f, 0.5f);
		} else {
			float baseValue = (index > 7) ? 1.0f : 0.5f;
			float red = ((index & 4) == 0) ? 0.0f : baseValue;
			float green = ((index & 2) == 0) ? 0.0f : baseValue;
			float blue = ((index & 1) == 0) ? 0.0f : baseValue;
			GL11.glColor3f(red, green, blue);
		}
	}

}
