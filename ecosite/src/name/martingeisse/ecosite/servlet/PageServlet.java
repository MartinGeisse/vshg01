/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecosite.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import name.martingeisse.ecosite.Constants;
import name.martingeisse.ecosite.page.AllPagesDirective;
import name.martingeisse.ecosite.page.FreemarkerConfiguration;
import name.martingeisse.ecosite.page.PageDirective;
import name.martingeisse.ecosite.page.PagePathUtil;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Serves publicly visible pages.
 */
public class PageServlet extends HttpServlet {

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// get and validate page path
		String pagePath = getPagePath(request);
		if (!PagePathUtil.isValidRelativePagePath(pagePath)) {
			emitPageNotFound(response);
			return;
		}
		
		// map the empty page path to the index.ftl
		if (pagePath.isEmpty()) {
			pagePath = "index";
		}
		
		// build the Freemarker data model
		Map<String, Object> dataModel = new HashMap<String, Object>();
		{
			int pagePathLastSlash = pagePath.lastIndexOf('/');
			String pagePathPrefix = (pagePathLastSlash == -1 ? "" : pagePath.substring(0, pagePathLastSlash + 1));
			dataModel.put("data.pagePathPrefix", pagePathPrefix);
		}
		dataModel.put("page", new PageDirective());
		dataModel.put("allPages", new AllPagesDirective());
		
		// get the Freemarker template (404 if not found)
		Template template;
		try {
			template = FreemarkerConfiguration.configuration.getTemplate(pagePath + ".ftl");
		} catch (IOException e) {
			emitPageNotFound(response);
			return;
		}
		
		// render the page intro unless this is a CSS, JS or similar
		PrintWriter out = response.getWriter();
		if (pagePath.indexOf('.') == -1) {
			out.println("<html><head><title>" + Constants.pageTitle + "</title><link rel=\"stylesheet\" type=\"text/css\" href=\"/main.css\"></head><body>");
			out.println("<div class=\"PageContainer\">");
			out.println("<div class=\"NavigationContainer\">");
			out.println("<div>feiuwhiu</div>");
			out.println("<div>feiuwhiu</div>");
			out.println("<div>feiuwhiu</div>");
			out.println("</div>");
			out.println("<div class=\"ContentContainer\">");
		}
		
		// render the template to the servlet response writer
		try {
			template.process(dataModel, out);
		} catch (TemplateException e) {
			throw new IOException(e);
		}
		
		// render the page outro
		if (pagePath.indexOf('.') == -1) {
			out.println("</div></div></body></html>");
		}

		// close the servlet response writer
		out.flush();
		out.close();
		
	}

	/**
	 * @param request
	 * @return
	 */
	private String getPagePath(HttpServletRequest request) {
		return PagePathUtil.trimSlashes(request.getRequestURI());
	}
	
	/**
	 * 
	 */
	private void emitPageNotFound(HttpServletResponse response) throws IOException {
		emitShortResponse(response, 404, "<html><body>Die angegebene URL wurde nicht gefunden. <a href=\"/\">Zur Startseite</a></body></html>");
	}
	
	/**
	 * 
	 */
	private void emitShortResponse(HttpServletResponse response, int statusCode, String html) throws IOException {
		response.setStatus(statusCode);
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html");
		PrintWriter w = response.getWriter();
		w.println(html);
		w.flush();
		w.close();
	}
	
}
