/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.webeco;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import name.martingeisse.webeco.model.ISimulatorAction;
import name.martingeisse.webeco.wicket.CharacterDisplayPatch;

/**
 * This class contains thread-safe queues for all GUI widgets in the simulator. It acts
 * as the central hub for passing messages between the different GUI threads and the
 * simulation thread.
 * 
 * This class itself does not pass messages to any thread. Rather, it receives and
 * stores messages from threads and allows other threads to de-queue them in a
 * "dead drop" fashion.
 */
public final class GuiMessageHub {

	/**
	 * the characterDisplayQueue
	 */
	private final Queue<CharacterDisplayPatch> characterDisplayQueue;

	/**
	 * the terminalInputQueue
	 */
	private final Queue<String> terminalInputQueue;

	/**
	 * the terminalOutputQueue
	 */
	private final Queue<String> terminalOutputQueue;

	/**
	 * the debugOutputQueue
	 */
	private final Queue<String> debugOutputQueue;

	/**
	 * the actions
	 */
	private final Queue<ISimulatorAction> actions;

	/**
	 * Constructor.
	 */
	public GuiMessageHub() {
		this.characterDisplayQueue = new ConcurrentLinkedQueue<CharacterDisplayPatch>();
		this.terminalInputQueue = new ConcurrentLinkedQueue<String>();
		this.terminalOutputQueue = new ConcurrentLinkedQueue<String>();
		this.debugOutputQueue = new ConcurrentLinkedQueue<String>();
		this.actions = new ConcurrentLinkedQueue<ISimulatorAction>();
	}

	/**
	 * Getter method for the characterDisplayQueue.
	 * @return the characterDisplayQueue
	 */
	public Queue<CharacterDisplayPatch> getCharacterDisplayQueue() {
		return characterDisplayQueue;
	}

	/**
	 * Getter method for the terminalInputQueue.
	 * @return the terminalInputQueue
	 */
	public Queue<String> getTerminalInputQueue() {
		return terminalInputQueue;
	}

	/**
	 * Getter method for the terminalOutputQueue.
	 * @return the terminalOutputQueue
	 */
	public Queue<String> getTerminalOutputQueue() {
		return terminalOutputQueue;
	}

	/**
	 * Getter method for the debugOutputQueue.
	 * @return the debugOutputQueue
	 */
	public Queue<String> getDebugOutputQueue() {
		return debugOutputQueue;
	}

	/**
	 * Getter method for the actions.
	 * @return the actions
	 */
	public Queue<ISimulatorAction> getActions() {
		return actions;
	}

}
