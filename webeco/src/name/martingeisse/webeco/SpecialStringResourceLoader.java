/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.webeco;

import java.util.Locale;

import name.martingeisse.terra.wicket.application.FirstLineResourceVersion;
import name.martingeisse.terra.wicket.jquery.Dummy;

import org.apache.wicket.Component;
import org.apache.wicket.request.resource.JavaScriptPackageResource;
import org.apache.wicket.resource.loader.IStringResourceLoader;

/**
 * Specialized {@link IStringResourceLoader} for strings that must be determined
 * programmatically, such as URL paths containing revision numbers.
 */
public class SpecialStringResourceLoader implements IStringResourceLoader {
	
	/* (non-Javadoc)
	 * @see org.apache.wicket.resource.loader.IStringResourceLoader#loadStringResource(java.lang.Class, java.lang.String, java.util.Locale, java.lang.String, java.lang.String)
	 */
	@Override
	public String loadStringResource(Class<?> clazz, String key, Locale locale, String style, String variation) {
		return lookup(key, locale, style, variation);
	}

	/* (non-Javadoc)
	 * @see org.apache.wicket.resource.loader.IStringResourceLoader#loadStringResource(org.apache.wicket.Component, java.lang.String, java.util.Locale, java.lang.String, java.lang.String)
	 */
	@Override
	public String loadStringResource(Component component, String key, Locale locale, String style, String variation) {
		return lookup(key, locale, style, variation);
	}

	/**
	 * @param key
	 * @return
	 */
	private String lookup(String key, Locale locale, String style, String variation) {
		if (key.equals("webeco.path.js.base")) {
			JavaScriptPackageResource resource = new JavaScriptPackageResource(Dummy.class, "jquery-1.6.4-with-tools-1.2.6.js", locale, style, variation);
			String version = FirstLineResourceVersion.getVersionFrom(resource);
			return "base-revision-" + version + ".js";
		} else {
			return null;
		}
	}
	
}
