/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecosite.page;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateScalarModel;

/**
 * This directive prints a link to a page whose path is specified
 * by the directive body.
 */
public class PageDirective implements TemplateDirectiveModel {

	/* (non-Javadoc)
	 * @see freemarker.template.TemplateDirectiveModel#execute(freemarker.core.Environment, java.util.Map, freemarker.template.TemplateModel[], freemarker.template.TemplateDirectiveBody)
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public void execute(Environment environment, Map parameters, TemplateModel[] loopVariables, TemplateDirectiveBody body) throws TemplateException, IOException {
		
		// resolve relative / absolute paths
		String pagePath = FreemarkerUtil.toString(body);
		if (pagePath.startsWith("/")) {
			pagePath = pagePath.substring(1);
		} else {
			String pagePathPrefix = ((TemplateScalarModel)environment.getDataModel().get("data.pagePathPrefix")).getAsString();
			while (pagePath.startsWith("../")) {
				if (pagePathPrefix.isEmpty()) {
					environment.getOut().append("[invalid page path]");
					return;
				}
				pagePathPrefix = pagePathPrefix.substring(0, pagePathPrefix.length() - 1);
				int lastSlash = pagePathPrefix.lastIndexOf('/');
				pagePathPrefix = (lastSlash == -1 ? "" : pagePathPrefix.substring(0, lastSlash + 1));
				pagePath = pagePath.substring(3);
			}
			pagePath = pagePathPrefix + pagePath;
		}
		
		// build the link from the path and page meta-data
		Writer out = environment.getOut();
		out.append("<a href=\"/" + pagePath + "\"");
		PageMetaData metaData = PageRegistry.instance.getMetaData(pagePath);
		if (metaData == null) {
			out.append(" class=\"broken\"");
		}
		out.append(">");
		out.append(metaData == null ? pagePath : metaData.getTitle());
		out.append("</a>");
		
	}

}
