/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.util.keyboard;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;

/**
 * Keyboard map that maps SWT key codes for a German Apple host keyboard
 * to a simulated German Windows keyboard. Generated scan codes are
 * AT (set 2) codes.
 */
public class GermanAppleToGermanWindowsKeyboardMap implements IKeyboardMap {

	/**
	 * the instance
	 */
	private static final GermanAppleToGermanWindowsKeyboardMap instance = new GermanAppleToGermanWindowsKeyboardMap();

	/**
	 * @return Returns the instance.
	 */
	public static GermanAppleToGermanWindowsKeyboardMap getInstance() {
		return instance;
	}

	/**
	 * the makeMappings
	 */
	private Map<Integer, int[]> makeMappings;

	/**
	 * the breakMappings
	 */
	private Map<Integer, int[]> breakMappings;

	/**
	 * Constructor
	 */
	private GermanAppleToGermanWindowsKeyboardMap() {
		this.makeMappings = new HashMap<Integer, int[]>();
		this.breakMappings = new HashMap<Integer, int[]>();
		addSimpleMapping('a', 0x1c);
		addSimpleMapping('b', 0x32);
		addSimpleMapping('c', 0x21);
		addSimpleMapping('d', 0x23);
		addSimpleMapping('e', 0x24);
		addSimpleMapping('f', 0x2b);
		addSimpleMapping('g', 0x34);
		addSimpleMapping('h', 0x33);
		addSimpleMapping('i', 0x43);
		addSimpleMapping('j', 0x3b);
		addSimpleMapping('k', 0x42);
		addSimpleMapping('l', 0x4b);
		addSimpleMapping('m', 0x3a);
		addSimpleMapping('n', 0x31);
		addSimpleMapping('o', 0x44);
		addSimpleMapping('p', 0x4d);
		addSimpleMapping('q', 0x15);
		addSimpleMapping('r', 0x2d);
		addSimpleMapping('s', 0x1b);
		addSimpleMapping('t', 0x2c);
		addSimpleMapping('u', 0x3c);
		addSimpleMapping('v', 0x2a);
		addSimpleMapping('w', 0x1d);
		addSimpleMapping('x', 0x22);
		addSimpleMapping('y', 0x1a);
		addSimpleMapping('z', 0x35);
		addSimpleMapping('0', 0x45);
		addSimpleMapping('1', 0x16);
		addSimpleMapping('2', 0x1e);
		addSimpleMapping('3', 0x26);
		addSimpleMapping('4', 0x25);
		addSimpleMapping('5', 0x2e);
		addSimpleMapping('6', 0x36);
		addSimpleMapping('7', 0x3d);
		addSimpleMapping('8', 0x3e);
		addSimpleMapping('9', 0x46);
		addSimpleMapping('ß', 0x4e);
		addSimpleMapping('´', 0x55);
		addSimpleMapping('ü', 0x54);
		addSimpleMapping('+', 0x5b);
		addSimpleMapping('ö', 0x4c);
		addSimpleMapping('ä', 0x52);
		addSimpleMapping('#', 0x5d);
		addSimpleMapping(',', 0x41);
		addSimpleMapping('.', 0x49);
		addSimpleMapping('-', 0x4a);
		addSimpleMapping('<', 0x61);
		addSimpleMapping(' ', 0x29);
		addSimpleMapping(SWT.F1, 0x05);
		addSimpleMapping(SWT.F2, 0x06);
		addSimpleMapping(SWT.F3, 0x04);
		addSimpleMapping(SWT.F4, 0x0c);
		addSimpleMapping(SWT.F5, 0x03);
		addSimpleMapping(SWT.F6, 0x0b);
		addSimpleMapping(SWT.F7, 0x83);
		addSimpleMapping(SWT.F8, 0x0a);
		addSimpleMapping(SWT.F9, 0x01);
		addSimpleMapping(SWT.F10, 0x09);
		addSimpleMapping(SWT.F11, 0x78);
		addSimpleMapping(SWT.F12, 0x07);
//		addExtendedMapping(SWT.F13, 0x27);
//		addExtendedMapping(SWT.F14, 0x2f);
//		addExtendedMapping(SWT.F15, ???); not yet assigned
		addSimpleMapping(SWT.ESC, 0x76);
		addSimpleMapping(SWT.TAB, 0x0d);
		addSimpleMapping(SWT.CR, 0x5a);
		addSimpleMapping(SWT.CAPS_LOCK, 0x58);
		addSimpleMapping(SWT.SHIFT, 0x12);
		addSimpleMapping(SWT.CTRL, 0x14);
		addSimpleMapping(SWT.ALT, 0x11);
		addExtendedMapping(SWT.COMMAND, 0x1f);
		addSimpleMapping(SWT.NUM_LOCK, 0x77);
		addSimpleMapping(SWT.BS, 0x66);
		addNumLockAffectedMapping(SWT.PAGE_UP, 0x7d);
		addNumLockAffectedMapping(SWT.PAGE_DOWN, 0x7a);
		addNumLockAffectedMapping(SWT.HOME, 0x6c);
		addNumLockAffectedMapping(SWT.END, 0x69);
		addNumLockAffectedMapping(SWT.DEL, 0x71);
		addNumLockAffectedMapping(SWT.INSERT, 0x70);
		addNumLockAffectedMapping(SWT.ARROW_LEFT, 0x6b);
		addNumLockAffectedMapping(SWT.ARROW_RIGHT, 0x74);
		addNumLockAffectedMapping(SWT.ARROW_UP, 0x75);
		addNumLockAffectedMapping(SWT.ARROW_DOWN, 0x72);
		addSimpleMapping(SWT.KEYPAD_0, 0x70);
		addSimpleMapping(SWT.KEYPAD_1, 0x69);
		addSimpleMapping(SWT.KEYPAD_2, 0x72);
		addSimpleMapping(SWT.KEYPAD_3, 0x7a);
		addSimpleMapping(SWT.KEYPAD_4, 0x6b);
		addSimpleMapping(SWT.KEYPAD_5, 0x73);
		addSimpleMapping(SWT.KEYPAD_6, 0x74);
		addSimpleMapping(SWT.KEYPAD_7, 0x6c);
		addSimpleMapping(SWT.KEYPAD_8, 0x75);
		addSimpleMapping(SWT.KEYPAD_9, 0x7d);
		addSimpleMapping(SWT.KEYPAD_ADD, 0x79);
		addSimpleMapping(SWT.KEYPAD_SUBTRACT, 0x7b);
		addSimpleMapping(SWT.KEYPAD_MULTIPLY, 0x7c);
		addExtendedMapping(SWT.KEYPAD_DIVIDE, 0x4a);
		addSimpleMapping(SWT.KEYPAD_DECIMAL, 0x71);
		addExtendedMapping(SWT.KEYPAD_CR, 0x5a);
		addNumLockAffectedMapping(SWT.KEYPAD_EQUAL, 0x70); // only exists on mac keyboards, but these don't have an "insert key"
	}
	
