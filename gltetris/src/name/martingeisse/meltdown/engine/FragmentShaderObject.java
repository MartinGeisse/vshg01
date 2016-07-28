/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.meltdown.engine;

import org.lwjgl.opengl.ARBFragmentShader;

/**
 * Represents the code for a fragment shader.
 */
public class FragmentShaderObject extends AbstractShaderObject {

	/**
	 * Constructor.
	 * @param code the shader code
	 */
	public FragmentShaderObject(String code) {
		super(ARBFragmentShader.GL_FRAGMENT_SHADER_ARB, code);
	}
	
}
