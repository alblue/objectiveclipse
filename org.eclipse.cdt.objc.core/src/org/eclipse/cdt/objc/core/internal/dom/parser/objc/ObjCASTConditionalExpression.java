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
import org.eclipse.cdt.core.dom.ast.IASTConditionalExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IPointerType;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;
import org.eclipse.cdt.internal.core.dom.parser.IASTAmbiguityParent;

/**
 * Conditional expression in C
 */
@SuppressWarnings("restriction")
public class ObjCASTConditionalExpression extends ASTNode implements IASTConditionalExpression,
        IASTAmbiguityParent {

    private IASTExpression condition;
    private IASTExpression negative;
    private IASTExpression positive;

    public ObjCASTConditionalExpression() {
    }

    public ObjCASTConditionalExpression(IASTExpression condition, IASTExpression positive,
            IASTExpression negative) {
        setLogicalConditionExpression(condition);
        setPositiveResultExpression(positive);
        setNegativeResultExpression(negative);
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

        if (condition != null) {
            if (!condition.accept(action)) {
                return false;
            }
        }
        if (positive != null) {
            if (!positive.accept(action)) {
                return false;
            }
        }
        if (negative != null) {
            if (!negative.accept(action)) {
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

    public ObjCASTConditionalExpression copy() {
        ObjCASTConditionalExpression copy = new ObjCASTConditionalExpression();
        copy.setLogicalConditionExpression(condition == null ? null : condition.copy());
        copy.setPositiveResultExpression(positive == null ? null : positive.copy());
        copy.setNegativeResultExpression(negative == null ? null : negative.copy());
        copy.setOffsetAndLength(this);
        return copy;
    }

    public IType getExpressionType() {
        IASTExpression positiveExpression = getPositiveResultExpression();
        if (positiveExpression == null) {
            positiveExpression = getLogicalConditionExpression();
        }
        IType t2 = positiveExpression.getExpressionType();
        IType t3 = getNegativeResultExpression().getExpressionType();
        if (t3 instanceof IPointerType || t2 == null) {
            return t3;
        }
        return t2;
    }

    public IASTExpression getLogicalConditionExpression() {
        return condition;
    }

    public IASTExpression getNegativeResultExpression() {
        return negative;
    }

    public IASTExpression getPositiveResultExpression() {
        return positive;
    }

    public void replace(IASTNode child, IASTNode other) {
        if (child == condition) {
            other.setPropertyInParent(child.getPropertyInParent());
            other.setParent(child.getParent());
            condition = (IASTExpression) other;
        }
        if (child == positive) {
            other.setPropertyInParent(child.getPropertyInParent());
            other.setParent(child.getParent());
            positive = (IASTExpression) other;
        }
        if (child == negative) {
            other.setPropertyInParent(child.getPropertyInParent());
            other.setParent(child.getParent());
            negative = (IASTExpression) other;
        }
    }

    public void setLogicalConditionExpression(IASTExpression expression) {
        assertNotFrozen();
        condition = expression;
        if (expression != null) {
            expression.setParent(this);
            expression.setPropertyInParent(LOGICAL_CONDITION);
        }
    }

    public void setNegativeResultExpression(IASTExpression expression) {
        assertNotFrozen();
        negative = expression;
        if (expression != null) {
            expression.setParent(this);
            expression.setPropertyInParent(NEGATIVE_RESULT);
        }
    }

    public void setPositiveResultExpression(IASTExpression expression) {
        assertNotFrozen();
        positive = expression;
        if (expression != null) {
            expression.setParent(this);
            expression.setPropertyInParent(POSITIVE_RESULT);
        }
    }
}
