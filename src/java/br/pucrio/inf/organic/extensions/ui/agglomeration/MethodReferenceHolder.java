package br.pucrio.inf.organic.extensions.ui.agglomeration;

import org.eclipse.jdt.core.BindingKey;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.core.ResolvedSourceMethod;
import org.eclipse.jdt.internal.core.SourceMapper;
import org.eclipse.jdt.internal.core.util.BindingKeyResolver;

public class MethodReferenceHolder extends ReferenceHolder<ResolvedSourceMethod> {

	public MethodReferenceHolder(ResolvedSourceMethod javaElement) {
		super(javaElement);
	}

	@Override
	public String getName() {
		return getPackageName(getJavaElement()) 
				 + "." + getJavaElement().getParent().getElementName()
				 + "." + getJavaElement().getElementName();
	}
	
	@Override
	public int getLineNumber() {
		ICompilationUnit cu = (ICompilationUnit) getJavaElement().getParent().getParent();
		
		try {
			return getLineNumFromOffset(cu, getJavaElement().getSourceRange().getOffset());
		} catch (JavaModelException e) {
			System.err.println(e);
		}
		return 0;
	}
}
