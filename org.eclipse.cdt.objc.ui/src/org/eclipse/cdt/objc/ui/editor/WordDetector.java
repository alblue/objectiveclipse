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

import org.eclipse.jface.text.rules.IWordDetector;

public class WordDetector implements IWordDetector {

    public boolean isWordPart(char c) {
        return Character.isJavaIdentifierPart(c);
    }

    public boolean isWordStart(char c) {
        return Character.isJavaIdentifierStart(c) || c == '@';
    }
}
