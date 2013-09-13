/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.r4e_gerrit.ui.test;

import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;
import org.eclipse.mylyn.internal.gerrit.core.GerritCorePlugin;

public class GerritCorePluginStub extends GerritCorePlugin {

	private static GerritCorePlugin plugin;

	public static GerritCorePlugin getDefault() {
		return plugin;
	}

	@Override
	public GerritConnector getConnector() {
		return new GerritConnector();
	}

}
