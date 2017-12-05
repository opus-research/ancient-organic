package br.pucrio.inf.organic.workflows;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import spirit.core.smells.CodeSmell;
import spirit.metrics.storage.InvokingCache;
import br.pucrio.inf.organic.builtin.agglomerationModels.ConcernOverloadAgglomeration;
import br.pucrio.inf.organic.builtin.agglomerationModels.HierarchicalAgglomeration;
import br.pucrio.inf.organic.builtin.agglomerationModels.IntraClassAgglomeration;
import br.pucrio.inf.organic.builtin.agglomerationModels.IntraComponentAgglomeration;
import br.pucrio.inf.organic.builtin.agglomerationModels.IntraMethodAgglomeration;
import br.pucrio.inf.organic.builtin.strategies.ConcernOverloadAgglomerationStrategy;
import br.pucrio.inf.organic.builtin.strategies.HierarchicalStrategy;
import br.pucrio.inf.organic.builtin.strategies.IntraClassAgglomerationStrategy;
import br.pucrio.inf.organic.builtin.strategies.IntraComponentStrategy;
import br.pucrio.inf.organic.builtin.strategies.IntraMethodStrategy;
import br.pucrio.inf.organic.builtin.textMarkers.HierarchicalAgglomerationTextMarker;
import br.pucrio.inf.organic.builtin.textMarkers.IntraComponentAgglomerationTextMarker;
import br.pucrio.inf.organic.builtin.textMarkers.IntraMethodAgglomerationTextMarker;
import br.pucrio.inf.organic.concerns.ConcernMapperReader;
import br.pucrio.inf.organic.dataanalysis.AnalysisExecutor;
import br.pucrio.inf.organic.extensionPoints.AgglomerationDetector;
import br.pucrio.inf.organic.extensionPoints.AgglomerationModel;
import br.pucrio.inf.organic.extensions.OrganicActivator;
import br.pucrio.inf.organic.extensions.ui.AnomaliesView;
import br.pucrio.inf.organic.extensions.ui.OrganicPreferencePage;
import br.pucrio.inf.organic.extensions.ui.TextMarkerUtil;
import br.pucrio.inf.organic.extensions.ui.agglomeration.AgglomerationTypeResults;
import br.pucrio.inf.organic.extensions.ui.agglomeration.AgglomerationsView;
import br.pucrio.inf.organic.extensions.ui.agglomeration.ProjectResults;
import br.pucrio.inf.organic.extensions.ui.agglomeration.WorkspaceResults;
import br.pucrio.inf.organic.history.AgglomerationsHistoryReader;
import br.pucrio.inf.organic.model.Component;
import br.pucrio.inf.organic.model.Project;
import br.pucrio.inf.organic.projectBuilders.ComponentVisitor;
import br.pucrio.inf.organic.projectBuilders.ProjectBuilder;
import br.pucrio.inf.organic.projectBuilders.ProjectBuilderFactory;

/**
 * Basic workflow for any execution of Organic.
 * Independently of the users preferences all the steps in this workflow must be executed.  
 * @author Willian
 *
 */
public class AgglomerationDetectionBasicWorkflow {

	public void executeFor(List<IProject> eclipseProjects) {
		WorkspaceResults results = new WorkspaceResults();
		for (IProject project : eclipseProjects) {
			ProjectResults projectResult = executeFor(project);
			results.addResult(projectResult);

			//TODO We need to implement a generic analysis
			AnalysisExecutor.performHealthWatcherAnalysis(projectResult).printStats();
		}
		
		showAgglomerationsView(results);			
		
		saveResultsToFile(results);
	}

	private ProjectResults executeFor(IProject eclipseProject) {
		Project project = buildProject(eclipseProject);
		
		readConcerns(project);
		
		readAgglomerationsHistory(project);
		
		project.findCodeAnomalies();
		
		Map<String, Set<? extends AgglomerationModel>> builtinAgglomerations = executeBultinDetectors(project);
		
		Map<String, Set<? extends AgglomerationModel>> extensionsAgglomerations = executeExtensions(project);
				
		return new ProjectResults(project, builtinAgglomerations, extensionsAgglomerations);
	}

