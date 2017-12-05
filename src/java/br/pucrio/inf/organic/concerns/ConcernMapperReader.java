package br.pucrio.inf.organic.concerns;

import static br.pucrio.inf.organic.util.ConcernMapperUtil.readConcernMapperFile;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.internal.core.SourceMethod;
import org.eclipse.jdt.internal.core.SourceType;

import br.pucrio.inf.organic.model.Component;
import br.pucrio.inf.organic.model.Concern;
import br.pucrio.inf.organic.model.Project;
import ca.mcgill.cs.serg.cm.ConcernMapper;

public class ConcernMapperReader {
	
	private final Project project;
	
	public static final String FILE_NAME = "concerns.cm";
	
	public ConcernMapperReader(Project project) {
		this.project = project;
	}
	
	@SuppressWarnings("restriction")
	public boolean execute() {
		if (!readConcernMapperFile(getEclipseProject(), FILE_NAME))
			return false;

		Set<SourceType> visitedTypes = new HashSet<SourceType>();
		Map<String, TypeDeclaration> classes = project.getClasses();
		
		for (String concernName : ConcernMapper.getDefault().getConcernModel().getConcernNames()) {
			Concern concern = new Concern(concernName);
			ConcernVisitor concernVisitor = new ConcernVisitor(concern);
			project.addConcern(concern);
			
			for (Object o : ConcernMapper.getDefault().getConcernModel().getElements(concernName)) {
				if (o instanceof SourceMethod) {
					SourceType parent = (SourceType) ((SourceMethod)o).getParent();
					
					if (visitedTypes.contains(parent))
						continue;
					visitedTypes.add(parent);
					
					TypeDeclaration class_ = classes.get(parent.getFullyQualifiedName());
					if (class_ != null) {
						//ConcernVisitor is responsible for adding the class to its implemented concerns
						class_.accept(concernVisitor);
					}
				}
			}
		}
		
		for (Component comp : project.getComponents()) {
			Concern concern = new Concern(comp.getName());
			ConcernVisitor cv = new ConcernVisitor(concern);
			for (TypeDeclaration class_ : comp.getClasses()) {
				class_.accept(cv);
			}
		}
		
		return true;
	}
	
	/**
	 * Reads a ICompilationUnit and creates the AST DOM for manipulating the
	 * Java source file
	 * 
	 * @param unit
	 * @return
	 */
	protected CompilationUnit parseInterfaceToCompilationUnit(ICompilationUnit unit) {
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		return (CompilationUnit) parser.createAST(null);
	}	

	private IProject getEclipseProject() {
		return project.getJavaProject().getProject();
	}
}
