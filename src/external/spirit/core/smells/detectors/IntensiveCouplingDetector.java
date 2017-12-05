package spirit.core.smells.detectors;

import spirit.core.smells.CodeSmell;
import spirit.core.smells.IntensiveCoupling;
import spirit.metrics.constants.MetricNames;
import spirit.metrics.constants.MetricThresholds;
import spirit.metrics.storage.MethodMetrics;
import spirit.metrics.storage.NodeMetrics;

public class IntensiveCouplingDetector extends CodeSmellDetector{
	
	public boolean codeSmellVerify(NodeMetrics methodMetric){
		if(methodMetric.getMetric(MetricNames.MNL)!=null && methodMetric.getMetric(MetricNames.MNL) > MetricThresholds.SHALLOW &&
				
		    ((methodMetric.getMetric(MetricNames.CDISP)!=null && methodMetric.getMetric(MetricNames.CDISP) < MetricThresholds.ONE_QUARTER &&
		    methodMetric.getMetric(MetricNames.CINT)!=null && methodMetric.getMetric(MetricNames.CINT) > MetricThresholds.FEW)||(
			methodMetric.getMetric(MetricNames.CDISP)!=null && methodMetric.getMetric(MetricNames.CDISP) < MetricThresholds.HALF &&
			methodMetric.getMetric(MetricNames.CINT)!=null && methodMetric.getMetric(MetricNames.CINT) > MetricThresholds.SMemCap))	   
		   ){

			return true;
		}
		return false;
	}
	
	public CodeSmell codeSmellDetected(NodeMetrics methodMetric){
		return new IntensiveCoupling(((MethodMetrics) methodMetric));
	}

}
