package br.pucrio.inf.organic.builtin.strategies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import spirit.core.smells.CodeSmell;
import br.pucrio.inf.organic.builtin.agglomerationModels.IntraMethodAgglomeration;
import br.pucrio.inf.organic.extensionPoints.AgglomerationStrategy;
import br.pucrio.inf.organic.model.Project;

/**
 * Strategy to detect Intra-method agglomerations.
 * @author Willian
 *
 */
public class IntraMethodStrategy extends AgglomerationStrategy<IntraMethodAgglomeration> {
	
	private final int threshold;

	/**
	 * Returns a detector that uses an Intra-method strategy.
	 * @param project: subject project
	 */
	public IntraMethodStrategy(Project project) {
		super(project);
		threshold = Thresholds.getIntraMethod();
	}

	/**
	 * finds agglomerations using an Intra-method strategy.
	 */
	public Set<IntraMethodAgglomeration> findAgglomerations() {
		Set<IntraMethodAgglomeration> agglomerations = new HashSet<IntraMethodAgglomeration>();
		for (Entry<MethodDeclaration, ArrayList<CodeSmell>> anomalies : buildAnomaliesPerMethod().entrySet()) {
			if (anomalies.getValue().size() > threshold) {
				agglomerations.add(new IntraMethodAgglomeration(anomalies.getKey(), anomalies.getValue()));
			}
		}
		
		return agglomerations;
	}

	/**
	 * Builds a dictionary of code anomalies where:
	 *  (i) the key is the anomaly name and (ii) the value is a list of instances.
	 * @return HashMap (name->instances)
	 */
	private HashMap<MethodDeclaration, ArrayList<CodeSmell>> buildAnomaliesPerMethod() {
		HashMap<MethodDeclaration, ArrayList<CodeSmell>> anomaliesPerMethod = new HashMap<MethodDeclaration, ArrayList<CodeSmell>>();
		
		for (CodeSmell codeAnomaly : spiritAdapter.getMethodAnomalies()) {
			MethodDeclaration method = (MethodDeclaration)codeAnomaly.getElement();
			ArrayList<CodeSmell> codeAnomalies = anomaliesPerMethod.get(method);
			if (codeAnomalies == null) {
				codeAnomalies = new ArrayList<CodeSmell>();
				anomaliesPerMethod.put(method, codeAnomalies);
			}
			codeAnomalies.add(codeAnomaly);
		}
		
		return anomaliesPerMethod;
	}
}
