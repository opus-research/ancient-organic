package br.pucrio.inf.organic.builtin.agglomerationModels;

import java.util.Collection;

import br.pucrio.inf.organic.extensionPoints.AgglomerationModel;
import br.pucrio.inf.organic.model.Component;
import spirit.core.smells.CodeSmell;

/**
 * Agglomeration model for Intra-component agglomerations
 * @author Willian
 *
 */
public class IntraComponentAgglomeration extends AgglomerationModel {

	public static final String NAME = "Intra-component";
	
	private final Component component;
	private final String anomalyName;

	/**
	 * Returns an Intra-component agglomeration
	 * @param component that encloses the agglomeration
	 * @param anomalyName: anomaly infecting all elements in the agglomeration 
	 * @param codeAnomalies: anomalous code elements composing the agglomeration
	 */
	public IntraComponentAgglomeration(Component component, String anomalyName, Collection<CodeSmell> codeAnomalies) {
		super(codeAnomalies);
		this.component = component;
		this.anomalyName = anomalyName;
	}

	/**
	 * Returns the components that encloses the agglomeration
	 * @return Component
	 */
	public Component getComponent() {
		return component;
	}

	/**
	 * Returns the anomaly infecting all elements in this agglomeration
	 * @return Anomaly Name
	 */
	public String getAnomalyName() {
		return anomalyName;
	}
	
	@Override
	public String toString() {
		return anomalyName + " -> " + component.getName();
	}
	
	@Override
	public String getUniqueID() {
		return NAME + "-" + anomalyName + "->" + component.getName();
	}	
	
	@Override
	public String getDescription() {
		return "Intra-component agglomeration affecting the " + getComponent().getName()
				+ " component.\n";
	}	
}
