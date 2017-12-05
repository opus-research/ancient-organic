package br.pucrio.inf.organic.builtin.agglomerationModels;

import java.util.Collection;

import org.eclipse.jdt.core.dom.TypeDeclaration;

import spirit.core.smells.CodeSmell;
import br.pucrio.inf.organic.extensionPoints.AgglomerationModel;

public class IntraClassAgglomeration extends AgglomerationModel {

	public static final String NAME = "Intra-class";
	
	private final TypeDeclaration enclosingClass;

	public IntraClassAgglomeration(TypeDeclaration enclosingClass, Collection<CodeSmell> codeAnomalies) {
		super(codeAnomalies);
		this.enclosingClass = enclosingClass;
	}
	
	@Override
	public String toString() {
		return getEnclosingClass().getName().getFullyQualifiedName();
	}
	
	@Override
	public String getUniqueID() {
		return NAME + "-" + enclosingClass.getName().getFullyQualifiedName();
	}	
	
	@Override
	public String getDescription() {
		return "Intra-class agglomeration affecting the " + getEnclosingClass().getName()
				+ " class.\n";
	}

	public TypeDeclaration getEnclosingClass() {
		return enclosingClass;
	}	
}
