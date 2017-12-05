package spirit.core.smells.detectors;

import java.util.List;

import spirit.core.smells.CodeSmell;
import spirit.core.smells.TraditionBreaker;
import spirit.metrics.constants.MetricNames;
import spirit.metrics.constants.MetricThresholds;
import spirit.metrics.storage.ClassMetrics;
import spirit.metrics.storage.NodeMetrics;

public class TraditionBreakerDetector extends CodeSmellDetector{
	
	public boolean codeSmellVerify(NodeMetrics classMetrics){
		if(isChild(classMetrics) &&
				excessiveIncreaseOfChildClassInterface((ClassMetrics) classMetrics) &&
					childClassHasSubstantialSizeAndComplexity((ClassMetrics) classMetrics) &&
						parentClassIsNeitherSmallNorDumb((ClassMetrics) classMetrics)
				){		   
			return true;
		}
		return false;	
	}

	public CodeSmell codeSmellDetected(NodeMetrics classMetrics){
		return new TraditionBreaker(((ClassMetrics) classMetrics));
	}
	

	public boolean excessiveIncreaseOfChildClassInterface(ClassMetrics classMetrics){
		if((classMetrics.getMetric(MetricNames.NAS)!=null && classMetrics.getMetric(MetricNames.NAS)>=MetricThresholds.NOMAvg &&
				classMetrics.getMetric(MetricNames.PNAS)!=null && classMetrics.getMetric(MetricNames.PNAS)>=MetricThresholds.TWO_THIRD)){
			return true;
		}
		return false;
	}
	
	public boolean childClassHasSubstantialSizeAndComplexity(ClassMetrics classMetrics){
		if(((classMetrics.getMetric(MetricNames.AMW)!=null && classMetrics.getMetric(MetricNames.AMW)>MetricThresholds.AMWAvg ||
				classMetrics.getMetric(MetricNames.WMC)!=null && classMetrics.getMetric(MetricNames.WMC)>=MetricThresholds.WMC_VERY_HIGH))&&
				(classMetrics.getMetric(MetricNames.NOM)!=null && classMetrics.getMetric(MetricNames.NOM)>=MetricThresholds.NOMHigh)){
			return true;
		}
		return false;
	}
	
	public boolean parentClassIsNeitherSmallNorDumb(ClassMetrics classMetrics){
		if(((classMetrics.getMetric(MetricNames.AMW)!=null && classMetrics.getMetric(MetricNames.AMW)>MetricThresholds.AMWAvg &&
				classMetrics.getMetric(MetricNames.WMC)!=null && classMetrics.getMetric(MetricNames.WMC)>MetricThresholds.WMC_VERY_HIGH/2))&&
				(classMetrics.getMetric(MetricNames.NOM)!=null && classMetrics.getMetric(MetricNames.NOM)>=MetricThresholds.NOMHigh/2)){
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
