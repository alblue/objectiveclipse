/*******************************************************************************
 * Copyright (c) 2005, 2009 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: John Camelon (IBM Rational Software) - Initial API and
 * implementation Yuan Zhang / Beth Tibbitts (IBM Research) Bryan Wilkinson
 * (QNX) Anton Leherbauer (Wind River Systems)
 *******************************************************************************/
package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTCompletionContext;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.ICompositeType;
import org.eclipse.cdt.core.dom.ast.IEnumeration;
import org.eclipse.cdt.core.dom.ast.IEnumerator;
import org.eclipse.cdt.core.dom.ast.IFunction;
import org.eclipse.cdt.core.dom.ast.IProblemBinding;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.IVariable;
import org.eclipse.cdt.core.parser.util.ArrayUtil;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;

/**
 * ID Expression in C.
 */
@SuppressWarnings("restriction")
public class ObjCASTIdExpression extends ASTNode implements IASTIdExpression, IASTCompletionContext {

    private IASTName name;

    public ObjCASTIdExpression() {
    }

    public ObjCASTIdExpression(IASTName name) {
        setName(name);
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

        if (name != null) {
            if (!name.accept(action)) {
                return false;
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

    public ObjCASTIdExpression copy() {
        ObjCASTIdExpression copy = new ObjCASTIdExpression(name == null ? null : name.copy());
        copy.setOffsetAndLength(this);
        return copy;
    }

    public IBinding[] findBindings(IASTName n, boolean isPrefix) {
        IBinding[] bindings = ObjCVisitor.findBindingsForContentAssist(n, isPrefix);

        for (int i = 0; i < bindings.length; i++) {
            if (bindings[i] instanceof IEnumeration || bindings[i] instanceof ICompositeType) {
                bindings[i] = null;
            }
        }

        return (IBinding[]) ArrayUtil.removeNulls(IBinding.class, bindings);
    }

    public IType getExpressionType() {
        IBinding binding = getName().resolveBinding();
        try {
            if (binding instanceof IVariable) {
                return ((IVariable) binding).getType();
            }
            if (binding instanceof IFunction) {
                return ((IFunction) binding).getType();
            }
            if (binding instanceof IEnumerator) {
                return ((IEnumerator) binding).getType();
            }
            if (binding instanceof IProblemBinding) {
                return (IProblemBinding) binding;
            }
        } catch (DOMException e) {
            return e.getProblem();
        }
        return null;
    }

    public IASTName getName() {
        return name;
    }

    public int getRoleForName(IASTName n) {
        if (n == name) {
            return r_reference;
        }
        return r_unclear;
    }

    public void setName(IASTName name) {
        assertNotFrozen();
        this.name = name;
        if (name != null) {
            name.setParent(this);
            name.setPropertyInParent(ID_NAME);
        }
    }
}
