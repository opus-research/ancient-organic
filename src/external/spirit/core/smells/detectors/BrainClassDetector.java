package spirit.core.smells.detectors;

import spirit.core.smells.BrainClass;
import spirit.core.smells.CodeSmell;
import spirit.metrics.constants.MetricNames;
import spirit.metrics.constants.MetricThresholds;
import spirit.metrics.storage.ClassMetrics;
import spirit.metrics.storage.MethodMetrics;
import spirit.metrics.storage.NodeMetrics;

public class BrainClassDetector extends CodeSmellDetector{
	
	public boolean codeSmellVerify(NodeMetrics classMetrics){
		boolean isGodclass = classMetrics.getAttribute(MetricNames.BM)!=null?(Boolean)(classMetrics.getAttribute(MetricNames.BM)):false;
		if(!isGodclass && ((isVeryLarge(classMetrics)||isExtremelyLarge(classMetrics))&&isVeryComplexAndNonCohesive(classMetrics))){		   
			return true;
		}
		return false;	
	}

	public CodeSmell codeSmellDetected(NodeMetrics classMetrics){
		return new BrainClass(((ClassMetrics) classMetrics));
	}
	
	public int countBrainMethods(NodeMetrics classMetrics){
		int countBrainMethods=0;
		for(MethodMetrics methodMetrics:((ClassMetrics)classMetrics).getMethodsMetrics()){
			if(methodMetrics.getAttribute(MetricNames.BM)!=null && (Boolean)(methodMetrics.getAttribute(MetricNames.BM))){
				countBrainMethods++;
			}
		}
		return countBrainMethods;
	}
	
	/**
	 * Checks if the class contains more than one brain method and is very large
	 * @return
	 */
	private boolean isVeryLarge(NodeMetrics classMetrics){
		if(classMetrics.getMetric(MetricNames.LOC)!=null && classMetrics.getMetric(MetricNames.LOC)>=MetricThresholds.LOCClassVeryHigh){
			if(countBrainMethods(classMetrics)>1){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks if the class contains only one brain method and is extremely large
	 * @return
	 */
	private boolean isExtremelyLarge(NodeMetrics classMetrics){
		if(classMetrics.getMetric(MetricNames.LOC)!=null && classMetrics.getMetric(MetricNames.LOC)>=2*MetricThresholds.LOCClassVeryHigh
				&& classMetrics.getMetric(MetricNames.WMC)!=null && classMetrics.getMetric(MetricNames.WMC)>=2*MetricThresholds.WMC_VERY_HIGH){
			if(countBrainMethods(classMetrics)==1){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks if the class is very complex and non-cohesive
	 * @return
	 */
	private boolean isVeryComplexAndNonCohesive(NodeMetrics classMetrics){
		if(classMetrics.getMetric(MetricNames.TCC)!=null && classMetrics.getMetric(MetricNames.TCC)<MetricThresholds.HALF
				&& classMetrics.getMetric(MetricNames.WMC)!=null && classMetrics.getMetric(MetricNames.WMC)>=MetricThresholds.WMC_VERY_HIGH){
			return true;
		}
		return false;
	}

}
