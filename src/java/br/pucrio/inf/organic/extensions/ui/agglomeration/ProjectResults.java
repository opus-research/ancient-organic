package br.pucrio.inf.organic.extensions.ui.agglomeration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.jdt.core.IJavaProject;

import br.pucrio.inf.organic.extensionPoints.AgglomerationModel;
import br.pucrio.inf.organic.model.Project;

/**
 * Data model that aggregates agglomeration types (AgglomerationType) of a Project.
 * This model is exclusive for the AgglomerationsView.
 * @author Willian
 *
 */
public class ProjectResults implements Comparable<ProjectResults> {
	
	private final String name;
	private final List<AgglomerationTypeResults> agglomerationTypes;
	private final String path;
	private final Project project;
	
	public ProjectResults(Project project, Map<String, Set<? extends AgglomerationModel>> builtinAgglomerations,
			Map<String, Set<? extends AgglomerationModel>> extensionsAgglomerations) {
		
		this(project);
		
		for (Entry<String, Set<? extends AgglomerationModel>> entry : builtinAgglomerations.entrySet()) {
			getAgglomerationTypes().add(new AgglomerationTypeResults(entry.getKey(), entry.getValue(), project.getHistory()));
		}
		
		for (Entry<String, Set<? extends AgglomerationModel>> entry : extensionsAgglomerations.entrySet()) {
			getAgglomerationTypes().add(new AgglomerationTypeResults(entry.getKey(), entry.getValue(), project.getHistory()));
		}
		
		Collections.sort(getAgglomerationTypes(), new Comparator<AgglomerationTypeResults>() {
			@Override public int compare(AgglomerationTypeResults o1, AgglomerationTypeResults o2) {
				return o1.getName().compareTo(o2.getName());
		}});
	}

	public ProjectResults(Project project) {
		this.project = project;
		IJavaProject javaProject = project.getJavaProject();
		this.name = javaProject.getElementName();
		this.path = javaProject.getResource().getLocationURI().getPath();
		
		agglomerationTypes = new ArrayList<AgglomerationTypeResults>();
	}

	public String getName() {
		return name;
	}

	public Object[] getAgglomerationTypesAsArray() {
		return getAgglomerationTypes().toArray();
	}
	
	@Override
	public String toString() {
		return getName();
	}

	@Override
	public int compareTo(ProjectResults other) {
		return this.getName().compareTo(other.getName());
	}

	public List<AgglomerationTypeResults> getAgglomerationTypes() {
		return agglomerationTypes;
	}

	public String getPath() {
		return path;
	}

	public Project getProject() {
		return project;
	}
}
