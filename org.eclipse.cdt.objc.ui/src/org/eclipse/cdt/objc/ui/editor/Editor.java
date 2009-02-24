/*******************************************************************************
 * Copyright (c) 2009, Leonardo Pessoa and others. All rights reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 
 * Leonardo Pessoa - Initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.objc.ui.editor;

import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.editors.text.TextEditor;

public class Editor extends TextEditor {

    private ISourceViewer sourceViewer;

    public Editor() {
        // Nothing to do here.
    }

    @Override
    protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles) {
        sourceViewer = new ProjectionViewer(parent, ruler, null, isOverviewRulerVisible(), styles);
        return sourceViewer;
    }

    public ISourceViewer getViewer() {
        return sourceViewer;
    }

    @Override
    protected void initializeEditor() {
        setSourceViewerConfiguration(new SourceViewerConfiguration());
        // setDocumentProvider((IDocumentProvider) null);
    }
}
