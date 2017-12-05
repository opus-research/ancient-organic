package br.pucrio.inf.organic.builtin.strategies;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.TypeDeclaration;

import spirit.core.smells.CodeSmell;
import br.pucrio.inf.organic.builtin.agglomerationModels.IntraClassAgglomeration;
import br.pucrio.inf.organic.extensionPoints.AgglomerationStrategy;
import br.pucrio.inf.organic.model.Project;

public class IntraClassAgglomerationStrategy extends AgglomerationStrategy<IntraClassAgglomeration> {

	public IntraClassAgglomerationStrategy(Project project) {
		super(project);
	}

	@Override
	public Set<IntraClassAgglomeration> findAgglomerations() {
		HashSet<IntraClassAgglomeration> agglomerations = new HashSet<IntraClassAgglomeration>();
		
		for (TypeDeclaration class_ : project.getClasses().values()) {
			ArrayList<CodeSmell> allSmellsOfClass = project.getSpiritAdapter().getAllSmellsOfClass(class_);
			if (allSmellsOfClass.size() > Thresholds.getIntraClass()) {
				agglomerations.add(new IntraClassAgglomeration(class_, allSmellsOfClass));
			}
		}
		
		return agglomerations;
	}
}
