package br.pucrio.inf.organic.builtin.agglomerationModels;

import java.util.Collection;
import java.util.Set;

import br.pucrio.inf.organic.extensionPoints.AgglomerationModel;
import br.pucrio.inf.organic.model.Component;
import br.pucrio.inf.organic.model.Concern;
import spirit.core.smells.CodeSmell;

/**
 * Agglomeration model for concern-overload agglomerations
 * @author Willian
 *
 */
public class ConcernOverloadAgglomeration extends AgglomerationModel {

	public static final String NAME = "Concern-overload";
	
	private final Component component;
	private final Concern commonConcern;
	private final Set<Concern> otherConcerns;

	/**
	 * Returns a Concern-overload agglomeration
	 * @param component that encloses the agglomeration 
	 * @param set 
	 * @param codeAnomalies: anomalous code elements composing the agglomeration
	 */
	public ConcernOverloadAgglomeration(Component component, Concern commonConcern, Collection<CodeSmell> codeAnomalies, Set<Concern> otherConcerns) {
		super(codeAnomalies);
		this.component = component;
		this.commonConcern = commonConcern;
		this.otherConcerns = otherConcerns;
	}

	/**
	 * Returns the components that encloses the agglomeration
	 * @return Component
	 */
	public Component getComponent() {
		return component;
	}
	
	@Override
	public String toString() {
		return component.getName() + " -> " + getOtherConcerns();
	}
	
	public String getUniqueID() {
		return NAME + "-" + component.getName() + "->" + getOtherConcerns();
	}	

	@Override
	public String getDescription() {
		return "Concern-overload agglomeration affecting the " + component.getName() + " component";
	}

	public Set<Concern> getOtherConcerns() {
		return otherConcerns;
	}

	public Concern getCommonConcern() {
		return commonConcern;
	}
}
