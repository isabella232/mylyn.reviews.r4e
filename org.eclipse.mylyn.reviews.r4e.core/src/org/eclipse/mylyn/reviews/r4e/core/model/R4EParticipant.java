/**
 * Copyright (c) 2010 Ericsson
 *  
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Description:
 * 
 * Contributors:
 * Alvaro Sanchez-Leon  - Initial API and implementation
 * 
 */
package org.eclipse.mylyn.reviews.r4e.core.model;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>R4E Participant</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.mylyn.reviews.r4e.core.model.R4EParticipant#getRoles <em>Roles</em>}</li>
 *   <li>{@link org.eclipse.mylyn.reviews.r4e.core.model.R4EParticipant#getSpentTime <em>Spent Time</em>}</li>
 *   <li>{@link org.eclipse.mylyn.reviews.r4e.core.model.R4EParticipant#getFocusArea <em>Focus Area</em>}</li>
 *   <li>{@link org.eclipse.mylyn.reviews.r4e.core.model.R4EParticipant#isIsPartOfDecision <em>Is Part Of Decision</em>}</li>
 *   <li>{@link org.eclipse.mylyn.reviews.r4e.core.model.R4EParticipant#getReviewedContent <em>Reviewed Content</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.mylyn.reviews.r4e.core.model.RModelPackage#getR4EParticipant()
 * @model
 * @generated
 */
public interface R4EParticipant extends R4EUser {
	/**
	 * Returns the value of the '<em><b>Roles</b></em>' attribute list.
	 * The list contents are of type {@link org.eclipse.mylyn.reviews.r4e.core.model.R4EUserRole}.
	 * The literals are from the enumeration {@link org.eclipse.mylyn.reviews.r4e.core.model.R4EUserRole}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Roles</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Roles</em>' attribute list.
	 * @see org.eclipse.mylyn.reviews.r4e.core.model.R4EUserRole
	 * @see org.eclipse.mylyn.reviews.r4e.core.model.RModelPackage#getR4EParticipant_Roles()
	 * @model
	 * @generated
	 */
	EList<R4EUserRole> getRoles();

	/**
	 * Returns the value of the '<em><b>Spent Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Spent Time</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Spent Time</em>' attribute.
	 * @see #setSpentTime(int)
	 * @see org.eclipse.mylyn.reviews.r4e.core.model.RModelPackage#getR4EParticipant_SpentTime()
	 * @model
	 * @generated
	 */
	int getSpentTime();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.r4e.core.model.R4EParticipant#getSpentTime <em>Spent Time</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Spent Time</em>' attribute.
	 * @see #getSpentTime()
	 * @generated
	 */
	void setSpentTime(int value);

	/**
	 * Returns the value of the '<em><b>Focus Area</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Focus Area</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Focus Area</em>' attribute.
	 * @see #setFocusArea(String)
	 * @see org.eclipse.mylyn.reviews.r4e.core.model.RModelPackage#getR4EParticipant_FocusArea()
	 * @model
	 * @generated
	 */
	String getFocusArea();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.r4e.core.model.R4EParticipant#getFocusArea <em>Focus Area</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Focus Area</em>' attribute.
	 * @see #getFocusArea()
	 * @generated
	 */
	void setFocusArea(String value);

	/**
	 * Returns the value of the '<em><b>Is Part Of Decision</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Is Part Of Decision</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Is Part Of Decision</em>' attribute.
	 * @see #setIsPartOfDecision(boolean)
	 * @see org.eclipse.mylyn.reviews.r4e.core.model.RModelPackage#getR4EParticipant_IsPartOfDecision()
	 * @model
	 * @generated
	 */
	boolean isIsPartOfDecision();

	/**
	 * Sets the value of the '{@link org.eclipse.mylyn.reviews.r4e.core.model.R4EParticipant#isIsPartOfDecision <em>Is Part Of Decision</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Is Part Of Decision</em>' attribute.
	 * @see #isIsPartOfDecision()
	 * @generated
	 */
	void setIsPartOfDecision(boolean value);

	/**
	 * Returns the value of the '<em><b>Reviewed Content</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.mylyn.reviews.r4e.core.model.R4EID}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Reviewed Content</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Reviewed Content</em>' reference list.
	 * @see org.eclipse.mylyn.reviews.r4e.core.model.RModelPackage#getR4EParticipant_ReviewedContent()
	 * @model
	 * @generated
	 */
	EList<R4EID> getReviewedContent();

} // R4EParticipant