package br.pucrio.inf.organic.extensionPoints;

import java.util.Set;

import br.pucrio.inf.organic.adapters.JSpiritAdapter;
import br.pucrio.inf.organic.model.Project;

/**
 * Abstract class that must be extended by all agglomeration strategies.
 * 
 * @author Willian
 *  
 */
public abstract class AgglomerationStrategy<T extends AgglomerationModel>  {

	protected final JSpiritAdapter spiritAdapter;
	protected final Project project;

	/**
	 * 
	 * @param project with all components and classes
	 */
	public AgglomerationStrategy(Project project) {
		this.project = project;
		this.spiritAdapter = project.getSpiritAdapter();
	}

	/**
	 * Abstract method responsible for find and return a set with all agglomeration using a given strategy.
	 * 
	 * @return set of agglomerations
	 */
	public abstract Set<T> findAgglomerations();
}
