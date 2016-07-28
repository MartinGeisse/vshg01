/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.gltetris.main;

import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import name.martingeisse.gltetris.game.Controls;
import name.martingeisse.gltetris.game.FrameTimer;
import name.martingeisse.gltetris.game.Game;
import name.martingeisse.gltetris.game.IFrameHandler;
import name.martingeisse.gltetris.game.Piece;
import name.martingeisse.gltetris.game.Resources;
import name.martingeisse.gltetris.game.Shape;
import name.martingeisse.gltetris.game.TitleScreen;
import name.martingeisse.meltdown.engine.Engine;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

/**
 * Test Main.
 */
public class Main {

	/**
	 * the currentFrameHandler
	 */
	public static IFrameHandler currentFrameHandler;
	
	/**
	 * the engine
	 */
	public static Engine engine;
	
	/**
	 * the titleScreen
	 */
	public static TitleScreen titleScreen;
	
	/**
	 * the game
	 */
	public static Game game;

	/**
	 * the frameTimer
	 */
	public static FrameTimer frameTimer;
	
	/**
	 * the frameCounter
	 */
	public static int frameCounter;
	
	/**
	 * @param argv ...
	 * @throws Exception ...
	 */
	public static void main(final String[] argv) throws Exception {
		try {

			// initialize constants
			Piece.initialize();
			Shape.initialize();
			
			// initialize LWJGL
			Display.setDisplayMode(new DisplayMode(640, 480));
			Display.create();
//			System.out.println(Display.getAdapter());
//			System.out.println(Display.getVersion());
//			System.out.println(GL11.glGetString(GL11.GL_VENDOR));
//			System.out.println(GL11.glGetString(GL11.GL_RENDERER));
//			System.out.println(GL11.glGetString(GL11.GL_VERSION));
			engine = new Engine();
			Resources.load(engine);
			
			// initialize OpenGL
			glMatrixMode(GL_PROJECTION);
			glLoadIdentity();
			glOrtho(0, 640, 480, 0, 1, -1);
			glMatrixMode(GL_MODELVIEW);

			// initialize the game itself
			currentFrameHandler = titleScreen = new TitleScreen();
			game = new Game(engine);
			frameTimer = new FrameTimer(30);
			frameCounter = 0;
			
			// main loop
			while (true) {
				nextFrame();
				currentFrameHandler.handleFrame(frameCounter);
			}
			
		} catch (final ShutdownException e) {
			
			// normal exit
			System.exit(0);
			
		} catch (final Exception e) {
			
			// error exit
			e.printStackTrace();
			System.exit(0);
			
		}

	}
	
	/**
	 * Starts a new frame by waiting until the frame starts. This method does not invoke the frame handler.
	 */
	public static void nextFrame() {

		// wait until the end of the frame
		while (!frameTimer.test()) {
			synchronized(frameTimer) {
				try {
					frameTimer.wait();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}
		
		frameCounter++;
		Display.update();
		Controls.checkKeyQuit();
		engine.advanceSoundChain();
		
	}

}
