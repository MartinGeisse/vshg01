/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.esdk.eclipse;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

/**
 * The Eclipse nature for the ESDK. This allows to make a project an
 * "ESDK project".
 */
public class EsdkNature implements IProjectNature {

	/**
	 * The fully qualified id of the nature extension.
	 */
	public static final String NATURE_ID = "name.martingeisse.esdk.EsdkNature";
	
	/**
	 * the project
	 */
	private IProject project;

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IProjectNature#configure()
	 */
	@Override
	public void configure() throws CoreException {
		addBuilder();
	}
	
	private void addBuilder() throws CoreException {
		
		final IProjectDescription projectDescription = project.getDescription();
		
		// check if the builder is already present
		final ICommand[] commands = projectDescription.getBuildSpec();
		for (int i = 0; i < commands.length; ++i) {
			if (commands[i].getBuilderName().equals(EsdkBuilder.BUILDER_ID)) {
				return;
			}
		}
		
		// add builder to project
		final ICommand command = projectDescription.newCommand();
		command.setBuilderName(EsdkBuilder.BUILDER_ID);
		final ICommand[] newCommands = new ICommand[commands.length + 1];
		System.arraycopy(commands, 0, newCommands, 1, commands.length);
		newCommands[0] = command;
		projectDescription.setBuildSpec(newCommands);
		project.setDescription(projectDescription, null);
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IProjectNature#deconfigure()
	 */
	@Override
	public void deconfigure() throws CoreException {
		removeBuilder();
	}

	private void removeBuilder() throws CoreException {
		final IProjectDescription projectDescription = project.getDescription();
		final List<ICommand> newCommandsList = new ArrayList<ICommand>();
		for (ICommand command : projectDescription.getBuildSpec()) {
			if (!command.getBuilderName().equals(EsdkBuilder.BUILDER_ID)) {
				newCommandsList.add(command);
			}
		}
		projectDescription.setBuildSpec(newCommandsList.toArray(new ICommand[newCommandsList.size()]));
		project.setDescription(projectDescription, null);
		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IProjectNature#getProject()
	 */
	@Override
	public IProject getProject() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IProjectNature#setProject(org.eclipse.core.resources.IProject)
	 */
	@Override
	public void setProject(final IProject project) {
		this.project = project;
	}

}
