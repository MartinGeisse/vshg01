/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.cpu;

/**
 * 
 */
public class MockCpuUserInterface implements ICpuUserInterface {

	/**
	 * the readGeneralRegister
	 */
	private boolean readGeneralRegister;

	/**
	 * the writeGeneralRegister
	 */
	private boolean writeGeneralRegister;

	/**
	 * the readSpecialRegister
	 */
	private boolean readSpecialRegister;

	/**
	 * the writeSpecialRegister
	 */
	private boolean writeSpecialRegister;

	/**
	 * the writePc
	 */
	private boolean writePc;

	/**
	 * the store
	 */
	private boolean store;

	/**
	 * the writeTlb
	 */
	private boolean writeTlb;

	/**
	 * the index
	 */
	private int index = -1;

	/**
	 * @return Returns the readGeneralRegister.
	 */
	public boolean isReadGeneralRegister() {
		return readGeneralRegister;
	}

	/**
	 * Sets the readGeneralRegister.
	 * @param readGeneralRegister the new value to set
	 */
	public void setReadGeneralRegister(boolean readGeneralRegister) {
		this.readGeneralRegister = readGeneralRegister;
	}

	/**
	 * @return Returns the writeGeneralRegister.
	 */
	public boolean isWriteGeneralRegister() {
		return writeGeneralRegister;
	}

	/**
	 * Sets the writeGeneralRegister.
	 * @param writeGeneralRegister the new value to set
	 */
	public void setWriteGeneralRegister(boolean writeGeneralRegister) {
		this.writeGeneralRegister = writeGeneralRegister;
	}

	/**
	 * @return Returns the readSpecialRegister.
	 */
	public boolean isReadSpecialRegister() {
		return readSpecialRegister;
	}

	/**
	 * Sets the readSpecialRegister.
	 * @param readSpecialRegister the new value to set
	 */
	public void setReadSpecialRegister(boolean readSpecialRegister) {
		this.readSpecialRegister = readSpecialRegister;
	}

	/**
	 * @return Returns the writeSpecialRegister.
	 */
	public boolean isWriteSpecialRegister() {
		return writeSpecialRegister;
	}

	/**
	 * Sets the writeSpecialRegister.
	 * @param writeSpecialRegister the new value to set
	 */
	public void setWriteSpecialRegister(boolean writeSpecialRegister) {
		this.writeSpecialRegister = writeSpecialRegister;
	}

	/**
	 * @return Returns the index.
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Sets the index.
	 * @param index the new value to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * @return Returns the writePc.
	 */
	public boolean isWritePc() {
		return writePc;
	}

	/**
	 * Sets the writePc.
	 * @param writePc the new value to set
	 */
	public void setWritePc(boolean writePc) {
		this.writePc = writePc;
	}

	/**
	 * @return Returns the store.
	 */
	public boolean isStore() {
		return store;
	}

	/**
	 * Sets the store.
	 * @param store the new value to set
	 */
	public void setStore(boolean store) {
		this.store = store;
	}

	/**
	 * @return Returns the writeTlb.
	 */
	public boolean isWriteTlb() {
		return writeTlb;
	}

	/**
	 * Sets the writeTlb.
	 * @param writeTlb the new value to set
	 */
	public void setWriteTlb(boolean writeTlb) {
		this.writeTlb = writeTlb;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.cpu.ICpuUserInterface#onReadGeneralRegister(int)
	 */
	@Override
	public void onReadGeneralRegister(int index) {
		this.readGeneralRegister = true;
		this.index = index;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.cpu.ICpuUserInterface#onReadSpecialRegister(int)
	 */
	@Override
	public void onReadSpecialRegister(int index) {
		this.readSpecialRegister = true;
		this.index = index;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.cpu.ICpuUserInterface#onWriteGeneralRegister(int)
	 */
	@Override
	public void onWriteGeneralRegister(int index) {
		this.writeGeneralRegister = true;
		this.index = index;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.cpu.ICpuUserInterface#onWriteSpecialRegister(int)
	 */
	@Override
	public void onWriteSpecialRegister(int index) {
		this.writeSpecialRegister = true;
		this.index = index;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.cpu.ICpuUserInterface#onWritePc()
	 */
	@Override
	public void onWritePc() {
		this.writePc = true;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.cpu.ICpuUserInterface#onStore()
	 */
	@Override
	public void onStore() {
		this.store = true;
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.ecotools.simulator.cpu.ICpuUserInterface#onWriteTlb()
	 */
	@Override
	public void onWriteTlb() {
		this.writeTlb = true;
	}

}
