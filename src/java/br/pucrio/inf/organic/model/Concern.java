package br.pucrio.inf.organic.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.TypeDeclaration;

public class Concern {
	
	private static final String ARCHITECTURAL_CONCERNS = "Architectural_Concerns";
	
	private final Set<TypeDeclaration> classes = new HashSet<TypeDeclaration>();
	private final String concernName;
	
	public Concern (String concernName) {
		this.concernName = concernName;
	}
	

	public void addClass(TypeDeclaration class_) {
		classes.add(class_);
		
		@SuppressWarnings("unchecked")
		List<Concern> concerns = (List<Concern>)class_.getProperty(ARCHITECTURAL_CONCERNS);
		if (concerns == null) {
			concerns = new ArrayList<Concern>();
			class_.setProperty(ARCHITECTURAL_CONCERNS, concerns);
		}
		concerns.add(this);
	}


	public String getConcernName() {
		return concernName;
	}


	public static List<Concern> getConcernsOfClass(TypeDeclaration anomalousClass) {
		@SuppressWarnings("unchecked")
		List<Concern> concerns = (List<Concern>)anomalousClass.getProperty(ARCHITECTURAL_CONCERNS);
		if (concerns == null)
			return new ArrayList<Concern>();
		return concerns;
	}
	
	@Override
	public String toString() {
		return concernName;
	}
}
