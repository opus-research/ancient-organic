package br.pucrio.inf.organic.extensions.ui.agglomeration;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.core.PackageFragment;

public abstract class ReferenceHolder<T extends IJavaElement> {

	private T javaElement;
	
	public ReferenceHolder(T javaElement) {
		this.setJavaElement(javaElement);
	}

	public abstract String getName();
	
	public int getLineNumber() {
		return 0;
	}
	
	protected String getPackageName(IJavaElement javaElement) {
		IJavaElement parent = javaElement.getParent();
		
		if (parent == null)
			return null;
		
		if (parent instanceof PackageFragment)
			return parent.getElementName();
		
		return getPackageName(parent);
	}
	
	protected int getLineNumFromOffset(ICompilationUnit cUnit, int offSet) {
		try {
			String source = cUnit.getSource();
			IType type = cUnit.findPrimaryType();
			if (type != null) {
				String sourcetodeclaration = source.substring(0, offSet);
				int lines = 0;
				char[] chars = new char[sourcetodeclaration.length()];
				sourcetodeclaration.getChars(0, sourcetodeclaration.length(),
						chars, 0);
				for (int i = 0; i < chars.length; i++) {
					if (chars[i] == '\n') {
						lines++;
					}
				}
				return lines + 1;
			}
		} catch (JavaModelException jme) {
		}
		return 0;
	}

	public T getJavaElement() {
		return javaElement;
	}

	public void setJavaElement(T javaElement) {
		this.javaElement = javaElement;
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
