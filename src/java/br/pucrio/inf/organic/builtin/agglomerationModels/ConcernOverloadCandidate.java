package br.pucrio.inf.organic.builtin.agglomerationModels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.TypeDeclaration;

import spirit.core.smells.CodeSmell;
import br.pucrio.inf.organic.model.Concern;

public class ConcernOverloadCandidate {

	private final Concern commonConcern;
	private final HashMap<TypeDeclaration, ArrayList<CodeSmell>> anomalousClasses = new HashMap<TypeDeclaration, ArrayList<CodeSmell>>();
	private final HashSet<Concern> otherConcerns = new HashSet<Concern>();

	public ConcernOverloadCandidate(Concern concern) {
		this.commonConcern = concern;
	}

	public void addAnomalousClass(TypeDeclaration anomalousClass, ArrayList<CodeSmell> anomalies) {
		anomalousClasses.put(anomalousClass, anomalies);
		for (Concern concern : Concern.getConcernsOfClass(anomalousClass)) {
			if(!commonConcern.equals(concern)) {
				otherConcerns.add(concern);
			}
		}
	}
	
	public ArrayList<CodeSmell> getAllAnomalies() {
		ArrayList<CodeSmell> allAnomalies = new ArrayList<CodeSmell>();
		for (ArrayList<CodeSmell> anomalies : anomalousClasses.values()) {
			allAnomalies.addAll(anomalies);
		}
		return allAnomalies;
	}
	
	public Set<Concern> getOtherConcerns() {
		return otherConcerns;
	}

	public Concern getConcern() {
		return commonConcern;
	}
}
