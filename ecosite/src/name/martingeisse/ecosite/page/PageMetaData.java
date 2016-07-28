/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecosite.page;

/**
 * This class keeps meta-data about a single page.
 */
public class PageMetaData {

	/**
	 * the pagePath
	 */
	private String pagePath;

	/**
	 * the title
	 */
	private String title;

	/**
	 * Constructor.
	 */
	public PageMetaData() {
	}

	/**
	 * Getter method for the pagePath.
	 * @return the pagePath
	 */
	public String getPagePath() {
		return pagePath;
	}

	/**
	 * Setter method for the pagePath.
	 * @param pagePath the pagePath to set
	 */
	public void setPagePath(final String pagePath) {
		this.pagePath = pagePath;
	}

	/**
	 * Getter method for the title.
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Setter method for the title.
	 * @param title the title to set
	 */
	public void setTitle(final String title) {
		this.title = title;
	}

}
