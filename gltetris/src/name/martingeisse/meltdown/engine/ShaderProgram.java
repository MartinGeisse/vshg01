/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.meltdown.engine;

import org.lwjgl.opengl.ARBShaderObjects;

/**
 * Represents a shader program (i.e. a pair of vertex shader and fragment shader).
 */
public class ShaderProgram {

	/**
	 * the vertexShader
	 */
	private final VertexShaderObject vertexShader;

	/**
	 * the fragmentShader
	 */
	private final FragmentShaderObject fragmentShader;
	
	/**
	 * the handle
	 */
	private final int handle;

	/**
	 * Constructor.
	 * @param vertexShader the vertex shader
	 * @param fragmentShader the fragment shader
	 */
	public ShaderProgram(final VertexShaderObject vertexShader, final FragmentShaderObject fragmentShader) {
		this.vertexShader = vertexShader;
		this.fragmentShader = fragmentShader;
		this.handle = ARBShaderObjects.glCreateProgramObjectARB();
		ARBShaderObjects.glAttachObjectARB(handle, vertexShader.getHandle());
		ARBShaderObjects.glAttachObjectARB(handle, fragmentShader.getHandle());
		ARBShaderObjects.glLinkProgramARB(handle);
//		ARBShaderObjects.glValidateProgramARB(handle);
//		System.out.println("### " + ARBShaderObjects.glGetObjectParameteriARB(handle, ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB));
	}

	/**
	 * Getter method for the vertexShader.
	 * @return the vertexShader
	 */
	public VertexShaderObject getVertexShader() {
		return vertexShader;
	}

	/**
	 * Getter method for the fragmentShader.
	 * @return the fragmentShader
	 */
	public FragmentShaderObject getFragmentShader() {
		return fragmentShader;
	}
	
	/**
	 * 
	 */
	public void use() {
		ARBShaderObjects.glUseProgramObjectARB(handle);		
	}
	
	/**
	 * 
	 */
	public static void useNone() {
		ARBShaderObjects.glUseProgramObjectARB(0);		
	}

}
