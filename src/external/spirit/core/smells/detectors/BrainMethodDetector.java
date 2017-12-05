package spirit.core.smells.detectors;

import spirit.core.smells.BrainMethod;
import spirit.core.smells.CodeSmell;
import spirit.metrics.constants.MetricNames;
import spirit.metrics.constants.MetricThresholds;
import spirit.metrics.storage.MethodMetrics;
import spirit.metrics.storage.NodeMetrics;

public class BrainMethodDetector extends CodeSmellDetector{
	
	public boolean codeSmellVerify(NodeMetrics methodMetric){
		if(methodMetric.getMetric(MetricNames.LOC)!=null && methodMetric.getMetric(MetricNames.LOC) > MetricThresholds.LOCVeryHigh &&
				   methodMetric.getMetric(MetricNames.MNL)!=null && methodMetric.getMetric(MetricNames.MNL) >= MetricThresholds.DEEP &&
				   //methodMetric.getMetric(MetricNames.WMC)!=null && methodMetric.getMetric(MetricNames.WMC)/2 >= MetricThresholds.DEEP &&
				   methodMetric.getMetric(MetricNames.WMC)!=null && methodMetric.getMetric(MetricNames.WMC) >= MetricThresholds.MANY &&
				   methodMetric.getMetric(MetricNames.NOF)!=null && methodMetric.getMetric(MetricNames.NOF) >= MetricThresholds.SMemCap
				   ){
				   
			methodMetric.setAttribute(MetricNames.BM, Boolean.valueOf(true));
			return true;
		}
		return false;
	}
	
	public CodeSmell codeSmellDetected(NodeMetrics methodMetric){
		return new BrainMethod(((MethodMetrics) methodMetric));
	}

}
