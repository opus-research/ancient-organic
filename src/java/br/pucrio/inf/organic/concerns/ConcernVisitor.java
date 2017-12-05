package br.pucrio.inf.organic.concerns;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import br.pucrio.inf.organic.model.Concern;

/**
 * Visitor responsible for decorating TypeDeclarations with it's implemented concerns.
 * @author Willian
 *
 */
public class ConcernVisitor extends ASTVisitor {
	
	private Concern concern;

	public ConcernVisitor(Concern concern) {
		this.concern = concern;
	}
	
	@Override
	public boolean visit(TypeDeclaration node) {
		concern.addClass(node);		
		return super.visit(node);
	}

}
