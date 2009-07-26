/*******************************************************************************
 * Copyright (c) 2009, Ryan Rusaw and others. All rights reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 
 * Ryan Rusaw - Initial API and implementation
 *******************************************************************************/

package org.eclipse.cdt.objc.core.internal.dom.parser.objc;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;
import org.eclipse.cdt.objc.core.dom.ast.objc.IObjCASTFastEnumStatement;

@SuppressWarnings("restriction")
public class ObjCASTFastEnumStatement extends ASTNode implements IObjCASTFastEnumStatement {

    private IASTExpression expression;

    private IASTStatement statement;

    public ObjCASTFastEnumStatement() {
    }

    public ObjCASTFastEnumStatement(IASTStatement s, IASTExpression e) {
        setStatement(s);
        setExpression(e);
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

        if (statement != null) {
            if (!statement.accept(action)) {
                return false;
            }
        }

        if (expression != null) {
            if (!expression.accept(action)) {
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

    public IObjCASTFastEnumStatement copy() {
        ObjCASTFastEnumStatement copy = new ObjCASTFastEnumStatement();
        copy.setStatement(statement == null ? null : statement.copy());
        copy.setExpression(expression == null ? null : expression.copy());
        copy.setOffsetAndLength(this);
        return copy;
    }

    public IASTExpression getExpression() {
        return expression;
    }

    public IASTStatement getStatement() {
        return statement;
    }

    public void setExpression(IASTExpression e) {
        assertNotFrozen();
        expression = e;
        if (e != null) {
            e.setParent(this);
            e.setPropertyInParent(EXPRESSION);
        }
    }

    public void setStatement(IASTStatement s) {
        assertNotFrozen();
        statement = s;
        if (s != null) {
            s.setParent(this);
            s.setPropertyInParent(STATEMENT);
        }
    }

}
