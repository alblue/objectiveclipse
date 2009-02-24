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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.WordRule;

public class DocCommentScanner extends RuleBasedScanner {

    static class DocWordDetector implements IWordDetector {

        public boolean isWordPart(char c) {
            return Character.isLetter(c);
        }

        public boolean isWordStart(char c) {
            return (c == '@');
        }
    }

    private static String[] docKeywords = { "@author", //$NON-NLS-1$
            "@deprecated", //$NON-NLS-1$
            "@exception", //$NON-NLS-1$
            "@param", //$NON-NLS-1$
            "@return", //$NON-NLS-1$
            "@see", //$NON-NLS-1$
            "@serial", //$NON-NLS-1$
            "@serialData", //$NON-NLS-1$
            "@serialField", //$NON-NLS-1$
            "@since", //$NON-NLS-1$
            "@throws", //$NON-NLS-1$
            "@version" //$NON-NLS-1$
    };

    public DocCommentScanner() {
        List<IRule> list = new ArrayList<IRule>();

        // Add rule for tags.
        list.add(new SingleLineRule("<", ">", StyleManager.DOC_COMMENT.getToken())); //$NON-NLS-2$ //$NON-NLS-1$

        // Add rule for links.
        list.add(new SingleLineRule("{", "}", StyleManager.DOC_COMMENT.getToken())); //$NON-NLS-2$ //$NON-NLS-1$

        // Add word rule for keywords.
        WordRule wordRule = new WordRule(new DocWordDetector());
        for (String word : docKeywords) {
            wordRule.addWord(word, StyleManager.DOC_KEYWORD.getToken());
        }
        list.add(wordRule);

        setDefaultReturnToken(StyleManager.DOC_COMMENT.getToken());
        setRules(list.toArray(new IRule[0]));
    }
}