	private void readConcerns(Project project) {
		ConcernMapperReader concernReader = new ConcernMapperReader(project);
		concernReader.execute();
	}
	
	
	private void readAgglomerationsHistory(Project project) {
		AgglomerationsHistoryReader historyReader = new AgglomerationsHistoryReader(project);
		historyReader.execute();
	}	

	private Project buildProject(IProject eclipseProject) {
		InvokingCache.getInstance().initialize();
		ProjectBuilder projectBuilder = ProjectBuilderFactory.get(eclipseProject);
		Project project = projectBuilder.parse();
		return project;
	}

	private Map<String, Set<? extends AgglomerationModel>> executeBultinDetectors(Project project) {
		Map<String, Set<? extends AgglomerationModel>> agglomerationsPerType = new HashMap<String, Set<? extends AgglomerationModel>>();
		
		Set<IntraMethodAgglomeration> intraMethod = executeIntraMethodStrategy(project);
		Set<IntraComponentAgglomeration> intraComponent = executeIntraComponentAgglomeration(project);
		Set<HierarchicalAgglomeration> hierarchical = executeHierarchicalAgglomeration(project);
		Set<IntraClassAgglomeration> intraClass = executeIntraClassAgglomeration(project);
		Set<ConcernOverloadAgglomeration> concernOverload = executeConcernOverloadAgglomeration(project);

		updateTextMarkers(project, intraMethod, intraComponent, hierarchical, intraClass);
		
		agglomerationsPerType.put(IntraMethodAgglomeration.NAME, intraMethod);
		agglomerationsPerType.put(IntraComponentAgglomeration.NAME, intraComponent);
		agglomerationsPerType.put(HierarchicalAgglomeration.NAME, hierarchical);
		agglomerationsPerType.put(IntraClassAgglomeration.NAME, intraClass);
		agglomerationsPerType.put(ConcernOverloadAgglomeration.NAME, concernOverload);		
		
		
		return agglomerationsPerType;
	}

	private void updateTextMarkers(Project project, Set<IntraMethodAgglomeration> intraMethod,
			Set<IntraComponentAgglomeration> intraComponent, Set<HierarchicalAgglomeration> hierarchical, Set<IntraClassAgglomeration> intraClass) {
		
		TextMarkerUtil.deleteMarkers(project.getJavaProject().getResource());

		IntraMethodAgglomerationTextMarker intraMethodMarker = new IntraMethodAgglomerationTextMarker();
		intraMethodMarker.updateTextMarkers(intraMethod);
		
		IntraComponentAgglomerationTextMarker intraComponentMarker = new IntraComponentAgglomerationTextMarker();
		intraComponentMarker.updateTextMarkers(intraComponent);
		
		HierarchicalAgglomerationTextMarker hierarchicalMarker = new HierarchicalAgglomerationTextMarker();
		hierarchicalMarker.updateTextMarkers(hierarchical);
	}

	private Set<IntraMethodAgglomeration> executeIntraMethodStrategy(Project project) {
		Set<IntraMethodAgglomeration> intraMethodAgglomerations = new IntraMethodStrategy(project).findAgglomerations();
		return intraMethodAgglomerations;
	}

	private Set<IntraComponentAgglomeration> executeIntraComponentAgglomeration(Project project) {
		Set<IntraComponentAgglomeration> intraComponentAgglomerations = new IntraComponentStrategy(project).findAgglomerations();
		return intraComponentAgglomerations;
	}
	
	private Set<HierarchicalAgglomeration> executeHierarchicalAgglomeration(Project project) {
		Set<HierarchicalAgglomeration> hierarchicalAgglomerations = new HierarchicalStrategy(project).findAgglomerations();
		return hierarchicalAgglomerations;
	}

	private Set<IntraClassAgglomeration> executeIntraClassAgglomeration(Project project) {
		Set<IntraClassAgglomeration> intraClassAgglomerations = new IntraClassAgglomerationStrategy(project).findAgglomerations();
		return intraClassAgglomerations;
	}
	
	private Set<ConcernOverloadAgglomeration> executeConcernOverloadAgglomeration(Project project) {
		Set<ConcernOverloadAgglomeration> concernOverloadAgglomerations = new ConcernOverloadAgglomerationStrategy(project).findAgglomerations();
		return concernOverloadAgglomerations;
	}

