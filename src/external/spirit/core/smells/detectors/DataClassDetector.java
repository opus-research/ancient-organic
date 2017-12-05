package spirit.core.smells.detectors;

import spirit.core.smells.CodeSmell;
import spirit.core.smells.DataClass;
import spirit.metrics.constants.MetricNames;
import spirit.metrics.constants.MetricThresholds;
import spirit.metrics.storage.ClassMetrics;
import spirit.metrics.storage.NodeMetrics;

public class DataClassDetector extends CodeSmellDetector{
	
	public boolean codeSmellVerify(NodeMetrics classMetrics){
		if(classMetrics.getMetric(MetricNames.NOAM)!=null && classMetrics.getMetric(MetricNames.NOPA)!=null){
			int sum = classMetrics.getMetric(MetricNames.NOAM).intValue() + (int)classMetrics.getMetric(MetricNames.NOPA).intValue(); 
			if(  classMetrics.getMetric(MetricNames.WOC)!=null && classMetrics.getMetric(MetricNames.WOC) < MetricThresholds.ONE_THIRD &&
			   ((sum > MetricThresholds.FEW &&
			   classMetrics.getMetric(MetricNames.WMC)!=null && classMetrics.getMetric(MetricNames.WMC) >= MetricThresholds.WMC_HIGH)||
			   (sum > MetricThresholds.MANY &&
					   classMetrics.getMetric(MetricNames.WMC)!=null && classMetrics.getMetric(MetricNames.WMC) >= MetricThresholds.WMC_VERY_HIGH))){		   
			return true;
			}
		}
		return false;	
	}
	
	public CodeSmell codeSmellDetected(NodeMetrics classMetrics){
		return new DataClass(((ClassMetrics) classMetrics));
	}

}
