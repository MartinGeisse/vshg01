/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.unit.webapp.marketplace;

import name.martingeisse.unit.util.ITesterState;
import name.martingeisse.unit.util.Tester;

/**
 * TODO: document me
 *
 */
public class MarketplaceShopListState implements ITesterState {

	/**
	 * the tester
	 */
	private Tester tester;
	
	/**
	 * Constructor.
	 * @param tester the tester
	 */
	public MarketplaceShopListState(Tester tester) {
		this.tester = tester;
	}
	
	/**
	 * Getter method for the tester.
	 * @return the tester
	 */
	public Tester getTester() {
		return tester;
	}
	
	/**
	 * 
	 */
	public void clickNewShopsTabHeader() {
		tester.expectState(this);
		tester.clickLink("//a[contains(@href,'new')]");
	}
	
	/**
	 * 
	 */
	public void clickAllShopsTabHeader() {
		tester.expectState(this);
		tester.clickLink("//a[contains(@href,'all')]");
	}
	
	/**
	 * 
	 */
	public void clickHighlightShopsTabHeader() {
		tester.expectState(this);
		tester.clickLink("//a[contains(@href,'highlights')]");
	}
	
	/**
	 * 
	 */
	public void clickShopsBySectorTabHeader() {
		tester.expectState(this);
		tester.clickLink("//a[contains(@href,'sector')]");
	}
	
}
