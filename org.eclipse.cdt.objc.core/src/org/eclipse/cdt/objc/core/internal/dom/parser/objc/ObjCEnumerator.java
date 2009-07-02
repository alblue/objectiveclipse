/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Andrew Niefer (IBM Corporation) - initial API and
 * implementation Markus Schorn (Wind River Systems)
 *******************************************************************************/
package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ILinkage;
import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTEnumerationSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IEnumeration;
import org.eclipse.cdt.core.dom.ast.IEnumerator;
import org.eclipse.cdt.core.dom.ast.IScope;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.IValue;
import org.eclipse.cdt.core.dom.ast.IASTEnumerationSpecifier.IASTEnumerator;
import org.eclipse.cdt.internal.core.dom.Linkage;
import org.eclipse.cdt.internal.core.dom.parser.ASTEnumerator;
import org.eclipse.cdt.internal.core.dom.parser.ProblemBinding;
import org.eclipse.cdt.internal.core.dom.parser.Value;
import org.eclipse.core.runtime.PlatformObject;

/**
 * C-specific binding for enumerators.
 */
@SuppressWarnings("restriction")
public class ObjCEnumerator extends PlatformObject implements IEnumerator {
    public static class CEnumeratorProblem extends ProblemBinding implements IEnumerator {
        public CEnumeratorProblem(IASTNode node, int id, char[] arg) {
            super(node, id, arg);
        }

        public IType getType() throws DOMException {
            throw new DOMException(this);
        }

        public IValue getValue() {
            return Value.UNKNOWN;
        }
    }

    private final IASTName enumeratorName;

    public ObjCEnumerator(IASTEnumerator enumtor) {
        enumeratorName = enumtor.getName();
        enumeratorName.setBinding(this);
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
        return enumeratorName.toString();
    }

    public char[] getNameCharArray() {
        return enumeratorName.toCharArray();
    }

    public IBinding getOwner() throws DOMException {
        return ObjCVisitor.findEnclosingFunction(enumeratorName);
    }

    public IASTNode getPhysicalNode() {
        return enumeratorName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.cdt.core.dom.ast.IBinding#getScope()
     */
    public IScope getScope() {
        return ObjCVisitor.getContainingScope(enumeratorName.getParent());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.cdt.core.dom.ast.IEnumerator#getType()
     */
    public IType getType() {
        IASTEnumerator etor = (IASTEnumerator) enumeratorName.getParent();
        IASTEnumerationSpecifier enumSpec = (IASTEnumerationSpecifier) etor.getParent();
        IEnumeration enumeration = (IEnumeration) enumSpec.getName().resolveBinding();
        return enumeration;
    }

    public IValue getValue() {
        IASTNode parent = enumeratorName.getParent();
        if (parent instanceof ASTEnumerator) {
            return ((ASTEnumerator) parent).getIntegralValue();
        }

        return Value.UNKNOWN;
    }
}
