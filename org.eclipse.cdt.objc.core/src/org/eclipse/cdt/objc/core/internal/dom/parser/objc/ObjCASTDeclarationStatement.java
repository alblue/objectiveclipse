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
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarationStatement;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;
import org.eclipse.cdt.internal.core.dom.parser.IASTAmbiguityParent;

/**
 * A declaration statement.
 */
@SuppressWarnings("restriction")
public class ObjCASTDeclarationStatement extends ASTNode implements IASTDeclarationStatement,
        IASTAmbiguityParent {

    private IASTDeclaration declaration;

    public ObjCASTDeclarationStatement() {
    }

    public ObjCASTDeclarationStatement(IASTDeclaration declaration) {
        setDeclaration(declaration);
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
        if (declaration != null) {
            if (!declaration.accept(action)) {
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

    public ObjCASTDeclarationStatement copy() {
        ObjCASTDeclarationStatement copy = new ObjCASTDeclarationStatement();
        copy.setDeclaration(declaration == null ? null : declaration.copy());
        copy.setOffsetAndLength(this);
        return copy;
    }

    public IASTDeclaration getDeclaration() {
        return declaration;
    }

    public void replace(IASTNode child, IASTNode other) {
        if (child == declaration) {
            other.setPropertyInParent(child.getPropertyInParent());
            other.setParent(child.getParent());
            declaration = (IASTDeclaration) other;
        }
    }

    public void setDeclaration(IASTDeclaration declaration) {
        assertNotFrozen();
        this.declaration = declaration;
        if (declaration != null) {
            declaration.setParent(this);
            declaration.setPropertyInParent(DECLARATION);
        }
    }
}
