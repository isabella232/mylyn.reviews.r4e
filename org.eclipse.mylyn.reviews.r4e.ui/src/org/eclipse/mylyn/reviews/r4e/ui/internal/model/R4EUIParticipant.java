// $codepro.audit.disable com.instantiations.assist.eclipse.analysis.audit.rule.effectivejava.alwaysOverridetoString.alwaysOverrideToString, com.instantiations.assist.eclipse.analysis.deserializeabilitySecurity, com.instantiations.assist.eclipse.analysis.disallowReturnMutable, com.instantiations.assist.eclipse.analysis.enforceCloneableUsageSecurity, com.instantiations.assist.eclipse.analysis.mutabilityOfArrays, explicitThisUsage
/*******************************************************************************
 * Copyright (c) 2010 Ericsson Research Canada
 * 
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Description:
 * 
 * This class implements the Participant element of the UI model
 * 
 * Contributors:
 *   Sebastien Dubois - Created for Mylyn Review R4E project
 *   
 ******************************************************************************/

package org.eclipse.mylyn.reviews.r4e.ui.internal.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import org.eclipse.emf.common.util.EList;
import org.eclipse.mylyn.reviews.frame.core.model.ReviewComponent;
import org.eclipse.mylyn.reviews.r4e.core.model.R4EParticipant;
import org.eclipse.mylyn.reviews.r4e.core.model.R4EReviewComponent;
import org.eclipse.mylyn.reviews.r4e.core.model.R4EReviewPhase;
import org.eclipse.mylyn.reviews.r4e.core.model.R4EReviewState;
import org.eclipse.mylyn.reviews.r4e.core.model.R4EReviewType;
import org.eclipse.mylyn.reviews.r4e.core.model.R4EUserRole;
import org.eclipse.mylyn.reviews.r4e.core.model.serial.impl.OutOfSyncException;
import org.eclipse.mylyn.reviews.r4e.core.model.serial.impl.ResourceHandlingException;
import org.eclipse.mylyn.reviews.r4e.ui.R4EUIPlugin;
import org.eclipse.mylyn.reviews.r4e.ui.internal.properties.general.ParticipantProperties;
import org.eclipse.mylyn.reviews.r4e.ui.internal.utils.R4EUIConstants;
import org.eclipse.mylyn.reviews.r4e.ui.internal.utils.UIUtils;
import org.eclipse.mylyn.reviews.userSearch.query.IQueryUser;
import org.eclipse.mylyn.reviews.userSearch.query.QueryUserFactory;
import org.eclipse.mylyn.reviews.userSearch.userInfo.IUserInfo;
import org.eclipse.ui.views.properties.IPropertySource;

/**
 * @author lmcdubo
 * @version $Revision: 1.0 $
 */
public class R4EUIParticipant extends R4EUIModelElement {

	// ------------------------------------------------------------------------
	// Constants
	// ------------------------------------------------------------------------

	/**
	 * Field PARTICIPANT_ICON_FILE. (value is ""icons/obj16/part_obj.png"")
	 */
	public static final String PARTICIPANT_ICON_FILE = "icons/obj16/part_obj.png";

	/**
	 * Field PARTICIPANT_REVIEWER_ICON_FILE. (value is ""icons/obj16/partrevr_obj.png"")
	 */
	public static final String PARTICIPANT_REVIEWER_ICON_FILE = "icons/obj16/partrevr_obj.png";

	/**
	 * Field PARTICIPANT_LEAD_ICON_FILE. (value is ""icons/obj16/partlead_obj.png"")
	 */
	public static final String PARTICIPANT_LEAD_ICON_FILE = "icons/obj16/partlead_obj.png";

	/**
	 * Field PARTICIPANT_AUTHOR_ICON_FILE. (value is ""icons/obj16/partauthr_obj.png"")
	 */
	public static final String PARTICIPANT_AUTHOR_ICON_FILE = "icons/obj16/partauthr_obj.png";

	/**
	 * Field PARTICIPANT_ORGANIZER_ICON_FILE. (value is ""icons/obj16/partorg_obj.png"")
	 */
	public static final String PARTICIPANT_ORGANIZER_ICON_FILE = "icons/obj16/partorg_obj.png";

	/**
	 * Field REMOVE_ELEMENT_ACTION_NAME. (value is ""Disable Participant"")
	 */
	private static final String REMOVE_ELEMENT_COMMAND_NAME = "Disable Participant";

