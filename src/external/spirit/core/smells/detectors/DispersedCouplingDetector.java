package spirit.core.smells.detectors;

import spirit.core.smells.CodeSmell;
import spirit.core.smells.DispersedCoupling;
import spirit.metrics.constants.MetricNames;
import spirit.metrics.constants.MetricThresholds;
import spirit.metrics.storage.MethodMetrics;
import spirit.metrics.storage.NodeMetrics;

public class DispersedCouplingDetector extends CodeSmellDetector{
	
	public boolean codeSmellVerify(NodeMetrics methodMetric){
		if(methodMetric.getMetric(MetricNames.MNL)!=null && methodMetric.getMetric(MetricNames.MNL) > MetricThresholds.SHALLOW &&
		   methodMetric.getMetric(MetricNames.CDISP)!=null && methodMetric.getMetric(MetricNames.CDISP) >= MetricThresholds.HALF &&
		   methodMetric.getMetric(MetricNames.CINT)!=null && methodMetric.getMetric(MetricNames.CINT) > MetricThresholds.SMemCap
		   ){

			return true;
		}
		return false;
	}
	
	public CodeSmell codeSmellDetected(NodeMetrics methodMetric){
		return new DispersedCoupling(((MethodMetrics) methodMetric));
	}

}
