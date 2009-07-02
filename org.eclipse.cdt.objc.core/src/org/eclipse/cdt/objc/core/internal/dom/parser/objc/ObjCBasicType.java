/*******************************************************************************
 * Copyright (c) 2005, 2009 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Rational Software - Initial API and implementation Markus
 * Schorn (Wind River Systems)
 *******************************************************************************/
package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IBasicType;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.ITypedef;
import org.eclipse.cdt.internal.core.index.IIndexType;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTSimpleDeclSpecifier;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCBasicType;

/**
 * @author dsteffle
 */
@SuppressWarnings("restriction")
public class ObjCBasicType implements IObjCBasicType {
    static public final int IS_COMPLEX = 1 << 5;
    static public final int IS_IMAGINARY = 1 << 6;
    static public final int IS_LONG = 1;
    static public final int IS_LONGLONG = 1 << 1;
    static public final int IS_SHORT = 1 << 2;
    static public final int IS_SIGNED = 1 << 3;
    static public final int IS_UNSIGNED = 1 << 4;

    private int qualifiers = 0;
    private int type = 0;
    private IASTExpression value = null;

    public ObjCBasicType(int type, int qualifiers) {
        this.type = type;
        this.qualifiers = qualifiers;

        if (type == IBasicType.t_unspecified) {
            if ((qualifiers & (IS_COMPLEX | IS_IMAGINARY)) != 0) {
                type = IBasicType.t_float;
            } else {
                type = IBasicType.t_int;
            }
        }
    }

    public ObjCBasicType(int type, int qualifiers, IASTExpression value) {
        this.type = type;
        this.qualifiers = qualifiers;
        this.value = value;
    }

    /**
     * keep a reference to the declaration specifier so that duplicate
     * information isn't generated.
     * 
     * @param sds
     *            the simple declaration specifier
     */
    public ObjCBasicType(IObjCASTSimpleDeclSpecifier sds) {
        type = sds.getType();
        qualifiers = (sds.isLong() ? ObjCBasicType.IS_LONG : 0)
                | (sds.isShort() ? ObjCBasicType.IS_SHORT : 0)
                | (sds.isSigned() ? ObjCBasicType.IS_SIGNED : 0)
                | (sds.isUnsigned() ? ObjCBasicType.IS_UNSIGNED : 0)
                | (sds.isLongLong() ? ObjCBasicType.IS_LONGLONG : 0)
                | (sds.isComplex() ? ObjCBasicType.IS_COMPLEX : 0)
                | (sds.isImaginary() ? ObjCBasicType.IS_IMAGINARY : 0);

        if (type == IBasicType.t_unspecified) {
            if ((qualifiers & (IS_COMPLEX | IS_IMAGINARY)) != 0) {
                type = IBasicType.t_float;
            } else {
                type = IBasicType.t_int;
            }
        }
    }

    @Override
    public Object clone() {
        IType t = null;
        try {
            t = (IType) super.clone();
        } catch (CloneNotSupportedException e) {
            // not going to happen
        }
        return t;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.cdt.core.dom.ast.IBasicType#getType()
     */
    public int getType() {
        return type;
    }

    @Deprecated
    public IASTExpression getValue() {
        return value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.cdt.core.dom.ast.c.ICBasicType#isComplex()
     */
    public boolean isComplex() {
        return (qualifiers & IS_COMPLEX) != 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.cdt.core.dom.ast.c.ICBasicType#isImaginary()
     */
    public boolean isImaginary() {
        return (qualifiers & IS_IMAGINARY) != 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.cdt.core.dom.ast.IBasicType#isLong()
     */
    public boolean isLong() {
        return (qualifiers & IS_LONG) != 0;
    }

    public boolean isLongLong() {
        return (qualifiers & IS_LONGLONG) != 0;
    }

    public boolean isSameType(IType obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ITypedef || obj instanceof IIndexType) {
            return obj.isSameType(this);
        }

        if (!(obj instanceof ObjCBasicType)) {
            return false;
        }

        ObjCBasicType cObj = (ObjCBasicType) obj;

        if (type != cObj.type) {
            return false;
        }

        if (type == IBasicType.t_int) {
            // signed int and int are equivalent
            return (qualifiers & ~IS_SIGNED) == (cObj.qualifiers & ~IS_SIGNED);
        } else {
            return (qualifiers == cObj.qualifiers);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.cdt.core.dom.ast.IBasicType#isShort()
     */
    public boolean isShort() {
        return (qualifiers & IS_SHORT) != 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.cdt.core.dom.ast.IBasicType#isSigned()
     */
    public boolean isSigned() {
        return (qualifiers & IS_SIGNED) != 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.cdt.core.dom.ast.IBasicType#isUnsigned()
     */
    public boolean isUnsigned() {
        return (qualifiers & IS_UNSIGNED) != 0;
    }

    public void setValue(IASTExpression expression) {
        value = expression;
    }
}
