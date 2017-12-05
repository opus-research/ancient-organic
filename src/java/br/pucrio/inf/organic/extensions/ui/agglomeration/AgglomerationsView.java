package br.pucrio.inf.organic.extensions.ui.agglomeration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.draw2d.IFigure;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.internal.core.ResolvedSourceMethod;
import org.eclipse.jdt.internal.core.ResolvedSourceType;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.algorithms.GridLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.HorizontalTreeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

import spirit.core.smells.CodeSmell;
import br.pucrio.inf.organic.builtin.agglomerationModels.ConcernOverloadAgglomeration;
import br.pucrio.inf.organic.builtin.agglomerationModels.HierarchicalAgglomeration;
import br.pucrio.inf.organic.builtin.agglomerationModels.IntraClassAgglomeration;
import br.pucrio.inf.organic.builtin.agglomerationModels.IntraComponentAgglomeration;
import br.pucrio.inf.organic.builtin.agglomerationModels.IntraMethodAgglomeration;
import br.pucrio.inf.organic.extensionPoints.AgglomerationModel;
import br.pucrio.inf.organic.model.Concern;
import br.pucrio.inf.organic.util.ZestUtils;

/**
 * UI for the visualization of agglomerations
 * 
 * @author Willian
 *
 */
public class AgglomerationsView extends ViewPart {

	public final static String ID = "br.pucrio.inf.organic.extensions.ui.agglomeration.AgglomerationsView";

	private Composite descriptionControl;
	private StyledText descriptionText;
	private Composite anomaliesControl;
	private TreeViewer anomaliesViewer;
	private Composite graphControl;
	private Graph graph;
	private TreeViewer treeViewer;
	private TreeViewer referencesViewer;
	private TreeViewer historyTree;
	private Composite referencesControl;
	private Composite historyControl;
	private Font graphFont;
	private Group enclosingComponentGroup;

	@Override
	public void createPartControl(Composite parent) {

		graphFont = new Font(parent.getDisplay(), "Courier New", 7, SWT.NONE);

		Group treeGroup = new Group(parent, SWT.SHADOW_NONE);
		treeGroup.setLayout(new FillLayout());
		treeGroup.setText("Agglomerations");

		treeViewer = new TreeViewer(treeGroup);
		treeViewer.setContentProvider(new AgglomerationContentProvider());
		treeViewer.setLabelProvider(new AgglomerationLabelProvider());

		Group detailsGroup = new Group(parent, SWT.SHADOW_NONE);
		detailsGroup.setLayout(new FillLayout());
		detailsGroup.setText("Details");

		CTabFolder detailsTab = new CTabFolder(detailsGroup, SWT.BOTTOM);

		CTabItem anomaliesTabItem = new CTabItem(detailsTab, SWT.NONE);
		anomaliesTabItem.setText("Anomalies");
		anomaliesControl = new Composite(detailsTab, SWT.NONE);
		anomaliesControl.setLayout(new FillLayout());
		anomaliesTabItem.setControl(anomaliesControl);
		anomaliesViewer = new TreeViewer(anomaliesControl);
		anomaliesViewer.setLabelProvider(new AnomaliesLabelProvider());
		anomaliesViewer.setContentProvider(new AnomaliesContentProvider());

		CTabItem descriptionTabItem = new CTabItem(detailsTab, SWT.NONE);
		descriptionTabItem.setText("Description");
		descriptionControl = new Composite(detailsTab, SWT.NONE);
		descriptionControl.setLayout(new FillLayout());
		descriptionTabItem.setControl(descriptionControl);

		descriptionText = new StyledText(descriptionControl, SWT.WRAP
				| SWT.V_SCROLL | SWT.H_SCROLL);
		descriptionText.setEditable(false);

		CTabItem referencesTabItem = new CTabItem(detailsTab, SWT.NONE);
		referencesTabItem.setText("References");
		referencesControl = new Composite(detailsTab, SWT.NONE);
		referencesControl.setLayout(new FillLayout());
		referencesTabItem.setControl(referencesControl);

		referencesViewer = new TreeViewer(referencesControl);
		referencesViewer.setLabelProvider(new ReferencesLabelProvider());
		referencesViewer.setContentProvider(new ReferencesContentProvider());

		CTabItem graphTabItem = new CTabItem(detailsTab, SWT.NONE);
		graphTabItem.setText("Graph");
		graphControl = new Composite(detailsTab, SWT.NONE);
		graphControl.setLayout(new FillLayout());
		graphTabItem.setControl(graphControl);

		CTabItem historyTabItem = new CTabItem(detailsTab, SWT.NONE);
		historyTabItem.setText("History");
		historyControl = new Composite(detailsTab, SWT.NONE);
		historyControl.setLayout(new FillLayout());
		historyTabItem.setControl(historyControl);

		historyTree = new TreeViewer(historyControl);
		historyTree.setContentProvider(new HistoryContentProvider());
		historyTree.setLabelProvider(new HistoryLabelProvider());

		detailsTab.setSelection(anomaliesTabItem);

		createDoubleClickAction();
		createClickActions();
	}

