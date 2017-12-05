package spirit.core.smells.detectors;

import spirit.core.smells.CodeSmell;
import spirit.core.smells.FeatureEnvy;
import spirit.metrics.constants.MetricNames;
import spirit.metrics.constants.MetricThresholds;
import spirit.metrics.storage.MethodMetrics;
import spirit.metrics.storage.NodeMetrics;

public class FeatureEnvyDetector extends CodeSmellDetector{
	
	public boolean codeSmellVerify(NodeMetrics methodMetric){
		if(methodMetric.getMetric(MetricNames.ATFD)!=null && methodMetric.getMetric(MetricNames.ATFD) > MetricThresholds.FEW &&
		   methodMetric.getMetric(MetricNames.LAA)!=null && methodMetric.getMetric(MetricNames.LAA) < MetricThresholds.ONE_THIRD &&
		   methodMetric.getMetric(MetricNames.FDP)!=null && methodMetric.getMetric(MetricNames.FDP) <= MetricThresholds.FEW){
		   
			return true;
		}
		return false;	
	}
		
	public CodeSmell codeSmellDetected(NodeMetrics methodMetric){
		return new FeatureEnvy(((MethodMetrics) methodMetric));
	}
	
}
