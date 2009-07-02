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
import org.eclipse.cdt.core.dom.ast.IASTTypeId;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTTypeIdExpression;

/**
 * Type-id or unary operation on a type-id.
 */
@SuppressWarnings("restriction")
public class ObjCASTTypeIdExpression extends ASTNode implements IObjCASTTypeIdExpression {

    private int op;
    private IASTTypeId typeId;

    public ObjCASTTypeIdExpression() {
    }

    public ObjCASTTypeIdExpression(int op, IASTTypeId typeId) {
        this.op = op;
        setTypeId(typeId);
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

        if (typeId != null) {
            if (!typeId.accept(action)) {
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

    public ObjCASTTypeIdExpression copy() {
        ObjCASTTypeIdExpression copy = new ObjCASTTypeIdExpression(op, typeId == null ? null : typeId.copy());
        copy.setOffsetAndLength(this);
        return copy;
    }

    public IType getExpressionType() {
        if (getOperator() == op_sizeof) {
            return ObjCVisitor.getSize_T(this);
        }
        return ObjCVisitor.createType(typeId.getAbstractDeclarator());
    }

    public int getOperator() {
        return op;
    }

    public IASTTypeId getTypeId() {
        return typeId;
    }

    public void setOperator(int value) {
        assertNotFrozen();
        op = value;
    }

    public void setTypeId(IASTTypeId typeId) {
        assertNotFrozen();
        this.typeId = typeId;
        if (typeId != null) {
            typeId.setParent(this);
            typeId.setPropertyInParent(TYPE_ID);
        }
    }
}
