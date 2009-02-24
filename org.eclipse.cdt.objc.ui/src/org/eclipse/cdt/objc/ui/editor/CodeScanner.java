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

import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.NumberRule;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.WordRule;

public final class CodeScanner extends BufferedRuleBasedScanner {

    // Constants which are additionally colored.
    private static String[] constants = { "__DATE__", //$NON-NLS-1$
            "__LINE__", //$NON-NLS-1$
            "__TIME__", //$NON-NLS-1$
            "__FILE__", //$NON-NLS-1$
            "__STDC__", //$NON-NLS-1$
            "EXT_TEXT", //$NON-NLS-1$
            "nil", //$NON-NLS-1$
            "NIL", //$NON-NLS-1$
            "NO", //$NON-NLS-1$
            "NULL", //$NON-NLS-1$
            "YES" //$NON-NLS-1$
    };

    // Keywords
    private static String[] keywords = { "@catch", //$NON-NLS-1$
            "@class", //$NON-NLS-1$
            "@end", //$NON-NLS-1$
            "@finally", //$NON-NLS-1$
            "@interface", //$NON-NLS-1$
            "@implementation", //$NON-NLS-1$
            "@private", //$NON-NLS-1$
            "@property", //$NON-NLS-1$
            "@protected", //$NON-NLS-1$
            "@protocol", //$NON-NLS-1$
            "@public", //$NON-NLS-1$
            "@selector", //$NON-NLS-1$
            "@synthesize", //$NON-NLS-1$
            "@throw", //$NON-NLS-1$
            "@try", //$NON-NLS-1$
            "break", //$NON-NLS-1$
            "class", //$NON-NLS-1$
            "const", //$NON-NLS-1$
            "continue", //$NON-NLS-1$
            "do", //$NON-NLS-1$
            "else", //$NON-NLS-1$
            "for", //$NON-NLS-1$
            "if", //$NON-NLS-1$
            "in", //$NON-NLS-1$
            "return", //$NON-NLS-1$
            "self", //$NON-NLS-1$
            "super", //$NON-NLS-1$
            "switch", //$NON-NLS-1$
            "while" //$NON-NLS-1$
    };

    private static String[] types = { "BOOL", //$NON-NLS-1$
            "char", //$NON-NLS-1$
            "double", //$NON-NLS-1$
            "float", //$NON-NLS-1$
            "id", //$NON-NLS-1$
            "IMP", //$NON-NLS-1$
            "int", //$NON-NLS-1$
            "SEL", //$NON-NLS-1$
            "void" //$NON-NLS-1$
    };

    public CodeScanner() {
        createRules();
    }

    protected void createRules() {
        List<IRule> rules = new ArrayList<IRule>();

        // Add rule for single line comments.
        rules.add(new EndOfLineRule("//", StyleManager.COMMENT.getToken())); //$NON-NLS-1$

        // Add rule for strings and character constants.
        rules.add(new SingleLineRule("@\"", "\"", StyleManager.STRING.getToken(), '\\')); //$NON-NLS-1$ //$NON-NLS-2$
        rules.add(new SingleLineRule("\"", "\"", StyleManager.STRING.getToken(), '\\')); //$NON-NLS-1$ //$NON-NLS-2$
        rules.add(new SingleLineRule("'", "'", StyleManager.STRING.getToken(), '\\')); //$NON-NLS-1$ //$NON-NLS-2$

        // Add word rule for keywords, types, and constants.
        WordRule wordRule = new WordRule(new WordDetector(), StyleManager.BLACK.getToken());

        // Add keywords
        for (String word : keywords) {
            wordRule.addWord(word, StyleManager.KEYWORD.getToken());
        }
        // Add types
        for (String word : types) {
            wordRule.addWord(word, StyleManager.TYPE.getToken());
        }
        // Add type constants
        for (String word : constants) {
            wordRule.addWord(word, StyleManager.CONSTANT.getToken());
        }
        rules.add(wordRule);

        // Add rule for numbers
        rules.add(new NumberRule(StyleManager.NUMBER.getToken()));

        // Add rule for preprocessor directives
        rules.add(new EndOfLineRule("#", StyleManager.PREPROCESSOR.getToken())); //$NON-NLS-1$

        // Sets the rules
        setDefaultReturnToken(StyleManager.BLACK.getToken());
        setRules(rules.toArray(new IRule[0]));
    }

}