package br.pucrio.inf.organic.extensions.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import br.pucrio.inf.organic.workflows.AgglomerationDetectionBasicWorkflow;

/**
 * Class responsible for adding Organic actions to the pop menu of eclipse.
 * @author Willian
 * @author Eclipse
 */
public class FindAgglomerationsPopUpAction implements IObjectActionDelegate {

	private Shell shell;
	private List<IProject> eclipseProjects = null;
	
	/**
	 * Constructor for FindAgglomerationsPopUpAction.
	 */
	public FindAgglomerationsPopUpAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		AgglomerationDetectionBasicWorkflow workflow = new AgglomerationDetectionBasicWorkflow();
		workflow.executeFor(eclipseProjects);
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		this.eclipseProjects = getSelectedProject(selection);
	}
	
	/**
	 * Extracts a list of projects (IProject) from a selection in the workspace.
	 * @param selection
	 * @return List
	 */
	private static List<IProject> getSelectedProject(ISelection selection) {
		if (selection == null) {
			return null;
		}
		 else if (selection instanceof IStructuredSelection) {
			 List<IProject> selected = new ArrayList<IProject>();
			for (Object o : ((IStructuredSelection) selection).toList()) {
				if (o instanceof IResource) {
					selected.add(((IResource)o).getProject());
				}
			}
			return selected;
		}
		return null;
	}
}
