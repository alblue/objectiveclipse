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
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IFunctionType;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;
import org.eclipse.cdt.internal.core.dom.parser.IASTAmbiguityParent;
import org.eclipse.cdt.internal.core.dom.parser.ITypeContainer;

/**
 * Function call expression in C.
 */
@SuppressWarnings("restriction")
public class ObjCASTFunctionCallExpression extends ASTNode implements IASTFunctionCallExpression,
        IASTAmbiguityParent {

    private IASTExpression functionName;
    private IASTExpression parameter;

    public ObjCASTFunctionCallExpression() {
    }

    public ObjCASTFunctionCallExpression(IASTExpression functionName, IASTExpression parameter) {
        setFunctionNameExpression(functionName);
        setParameterExpression(parameter);
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

        if (functionName != null) {
            if (!functionName.accept(action)) {
                return false;
            }
        }
        if (parameter != null) {
            if (!parameter.accept(action)) {
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

    public ObjCASTFunctionCallExpression copy() {
        ObjCASTFunctionCallExpression copy = new ObjCASTFunctionCallExpression();
        copy.setFunctionNameExpression(functionName == null ? null : functionName.copy());
        copy.setParameterExpression(parameter == null ? null : parameter.copy());
        copy.setOffsetAndLength(this);
        return copy;
    }

    public IType getExpressionType() {
        IType type = getFunctionNameExpression().getExpressionType();
        try {
            while (type instanceof ITypeContainer) {
                type = ((ITypeContainer) type).getType();
            }
            if (type instanceof IFunctionType) {
                return ((IFunctionType) type).getReturnType();
            }
        } catch (DOMException e) {
            return e.getProblem();
        }
        return null;
    }

    public IASTExpression getFunctionNameExpression() {
        return functionName;
    }

    public IASTExpression getParameterExpression() {
        return parameter;
    }

    public void replace(IASTNode child, IASTNode other) {
        if (child == functionName) {
            other.setPropertyInParent(child.getPropertyInParent());
            other.setParent(child.getParent());
            functionName = (IASTExpression) other;
        }
        if (child == parameter) {
            other.setPropertyInParent(child.getPropertyInParent());
            other.setParent(child.getParent());
            parameter = (IASTExpression) other;
        }
    }

    public void setFunctionNameExpression(IASTExpression expression) {
        assertNotFrozen();
        functionName = expression;
        if (expression != null) {
            expression.setParent(this);
            expression.setPropertyInParent(FUNCTION_NAME);
        }
    }

    public void setParameterExpression(IASTExpression expression) {
        assertNotFrozen();
        parameter = expression;
        if (expression != null) {
            expression.setParent(this);
            expression.setPropertyInParent(PARAMETERS);
        }
    }
}
