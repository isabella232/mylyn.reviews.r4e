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
 * This class extends the anomaly erlement to include additional parameters used
 * in informal and formal reviews
 * 
 * Contributors:
 *   Sebastien Dubois - Created for Mylyn Review R4E project
 *   
 *******************************************************************************/
package org.eclipse.mylyn.reviews.r4e.ui.internal.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylyn.reviews.frame.core.model.ReviewComponent;
import org.eclipse.mylyn.reviews.r4e.core.model.R4EAnomaly;
import org.eclipse.mylyn.reviews.r4e.core.model.R4EAnomalyState;
import org.eclipse.mylyn.reviews.r4e.core.model.R4EFormalReview;
import org.eclipse.mylyn.reviews.r4e.core.model.R4EReviewComponent;
import org.eclipse.mylyn.reviews.r4e.core.model.R4EReviewPhase;
import org.eclipse.mylyn.reviews.r4e.core.model.R4EReviewType;
import org.eclipse.mylyn.reviews.r4e.core.model.serial.impl.OutOfSyncException;
import org.eclipse.mylyn.reviews.r4e.core.model.serial.impl.ResourceHandlingException;
import org.eclipse.mylyn.reviews.r4e.ui.internal.properties.general.AnomalyExtraProperties;
import org.eclipse.mylyn.reviews.r4e.ui.internal.utils.R4EUIConstants;
import org.eclipse.ui.views.properties.IPropertySource;

/**
 * @author lmcdubo
 * @version $Revision: 1.0 $
 */
public class R4EUIAnomalyExtended extends R4EUIAnomalyBasic {

	// ------------------------------------------------------------------------
	// Constants
	// ------------------------------------------------------------------------

	/**
	 * Field ANOMALY_STATE_CREATED. (value is ""CREATED"")
	 */
	private static final String ANOMALY_STATE_CREATED = "CREATED";

	/**
	 * Field ANOMALY_STATE_ASSIGNED. (value is ""ASSIGNED"")
	 */
	private static final String ANOMALY_STATE_ASSIGNED = "ASSIGNED";

	/**
	 * Field ANOMALY_STATE_ACCEPTED. (value is ""ACCEPTED"")
	 */
	private static final String ANOMALY_STATE_ACCEPTED = "ACCEPTED";

	/**
	 * Field ANOMALY_STATE_DUPLICATED. (value is ""DUPLICATED"")
	 */
	private static final String ANOMALY_STATE_DUPLICATED = "DUPLICATED";

	/**
	 * Field ANOMALY_STATE_REJECTED. (value is ""REJECTED"")
	 */
	private static final String ANOMALY_STATE_REJECTED = "REJECTED";

	/**
	 * Field ANOMALY_STATE_DEFERRED. (value is ""DEFERRED"")
	 */
	private static final String ANOMALY_STATE_DEFERRED = "DEFERRED";

	/**
	 * Field ANOMALY_STATE_FIXED. (value is ""FIXED"")
	 */
	private static final String ANOMALY_STATE_FIXED = "FIXED";

	/**
	 * Field ANOMALY_STATE_VERIFIED. (value is ""VERIFIED"")
	 */
	private static final String ANOMALY_STATE_VERIFIED = "VERIFIED";

