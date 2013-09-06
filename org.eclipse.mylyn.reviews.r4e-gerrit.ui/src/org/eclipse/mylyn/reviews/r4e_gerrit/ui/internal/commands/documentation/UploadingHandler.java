/*******************************************************************************
 * Copyright (c) 2013 Ericsson
 * 
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Description:
 * 	This class implements the implementation of the R4E-Gerrit UI documentation uploading reviews handler.
 * 
 * Contributors:
 *   Jacques Bouthillier - Initial Implementation of the plug-in handler
 ******************************************************************************/

package org.eclipse.mylyn.reviews.r4e_gerrit.ui.internal.commands.documentation;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.mylyn.reviews.r4e_gerrit.ui.R4EGerritUi;
import org.eclipse.mylyn.reviews.r4e_gerrit.ui.internal.utils.R4EGerritServerUtility;

/**
 * @author Jacques Bouthillier
 * @version $Revision: 1.0 $
 *
 */

public class UploadingHandler extends AbstractHandler {

	private final String UPLOADING_DOCUMENTATION = "Documentation/user-upload.html";
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent aEvent) throws ExecutionException {
		R4EGerritUi.Ftracer.traceInfo("Search the documentation UploadingHandler  " ); //$NON-NLS-1$

		R4EGerritServerUtility.getInstance().openWebBrowser (UPLOADING_DOCUMENTATION);
		
		return null;
	}

}
