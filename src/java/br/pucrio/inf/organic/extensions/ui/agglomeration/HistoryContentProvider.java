package br.pucrio.inf.organic.extensions.ui.agglomeration;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import br.pucrio.inf.organic.history.AgglomerationHistory;

/**
 * Tree content provider for history of agglomerations
 * @author Willian
 *
 */
public class HistoryContentProvider implements ITreeContentProvider {

	
	@Override
	public void dispose() {
		
	}

	@Override
	public Object[] getChildren(Object node) {
		if (node instanceof List) {
			return ((List)node).toArray();
		}
		if (node instanceof AgglomerationHistory) {
			return ((AgglomerationHistory)node).getAnomalies();
		}
		
		return null;
	}

	@Override
	public Object[] getElements(Object node) {
		if (node instanceof List) {
			return ((List)node).toArray();
		}
		if (node instanceof AgglomerationHistory) {
			return ((AgglomerationHistory)node).getAnomalies();
		}
		
		return null;
	}

	@Override
	public Object getParent(Object node) {		
		return null;
	}

	@Override
	public boolean hasChildren(Object node) {
		return (node instanceof AgglomerationHistory || node instanceof List);
	}

	@Override
	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
		
	}

}
