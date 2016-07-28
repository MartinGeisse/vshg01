/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.webeco;

import name.martingeisse.terra.wicket.application.AbstractTerraWicketApplication;
import name.martingeisse.terra.wicket.jquery.Dummy;
import name.martingeisse.webeco.model.Simulator;

import org.apache.wicket.Page;
import org.apache.wicket.request.resource.PackageResourceReference;

/**
 * The application-specific Wicket application class.
 */
public class WebecoApplication extends AbstractTerraWicketApplication {

	/**
	 * the baseJavascriptUrl
	 */
//	private String baseJavascriptUrl;
	
	/* (non-Javadoc)
	 * @see org.apache.wicket.protocol.http.WebApplication#init()
	 */
	@Override
	protected void init() {
		super.init();
		
		// mount URLs
		mountResource("base.js", new PackageResourceReference(Dummy.class, "jquery-1.6.4-with-tools-1.2.6.js"));
		
		// other stuff
		getResourceSettings().getStringResourceLoaders().add(new SpecialStringResourceLoader());
		Simulator.initialize();
		
	}
	
	/* (non-Javadoc)
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	@Override
	public Class<? extends Page> getHomePage() {
		return SimulatorPage.class;
	}
	
	/**
	 * @return the singleton instance of this class
	 */
	public static WebecoApplication get() {
		return (WebecoApplication)(AbstractTerraWicketApplication.get());
	}
	
}
