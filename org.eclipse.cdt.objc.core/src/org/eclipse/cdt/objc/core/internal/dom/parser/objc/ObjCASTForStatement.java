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
import org.eclipse.cdt.core.dom.ast.EScopeKind;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTForStatement;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IScope;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;
import org.eclipse.cdt.internal.core.dom.parser.IASTAmbiguityParent;

/**
 * @author jcamelon
 */
@SuppressWarnings("restriction")
public class ObjCASTForStatement extends ASTNode implements IASTForStatement, IASTAmbiguityParent {
    private IASTStatement body, init;

    private IASTExpression condition;
    private IASTExpression iterationExpression;
    private IScope scope = null;

    public ObjCASTForStatement() {
    }

    public ObjCASTForStatement(IASTStatement init, IASTExpression condition,
            IASTExpression iterationExpression, IASTStatement body) {
        setInitializerStatement(init);
        setConditionExpression(condition);
        setIterationExpression(iterationExpression);
        setBody(body);
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
        if (init != null) {
            if (!init.accept(action)) {
                return false;
            }
        }
        if (condition != null) {
            if (!condition.accept(action)) {
                return false;
            }
        }
        if (iterationExpression != null) {
            if (!iterationExpression.accept(action)) {
                return false;
            }
        }
        if (body != null) {
            if (!body.accept(action)) {
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

    public ObjCASTForStatement copy() {
        ObjCASTForStatement copy = new ObjCASTForStatement();
        copyForStatement(copy);
        return copy;
    }

    protected void copyForStatement(ObjCASTForStatement copy) {
        copy.setInitializerStatement(init == null ? null : init.copy());
        copy.setConditionExpression(condition == null ? null : condition.copy());
        copy.setIterationExpression(iterationExpression == null ? null : iterationExpression.copy());
        copy.setBody(body == null ? null : body.copy());
        copy.setOffsetAndLength(this);
    }

    public IASTStatement getBody() {
        return body;
    }

    public IASTExpression getConditionExpression() {
        return condition;
    }

    public IASTStatement getInitializerStatement() {
        return init;
    }

    public IASTExpression getIterationExpression() {
        return iterationExpression;
    }

    public IScope getScope() {
        if (scope == null) {
            scope = new ObjCScope(this, EScopeKind.eLocal);
        }
        return scope;
    }

    public void replace(IASTNode child, IASTNode other) {
        if (body == child) {
            other.setPropertyInParent(child.getPropertyInParent());
            other.setParent(child.getParent());
            body = (IASTStatement) other;
        }
        if (child == init) {
            other.setPropertyInParent(child.getPropertyInParent());
            other.setParent(child.getParent());
            init = (IASTStatement) other;
        }
        if (child == iterationExpression) {
            other.setPropertyInParent(child.getPropertyInParent());
            other.setParent(child.getParent());
            iterationExpression = (IASTExpression) other;
        }
        if (child == condition) {
            other.setPropertyInParent(child.getPropertyInParent());
            other.setParent(child.getParent());
            condition = (IASTExpression) other;
        }

    }

    public void setBody(IASTStatement statement) {
        assertNotFrozen();
        body = statement;
        if (statement != null) {
            statement.setParent(this);
            statement.setPropertyInParent(BODY);
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

    public void setInitializerStatement(IASTStatement statement) {
        assertNotFrozen();
        init = statement;
        if (statement != null) {
            statement.setParent(this);
            statement.setPropertyInParent(INITIALIZER);
        }
    }

    public void setIterationExpression(IASTExpression iterator) {
        assertNotFrozen();
        iterationExpression = iterator;
        if (iterator != null) {
            iterator.setParent(this);
            iterator.setPropertyInParent(ITERATION);
        }
    }
}