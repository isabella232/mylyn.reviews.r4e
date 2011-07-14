/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.mylyn.reviews.r4e.core.model.validation;

import org.eclipse.mylyn.reviews.r4e.core.model.R4EFileVersion;

/**
 * A sample validator interface for {@link org.eclipse.mylyn.reviews.r4e.core.model.R4EAnomalyTextPosition}.
 * This doesn't really do anything, and it's not a real EMF artifact.
 * It was generated by the org.eclipse.emf.examples.generator.validator plug-in to illustrate how EMF's code generator can be extended.
 * This can be disabled with -vmargs -Dorg.eclipse.emf.examples.generator.validator=false.
 */
public interface R4EAnomalyTextPositionValidator {
	boolean validate();

	boolean validateFile(R4EFileVersion value);
}
