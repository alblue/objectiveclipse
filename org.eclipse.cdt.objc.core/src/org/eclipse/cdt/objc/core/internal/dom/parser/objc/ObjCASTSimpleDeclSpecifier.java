/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Rational Software - Initial API and implementation Yuan
 * Zhang / Beth Tibbitts (IBM Research)
 *******************************************************************************/
package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTSimpleDeclSpecifier;

/**
 * @author jcamelon
 */
@SuppressWarnings("restriction")
public class ObjCASTSimpleDeclSpecifier extends ObjCASTBaseDeclSpecifier implements
        IObjCASTSimpleDeclSpecifier {

    private boolean complex = false;
    private boolean imaginary = false;
    private boolean isLong;
    private boolean isShort;
    private boolean isSigned;
    private boolean isUnsigned;
    private boolean longlong;
    private int simpleType;

    @Override
    public boolean accept(ASTVisitor action) {
        if (action.shouldVisitDeclSpecifiers) {
            switch (action.visit(this)) {
                case ASTVisitor.PROCESS_ABORT:
                    return false;
                case ASTVisitor.PROCESS_SKIP:
                    return true;
                default:
                    break;
            }
        }
        if (action.shouldVisitDeclSpecifiers) {
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

    public ObjCASTSimpleDeclSpecifier copy() {
        ObjCASTSimpleDeclSpecifier copy = new ObjCASTSimpleDeclSpecifier();
        copySimpleDeclSpec(copy);
        return copy;
    }

    protected void copySimpleDeclSpec(ObjCASTSimpleDeclSpecifier copy) {
        copyBaseDeclSpec(copy);
        copy.simpleType = simpleType;
        copy.isSigned = isSigned;
        copy.isUnsigned = isUnsigned;
        copy.isShort = isShort;
        copy.isLong = isLong;
        copy.longlong = longlong;
        copy.complex = complex;
        copy.imaginary = imaginary;
    }

    public int getType() {
        return simpleType;
    }

    public boolean isComplex() {
        return complex;
    }

    public boolean isImaginary() {
        return imaginary;
    }

    public boolean isLong() {
        return isLong;
    }

    public boolean isLongLong() {
        return longlong;
    }

    public boolean isShort() {
        return isShort;
    }

    public boolean isSigned() {
        return isSigned;
    }

    public boolean isUnsigned() {
        return isUnsigned;
    }

    public void setComplex(boolean value) {
        assertNotFrozen();
        complex = value;
    }

    public void setImaginary(boolean value) {
        assertNotFrozen();
        imaginary = value;
    }

    public void setLong(boolean value) {
        assertNotFrozen();
        isLong = value;
    }

    public void setLongLong(boolean value) {
        assertNotFrozen();
        longlong = value;
    }

    public void setShort(boolean value) {
        assertNotFrozen();
        isShort = value;
    }

    public void setSigned(boolean value) {
        assertNotFrozen();
        isSigned = value;
    }

    public void setType(int type) {
        assertNotFrozen();
        simpleType = type;
    }

    public void setUnsigned(boolean value) {
        assertNotFrozen();
        isUnsigned = value;
    }
}
