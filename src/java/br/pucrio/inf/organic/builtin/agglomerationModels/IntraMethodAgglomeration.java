package br.pucrio.inf.organic.builtin.agglomerationModels;

import java.util.Collection;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import br.pucrio.inf.organic.extensionPoints.AgglomerationModel;
import spirit.core.smells.CodeSmell;

/**
 * Agglomeration model for Intra-method agglomerations
 * @author Willian
 *
 */
public class IntraMethodAgglomeration extends AgglomerationModel {
	
	public static final String NAME = "Intra-method";
	
	private final MethodDeclaration affectedMethod;
	
	/**
	 * Returns an Intra-method agglomeration
	 * @param affectedMethod: MethodDeclaration that encloses the agglomeration
	 * @param codeAnomalies: code anomalies in the affected Method 
	 */
	public IntraMethodAgglomeration(MethodDeclaration affectedMethod, Collection<CodeSmell> codeAnomalies) {
		super(codeAnomalies);
		this.affectedMethod = affectedMethod;
	}
	
	/**
	 * Returns affected method in this agglomeration
	 * @return MethodDeclaration
	 */
	public MethodDeclaration getAffectedMethod() {
		return affectedMethod;
	}
	
	@Override
	public String toString() {
		return methodFullName();
	}
	
	private String methodFullName() {
		return ((TypeDeclaration)getAffectedMethod().getParent()).getName() + "." + getAffectedMethod().getName();
	}	
	
	@Override
	public String getUniqueID() {
		return NAME + "-" + methodFullName();
	}	

	@Override
	public String getDescription() {
		return "Intra-method agglomeration affecting the " + getAffectedMethod().getName() + " method.\n"; 
	}	
}
