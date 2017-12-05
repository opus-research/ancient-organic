package br.pucrio.inf.organic.projectBuilders.impl;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.core.SourceMethod;
import org.eclipse.jdt.internal.core.SourceType;

import spirit.metrics.analizer.SpiritVisitor;
import br.pucrio.inf.organic.adapters.JSpiritAdapter;
import br.pucrio.inf.organic.model.Component;
import br.pucrio.inf.organic.model.Project;
import br.pucrio.inf.organic.projectBuilders.ComponentVisitor;
import br.pucrio.inf.organic.projectBuilders.ProjectBuilder;
import ca.mcgill.cs.serg.cm.ConcernMapper;

import static br.pucrio.inf.organic.util.ConcernMapperUtil.*;

/**
 * Concrete implementation of {@link ProjectBuilder }.
 * Parses a mapping file to a {@link Project} instance.
 * 
 * @author Willian
 *
 */
public class ProjectFromConcernMapperBuilder extends ProjectBuilder {

	public static final String NAME = "Concern Mapper File";
	public static final String FILE_NAME = "components.cm";

	public ProjectFromConcernMapperBuilder(IProject eclipseProject) {
		super(eclipseProject);
	}

	@Override
	public Project parse() {
		if (!readConcernMapperFile(eclipseProject, FILE_NAME))
			return defaultBuilder();

		SpiritVisitor codeSmellsVisitor = new SpiritVisitor();
		JSpiritAdapter connector = new JSpiritAdapter(codeSmellsVisitor);
		IJavaProject javaProject = JavaCore.create(getEclipseProject());
		Project project = new Project(javaProject, connector);
		Set<SourceType> sourceTypes = new HashSet<SourceType>();
		
		for (String componentName : ConcernMapper.getDefault().getConcernModel().getConcernNames()) {
			Component component = new Component(componentName);
			ComponentVisitor componentBuilderVisitor = new ComponentVisitor(component);
			project.addComponent(component);
			
			for (Object o : ConcernMapper.getDefault().getConcernModel().getElements(componentName)) {
				if (o instanceof SourceMethod) {
					SourceType parent = (SourceType) ((SourceMethod)o).getParent();
					
					if (sourceTypes.contains(parent))
						continue;
					sourceTypes.add(parent);
					
					ICompilationUnit iCU = parent.getCompilationUnit();
					CompilationUnit classCU = parseInterfaceToCompilationUnit(iCU);
					//SmellVisitor is responsible for finding code smells in the visited class
					classCU.accept(codeSmellsVisitor);
					//ComponentBuilderVisitor is responsible for adding the class to its enclosing component
					classCU.accept(componentBuilderVisitor);
				}
			}
		}
		
		return project;
	}

}
