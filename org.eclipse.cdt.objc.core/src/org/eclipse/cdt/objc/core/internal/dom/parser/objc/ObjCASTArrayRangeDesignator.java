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
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTVisitor;
import org.eclipse.cdt.objc.core.dom.parser.gnu.objc.IObjCGCCASTArrayRangeDesignator;

/**
 * @author jcamelon
 */
@SuppressWarnings("restriction")
public class ObjCASTArrayRangeDesignator extends ASTNode implements IObjCGCCASTArrayRangeDesignator,
        IASTAmbiguityParent {

    private IASTExpression floor, ceiling;

    public ObjCASTArrayRangeDesignator() {
    }

    public ObjCASTArrayRangeDesignator(IASTExpression floor, IASTExpression ceiling) {
        setRangeFloor(floor);
        setRangeCeiling(ceiling);
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
        if (floor != null) {
            if (!floor.accept(action)) {
                return false;
            }
        }
        if (ceiling != null) {
            if (!ceiling.accept(action)) {
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

    public ObjCASTArrayRangeDesignator copy() {
        ObjCASTArrayRangeDesignator copy = new ObjCASTArrayRangeDesignator();
        copy.setRangeFloor(floor == null ? null : floor.copy());
        copy.setRangeCeiling(ceiling == null ? null : ceiling.copy());
        copy.setOffsetAndLength(this);
        return copy;
    }

    public IASTExpression getRangeCeiling() {
        return ceiling;
    }

    public IASTExpression getRangeFloor() {
        return floor;
    }

    public void replace(IASTNode child, IASTNode other) {
        if (child == floor) {
            other.setPropertyInParent(child.getPropertyInParent());
            other.setParent(child.getParent());
            floor = (IASTExpression) other;
        }
        if (child == ceiling) {
            other.setPropertyInParent(child.getPropertyInParent());
            other.setParent(child.getParent());
            ceiling = (IASTExpression) other;
        }
    }

    public void setRangeCeiling(IASTExpression expression) {
        assertNotFrozen();
        ceiling = expression;
        if (expression != null) {
            expression.setParent(this);
            expression.setPropertyInParent(SUBSCRIPT_CEILING_EXPRESSION);
        }
    }

    public void setRangeFloor(IASTExpression expression) {
        assertNotFrozen();
        floor = expression;
        if (expression != null) {
            expression.setParent(this);
            expression.setPropertyInParent(SUBSCRIPT_FLOOR_EXPRESSION);
        }
    }

}
