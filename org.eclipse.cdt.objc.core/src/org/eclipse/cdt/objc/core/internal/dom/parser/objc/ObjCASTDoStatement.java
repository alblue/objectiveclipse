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
import org.eclipse.cdt.core.dom.ast.IASTDoStatement;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;
import org.eclipse.cdt.internal.core.dom.parser.IASTAmbiguityParent;

/**
 * @author jcamelon
 */
@SuppressWarnings("restriction")
public class ObjCASTDoStatement extends ASTNode implements IASTDoStatement, IASTAmbiguityParent {

    private IASTStatement body;
    private IASTExpression condition;

    public ObjCASTDoStatement() {
    }

    public ObjCASTDoStatement(IASTStatement body, IASTExpression condition) {
        setBody(body);
        setCondition(condition);
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
        if (body != null) {
            if (!body.accept(action)) {
                return false;
            }
        }
        if (condition != null) {
            if (!condition.accept(action)) {
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

    public ObjCASTDoStatement copy() {
        ObjCASTDoStatement copy = new ObjCASTDoStatement();
        copy.setBody(body == null ? null : body.copy());
        copy.setCondition(condition == null ? null : condition.copy());
        copy.setOffsetAndLength(this);
        return copy;
    }

    public IASTStatement getBody() {
        return body;
    }

    public IASTExpression getCondition() {
        return condition;
    }

    public void replace(IASTNode child, IASTNode other) {
        if (body == child) {
            other.setPropertyInParent(body.getPropertyInParent());
            other.setParent(body.getParent());
            body = (IASTStatement) other;
        }
        if (child == condition) {
            other.setPropertyInParent(child.getPropertyInParent());
            other.setParent(child.getParent());
            condition = (IASTExpression) other;
        }
    }

    public void setBody(IASTStatement body) {
        assertNotFrozen();
        this.body = body;
        if (body != null) {
            body.setParent(this);
            body.setPropertyInParent(BODY);
        }
    }

    public void setCondition(IASTExpression condition) {
        assertNotFrozen();
        this.condition = condition;
        if (condition != null) {
            condition.setParent(this);
            condition.setPropertyInParent(CONDITION);
        }
    }
}
