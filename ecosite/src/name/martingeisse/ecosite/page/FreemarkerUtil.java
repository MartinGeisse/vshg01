/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecosite.page;

import java.io.IOException;
import java.io.StringWriter;

import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;

/**
 * Utility methods for Freemarker-using code.
 */
public class FreemarkerUtil {

	/**
	 * Renders a template directive body and returns the result as a string.
	 * @param body the body to render
	 * @return the body text
	 * @throws TemplateException on template errors
	 * @throws IOException on I/O errors
	 */
	public static String toString(TemplateDirectiveBody body) throws TemplateException, IOException {
		StringWriter w = new StringWriter();
		body.render(w);
		return w.toString();
	}
	
}