	/**
	 * Field FStateValues.
	 */
	private static final String[] STATE_VALUES = { ANOMALY_STATE_ACCEPTED, ANOMALY_STATE_DUPLICATED,
			ANOMALY_STATE_REJECTED, ANOMALY_STATE_DEFERRED, ANOMALY_STATE_ASSIGNED, ANOMALY_STATE_CREATED,
			ANOMALY_STATE_VERIFIED, ANOMALY_STATE_FIXED }; //NOTE: This has to match R4EAnomalyState in R4E core plugin

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	/**
	 * Constructor for R4EUIAnomalyExtended.
	 * 
	 * @param aParent
	 *            IR4EUIModelElement
	 * @param aAnomaly
	 *            R4EAnomaly
	 * @param aPosition
	 *            IR4EUIPosition
	 */
	public R4EUIAnomalyExtended(IR4EUIModelElement aParent, R4EAnomaly aAnomaly, IR4EUIPosition aPosition) {
		super(aParent, aAnomaly, aPosition);
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * Method buildAnomalyName.
	 * 
	 * @param aAnomaly
	 *            - the anomaly to use
	 * @return String - the new name
	 */
	public static String buildAnomalyName(R4EAnomaly aAnomaly, IR4EUIPosition aPosition) {
		StringBuilder sb = new StringBuilder(getStateString(aAnomaly.getState()) + ": ");
		if (null == aPosition) {
			sb.append(aAnomaly.getTitle());
		} else {
			sb.append(aPosition.toString() + "->" + aAnomaly.getTitle());
		}
		return sb.toString();
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
			return new AnomalyExtraProperties(this);
		}
		return null;
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
		super.setModelData(aModelComponent);
		final Long bookNum = R4EUIModelController.FResourceUpdater.checkOut(fAnomaly,
				R4EUIModelController.getReviewer());
		fAnomaly.setType(((R4EAnomaly) aModelComponent).getType());
		fAnomaly.setRank(((R4EAnomaly) aModelComponent).getRank());
		R4EUIModelController.FResourceUpdater.checkIn(bookNum);
	}

	/**
	 * Method updateState.
	 * 
	 * @param aNewState
	 *            R4EAnomalyState
	 * @throws OutOfSyncException
	 * @throws ResourceHandlingException
	 */
	public void updateState(R4EAnomalyState aNewState) throws ResourceHandlingException, OutOfSyncException {
		//Set data in model element
		final Long bookNum = R4EUIModelController.FResourceUpdater.checkOut(fAnomaly,
				R4EUIModelController.getReviewer());
		fAnomaly.setState(aNewState);
		updateDecider(aNewState);
		R4EUIModelController.FResourceUpdater.checkIn(bookNum);
		String nameLabel = null;
		if (null == getPosition()) {
			nameLabel = fAnomaly.getTitle();
		} else {
			nameLabel = getPosition().toString() + "->" + fAnomaly.getTitle();
		}
		setName(getStateString(aNewState) + ": " + nameLabel);
	}

	/**
	 * Method updateDecider.
	 * 
	 * @param aNewState
	 *            R4EAnomalyState
	 */
	private void updateDecider(R4EAnomalyState aNewState) {
		if (aNewState.equals(R4EAnomalyState.R4E_ANOMALY_STATE_ACCEPTED)
				|| aNewState.equals(R4EAnomalyState.R4E_ANOMALY_STATE_DEFERRED)
				|| aNewState.equals(R4EAnomalyState.R4E_ANOMALY_STATE_DUPLICATED)
				|| aNewState.equals(R4EAnomalyState.R4E_ANOMALY_STATE_REJECTED)) {
			fAnomaly.setDecidedByID(R4EUIModelController.getReviewer());
		} else if (aNewState.equals(R4EAnomalyState.R4E_ANOMALY_STATE_FIXED)) {
			fAnomaly.setFixedByID(R4EUIModelController.getReviewer());
		} else if (aNewState.equals(R4EAnomalyState.R4E_ANOMALY_STATE_VERIFIED)) {
			fAnomaly.setFollowUpByID(R4EUIModelController.getReviewer());
		}
	}

	/**
	 * Method getStateString.
	 * 
	 * @param aNewState
	 *            R4EAnomalyState
	 * @return String
	 */
	public static String getStateString(R4EAnomalyState aNewState) {
		if (aNewState.equals(R4EAnomalyState.R4E_ANOMALY_STATE_CREATED)) {
			return ANOMALY_STATE_CREATED;
		} else if (aNewState.equals(R4EAnomalyState.R4E_ANOMALY_STATE_ASSIGNED)) {
			return ANOMALY_STATE_ASSIGNED;
		} else if (aNewState.equals(R4EAnomalyState.R4E_ANOMALY_STATE_ACCEPTED)) {
			return ANOMALY_STATE_ACCEPTED;
		} else if (aNewState.equals(R4EAnomalyState.R4E_ANOMALY_STATE_DUPLICATED)) {
			return ANOMALY_STATE_DUPLICATED;
		} else if (aNewState.equals(R4EAnomalyState.R4E_ANOMALY_STATE_REJECTED)) {
			return ANOMALY_STATE_REJECTED;
		} else if (aNewState.equals(R4EAnomalyState.R4E_ANOMALY_STATE_DEFERRED)) {
			return ANOMALY_STATE_DEFERRED;
		} else if (aNewState.equals(R4EAnomalyState.R4E_ANOMALY_STATE_FIXED)) {
			return ANOMALY_STATE_FIXED;
		} else if (aNewState.equals(R4EAnomalyState.R4E_ANOMALY_STATE_VERIFIED)) {
			return ANOMALY_STATE_VERIFIED;
		} else {
			return "";
		}
	}

