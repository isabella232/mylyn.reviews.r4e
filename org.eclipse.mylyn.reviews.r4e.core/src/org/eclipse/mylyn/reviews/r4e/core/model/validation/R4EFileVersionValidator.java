/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.mylyn.reviews.r4e.core.model.validation;

import org.eclipse.core.resources.IResource;

import org.eclipse.emf.common.util.EMap;

import org.eclipse.team.core.history.IFileRevision;

/**
 * A sample validator interface for {@link org.eclipse.mylyn.reviews.r4e.core.model.R4EFileVersion}.
 * This doesn't really do anything, and it's not a real EMF artifact.
 * It was generated by the org.eclipse.emf.examples.generator.validator plug-in to illustrate how EMF's code generator can be extended.
 * This can be disabled with -vmargs -Dorg.eclipse.emf.examples.generator.validator=false.
 */
public interface R4EFileVersionValidator {
	boolean validate();

	boolean validatePlatformURI(String value);
	boolean validateVersionID(String value);
	boolean validateRepositoryPath(String value);
	boolean validateName(String value);
	boolean validateResource(IResource value);
	boolean validateLocalVersionID(String value);
	boolean validateFileRevision(IFileRevision value);
	boolean validateInfoAtt(EMap<String, String> value);
}
