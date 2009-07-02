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
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTExpressionStatement;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.gnu.IGNUASTCompoundStatementExpression;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;

/**
 * Compound statement for c
 */
@SuppressWarnings("restriction")
public class ObjCASTCompoundStatementExpression extends ASTNode implements IGNUASTCompoundStatementExpression {

    private IASTCompoundStatement statement;

    public ObjCASTCompoundStatementExpression() {
    }

    public ObjCASTCompoundStatementExpression(IASTCompoundStatement statement) {
        setCompoundStatement(statement);
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

        if (statement != null) {
            if (!statement.accept(action)) {
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

    public ObjCASTCompoundStatementExpression copy() {
        ObjCASTCompoundStatementExpression copy = new ObjCASTCompoundStatementExpression();
        copy.setCompoundStatement(statement == null ? null : statement.copy());
        copy.setOffsetAndLength(this);
        return copy;
    }

    public IASTCompoundStatement getCompoundStatement() {
        return statement;
    }

    public IType getExpressionType() {
        IASTCompoundStatement compound = getCompoundStatement();
        IASTStatement[] statements = compound.getStatements();
        if (statements.length > 0) {
            IASTStatement st = statements[statements.length - 1];
            if (st instanceof IASTExpressionStatement) {
                return ((IASTExpressionStatement) st).getExpression().getExpressionType();
            }
        }
        return null;
    }

    public void setCompoundStatement(IASTCompoundStatement statement) {
        assertNotFrozen();
        this.statement = statement;
        if (statement != null) {
            statement.setParent(this);
            statement.setPropertyInParent(STATEMENT);
        }
    }

}
