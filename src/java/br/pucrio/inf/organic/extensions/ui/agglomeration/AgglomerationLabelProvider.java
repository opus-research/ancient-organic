package br.pucrio.inf.organic.extensions.ui.agglomeration;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;

import spirit.core.smells.CodeSmell;
import br.pucrio.inf.organic.extensionPoints.AgglomerationModel;
import br.pucrio.inf.organic.extensions.OrganicActivator;

/**
 * Label provider for agglomerations.
 * @author Willian
 *
 */
public class AgglomerationLabelProvider extends StyledCellLabelProvider {
	
	private static final String ICONS_URL = "icons/";
	private static final String PROJECT_IMG = "project.png";
	private static final String AGGLOMERATION_TYPE_IMG = "agglomerationType.jpg";
	private static final String AGGLOMERATION_IMG = "agglomeration.png";
	

	@Override
	public void update(ViewerCell cell) {
		Object element = cell.getElement();
		StyledString text = new StyledString();						

		text.append(getText(element));
		cell.setImage(getImage(element));
		cell.setText(text.toString());
		cell.setStyleRanges(text.getStyleRanges());
		
		super.update(cell);
	}
	
	private String getText(Object node) {
		return node.toString();
	}

	/**
	 * Returns an Image instance for a given node
	 * according to the node's type
	 * @param node
	 * @return Image
	 */
	private Image getImage(Object node) {
		String imgUrl = null;
		
		if (node instanceof ProjectResults) {
			imgUrl = PROJECT_IMG;
		} else if (node instanceof AgglomerationTypeResults) {
			imgUrl = AGGLOMERATION_TYPE_IMG;
		} else if (node instanceof AgglomerationModel) {
			imgUrl = AGGLOMERATION_IMG;
		} 
		
		if (imgUrl != null) {
			ImageDescriptor icon;

			URL url = null;
			try {
				url = new URL(OrganicActivator.getDefault().getDescriptor().getInstallURL(),ICONS_URL + imgUrl);
			} catch (MalformedURLException e) {
				System.err.println("Exception while opening icon: " + e);
			}
			
			icon = ImageDescriptor.createFromURL(url);
			
			return icon.createImage();
		}

		return null;
	}
}
