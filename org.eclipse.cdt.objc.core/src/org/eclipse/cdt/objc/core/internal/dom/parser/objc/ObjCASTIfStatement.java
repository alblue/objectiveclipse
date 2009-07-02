/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Rational Software - Initial API and implementation Yuan
 * Zhang / Beth Tibbitts (IBM Research)
 *******************************************************************************/
package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;
import org.eclipse.cdt.internal.core.dom.parser.IASTAmbiguityParent;

/**
 * @author jcamelon
 */
@SuppressWarnings("restriction")
public class ObjCASTIfStatement extends ASTNode implements IASTIfStatement, IASTAmbiguityParent {

    private IASTExpression condition;
    private IASTStatement elseClause;
    private IASTStatement thenClause;

    public ObjCASTIfStatement() {
    }

    public ObjCASTIfStatement(IASTExpression condition, IASTStatement thenClause) {
        setConditionExpression(condition);
        setThenClause(thenClause);
    }

    public ObjCASTIfStatement(IASTExpression condition, IASTStatement thenClause, IASTStatement elseClause) {
        this(condition, thenClause);
        setElseClause(elseClause);
    }

    @Override
    public boolean accept(ASTVisitor action) {
        if (action.shouldVisitStatements) {
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
        if (thenClause != null) {
            if (!thenClause.accept(action)) {
                return false;
            }
        }
        if (elseClause != null) {
            if (!elseClause.accept(action)) {
                return false;
            }
        }

        if (action.shouldVisitStatements) {
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

    public ObjCASTIfStatement copy() {
        ObjCASTIfStatement copy = new ObjCASTIfStatement();
        copy.setConditionExpression(condition == null ? null : condition.copy());
        copy.setThenClause(thenClause == null ? null : thenClause.copy());
        copy.setElseClause(elseClause == null ? null : elseClause.copy());
        copy.setOffsetAndLength(this);
        return copy;
    }

    public IASTExpression getConditionExpression() {
        return condition;
    }

    public IASTStatement getElseClause() {
        return elseClause;
    }

    public IASTStatement getThenClause() {
        return thenClause;
    }

    public void replace(IASTNode child, IASTNode other) {
        if (thenClause == child) {
            other.setParent(child.getParent());
            other.setPropertyInParent(child.getPropertyInParent());
            thenClause = (IASTStatement) other;
        }
        if (elseClause == child) {
            other.setParent(child.getParent());
            other.setPropertyInParent(child.getPropertyInParent());
            elseClause = (IASTStatement) other;
        }
        if (child == condition) {
            other.setPropertyInParent(child.getPropertyInParent());
            other.setParent(child.getParent());
            condition = (IASTExpression) other;
        }
    }

    public void setConditionExpression(IASTExpression condition) {
        assertNotFrozen();
        this.condition = condition;
        if (condition != null) {
            condition.setParent(this);
            condition.setPropertyInParent(CONDITION);
        }
    }

    public void setElseClause(IASTStatement elseClause) {
        assertNotFrozen();
        this.elseClause = elseClause;
        if (elseClause != null) {
            elseClause.setParent(this);
            elseClause.setPropertyInParent(ELSE);
        }
    }

    public void setThenClause(IASTStatement thenClause) {
        assertNotFrozen();
        this.thenClause = thenClause;
        if (thenClause != null) {
            thenClause.setParent(this);
            thenClause.setPropertyInParent(THEN);
        }
    }
}
