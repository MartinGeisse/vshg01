/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecosite.page;

import java.io.IOException;
import java.util.Map;

import freemarker.core.Environment;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * This directive repeats its body for all registered pages, using
 * the variables 'path' and 'meta' to refer to the page's path and
 * meta-data, respectively. This is used in the sitemap page.
 * 
 * TODO: 'meta' is not yet set
 */
public class AllPagesDirective implements TemplateDirectiveModel {

	/* (non-Javadoc)
	 * @see freemarker.template.TemplateDirectiveModel#execute(freemarker.core.Environment, java.util.Map, freemarker.template.TemplateModel[], freemarker.template.TemplateDirectiveBody)
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public void execute(Environment environment, Map parameters, TemplateModel[] loopVariables, TemplateDirectiveBody body) throws TemplateException, IOException {
		for (PageMetaData meta : PageRegistry.instance.getAllMetaDatas()) {
			environment.setVariable("path", new SimpleScalar(meta.getPagePath()));
			body.render(environment.getOut());
		}
	}

}
