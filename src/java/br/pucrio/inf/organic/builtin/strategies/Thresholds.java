package br.pucrio.inf.organic.builtin.strategies;


/**
 * Class responsible for providing the thresholds for the built-in types of agglomeration.
 * The thresholds are read from the preferences of eclipse.
 * @author Willian
 *
 */
public class Thresholds {

	public static final String INTRA_METHOD = "IntraMethodThreshold";
	public static final String INTRA_COMPONENT = "IntraComponentThreshold";
	public static final String HIERARCHICAL = "HierarchicalThreshold";
	public static final String INTRA_CLASS = "IntraClassThreshold";

	public static int getIntraMethod() {
		return 1;
//		return OrganicActivator.getPreferences().getInt(INTRA_METHOD);
	}
	
	public static int getIntraComponent() {
		return 1;
//		return OrganicActivator.getPreferences().getInt(INTRA_COMPONENT);
	}
	
	public static int getHierarchical() {
		return 1;
//		return OrganicActivator.getPreferences().getInt(HIERARCHICAL);
	}

	public static int getIntraClass() {
		return 1;
//		return OrganicActivator.getPreferences().getInt(INTRA_CLASS);
	}

	public static int getConcernOverloadForAnomalies() {
		//TODO read from preferences
		return 1;
	}

	public static int getConcernOverloadForConcerns() {
		// TODO Auto-generated method stub
		return 1;
	}
}
