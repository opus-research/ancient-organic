package br.pucrio.inf.organic.history;

import java.util.HashMap;
import java.util.Map;

public class ProjectHistory implements Comparable<ProjectHistory> {

	private final Map<String, AgglomerationHistory> agglomerations = new HashMap<String, AgglomerationHistory>();
	private final String version;
	
	public ProjectHistory(String version) {
		this.version = version;
	}

	public Map<String, AgglomerationHistory> getAgglomerations() {
		return agglomerations;
	}

	@Override
	public int compareTo(ProjectHistory other) {
		return this.getVersion().compareTo(other.getVersion());
	}

	public String getVersion() {
		return version;
	}
}