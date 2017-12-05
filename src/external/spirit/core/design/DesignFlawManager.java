package spirit.core.design;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import spirit.core.smells.CodeSmell;
import spirit.core.smells.detectors.BrainClassDetector;
import spirit.core.smells.detectors.BrainMethodDetector;
import spirit.core.smells.detectors.CodeSmellDetector;
import spirit.core.smells.detectors.DataClassDetector;
import spirit.core.smells.detectors.DispersedCouplingDetector;
import spirit.core.smells.detectors.FeatureEnvyDetector;
import spirit.core.smells.detectors.GodClassDetector;
import spirit.core.smells.detectors.IntensiveCouplingDetector;
import spirit.core.smells.detectors.RefusedParentBequestDetector;
import spirit.core.smells.detectors.ShotgunSurgeryDetector;
import spirit.core.smells.detectors.TraditionBreakerDetector;
import spirit.metrics.analizer.SpiritVisitor;
import spirit.metrics.storage.ClassMetrics;
import spirit.metrics.storage.MethodMetrics;



public class DesignFlawManager {
	private static DesignFlawManager instance;
	private Vector<CodeSmell> smells;
	private Vector<CodeSmell> methodSmells;
	private Vector<CodeSmell> classSmells;
	private Vector<CodeSmellDetector> classDetectors;
	private Vector<CodeSmellDetector> methodDetectors; 
	SpiritVisitor visitor = new SpiritVisitor();
	
	public static DesignFlawManager getInstance(){
		if(instance==null)
			instance=new DesignFlawManager();
		return instance;
	}
	
	private DesignFlawManager() {
		initialize();
	}
	
	public void initialize(){
		smells=new Vector<CodeSmell>();
		methodSmells=new Vector<CodeSmell>();
		classSmells=new Vector<CodeSmell>();
		visitor = new SpiritVisitor();
		classDetectors = new Vector<CodeSmellDetector>();
		methodDetectors = new Vector<CodeSmellDetector>();
		
		methodDetectors.add(new BrainMethodDetector());
		methodDetectors.add(new FeatureEnvyDetector());
		methodDetectors.add(new DispersedCouplingDetector());
		methodDetectors.add(new IntensiveCouplingDetector());
		methodDetectors.add(new ShotgunSurgeryDetector());
		classDetectors.add(new GodClassDetector());
		classDetectors.add(new BrainClassDetector());
		classDetectors.add(new DataClassDetector());
		classDetectors.add(new RefusedParentBequestDetector());
		classDetectors.add(new TraditionBreakerDetector());
	}
	
	public Vector<CodeSmell> getSmells(){
		return smells;//TODO
	}
	
	public Vector<CodeSmell> getMethodSmells(){
		return methodSmells;
	}
	
	public Vector<CodeSmell> getClassSmells(){
		return classSmells;
	}
	
	public void countCodeSmellsDebug(){
		HashMap<String,Integer> codeSmellsCount = new HashMap<String,Integer>();
		for(CodeSmell smell:smells){
			if(codeSmellsCount.get(smell.getKindOfSmellName())==null){
				codeSmellsCount.put(smell.getKindOfSmellName(), 1);
			}else{
				codeSmellsCount.put(smell.getKindOfSmellName(), codeSmellsCount.get(smell.getKindOfSmellName())+1);
			}
		}
		for(String codeSmell:codeSmellsCount.keySet()){
			System.out.println(codeSmell + ": "+ codeSmellsCount.get(codeSmell));
		}
	}

