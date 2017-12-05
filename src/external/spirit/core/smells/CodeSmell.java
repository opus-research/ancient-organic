package spirit.core.smells;

import java.util.Set;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import spirit.core.design.DesignFlaw;
import spirit.metrics.storage.NodeMetrics;
import br.pucrio.inf.organic.model.Component;
import br.pucrio.inf.organic.projectBuilders.ComponentVisitor;

public abstract class CodeSmell extends DesignFlaw {
	private String kindOfSmellName;
	protected ASTNode element;
	protected NodeMetrics node;
	private Set<IJavaElement> referencesToAnomalousElement;

	public CodeSmell(String kindOfSmellName) {
		this.kindOfSmellName = kindOfSmellName;
	}

	public ASTNode getElement() {
		return element;
	}

	public String getKindOfSmellName() {
		return kindOfSmellName;
	}

	public String getMainClassName() {
		// La tengo que devolver con puntos en lugar de / como esta en la
		// hashtable
		// Un caso especial es si es inner class ya que el location es dentro de
		// su padre
		// System.out.println(getMainClass().resolveBinding().getQualifiedName());
		return getMainClass().resolveBinding().getQualifiedName();
	}

	public abstract TypeDeclaration getMainClass();

	/**
	 * Get all the affected classes by the code smell. This method depends on
	 * the kind of smell
	 * 
	 * @return a set of strings that contains the relative paths to the classes.
	 *         e.g. "spirit.core.smells.CodeSmell"
	 */
	public abstract Set<String> getAffectedClasses();

	public String getElementName() {
		if (element instanceof TypeDeclaration) {
			return ((TypeDeclaration) (element)).getName().toString();
		} else if (element instanceof MethodDeclaration) {
			return ((TypeDeclaration) ((MethodDeclaration) (element))
					.getParent()).getName()
					+ "."
					+ ((MethodDeclaration) (element)).getName().toString();
		}
		return element.toString();
	}

	/**
	 * get the line of a method in a java file
	 */
	public int getLine() {
		IJavaElement javaElement = this.getMainClass().resolveBinding()
				.getJavaElement();
		ICompilationUnit cu = (ICompilationUnit) javaElement
				.getAncestor(IJavaElement.COMPILATION_UNIT);

		return (this.getLineNumFromOffset(cu, element.getStartPosition()));// this.getiMethod().getSourceRange().getOffset()));

	}

	/**
	 * get the line of a method in a java file, given the offset
	 */
	private int getLineNumFromOffset(ICompilationUnit cUnit, int offSet) {
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
	
	@Override
	public String toString() {
		return getKindOfSmellName() + " -> " + getElementName();
	}
	
	
	public abstract String getDescription();
	
	public String getDetailedDescription() {
		String description = getKindOfSmellName() + " affecting ";
		
		if (getElementName().contains(".")) {
			String[] names = getElementName().split("\\.");
			description += " the " + names[1] + " method of the " + names[0] + " class";
		} else {
			description += " the " + getElementName() + " class";
		}
		
		description += "\n\nAnomaly Description: \n\n" + getDescription();
		
		return description;
	}
	
	public void setReferencesToAnomalousElement(Set<IJavaElement> refs) {
		referencesToAnomalousElement = refs;
	}

	public Set<IJavaElement> getReferencesToAnomalousElement() {
		return referencesToAnomalousElement;
	}

	/**
	 * Returns the element name with package/component
	 * @return
	 */
	public String getFullElementName() {
		Component comp = (Component) this.getMainClass().getProperty(ComponentVisitor.ARCHITECTURAL_COMPONENT);
		return comp.getName() + "." + this.getElementName();
	}
}
