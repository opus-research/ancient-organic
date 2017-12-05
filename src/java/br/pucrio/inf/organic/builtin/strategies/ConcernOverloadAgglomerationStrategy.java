package br.pucrio.inf.organic.builtin.strategies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.jdt.core.dom.TypeDeclaration;

import spirit.core.smells.CodeSmell;
import br.pucrio.inf.organic.builtin.agglomerationModels.ConcernOverloadAgglomeration;
import br.pucrio.inf.organic.builtin.agglomerationModels.ConcernOverloadCandidate;
import br.pucrio.inf.organic.extensionPoints.AgglomerationStrategy;
import br.pucrio.inf.organic.model.Component;
import br.pucrio.inf.organic.model.Concern;
import br.pucrio.inf.organic.model.Project;

/**
 * Strategy to detect Concern-overload agglomerations
 * @author Willian
 *
 */
public class ConcernOverloadAgglomerationStrategy extends AgglomerationStrategy<ConcernOverloadAgglomeration> {
	
	private int threshold;
	private int concernsThreshold;

	/**
	 * Returns a detector that uses a Concern-overload strategy 
	 * @param project
	 */
	public ConcernOverloadAgglomerationStrategy(Project project) {
		super(project);
		threshold = Thresholds.getConcernOverloadForAnomalies();
		concernsThreshold = Thresholds.getConcernOverloadForConcerns();
	}
	
	/**
	 * finds agglomerations using an Concern-overload strategy.
	 */
	public Set<ConcernOverloadAgglomeration> findAgglomerations() {
		Set<ConcernOverloadAgglomeration> agglomerations = new HashSet<ConcernOverloadAgglomeration>();
		
		for (Component component : project.getComponents()) {
			HashMap<TypeDeclaration, ArrayList<CodeSmell>> typesToAnomalies = computeAnomaliesInComponent(component);
			HashMap<Concern, ConcernOverloadCandidate> concernsToClasses = computeConcernsInAnomalousClasses(typesToAnomalies);
			for (ConcernOverloadCandidate candidate : concernsToClasses.values()) {
				if (candidate.getAllAnomalies().size() > threshold
						&& candidate.getOtherConcerns().size() > concernsThreshold) {
					agglomerations.add(new ConcernOverloadAgglomeration(component, candidate.getConcern(), candidate.getAllAnomalies(), candidate.getOtherConcerns()));
				}
			}
		}
		return agglomerations;
	}
	
	private HashMap<Concern, ConcernOverloadCandidate> computeConcernsInAnomalousClasses(HashMap<TypeDeclaration, ArrayList<CodeSmell>> typesToAnomalies) {
		HashMap<Concern, ConcernOverloadCandidate> allConcerns = new HashMap<Concern, ConcernOverloadCandidate>(); 
		for (Entry<TypeDeclaration, ArrayList<CodeSmell>> anomalousClass : typesToAnomalies.entrySet()) {
			List<Concern> concernsOfClass = Concern.getConcernsOfClass(anomalousClass.getKey());
			for (Concern concern : concernsOfClass) {
				ConcernOverloadCandidate candidate = allConcerns.get(concern);
				if (candidate == null) {
					candidate = new ConcernOverloadCandidate(concern);
					allConcerns.put(concern, candidate);
				}
				candidate.addAnomalousClass(anomalousClass.getKey(), anomalousClass.getValue());
			}
		}
		return allConcerns;
	}

	/**
	 * Builds a dictionary of code anomalies where:
	 *  (i) the key is the anomaly name and (ii) the value is a list of instances.
	 * @param component
	 * @return HashMap (name->instances) of code anomalies
	 */
	private HashMap<TypeDeclaration, ArrayList<CodeSmell>> computeAnomaliesInComponent(Component component) {
		HashMap<TypeDeclaration, ArrayList<CodeSmell>> typesToAnomalies = new HashMap<TypeDeclaration, ArrayList<CodeSmell>>();
		for (TypeDeclaration class_ : component.getClasses()) {
			for (CodeSmell codeSmell : spiritAdapter.getAllSmellsOfClass(class_)) {
				ArrayList<CodeSmell> codeSmells = typesToAnomalies.get(codeSmell.getKindOfSmellName());
				if (codeSmells == null) {
					codeSmells = new ArrayList<CodeSmell>();
					typesToAnomalies.put(class_, codeSmells);
				}
				codeSmells.add(codeSmell);
			}
		}
		return typesToAnomalies;
	}	
}
