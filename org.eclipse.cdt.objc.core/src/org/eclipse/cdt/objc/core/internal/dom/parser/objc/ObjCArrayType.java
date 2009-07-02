/*******************************************************************************
 * Copyright (c) 2004, 2009 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.ASTTypeUtil;
import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IArrayType;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.ITypedef;
import org.eclipse.cdt.core.dom.ast.c.ICArrayType;
import org.eclipse.cdt.internal.core.dom.parser.ITypeContainer;
import org.eclipse.cdt.internal.core.index.IIndexType;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTArrayModifier;

/**
 * @author dsteffle
 */
@SuppressWarnings("restriction")
public class ObjCArrayType implements ICArrayType, ITypeContainer {
    IObjCASTArrayModifier mod;
    IType type;

    public ObjCArrayType(IType type) {
        this.type = type;
    }

    @Override
    public Object clone() {
        IType t = null;
        try {
            t = (IType) super.clone();
        } catch (CloneNotSupportedException e) {
            // Not going to happen
        }
        return t;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.cdt.core.dom.ast.IArrayType#getArraySizeExpression()
     */
    public IASTExpression getArraySizeExpression() {
        if (mod != null) {
            return mod.getConstantExpression();
        }
        return null;
    }

    public IObjCASTArrayModifier getModifier() {
        return mod;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.cdt.core.dom.ast.IArrayType#getType()
     */
    public IType getType() {
        return type;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.cdt.core.dom.ast.c.ICArrayType#isConst()
     */
    public boolean isConst() {
        if (mod == null) {
            return false;
        }
        return mod.isConst();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.cdt.core.dom.ast.c.ICArrayType#isRestrict()
     */
    public boolean isRestrict() {
        if (mod == null) {
            return false;
        }
        return mod.isRestrict();
    }

    public boolean isSameType(IType obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ITypedef) {
            return obj.isSameType(this);
        }
        if (obj instanceof ICArrayType) {
            ICArrayType at = (ICArrayType) obj;
            try {
                if (isConst() != at.isConst()) {
                    return false;
                }
                if (isRestrict() != at.isRestrict()) {
                    return false;
                }
                if (isStatic() != at.isStatic()) {
                    return false;
                }
                if (isVolatile() != at.isVolatile()) {
                    return false;
                }
                if (isVariableLength() != at.isVariableLength()) {
                    return false;
                }

                return at.getType().isSameType(type);
            } catch (DOMException e) {
                return false;
            }
        }
        // Workaround for bug 182976, no PDOMCArrayType.
        else if (obj instanceof IArrayType && obj instanceof IIndexType) {
            return obj.isSameType(this);
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.cdt.core.dom.ast.c.ICArrayType#isStatic()
     */
    public boolean isStatic() {
        if (mod == null) {
            return false;
        }
        return mod.isStatic();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.cdt.core.dom.ast.c.ICArrayType#isVariableLength()
     */
    public boolean isVariableLength() {
        if (mod == null) {
            return false;
        }
        return mod.isVariableSized();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.cdt.core.dom.ast.c.ICArrayType#isVolatile()
     */
    public boolean isVolatile() {
        if (mod == null) {
            return false;
        }
        return mod.isVolatile();
    }

    public void setModifier(IObjCASTArrayModifier mod) {
        this.mod = mod;
    }

    public void setType(IType t) {
        type = t;
    }

    @Override
    public String toString() {
        return ASTTypeUtil.getType(this);
    }
}
