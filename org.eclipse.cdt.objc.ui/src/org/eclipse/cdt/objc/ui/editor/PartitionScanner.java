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

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;

public class PartitionScanner extends RuleBasedPartitionScanner {

    static class EmptyCommentDetector implements IWordDetector {

        public boolean isWordPart(char c) {
            return (c == '*' || c == '/');
        }

        public boolean isWordStart(char c) {
            return (c == '/');
        }
    }

    static class WordPredicateRule extends WordRule implements IPredicateRule {

        private final IToken successToken;

        public WordPredicateRule(IToken successToken) {
            super(new EmptyCommentDetector());
            this.successToken = successToken;
            addWord("/**/", successToken); //$NON-NLS-1$
        }

        public IToken evaluate(ICharacterScanner scanner, boolean resume) {
            return super.evaluate(scanner);
        }

        public IToken getSuccessToken() {
            return successToken;
        }
    }

    public PartitionScanner() {
        IToken doc = new Token(IPartition.DOC_COMMENT);
        IToken comment = new Token(IPartition.MULTI_LINE_COMMENT);

        List<IRule> rules = new ArrayList<IRule>();

        // Add rule for single line comments.
        rules.add(new EndOfLineRule("//", Token.UNDEFINED)); //$NON-NLS-1$

        // Add rule for strings and character constants.
        rules.add(new SingleLineRule("@\"", "\"", Token.UNDEFINED, '\\')); //$NON-NLS-1$ //$NON-NLS-2$
        rules.add(new SingleLineRule("\"", "\"", Token.UNDEFINED, '\\')); //$NON-NLS-2$ //$NON-NLS-1$
        rules.add(new SingleLineRule("'", "'", Token.UNDEFINED, '\\')); //$NON-NLS-2$ //$NON-NLS-1$

        // Add special case word rule.
        rules.add(new WordPredicateRule(comment));

        // Add rules for multi-line comments and javadoc.
        rules.add(new MultiLineRule("/**", "*/", doc, (char) 0, true)); //$NON-NLS-1$ //$NON-NLS-2$
        rules.add(new MultiLineRule("/*", "*/", comment, (char) 0, true)); //$NON-NLS-1$ //$NON-NLS-2$

        setPredicateRules(rules.toArray(new IPredicateRule[0]));
    }
}
