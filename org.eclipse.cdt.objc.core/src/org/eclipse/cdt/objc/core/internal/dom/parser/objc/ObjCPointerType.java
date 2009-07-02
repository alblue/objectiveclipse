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
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.ITypedef;
import org.eclipse.cdt.core.dom.ast.c.ICPointerType;
import org.eclipse.cdt.internal.core.dom.parser.ITypeContainer;

/**
 * @author dsteffle
 */
@SuppressWarnings("restriction")
public class ObjCPointerType implements ICPointerType, ITypeContainer {
    static public final int IS_CONST = 1;
    static public final int IS_RESTRICT = 1 << 1;
    static public final int IS_VOLATILE = 1 << 2;

    IType nextType = null;
    private int qualifiers = 0;

    public ObjCPointerType() {
    }

    public ObjCPointerType(IType next, int qualifiers) {
        nextType = next;
        this.qualifiers = qualifiers;
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
     * @see org.eclipse.cdt.core.dom.ast.IPointerType#getType()
     */
    public IType getType() {
        return nextType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.cdt.core.dom.ast.IPointerType#isConst()
     */
    public boolean isConst() {
        return (qualifiers & IS_CONST) != 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.cdt.core.dom.ast.c.ICPointerType#isRestrict()
     */
    public boolean isRestrict() {
        return (qualifiers & IS_RESTRICT) != 0;
    }

    public boolean isSameType(IType obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ITypedef) {
            return obj.isSameType(this);
        }

        if (obj instanceof ICPointerType) {
            ICPointerType pt = (ICPointerType) obj;
            try {
                if (isConst() != pt.isConst()) {
                    return false;
                }
                if (isRestrict() != pt.isRestrict()) {
                    return false;
                }
                if (isVolatile() != pt.isVolatile()) {
                    return false;
                }

                return pt.getType().isSameType(nextType);
            } catch (DOMException e) {
                return false;
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.cdt.core.dom.ast.IPointerType#isVolatile()
     */
    public boolean isVolatile() {
        return (qualifiers & IS_VOLATILE) != 0;
    }

    public void setQualifiers(int qualifiers) {
        this.qualifiers = qualifiers;
    }

    public void setType(IType type) {
        nextType = type;
    }
}
