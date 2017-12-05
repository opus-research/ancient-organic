package br.pucrio.inf.organic.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;

import spirit.core.smells.CodeSmell;
import spirit.core.smells.detectors.BrainClassDetector;
import spirit.core.smells.detectors.BrainMethodDetector;
import spirit.core.smells.detectors.CodeSmellDetector;
import spirit.core.smells.detectors.DataClassDetector;
import spirit.core.smells.detectors.DispersedCouplingDetector;
import spirit.core.smells.detectors.FeatureEnvyDetector;
import spirit.core.smells.detectors.GodClassDetector;
import spirit.core.smells.detectors.IntensiveCouplingDetector;
import spirit.core.smells.detectors.RefusedParentBequestDetector;
import spirit.core.smells.detectors.ShotgunSurgeryDetector;
import spirit.core.smells.detectors.TraditionBreakerDetector;
import spirit.metrics.analizer.SpiritVisitor;
import spirit.metrics.storage.ClassMetrics;
import spirit.metrics.storage.MethodMetrics;

/**
 * Adapter to the JSpirit api.
 * @author Willian
 */
public class JSpiritAdapter {

	private static final Vector<CodeSmellDetector> methodDetectors = new Vector<CodeSmellDetector>();
	private static final Vector<CodeSmellDetector> classDetectors = new Vector<CodeSmellDetector>();
	private Set<CodeSmell> methodSmells = new HashSet<CodeSmell>();
	private Set<CodeSmell> classSmells = new HashSet<CodeSmell>();
	
	private Map<TypeDeclaration, ArrayList<CodeSmell>> smellsPerClass = new HashMap<TypeDeclaration, ArrayList<CodeSmell>>();
	private Map<TypeDeclaration, ArrayList<CodeSmell>> methodSmellsPerClass = new HashMap<TypeDeclaration, ArrayList<CodeSmell>>();
	private Map<MethodDeclaration, ArrayList<CodeSmell>> smellsPerMethod = new HashMap<MethodDeclaration, ArrayList<CodeSmell>>();
	
	private final SpiritVisitor spiritVisitor;

	/**
	 * Initializes dictionaries with code anomaly detectors
	 */
	static {
		methodDetectors.add(new BrainMethodDetector());
		methodDetectors.add(new FeatureEnvyDetector());
		methodDetectors.add(new DispersedCouplingDetector());
		methodDetectors.add(new IntensiveCouplingDetector());
		methodDetectors.add(new ShotgunSurgeryDetector());
		classDetectors.add(new GodClassDetector());
		classDetectors.add(new BrainClassDetector());
		classDetectors.add(new DataClassDetector());
		classDetectors.add(new RefusedParentBequestDetector());
		classDetectors.add(new TraditionBreakerDetector());
	}
	
	/**
	 * Creates a JSpirit adapter for a SpiritVisitor
	 * @param spiritVisitor
	 */
	public JSpiritAdapter(SpiritVisitor spiritVisitor) {
		this.getMethodAnomalies().addAll(methodSmells);
		this.getClassSmells().addAll(classSmells);
		this.spiritVisitor = spiritVisitor;
	}

	/**
	 * Returns all method smells in the project
	 */
	public Set<CodeSmell> getMethodAnomalies() {
		return methodSmells;
	}

	/**
	 * Returns all class smells in the project
	 */
	public Set<CodeSmell> getClassSmells() {
		return classSmells;
	}
	
	/**
	 * Returns only class smells of a TypeDeclaration
	 */
	public ArrayList<CodeSmell> getSmellsOfClass(TypeDeclaration class_) {
		ArrayList<CodeSmell> smells = smellsPerClass.get(class_);
		return smells != null ? smells : new ArrayList<CodeSmell>();
	}
	