	/**
	 * Field REMOVE_ELEMENT_ACTION_TOOLTIP. (value is ""Disable (and Optionally Remove) this Participant"")
	 */
	private static final String REMOVE_ELEMENT_COMMAND_TOOLTIP = "Disable (and Optionally Remove) this Participant";

	/**
	 * Field RESTORE_ELEMENT_COMMAND_NAME. (value is ""Restore Participant"")
	 */
	private static final String RESTORE_ELEMENT_COMMAND_NAME = "Restore Participant";

	/**
	 * Field RESTORE_ELEMENT_ACTION_TOOLTIP. (value is ""Restore this disabled Participant"")
	 */
	private static final String RESTORE_ELEMENT_COMMAND_TOOLTIP = "Restore this disabled Participant";

	// ------------------------------------------------------------------------
	// Member variables
	// ------------------------------------------------------------------------

	/**
	 * Field fParticipant.
	 */
	private final R4EParticipant fParticipant;

	/**
	 * Field fParticipantDetails.
	 */
	private String fParticipantDetails = null;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	/**
	 * Constructor for R4EUIParticipant.
	 * 
	 * @param aParent
	 *            IR4EUIModelElement
	 * @param aParticipant
	 *            R4EParticipant
	 */
	public R4EUIParticipant(IR4EUIModelElement aParent, R4EParticipant aParticipant, R4EReviewType aType) {
		super(aParent, aParticipant.getId());
		fReadOnly = aParent.isReadOnly();
		fParticipant = aParticipant;
		setRoleIcon(aType);
		return;
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * Method getToolTip.
	 * 
	 * @return String
	 * @see org.eclipse.mylyn.reviews.r4e.ui.internal.model.IR4EUIModelElement#getToolTip()
	 */
	@Override
	public String getToolTip() {
		return fParticipant.getEmail();
	}

	/**
	 * Method getAdapter.
	 * 
	 * @param adapter
	 *            Class
	 * @return Object
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(Class)
	 */
	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes")
	Class adapter) {
		if (IR4EUIModelElement.class.equals(adapter)) {
			return this;
		}
		if (IPropertySource.class.equals(adapter)) {
			return new ParticipantProperties(this);
		}
		return null;
	}

	/**
	 * Method getParticipant.
	 * 
	 * @return R4EParticipant
	 */
	public R4EParticipant getParticipant() {
		return fParticipant;
	}

	/**
	 * Method setRoleIcon. Set particpant icon based on most significant role
	 */
	public void setRoleIcon(R4EReviewType aType) {
		if (aType.equals(R4EReviewType.R4E_REVIEW_TYPE_BASIC)) {
			setImage(PARTICIPANT_ICON_FILE);
		} else {
			final EList<R4EUserRole> roles = fParticipant.getRoles();
			//First check for Lead
			for (R4EUserRole role : roles) {
				if (role.equals(R4EUserRole.R4E_ROLE_LEAD)) {
					setImage(PARTICIPANT_LEAD_ICON_FILE);
					return;
				}
			}
			//Next Organizer
			for (R4EUserRole role : roles) {
				if (role.equals(R4EUserRole.R4E_ROLE_ORGANIZER)) {
					setImage(PARTICIPANT_ORGANIZER_ICON_FILE);
					return;
				}
			}
			//Next Author
			for (R4EUserRole role : roles) {
				if (role.equals(R4EUserRole.R4E_ROLE_AUTHOR)) {
					setImage(PARTICIPANT_AUTHOR_ICON_FILE);
					return;
				}
			}
			//Finally Reviewer
			for (R4EUserRole role : roles) {
				if (role.equals(R4EUserRole.R4E_ROLE_REVIEWER)) {
					setImage(PARTICIPANT_REVIEWER_ICON_FILE);
					return;
				}
			}
			//If no role, set default icon
			setImage(PARTICIPANT_ICON_FILE);
		}
	}

