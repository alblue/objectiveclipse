/*******************************************************************************
 * Copyright (c) 2007 Wind River Systems, Inc. and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Markus Schorn - initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpressionList;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IPointerType;
import org.eclipse.cdt.core.dom.ast.IQualifierType;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.internal.core.dom.parser.VariableReadWriteFlags;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTFieldDesignator;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTTypeIdInitializerExpression;

/**
 * Helper class to determine whether a variable is accessed for reading and/or
 * writing. The algorithm works starting from the variable and looking upwards
 * what's being done with the variable.
 */
@SuppressWarnings("restriction")
public final class ObjCVariableReadWriteFlags extends VariableReadWriteFlags {

    private static ObjCVariableReadWriteFlags INSTANCE = new ObjCVariableReadWriteFlags();

    public static int getReadWriteFlags(IASTName variable) {
        return INSTANCE.rwAnyNode(variable, 0);
    }

    @Override
    protected int rwAnyNode(IASTNode node, int indirection) {
        final IASTNode parent = node.getParent();
        if (parent instanceof IObjCASTFieldDesignator) {
            return WRITE; // node is initialized via designated initializer
        }
        return super.rwAnyNode(node, indirection);
    }

    @Override
    protected int rwArgumentForFunctionCall(IASTFunctionCallExpression func, int parameterIdx, int indirection) {
        if (indirection == 0) {
            return READ;
        }
        return super.rwArgumentForFunctionCall(func, parameterIdx, indirection);
    }

    @Override
    protected int rwArgumentForFunctionCall(IASTNode node, IASTExpressionList exprList,
            IASTFunctionCallExpression funcCall, int indirection) {
        if (indirection == 0) {
            return READ;
        }
        return super.rwArgumentForFunctionCall(node, exprList, funcCall, indirection);
    }

    @Override
    protected int rwAssignmentToType(IType type, int indirection) {
        if (indirection == 0) {
            return READ;
        }
        try {
            while (indirection > 0 && (type instanceof IPointerType)) {
                type = ((IPointerType) type).getType();
                indirection--;
            }
            if (indirection == 0) {
                if (type instanceof IQualifierType) {
                    return ((IQualifierType) type).isConst() ? READ : READ | WRITE;
                } else if (type instanceof IPointerType) {
                    return ((IPointerType) type).isConst() ? READ : READ | WRITE;
                }
            }
        } catch (DOMException e) {
        }
        return READ | WRITE; // fallback
    }

    @Override
    protected int rwInExpression(IASTNode node, IASTExpression expr, int indirection) {
        if (expr instanceof IObjCASTTypeIdInitializerExpression) {
            return 0;
        }
        return super.rwInExpression(node, expr, indirection);
    }

    @Override
    protected int rwInInitializerExpression(int indirection, IASTNode parent) {
        if (indirection == 0) {
            return READ;
        }
        return super.rwInInitializerExpression(indirection, parent);
    }
}
