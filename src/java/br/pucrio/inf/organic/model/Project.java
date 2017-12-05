package br.pucrio.inf.organic.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import br.pucrio.inf.organic.adapters.JSpiritAdapter;
import br.pucrio.inf.organic.history.ProjectHistory;

/**
 * Each instance of this class represents the architecture of a given eclipse project ({@link IJavaProject})
 * @author Willian
 *
 */
public class Project {

	private final IJavaProject javaProject;
	private final Set<Component> components = new HashSet<Component>();
	private final Set<Concern> concerns = new HashSet<Concern>();
	private final JSpiritAdapter adapter;
	private final Set<ProjectHistory> history = new HashSet<ProjectHistory>();

	public Project(IJavaProject javaProject, JSpiritAdapter connector) {
		this.javaProject = javaProject;
		this.adapter = connector;
	}

	public void addComponent(Component component) {
		getComponents().add(component);
	}

	@Override
	public String toString() {
		return getJavaProject().getElementName();
	}

	public IJavaProject getJavaProject() {
		return javaProject;
	}

	public JSpiritAdapter getSpiritAdapter() {
		return adapter;
	}

	/**
	 * Call JSpirit to detect all code anomalies in this project.
	 */
	public void findCodeAnomalies() {
		adapter.findCodeAnomalies();
	}

	public Set<Component> getComponents() {
		return components;
	}
	
	/**
	 * Return all the classes in components of this project
	 * @return Map
	 */
	public Map<String, TypeDeclaration> getClasses() {
		Map<String, TypeDeclaration> classes = new HashMap<String, TypeDeclaration>();
		for (Component component : components) {
			for(TypeDeclaration class_ : component.getClasses())
				classes.put(class_.resolveBinding().getQualifiedName(), class_);
		}
		return classes;
	}

	public void addConcern(Concern concern) {
		getConcerns().add(concern);
	}

	public Set<Concern> getConcerns() {
		return concerns;
	}

	public Set<ProjectHistory> getHistory() {
		return history;
	}
}
