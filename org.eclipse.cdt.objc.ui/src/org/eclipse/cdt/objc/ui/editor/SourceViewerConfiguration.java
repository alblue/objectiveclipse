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

import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;

public class SourceViewerConfiguration extends TextSourceViewerConfiguration {

    private RuleBasedScanner blockCommentScanner = null;
    private RuleBasedScanner defaultScanner = null;
    private RuleBasedScanner docCommentScanner = null;

    @Override
    public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
        return new String[] { IPartition.DEFAULT, IPartition.DOC_COMMENT, IPartition.MULTI_LINE_COMMENT };
    }

    @Override
    public String getConfiguredDocumentPartitioning(ISourceViewer sourceViewer) {
        return DocumentSetupParticipant.DEFAULT_PARTITIONING;
    }

    protected RuleBasedScanner getDefaultScanner() {
        if (defaultScanner == null) {
            defaultScanner = new CodeScanner();
        }
        return defaultScanner;
    }

    private RuleBasedScanner getDocCommentScanner() {
        if (docCommentScanner == null) {
            docCommentScanner = new DocCommentScanner();
        }
        return docCommentScanner;
    }

    protected RuleBasedScanner getMultipleLineCommentScanner() {
        if (blockCommentScanner == null) {
            blockCommentScanner = new CommentScanner();
        }
        return blockCommentScanner;
    }

    @Override
    public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
        // Initialization
        PresentationReconciler reconciler = new PresentationReconciler();
        reconciler.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));

        // DEFAULT
        DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getDefaultScanner());
        reconciler.setDamager(dr, IPartition.DEFAULT);
        reconciler.setRepairer(dr, IPartition.DEFAULT);

        // DOC_COMMENT
        dr = new DefaultDamagerRepairer(getDocCommentScanner());
        reconciler.setDamager(dr, IPartition.DOC_COMMENT);
        reconciler.setRepairer(dr, IPartition.DOC_COMMENT);

        // MULTI_LINE_COMMENT
        dr = new DefaultDamagerRepairer(getMultipleLineCommentScanner());
        reconciler.setDamager(dr, IPartition.MULTI_LINE_COMMENT);
        reconciler.setRepairer(dr, IPartition.MULTI_LINE_COMMENT);

        return reconciler;
    }

}
