/*******************************************************************************
 * Copyright (c) 2013 Ericsson
 * 
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Jacques Bouthillier - Initial implementation
 ******************************************************************************/

package org.eclipse.mylyn.reviews.r4e_gerrit.ui.internal.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.reviews.r4e_gerrit.core.R4EGerritQueryHandler;
import org.eclipse.mylyn.reviews.r4e_gerrit.core.R4EGerritReviewSummary;
import org.eclipse.mylyn.reviews.r4e_gerrit.ui.R4EGerritUi;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Jacques Bouthillier
 * @version $Revision: 1.0 $
 * 
 */
public class R4EQueryUtil {



    /**
     * 
     */
    public static List<R4EGerritReviewSummary> getReviewListFromRepository(TaskRepository repository, String query) {
        List<R4EGerritReviewSummary> results = new ArrayList<R4EGerritReviewSummary>();
        R4EGerritQueryHandler handler = new R4EGerritQueryHandler(repository, query);
        IStatus status = handler.performQuery();
        if (status.isOK()) {
            for (R4EGerritReviewSummary summary : handler.getQueryResult()) {
                if (summary.getAttribute(R4EGerritReviewSummary.DATE_COMPLETION) == null) {
                    R4EGerritUi.Ftracer.traceInfo(summary.toString());
                    results.add(summary);
                }
            }
        } else {
        	R4EGerritUi.Ftracer.traceWarning(status.toString());
        	String msg = "Unable to read the Gerrit server.";
        	UIUtils.showErrorDialog(msg, status.toString());
        }
        return results;
    }

}
