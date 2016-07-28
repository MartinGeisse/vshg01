/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecosite.page;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

/**
 * This class holds the global Freemarker configuration object.
 */
public class FreemarkerConfiguration {

	/**
	 * the configuration
	 */
	public static final Configuration configuration;

	// static intializer
	static {
		try {
			configuration = new Configuration();
			configuration.setDirectoryForTemplateLoading(new File("./pages"));
			configuration.setObjectWrapper(new DefaultObjectWrapper());
			configuration.setTemplateExceptionHandler(new TemplateExceptionHandler() {
				@Override
				public void handleTemplateException(TemplateException e, Environment env, Writer w) throws TemplateException {
//					System.out.println("An exception occurred in template " + env.getTemplate().getName() + ": " + e);
//					e.printStackTrace();
				}
			});
		} catch (IOException e) {
			throw new ExceptionInInitializerError(e);
		}
	}
	
}