	/**
	 * Method getRoles.
	 * 
	 * @param aRoles
	 *            EList<R4EUserRole>
	 * @return String[]
	 */
	public String[] getRoles(EList<R4EUserRole> aRoles) {
		final List<String> roles = new ArrayList<String>();
		for (R4EUserRole role : aRoles) {
			if (role.getValue() == R4EUserRole.R4E_ROLE_ORGANIZER_VALUE) {
				roles.add(R4EUIConstants.USER_ROLE_ORGANIZER);
			} else if (role.getValue() == R4EUserRole.R4E_ROLE_LEAD_VALUE) {
				roles.add(R4EUIConstants.USER_ROLE_LEAD);
			} else if (role.getValue() == R4EUserRole.R4E_ROLE_AUTHOR_VALUE) {
				roles.add(R4EUIConstants.USER_ROLE_AUTHOR);
			} else if (role.getValue() == R4EUserRole.R4E_ROLE_REVIEWER_VALUE) {
				roles.add(R4EUIConstants.USER_ROLE_REVIEWER);
			}
		}
		return roles.toArray(new String[roles.size()]);
	}

	/**
	 * Method mapStringToRole.
	 * 
	 * @param aRoleStr
	 *            String
	 * @return R4EUserRole
	 */
	public static R4EUserRole mapStringToRole(String aRoleStr) {
		if (aRoleStr.equals(R4EUIConstants.USER_ROLE_ORGANIZER)) {
			return R4EUserRole.R4E_ROLE_ORGANIZER;
		} else if (aRoleStr.equals(R4EUIConstants.USER_ROLE_LEAD)) {
			return R4EUserRole.R4E_ROLE_LEAD;
		}
		if (aRoleStr.equals(R4EUIConstants.USER_ROLE_AUTHOR)) {
			return R4EUserRole.R4E_ROLE_AUTHOR;
		} else if (aRoleStr.equals(R4EUIConstants.USER_ROLE_REVIEWER)) {
			return R4EUserRole.R4E_ROLE_REVIEWER;
		}
		return null;
	}

	/**
	 * Method mapRoleToString.
	 * 
	 * @param aRole
	 *            R4EUserRole
	 * @return String
	 */
	public static String mapRoleToString(R4EUserRole aRole) {
		if (aRole.equals(R4EUserRole.R4E_ROLE_ORGANIZER)) {
			return R4EUIConstants.USER_ROLE_ORGANIZER;
		} else if (aRole.equals(R4EUserRole.R4E_ROLE_LEAD)) {
			return R4EUIConstants.USER_ROLE_LEAD;
		}
		if (aRole.equals(R4EUserRole.R4E_ROLE_AUTHOR)) {
			return R4EUIConstants.USER_ROLE_AUTHOR;
		} else if (aRole.equals(R4EUserRole.R4E_ROLE_REVIEWER)) {
			return R4EUIConstants.USER_ROLE_REVIEWER;
		}
		return null;
	}

	/**
	 * Method isEnabled.
	 * 
	 * @return boolean
	 * @see org.eclipse.mylyn.reviews.r4e.ui.internal.model.IR4EUIModelElement#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return fParticipant.isEnabled();
	}

	/**
	 * Method setEnabled.
	 * 
	 * @param aEnabled
	 *            boolean
	 * @throws ResourceHandlingException
	 * @throws OutOfSyncException
	 * @see org.eclipse.mylyn.reviews.r4e.ui.internal.model.IR4EUIModelElement#setUserReviewed(boolean)
	 */
	@Override
	public void setEnabled(boolean aEnabled) throws ResourceHandlingException, OutOfSyncException {
		final Long bookNum = R4EUIModelController.FResourceUpdater.checkOut(fParticipant,
				R4EUIModelController.getReviewer());
		fParticipant.setEnabled(true);
		R4EUIModelController.FResourceUpdater.checkIn(bookNum);
		R4EUIModelController.getNavigatorView().getTreeViewer().refresh();
	}

	/**
	 * Set serialization model data by copying it from the passed-in object
	 * 
	 * @param aModelComponent
	 *            - a serialization model element to copy information from
	 * @throws ResourceHandlingException
	 * @throws OutOfSyncException
	 * @see org.eclipse.mylyn.reviews.r4e.ui.internal.model.IR4EUIModelElement#setModelData(R4EReviewComponent)
	 */
	@Override
	public void setModelData(ReviewComponent aModelComponent) throws ResourceHandlingException, OutOfSyncException {
		//Set data in model element
		final Long bookNum = R4EUIModelController.FResourceUpdater.checkOut(fParticipant,
				R4EUIModelController.getReviewer());
		fParticipant.setId(((R4EParticipant) aModelComponent).getId());
		fParticipant.setEmail(((R4EParticipant) aModelComponent).getEmail());
		fParticipant.getRoles().addAll(((R4EParticipant) aModelComponent).getRoles());
		fParticipant.setFocusArea(((R4EParticipant) aModelComponent).getFocusArea());
		R4EUIModelController.FResourceUpdater.checkIn(bookNum);
	}

