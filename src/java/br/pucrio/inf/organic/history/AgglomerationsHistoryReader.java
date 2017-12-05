package br.pucrio.inf.organic.history;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import br.pucrio.inf.organic.model.Project;

public class AgglomerationsHistoryReader {

	private static final String ANOMALIES = "Anomalies";
	private static final String DESCRIPTION = "Description";
	private static final String ID = "Id";
	private Project project;
	public static final String FILE_FOLDER = "Organic/History/";

	public AgglomerationsHistoryReader(Project project) {
		this.project = project;
	}

	public void execute() {
		final File  historyFolder = project.getJavaProject().getProject().getFile(FILE_FOLDER).getRawLocation().toFile(); 
		if (historyFolder.exists()) {
			File[] historyFiles = historyFolder.listFiles();
			CSVFormat format = CSVFormat.DEFAULT
										.withDelimiter(';')
										.withHeader(ID, DESCRIPTION, ANOMALIES);
			CSVParser hfParser = null;
			
			for (File hf : historyFiles) {
				try {
					hfParser = CSVParser.parse(hf, Charset.defaultCharset(), format);
					
					String version = hf.getName();
					ProjectHistory projectHistory = new ProjectHistory(version);
					
					for (CSVRecord r : hfParser.getRecords()) {
						AgglomerationHistory agglomerationHistory = new AgglomerationHistory(version, r.get(ID), r.get(DESCRIPTION), r.get(ANOMALIES));
						projectHistory.getAgglomerations().put(r.get(ID), agglomerationHistory);
					}
					
					project.getHistory().add(projectHistory);
					
				} catch (IOException e) {
					System.err.println("Exception while reading history files: " + e);
				} finally {
					try {
						hfParser.close();
					} catch (IOException e) {
						System.err.println("Exception while closing history file: " + e);
					}
				}
			}
		}
	}
}
