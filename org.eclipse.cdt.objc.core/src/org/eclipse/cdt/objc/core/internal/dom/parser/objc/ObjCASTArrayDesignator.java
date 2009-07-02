/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Rational Software - Initial API and implementation Yuan
 * Zhang / Beth Tibbitts (IBM Research) Markus Schorn (Wind River Systems)
 *******************************************************************************/
package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;
import org.eclipse.cdt.internal.core.dom.parser.IASTAmbiguityParent;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTArrayDesignator;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTVisitor;

/**
 * @author jcamelon
 */
@SuppressWarnings("restriction")
public class ObjCASTArrayDesignator extends ASTNode implements IObjCASTArrayDesignator, IASTAmbiguityParent {

    private IASTExpression exp;

    public ObjCASTArrayDesignator() {
    }

    public ObjCASTArrayDesignator(IASTExpression exp) {
        setSubscriptExpression(exp);
    }

    @Override
    public boolean accept(ASTVisitor action) {
        if (action.shouldVisitDesignators && action instanceof IObjCASTVisitor) {
            switch (((IObjCASTVisitor) action).visit(this)) {
                case ASTVisitor.PROCESS_ABORT:
                    return false;
                case ASTVisitor.PROCESS_SKIP:
                    return true;
                default:
                    break;
            }
        }
        if (exp != null) {
            if (!exp.accept(action)) {
                return false;
            }
        }

        if (action.shouldVisitDesignators && action instanceof IObjCASTVisitor) {
            switch (((IObjCASTVisitor) action).leave(this)) {
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

    public ObjCASTArrayDesignator copy() {
        ObjCASTArrayDesignator copy = new ObjCASTArrayDesignator(exp == null ? null : exp.copy());
        copy.setOffsetAndLength(this);
        return copy;
    }

    public IASTExpression getSubscriptExpression() {
        return exp;
    }

    public void replace(IASTNode child, IASTNode other) {
        if (child == exp) {
            other.setPropertyInParent(child.getPropertyInParent());
            other.setParent(child.getParent());
            exp = (IASTExpression) other;
        }
    }

    public void setSubscriptExpression(IASTExpression value) {
        assertNotFrozen();
        exp = value;
        if (value != null) {
            value.setParent(this);
            value.setPropertyInParent(SUBSCRIPT_EXPRESSION);
        }
    }
}
