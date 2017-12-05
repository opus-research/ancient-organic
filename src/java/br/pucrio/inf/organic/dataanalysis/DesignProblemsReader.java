package br.pucrio.inf.organic.dataanalysis;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

public class DesignProblemsReader {
	
	public static HashMap<String, ArrayList<DesignProblem>> readHealthWatcherDesignProblems() {
		HashMap<String, ArrayList<DesignProblem>> results = new HashMap<String, ArrayList<DesignProblem>>();

		InputStream fis = null;
		BufferedReader br = null;
		String line;

		try {
			fis = new FileInputStream("/home/willian/Repositories/organic/auxiliar/archtecturalProblemsHW.csv");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));

		try {
			while ((line = br.readLine()) != null) {
				String[] celulas = line.split(";");				
				
				if (celulas.length >= 3 && celulas[2] != null && !celulas[2].trim().isEmpty()) {
					addDesignProblem(results, celulas, 2, "connectorEnvy");
				}
				if (celulas.length >= 4 && celulas[3] != null && !celulas[3].trim().isEmpty()) {
					addDesignProblem(results, celulas, 3, "ambiguousInterface");
				}
				if (celulas.length >= 5 && celulas[4] != null && !celulas[4].trim().isEmpty()) {
					addDesignProblem(results, celulas, 4, "extraneousAdjacentConnector");
				}
				if (celulas.length >= 6 && celulas[5] != null && !celulas[5].trim().isEmpty()) {
					addDesignProblem(results, celulas, 5, "violations");
				}
			}

			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		br = null;
		fis = null;
		return results;
	}

	private static void addDesignProblem( HashMap<String, ArrayList<DesignProblem>> results, String[] celulas, int index, String designProblemName) {
		DesignProblem designProblem = new DesignProblem();					
		designProblem.type = celulas[0];
		designProblem.codeElement = celulas[1];
		designProblem.designProblemName = designProblemName;
		designProblem.numberOfInstances = Integer.parseInt(celulas[index]);
							
		ArrayList<DesignProblem> problems = results.get(designProblem.codeElement);
		if (problems == null) {
			problems = new ArrayList<DesignProblem>();
			results.put(designProblem.codeElement, problems);
		}
		problems.add(designProblem);
	}

}
