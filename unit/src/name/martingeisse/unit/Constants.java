/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.unit;

/**
 * Global constants that control the test code.
 */
public class Constants {

	/**
	 * the WEBAPP_DOMAIN
	 */
//	public static final String WEBAPP_DOMAIN = "m.shopgatepg.com";
	public static final String WEBAPP_DOMAIN = "m.geisse.localdev.cc";
	
	/**
	 * the WEBAPP_PATH_PREFIX
	 */
//	public static final String WEBAPP_PATH_PREFIX = "";
	public static final String WEBAPP_PATH_PREFIX = "php/shopgate/index.php";
	
	/**
	 * the WEBAPP_HTTP
	 */
	public static final String WEBAPP_HTTP = "http://" + WEBAPP_DOMAIN + "/" + WEBAPP_PATH_PREFIX;
	
	/**
	 * the WEBAPP_HTTPS
	 */
	public static final String WEBAPP_HTTPS = "https://" + WEBAPP_DOMAIN + "/" + WEBAPP_PATH_PREFIX;
	
}
