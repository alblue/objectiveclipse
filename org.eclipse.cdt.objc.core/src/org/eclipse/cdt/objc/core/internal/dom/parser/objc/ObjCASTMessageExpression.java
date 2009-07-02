/*******************************************************************************
 * Copyright (c) 2005, 2009 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: John Camelon (IBM Rational Software) - Initial API and
 * implementation Yuan Zhang / Beth Tibbitts (IBM Research) Markus Schorn (Wind
 * River Systems)
 *******************************************************************************/
package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;
import org.eclipse.cdt.internal.core.dom.parser.IASTAmbiguityParent;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTMessageExpression;

/**
 * Binary expression for c
 */
@SuppressWarnings("restriction")
public class ObjCASTMessageExpression extends ASTNode implements IObjCASTMessageExpression,
        IASTAmbiguityParent {

    private IASTExpression receiver;
    private IASTExpression selector;

    public ObjCASTMessageExpression() {
    }

    public ObjCASTMessageExpression(IASTExpression r, IASTExpression s) {
        setReceiverExpression(r);
        setSelectorExpression(s);
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

        if (receiver != null) {
            if (!receiver.accept(action)) {
                return false;
            }
        }
        if (selector != null) {
            if (!selector.accept(action)) {
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

    public ObjCASTMessageExpression copy() {
        ObjCASTMessageExpression copy = new ObjCASTMessageExpression();
        copy.setReceiverExpression(receiver == null ? null : receiver.copy());
        copy.setSelectorExpression(selector == null ? null : selector.copy());
        copy.setOffsetAndLength(this);
        return copy;
    }

    public IType getExpressionType() {
        return getSelectorExpression().getExpressionType();
    }

    public IASTExpression getReceiverExpression() {
        return receiver;
    }

    public IASTExpression getSelectorExpression() {
        return selector;
    }

    public void replace(IASTNode child, IASTNode other) {
        if (child == receiver) {
            other.setPropertyInParent(child.getPropertyInParent());
            other.setParent(child.getParent());
            receiver = (IASTExpression) other;
        }
        if (child == selector) {
            other.setPropertyInParent(child.getPropertyInParent());
            other.setParent(child.getParent());
            selector = (IASTExpression) other;
        }
    }

    public void setReceiverExpression(IASTExpression expression) {
        assertNotFrozen();
        receiver = expression;
        if (expression != null) {
            expression.setParent(this);
            expression.setPropertyInParent(RECEIVER);
        }
    }

    public void setSelectorExpression(IASTExpression expression) {
        assertNotFrozen();
        selector = expression;
        if (expression != null) {
            expression.setParent(this);
            expression.setPropertyInParent(SELECTOR);
        }
    }

}