	/**
	 * Adds an SWT key code to make scan code sequence mapping.
	 * @param swtCode the SWT key code
	 * @param scanCodes the scan code sequence for the "make" event
	 */
	private void addMakeMapping(int swtCode, int... scanCodes) {
		makeMappings.put(swtCode, scanCodes);
	}
	
	/**
	 * Adds an SWT key code to break scan code sequence mapping.
	 * @param swtCode the SWT key code
	 * @param scanCodes the scan code sequence for the "break" event
	 */
	private void addBreakMapping(int swtCode, int... scanCodes) {
		breakMappings.put(swtCode, scanCodes);
	}
	
	/**
	 * Adds both a "make" and "break" mapping for a key that uses a single-byte
	 * make sequence and the same byte preceded by 0xf0 for the break event.
	 * @param swtCode the SWT key code
	 * @param simpleScanCode the value to use for the "make" event
	 */
	private void addSimpleMapping(int swtCode, int simpleScanCode) {
		addMakeMapping(swtCode, simpleScanCode);
		addBreakMapping(swtCode, 0xf0, simpleScanCode);
	}

	/**
	 * Adds both a "make" and "break" mapping for a key that uses the
	 * make sequence "e0 subCode" and the break sequence "e0 f0 subCode".
	 * @param swtCode the SWT key code
	 * @param subCode the sub-code to use
	 */
	private void addExtendedMapping(int swtCode, int subCode) {
		addMakeMapping(swtCode, 0xe0, subCode);
		addBreakMapping(swtCode, 0xe0, 0xf0, subCode);
	}

