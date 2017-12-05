package br.pucrio.inf.organic.extensionPoints;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import spirit.core.smells.CodeSmell;
import br.pucrio.inf.organic.history.AgglomerationHistory;

/**
 * Class responsible for defining the common attributes and method for all types of agglomeration.
 * Each new type of agglomeration must inherit from this class.
 * @author Willian
 *
 */
public abstract class AgglomerationModel {

	private final Set<CodeSmell> codeAnomalies = new HashSet<CodeSmell>();
	private final Map<String, AgglomerationHistory> history = new HashMap<String, AgglomerationHistory>();
	private final String anomaliesSummary;
	
	/**
	 * Every agglomeration must have a list of code anomalies
	 * @param codeAnomalies
	 */
	public AgglomerationModel(Collection<CodeSmell> codeAnomalies) {
		this.getCodeAnomalies().addAll(codeAnomalies);		
		
		anomaliesSummary = summarizeAnomalies();
	}

	private String summarizeAnomalies() {
		HashMap<String, String> anomaliesDescription = new HashMap<String, String>();
		for (CodeSmell cs : getCodeAnomalies()) {
			anomaliesDescription.put(cs.getKindOfSmellName(), cs.getDescription());
		}
		StringBuilder builder = new StringBuilder();
		for (String type : anomaliesDescription.keySet()) {
			builder.append("\n\n" + type + " - " + anomaliesDescription.get(type));
		}
		return builder.toString();
	}

	public Set<CodeSmell> getCodeAnomalies() {
		return codeAnomalies;
	}
	
	/**
	 * Returns the list of code anomalies in the agglomeration sorted by name (KindOfSmellName in JSpirit)
	 * @return List
	 */
	public List<CodeSmell> getCodeAnomaliesSortedByName() {
		List<CodeSmell> sortedCodeAnomalies = new ArrayList<CodeSmell>(codeAnomalies);
		
		Collections.sort(sortedCodeAnomalies, new Comparator<CodeSmell>() {
			@Override public int compare(CodeSmell o1, CodeSmell o2) {
				return o1.getKindOfSmellName().compareTo(o2.getKindOfSmellName());
			}});
		
		return sortedCodeAnomalies;
	}
	
	@Override
	public String toString() {
		return super.toString();
	}
	
	/**
	 * Returns the unique ID of this agglomeration
	 * @return
	 */
	public abstract String getUniqueID();
	
	public abstract String getDescription();

	public List<AgglomerationHistory> getHistory() {
		return new ArrayList<AgglomerationHistory>(history.values());
	}

	public void addHistory(String version, AgglomerationHistory agglomerationHistory) {
		history.put(version, agglomerationHistory);
	}
	
	public int getNumberOfAnomalies() {
		return codeAnomalies.size();
	}
	
	public String getAnomaliesSummary() {
		return anomaliesSummary;
	}

	public boolean isGrowing() {
		int numberOfAnomalies = codeAnomalies.size();
		if (history.size() > 0) {
			int[] earlyAnomalies = new int[history.size()];
			for (int nEarlyAnomalies : earlyAnomalies) {
				if (nEarlyAnomalies < numberOfAnomalies)
					return true;
			}
		}
		
		return false;
	}
}
