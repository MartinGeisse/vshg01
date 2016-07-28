/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.webeco;

import java.awt.image.BufferedImage;
import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;

import name.martingeisse.terra.servlet.AntiJsessionidUrlFilter;
import name.martingeisse.terra.servlet.GlobalServletContext;
import name.martingeisse.webeco.wicket.CharacterGeneratorServletFilter;

import org.apache.wicket.protocol.http.WicketFilter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
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

		// This seems to be needed to prevent a deadlock when creating a BufferedImage
		// while the server is running. I guess the reason is that this forces AWT to start.
		new BufferedImage(8, 16, BufferedImage.TYPE_BYTE_INDEXED);
		
		final EnumSet<DispatcherType> allDispatcherTypes = EnumSet.allOf(DispatcherType.class);
		
		final Filter wicketFilter = new WicketFilter();
		FilterHolder wicketFilterHolder = new FilterHolder(wicketFilter);
		wicketFilterHolder.setInitParameter(WicketFilter.FILTER_MAPPING_PARAM, "/*");
		wicketFilterHolder.setInitParameter("applicationClassName", WebecoApplication.class.getCanonicalName());

		final ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		context.getSessionHandler().getSessionManager().setMaxInactiveInterval(30 * 60);
//		context.setInitParameter("useFakeFacebookLikeCount", "true");
//		context.setInitParameter("username", "postgres");
//		context.setInitParameter("password", "postgres");
		context.addEventListener(new GlobalServletContext());
		context.addFilter(AntiJsessionidUrlFilter.class, "/*", allDispatcherTypes);
		context.addFilter(CharacterGeneratorServletFilter.class, "/*", allDispatcherTypes);
//		// context.addFilter(DomainEnforcementFilter.class, "/*", allDispatcherTypes);

//		// the GZIP filter seems to cause problems on Jetty. The HTTP response either has
//		// an incorrect or duplicate Content-Length header (my tools won't tell me...)
////		context.addFilter(GzipFilter.class, "/*", allDispatcherTypes);

//		context.addFilter(JpaSessionServletFilter.class, "/*", allDispatcherTypes);
		context.addFilter(wicketFilterHolder, "/*", allDispatcherTypes);
		
		// a default servlet is needed, otherwise the filters cannot catch the request
		context.addServlet(DefaultServlet.class, "/*");
		
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
