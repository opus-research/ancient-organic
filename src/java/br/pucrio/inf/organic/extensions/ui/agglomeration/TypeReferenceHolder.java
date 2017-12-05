package br.pucrio.inf.organic.extensions.ui.agglomeration;

import org.eclipse.jdt.internal.core.ResolvedSourceType;

public class TypeReferenceHolder extends ReferenceHolder<ResolvedSourceType> {

	public TypeReferenceHolder(ResolvedSourceType javaElement) {
		super(javaElement);
	}

	@Override
	public String getName() {
		return getPackageName(getJavaElement()) + "." + getJavaElement().getElementName();
	}
	
}