	/**
	 * Method getStateFromString.
	 * 
	 * @param aNewState
	 *            String
	 * @return R4EAnomalyState
	 */
	public static R4EAnomalyState getStateFromString(String aNewState) {
		if (aNewState.equals(ANOMALY_STATE_CREATED)) {
			return R4EAnomalyState.R4E_ANOMALY_STATE_CREATED;
		} else if (aNewState.equals(ANOMALY_STATE_ASSIGNED)) {
			return R4EAnomalyState.R4E_ANOMALY_STATE_ASSIGNED;
		} else if (aNewState.equals(ANOMALY_STATE_ACCEPTED)) {
			return R4EAnomalyState.R4E_ANOMALY_STATE_ACCEPTED;
		} else if (aNewState.equals(ANOMALY_STATE_DUPLICATED)) {
			return R4EAnomalyState.R4E_ANOMALY_STATE_DUPLICATED;
		} else if (aNewState.equals(ANOMALY_STATE_REJECTED)) {
			return R4EAnomalyState.R4E_ANOMALY_STATE_REJECTED;
		} else if (aNewState.equals(ANOMALY_STATE_DEFERRED)) {
			return R4EAnomalyState.R4E_ANOMALY_STATE_DEFERRED;
		} else if (aNewState.equals(ANOMALY_STATE_FIXED)) {
			return R4EAnomalyState.R4E_ANOMALY_STATE_FIXED;
		} else if (aNewState.equals(ANOMALY_STATE_VERIFIED)) {
			return R4EAnomalyState.R4E_ANOMALY_STATE_VERIFIED;
		} else {
			return null; //should never happen
		}
	}

	/**
	 * Method getStates.
	 * 
	 * @return String[]
	 */
	public static String[] getStates() {
		return STATE_VALUES;
	}

	/**
	 * Method getAvailableStates.
	 * 
	 * @return String[]
	 */
	public String[] getAvailableStates() {
		//Peek state machine to get available states
		final R4EAnomalyState[] states = getAllowedStates(getAnomaly().getState());
		final List<String> stateStrings = new ArrayList<String>();
		for (R4EAnomalyState state : states) {
			stateStrings.add(getStateString(state));
		}
		return stateStrings.toArray(new String[stateStrings.size()]);
	}

	/**
	 * Method mapStateToIndex.
	 * 
	 * @param aState
	 *            R4EAnomalyState
	 * @return int
	 */
	public int mapStateToIndex(R4EAnomalyState aState) {
		//Peek state machine to get available states
		final R4EAnomalyState[] states = getAllowedStates(getAnomaly().getState());
		for (int i = 0; i < states.length; i++) {
			if (states[i].getValue() == aState.getValue()) {
				return i;
			}
		}
		return R4EUIConstants.INVALID_VALUE; //should never happen
	}

	//Anomaly State Machine

