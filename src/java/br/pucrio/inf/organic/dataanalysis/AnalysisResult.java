package br.pucrio.inf.organic.dataanalysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import br.pucrio.inf.organic.extensionPoints.AgglomerationModel;

public class AnalysisResult {
	
	public HashSet<DesignProblem> allDesignProblems = new HashSet<DesignProblem>();
	public HashSet<DesignProblem> designProblemsRelatedToAgglomerations = new HashSet<DesignProblem>();
	public HashMap<AgglomerationModel, ArrayList<DesignProblem>> designProblemsPerAgglomeration = new HashMap<AgglomerationModel, ArrayList<DesignProblem>>();

	//History Information
	
	public HashSet<AgglomerationModel> growingAgglomerationsRelatedToDP = new HashSet<AgglomerationModel>();
	public HashSet<DesignProblem> designProblemsInLateAgglomerations = new HashSet<DesignProblem>();
	
	public void calculateHistoryInformation() {
		growingAgglomerationsRelatedToDP = new HashSet<AgglomerationModel>();
		designProblemsInLateAgglomerations = new HashSet<DesignProblem>();
		
		for (Entry<AgglomerationModel, ArrayList<DesignProblem>> e : designProblemsPerAgglomeration.entrySet()) {
			
			//aqui identificamos problemas de projeto associados com agglomeracoes que surgiram somente na ultima versao
			if (e.getKey().getHistory().size() == 0) {
				designProblemsInLateAgglomerations.addAll(e.getValue());
			}
			
			if (e.getKey().isGrowing()) {
				growingAgglomerationsRelatedToDP.add(e.getKey());
			}
		}
	}
	
	public void printStats() {
		System.out.print("Number of Design Problems: ");
		System.out.println(allDesignProblems.size());
		
		System.out.print("Number of Design Problems Related to Agglomerations: ");
		System.out.println(designProblemsRelatedToAgglomerations.size());
		
		System.out.print("Number of Growing Agglomerations Related to Design Problems: ");
		System.out.println(growingAgglomerationsRelatedToDP.size());
		
		System.out.print("Number of Design Problems Related to Late Agglomerations: ");
		System.out.println(designProblemsInLateAgglomerations.size());
	}
}
