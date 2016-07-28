/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.esdk.eclipse;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * TODO: document me
 *
 */
public class EsdkNatureDelegate implements IObjectActionDelegate {

	/**
	 * the selection
	 */
	private ISelection selection;

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
	 */
	@Override
	public void setActivePart(final IAction action, final IWorkbenchPart targetPart) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void selectionChanged(final IAction action, final ISelection selection) {
		this.selection = selection;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	@Override
	public void run(final IAction action) {
		if (selection instanceof IStructuredSelection) {
			final IStructuredSelection structuredSelection = (IStructuredSelection)selection;
			for (final Object element : structuredSelection.toList()) {
				if (element instanceof IProject) {
					final IProject project = (IProject)element;
					if (action.getId().equals("name.martingeisse.esdk.ProjectPopupMenuContribution.AddEsdkNature")) {
						addNature(project);
					} else if (action.getId().equals("name.martingeisse.esdk.ProjectPopupMenuContribution.RemoveEsdkNature")) {
						deleteNature(project);
					} else {
						Activator.getDefault().logWarning("EsdkNatureDelegate invoked for unknown action id: " + action.getId());
					}
				} else {
					Activator.getDefault().logWarning("EsdkNatureDelegate invoked for unknown target object: " + element);
				}
			}
		}
	}

	private void addNature(final IProject project) {
		try {
			final IProjectDescription description = project.getDescription();
			final String[] natures = description.getNatureIds();
			final String[] newNatures = new String[natures.length + 1];
			System.arraycopy(natures, 0, newNatures, 0, natures.length);
			newNatures[natures.length] = EsdkNature.NATURE_ID;
			description.setNatureIds(newNatures);
			project.setDescription(description, null);
		} catch (final CoreException e) {
			Activator.getDefault().logError("Could not associate ESDK nature with project " + project.getName(), e);
		}
	}

	private void deleteNature(final IProject project) {
		try {
			final IProjectDescription description = project.getDescription();
			final String[] natures = description.getNatureIds();
			final List<String> newNaturesList = new ArrayList<String>();
			for (String s : natures) {
				if (!s.equals(EsdkNature.NATURE_ID)) {
					newNaturesList.add(s);
				}
			}
			final String[] newNatures = newNaturesList.toArray(new String[newNaturesList.size()]);
			description.setNatureIds(newNatures);
			project.setDescription(description, null);
		} catch (final CoreException e) {
			Activator.getDefault().logError("Could not de-associate ESDK nature with project " + project.getName(), e);
		}
	}
	
}
