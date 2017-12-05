package br.pucrio.inf.organic.extensions.ui.agglomeration;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import br.pucrio.inf.organic.extensionPoints.AgglomerationModel;

/**
 * Tree content provider for agglomerations
 * @author Willian
 *
 */
public class AgglomerationContentProvider implements ITreeContentProvider {

	
	@Override
	public void dispose() {
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (oldInput != null && newInput != null 
				&& newInput instanceof WorkspaceResults
				&& oldInput instanceof WorkspaceResults) {
			((WorkspaceResults)newInput).mergeResultsWith((WorkspaceResults) oldInput);
		}
	}

	@Override
	public Object[] getChildren(Object node) {
		if (node instanceof WorkspaceResults) {
			return ((WorkspaceResults)node).getSortedResultsAsArray();
		}
		if (node instanceof ProjectResults) {
			return ((ProjectResults)node).getAgglomerationTypesAsArray();
		}
		if (node instanceof AgglomerationTypeResults) {
			return ((AgglomerationTypeResults)node).getAgglomerations().toArray();
		}
		
		return null;
	}

	@Override
	public Object[] getElements(Object node) {
		if (node instanceof WorkspaceResults) {
			return ((WorkspaceResults)node).getSortedResultsAsArray();
		}
		if (node instanceof ProjectResults) {
			return ((ProjectResults)node).getAgglomerationTypesAsArray();
		}
		if (node instanceof AgglomerationTypeResults) {
			return ((AgglomerationTypeResults)node).getAgglomerations().toArray();
		}
		
		return null;
	}

	@Override
	public Object getParent(Object node) {		
		return null;
	}

	@Override
	public boolean hasChildren(Object node) {
		return (node instanceof WorkspaceResults || node instanceof ProjectResults || node instanceof AgglomerationTypeResults);
	}

}
