/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.mylyn.reviews.r4e.core.model.validation;

import org.eclipse.emf.common.util.EList;

import org.eclipse.mylyn.reviews.r4e.core.model.R4EParticipant;
import org.eclipse.mylyn.reviews.r4e.core.model.R4EReviewPhaseInfo;

/**
 * A sample validator interface for {@link org.eclipse.mylyn.reviews.r4e.core.model.R4EFormalReview}.
 * This doesn't really do anything, and it's not a real EMF artifact.
 * It was generated by the org.eclipse.emf.examples.generator.validator plug-in to illustrate how EMF's code generator can be extended.
 * This can be disabled with -vmargs -Dorg.eclipse.emf.examples.generator.validator=false.
 */
public interface R4EFormalReviewValidator {
	boolean validate();

	boolean validatePhaseOwner(R4EParticipant value);
	boolean validatePhases(EList<R4EReviewPhaseInfo> value);
	boolean validateCurrent(R4EReviewPhaseInfo value);
}
