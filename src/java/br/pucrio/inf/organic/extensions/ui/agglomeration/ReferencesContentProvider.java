package br.pucrio.inf.organic.extensions.ui.agglomeration;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Tree content provider for references
 * @author Willian
 *
 */
public class ReferencesContentProvider implements ITreeContentProvider {

	
	@Override
	public void dispose() {
		
	}

	@Override
	public Object[] getChildren(Object node) {
		if (node instanceof Map) {
			return ((Map)node).entrySet().toArray();
		}
		
		if (node instanceof Entry) {
			return ((Entry<String, ArrayList<ReferenceHolder>>)node).getValue().toArray();
		}
		
		return null;
	}

	@Override
	public Object[] getElements(Object node) {
		if (node instanceof Map) {
			return ((Map)node).entrySet().toArray();
		}
		if (node instanceof Entry) {
			return ((Entry<String, ArrayList<ReferenceHolder>>)node).getValue().toArray();
		}
		
		return null;
	}

	@Override
	public Object getParent(Object node) {		
		return null;
	}

	@Override
	public boolean hasChildren(Object node) {
		return (node instanceof Map || node instanceof Entry);
	}

	@Override
	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
		
	}
}
