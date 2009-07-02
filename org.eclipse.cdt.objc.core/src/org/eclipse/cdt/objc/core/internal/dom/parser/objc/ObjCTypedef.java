/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Rational Software - Initial API and implementation Markus
 * Schorn (Wind River Systems)
 *******************************************************************************/
package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ILinkage;
import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IScope;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.ITypedef;
import org.eclipse.cdt.internal.core.dom.Linkage;
import org.eclipse.cdt.internal.core.dom.parser.ITypeContainer;
import org.eclipse.core.runtime.PlatformObject;

/**
 * Represents a typedef.
 */
@SuppressWarnings("restriction")
public class ObjCTypedef extends PlatformObject implements ITypedef, ITypeContainer, IObjCInternalBinding {
    private final IASTName name;
    private IType type = null;

    public ObjCTypedef(IASTName name) {
        this.name = name;
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

    public IASTNode[] getDeclarations() {
        return IASTNode.EMPTY_NODE_ARRAY;
    }

    public IASTNode getDefinition() {
        return name;
    }

    public ILinkage getLinkage() {
        return Linkage.OBJC_LINKAGE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.cdt.core.dom.ast.IBinding#getName()
     */
    public String getName() {
        return name.toString();
    }

    public char[] getNameCharArray() {
        return name.toCharArray();
    }

    public IBinding getOwner() throws DOMException {
        return ObjCVisitor.findEnclosingFunction(name);
    }

    public IASTNode getPhysicalNode() {
        return name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.cdt.core.dom.ast.IBinding#getScope()
     */
    public IScope getScope() {
        IASTDeclarator declarator = (IASTDeclarator) name.getParent();
        return ObjCVisitor.getContainingScope(declarator.getParent());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.cdt.core.dom.ast.ITypedef#getType()
     */
    public IType getType() {
        if (type == null && name.getParent() instanceof IASTDeclarator) {
            type = ObjCVisitor.createType((IASTDeclarator) name.getParent());
        }
        return type;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.cdt.core.dom.ast.IType#isSameType(org.eclipse.cdt.core.dom
     * .ast.IType)
     */
    public boolean isSameType(IType t) {
        if (t == this) {
            return true;
        }
        if (t instanceof ITypedef) {
            try {
                IType temp = getType();
                if (temp != null) {
                    return temp.isSameType(((ITypedef) t).getType());
                }
                return false;
            } catch (DOMException e) {
                return false;
            }
        }

        IType temp = getType();
        if (temp != null) {
            return temp.isSameType(t);
        }
        return false;
    }

    public void setType(IType t) {
        type = t;
    }
}