	/**
	 * Returns all smells of a TypeDeclaration, including class smells and method smells
	 */
	public ArrayList<CodeSmell> getAllSmellsOfClass(TypeDeclaration class_) {
		ArrayList<CodeSmell> smells = smellsPerClass.get(class_);
		ArrayList<CodeSmell> methodSmells = methodSmellsPerClass.get(class_);
		
		ArrayList<CodeSmell> allSmells = new ArrayList<CodeSmell>();
		if (smells != null)
			allSmells.addAll(smells);
		if (methodSmells != null)
			allSmells.addAll(methodSmells);
		
		return allSmells;
	}	

	/**
	 * Calls Jspirit to detect code anomalies. After execution, code anomalies
	 * will be available from the methods getClassSmells and getMethodSmells
	 */
	public void findCodeAnomalies() {
		spiritVisitor.executeMetrics();
		spiritVisitor.calculateAditionalMetrics();
		
		methodSmells = new HashSet<CodeSmell>();
		classSmells = new HashSet<CodeSmell>();
		
		
		for (ClassMetrics classMetrics : spiritVisitor.getLClassesMetrics()) {
			ArrayList<CodeSmell> methodSmellsOfClass = new ArrayList<CodeSmell>();
			
			for (MethodMetrics methodMetrics : classMetrics.getMethodsMetrics()) {
				for (CodeSmellDetector methodDetector : methodDetectors) {
					if (methodDetector.codeSmellVerify(methodMetrics)) {
						CodeSmell codeSmellDetected = methodDetector.codeSmellDetected(methodMetrics);
						methodSmells.add(codeSmellDetected);
						methodSmellsOfClass.add(codeSmellDetected);
					}
				}
			}
			
			if (methodSmellsOfClass.size() > 0)
				methodSmellsPerClass.put(classMetrics.getDeclaration(), methodSmellsOfClass);
			
			for (CodeSmellDetector classDetector : classDetectors) {
				if (classDetector.codeSmellVerify(classMetrics)) {
					CodeSmell codeSmellDectected = classDetector.codeSmellDetected(classMetrics);
					classSmells.add(codeSmellDectected);
					ArrayList<CodeSmell> smellsOfClass = smellsPerClass.get(codeSmellDectected.getMainClass());
					if (smellsOfClass == null) {
						smellsOfClass = new ArrayList<CodeSmell>();
						smellsPerClass.put(codeSmellDectected.getMainClass(), smellsOfClass);
					}
					smellsOfClass.add(codeSmellDectected);
				}
			}
		}
		
		IJavaSearchScope searchScope = SearchEngine.createWorkspaceScope();
		
		for (CodeSmell smell : classSmells) {
			Set<IJavaElement> refs = findReferencesOfAnomalousElement(searchScope, smell);
			smell.setReferencesToAnomalousElement(refs);
		}
		
		for (CodeSmell smell : methodSmells) {
			Set<IJavaElement> refs = findReferencesOfAnomalousElement(searchScope, smell);
			smell.setReferencesToAnomalousElement(refs);
		}
	}
	
	private Set<IJavaElement> findReferencesOfAnomalousElement(
			IJavaSearchScope searchScope, CodeSmell smell) {
		IJavaElement element = null;
		if (smell.getElement() instanceof MethodDeclaration) {
			element = ((MethodDeclaration) smell.getElement()).resolveBinding()
					.getJavaElement();
		} else if (smell.getElement() instanceof TypeDeclaration) {
			element = ((TypeDeclaration) smell.getElement()).resolveBinding()
					.getJavaElement();
		}
		SearchPattern referencesPattern = SearchPattern.createPattern(element,
				IJavaSearchConstants.REFERENCES);
		final Set<IJavaElement> foundElements = new HashSet<IJavaElement>();
		SearchRequestor requestor = new SearchRequestor() {
			@Override
			public void acceptSearchMatch(SearchMatch sm) throws CoreException {
				foundElements.add((IJavaElement) sm.getElement());
			}
		};

		SearchEngine engine = new SearchEngine();
		SearchParticipant[] participants = new SearchParticipant[] { SearchEngine
				.getDefaultSearchParticipant() };
		try {
			engine.search(referencesPattern, participants, searchScope,
					requestor, null);
		} catch (CoreException e) {
			System.err.println(e);
		}

		return foundElements;
	}	
}