	@Override
	public void setFocus() {
		// For this plugin no action is required when this view receives the
		// focus
	}

	/**
	 * Updates the content that is displayed in this view
	 * 
	 * @param results
	 */
	public void updateContent(WorkspaceResults results) {
		treeViewer.setInput(results);
	}

	/**
	 * Creates the event that is called when the user double-click a node in the
	 * tree view
	 */
	private void createDoubleClickAction() {
		anomaliesViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				ISelection selection = event.getSelection();
				if (selection instanceof ITreeSelection) {
					ITreeSelection structuredSelection = (ITreeSelection) selection;
					Object objSel = structuredSelection.getFirstElement();

					if (objSel instanceof CodeSmell) {
						CodeSmell anomaly = (CodeSmell) objSel;
						CompilationUnit cu = (CompilationUnit) anomaly
								.getElement().getRoot();
						try {
							ITextEditor textEditor = (ITextEditor) JavaUI
									.openInEditor(cu.getJavaElement());
							textEditor.selectAndReveal(anomaly.getElement()
									.getStartPosition(), 1);
						} catch (PartInitException e) {
							e.printStackTrace();
						} catch (JavaModelException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});

		referencesViewer.addDoubleClickListener(new IDoubleClickListener() {
			@SuppressWarnings("rawtypes")
			@Override
			public void doubleClick(DoubleClickEvent event) {

				ISelection selection = event.getSelection();
				if (selection instanceof ITreeSelection) {
					ITreeSelection structuredSelection = (ITreeSelection) selection;
					Object objSel = structuredSelection.getFirstElement();

					if (objSel instanceof ReferenceHolder) {
						ReferenceHolder reference = (ReferenceHolder) objSel;
						try {
							ITextEditor textEditor = (ITextEditor) JavaUI.openInEditor(reference.getJavaElement());
							int lineNumber = reference.getLineNumber();

							IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
							if (document != null) {
								IRegion lineInfo = null;

								// line count internaly starts with 0, and not
								// with 1 like in GUI
								lineInfo = document.getLineInformation(lineNumber - 1);

								if (lineInfo != null) {
									textEditor.selectAndReveal(
											lineInfo.getOffset(),
											lineInfo.getLength());
								}
							}

						} catch (PartInitException e) {
							e.printStackTrace();
						} catch (JavaModelException e) {
							e.printStackTrace();
						} catch (BadLocationException e) {
							System.err.println(e);
						}
					}
				}
			}
		});
	}

	/**
	 * Creates the event that is called when the user double-click a node in the
	 * tree view
	 */
	private void createClickActions() {
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				if (selection instanceof ITreeSelection) {
					if (graph != null) {
						graph.dispose();
						graph = null;
					}
					if (enclosingComponentGroup != null) {
						enclosingComponentGroup.dispose();
						enclosingComponentGroup = null;
					}

					ITreeSelection structuredSelection = (ITreeSelection) selection;
					Object objSel = structuredSelection.getFirstElement();

					if (objSel instanceof AgglomerationModel) {
						AgglomerationModel agglomeration = (AgglomerationModel) objSel;

						updateAnomalies(agglomeration);
						updateLabelDescription(agglomeration);
						updateGraph(agglomeration);
						updateDependencies(agglomeration);
						updateHistory(agglomeration);

					} else {
						anomaliesViewer.setInput(null);
						descriptionText.setText("");
						graph = new Graph(graphControl, SWT.NONE);
						referencesViewer.setInput(null);
						historyTree.setInput(null);
					}

					descriptionControl.layout();
				}
			}
		});
	}

	protected void updateAnomalies(AgglomerationModel agglomeration) {
		anomaliesViewer.setInput(agglomeration.getCodeAnomalies());
		anomaliesControl.layout();
	}

	protected void updateHistory(AgglomerationModel agglomeration) {
		historyTree.setInput(agglomeration.getHistory());
		historyControl.layout();
	}

	protected void updateDependencies(AgglomerationModel agglomeration) {
		HashMap<String, ArrayList<ReferenceHolder>> mapRefs = new HashMap<String, ArrayList<ReferenceHolder>>();

		for (CodeSmell smell : agglomeration.getCodeAnomalies()) {

			ArrayList<ReferenceHolder> refNames = mapRefs.get(smell
					.getElementName());
			if (refNames != null)
				continue;

			refNames = new ArrayList<ReferenceHolder>();
			mapRefs.put(smell.getElementName(), refNames);

			Set<IJavaElement> refs = smell.getReferencesToAnomalousElement();
			
			if (refs.size() > 0) {
				for (IJavaElement element : refs) {

					if (element instanceof ResolvedSourceMethod) {
						refNames.add(new MethodReferenceHolder(
								(ResolvedSourceMethod) element));
					} else if (element instanceof ResolvedSourceType) {
						refNames.add(new TypeReferenceHolder(
								(ResolvedSourceType) element));
					}
				}
			}
		}

		referencesViewer.setInput(mapRefs);
		referencesControl.layout();
	}

	protected void updateLabelDescription(AgglomerationModel agglomeration) {
		String text = agglomeration.getDescription();

		if (agglomeration instanceof ConcernOverloadAgglomeration) {
			ConcernOverloadAgglomeration concernOverload = (ConcernOverloadAgglomeration) agglomeration;
			text += "\n\nMain concern:\n" + concernOverload.getCommonConcern();
			text += "\n\nCrosscutting concerns:\n"
					+ concernOverload.getOtherConcerns();
		}

		text += "\n\nNumber of Anomalies: "
				+ agglomeration.getNumberOfAnomalies();

		text += "\n\n\n-------- Types of Anomalies --------\n";
		text += agglomeration.getAnomaliesSummary();

		descriptionText.setText(text);
	}

	protected void updateGraph(AgglomerationModel agglomeration) {
		if (agglomeration instanceof HierarchicalAgglomeration)
			createHierarchicalGraph(agglomeration);

		if (agglomeration instanceof IntraClassAgglomeration)
			createIntraClassGraph(agglomeration);

		if (agglomeration instanceof IntraComponentAgglomeration) {
			createIntraComponentGraph(agglomeration);
		}

		if (agglomeration instanceof IntraMethodAgglomeration) {
			createIntraMethodGraph(agglomeration);
		}

		if (agglomeration instanceof ConcernOverloadAgglomeration) {
			createConcernOverloadGraph(agglomeration);
		}

		graphControl.layout();
	}

	private void createConcernOverloadGraph(AgglomerationModel agglomeration) {
		ConcernOverloadAgglomeration concernOverload = (ConcernOverloadAgglomeration) agglomeration;

		enclosingComponentGroup = new Group(graphControl, SWT.SHADOW_OUT);
		enclosingComponentGroup.setLayout(new FillLayout());
		enclosingComponentGroup.setText(concernOverload.getCommonConcern()
				.toString());

		graph = new Graph(enclosingComponentGroup, SWT.NONE);
		graph.setFont(graphFont);

		HashMap<String, Set<CodeSmell>> smellsOfClasses = new HashMap<String, Set<CodeSmell>>();

		for (CodeSmell cs : concernOverload.getCodeAnomalies()) {
			String className = cs.getMainClass().getName().toString();
			Set<CodeSmell> smells = smellsOfClasses.get(className);
			if (smells == null) {
				smells = new HashSet<CodeSmell>();
				smellsOfClasses.put(className, smells);
			}
			smells.add(cs);
		}

		for (Entry<String, Set<CodeSmell>> es : smellsOfClasses.entrySet()) {
			String smellsMessage = "Code Smells: \n" + summarizeSmells(es.getValue());
			
			smellsMessage += "\nConcerns:";
			List<Concern> concernsOfClass = Concern.getConcernsOfClass(((CodeSmell)es.getValue().toArray()[0]).getMainClass());
			for (Concern concern : concernsOfClass) {
				smellsMessage += "\n- " + concern.getConcernName();
			}
			
			IFigure tp = ZestUtils.createWarningToolTip(smellsMessage);

			String name = es.getKey();
			GraphNode node = new GraphNode(graph, SWT.NONE, name);
			node.setTooltip(tp);
		}

		enclosingComponentGroup.setBackground(graph.getDisplay()
				.getSystemColor(SWT.COLOR_RED));
		graph.setLayoutAlgorithm(new GridLayoutAlgorithm(), true);
	}

	private void createIntraMethodGraph(AgglomerationModel agglomeration) {
		graph = new Graph(graphControl, SWT.NONE);
		graph.setFont(graphFont);

		IntraMethodAgglomeration intraMethod = (IntraMethodAgglomeration) agglomeration;

		String methodName = intraMethod.getAffectedMethod().getName()
				.toString();
		GraphNode enclosingMethodNode = new GraphNode(graph, SWT.NONE,
				methodName);
		enclosingMethodNode.setBorderColor(graph.getDisplay().getSystemColor(
				SWT.COLOR_RED));
		String toolTipMessage = summarizeSmells(intraMethod.getCodeAnomalies());
		IFigure tp = ZestUtils.createWarningToolTip(toolTipMessage);
		enclosingMethodNode.setTooltip(tp);

		for (CodeSmell cs : intraMethod.getCodeAnomaliesSortedByName()) {
			String name = cs.getKindOfSmellName();
			GraphNode node = new GraphNode(graph, SWT.NONE, name);
			node.setBackgroundColor(graph.getDisplay().getSystemColor(
					SWT.COLOR_RED));

			GraphConnection connection = new GraphConnection(graph,
					ZestStyles.CONNECTIONS_DIRECTED, node, enclosingMethodNode);
			connection.changeLineColor(graph.getDisplay().getSystemColor(
					SWT.COLOR_RED));
		}

		graph.setLayoutAlgorithm(new HorizontalTreeLayoutAlgorithm(), true);
	}

	private void createIntraComponentGraph(AgglomerationModel agglomeration) {
		IntraComponentAgglomeration intraComponent = (IntraComponentAgglomeration) agglomeration;

		enclosingComponentGroup = new Group(graphControl, SWT.SHADOW_OUT);
		enclosingComponentGroup.setLayout(new FillLayout());
		enclosingComponentGroup
				.setText(intraComponent.getComponent().getName());

		graph = new Graph(enclosingComponentGroup, SWT.NONE);
		graph.setFont(graphFont);

		HashMap<String, Set<CodeSmell>> smellsOfClasses = new HashMap<String, Set<CodeSmell>>();

		for (CodeSmell cs : intraComponent.getCodeAnomalies()) {
			String className = cs.getMainClass().getName().toString();
			Set<CodeSmell> smells = smellsOfClasses.get(className);
			if (smells == null) {
				smells = new HashSet<CodeSmell>();
				smellsOfClasses.put(className, smells);
			}
			smells.add(cs);
		}

		for (Entry<String, Set<CodeSmell>> es : smellsOfClasses.entrySet()) {
			String toolTipMessage = summarizeSmells(es.getValue());
			IFigure tp = ZestUtils.createWarningToolTip(toolTipMessage);

			String name = es.getKey();
			GraphNode node = new GraphNode(graph, SWT.NONE, name);
			node.setTooltip(tp);
		}

		enclosingComponentGroup.setBackground(graph.getDisplay()
				.getSystemColor(SWT.COLOR_RED));
		graph.setLayoutAlgorithm(new GridLayoutAlgorithm(), true);
	}

	private void createIntraClassGraph(AgglomerationModel agglomeration) {
		graph = new Graph(graphControl, SWT.NONE);
		graph.setFont(graphFont);

		IntraClassAgglomeration intraClass = (IntraClassAgglomeration) agglomeration;

		HashMap<String, Set<CodeSmell>> smellsOfElements = new HashMap<String, Set<CodeSmell>>();
		String className = intraClass.getEnclosingClass().getName().toString();
		GraphNode enclosingClassNode = new GraphNode(graph, SWT.NONE, className);
		enclosingClassNode.setBorderColor(graph.getDisplay().getSystemColor(
				SWT.COLOR_RED));

		for (CodeSmell cs : intraClass.getCodeAnomalies()) {
			String elementName = cs.getElementName();
			Set<CodeSmell> smells = smellsOfElements.get(elementName);
			if (smells == null) {
				smells = new HashSet<CodeSmell>();
				smellsOfElements.put(elementName, smells);
			}
			smells.add(cs);
		}

		for (Entry<String, Set<CodeSmell>> es : smellsOfElements.entrySet()) {
			String toolTipMessage = summarizeSmells(es.getValue());
			IFigure tp = ZestUtils.createWarningToolTip(toolTipMessage);

			String name = es.getKey();
			if (name.contains(".")) {
				name = name.split("\\.")[1];
				GraphNode node = new GraphNode(graph, SWT.NONE, name);
				node.setBackgroundColor(graph.getDisplay().getSystemColor(
						SWT.COLOR_GRAY));
				node.setTooltip(tp);

				GraphConnection connection = new GraphConnection(graph,
						ZestStyles.CONNECTIONS_DOT, node, enclosingClassNode);
				connection.changeLineColor(graph.getDisplay().getSystemColor(
						SWT.COLOR_RED));

			} else {
				enclosingClassNode.setTooltip(tp);
			}
		}
		graph.setLayoutAlgorithm(new HorizontalTreeLayoutAlgorithm(), true);
	}

	private void createHierarchicalGraph(AgglomerationModel agglomeration) {
		graph = new Graph(graphControl, SWT.NONE);
		graph.setFont(graphFont);
		HierarchicalAgglomeration hierarchical = (HierarchicalAgglomeration) agglomeration;

		HashMap<String, HashSet<CodeSmell>> classToSmells = new HashMap<String, HashSet<CodeSmell>>();
		HashMap<String, GraphNode> nodes = new HashMap<String, GraphNode>();
		String rootName = hierarchical.getRoot().resolveBinding()
				.getQualifiedName();
		GraphNode root = new GraphNode(graph, SWT.NONE, hierarchical.getRoot()
				.getName().toString());
		root.setData("class", hierarchical.getRoot());
		nodes.put(rootName, root);

		for (CodeSmell cs : hierarchical.getCodeAnomalies()) {
			TypeDeclaration class_ = cs.getMainClass();
			String nodeName = class_.resolveBinding().getQualifiedName();

			if (!nodes.containsKey(nodeName)) {
				GraphNode node = new GraphNode(graph, SWT.NONE, class_
						.getName().toString());
				node.setData("class", class_);
				nodes.put(nodeName, node);
			}

			HashSet<CodeSmell> smells = classToSmells.get(nodeName);
			if (smells == null) {
				smells = new HashSet<CodeSmell>();
				classToSmells.put(nodeName, smells);
			}
			smells.add(cs);
		}

		for (Entry<String, GraphNode> node : nodes.entrySet()) {
			HashSet<CodeSmell> smells = classToSmells.get(node.getKey());

			if (smells != null) {
				String toolTip = summarizeSmells(smells);
				node.getValue().setTooltip(
						ZestUtils.createWarningToolTip(toolTip));
			}

			if (!node.getValue().equals(root)) {
				TypeDeclaration class_ = (TypeDeclaration) node.getValue()
						.getData("class");
				String superclassName = class_.getSuperclassType()
						.resolveBinding().getQualifiedName();
				GraphNode father = nodes.get(superclassName);

				// TODO fix this
				if (father == null) {
					father = root;
				}

				GraphConnection connection = new GraphConnection(graph,
						ZestStyles.CONNECTIONS_DIRECTED, node.getValue(),
						father);
				connection.changeLineColor(graph.getDisplay().getSystemColor(
						SWT.COLOR_RED));
			}
		}
		graph.setLayoutAlgorithm(new TreeLayoutAlgorithm(), true);
	}

	private String summarizeSmells(Set<CodeSmell> hashSet) {
		String message = "";
		for (CodeSmell codeSmell : hashSet) {
			message += "- " + codeSmell.getKindOfSmellName() + " in "
					+ codeSmell.getElementName() + "\n";
		}

		return message;
	}
}
