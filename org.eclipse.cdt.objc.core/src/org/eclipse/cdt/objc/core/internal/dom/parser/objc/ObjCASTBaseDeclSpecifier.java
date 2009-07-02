/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Rational Software - Initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.internal.core.dom.parser.ASTNode;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTDeclSpecifier;

/**
 * @author jcamelon
 */
@SuppressWarnings("restriction")
public abstract class ObjCASTBaseDeclSpecifier extends ASTNode implements IObjCASTDeclSpecifier {

    protected boolean isConst;
    protected boolean isInline;
    private boolean isProtocol;
    protected boolean isRestrict;
    protected boolean isVolatile;
    protected int storageClass;

    protected void copyBaseDeclSpec(ObjCASTBaseDeclSpecifier copy) {
        copy.storageClass = storageClass;
        copy.isConst = isConst;
        copy.isVolatile = isVolatile;
        copy.isRestrict = isRestrict;
        copy.isInline = isInline;
        copy.isProtocol = isProtocol;
        copy.setOffsetAndLength(this);
    }

    public int getStorageClass() {
        return storageClass;
    }

    public boolean isConst() {
        return isConst;
    }

    public boolean isInline() {
        return isInline;
    }

    public boolean isProtocol() {
        return isProtocol;
    }

    public boolean isRestrict() {
        return isRestrict;
    }

    public boolean isVolatile() {
        return isVolatile;
    }

    public void setConst(boolean value) {
        assertNotFrozen();
        isConst = value;
    }

    public void setInline(boolean value) {
        assertNotFrozen();
        isInline = value;
    }

    public void setProtocol(boolean value) {
        assertNotFrozen();
        isProtocol = value;
    }

    public void setRestrict(boolean value) {
        assertNotFrozen();
        isRestrict = value;
    }

    public void setStorageClass(int storageClass) {
        assertNotFrozen();
        this.storageClass = storageClass;
    }

    public void setVolatile(boolean value) {
        assertNotFrozen();
        isVolatile = value;
    }
}
