package br.pucrio.inf.organic.builtin.strategies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.jdt.core.dom.TypeDeclaration;

import spirit.core.smells.CodeSmell;
import br.pucrio.inf.organic.builtin.agglomerationModels.IntraComponentAgglomeration;
import br.pucrio.inf.organic.extensionPoints.AgglomerationStrategy;
import br.pucrio.inf.organic.model.Component;
import br.pucrio.inf.organic.model.Project;

/**
 * Strategy to detect Intra-component agglomerations
 * @author Willian
 *
 */
public class IntraComponentStrategy extends AgglomerationStrategy<IntraComponentAgglomeration> {
	
	private int threshold;

	/**
	 * Returns a detector that uses a Intra-component strategy 
	 * @param project
	 */
	public IntraComponentStrategy(Project project) {
		super(project);
		threshold = Thresholds.getIntraComponent();
	}
	
	/**
	 * finds agglomerations using an Intra-component strategy.
	 */
	public Set<IntraComponentAgglomeration> findAgglomerations() {
		Set<IntraComponentAgglomeration> agglomerations = new HashSet<IntraComponentAgglomeration>();
		
		for (Component component : project.getComponents()) {
			HashMap<String, ArrayList<CodeSmell>> typesToAnomalies = computeAnomaliesInComponent(component);
			
			for (Entry<String, ArrayList<CodeSmell>> typeAnomalies : typesToAnomalies.entrySet()) {
				if (typeAnomalies.getValue().size() > threshold)
					agglomerations.add(new IntraComponentAgglomeration(component, typeAnomalies.getKey(), typeAnomalies.getValue()));
			}
		}
		return agglomerations;
	}


	/**
	 * Builds a dictionary of code anomalies where:
	 *  (i) the key is the anomaly name and (ii) the value is a list of instances.
	 * @param component
	 * @return HashMap (name->instances) of code anomalies
	 */
	private HashMap<String, ArrayList<CodeSmell>> computeAnomaliesInComponent(Component component) {
		HashMap<String, ArrayList<CodeSmell>> typesToAnomalies = new HashMap<String, ArrayList<CodeSmell>>();
		for (TypeDeclaration class_ : component.getClasses()) {
			for (CodeSmell codeSmell : spiritAdapter.getAllSmellsOfClass(class_)) {
				ArrayList<CodeSmell> codeSmells = typesToAnomalies.get(codeSmell.getKindOfSmellName());
				if (codeSmells == null) {
					codeSmells = new ArrayList<CodeSmell>();
					typesToAnomalies.put(codeSmell.getKindOfSmellName(), codeSmells);
				}
				codeSmells.add(codeSmell);
			}
		}
		return typesToAnomalies;
	}
}
