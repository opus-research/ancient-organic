package br.pucrio.inf.organic.dataanalysis;

public class DesignProblem {
	
	public static String[] archProblemsNames;
	
	public String type;
	public String codeElement;
	public int numberOfInstances;
	public String designProblemName;
	
	static {
		archProblemsNames = new String[9];
		archProblemsNames[0] = "connectorEnvy";                 
		archProblemsNames[1] = "componentConcernOverload";      
		archProblemsNames[2] = "scatteredParasitFunctionality"; 
		archProblemsNames[3] = "unusedInterface";               
		archProblemsNames[4] = "ambiguousInterface";            
		archProblemsNames[5] = "extraneousAdjacentConnector";   
		archProblemsNames[6] = "cyclicDependency";
		archProblemsNames[7] = "violations";
		archProblemsNames[8] = "overusedInterface";
	}
}
