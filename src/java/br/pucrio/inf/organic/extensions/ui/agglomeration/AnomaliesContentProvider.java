package br.pucrio.inf.organic.extensions.ui.agglomeration;

import java.util.Set;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Tree content provider for anomalies
 * @author Willian
 *
 */
public class AnomaliesContentProvider implements ITreeContentProvider {

	
	@Override
	public void dispose() {
		
	}

	@Override
	public Object[] getChildren(Object node) {
		if (node instanceof Set) {
			return ((Set)node).toArray();
		}		
		
		return null;
	}

	@Override
	public Object[] getElements(Object node) {
		if (node instanceof Set) {
			return ((Set)node).toArray();
		}
		
		return null;
	}

	@Override
	public Object getParent(Object node) {		
		return null;
	}

	@Override
	public boolean hasChildren(Object node) {
		return (node instanceof Set);
	}

	@Override
	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
		
	}
}
