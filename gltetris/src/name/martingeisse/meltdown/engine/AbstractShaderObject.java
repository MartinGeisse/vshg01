/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.meltdown.engine;

import org.lwjgl.opengl.ARBShaderObjects;

/**
 * Represents a shader object (i.e. compiled shader code unit).
 */
public abstract class AbstractShaderObject {

	/**
	 * the handle
	 */
	private final int handle;

	/**
	 * Constructor.
	 * @param shaderCode the shader code to compile
	 */
	AbstractShaderObject(final int type, final String shaderCode) {
		handle = ARBShaderObjects.glCreateShaderObjectARB(type);
		ARBShaderObjects.glShaderSourceARB(handle, shaderCode);
		ARBShaderObjects.glCompileShaderARB(handle);
//		System.out.println("* " + ARBShaderObjects.glGetInfoLogARB(handle, 1000));
	}

	/**
	 * Getter method for the handle.
	 * @return the handle
	 */
	public int getHandle() {
		return handle;
	}

}
