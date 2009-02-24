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
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.SWT;

public class StringScanner extends BufferedRuleBasedScanner {

    private IToken defaultReturnToken;
    private int offset, length, end;

    public StringScanner() {
        super(20);
        createRules();
    }

    protected void createRules() {
        defaultReturnToken = new Token(new TextAttribute(StyleManager.STRING.getColor(), StyleManager.WHITE
                .getColor(), SWT.NORMAL));
        setDefaultReturnToken(defaultReturnToken);
    }

    @Override
    public int getTokenLength() {
        return length;
    }

    @Override
    public IToken nextToken() {
        fTokenOffset = offset;
        if (offset < end) {
            length = end - offset;
            offset = end;
            return defaultReturnToken;
        }
        return Token.EOF;
    }

    @Override
    public void setRange(IDocument document, int offset, int length) {
        super.setRange(document, offset, length);
        this.offset = offset;
        this.length = length;
        end = offset + length;
    }
}
