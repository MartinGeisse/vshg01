/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.unit.webapp;

import name.martingeisse.unit.Constants;
import name.martingeisse.unit.util.Tester;

import org.junit.Assert;
import org.junit.Test;

/**
 * TODO: document me
 *
 */
public class TestTest {

	/**
	 * the tester
	 */
	private Tester tester;
	
	/**
	 * Constructor.
	 */
	public TestTest() {
		tester = new Tester();
		tester.openPage(Constants.WEBAPP_HTTP);
		tester.setCurrentState(tester.marketplaceMainScreen);
	}
	
	/**
	 * 
	 */
	@Test
	public void testFacebookLink() {
		tester.marketplaceMainScreen.clickFacebookLink();
		Assert.assertEquals("http://www.facebook.com/shopgate", tester.getCurrentPage().getUrl().toString());
	}
	
	/**
	 * 
	 */
	@Test
	public void testAllShopsListContainsSnipesShop() {
		tester.marketplaceMainScreen.clickShopsLink();
		tester.marketplaceShopList.clickAllShopsTabHeader();
		tester.findSingleElement("test", "//span[@class='line-one'][string(self::*)='NeueTischkultur.de']");
	}
	
	/**
	 * 
	 */
	@Test
	public void test3() {
		tester.marketplaceMainScreen.goToShopPage("NeueTischkultur.de");
		tester.dumpPage();
	}

}