	/**
	 * Adds both a "make" and "break" mapping for a key that uses the
	 * make sequence "e0 12 e0 subCode" and the break sequence
	 * "e0 f0 subCode e0 f0 12". The extended code 12 is a fake shift key
	 * press used to make NumLock backwards compatible. This implementation
	 * does not correctly reproduce fake key sequences. Instead, it is
	 * intended to encourage filtering fake codes at the lowest level possible.
	 * @param swtCode the SWT key code
	 * @param subCode the sub-code to use
	 */
	private void addNumLockAffectedMapping(int swtCode, int subCode) {
		addMakeMapping(swtCode, 0xe0, 0x12, 0xe0, subCode);
		addBreakMapping(swtCode, 0xe0, 0xf0, subCode, 0xe0, 0xf0, 0x12);
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.devices.keyboard.IKeyboardMap#translate(org.eclipse.swt.events.KeyEvent, boolean)
	 */
	@Override
	public int[] translate(KeyEvent event, boolean pressed) {
		Map<Integer, int[]> mappings = pressed ? makeMappings : breakMappings;
		return mappings.get(event.keyCode);
		
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.ui.util.keyboard.IKeyboardMap#getMissedKeys()
	 */
	@Override
	public IKeyboardMapMissedKey[] getMissedKeys() {

		KeyboardMapMissedKey rightWindowsKey = new KeyboardMapMissedKey();
		rightWindowsKey.setLabel("Right Win");
		rightWindowsKey.setMakeCodeSequence(0xe0, 0x27);
		rightWindowsKey.setBreakCodeSequence(0xe0, 0xf0, 0x27);

		KeyboardMapMissedKey windowsMenuKey = new KeyboardMapMissedKey();
		windowsMenuKey.setLabel("Win Menu");
		windowsMenuKey.setMakeCodeSequence(0xe0, 0x2f);
		windowsMenuKey.setBreakCodeSequence(0xe0, 0xf0, 0x2f);

		KeyboardMapMissedKey rightShiftKey = new KeyboardMapMissedKey();
		rightShiftKey.setLabel("Right Shift");
		rightShiftKey.setMakeCodeSequence(0x59);
		rightShiftKey.setBreakCodeSequence(0xf0, 0x59);

		KeyboardMapMissedKey altGrKey = new KeyboardMapMissedKey();
		altGrKey.setLabel("Alt Gr");
		altGrKey.setMakeCodeSequence(0xe0, 0x11);
		altGrKey.setBreakCodeSequence(0xe0, 0xf0, 0x11);

		KeyboardMapMissedKey rightControlKey = new KeyboardMapMissedKey();
		rightControlKey.setLabel("Right Control");
		rightControlKey.setMakeCodeSequence(0xe0, 0x14);
		rightControlKey.setBreakCodeSequence(0xe0, 0xf0, 0x14);

		KeyboardMapMissedKey circumflexKey = new KeyboardMapMissedKey();
		circumflexKey.setLabel("^");
		circumflexKey.setMakeCodeSequence(0x0e);
		circumflexKey.setBreakCodeSequence(0xf0, 0x0e);

		KeyboardMapMissedKey acuteKey = new KeyboardMapMissedKey();
		acuteKey.setLabel("´");
		acuteKey.setMakeCodeSequence(0x55);
		acuteKey.setBreakCodeSequence(0xf0, 0x55);

		KeyboardMapMissedKey printScreenKey = new KeyboardMapMissedKey();
		printScreenKey.setLabel("Print Screen");
		printScreenKey.setMakeCodeSequence(0xe0, 0x12, 0xe0, 0x7c);
		printScreenKey.setBreakCodeSequence(0xe0, 0xf0, 0x7c, 0xe0, 0xf0, 0x12);

		KeyboardMapMissedKey altPrintScreenKey = new KeyboardMapMissedKey();
		altPrintScreenKey.setLabel("Alt + Print Screen");
		altPrintScreenKey.setMakeCodeSequence(0x84);
		altPrintScreenKey.setBreakCodeSequence(0xf0, 0x84);

		KeyboardMapMissedKey scrollLockKey = new KeyboardMapMissedKey();
		scrollLockKey.setLabel("Scroll Lock");
		scrollLockKey.setMakeCodeSequence(0x7e);
		scrollLockKey.setBreakCodeSequence(0xf0, 0x7e);

		KeyboardMapMissedKey pauseBreakKey = new KeyboardMapMissedKey();
		pauseBreakKey.setLabel("Pause / Break");
		pauseBreakKey.setMakeCodeSequence(0xe1, 0x14, 0x77, 0xe1, 0xf0, 0x14, 0xf0, 0x77);
		// no break codes

		return new IKeyboardMapMissedKey[] {
			rightWindowsKey, windowsMenuKey, rightShiftKey, altGrKey, rightControlKey, circumflexKey, acuteKey, printScreenKey, altPrintScreenKey, scrollLockKey, pauseBreakKey
		};
	}

}
