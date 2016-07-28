/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecosite;

import java.util.EnumSet;

import javax.servlet.DispatcherType;

import name.martingeisse.ecosite.page.PageRegistry;
import name.martingeisse.ecosite.servlet.AntiJsessionidUrlFilter;
import name.martingeisse.ecosite.servlet.DomainEnforcementFilter;
import name.martingeisse.ecosite.servlet.GlobalServletContext;
import name.martingeisse.ecosite.servlet.PageServlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

/**
 * The main class.
 */
public class Main {

	/**
	 * The main method.
	 * @param args command-line arguments (ignored)
	 * @throws Exception on errors
	 */
	public static void main(String[] args) throws Exception {

		// TODO
		PageRegistry.instance.getClass();
		
		final EnumSet<DispatcherType> allDispatcherTypes = EnumSet.allOf(DispatcherType.class);
		
		final ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		context.getSessionHandler().getSessionManager().setMaxInactiveInterval(30 * 60);
		context.addEventListener(new GlobalServletContext());
		context.addFilter(AntiJsessionidUrlFilter.class, "/*", allDispatcherTypes);
		context.addFilter(DomainEnforcementFilter.class, "/*", allDispatcherTypes);

//		// the GZIP filter seems to cause problems on Jetty. The HTTP response either has
//		// an incorrect or duplicate Content-Length header (my tools won't tell me...)
////		context.addFilter(GzipFilter.class, "/*", allDispatcherTypes);

		// a default servlet is needed, otherwise the filters cannot catch the request
		context.addServlet(PageServlet.class, "/*");
		
//		ErrorPageErrorHandler errorHandler = new ErrorPageErrorHandler();
//		context.setErrorHandler(errorHandler);
//		errorHandler.addErrorPage(404, "/404");

//		SslContextFactory sslContextFactory = new SslContextFactory("/Users/martin/.keystore");
//		sslContextFactory.setKeyStorePassword("changeit");

//		SslSocketConnector sslSocketConnector = new SslSocketConnector(sslContextFactory);
//		sslSocketConnector.setPort(8443);

		final Server server = new Server(8080);
//		server.addConnector(sslSocketConnector);
		server.setHandler(context);
		server.start();
		server.join();
		
	}
	
}
