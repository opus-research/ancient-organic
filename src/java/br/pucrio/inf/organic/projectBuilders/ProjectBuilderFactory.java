package br.pucrio.inf.organic.projectBuilders;

import org.eclipse.core.resources.IProject;

import br.pucrio.inf.organic.extensions.OrganicActivator;
import br.pucrio.inf.organic.projectBuilders.impl.ProjectFromConcernMapperBuilder;
import br.pucrio.inf.organic.projectBuilders.impl.ProjectFromPackageStructureBuilder;

/**
 * Static factory for ProjectBuilder.
 * @author Willian
 *
 */
public class ProjectBuilderFactory {
	
	public static final String SOURCE = "ComponentsSource";
	
	/**
	 * Returns a ProjectBuilder according to user preferences.	
	 * @param eclipseProject
	 * @return
	 */
	public static ProjectBuilder get(IProject eclipseProject) {
		if (eclipseProject == null)
			return null;
		
		String projectBuilderName = OrganicActivator.getPreferences().getString(SOURCE);
		
		if (projectBuilderName.equals(ProjectFromConcernMapperBuilder.NAME))
			return new ProjectFromConcernMapperBuilder(eclipseProject);
		
		return new ProjectFromPackageStructureBuilder(eclipseProject);
	}
}
