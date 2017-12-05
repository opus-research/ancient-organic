package spirit.core.smells.detectors;

import spirit.core.smells.CodeSmell;
import spirit.core.smells.GodClass;
import spirit.metrics.constants.MetricNames;
import spirit.metrics.constants.MetricThresholds;
import spirit.metrics.storage.ClassMetrics;
import spirit.metrics.storage.NodeMetrics;

public class GodClassDetector extends CodeSmellDetector{
	
	public boolean codeSmellVerify(NodeMetrics classMetrics){
		if(classMetrics.getMetric(MetricNames.ATFD)!=null && classMetrics.getMetric(MetricNames.ATFD) > MetricThresholds.FEW &&
		   classMetrics.getMetric(MetricNames.TCC)!=null && classMetrics.getMetric(MetricNames.TCC) < MetricThresholds.ONE_THIRD &&
		   classMetrics.getMetric(MetricNames.WMC)!=null && classMetrics.getMetric(MetricNames.WMC) >= MetricThresholds.WMC_VERY_HIGH){
		   
			classMetrics.setAttribute(MetricNames.GC, Boolean.valueOf(true));
			return true;
		}
		return false;	
	}
	
	public CodeSmell codeSmellDetected(NodeMetrics classMetrics){
		return new GodClass(((ClassMetrics) classMetrics));
	}

}
