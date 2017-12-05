package br.pucrio.inf.organic.workflows;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import spirit.metrics.storage.InvokingCache;
import br.pucrio.inf.organic.extensions.ui.AnomaliesView;
import br.pucrio.inf.organic.extensions.ui.agglomeration.ProjectResults;
import br.pucrio.inf.organic.extensions.ui.agglomeration.WorkspaceResults;
import br.pucrio.inf.organic.model.Project;
import br.pucrio.inf.organic.projectBuilders.ProjectBuilder;
import br.pucrio.inf.organic.projectBuilders.ProjectBuilderFactory;

/**
 * Basic workflow for any execution of Organic.
 * Independently of the users preferences all the steps in this workflow must be executed.  
 * @author Willian
 *
 */
public class AnomaliesDetectionBasicWorkflow {

	public void executeFor(List<IProject> eclipseProjects) {
		WorkspaceResults results = new WorkspaceResults();
		for (IProject project : eclipseProjects) {
			results.addResult(executeFor(project));
		}
		
		showAnomaliesView(results);
	}

	private ProjectResults executeFor(IProject eclipseProject) {
		Project project = buildProject(eclipseProject);
		
		project.findCodeAnomalies();
		
		return new ProjectResults(project);
	}

	private Project buildProject(IProject eclipseProject) {
		InvokingCache.getInstance().initialize();
		ProjectBuilder projectBuilder = ProjectBuilderFactory.get(eclipseProject);
		Project project = projectBuilder.parse();
		return project;
	}

	private void showAnomaliesView(WorkspaceResults results) {
		try {
			AnomaliesView view = (AnomaliesView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(AnomaliesView.ID);
			view.updateContent(results);
			
		} catch (PartInitException e) {
			System.err.println("Exception while showing agglomerations: " + e);
		}
	}	
}
