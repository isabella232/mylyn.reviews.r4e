/*******************************************************************************
 * Copyright (c) 2013 Ericsson
 * 
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Francois Chouinard - Initial implementation
 ******************************************************************************/

package org.eclipse.mylyn.reviews.r4e_gerrit.core;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.mylyn.internal.gerrit.core.GerritQueryResultSchema;
import org.eclipse.mylyn.internal.gerrit.core.GerritTaskSchema;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * A Gerrit review data entry.
 *  
 * @author Francois Chouinard
 * @version 0.1
 */
@SuppressWarnings("restriction")
public class R4EGerritReviewData {

    //-------------------------------------------------------------------------
    // Constants
    //-------------------------------------------------------------------------

    /**
     * Mylyn Task ID
     */
    public static final String TASK_ID = "r4e.mylyn.task.id"; 

    /**
     * Gerrit Review shortened Change-Id
     */
    public static final String SHORT_CHANGE_ID = TaskAttribute.TASK_KEY;

    /**
     * Gerrit Review Change-Id
     */
    public static final String CHANGE_ID = GerritQueryResultSchema.getDefault().CHANGE_ID.getKey();

    /**
     * Gerrit Review subject
     */
    public static final String SUBJECT = TaskAttribute.SUMMARY;

    /**
     * Gerrit Review owner
     */
    public static final String OWNER = GerritTaskSchema.getDefault().OWNER.getKey();

    /**
     * Gerrit Review project
     */
    public static final String PROJECT = TaskAttribute.PRODUCT; 

    /**
     * Gerrit Review branch
     */
    public static final String BRANCH = GerritTaskSchema.getDefault().BRANCH.getKey();

    /**
     * Gerrit Review creation date
     */
    public static final String DATE_CREATION = TaskAttribute.DATE_CREATION;

    /**
     * Gerrit Review last modification date
     */
    public static final String DATE_MODIFICATION = TaskAttribute.DATE_MODIFICATION;

    /**
     * Gerrit Review completion date
     */
    public static final String DATE_COMPLETION = TaskAttribute.DATE_COMPLETION;

    /**
     * Gerrit Review flags
     */
    public static final String REVIEW_FLAG_STAR = "org.eclipse.gerrit.Starred";
    public static final String REVIEW_FLAG_CI   = "org.eclipse.gerrit.Checked";
    public static final String REVIEW_FLAG_CR   = "org.eclipse.gerrit.Reviewed";
    public static final String REVIEW_FLAG_V    = "org.eclipse.gerrit.Verified";

    /**
     * Date format
     */
    private static SimpleDateFormat FORMAT_TODAY = new SimpleDateFormat("h:mm a");
    private static SimpleDateFormat FORMAT_FULL = new SimpleDateFormat("MMM d");

    //-------------------------------------------------------------------------
    // Attributes
    //-------------------------------------------------------------------------

    /*
     * Map of review attributes 
     */
    private Map<String, String> fReviewSummaryAttributes;

    //-------------------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------------------

    /**
     * Construct an R4EGerritReviewSumamry from a Gerrit query result. Some
     * fields my be missing from the task data.
     * 
     * @param result the Gerrit query result
     */
    public R4EGerritReviewData(final TaskData result) {
        TaskAttribute root = result.getRoot();
        Map<String, TaskAttribute> attributes = root.getAttributes();

        fReviewSummaryAttributes = new HashMap<String, String>();
        
        setAttribute(TASK_ID,           result.getTaskId());
        setAttribute(SHORT_CHANGE_ID,   getValue(attributes.get(SHORT_CHANGE_ID)));
        setAttribute(CHANGE_ID,         getValue(attributes.get(CHANGE_ID)));
        setAttribute(SUBJECT,           getValue(attributes.get(SUBJECT)));

        setAttribute(OWNER,             getValue(attributes.get(OWNER)));
        setAttribute(PROJECT,           getValue(attributes.get(PROJECT)));
        setAttribute(BRANCH,            getValue(attributes.get(BRANCH)));

        setAttribute(DATE_CREATION,     getValue(attributes.get(DATE_CREATION)));
        setAttribute(DATE_MODIFICATION, getValue(attributes.get(DATE_MODIFICATION)));
        setAttribute(DATE_COMPLETION,   getValue(attributes.get(DATE_COMPLETION)));

        setAttribute(REVIEW_FLAG_STAR,  getValue(attributes.get(REVIEW_FLAG_STAR)));
        setAttribute(REVIEW_FLAG_CI,    getValue(attributes.get(REVIEW_FLAG_CI)));
        setAttribute(REVIEW_FLAG_CR,    getValue(attributes.get(REVIEW_FLAG_CR)));
        setAttribute(REVIEW_FLAG_V,     getValue(attributes.get(REVIEW_FLAG_V)));
    }

