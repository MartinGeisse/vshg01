/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.meltdown.engine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.Util;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

/**
 * This class represents the run-time data of the technical engine behind the game.
 * 
 * Animation states are counted in steps of 1/256 seconds.
 */
public class Engine {

	/**
	 * the currentAnimationState
	 */
	private int currentAnimationState;

	/**
	 * the soundBuffer
	 */
	private ByteBuffer soundBuffer;
	
	/**
	 * the soundBufferId
	 */
	private int soundBufferId;
	
	/**
	 * the soundSourceId
	 */
	private int soundSourceId;
	
	/**
	 * the currentSoundChain
	 */
	private SoundChain currentSoundChain;
	
	/**
	 * the currentSoundChainElement
	 */
	private int currentSoundChainElement;
	
	/**
	 * the finishedSoundChainElementDuration
	 */
	private int finishedSoundChainElementDuration;

	/**
	 * Constructor.
	 * @throws LWJGLException on LWJGL problems
	 */
	public Engine() throws LWJGLException {
		currentAnimationState = 0;
		initializeSound();
	}

	/**
	 * 
	 */
	private void initializeSound() throws LWJGLException {
		
		// the samples we're gonna play. Note that OpenAL chokes on explicitly set frequencies < 500 Hz
		soundBuffer = ByteBuffer.allocateDirect(128);
		for (int i=0; i<128; i++) {
			soundBuffer.put(i, (byte)(2*i));
		}
		
		// initialize OpenAL
		AL.create();
		Util.checkALError();
		
		// create a sound buffer
		soundBufferId = AL10.alGenBuffers();
		Util.checkALError();
		
		// create a sound source
		soundSourceId = AL10.alGenSources();
		Util.checkALError();

		// fill the buffer with our data
		AL10.alBufferData(soundBufferId, AL10.AL_FORMAT_MONO8, soundBuffer, 40000);
		Util.checkALError();

		// link the source to the buffer
		AL10.alSourcei(soundSourceId, AL10.AL_BUFFER, soundBufferId);
		Util.checkALError();
		
		// set the source to looping mode
		AL10.alSourcei(soundSourceId, AL10.AL_LOOPING, 1);
		Util.checkALError();

	}
	
	/**
	 * @param frequency the frequency
	 */
	public void playSound(int frequency) {

		// set pitch (frequencyof the rectangle wave)
		AL10.alSourcef(soundSourceId, AL10.AL_PITCH, frequency / 500.0f);
		Util.checkALError();
		
		// enable playback
		AL10.alSourcePlay(soundSourceId);
		Util.checkALError();
		
	}
	
	/**
	 * 
	 */
	public void stopSound() {
		
		AL10.alSourceStop(soundSourceId);
		Util.checkALError();
		
	}
	
	/**
	 * @param soundChain the sound chain to play
	 */
	public void startSoundChain(SoundChain soundChain) {
		this.currentSoundChain = soundChain;
		this.currentSoundChainElement = 0;
		this.finishedSoundChainElementDuration = 0;
		playCurrentSoundChainElement();
	}
	
	/**
	 * 
	 */
	public void advanceSoundChain() {
		if (currentSoundChain != null) {
			if (currentSoundChainElement < currentSoundChain.getElementCount()) {
				finishedSoundChainElementDuration++;
				if (finishedSoundChainElementDuration >= currentSoundChain.getElement(currentSoundChainElement).getDuration()) {
					finishedSoundChainElementDuration = 0;
					currentSoundChainElement++;
					if (currentSoundChainElement >= currentSoundChain.getElementCount()) {
						currentSoundChain = null;
					} else {
						playCurrentSoundChainElement();
					}
				}
			} else {
				currentSoundChain = null;
				stopSound();
			}
		} else {
			stopSound();
		}
	}
	
	/**
	 * 
	 */
	private void playCurrentSoundChainElement() {
		if (currentSoundChain != null && currentSoundChainElement < currentSoundChain.getElementCount()) {
			playSound(currentSoundChain.getElement(currentSoundChainElement).getFrequency());
		} else {
			stopSound();
		}
	}
	
	/**
	 * Getter method for the currentAnimationState.
	 * @return the currentAnimationState
	 */
	public int getCurrentAnimationState() {
		return currentAnimationState;
	}

	/**
	 * Setter method for the currentAnimationState.
	 * @param currentAnimationState the currentAnimationState to set
	 */
	public void setCurrentAnimationState(final int currentAnimationState) {
		this.currentAnimationState = currentAnimationState;
	}

	/**
	 * Advances all animations.
	 * @param stateSteps the state steps (1/256th seconds) to advance
	 */
	public void advanceAnimation(final int stateSteps) {
		this.currentAnimationState += stateSteps;
	}

	/**
	 * Returns the current frame for an animation with the specified parameters.
	 * @param startState the engine's animation state at the time the animation was created.
	 * @param stepsPerFrame the number of state steps per animation frame
	 * @return the frame number to display for the animation
	 */
	public int getAnimationFrame(final int startState, final int stepsPerFrame) {
		return (currentAnimationState - startState) / stepsPerFrame;
	}

	/**
	 * @param filename the filename of the PNG, relative to the "data/gfx" folder
	 * @return the texture
	 * @throws IOException on I/O errors
	 */
	public Texture loadPngTexture(final String filename) throws IOException {
		return TextureLoader.getTexture("PNG", new FileInputStream("data/gfx/" + filename));
	}

	/**
	 * @param filename the filename of the PNG, relative to the "data/gfx" folder
	 * @return the image
	 * @throws IOException on I/O errors
	 */
	public Image loadPngImage(final String filename) throws IOException {
		return new Image(loadPngTexture(filename));
	}
	
	/**
	 * @param filenamePattern the filename pattern of the frame PNGs, using $ as the frame counting
	 * variable and relative to the "data/gfx" folder.
	 * @param animationStepsPerFrame the number of animation state steps per animation frame
	 * @return the animation
	 * @throws IOException on I/O errors
	 */
	public Animation loadPngAnimation(final String filenamePattern, final int animationStepsPerFrame) throws IOException {
		List<Image> frames = new ArrayList<Image>();
		int i = 0;
		while (true) {
			Image frame;
			try {
				frame = loadPngImage(filenamePattern.replace("$", "" + i));
			} catch (FileNotFoundException e) {
				break;
			}
			frames.add(frame);
			i++;
		}
		if (frames.isEmpty()) {
			throw new IOException("frame 0 not found for animation file pattern: " + filenamePattern);
		}
		Image[] frameArray = frames.toArray(new Image[frames.size()]);
		return new Animation(this, frameArray, animationStepsPerFrame);
	}
	
}
