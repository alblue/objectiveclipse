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

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;

public class CommentScanner extends RuleBasedScanner {

    public CommentScanner() {
        IToken commentToken = new Token(new TextAttribute(StyleManager.COMMENT.getColor()));
        setDefaultReturnToken(commentToken);
        createRules();
    }

    private void createRules() {
        setRules(new IRule[] { new MultiLineRule("/*", "*/", StyleManager.COMMENT.getToken(), (char) 0, true) //$NON-NLS-1$ //$NON-NLS-2$
        });
    }

}