    /*
     * Extract the first value from the specified task attributes list.
     * 
     * @param taskAttribute
     * @return the first value in the list (if any)
     */
    private String getValue(TaskAttribute taskAttribute) {
        if (taskAttribute != null) {
            List<String> values = taskAttribute.getValues();
            if (values.size() > 0) {
                return values.get(0);
            }
        }
        return null;
    }

    //-------------------------------------------------------------------------
    // Getters and Setters
    //-------------------------------------------------------------------------

    /**
     * @param key the review attribute key
     * @param value the review attribute value
     */
    public void setAttribute(String key, String value) {
        fReviewSummaryAttributes.put(key, value);
    }

    /**
     * @return the requested review attribute
     */
    public String getAttribute(String key) {
        return fReviewSummaryAttributes.get(key);
    }

    /**
     * @return the requested Gerrit Review attribute as a string formatted
     *         depending if the date is the same day or a different day
     */
    public String getAttributeAsDate(String aKey) {
        Date today = new Date();
        // R4EGerritPlugin.Ftracer.traceInfo("Date today:   "
        // + today );
        Date keyDate = new Date(Long.parseLong(fReviewSummaryAttributes.get(aKey)));
        if (keyDate.getDay() == today.getDay()) {
            // Same Day, so just display the time
            // R4EGerritPlugin.Ftracer.traceInfo("Format today:   "
            // + FORMAT_TODAY.format(today) );
            return FORMAT_TODAY.format(today);
        } else {
            // Another day, so display the day with the time
            // R4EGerritPlugin.Ftracer.traceInfo("Format FULL:   "
            // + FORMAT_FULL.format(keyDate) );
            return FORMAT_FULL.format(keyDate);
        }

    }

    //-------------------------------------------------------------------------
    // Object
    //-------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("TaskID  = ").append(getAttribute(R4EGerritReviewData.TASK_ID)).append("\n");
        buffer.append("ShortID = ").append(getAttribute(R4EGerritReviewData.SHORT_CHANGE_ID)).append("\n");
        buffer.append("ChangeID= ").append(getAttribute(R4EGerritReviewData.CHANGE_ID)).append("\n");
        buffer.append("Subject = ").append(getAttribute(R4EGerritReviewData.SUBJECT)).append("\n");
        buffer.append("Owner   = ").append(getAttribute(R4EGerritReviewData.OWNER)).append("\n");
        buffer.append("Project = ").append(getAttribute(R4EGerritReviewData.PROJECT)).append("\n");
        buffer.append("Branch  = ").append(getAttribute(R4EGerritReviewData.BRANCH)).append("\n");
        buffer.append("Updated = ").append(getAttributeAsDate(R4EGerritReviewData.DATE_MODIFICATION)).append("\n");
        buffer.append("STAR = ").append(getAttribute(R4EGerritReviewData.REVIEW_FLAG_STAR))
              .append(", CR = ").append(getAttribute(R4EGerritReviewData.REVIEW_FLAG_CR))
              .append(", IC = ").append(getAttribute(R4EGerritReviewData.REVIEW_FLAG_CI))
              .append(", Ve = ").append(getAttribute(R4EGerritReviewData.REVIEW_FLAG_V)).append("\n");
        return buffer.toString();
    }

}
