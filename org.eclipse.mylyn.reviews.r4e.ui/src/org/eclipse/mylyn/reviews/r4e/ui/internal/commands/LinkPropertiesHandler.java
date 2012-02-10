/*******************************************************************************
 * Copyright (c) 2011 Ericsson Research Canada
 * 
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Description:
 * 
 * This class implements the navigator view toolbar command to link the R4E properties 
 * view to the review navigator
 * 
 * Contributors:
 *   Sebastien Dubois - Created for Mylyn Review R4E project
 *   
 ******************************************************************************/
package org.eclipse.mylyn.reviews.r4e.ui.internal.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.mylyn.reviews.r4e.ui.R4EUIPlugin;
import org.eclipse.mylyn.reviews.r4e.ui.internal.model.R4EUIModelController;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author lmcdubo
 * @version $Revision: 1.0 $
 */
public class LinkPropertiesHandler extends AbstractHandler {

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * Method execute.
	 * 
	 * @param aEvent
	 *            ExecutionEvent
	 * @return Object
	 * @throws ExecutionException
	 * @see org.eclipse.core.commands.IHandler#execute(ExecutionEvent)
	 */
	public Object execute(ExecutionEvent aEvent) throws ExecutionException {

		//We need to preserve the expansion state and restore it afterwards
		final Object[] elements = R4EUIModelController.getNavigatorView().getTreeViewer().getExpandedElements();
		final Command command = aEvent.getCommand();
		boolean oldValue = HandlerUtil.toggleCommandState(command);

		if (!oldValue) {
			R4EUIPlugin.Ftracer.traceInfo("Linking Properties view with ReviewNavigator");
		} else {
			R4EUIPlugin.Ftracer.traceInfo("Unlinking Properties view with ReviewNavigator");
		}
		R4EUIModelController.getNavigatorView().setPropertiesLinked(!oldValue);
		R4EUIModelController.getNavigatorView().getTreeViewer().setExpandedElements(elements);
		return null;
	}

}