	/**
	 * Method isClassEnabled.
	 * 
	 * @return boolean
	 */
	public boolean isClassEnabled() {
		if (R4EUIModelController.getActiveReview().getReview().getType().equals(R4EReviewType.R4E_REVIEW_TYPE_INFORMAL)) {
			if (fAnomaly.getState().equals(R4EAnomalyState.R4E_ANOMALY_STATE_ASSIGNED)) {
				return true;
			}
		} else if (R4EUIModelController.getActiveReview()
				.getReview()
				.getType()
				.equals(R4EReviewType.R4E_REVIEW_TYPE_FORMAL)) {
			if (fAnomaly.getState().equals(R4EAnomalyState.R4E_ANOMALY_STATE_CREATED)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Method isRankEnabled.
	 * 
	 * @return boolean
	 */
	public boolean isRankEnabled() {
		if (R4EUIModelController.getActiveReview().getReview().getType().equals(R4EReviewType.R4E_REVIEW_TYPE_INFORMAL)) {
			if (fAnomaly.getState().equals(R4EAnomalyState.R4E_ANOMALY_STATE_ASSIGNED)) {
				return true;
			}
		} else if (R4EUIModelController.getActiveReview()
				.getReview()
				.getType()
				.equals(R4EReviewType.R4E_REVIEW_TYPE_FORMAL)) {
			if (fAnomaly.getState().equals(R4EAnomalyState.R4E_ANOMALY_STATE_CREATED)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Method isDueDateEnabled.
	 * 
	 * @return boolean
	 */
	public boolean isDueDateEnabled() {
		if (R4EUIModelController.getActiveReview().getReview().getType().equals(R4EReviewType.R4E_REVIEW_TYPE_INFORMAL)) {
			if (fAnomaly.getState().equals(R4EAnomalyState.R4E_ANOMALY_STATE_ASSIGNED)) {
				return true;
			}
		} else if (R4EUIModelController.getActiveReview()
				.getReview()
				.getType()
				.equals(R4EReviewType.R4E_REVIEW_TYPE_FORMAL)) {
			if (fAnomaly.getState().equals(R4EAnomalyState.R4E_ANOMALY_STATE_CREATED)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Method isDecidedByEnabled.
	 * 
	 * @return boolean
	 */
	public boolean isDecidedByEnabled() {
		if (R4EUIModelController.getActiveReview().getReview().getType().equals(R4EReviewType.R4E_REVIEW_TYPE_INFORMAL)) {
			if (fAnomaly.getState().equals(R4EAnomalyState.R4E_ANOMALY_STATE_DEFERRED)
					|| fAnomaly.getState().equals(R4EAnomalyState.R4E_ANOMALY_STATE_DUPLICATED)
					|| fAnomaly.getState().equals(R4EAnomalyState.R4E_ANOMALY_STATE_REJECTED)
					|| fAnomaly.getState().equals(R4EAnomalyState.R4E_ANOMALY_STATE_FIXED)
					|| fAnomaly.getState().equals(R4EAnomalyState.R4E_ANOMALY_STATE_VERIFIED)) {
				return true;
			}
		} else { //R4EReviewType.R4E_REVIEW_TYPE_FORMAL
			final R4EReviewPhase phase = ((R4EFormalReview) R4EUIModelController.getActiveReview().getReview()).getCurrent()
					.getType();
			if (phase.equals(R4EReviewPhase.R4E_REVIEW_PHASE_DECISION)) {
				if (fAnomaly.getState().equals(R4EAnomalyState.R4E_ANOMALY_STATE_DEFERRED)
						|| fAnomaly.getState().equals(R4EAnomalyState.R4E_ANOMALY_STATE_DUPLICATED)
						|| fAnomaly.getState().equals(R4EAnomalyState.R4E_ANOMALY_STATE_REJECTED)
						|| fAnomaly.getState().equals(R4EAnomalyState.R4E_ANOMALY_STATE_ACCEPTED)) {
					return true;
				} else if (phase.equals(R4EReviewPhase.R4E_REVIEW_PHASE_REWORK)) {
					if (fAnomaly.getState().equals(R4EAnomalyState.R4E_ANOMALY_STATE_FIXED)
							|| fAnomaly.getState().equals(R4EAnomalyState.R4E_ANOMALY_STATE_VERIFIED)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Method isFixedByEnabled.
	 * 
	 * @return boolean
	 */
	public boolean isFixedByEnabled() {
		if (fAnomaly.getState().equals(R4EAnomalyState.R4E_ANOMALY_STATE_FIXED)) {
			return true;
		}
		return false;
	}

	/**
	 * Method isFollowUpByEnabled.
	 * 
	 * @return boolean
	 */
	public boolean isFollowUpByEnabled() {
		if (fAnomaly.getState().equals(R4EAnomalyState.R4E_ANOMALY_STATE_VERIFIED)) {
			return true;
		}
		return false;
	}

	/**
	 * Method isNotAcceptedReasonEnabled.
	 * 
	 * @return boolean
	 */
	public boolean isNotAcceptedReasonEnabled() {
		if (fAnomaly.getState().equals(R4EAnomalyState.R4E_ANOMALY_STATE_REJECTED)) {
			return true;
		}
		return false;
	}

	/**
	 * Method getAllowedState.
	 * 
	 * @param aCurrentState
	 *            R4EAnomalyState
	 * @return R4EAnomalyState[]
	 */
	private R4EAnomalyState[] getAllowedStates(R4EAnomalyState aCurrentState) {
		final List<R4EAnomalyState> states = new ArrayList<R4EAnomalyState>();

		if (R4EUIModelController.getActiveReview().getReview().getType().equals(R4EReviewType.R4E_REVIEW_TYPE_INFORMAL)) {
			switch (aCurrentState.getValue()) {
			case R4EAnomalyState.R4E_ANOMALY_STATE_ASSIGNED_VALUE:
				states.add(R4EAnomalyState.R4E_ANOMALY_STATE_ASSIGNED);
				states.add(R4EAnomalyState.R4E_ANOMALY_STATE_DEFERRED);
				states.add(R4EAnomalyState.R4E_ANOMALY_STATE_DUPLICATED);
				states.add(R4EAnomalyState.R4E_ANOMALY_STATE_REJECTED);
				states.add(R4EAnomalyState.R4E_ANOMALY_STATE_FIXED);
				break;

			case R4EAnomalyState.R4E_ANOMALY_STATE_DEFERRED_VALUE:
				states.add(R4EAnomalyState.R4E_ANOMALY_STATE_ASSIGNED);
				states.add(R4EAnomalyState.R4E_ANOMALY_STATE_DEFERRED);
				states.add(R4EAnomalyState.R4E_ANOMALY_STATE_DUPLICATED);
				states.add(R4EAnomalyState.R4E_ANOMALY_STATE_REJECTED);
				break;

			case R4EAnomalyState.R4E_ANOMALY_STATE_DUPLICATED_VALUE:
				states.add(R4EAnomalyState.R4E_ANOMALY_STATE_ASSIGNED);
				states.add(R4EAnomalyState.R4E_ANOMALY_STATE_DEFERRED);
				states.add(R4EAnomalyState.R4E_ANOMALY_STATE_DUPLICATED);
				break;

			case R4EAnomalyState.R4E_ANOMALY_STATE_REJECTED_VALUE:
				states.add(R4EAnomalyState.R4E_ANOMALY_STATE_ASSIGNED);
				states.add(R4EAnomalyState.R4E_ANOMALY_STATE_DEFERRED);
				states.add(R4EAnomalyState.R4E_ANOMALY_STATE_REJECTED);
				break;

			case R4EAnomalyState.R4E_ANOMALY_STATE_FIXED_VALUE:
				states.add(R4EAnomalyState.R4E_ANOMALY_STATE_ASSIGNED);
				states.add(R4EAnomalyState.R4E_ANOMALY_STATE_FIXED);
				states.add(R4EAnomalyState.R4E_ANOMALY_STATE_VERIFIED);
				break;

			case R4EAnomalyState.R4E_ANOMALY_STATE_VERIFIED_VALUE:
				states.add(R4EAnomalyState.R4E_ANOMALY_STATE_VERIFIED);
				break;

			default:
				//should never happen
			}
		} else { //R4EReviewType.R4E_REVIEW_TYPE_FORMAL
			final R4EReviewPhase phase = ((R4EFormalReview) R4EUIModelController.getActiveReview().getReview()).getCurrent()
					.getType();
			switch (aCurrentState.getValue()) {
			case R4EAnomalyState.R4E_ANOMALY_STATE_CREATED_VALUE:
				if (phase.equals(R4EReviewPhase.R4E_REVIEW_PHASE_DECISION)) {
					states.add(R4EAnomalyState.R4E_ANOMALY_STATE_CREATED);
					states.add(R4EAnomalyState.R4E_ANOMALY_STATE_ACCEPTED);
					states.add(R4EAnomalyState.R4E_ANOMALY_STATE_DEFERRED);
					states.add(R4EAnomalyState.R4E_ANOMALY_STATE_DUPLICATED);
					states.add(R4EAnomalyState.R4E_ANOMALY_STATE_REJECTED);
				} else {
					states.add(R4EAnomalyState.R4E_ANOMALY_STATE_CREATED);
				}
				break;

			case R4EAnomalyState.R4E_ANOMALY_STATE_DEFERRED_VALUE:
				if (phase.equals(R4EReviewPhase.R4E_REVIEW_PHASE_DECISION)) {
					states.add(R4EAnomalyState.R4E_ANOMALY_STATE_ACCEPTED);
					states.add(R4EAnomalyState.R4E_ANOMALY_STATE_DUPLICATED);
					states.add(R4EAnomalyState.R4E_ANOMALY_STATE_REJECTED);
					states.add(R4EAnomalyState.R4E_ANOMALY_STATE_DEFERRED);
				} else {
					states.add(R4EAnomalyState.R4E_ANOMALY_STATE_DEFERRED);
				}
				break;

			case R4EAnomalyState.R4E_ANOMALY_STATE_DUPLICATED_VALUE:
				if (phase.equals(R4EReviewPhase.R4E_REVIEW_PHASE_DECISION)) {
					states.add(R4EAnomalyState.R4E_ANOMALY_STATE_ACCEPTED);
					states.add(R4EAnomalyState.R4E_ANOMALY_STATE_DEFERRED);
					states.add(R4EAnomalyState.R4E_ANOMALY_STATE_DUPLICATED);
				} else {
					states.add(R4EAnomalyState.R4E_ANOMALY_STATE_DUPLICATED);
				}
				break;

			case R4EAnomalyState.R4E_ANOMALY_STATE_REJECTED_VALUE:
				if (phase.equals(R4EReviewPhase.R4E_REVIEW_PHASE_DECISION)) {
					states.add(R4EAnomalyState.R4E_ANOMALY_STATE_ACCEPTED);
					states.add(R4EAnomalyState.R4E_ANOMALY_STATE_DEFERRED);
					states.add(R4EAnomalyState.R4E_ANOMALY_STATE_REJECTED);
				} else {
					states.add(R4EAnomalyState.R4E_ANOMALY_STATE_REJECTED);
				}
				break;

			case R4EAnomalyState.R4E_ANOMALY_STATE_ACCEPTED_VALUE:
				if (phase.equals(R4EReviewPhase.R4E_REVIEW_PHASE_DECISION)) {
					states.add(R4EAnomalyState.R4E_ANOMALY_STATE_ACCEPTED);
					states.add(R4EAnomalyState.R4E_ANOMALY_STATE_DUPLICATED);
					states.add(R4EAnomalyState.R4E_ANOMALY_STATE_DEFERRED);
					states.add(R4EAnomalyState.R4E_ANOMALY_STATE_REJECTED);
				} else if (phase.equals(R4EReviewPhase.R4E_REVIEW_PHASE_REWORK)) {
					states.add(R4EAnomalyState.R4E_ANOMALY_STATE_ACCEPTED);
					states.add(R4EAnomalyState.R4E_ANOMALY_STATE_FIXED);
				} else {
					states.add(R4EAnomalyState.R4E_ANOMALY_STATE_ACCEPTED);
				}
				break;

			case R4EAnomalyState.R4E_ANOMALY_STATE_FIXED_VALUE:
				if (phase.equals(R4EReviewPhase.R4E_REVIEW_PHASE_REWORK)) {
					states.add(R4EAnomalyState.R4E_ANOMALY_STATE_FIXED);
					states.add(R4EAnomalyState.R4E_ANOMALY_STATE_VERIFIED);
				} else {
					states.add(R4EAnomalyState.R4E_ANOMALY_STATE_FIXED);
				}
				break;

			case R4EAnomalyState.R4E_ANOMALY_STATE_VERIFIED_VALUE:
				states.add(R4EAnomalyState.R4E_ANOMALY_STATE_VERIFIED);
				break;

			default:
				//should never happen
			}
		}
		return states.toArray(new R4EAnomalyState[states.size()]);
	}
}