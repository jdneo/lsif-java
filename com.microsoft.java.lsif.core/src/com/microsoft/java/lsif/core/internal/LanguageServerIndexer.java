/* --------------------------------------------------------------------------------------------
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 * ------------------------------------------------------------------------------------------ */

package com.microsoft.java.lsif.core.internal;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

import com.microsoft.java.lsif.core.internal.indexer.Indexer;

public class LanguageServerIndexer implements IApplication {

	@Override
	public Object start(IApplicationContext context) {
		try {
			Indexer indexer = new Indexer();
			indexer.generateLsif();

		} catch (Exception ex) {
			LanguageServerIndexerPlugin.logException("Exception when indexing ", ex);
			System.exit(1);
		}

		return null;
	}

	@Override
	public void stop() {
	}
}
