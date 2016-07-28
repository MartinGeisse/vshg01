/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.gltetris.game;

import name.martingeisse.gltetris.main.ShutdownException;

import org.lwjgl.input.Keyboard;

/**
 * This class handles keyboard controls.
 */
public class Controls {

	/**
	 * key cooldowns
	 */
	private static int leftCooldown, rightCooldown, downCooldown, clockwiseCooldown, counterClockwiseCooldown;

	/**
	 * Constructor.
	 */
	public Controls() {
	}
	
	/**
	 * Resets all cooldowns to zero.
	 */
	public static void resetCooldowns() {
		leftCooldown = rightCooldown = downCooldown = clockwiseCooldown = counterClockwiseCooldown = 0;
	}
	
	/**
	 * Tests for the LEFT key being pressed, and also handles cooldowns
	 * @return true iff the LEFT key is pressed
	 */
	public static boolean keyLeft() {
		boolean result = false;
		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
			if (leftCooldown == 0) {
				result = true;
				leftCooldown = 3;
			} else {
				leftCooldown--;
			}
		} else {
			leftCooldown = 0;
		}
		return result;
	}

	/**
	 * Tests for the RIGHT key being pressed, and also handles cooldowns
	 * @return true iff the RIGHT key is pressed
	 */
	public static boolean keyRight() {
		boolean result = false;
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
			if (rightCooldown == 0) {
				result = true;
				rightCooldown = 3;
			} else {
				rightCooldown--;
			}
		} else {
			rightCooldown = 0;
		}
		return result;
	}

	/**
	 * Tests for the DOWN key being pressed, and also handles cooldowns
	 * @return true iff the DOWN key is pressed
	 */
	public static boolean keyDown() {
		boolean result = false;
		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
			if (downCooldown == 0) {
				result = true;
				downCooldown = 1;
			} else {
				downCooldown--;
			}
		} else {
			downCooldown = 0;
		}
		return result;
	}

	/**
	 * Cooldown-independent way to obtain the state of the DOWN key.
	 * @return true iff the DOWN key is currently being held
	 */
	public static boolean keyDownHeld() {
		return Keyboard.isKeyDown(Keyboard.KEY_DOWN);
	}
	
	/**
	 * Tests for the ROTATE CLOCKWISE key being pressed, and also handles cooldowns
	 * @return true iff the ROTATE CLOCKWISE key is pressed
	 */
	public static boolean keyClockwise() {
		boolean result = false;
		if (Keyboard.isKeyDown(Keyboard.KEY_Y)) {
			if (clockwiseCooldown == 0) {
				result = true;
				clockwiseCooldown = 10;
			} else {
				clockwiseCooldown--;
			}
		} else {
			clockwiseCooldown = 0;
		}
		return result;
	}

	/**
	 * Tests for the ROTATE COUNTERCLOCKWISE key being pressed, and also handles cooldowns
	 * @return true iff the ROTATE COUNTERCLOCKWISE key is pressed
	 */
	public static boolean keyCounterClockwise() {
		boolean result = false;
		if (Keyboard.isKeyDown(Keyboard.KEY_X)) {
			if (counterClockwiseCooldown == 0) {
				result = true;
				counterClockwiseCooldown = 10;
			} else {
				counterClockwiseCooldown--;
			}
		} else {
			counterClockwiseCooldown = 0;
		}
		return result;
	}

	/**
	 * If the QUIT key is pressed, throws a {@link ShutdownException}.
	 */
	public static void checkKeyQuit() {
		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			throw new ShutdownException();
		}
	}
	
	/**
	 * Waits for the "any key" (enter) to be pressed.
	 * @return whether "any" key was pressed
	 */
	public static boolean checkForAnyKey() {
		Keyboard.poll();
		checkKeyQuit();
		return Keyboard.isKeyDown(Keyboard.KEY_RETURN);
	}

}