	/**
	 * Method getParticipantDetails.
	 * 
	 * @return String
	 */
	public String getParticipantDetails() {
		return fParticipantDetails;
	}

	/**
	 * Method setParticipantDetails.
	 */
	public void setParticipantDetails() {
		if (R4EUIModelController.isUserQueryAvailable()) {
			try {
				//Get detailed info from DB if available
				final IQueryUser query = new QueryUserFactory().getInstance();
				final List<IUserInfo> info = query.searchByUserId(fParticipant.getId());
				if (info.size() > 0) {
					final IUserInfo userInfo = info.get(0);
					fParticipantDetails = UIUtils.buildUserDetailsString(userInfo);
					if (null == fParticipant.getEmail()) {
						fParticipant.setEmail(userInfo.getEmail());
					}
				}
			} catch (NamingException e) {
				R4EUIPlugin.Ftracer.traceWarning("Exception: " + e.toString() + " (" + e.getMessage() + ")");
			} catch (IOException e) {
				R4EUIPlugin.Ftracer.traceWarning("Exception: " + e.toString() + " (" + e.getMessage() + ")");
			}
		}
	}

	/**
	 * Method isRemoveElementCmd.
	 * 
	 * @return boolean
	 * @see org.eclipse.mylyn.reviews.r4e.ui.internal.model.IR4EUIModelElement#isRemoveElementCmd()
	 */
	@Override
	public boolean isRemoveElementCmd() {
		if (isEnabled()
				&& !isReadOnly()
				&& !(((R4EReviewState) R4EUIModelController.getActiveReview().getReview().getState()).getState().equals(R4EReviewPhase.R4E_REVIEW_PHASE_COMPLETED))) {
			return true;
		}
		return false;
	}

	/**
	 * Method getRemoveElementCmdName.
	 * 
	 * @return String
	 * @see org.eclipse.mylyn.reviews.r4e.ui.internal.model.IR4EUIModelElement#getRemoveElementCmdName()
	 */
	@Override
	public String getRemoveElementCmdName() {
		return REMOVE_ELEMENT_COMMAND_NAME;
	}

	/**
	 * Method getRemoveElementCmdTooltip.
	 * 
	 * @return String
	 * @see org.eclipse.mylyn.reviews.r4e.ui.internal.model.IR4EUIModelElement#getRemoveElementCmdTooltip()
	 */
	@Override
	public String getRemoveElementCmdTooltip() {
		return REMOVE_ELEMENT_COMMAND_TOOLTIP;
	}

	/**
	 * Method isRestoreElementCmd.
	 * 
	 * @return boolean
	 * @see org.eclipse.mylyn.reviews.r4e.ui.internal.model.IR4EUIModelElement#iisRestoreElementCmd()
	 */
	@Override
	public boolean isRestoreElementCmd() {
		if (!(getParent().getParent().isEnabled())) {
			return false;
		}
		if (isEnabled()
				|| isReadOnly()
				|| ((R4EReviewState) R4EUIModelController.getActiveReview().getReview().getState()).getState().equals(
						R4EReviewPhase.R4E_REVIEW_PHASE_COMPLETED)) {
			return false;
		}
		return true;
	}

	/**
	 * Method getRestoreElementCmdName.
	 * 
	 * @return String
	 * @see org.eclipse.mylyn.reviews.r4e.ui.internal.model.IR4EUIModelElement#getRestoreElementCmdName()
	 */
	@Override
	public String getRestoreElementCmdName() {
		return RESTORE_ELEMENT_COMMAND_NAME;
	}

	/**
	 * Method getRestoreElementCmdTooltip.
	 * 
	 * @return String
	 * @see org.eclipse.mylyn.reviews.r4e.ui.internal.model.IR4EUIModelElement#getRestoreElementCmdTooltip()
	 */
	@Override
	public String getRestoreElementCmdTooltip() {
		return RESTORE_ELEMENT_COMMAND_TOOLTIP;
	}
}
