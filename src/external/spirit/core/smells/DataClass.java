package spirit.core.smells;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jdt.core.dom.TypeDeclaration;

import spirit.metrics.constants.MetricNames;
import spirit.metrics.storage.ClassMetrics;

public class DataClass extends CodeSmell{
	public static String NAME="Data Class";
	public DataClass(ClassMetrics node) {
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
	
	/**All the classes invoking the main class of the smell
	 * 
	 */
	@Override
	public Set<String> getAffectedClasses() {
		return new TreeSet<String>((List<String>)(node.getAttribute(MetricNames.ListOfClassInvoking)));
	}
	
	@Override
	public String getDescription() {
		return "Class that contains data but not behavior related to the data";
	}	
}
