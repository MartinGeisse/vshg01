/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.meltdown.engine;

/**
 * An animation that consists of multiple images and an animation speed.
 */
public final class Animation extends EngineBoundObject implements IDrawable {

	/**
	 * the frames
	 */
	private final Image[] frames;

	/**
	 * the animationStepsPerFrame
	 */
	private final int animationStepsPerFrame;

	/**
	 * Constructor.
	 */
	Animation(final Engine engine, final Image[] frames, final int animationStepsPerFrame) {
		super(engine);
		this.frames = frames;
		this.animationStepsPerFrame = animationStepsPerFrame;
	}

	/**
	 * Getter method for the frames.
	 * @return the frames
	 */
	public Image[] getFrames() {
		return frames;
	}

	/**
	 * Getter method for the animationStepsPerFrame.
	 * @return the animationStepsPerFrame
	 */
	public int getAnimationStepsPerFrame() {
		return animationStepsPerFrame;
	}

	/**
	 * @param startState the start state when the animation instance was created
	 * @return the current frame to display
	 */
	public Image getCurrentFrame(int startState) {
		return frames[getEngine().getAnimationFrame(startState, animationStepsPerFrame) % frames.length];
	}
	
	/* (non-Javadoc)
	 * @see name.martingeisse.meltdown.engine.IDrawable#draw(int, int, int)
	 */
	@Override
	public void draw(final int x, final int y, final int startState) {
		getCurrentFrame(startState).draw(x, y);
	}

	/**
	 * Draws this image to the specified screen position.
	 * @param x the x position to draw to
	 * @param y the y position to draw to
	 * @param horizontalAlignment the horizonal alignment
	 * @param verticalAlignment the horizonal alignment
	 * @param startState the start state when the animation instance was created
	 */
	public void draw(final int x, final int y, final HorizontalAlignment horizontalAlignment, final VerticalAlignment verticalAlignment, int startState) {
		getCurrentFrame(startState).draw(x, y, horizontalAlignment, verticalAlignment);
	}
	
}
