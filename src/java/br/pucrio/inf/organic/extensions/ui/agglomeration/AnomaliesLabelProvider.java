package br.pucrio.inf.organic.extensions.ui.agglomeration;

import java.net.MalformedURLException;
import java.net.URI;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;

import spirit.core.smells.CodeSmell;

/**
 * Label provider for anomalies.
 * 
 * @author Willian
 *
 */
public class AnomaliesLabelProvider extends StyledCellLabelProvider {

	private static final String ANOMALY_IMG = "anomaly.png";
	private static final String ICONS_URL = "platform:/plugin/br.pucrio.inf.organic/icons/";

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
	 * Returns an Image instance for a given node according to the node's type
	 * 
	 * @param node
	 * @return Image
	 */
	private Image getImage(Object node) {
		URI url = null;
		
		try {
			
			if (node instanceof CodeSmell) {
				url = URI.create(ICONS_URL + ANOMALY_IMG);
			}

			if (url != null) {
				ImageDescriptor icon;
				icon = ImageDescriptor.createFromURL(url.toURL());
				return icon.createImage();
			}
		
		} catch (MalformedURLException e) {
			System.err.println("Exception while opening icon: " + e);
		}

		return null;
	}
}