	private Map<String, Set<? extends AgglomerationModel>> executeExtensions(Project project) {
		Map<String, Set<? extends AgglomerationModel>> agglomerationsPerType = new HashMap<String, Set<? extends AgglomerationModel>>();
		
		IConfigurationElement[] configurationElementsFor = Platform.getExtensionRegistry().getConfigurationElementsFor(AgglomerationDetector.EXTENSION_ID); 
		for (IConfigurationElement configElement : configurationElementsFor) {
			String clazz = configElement.getAttribute(AgglomerationDetector.CLASS_ATTRIBUTE_ID);
			try {
				Class<AgglomerationDetector> detectorClass = (Class<AgglomerationDetector>) Platform.getBundle(configElement.getDeclaringExtension().getContributor().getName()).loadClass(clazz);
				AgglomerationDetector detector = detectorClass.newInstance();
				Set<? extends AgglomerationModel> agglomerations = detector.findAndMarkAgglomerationsFor(project);
				agglomerationsPerType.put(detector.getName(), agglomerations);
				
			} catch (ClassNotFoundException e) {
				System.err.println("Exception while loading strategy :" + e);
			} catch (InstantiationException e) {
				System.err.println("Exception while loading strategy :" + e);
			} catch (IllegalAccessException e) {
				System.err.println("Exception while loading strategy :" + e);
			} catch (IllegalArgumentException e) {
				System.err.println("Exception while loading strategy :" + e);
			} catch (Exception e) {
				System.err.println("Exception while loading strategy :" + e);
			}
		}
		
		return agglomerationsPerType;
	}
	
	private void showAnomaliesView(WorkspaceResults results) {
		try {
			AnomaliesView view = (AnomaliesView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(AnomaliesView.ID);
			view.updateContent(results);
			
		} catch (PartInitException e) {
			System.err.println("Exception while showing agglomerations: " + e);
		}
	}	

	private void showAgglomerationsView(WorkspaceResults results) {
		try {
			AgglomerationsView view = (AgglomerationsView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(AgglomerationsView.ID);
			view.updateContent(results);			
			
		} catch (PartInitException e) {
			System.err.println("Exception while showing agglomerations: " + e);
		}
	}
	
	//TODO extract to another class
	private void saveResultsToFile(WorkspaceResults results) {
		if (!OrganicActivator.getPreferences().getBoolean(OrganicPreferencePage.SAVE_TO_FILE))
			return;
		
		String dateTime = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss").format(new Date());
		
		for (ProjectResults pr : results.getResults().values()) {
			List<String> aggregated = new ArrayList<String>();
			List<String> agglomerations = new ArrayList<String>();
			List<String> smells = new ArrayList<String>();
			
			for (AgglomerationTypeResults aType : pr.getAgglomerationTypes()) {
				for (AgglomerationModel ag : aType.getAgglomerations()) {
					for (CodeSmell cm : ag.getCodeAnomaliesSortedByName()) {
						aggregated.add(ag.getUniqueID() + ";" + ag.toString() + ";" + ag.getCodeAnomaliesSortedByName());
						agglomerations.add(ag.getUniqueID() + ";" + cm.getFullElementName());
						smells.add(cm.getFullElementName() + ";" + cm.getKindOfSmellName());
					}
				}
			}
			
			
			try {
				
				File resultsFolder = new File(pr.getPath() + "/Organic/Results/");
				if (!resultsFolder.exists())
					resultsFolder.mkdirs();

				PrintWriter writer = new PrintWriter(pr.getPath() + "/Organic/Results/Aggregated_Results_" + pr.getName() + "_" + dateTime + ".csv","UTF-8");
				
				for (String s : aggregated) {
					writer.println(s);
				}
				
				writer.close();
				
				writer = new PrintWriter(pr.getPath() + "/Organic/Results/Agglomeration_Results_" + pr.getName() + "_" + dateTime + ".csv","UTF-8");
				
				for (String s : agglomerations) {
					writer.println(s);
				}
				
				writer.close();
				
				writer = new PrintWriter(pr.getPath() + "/Organic/Results/Code_Smell_Results_" + pr.getName() + "_" + dateTime + ".csv","UTF-8");
				
				for (String s : smells) {
					writer.println(s);
				}
				
				writer.close();
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}
}
