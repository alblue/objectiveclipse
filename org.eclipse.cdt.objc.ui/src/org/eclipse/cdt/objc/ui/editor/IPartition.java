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

import org.eclipse.jface.text.IDocument;

public interface IPartition {

    String CHARACTER = "__objc_character"; //$NON-NLS-1$

    String DEFAULT = IDocument.DEFAULT_CONTENT_TYPE;

    String DOC_COMMENT = "__objc_doc_coment"; //$NON-NLS-1$

    String MULTI_LINE_COMMENT = "__objc_multi_line_comment"; //$NON-NLS-1$

    String SINGLE_LINE_COMMENT = "__objc_single_line_comment"; //$NON-NLS-1$

    String SKIP = "__skip"; //$NON-NLS-1$

    String STRING = "__objc_string"; //$NON-NLS-1$
}
