/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.meltdown.engine;

import org.lwjgl.opengl.ARBVertexShader;

/**
 * Represents the code for a vertex shader.
 */
public class VertexShaderObject extends AbstractShaderObject {

	/**
	 * Constructor.
	 * @param code the shader code
	 */
	public VertexShaderObject(String code) {
		super(ARBVertexShader.GL_VERTEX_SHADER_ARB, code);
	}
	
}
