/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.unit.webapp.marketplace;

import com.gargoylesoftware.htmlunit.html.HtmlElement;

import name.martingeisse.unit.util.ITesterState;
import name.martingeisse.unit.util.Tester;

/**
 * TODO: document me
 *
 */
public class MarketplaceMainScreenState implements ITesterState {

	/**
	 * the tester
	 */
	private Tester tester;
	
	/**
	 * Constructor.
	 * @param tester the tester
	 */
	public MarketplaceMainScreenState(Tester tester) {
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
	public void clickFacebookLink() {
		tester.expectState(this);
		tester.clickLink("//a[contains(@href,'facebook')]");
		tester.setCurrentState(null);
	}
	
	/**
	 * 
	 */
	public void clickShopsLink() {
		tester.expectState(this);
		tester.clickLink("//a[contains(@href,'shops')]");
		tester.setCurrentState(tester.marketplaceShopList);
	}
	
	/**
	 * @param shopName the name of the shop, as displayed in the "all shops" list
	 */
	public void goToShopPage(String shopName) {
		
		// Determine the delimiter. NOTE: XPath has no escape syntax, so a string cannot contain
		// both single quotes and double quotes. We'd have to hack around using concat() in that case!
		char delimiter;
		if (shopName.indexOf('\'') == -1) {
			delimiter = '\'';
		} else if (shopName.indexOf('"') == -1) {
			delimiter = '"';
		} else {
			throw new IllegalArgumentException("this function cannot handle shop names with both single-quotes and double-quotes yet");
		}
		
		// go to the shop page
		tester.marketplaceMainScreen.clickShopsLink();
		tester.marketplaceShopList.clickAllShopsTabHeader();
		HtmlElement element = tester.findSingleElement("test", "//span[@class='line-one'][contains(string(self::*)," + delimiter + shopName + delimiter + ")]");
		while (element.getNodeName().equals("a")) {
			element = (HtmlElement)element.getParentNode();
		}
		tester.click(element);
		tester.setCurrentState(null);
		
	}
	
}
