/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.unit.util;

import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Random;

import name.martingeisse.unit.webapp.marketplace.MarketplaceMainScreenState;
import name.martingeisse.unit.webapp.marketplace.MarketplaceShopListState;

import org.junit.Assert;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * This class encapsulates all utility functions
 * for testing.
 * 
 * State handling: This tester (or its subclass) contains
 * various public state objects that can be used to invoke
 * client behavior. These state objects correspond to
 * expected server-side pages, although their methods may
 * do more than just interact with a single page.
 * 
 * The client also holds a current state; whenever a state
 * object is used, it must be the current state or an error
 * is thrown. State methods interact with the server-side
 * page and typically exchange the current state with a
 * different one. As said above, state methods may do more
 * than interact with a single page and so may swap states
 * several times and also invoke methods of other states;
 * states only check that they are the current state at the
 * beginning of a method. Note that a state mismatch does
 * not indicate that the server served a different page than
 * expected; it only indicates that test code has a different
 * idea of what would be the next expected state than the
 * state that actually interacts with the server (in other
 * words, it indicates a problem with the test code, not
 * with the pages under test).
 */
public class Tester {

	/**
	 * the webClient
	 */
	private final WebClient webClient;
	
	/**
	 * the currentState
	 */
	private ITesterState currentState;
	
	/**
	 * the currentPage
	 */
	private HtmlPage currentPage;
	
	/**
	 * the marketplaceMainScreen
	 */
	public final MarketplaceMainScreenState marketplaceMainScreen = new MarketplaceMainScreenState(this);

	/**
	 * the marketplaceShopList
	 */
	public final MarketplaceShopListState marketplaceShopList = new MarketplaceShopListState(this);
	
	/**
	 * Constructor.
	 */
	public Tester() {
		webClient = new WebClient(BrowserVersion.FIREFOX_3_6);
	}
	
	/**
	 * Getter method for the webClient.
	 * @return the webClient
	 */
	public WebClient getWebClient() {
		return webClient;
	}

	/**
	 * Getter method for the currentState.
	 * @return the currentState
	 */
	public ITesterState getCurrentState() {
		return currentState;
	}
	
	/**
	 * Setter method for the currentState.
	 * @param currentState the currentState to set
	 */
	public void setCurrentState(ITesterState currentState) {
		this.currentState = currentState;
	}

	/**
	 * Getter method for the currentPage.
	 * @return the currentPage
	 */
	public HtmlPage getCurrentPage() {
		return currentPage;
	}
	
	/**
	 * Setter method for the currentPage.
	 * @param currentPage the currentPage to set
	 */
	public void setCurrentPage(HtmlPage currentPage) {
		this.currentPage = currentPage;
	}
	
	/**
	 * If the current state is not the specified state, then an
	 * {@link IllegalStateException} is thrown. As described
	 * in the class comment, this indicates a problem with either
	 * the test case code or the calling state.
	 * 
	 * @param expectedState the expected state (must not be null)
	 */
	public void expectState(ITesterState expectedState) {
		if (expectedState == null) {
			throw new IllegalArgumentException("expectedState argument is null");
		}
		if (currentState == null || currentState != expectedState) {
			throw new IllegalStateException("currentState is " + currentState + ", expectedState is " + expectedState);
		}
	}
	
	/**
	 * Opens the specified page.
	 * @param url the URL of the page
	 */
	public void openPage(String url) {
		try {
			currentPage = webClient.getPage(url);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Malformed URL", e);
		} catch (IOException e) {
			throw new IllegalArgumentException("I/O error", e);
		}
	}
	
	/**
	 * Dumps the page as HTML/XML to stdout. This method is typically
	 * used when writing test case code, not when actually testing.
	 */
	public void dumpPage() {
		System.out.println(currentPage.asXml());
	}
	
	/**
	 * Generates a random key to use for support log files.
	 * Technically, this returns a random 32-character string
	 * containing lowercase letters.
	 * @return the random key
	 */
	public static String generateRandomKey() {
		StringBuilder builder = new StringBuilder();
		Random random = new Random();
		for (int i=0; i<32; i++) {
			builder.append((char)('a' + random.nextInt(26)));
		}
		return builder.toString();
	}
	
	/**
	 * Fails the current test with the specified message. The contents of the current
	 * page are stored in a file for debugging.
	 * @param message the failure message
	 */
	public void fail(String message) {
		String filename = "reports/" + generateRandomKey() + ".html";
		try {
			FileWriter w = new FileWriter(filename);
			w.write(getCurrentPage().asXml());
			w.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		Assert.fail(message + ", dump: " + filename);
	}
	
	/**
	 * Evaluates the specified XPath expression. The expression must return
	 * a single HTML element, which is returned by this method.
	 * 
	 * @param context a context title, used for error messages only
	 * @param xpath the XPath expression for the element
	 * @return the element
	 */
	public HtmlElement findSingleElement(String context, String xpath) {
		List<?> matches = currentPage.getByXPath(xpath);
		if (matches.size() == 0) {
			fail(context + ": No element found for XPath: " + xpath);
		}
		if (matches.size() != 1) {
			fail(context + ": Multiple elements (" + matches.size() + ") found for XPath: " + xpath);
		}
		Object firstMatch = matches.get(0);
		if (!(firstMatch instanceof HtmlElement)) {
			fail(context + ": Element for XPath " + xpath + " is not an HtmlElement: " + firstMatch);
		}
		HtmlElement element = (HtmlElement)firstMatch;
		return element;
	}
	
	/**
	 * Evaluates the specified XPath expression. The expression must return
	 * a single HTML element. This element must have the specified elementName,
	 * otherwise the test fails. The element is returned by this method.
	 * 
	 * @param context a context title, used for error messages only
	 * @param xpath the XPath expression for the element
	 * @param expectedElementName the expected element name
	 * @return the element
	 */
	public HtmlElement findSingleElement(String context, String xpath, String expectedElementName) {
		HtmlElement element = findSingleElement(context, xpath);
		if (!element.getNodeName().equals(expectedElementName)) {
			fail(context + ": Element for XPath " + xpath + " is not an <" + expectedElementName + "> element, nodeName: " + element.getNodeName());
		}
		return element;
	}
	
	/**
	 * Clicks an element, wrapping {@link IOException}s.
	 * @param element the element to click
	 */
	public void click(HtmlElement element) {
		try {
			currentPage = element.click();
		} catch (IOException e) {
			throw new RuntimeException("I/O error", e);
		}
	}
	
	/**
	 * Evaluates the specified XPath expression. The expression must return
	 * a single HTML element, which must be an anchor (a) element. This
	 * element is then clicked. This method does not expect a href attribute
	 * so it works with Javascript links.
	 * 
	 * @param xpath the XPath expression for the link
	 */
	public void clickLink(String xpath) {
		click(findSingleElement("clickLink()", xpath, "a"));
	}
	
}
