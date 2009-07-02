/*******************************************************************************
 * Copyright (c) 2005, 2009 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: John Camelon (IBM Rational Software) - Initial API and
 * implementation Yuan Zhang / Beth Tibbitts (IBM Research) Markus Schorn (Wind
 * River Systems)
 *******************************************************************************/
package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.IBasicType;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.parser.util.CharArrayUtils;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTLiteralExpression;

/**
 * Represents a literal
 */
@SuppressWarnings("restriction")
public class ObjCASTLiteralExpression extends ASTNode implements IObjCASTLiteralExpression {

    private int kind;
    private char[] value = CharArrayUtils.EMPTY;

    public ObjCASTLiteralExpression() {
    }

    public ObjCASTLiteralExpression(int kind, char[] value) {
        this.kind = kind;
        this.value = value;
    }

    /**
     * @deprecated use {@link #ObjCASTLiteralExpression(int, char[])}, instead.
     */
    @Deprecated
    public ObjCASTLiteralExpression(int kind, String value) {
        this(kind, value.toCharArray());
    }

    @Override
    public boolean accept(ASTVisitor action) {
        if (action.shouldVisitExpressions) {
            switch (action.visit(this)) {
                case ASTVisitor.PROCESS_ABORT:
                    return false;
                case ASTVisitor.PROCESS_SKIP:
                    return true;
                default:
                    break;
            }
        }
        if (action.shouldVisitExpressions) {
            switch (action.leave(this)) {
                case ASTVisitor.PROCESS_ABORT:
                    return false;
                case ASTVisitor.PROCESS_SKIP:
                    return true;
                default:
                    break;
            }
        }
        return true;
    }

    private IType classifyTypeOfFloatLiteral() {
        final char[] lit = getValue();
        final int len = lit.length;
        int kind = IBasicType.t_double;
        int flags = 0;
        if (len > 0) {
            switch (lit[len - 1]) {
                case 'f':
                case 'F':
                    kind = IBasicType.t_float;
                    break;
                case 'l':
                case 'L':
                    kind = IBasicType.t_double;
                    flags |= ObjCBasicType.IS_LONG;
                    break;
            }
        }

        return new ObjCBasicType(kind, flags, this);
    }

    private IType classifyTypeOfIntLiteral() {
        int makelong = 0;
        boolean unsigned = false;

        final char[] lit = getValue();
        for (int i = lit.length - 1; i >= 0; i--) {
            final char c = lit[i];
            if (!(c > 'f' && c <= 'z') && !(c > 'F' && c <= 'Z')) {
                break;
            }
            switch (c) {
                case 'u':
                case 'U':
                    unsigned = true;
                    break;
                case 'l':
                case 'L':
                    makelong++;
                    break;
            }
        }

        int flags = 0;
        if (unsigned) {
            flags |= ObjCBasicType.IS_UNSIGNED;
        }

        if (makelong > 1) {
            flags |= ObjCBasicType.IS_LONGLONG;
        } else if (makelong == 1) {
            flags |= ObjCBasicType.IS_LONG;
        }
        return new ObjCBasicType(IBasicType.t_int, flags, this);
    }

    public ObjCASTLiteralExpression copy() {
        ObjCASTLiteralExpression copy = new ObjCASTLiteralExpression(kind, value == null ? null : value
                .clone());
        copy.setOffsetAndLength(this);
        return copy;
    }

    public IType getExpressionType() {
        switch (getKind()) {
            case IASTLiteralExpression.lk_char_constant:
                return new ObjCBasicType(IBasicType.t_char, 0, this);
            case IASTLiteralExpression.lk_float_constant:
                return classifyTypeOfFloatLiteral();
            case IASTLiteralExpression.lk_integer_constant:
                return classifyTypeOfIntLiteral();
            case IASTLiteralExpression.lk_string_literal:
                IType type = new ObjCBasicType(IBasicType.t_char, 0, this);
                type = new ObjCQualifierType(type, true, false, false);
                return new ObjCPointerType(type, 0);
        }
        return null;
    }

    public int getKind() {
        return kind;
    }

    public char[] getValue() {
        return value;
    }

    public void setKind(int value) {
        assertNotFrozen();
        kind = value;
    }

    public void setValue(char[] value) {
        assertNotFrozen();
        this.value = value;
    }

    /**
     * @deprecated, use {@link #setValue(char[])}, instead.
     */
    @Deprecated
    public void setValue(String value) {
        assertNotFrozen();
        this.value = value.toCharArray();
    }

    @Override
    public String toString() {
        return new String(value);
    }
}
