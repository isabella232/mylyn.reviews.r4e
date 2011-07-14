/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.mylyn.reviews.r4e.core.model.validation;

import org.eclipse.emf.common.util.EList;

/**
 * A sample validator interface for {@link org.eclipse.mylyn.reviews.r4e.core.model.R4EMeetingData}.
 * This doesn't really do anything, and it's not a real EMF artifact.
 * It was generated by the org.eclipse.emf.examples.generator.validator plug-in to illustrate how EMF's code generator can be extended.
 * This can be disabled with -vmargs -Dorg.eclipse.emf.examples.generator.validator=false.
 */
public interface R4EMeetingDataValidator {
	boolean validate();

	boolean validateId(String value);
	boolean validateSubject(String value);
	boolean validateLocation(String value);
	boolean validateStartTime(long value);
	boolean validateDuration(int value);
	boolean validateSentCount(int value);
	boolean validateSender(String value);
	boolean validateReceivers(EList<String> value);
	boolean validateBody(String value);
}
