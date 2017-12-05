package br.pucrio.inf.organic.extensions.ui.agglomeration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Data model that aggregates project results in an workspace.
 * This model is exclusive for the AgglomerationsView.
 * @author Willian
 *
 */
public class WorkspaceResults {

	private final HashMap<String, ProjectResults> results = new HashMap<String, ProjectResults>();

	public HashMap<String, ProjectResults> getResults() {
		return results;
	}
	
	public List<ProjectResults> getSortedResults() {
		List<ProjectResults> sortedResults = new ArrayList<ProjectResults>(getResults().values());
		Collections.sort(sortedResults);
		return sortedResults;
	}	

	public void addResult(ProjectResults result) {
		if (!contains(result.getName()))
			getResults().put(result.getName(), result);
	}
	
	public Object[] getSortedResultsAsArray() {
		return getSortedResults().toArray();
	}
	
	/**
	 * Merges the results in this instance with results of other (older) instance.
	 * This method Includes only those projects that does not exists in this instance. 
	 * @param older
	 */
	public void mergeResultsWith(WorkspaceResults older) {
		HashMap<String, ProjectResults> oldResults = older.getResults();
		for (ProjectResults r : oldResults.values()) {
			addResult(r);
		}
	}

	private boolean contains(String key) {
		return getResults().containsKey(key);
	}

}
