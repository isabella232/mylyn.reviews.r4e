/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.mylyn.reviews.r4e.core.model.validation;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;

import org.eclipse.mylyn.reviews.r4e.core.model.R4EContextType;
import org.eclipse.mylyn.reviews.r4e.core.model.R4EDelta;
import org.eclipse.mylyn.reviews.r4e.core.model.R4EFileVersion;

/**
 * A sample validator interface for {@link org.eclipse.mylyn.reviews.r4e.core.model.R4EFileContext}.
 * This doesn't really do anything, and it's not a real EMF artifact.
 * It was generated by the org.eclipse.emf.examples.generator.validator plug-in to illustrate how EMF's code generator can be extended.
 * This can be disabled with -vmargs -Dorg.eclipse.emf.examples.generator.validator=false.
 */
public interface R4EFileContextValidator {
	boolean validate();

	boolean validateDeltas(EList<R4EDelta> value);
	boolean validateBase(R4EFileVersion value);
	boolean validateTarget(R4EFileVersion value);
	boolean validateType(R4EContextType value);
	boolean validateInfoAtt(EMap<String, String> value);
}
