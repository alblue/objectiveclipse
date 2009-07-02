/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Rational Software - Initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTElaboratedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTEnumerationSpecifier;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.ITypedef;
import org.eclipse.cdt.core.dom.ast.c.ICQualifierType;
import org.eclipse.cdt.internal.core.dom.parser.ITypeContainer;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTDeclSpecifier;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTSimpleDeclSpecifier;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTTypedefNameSpecifier;

/**
 * @author dsteffle
 */
@SuppressWarnings("restriction")
public class ObjCQualifierType implements ICQualifierType, ITypeContainer {

    private final boolean isConst;
    private final boolean isRestrict;
    private final boolean isVolatile;
    private IType type = null;

    /**
     * CQualifierType has an IBasicType to keep track of the basic type
     * information.
     */
    public ObjCQualifierType(IObjCASTDeclSpecifier declSpec) {
        type = resolveType(declSpec);
        isConst = declSpec.isConst();
        isVolatile = declSpec.isVolatile();
        isRestrict = declSpec.isRestrict();
    }

    public ObjCQualifierType(IType type, boolean isConst, boolean isVolatile, boolean isRestrict) {
        this.type = type;
        this.isConst = isConst;
        this.isVolatile = isVolatile;
        this.isRestrict = isRestrict;
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

    public IType getType() {
        return type;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.cdt.core.dom.ast.IQualifierType#isConst()
     */
    public boolean isConst() {
        return isConst;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.cdt.core.dom.ast.c.ICQualifierType#isRestrict()
     */
    public boolean isRestrict() {
        return isRestrict;
    }

    public boolean isSameType(IType obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ITypedef) {
            return obj.isSameType(this);
        }

        if (obj instanceof ICQualifierType) {
            ICQualifierType qt = (ICQualifierType) obj;
            try {
                if (isConst() != qt.isConst()) {
                    return false;
                }
                if (isRestrict() != qt.isRestrict()) {
                    return false;
                }
                if (isVolatile() != qt.isVolatile()) {
                    return false;
                }

                if (type == null) {
                    return false;
                }
                return type.isSameType(qt.getType());
            } catch (DOMException e) {
                return false;
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.cdt.core.dom.ast.IQualifierType#isVolatile()
     */
    public boolean isVolatile() {
        return isVolatile;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.cdt.core.dom.ast.IQualifierType#getType()
     */
    private IType resolveType(IObjCASTDeclSpecifier declSpec) {
        IType t = null;
        if (declSpec instanceof IObjCASTTypedefNameSpecifier) {
            IObjCASTTypedefNameSpecifier nameSpec = (IObjCASTTypedefNameSpecifier) declSpec;
            t = (IType) nameSpec.getName().resolveBinding();
        } else if (declSpec instanceof IASTElaboratedTypeSpecifier) {
            IASTElaboratedTypeSpecifier elabTypeSpec = (IASTElaboratedTypeSpecifier) declSpec;
            t = (IType) elabTypeSpec.getName().resolveBinding();
        } else if (declSpec instanceof IASTCompositeTypeSpecifier) {
            IASTCompositeTypeSpecifier compTypeSpec = (IASTCompositeTypeSpecifier) declSpec;
            t = (IType) compTypeSpec.getName().resolveBinding();
        } else if (declSpec instanceof IASTEnumerationSpecifier) {
            t = new ObjCEnumeration(((IASTEnumerationSpecifier) declSpec).getName());
        } else {
            t = new ObjCBasicType((IObjCASTSimpleDeclSpecifier) declSpec);
        }

        return t;
    }

    public void setType(IType t) {
        type = t;
    }
}
