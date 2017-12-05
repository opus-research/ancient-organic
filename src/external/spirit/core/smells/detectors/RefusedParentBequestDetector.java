package spirit.core.smells.detectors;

import java.util.List;

import spirit.core.smells.CodeSmell;
import spirit.core.smells.RefusedParentBequest;
import spirit.metrics.constants.MetricNames;
import spirit.metrics.constants.MetricThresholds;
import spirit.metrics.storage.ClassMetrics;
import spirit.metrics.storage.NodeMetrics;

public class RefusedParentBequestDetector extends CodeSmellDetector{
	
	public boolean codeSmellVerify(NodeMetrics classMetrics){
		if(isChild(classMetrics) && 
				childClassIgnoresBequest((ClassMetrics) classMetrics) &&
				childClassIsNotSmallAndSimple((ClassMetrics) classMetrics)){		   
			return true;
		}
		return false;	
	}

	public CodeSmell codeSmellDetected(NodeMetrics classMetrics){
		return new RefusedParentBequest(((ClassMetrics) classMetrics));
	}
	

	
	public boolean childClassIgnoresBequest(ClassMetrics classMetrics){
		if(((classMetrics.getMetric(MetricNames.NProtM)!=null && classMetrics.getMetric(MetricNames.NProtM)>MetricThresholds.FEW &&
				classMetrics.getMetric(MetricNames.BUR)!=null && classMetrics.getMetric(MetricNames.BUR)<MetricThresholds.ONE_THIRD))||
				(classMetrics.getMetric(MetricNames.BOvR)!=null && classMetrics.getMetric(MetricNames.BOvR)<MetricThresholds.ONE_THIRD)){
			return true;
		}
		return false;
	}
	
	public boolean childClassIsNotSmallAndSimple(ClassMetrics classMetrics){
		if(((classMetrics.getMetric(MetricNames.AMW)!=null && classMetrics.getMetric(MetricNames.AMW)>MetricThresholds.AMWAvg ||
				classMetrics.getMetric(MetricNames.WMC)!=null && classMetrics.getMetric(MetricNames.WMC)>MetricThresholds.WMCAvg))&&
				(classMetrics.getMetric(MetricNames.NOM)!=null && classMetrics.getMetric(MetricNames.NOM)>MetricThresholds.NOMAvg)){
			return true;
		}
		return false;
	}
	
	private boolean isChild(NodeMetrics classMetrics){
		List<String> nameOfClasses = (List<String>) classMetrics.getAttribute(MetricNames.nameOfClasses);
		if(((ClassMetrics) classMetrics).getDeclaration().resolveBinding().getSuperclass()!=null && 
				nameOfClasses.contains(((ClassMetrics) classMetrics).getDeclaration().resolveBinding().getSuperclass().getBinaryName()) ){
			return true;
		}
		return false;
	}

}
