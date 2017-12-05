package br.pucrio.inf.organic.history;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.draw2d.geometry.Ray;

public class AgglomerationHistory {

	private final List<String> anomalies = new ArrayList<String>();
	private final String uniqueID;
	private final String version;
	private final String description;
	
	public AgglomerationHistory(String version, String uniqueID, String description, String rawAnomalies) {
		this.version = version;
		this.uniqueID = uniqueID;
		this.description = description;
		
		rawAnomalies = rawAnomalies.replaceAll("\\[|\\]", "");
		
		anomalies.addAll(Arrays.asList(rawAnomalies.split(",")));
	}

	public String[] getAnomalies() {
		return anomalies.toArray(new String[0]);
	}

	public String getVersion() {
		return version;
	}
	
	public String getDescription() {
		return description;
	}

	public String getUniqueID() {
		return uniqueID;
	}
	
	@Override
	public String toString() {
		return getVersion();
	}
}