package br.pucrio.inf.organic.extensions.ui;

import java.text.Collator;
import java.util.HashSet;
import java.util.Set;

import javax.xml.crypto.Data;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.ITextEditor;

import spirit.core.smells.CodeSmell;
import br.pucrio.inf.organic.extensions.ui.agglomeration.ProjectResults;
import br.pucrio.inf.organic.extensions.ui.agglomeration.WorkspaceResults;

public class AnomaliesView extends ViewPart {

	public final static String ID = "br.pucrio.inf.organic.extensions.ui.AnomaliesView";
	
	private TableViewer viewer;
	
	@Override
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		// create the columns 
		createUpdateColumns();

		// make lines and header visible
		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true); 
		
		// Set the ContentProvider
		viewer.setContentProvider(ArrayContentProvider.getInstance());

		// make the selection available to other views
		getSite().setSelectionProvider(viewer);
		
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override public void doubleClick(DoubleClickEvent event) {
				ISelection selection = event.getSelection();
				
				if (selection instanceof StructuredSelection) {
					CodeSmell anomaly = (CodeSmell) ((StructuredSelection)selection).getFirstElement();
					CompilationUnit cu = (CompilationUnit) anomaly.getElement().getRoot();
					try {
						ITextEditor textEditor = (ITextEditor) JavaUI.openInEditor(cu.getJavaElement());
						textEditor.selectAndReveal(anomaly.getElement().getStartPosition(), 1);
					} catch (PartInitException e) {
						e.printStackTrace();
					} catch (JavaModelException e) {
						e.printStackTrace();
					}
				}
			}});
	}
	
	public void createUpdateColumns() {
		
		final TableViewerColumn colKindOfSmell = createTableViewerColumn(viewer, "Type of Anomaly", 300, 0);
		colKindOfSmell.setLabelProvider(new ColumnLabelProvider() {
		  @Override
		  public String getText(Object element) {
		    CodeSmell p = (CodeSmell) element;
		    return p.getKindOfSmellName();
		  }
		});
		
		new TableSortSelectionListener(viewer, colKindOfSmell.getColumn(), new TextSorter(colKindOfSmell.getColumn()), SWT.DOWN, false).chooseColumnForSorting();

		
		final TableViewerColumn colJavaElement = createTableViewerColumn(viewer, "Java Element", 600, 1);
		colJavaElement.setLabelProvider(new ColumnLabelProvider() {
		  @Override
		  public String getText(Object element) {
		    CodeSmell p = (CodeSmell) element;
		    return p.getElementName();
		  }
		});
		
		new TableSortSelectionListener(viewer, colJavaElement.getColumn(), new TextSorter(colJavaElement.getColumn()), SWT.UP, false);
	}
	
	protected TableViewerColumn createTableViewerColumn(TableViewer viewer,String title, int bound, final int colNumber) {
	    final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
	    final TableColumn column = viewerColumn.getColumn();
	    column.setText(title);
	    column.setWidth(bound);
	    column.setResizable(true);
	    column.setMoveable(true);	    
	    return viewerColumn;
	 }

	@Override
	public void setFocus() {
		
	}
	
	public void updateContent(WorkspaceResults results) {
		Set<CodeSmell> codeAnomalies = new HashSet<CodeSmell>();
		for (ProjectResults pr : results.getResults().values()) {
			codeAnomalies.addAll(pr.getProject().getSpiritAdapter().getClassSmells());
			codeAnomalies.addAll(pr.getProject().getSpiritAdapter().getMethodAnomalies());
		}
		viewer.setInput(codeAnomalies.toArray());
	}
	
	private class TextSorter extends AbstractInvertableTableSorter {
		
		private TableColumn column;

		public TextSorter(TableColumn column) {
			this.column = column;
		}
		
		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			CodeSmell c1 = (CodeSmell) e1;
			CodeSmell c2 = (CodeSmell) e2;
			
			if (column.getText().equals("Java Element")) {
				return Collator.getInstance().compare(c1.getElementName(), c2.getElementName());
			}
			
			return Collator.getInstance().compare(c1.getKindOfSmellName(), c2.getKindOfSmellName());
		}
	}

}
