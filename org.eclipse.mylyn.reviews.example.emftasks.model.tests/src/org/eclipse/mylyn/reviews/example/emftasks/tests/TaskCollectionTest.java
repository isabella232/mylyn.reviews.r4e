/**
 * Copyright (c) 2013 Ericsson
 * 
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Description:
 * 
 * Contributors:
 *    Miles Parker, Tasktop Technologies - Initial API and implementation
 * 
 */
package org.eclipse.mylyn.reviews.example.emftasks.tests;

import junit.framework.TestCase;

import junit.textui.TestRunner;

import org.eclipse.mylyn.reviews.example.emftasks.EmfTasksFactory;
import org.eclipse.mylyn.reviews.example.emftasks.TaskCollection;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Task Collection</b></em>'.
 * <!-- end-user-doc -->
 * @generated
 */
public class TaskCollectionTest extends TestCase {

	/**
	 * The fixture for this Task Collection test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected TaskCollection fixture = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static void main(String[] args) {
		TestRunner.run(TaskCollectionTest.class);
	}

	/**
	 * Constructs a new Task Collection test case with the given name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TaskCollectionTest(String name) {
		super(name);
	}

	/**
	 * Sets the fixture for this Task Collection test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void setFixture(TaskCollection fixture) {
		this.fixture = fixture;
	}

	/**
	 * Returns the fixture for this Task Collection test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected TaskCollection getFixture() {
		return fixture;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see junit.framework.TestCase#setUp()
	 * @generated
	 */
	@Override
	protected void setUp() throws Exception {
		setFixture(EmfTasksFactory.eINSTANCE.createTaskCollection());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see junit.framework.TestCase#tearDown()
	 * @generated
	 */
	@Override
	protected void tearDown() throws Exception {
		setFixture(null);
	}

} //TaskCollectionTest
