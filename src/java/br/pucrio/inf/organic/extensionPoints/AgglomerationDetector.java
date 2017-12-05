package br.pucrio.inf.organic.extensionPoints;

import java.util.Set;

import br.pucrio.inf.organic.model.Project;

/**
 * Defines the interface that every external agglomeration detector must implement
 * @author Willian
 *
 * @param <T>
 */
public interface AgglomerationDetector<T extends AgglomerationModel> {
	
	public static final String EXTENSION_ID = "br.pucrio.inf.organic.AgglomerationDetector";
	public static final String CLASS_ATTRIBUTE_ID = "class";

	/**
	 * Returns the name of the provided agglomeration detector
	 * @return
	 */
	public String getName();
	
	/**
	 * Returns a description about the provided detector
	 * @return
	 */
	public String getDescription();

	/**
	 * Returns the agglomeration detected for a given project
	 * @param project
	 * @return
	 */
	public Set<T> findAndMarkAgglomerationsFor(Project project);
}