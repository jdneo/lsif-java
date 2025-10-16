/*******************************************************************************
* Copyright (c) 2021 Microsoft Corporation and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*     Microsoft Corporation - initial API and implementation
*******************************************************************************/

package com.microsoft.java.lsif.core.internal.visitors;

import com.microsoft.java.lsif.core.internal.emitter.LsifEmitter;
import com.microsoft.java.lsif.core.internal.indexer.IndexerContext;
import com.microsoft.java.lsif.core.internal.indexer.LsifService;
import com.microsoft.java.lsif.core.internal.protocol.Document;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.text.IDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class DiagnosticVisitorTest {

    private LsifService lsifService;
    private IndexerContext context;
    private DiagnosticVisitor visitor;

    @BeforeEach
    void setUp() {
        lsifService = Mockito.mock(LsifService.class);
        context = Mockito.mock(IndexerContext.class);
        visitor = new DiagnosticVisitor(lsifService, context);
    }

    @Test
    void testEnlistWithNoResource() {
        // Setup mocks to simulate resource not found
        // Call visitor.enlist() and verify expected behavior
    }

    @Test
    void testToDiagnosticsArrayWithEmptyMarkers() {
        // Test private method via reflection or by making it package-private for testing
    }

    // Add more tests for other scenarios as needed
}
