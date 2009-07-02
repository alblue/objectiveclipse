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
import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.IArrayType;
import org.eclipse.cdt.core.dom.ast.IPointerType;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;
import org.eclipse.cdt.internal.core.dom.parser.IASTAmbiguityParent;
import org.eclipse.cdt.internal.core.dom.parser.ITypeContainer;

/**
 * Unary expression in C.
 */
@SuppressWarnings("restriction")
public class ObjCASTUnaryExpression extends ASTNode implements IASTUnaryExpression, IASTAmbiguityParent {
    private IASTExpression operand;
    private int operator;

    public ObjCASTUnaryExpression() {
    }

    public ObjCASTUnaryExpression(int operator, IASTExpression operand) {
        this.operator = operator;
        setOperand(operand);
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

        if (operand != null) {
            if (!operand.accept(action)) {
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

    public ObjCASTUnaryExpression copy() {
        ObjCASTUnaryExpression copy = new ObjCASTUnaryExpression(operator, operand == null ? null : operand
                .copy());
        copy.setOffsetAndLength(this);
        return copy;
    }

    public IType getExpressionType() {
        IType type = getOperand().getExpressionType();
        int op = getOperator();
        try {
            if (op == IASTUnaryExpression.op_star
                    && (type instanceof IPointerType || type instanceof IArrayType)) {
                return ((ITypeContainer) type).getType();
            } else if (op == IASTUnaryExpression.op_amper) {
                return new ObjCPointerType(type, 0);
            }
        } catch (DOMException e) {
            return e.getProblem();
        }
        return type;
    }

    public IASTExpression getOperand() {
        return operand;
    }

    public int getOperator() {
        return operator;
    }

    public void replace(IASTNode child, IASTNode other) {
        if (child == operand) {
            other.setPropertyInParent(child.getPropertyInParent());
            other.setParent(child.getParent());
            operand = (IASTExpression) other;
        }
    }

    public void setOperand(IASTExpression expression) {
        assertNotFrozen();
        operand = expression;
        if (expression != null) {
            expression.setParent(this);
            expression.setPropertyInParent(OPERAND);
        }
    }

    public void setOperator(int value) {
        assertNotFrozen();
        operator = value;
    }

}
