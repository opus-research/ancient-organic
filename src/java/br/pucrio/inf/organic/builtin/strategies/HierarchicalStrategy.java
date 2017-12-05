package br.pucrio.inf.organic.builtin.strategies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import spirit.core.smells.CodeSmell;
import br.pucrio.inf.organic.builtin.agglomerationModels.HierarchicalAgglomeration;
import br.pucrio.inf.organic.extensionPoints.AgglomerationStrategy;
import br.pucrio.inf.organic.model.Project;

/**
 * Strategy to detect Hierarchical agglomerations.
 * @author Willian
 *
 */
public class HierarchicalStrategy extends AgglomerationStrategy<HierarchicalAgglomeration> {
	
	private Map<TypeDeclaration, ArrayList<TypeDeclaration>> parentToChildren = new HashMap<TypeDeclaration, ArrayList<TypeDeclaration>>();  
	private Set<TypeDeclaration> areSubTree = new HashSet<TypeDeclaration>();
	private int threshold;
	
	/**
	 * Returns a detector that uses a hierarchical strategy.
	 * @param project: subject project
	 */
	public HierarchicalStrategy(Project project) {
		super(project);
		threshold = Thresholds.getHierarchical();
	}
	
	/**
	 * finds agglomerations using a hierarchical strategy.
	 */
	public Set<HierarchicalAgglomeration> findAgglomerations() {
		Set<HierarchicalAgglomeration> agglomerations = new HashSet<HierarchicalAgglomeration>();
		
		buildHierarchies();
		
		for (TypeDeclaration class_ : parentToChildren.keySet()) {
			if (!areSubTree.contains(class_)) { 
				HashMap<String, ArrayList<CodeSmell>> typesToInstances = new HashMap<String, ArrayList<CodeSmell>>();
				computeSmellsInHierarchy(class_, typesToInstances);
				
				for (Entry<String, ArrayList<CodeSmell>> typeToInstances : typesToInstances.entrySet()) {
					if (typeToInstances.getValue().size() > threshold) {
						agglomerations.add(new HierarchicalAgglomeration(class_, typeToInstances.getKey(), typeToInstances.getValue()));
					}
				}
			}
		}
		
		return agglomerations;
	}

	/**
	 * Computes the smells in a tree where class_ is the root. 
	 * @param class_
	 * @param typesToAnomalies
	 */
	private void computeSmellsInHierarchy(TypeDeclaration class_, HashMap<String, ArrayList<CodeSmell>> typesToAnomalies) {
		computeSmellsInClass(class_, typesToAnomalies);
		
		ArrayList<TypeDeclaration> children = parentToChildren.get(class_);
		if (children != null) {
			for (TypeDeclaration child : children) {
				computeSmellsInHierarchy(child, typesToAnomalies);
			}
		}
	}

	/**
	 * Finds code anomalies for a given class (TypeDeclaration) and put in the typesToAnomalies dictionary.
	 * @param class_
	 * @param typesToAnomalies
	 */
	private void computeSmellsInClass(TypeDeclaration class_, HashMap<String, ArrayList<CodeSmell>> typesToAnomalies) {
		ArrayList<CodeSmell> smellsOfClass = spiritAdapter.getAllSmellsOfClass(class_);
		for (CodeSmell codeSmell : smellsOfClass) {
			ArrayList<CodeSmell> codeSmells = typesToAnomalies.get(codeSmell.getKindOfSmellName());
			if (codeSmells == null) {
				codeSmells = new ArrayList<CodeSmell>();
				typesToAnomalies.put(codeSmell.getKindOfSmellName(), codeSmells);
			}
			codeSmells.add(codeSmell);
		}
	}
	
	/**
	 * Builds a dictionary of trees (parentToChildren) in the evaluated project.
	 * Each key in the parentToChildren is the root of a given (sub-)tree.
	 * Elements that have parents are included in the areSubTree set.
	 */
	private void buildHierarchies() {
		Map<String, TypeDeclaration> classes = project.getClasses();
		for (Entry<String, TypeDeclaration> pair : classes.entrySet()) {
			Type superclassType = pair.getValue().getSuperclassType();
			if (superclassType != null) {
				TypeDeclaration superClass = classes.get(superclassType.resolveBinding().getQualifiedName());
				if (superClass != null) {
					ArrayList<TypeDeclaration> children = parentToChildren.get(superClass);
					if (children == null) {
						children = new ArrayList<TypeDeclaration>();
						parentToChildren.put(superClass, children);
					}
					children.add(pair.getValue());
					areSubTree.add(pair.getValue());
				}
			}
		}
	}
}
