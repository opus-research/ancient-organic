package br.pucrio.inf.organic.dataanalysis;

import java.util.ArrayList;
import java.util.HashMap;

import spirit.core.smells.CodeSmell;
import br.pucrio.inf.organic.extensionPoints.AgglomerationModel;
import br.pucrio.inf.organic.extensions.ui.agglomeration.AgglomerationTypeResults;
import br.pucrio.inf.organic.extensions.ui.agglomeration.ProjectResults;

public class AnalysisExecutor {
		
	public static AnalysisResult performHealthWatcherAnalysis(ProjectResults project) {
		AnalysisResult result = new AnalysisResult();
		
		HashMap<String,ArrayList<DesignProblem>> designProblems = DesignProblemsReader.readHealthWatcherDesignProblems();
		
		for (ArrayList<DesignProblem> dps : designProblems.values()) {
			result.allDesignProblems.addAll(dps);
		}
		
		for (AgglomerationTypeResults aType : project.getAgglomerationTypes()) {
			for (AgglomerationModel ag : aType.getAgglomerations()) {
				ArrayList<DesignProblem> relatedDesignProblems = new ArrayList<DesignProblem>();
				for (CodeSmell cm : ag.getCodeAnomaliesSortedByName()) {
					 ArrayList<DesignProblem> codeSmellDesignProblems = designProblems.get(cm.getFullElementName());
					 if (codeSmellDesignProblems != null) {
						 relatedDesignProblems.addAll(codeSmellDesignProblems);
					 }
				}
				if (relatedDesignProblems.size() > 0) {
					result.designProblemsPerAgglomeration.put(ag, relatedDesignProblems);
					result.designProblemsRelatedToAgglomerations.addAll(relatedDesignProblems);
				}
			}
		}		
		
		return result;
	}

}
