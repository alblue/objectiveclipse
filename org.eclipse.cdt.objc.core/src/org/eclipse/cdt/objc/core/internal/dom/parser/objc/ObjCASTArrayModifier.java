/*******************************************************************************
 * Copyright (c) 2005, 2009 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: John Camelon (IBM Rational Software) - Initial API and
 * implementation Markus Schorn (Wind River Systems)
 *******************************************************************************/
package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;
import org.eclipse.cdt.internal.core.dom.parser.IASTAmbiguityParent;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTArrayModifier;

/**
 * c-specific modifier for array specifiers.
 */
@SuppressWarnings("restriction")
public class ObjCASTArrayModifier extends ASTNode implements IObjCASTArrayModifier, IASTAmbiguityParent {

    private IASTExpression exp;
    private boolean isConst;
    private boolean isRestrict;
    private boolean isStatic;
    private boolean isVarSized;
    private boolean isVolatile;

    public ObjCASTArrayModifier() {
    }

    public ObjCASTArrayModifier(IASTExpression exp) {
        setConstantExpression(exp);
    }

    @Override
    public boolean accept(ASTVisitor action) {
        if (action.shouldVisitArrayModifiers) {
            switch (action.visit(this)) {
                case ASTVisitor.PROCESS_ABORT:
                    return false;
                case ASTVisitor.PROCESS_SKIP:
                    return true;
                default:
                    break;
            }
        }
        if (exp != null && !exp.accept(action)) {
            return false;
        }

        if (action.shouldVisitArrayModifiers && action.leave(this) == ASTVisitor.PROCESS_ABORT) {
            return false;
        }
        return true;
    }

    public ObjCASTArrayModifier copy() {
        ObjCASTArrayModifier copy = new ObjCASTArrayModifier(exp == null ? null : exp.copy());
        copy.setOffsetAndLength(this);
        copy.isVolatile = isVolatile;
        copy.isRestrict = isRestrict;
        copy.isStatic = isStatic;
        copy.isConst = isConst;
        copy.isVarSized = isVarSized;
        return copy;
    }

    public IASTExpression getConstantExpression() {
        return exp;
    }

    public boolean isConst() {
        return isConst;
    }

    public boolean isRestrict() {
        return isRestrict;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public boolean isVariableSized() {
        return isVarSized;
    }

    public boolean isVolatile() {
        return isVolatile;
    }

    public void replace(IASTNode child, IASTNode other) {
        if (child == exp) {
            other.setPropertyInParent(child.getPropertyInParent());
            other.setParent(child.getParent());
            exp = (IASTExpression) other;
        }
    }

    public void setConst(boolean value) {
        assertNotFrozen();
        isConst = value;
    }

    public void setConstantExpression(IASTExpression expression) {
        assertNotFrozen();
        exp = expression;
        if (expression != null) {
            expression.setParent(this);
            expression.setPropertyInParent(CONSTANT_EXPRESSION);
        }
    }

    public void setRestrict(boolean value) {
        assertNotFrozen();
        isRestrict = value;
    }

    public void setStatic(boolean value) {
        assertNotFrozen();
        isStatic = value;
    }

    public void setVariableSized(boolean value) {
        assertNotFrozen();
        isVarSized = value;
    }

    public void setVolatile(boolean value) {
        assertNotFrozen();
        isVolatile = value;
    }
}
