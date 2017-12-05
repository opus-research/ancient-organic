package spirit.core.smells;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jdt.core.dom.TypeDeclaration;

import spirit.metrics.constants.MetricNames;
import spirit.metrics.storage.ClassMetrics;

public class GodClass extends CodeSmell{
	public static String NAME="God Class";
	public GodClass(ClassMetrics node) {
		super(NAME);
		this.element=node.getDeclaration();
		this.node=node;
	}
	@Override
	public TypeDeclaration getMainClass() {
		if(this.getElement() instanceof TypeDeclaration){
	    	return ((TypeDeclaration)(this.getElement()));
	    }
		return null;
	}
	/**All the classes invoked by the main class of the smell
	 * 
	 */
	@Override
	public Set<String> getAffectedClasses() {
		return new TreeSet<String>((List<String>)(node.getAttribute(MetricNames.ListOfClassInvoked)));
	}
	
	@Override
	public String getDescription() {
		return "Long and complex class that centralizes the intelligence of the system";
	}	
}
