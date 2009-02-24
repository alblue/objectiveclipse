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

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;

public class DocumentSetupParticipant implements IDocumentSetupParticipant {

    public static final String DEFAULT_PARTITIONING = "__objc_partitioning"; //$NON-NLS-1$

    private String partitioning = DEFAULT_PARTITIONING;

    public IDocumentPartitioner createDocumentPartitioner() {
        String[] types = new String[] { IPartition.MULTI_LINE_COMMENT, IPartition.DOC_COMMENT };
        return new FastPartitioner(new PartitionScanner(), types);
    }

    public String getDocumentPartitioning() {
        return partitioning;
    }

    public void setDocumentPartitioning(String documentPartitioning) {
        partitioning = documentPartitioning;
    }

    public void setup(IDocument document) {
        IDocumentPartitioner partitioner = createDocumentPartitioner();
        if (document instanceof IDocumentExtension3) {
            IDocumentExtension3 extension3 = (IDocumentExtension3) document;
            extension3.setDocumentPartitioner(partitioning, partitioner);
        } else {
            document.setDocumentPartitioner(partitioner);
        }
        partitioner.connect(document);
    }

}
