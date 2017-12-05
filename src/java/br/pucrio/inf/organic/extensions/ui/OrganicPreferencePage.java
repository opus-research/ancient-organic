package br.pucrio.inf.organic.extensions.ui;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import br.pucrio.inf.organic.builtin.strategies.Thresholds;
import br.pucrio.inf.organic.extensions.OrganicActivator;
import br.pucrio.inf.organic.projectBuilders.ProjectBuilderFactory;
import br.pucrio.inf.organic.projectBuilders.impl.ProjectFromConcernMapperBuilder;
import br.pucrio.inf.organic.projectBuilders.impl.ProjectFromPackageStructureBuilder;

/**
 * Preferences page for the Organic Plugin.
 * This page allows users to configure Organic for the current workspace.
 * @author Willian
 *
 */
public class OrganicPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public static final String SAVE_TO_FILE = "SaveToFile";	

	public OrganicPreferencePage() {		
		super(GRID);
	}
	
	@Override
	protected void createFieldEditors() {
		String[][] componentsSources = new String[][] { { ProjectFromPackageStructureBuilder.NAME, ProjectFromPackageStructureBuilder.NAME }
		, { ProjectFromConcernMapperBuilder.NAME, ProjectFromConcernMapperBuilder.NAME } };
		addField(new ComboFieldEditor(ProjectBuilderFactory.SOURCE, "Read component structure from", componentsSources, getFieldEditorParent()));
		
		addField(new BooleanFieldEditor(SAVE_TO_FILE, "Save results to file?" ,getFieldEditorParent()));
		
		IntegerFieldEditor intraMethodField = new IntegerFieldEditor(Thresholds.INTRA_METHOD, "Intra-method threshold" ,getFieldEditorParent());
		intraMethodField.setValidRange(0, 100);
		IntegerFieldEditor intraClassField = new IntegerFieldEditor(Thresholds.INTRA_CLASS, "Intra-class threshold" ,getFieldEditorParent());
		intraClassField.setValidRange(0, 100);
		IntegerFieldEditor intraComponentField = new IntegerFieldEditor(Thresholds.INTRA_COMPONENT, "Intra-component threshold" ,getFieldEditorParent());
		intraComponentField.setValidRange(0, 100);
		IntegerFieldEditor hierarchicalField = new IntegerFieldEditor(Thresholds.HIERARCHICAL, "Hierarchical threshold" ,getFieldEditorParent());
		hierarchicalField.setValidRange(0, 100);
		
		addField(intraMethodField);
		addField(intraClassField);
		addField(intraComponentField);
		addField(hierarchicalField);
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(OrganicActivator.getDefault().getPreferenceStore());
	}
}
