/*******************************************************************************
 * Copyright (c) 2005, 2009 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: John Camelon (IBM Rational Software) - Initial API and
 * implementation Yuan Zhang / Beth Tibbitts (IBM Research) Bryan Wilkinson
 * (QNX) Markus Schorn (Wind River Systems)
 *******************************************************************************/
package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTCompletionContext;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFieldReference;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.IVariable;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;
import org.eclipse.cdt.internal.core.dom.parser.IASTAmbiguityParent;

/**
 * Field reference in C.
 */
@SuppressWarnings("restriction")
public class ObjCASTFieldReference extends ASTNode implements IASTFieldReference, IASTAmbiguityParent,
        IASTCompletionContext {

    private IASTName name;
    private IASTExpression owner;
    private boolean ptr;

    public ObjCASTFieldReference() {
    }

    public ObjCASTFieldReference(IASTName name, IASTExpression owner) {
        this(name, owner, false);
    }

    public ObjCASTFieldReference(IASTName name, IASTExpression owner, boolean ptr) {
        setFieldOwner(owner);
        setFieldName(name);
        this.ptr = ptr;
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

        if (owner != null) {
            if (!owner.accept(action)) {
                return false;
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

    public ObjCASTFieldReference copy() {
        ObjCASTFieldReference copy = new ObjCASTFieldReference();
        copy.setFieldOwner(owner == null ? null : owner.copy());
        copy.setFieldName(name == null ? null : name.copy());
        copy.ptr = ptr;
        copy.setOffsetAndLength(this);
        return copy;
    }

    public IBinding[] findBindings(IASTName n, boolean isPrefix) {
        return ObjCVisitor.findBindingsForContentAssist(n, isPrefix);
    }

    public IType getExpressionType() {
        IBinding binding = getFieldName().resolveBinding();
        if (binding instanceof IVariable) {
            try {
                return ((IVariable) binding).getType();
            } catch (DOMException e) {
                return e.getProblem();
            }
        }
        return null;
    }

    public IASTName getFieldName() {
        return name;
    }

    public IASTExpression getFieldOwner() {
        return owner;
    }

    public int getRoleForName(IASTName n) {
        if (n == name) {
            return r_reference;
        }
        return r_unclear;
    }

    public boolean isPointerDereference() {
        return ptr;
    }

    public void replace(IASTNode child, IASTNode other) {
        if (child == owner) {
            other.setPropertyInParent(child.getPropertyInParent());
            other.setParent(child.getParent());
            owner = (IASTExpression) other;
        }
    }

    public void setFieldName(IASTName name) {
        assertNotFrozen();
        this.name = name;
        if (name != null) {
            name.setParent(this);
            name.setPropertyInParent(FIELD_NAME);
        }
    }

    public void setFieldOwner(IASTExpression expression) {
        assertNotFrozen();
        owner = expression;
        if (expression != null) {
            expression.setParent(this);
            expression.setPropertyInParent(FIELD_OWNER);
        }
    }

    public void setIsPointerDereference(boolean value) {
        assertNotFrozen();
        ptr = value;
    }
}
