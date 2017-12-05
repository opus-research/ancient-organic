package br.pucrio.inf.organic.util;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class ZestUtils {

	public static IFigure createWarningToolTip(String message) {
		Image warningIcon = Display.getDefault().getSystemImage(SWT.ICON_WARNING);
		
		IFigure toolTip = new Label(message, warningIcon);
		
		return toolTip;
	}

}