	public void calculateMetricsCode(IProject project) throws IOException {
		try {
			analyseProject(project);
			visitor.executeMetrics();
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}
	
	public void calculateAditionalMetrics(){
		visitor.calculateAditionalMetrics();
	}
	
	public void detectCodeSmells(IProject selectedProject){
		List<ClassMetrics> lclassMetrics = visitor.getLClassesMetrics();
		for(ClassMetrics classMetrics:lclassMetrics){
			for(MethodMetrics methodMetrics:classMetrics.getMethodsMetrics()){
				for(CodeSmellDetector methodDetector:methodDetectors){
					if(methodDetector.codeSmellVerify(methodMetrics)){
						CodeSmell codeSmellDetected = methodDetector.codeSmellDetected(methodMetrics);
						smells.add(codeSmellDetected);
						methodSmells.add(codeSmellDetected);
					}
				}
			}
			for(CodeSmellDetector classDetector:classDetectors){
				if(classDetector.codeSmellVerify(classMetrics)){
					CodeSmell codeSmellDetected = classDetector.codeSmellDetected(classMetrics);
					smells.add(codeSmellDetected);
					classSmells.add(codeSmellDetected);					
				}
			}
		}
		loadTextMarkers(selectedProject);
	}

	private void loadTextMarkers(IProject selectedProject) {
		//deleteTextMarkers(selectedProject);
		
		for (Iterator<CodeSmell> iterator = smells.iterator(); iterator.hasNext();) {
			CodeSmell type = (CodeSmell) iterator.next();
			
			
			IJavaElement javaElement=type.getMainClass().resolveBinding().getJavaElement();
			ICompilationUnit cu= (ICompilationUnit)javaElement.getAncestor(IJavaElement.COMPILATION_UNIT);
			
			IMarker marker;
			try {
				deleteSmellMarker(cu.getCorrespondingResource(),type.getKindOfSmellName(),type.getLine());
				marker = cu.getCorrespondingResource().createMarker(IMarker.PROBLEM);//"codeSmellMarker");
				marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
				marker.setAttribute(IMarker.MESSAGE, type.getKindOfSmellName());
				marker.setAttribute(IMarker.LINE_NUMBER,type.getLine());//1);
				marker.setAttribute(IMarker.TRANSIENT, false);
				
				//marker.setAttribute(IMarker.CHAR_START,type.getElement().getStartPosition());
				//marker.setAttribute(IMarker.CHAR_END,type.getElement().getStartPosition()+type.getElement().getLength());
			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
		
	}
	private void deleteSmellMarker(IResource target, String kind, int line) {
		  
		   try {
			IMarker[] markers = target.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
			for (int i = 0; i < markers.length; i++) {
				String markerMessage=(String) markers[i].getAttribute(IMarker.MESSAGE);
				Integer lineNumber=(Integer)markers[i].getAttribute(IMarker.LINE_NUMBER);
				if(markerMessage!=null && lineNumber!=null){
					if((markerMessage.equals(kind))&&(lineNumber==line)){
						markers[i].delete();
					}
				}
					
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	/**
	 * get the ICompilationUnit for the source
	 * 
	 * @return ICompilationUnit
	 */
	public ASTNode getCompilationUnit(ASTNode node) {
		if (node.getNodeType() == ASTNode.COMPILATION_UNIT)
			return node;
		else {
			return getAncestorCU(node);
		}
	}

	/**
	 * @see IJavaElement
	 */
	public ASTNode getAncestorCU(ASTNode element) {
		while (element != null) {
			if (element.getNodeType() == ASTNode.COMPILATION_UNIT)
				return element;
			element = element.getParent();
		}
		return null;
	}

	private void analyseProject(IProject project) throws JavaModelException {
		IPackageFragment[] packages = JavaCore.create(project)
				.getPackageFragments();
		for (IPackageFragment mypackage : packages) {
			if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
				createAST(mypackage);
			}
		}
	}

	private void createAST(IPackageFragment mypackage)
			throws JavaModelException {
		for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
			
			CompilationUnit parse = parse(unit);
			parse.accept(visitor);
		}
	}

	/**
	 * Reads a ICompilationUnit and creates the AST DOM for manipulating the
	 * Java source file
	 * 
	 * @param unit
	 * @return
	 */

	private static CompilationUnit parse(ICompilationUnit unit) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		return (CompilationUnit) parser.createAST(null);
	}
}
