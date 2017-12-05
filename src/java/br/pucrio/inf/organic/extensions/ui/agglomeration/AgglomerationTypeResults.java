package br.pucrio.inf.organic.extensions.ui.agglomeration;

import java.util.List;
import java.util.Map;
import java.util.Set;

import br.pucrio.inf.organic.extensionPoints.AgglomerationModel;
import br.pucrio.inf.organic.history.AgglomerationHistory;
import br.pucrio.inf.organic.history.ProjectHistory;

/**
 * Data model that aggregates agglomerations of a given type.
 * This model is exclusive for the AgglomerationsView.
 * @author Willian
 *
 */
public class AgglomerationTypeResults {

	private final String name;
	private final Set<? extends AgglomerationModel> agglomerations;
	private final Set<ProjectHistory> projectHistory;
	
	public AgglomerationTypeResults(String name, Set<? extends AgglomerationModel> agglomerations, Set<ProjectHistory> projectHistory) {
		this.name = name;
		this.agglomerations = agglomerations;
		this.projectHistory = projectHistory;
		
		setAgglomerationsHistory();
	}

	private void setAgglomerationsHistory() {
		for (AgglomerationModel agg : this.agglomerations) {
			for (ProjectHistory projVersion : this.projectHistory) {
				AgglomerationHistory history = projVersion.getAgglomerations().get(agg.getUniqueID());
				if (history != null) {
					agg.addHistory(projVersion.getVersion(), history);
				}
			}
		}
	}

	public String getName() {
		return name;
	}

	public Set<? extends AgglomerationModel> getAgglomerations() {
		return agglomerations;
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
