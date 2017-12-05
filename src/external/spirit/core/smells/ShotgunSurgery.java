package spirit.core.smells;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import spirit.metrics.constants.MetricNames;
import spirit.metrics.storage.MethodMetrics;

public class ShotgunSurgery extends CodeSmell{
	public static String NAME="Shotgun Surgery";
	public ShotgunSurgery(MethodMetrics node) {
		super(NAME);
		this.element=node.getDeclaration();
		this.node=node;
	}
	@Override
	public TypeDeclaration getMainClass() {
		MethodDeclaration element = (MethodDeclaration) this.getElement();  
		if(element.getParent() instanceof TypeDeclaration){
		    	return ((TypeDeclaration)(element.getParent()));
		}else{
			return ((TypeDeclaration)(element.getParent().getParent()));
		}
	}
	
	/**All the classes invoking the main method of the smell
	 * 
	 */
	@Override
	public Set<String> getAffectedClasses() {
		return new TreeSet<String>((List<String>)(node.getAttribute(MetricNames.ListOfClassInvoking)));
	}
	
	@Override
	public String getDescription() {
		return "Method called by many methods that are implemented in different classes";
	}	
}
