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
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public enum StyleManager {
    BLACK(0, 0, 0), COMMENT(127, 127, 159), CONSTANT(0, 0, 200, SWT.ITALIC), DOC_COMMENT(117, 113, 94), DOC_KEYWORD(
            167, 163, 124, SWT.BOLD), KEYWORD(162, 105, 0, SWT.BOLD), NUMBER(200, 0, 0), PREPROCESSOR(100,
            100, 100, SWT.BOLD), STRING(41, 121, 205), TYPE(133, 44, 26, SWT.BOLD), WHITE(255, 255, 255);

    private TextAttribute attr = null;
    private Color color = null;
    private RGB rgb;
    private int style;
    private IToken token = null;

    private StyleManager(int red, int green, int blue) {
        this(red, green, blue, SWT.NONE);
    }

    private StyleManager(int red, int green, int blue, int style) {
        rgb = new RGB(red, green, blue);
        this.style = style;
    }

    public Color getColor() {
        if (color == null) {
            color = new Color(Display.getCurrent(), rgb);
        }
        return color;
    }

    public int getStyle() {
        return style;
    }

    public TextAttribute getTextAttribute() {
        if (attr == null) {
            if (style == SWT.NONE) {
                attr = new TextAttribute(getColor());
            }
            attr = new TextAttribute(getColor(), WHITE.getColor(), style);
        }
        return attr;
    }

    public IToken getToken() {
        if (token == null) {
            token = new Token(getTextAttribute());
        }
        return token;
    }
}
